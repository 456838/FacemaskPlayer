package com.salton123.facemaskplayer;

import android.support.annotation.IntDef;

/**
 * User: newSalton@outlook.com
 * Date: 2018/3/8 18:26
 * ModifyTime: 18:26
 * Description:
 */

@IntDef({Mode.MODE_NORMAL, Mode.MODE_FULL_SCREEN, Mode.MODE_TINY_WINDOW})
public @interface Mode {

    /**
     * 普通模式
     **/
    public static final int MODE_NORMAL = 10;
    /**
     * 全屏模式
     **/
    public static final int MODE_FULL_SCREEN = 11;
    /**
     * 小窗口模式
     **/
    public static final int MODE_TINY_WINDOW = 12;
}
