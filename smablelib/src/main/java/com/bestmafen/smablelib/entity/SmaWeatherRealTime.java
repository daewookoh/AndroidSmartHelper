package com.bestmafen.smablelib.entity;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Real-time weather
 */
public class SmaWeatherRealTime implements ISmaCmd {
    public static final int WEATHER_UNIT_METRIC   = 0;
    public static final int WEATHER_UNIT_IMPERIAL = 1;

    public static final int SUNNY         = 1;
    public static final int CLOUDY        = 2;
    public static final int OVERCAST      = 3;
    public static final int RAINY         = 4;
    public static final int THUNDER       = 5;
    public static final int THUNDERSHOWER = 6;
    public static final int HIGH_WINDY    = 7;
    public static final int SNOWY         = 8;
    public static final int FOGGY         = 9;
    public static final int SAND_STORM    = 10;
    public static final int OTHER         = -1;

    @IntDef({SUNNY, CLOUDY, OVERCAST, RAINY, THUNDER, THUNDERSHOWER, HIGH_WINDY, SNOWY, FOGGY, SAND_STORM, OTHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {

    }

    public SmaTime time;
    public int     temperature;//℃
    @Type
    public int     weatherCode;
    public int     precipitation;//mm
    public int     visibility;//km
    public int     windSpeed;//m/s
    public int     humidity;//%

    @Override
    public byte[] toByteArray() {
        byte[] extra = new byte[11];
        extra[4] = (byte) temperature;
        extra[5] = (byte) weatherCode;
        extra[6] = (byte) (precipitation >>> 8);
        extra[7] = (byte) (precipitation & 0xff);
        extra[8] = (byte) visibility;
        extra[9] = (byte) windSpeed;
        extra[10] = (byte) humidity;
        if (time != null) {
            System.arraycopy(time.toByteArray(), 0, extra, 0, 4);
        }
        return extra;
    }
}
