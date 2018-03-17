package com.salton123.facemaskplayerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.salton123.facemaskplayer.FaceMaskPlayer;
import com.salton123.facemaskplayer.controller.TxVideoPlayerController;
import com.salton123.facemaskplayer.Type;

public class MainActivity extends AppCompatActivity {

    private FaceMaskPlayer mFaceMaskPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFaceMaskPlayer = findViewById(R.id.facemaskPlayer);
        mFaceMaskPlayer.playerType(Type.TYPE_SYSTEM);
        TxVideoPlayerController controller = new TxVideoPlayerController(this);
        controller.setTitle("Beautiful China...");
        controller.setLenght(117000);
        mFaceMaskPlayer.url("http://play.g3proxy.lecloud.com/vod/v2/MjUxLzE2LzgvbGV0di11dHMvMTQvdmVyXzAwXzIyLTExMDc2NDEzODctYXZjLTE5OTgxOS1hYWMtNDgwMDAtNTI2MTEwLTE3MDg3NjEzLWY1OGY2YzM1NjkwZTA2ZGFmYjg2MTVlYzc5MjEyZjU4LTE0OTg1NTc2ODY4MjMubXA0?b=259&mmsid=65565355&tm=1499247143&key=f0eadb4f30c404d49ff8ebad673d3742&platid=3&splatid=345&playid=0&tss=no&vtype=21&cvid=2026135183914&payff=0&pip=08cc52f8b09acd3eff8bf31688ddeced&format=0&sign=mb&dname=mobile&expect=1&tag=mobile&xformat=super", null);
        mFaceMaskPlayer.setController(controller);
        mFaceMaskPlayer.start();
    }
}
