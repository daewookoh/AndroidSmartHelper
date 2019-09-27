package com.bestmafen.smablelib.util;

import java.util.UUID;

public final class SmaConsts {
    public static final String DATE_FORMAT_yyyy_MM_dd = "yyyy-MM-dd";
    public static final String DATE_FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_HH_mm = "HH:mm";

    /**
     * 固件版服务地址
     */
    public final static UUID DEVICE_SERVICE = UUID
            .fromString("0000180a-0000-1000-8000-00805f9b34fb");
    /**
     * 固件版本信息地址
     */
    public final static UUID DEVICE_VERSION = UUID
            .fromString("00002a26-0000-1000-8000-00805f9b34fb");
    /**
     * 硬件版本信息地址
     */
    public final static UUID HARDWARE_VERSION = UUID
            .fromString("00002a27-0000-1000-8000-00805f9b34fb");
    /**
     * 电量服务地址
     */
    public final static UUID BATTERY_SERVICE = UUID
            .fromString("0000180F-0000-1000-8000-00805f9b34fb");
    /**
     * 电量读取地址
     */
    public final static UUID BATTERY_LEVEL_CHARACTERISTIC = UUID
            .fromString("00002A19-0000-1000-8000-00805f9b34fb");

    public static final UUID CCCD = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final String SP_ENABLE_ANTI_LOST = "sp_enable_anti_lost";
    public static final String SP_ENABLE_NODISTURB = "sp_enable_nodisturb";
    public static final String SP_ENABLE_CALL = "sp_enable_call";
    public static final String SP_ENABLE_NOTIFICATION = "sp_enable_notification";
    public static final String SP_ENABLE_DISPLAY_VERTICAL = "sp_enable_display_vertical";
    public static final String SP_ENABLE_DETECT_SLEEP = "sp_enable_detect_sleep";
    public static final String SP_VIBRATION = "sp_vibration";
    public static final String SP_BACK_LIGHT = "sp_back_light";

    public static final int VIBRATION_ALARM = 0x5;
}
