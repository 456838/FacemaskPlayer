package com.salton123.facemaskplayer;

/**
 * User: newSalton@outlook.com
 * Date: 2018/3/8 20:37
 * ModifyTime: 20:37
 * Description:
 */
public enum FaceMaskPlayerManager {
    INSTANCE;

    private IFaceMaskPlayer mVideoPlayer;

    public IFaceMaskPlayer currentPlayer() {
        return mVideoPlayer;
    }

    public void currentPlayer(IFaceMaskPlayer videoPlayer) {
        if (mVideoPlayer != videoPlayer) {
            releasePlayer();
            mVideoPlayer = videoPlayer;
        }
    }

    public void suspend() {
        if (mVideoPlayer != null && (mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying())) {
            mVideoPlayer.pause();
        }
    }

    public void resume() {
        if (mVideoPlayer != null && (mVideoPlayer.isPaused() || mVideoPlayer.isBufferingPaused())) {
            mVideoPlayer.restart();
        }
    }

    public void releasePlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    public boolean backPressd() {
        if (mVideoPlayer != null) {
            if (mVideoPlayer.isFullScreen()) {
                mVideoPlayer.fullMode(false);
                return true;
            } else if (mVideoPlayer.isTinyWindow()) {
                mVideoPlayer.tinyMode(false);
                return true;
            }
        }
        return false;
    }
}
