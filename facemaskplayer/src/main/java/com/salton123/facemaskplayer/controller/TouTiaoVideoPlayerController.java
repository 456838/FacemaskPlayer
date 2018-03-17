package com.salton123.facemaskplayer.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * User: newSalton@outlook.com
 * Date: 2018/3/9 18:00
 * ModifyTime: 18:00
 * Description:
 */
public class TouTiaoVideoPlayerController extends AbsFaceMaskController {
    public TouTiaoVideoPlayerController(@NonNull Context context) {
        super(context);
    }

    public TouTiaoVideoPlayerController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouTiaoVideoPlayerController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setImage(int resId) {

    }

    @Override
    public ImageView imageView() {
        return null;
    }

    @Override
    public void setLenght(long length) {

    }

    @Override
    public void onPlayStateChanged(int playState) {

    }

    @Override
    public void onPlayModeChanged(int playMode) {

    }

    @Override
    public void reset() {

    }

    @Override
    public void updateProgress() {

    }

    @Override
    public void showChangePosition(long duration, int newPositionProgress) {

    }

    @Override
    public void hideChangePosition() {

    }

    @Override
    public void showChangeVolume(int newVolumeProgress) {

    }

    @Override
    public void hideChangeVolume() {

    }

    @Override
    public void showChangeBrightness(int newBrightnessProgress) {

    }

    @Override
    public void hideChangeBrightness() {

    }
}
