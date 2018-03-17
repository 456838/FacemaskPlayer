package com.salton123.facemaskplayer;

import android.support.annotation.IntDef;

/**
 * User: newSalton@outlook.com
 * Date: 2018/3/8 18:28
 * ModifyTime: 18:28
 * Description:
 */
@IntDef({Type.TYPE_IJK, Type.TYPE_SYSTEM})
public @interface Type {
    /**
     * IjkPlayer
     **/
    public static final int TYPE_IJK = 0x1;
    /**
     * MediaPlayer
     **/
    public static final int TYPE_SYSTEM = 0x0;
}
