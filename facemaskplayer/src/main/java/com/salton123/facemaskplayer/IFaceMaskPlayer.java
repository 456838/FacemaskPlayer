package com.salton123.facemaskplayer;

import java.util.Map;

/**
 * User: newSalton@outlook.com
 * Date: 2018/3/8 17:59
 * ModifyTime: 17:59
 * Description:
 */
public interface IFaceMaskPlayer {
    /**
     * 设置播放的视频地址以及map参数
     *
     * @param url
     * @param headers
     * @return
     */
    IFaceMaskPlayer url(String url, Map<String, String> headers);

    /**
     * @return 当前播放地址
     */
    String url();

    /**
     * 开始播放，必须在IDLE状态下
     *
     * @return
     */
    IFaceMaskPlayer start();

    /**
     * 从指定位置开始播放
     *
     * @param position 指定位置
     * @return
     */
    IFaceMaskPlayer start(long position);

    /**
     * 重新播放，播放器被暂停、播放错误、播放完成后，需要调用此方法重新播放
     *
     * @return
     */
    IFaceMaskPlayer restart();

    /**
     * 暂停播放
     *
     * @return
     */
    IFaceMaskPlayer pause();

    /**
     * 跳到指定的位置继续播放
     *
     * @param position
     * @return
     */
    IFaceMaskPlayer seekTo(long position);

    /**
     * 设置音量
     *
     * @param volume
     * @return
     */
    IFaceMaskPlayer volume(int volume);

    /**
     * 获取音量
     *
     * @return
     */
    int volume();

    /**
     * 当前最大音量
     *
     * @return
     */
    int maxVloume();

    /**
     * 设置播放速度
     *
     * @param speed
     * @return
     */
    IFaceMaskPlayer speed(float speed);

    /**
     * 获取当前播放速度
     *
     * @return
     */
    float speed();

    IFaceMaskPlayer fullMode(boolean isFull);

    IFaceMaskPlayer tinyMode(boolean isTiny);

    long duration();

    long current();

    /**
     * 释放后，内部的播放器被释放掉，同时如果在全屏、小窗口模式下都会退出
     * 并且控制器的UI也应该恢复到最初始的状态.
     */
    void release();

    /**
     * 此处只释放播放器（如果要释放播放器并恢复控制器状态需要调用{@link #release()}方法）
     * 不管是全屏、小窗口还是Normal状态下控制器的UI都不恢复初始状态
     * 这样以便在当前播放器状态下可以方便的切换不同的清晰度的视频地址
     */
    void releasePlayer();


    /*********************************
     * 以下3个方法是播放器的模式
     **********************************/
    boolean isFullScreen();

    boolean isTinyWindow();

    boolean isNormal();

    /*********************************
     * 以下9个方法是播放器在当前的播放状态
     **********************************/
    boolean isIdle();

    boolean isPreparing();

    boolean isPrepared();

    boolean isBufferingPlaying();

    boolean isBufferingPaused();

    boolean isPlaying();

    boolean isPaused();

    boolean isError();

    boolean isCompleted();

    /**
     * 获取视频缓冲百分比
     *
     * @return 缓冲白百分比
     */
    int bufferPercentage();
}
