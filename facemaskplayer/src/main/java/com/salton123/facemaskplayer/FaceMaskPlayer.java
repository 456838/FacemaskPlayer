package com.salton123.facemaskplayer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.salton123.facemaskplayer.controller.AbsFaceMaskController;
import com.salton123.facemaskplayer.controller.TxVideoPlayerController;
import com.salton123.facemaskplayer.util.LogUtil;
import com.salton123.facemaskplayer.util.NiceUtil;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * User: newSalton@outlook.com
 * Date: 2018/3/8 17:58
 * ModifyTime: 17:58
 * Description:
 */
public class FaceMaskPlayer extends FrameLayout implements IFaceMaskPlayer, TextureView.SurfaceTextureListener {

    private @Type
    int mType = Type.TYPE_IJK;
    private @Mode
    int mMode = Mode.MODE_NORMAL;
    private @State
    int mState = State.STATE_IDLE;

    private AudioManager mAudioManager;
    private IMediaPlayer mMediaPlayer;
    private FrameLayout mContainer;
    private AbsFaceMaskController mController;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private FaceMaskTextureView mTextureView;
    private String mUrl;
    private Map<String, String> mHeaders;
    private long position;
    private boolean continueFromLastPosition = true;
    private int mBufferPercentage;

    public FaceMaskPlayer(@NonNull Context context) {
        super(context);
        init();
    }

    public FaceMaskPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceMaskPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mContainer = new FrameLayout(getContext());
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
    }

    @Override
    public IFaceMaskPlayer url(String url, Map<String, String> headers) {
        this.mUrl = url;
        this.mHeaders = headers;
        return this;
    }

    public void setController(AbsFaceMaskController controller) {
        mContainer.removeView(mController);
        mController = controller;
        mController.reset();
        mController.setPlayer(this);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mController, params);
    }

    public void defaultController(String title, String url) {
        mContainer.removeView(mController);
        TxVideoPlayerController controller = new TxVideoPlayerController(getContext());
        controller.setTitle(title);
        url(url, null);
        mController = controller;
        mController.reset();
        mController.setPlayer(this);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mController, params);
    }

    public void playerType(int playerType) {
        mType = playerType;
    }

    @Override
    public String url() {
        return this.mUrl;
    }

    @Override
    public IFaceMaskPlayer start() {
        if (mState == State.STATE_IDLE) {
            FaceMaskPlayerManager.INSTANCE.currentPlayer(this);
            initAudioManager();
            initMediaPlayer();
            initTextureView();
            addTextureView();
        } else {
            LogUtil.d("NiceVideoPlayer只有在mState == STATE_IDLE时才能调用start方法.");
        }
        return this;
    }

    private void initAudioManager() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            switch (mType) {
                case Type.TYPE_SYSTEM:
                    mMediaPlayer = new AndroidMediaPlayer();
                    break;
                case Type.TYPE_IJK:
                default:
                    mMediaPlayer = new IjkMediaPlayer();
                    ((IjkMediaPlayer) mMediaPlayer).setOption(1, "analyzemaxduration", 100L);
                    ((IjkMediaPlayer) mMediaPlayer).setOption(1, "probesize", 10240L);
                    ((IjkMediaPlayer) mMediaPlayer).setOption(1, "flush_packets", 1L);
                    ((IjkMediaPlayer) mMediaPlayer).setOption(4, "packet-buffering", 0L);
                    ((IjkMediaPlayer) mMediaPlayer).setOption(4, "framedrop", 1L);
                    break;
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new FaceMaskTextureView(getContext());
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    private void addTextureView() {
        mContainer.removeView(mTextureView);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        mContainer.addView(mTextureView, 0, params);
    }

    @Override
    public IFaceMaskPlayer start(long position) {
        this.position = position;
        start();
        return this;
    }

    @Override
    public IFaceMaskPlayer restart() {
        if (mState == State.STATE_PAUSED) {
            mMediaPlayer.start();
            mState = State.STATE_PLAYING;
            mController.onPlayStateChanged(mState);
            LogUtil.d("STATE_PLAYING");
        } else if (mState == State.STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start();
            mState = State.STATE_BUFFERING_PLAYING;
            mController.onPlayStateChanged(mState);
            LogUtil.d("STATE_BUFFERING_PLAYING");
        } else if (mState == State.STATE_COMPLETED || mState == State.STATE_ERROR) {
            mMediaPlayer.reset();
            openMediaPlayer();
        } else {
            LogUtil.d("NiceVideoPlayer在mState == " + mState + "时不能调用restart()方法.");
        }
        return this;
    }

    private void openMediaPlayer() {
        // 屏幕常亮
        mContainer.setKeepScreenOn(true);
        // 设置监听
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnInfoListener(mOnInfoListener);
        mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        // 设置dataSource
        try {
            mMediaPlayer.setDataSource(getContext().getApplicationContext(), Uri.parse(mUrl), mHeaders);
            if (mSurface == null) {
                mSurface = new Surface(mSurfaceTexture);
            }
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.prepareAsync();
            mState = State.STATE_PREPARING;
            mController.onPlayStateChanged(mState);
            LogUtil.d("STATE_PREPARING");
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("打开播放器发生错误", e.getMessage());
        }
    }

    private IMediaPlayer.OnPreparedListener mOnPreparedListener
            = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            mState = State.STATE_PREPARED;
            mController.onPlayStateChanged(mState);
            LogUtil.d("onPrepared ——> STATE_PREPARED");
            mp.start();
            // 从上次的保存位置播放
            if (continueFromLastPosition) {
                long savedPlayPosition = NiceUtil.getSavedPlayPosition(getContext(), mUrl);
                mp.seekTo(savedPlayPosition);
            }
            // 跳到指定位置播放
            if (position != 0) {
                mp.seekTo(position);
            }
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener
            = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            mTextureView.adaptVideoSize(width, height);
            LogUtil.d("onVideoSizeChanged ——> width：" + width + "， height：" + height);
        }
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener
            = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            mState = State.STATE_COMPLETED;
            mController.onPlayStateChanged(mState);
            LogUtil.d("onCompletion ——> STATE_COMPLETED");
            // 清除屏幕常亮
            mContainer.setKeepScreenOn(false);
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener
            = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            // 直播流播放时去调用mediaPlayer.getDuration会导致-38和-2147483648错误，忽略该错误
            if (what != -38 && what != -2147483648 && extra != -38 && extra != -2147483648) {
                mState = State.STATE_ERROR;
                mController.onPlayStateChanged(mState);
                LogUtil.d("onError ——> STATE_ERROR ———— what：" + what + ", extra: " + extra);
            }
            return true;
        }
    };

    private IMediaPlayer.OnInfoListener mOnInfoListener
            = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                // 播放器开始渲染
                mState = State.STATE_PLAYING;
                mController.onPlayStateChanged(mState);
                LogUtil.d("onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING");
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // MediaPlayer暂时不播放，以缓冲更多的数据
                if (mState == State.STATE_PAUSED || mState == State.STATE_BUFFERING_PAUSED) {
                    mState = State.STATE_BUFFERING_PAUSED;
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED");
                } else {
                    mState = State.STATE_BUFFERING_PLAYING;
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING");
                }
                mController.onPlayStateChanged(mState);
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (mState == State.STATE_BUFFERING_PLAYING) {
                    mState = State.STATE_PLAYING;
                    mController.onPlayStateChanged(mState);
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING");
                }
                if (mState == State.STATE_BUFFERING_PAUSED) {
                    mState = State.STATE_PAUSED;
                    mController.onPlayStateChanged(mState);
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED");
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
                // 视频旋转了extra度，需要恢复
                if (mTextureView != null) {
                    mTextureView.setRotation(extra);
                    LogUtil.d("视频旋转角度：" + extra);
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                LogUtil.d("视频不能seekTo，为直播视频");
            } else {
                LogUtil.d("onInfo ——> what：" + what);
            }
            return true;
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener
            = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            mBufferPercentage = percent;
        }
    };

    @Override
    public IFaceMaskPlayer pause() {
        if (mState == State.STATE_PLAYING) {
            mMediaPlayer.pause();
            mState = State.STATE_PAUSED;
            mController.onPlayStateChanged(mState);
            LogUtil.d("STATE_PAUSED");
        }
        if (mState == State.STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause();
            mState = State.STATE_BUFFERING_PAUSED;
            mController.onPlayStateChanged(mState);
            LogUtil.d("STATE_BUFFERING_PAUSED");
        }
        return this;
    }

    @Override
    public IFaceMaskPlayer seekTo(long position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
        return this;
    }


    @Override
    public IFaceMaskPlayer volume(int volume) {
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        }
        return this;
    }

    @Override
    public int volume() {
        if (mAudioManager != null) {
            return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public int maxVloume() {
        if (mAudioManager != null) {
            return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public IFaceMaskPlayer speed(float speed) {
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            ((IjkMediaPlayer) mMediaPlayer).setSpeed(speed);
        } else {
            LogUtil.d("只有IjkPlayer才能设置播放速度");
        }
        return this;
    }

    @Override
    public float speed() {
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) mMediaPlayer).getSpeed(0);
        } else {
            LogUtil.d("只有IjkPlayer才能设置获取速度");
            return 0;
        }
    }

    @Override
    public IFaceMaskPlayer fullMode(boolean isFull) {
        if (isFull) {
            enterFullScreen();
        } else {
            exitFullScreen();
        }
        return this;
    }

    private void enterFullScreen() {
        if (mMode == Mode.MODE_FULL_SCREEN) {
            return;
        }

        // 隐藏ActionBar、状态栏，并横屏
        NiceUtil.hideActionBar(getContext());
        NiceUtil.scanForActivity(getContext())
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ViewGroup contentView = NiceUtil.scanForActivity(getContext())
                .findViewById(android.R.id.content);
        if (mMode == Mode.MODE_TINY_WINDOW) {
            contentView.removeView(mContainer);
        } else {
            this.removeView(mContainer);
        }
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mContainer, params);

        mMode = Mode.MODE_FULL_SCREEN;
        mController.onPlayModeChanged(mMode);
        LogUtil.d("MODE_FULL_SCREEN");
    }

    /**
     * 退出全屏，移除mTextureView和mController，并添加到非全屏的容器中。
     * 切换竖屏时需要在manifest的activity标签下添加android:configChanges="orientation|keyboardHidden|screenSize"配置，
     * 以避免Activity重新走生命周期.
     *
     * @return true退出全屏.
     */
    private boolean exitFullScreen() {
        if (mMode == Mode.MODE_FULL_SCREEN) {
            NiceUtil.showActionBar(getContext());
            NiceUtil.scanForActivity(getContext())
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            ViewGroup contentView = (ViewGroup) NiceUtil.scanForActivity(getContext())
                    .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);

            mMode = Mode.MODE_NORMAL;
            mController.onPlayModeChanged(mMode);
            LogUtil.d("MODE_NORMAL");
            return true;
        }
        return false;
    }

    /**
     * 进入小窗口播放，小窗口播放的实现原理与全屏播放类似。
     */
    private void enterTinyWindow() {
        if (mMode == Mode.MODE_TINY_WINDOW) {
            return;
        }
        this.removeView(mContainer);

        ViewGroup contentView = (ViewGroup) NiceUtil.scanForActivity(getContext())
                .findViewById(android.R.id.content);
        // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp。
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                (int) (NiceUtil.getScreenWidth(getContext()) * 0.6f),
                (int) (NiceUtil.getScreenWidth(getContext()) * 0.6f * 9f / 16f));
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.rightMargin = NiceUtil.dp2px(getContext(), 8f);
        params.bottomMargin = NiceUtil.dp2px(getContext(), 8f);

        contentView.addView(mContainer, params);

        mMode = Mode.MODE_TINY_WINDOW;
        mController.onPlayModeChanged(mMode);
        LogUtil.d("MODE_TINY_WINDOW");
    }

    /**
     * 退出小窗口播放
     */

    private boolean exitTinyWindow() {
        if (mMode == Mode.MODE_TINY_WINDOW) {
            ViewGroup contentView = (ViewGroup) NiceUtil.scanForActivity(getContext())
                    .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);

            mMode = Mode.MODE_NORMAL;
            mController.onPlayModeChanged(mMode);
            LogUtil.d("MODE_NORMAL");
            return true;
        }
        return false;
    }

    @Override
    public IFaceMaskPlayer tinyMode(boolean isTiny) {
        if (isTiny) {
            enterTinyWindow();
        } else {
            exitTinyWindow();
        }
        return this;
    }

    @Override
    public long duration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    @Override
    public long current() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void release() {
        // 保存播放位置
        if (isPlaying() || isBufferingPlaying() || isBufferingPaused() || isPaused()) {
            NiceUtil.savePlayPosition(getContext(), mUrl, current());
        } else if (isCompleted()) {
            NiceUtil.savePlayPosition(getContext(), mUrl, 0);
        }
        // 退出全屏或小窗口
        if (isFullScreen()) {
            exitFullScreen();
        }
        if (isTinyWindow()) {
            exitTinyWindow();
        }
        mMode = Mode.MODE_NORMAL;
        // 释放播放器
        releasePlayer();

        // 恢复控制器
        if (mController != null) {
            mController.reset();
        }
        Runtime.getRuntime().gc();
    }

    @Override
    public void releasePlayer() {
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(null);
            mAudioManager = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mContainer.removeView(mTextureView);
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mState = State.STATE_IDLE;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
            openMediaPlayer();
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    @Override
    public boolean isIdle() {
        return mState == State.STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mState == State.STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mState == State.STATE_PREPARED;
    }

    @Override
    public boolean isBufferingPlaying() {
        return mState == State.STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return mState == State.STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mState == State.STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mState == State.STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return mState == State.STATE_ERROR;
    }

    @Override
    public boolean isCompleted() {
        return mState == State.STATE_COMPLETED;
    }

    @Override
    public int bufferPercentage() {
        return mBufferPercentage;
    }

    @Override
    public boolean isFullScreen() {
        return mMode == Mode.MODE_FULL_SCREEN;
    }

    @Override
    public boolean isTinyWindow() {
        return mMode == Mode.MODE_TINY_WINDOW;
    }

    @Override
    public boolean isNormal() {
        return mMode == Mode.MODE_NORMAL;
    }

}
