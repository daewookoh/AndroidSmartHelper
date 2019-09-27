package com.bestmafen.smablelib.entity;

import android.support.annotation.IntDef;

import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by xiaokai on 2018/8/2.
 */
public class SmaStream {
    public static final int FLAG_LOCATION_ASSISTED = 0x0001;//M1 agps辅助文件
    public static final int FLAG_EPHEMERIS         = 0x0002;//b3g agps辅助文件，暂时没用，b3g用XMode传输
    public static final int FLAG_M1_WATCHFACE      = 0x0003;//m1表盘文件

    @IntDef({FLAG_LOCATION_ASSISTED, FLAG_EPHEMERIS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FileFlag {

    }

    public InputStream inputStream;
    @FileFlag
    public int         flag;
    public String[]    extras;
}
