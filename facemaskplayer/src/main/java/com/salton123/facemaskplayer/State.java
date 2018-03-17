package com.salton123.facemaskplayer;

import android.support.annotation.IntDef;


/**
 * User: newSalton@outlook.com
 * Date: 2018/3/8 18:23
 * ModifyTime: 18:23
 * Description:
 */
@IntDef({State.STATE_ERROR, State.STATE_IDLE
        , State.STATE_PREPARING, State.STATE_PREPARED
        , State.STATE_PLAYING, State.STATE_PAUSED
        , State.STATE_BUFFERING_PLAYING, State.STATE_BUFFERING_PAUSED
        , State.STATE_COMPLETED
})
public @interface State {
    /**
     * 播放错误
     **/
    public static final int STATE_ERROR = -1;
    /**
     * 播放未开始
     **/
    public static final int STATE_IDLE = 0;
    /**
     * 播放准备中
     **/
    public static final int STATE_PREPARING = 1;
    /**
     * 播放准备就绪
     **/
    public static final int STATE_PREPARED = 2;
    /**
     * 正在播放
     **/
    public static final int STATE_PLAYING = 3;
    /**
     * 暂停播放
     **/
    public static final int STATE_PAUSED = 4;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     **/
    public static final int STATE_BUFFERING_PLAYING = 5;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
     **/
    public static final int STATE_BUFFERING_PAUSED = 6;
    /**
     * 播放完成
     **/
    public static final int STATE_COMPLETED = 7;
}
