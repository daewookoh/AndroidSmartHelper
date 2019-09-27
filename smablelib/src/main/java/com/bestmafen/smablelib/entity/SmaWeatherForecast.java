package com.bestmafen.smablelib.entity;

/**
 * 天气预报
 */

public class SmaWeatherForecast implements ISmaCmd {
    public int temH;//℃
    public int temL;//℃
    @SmaWeatherRealTime.Type
    public int weatherCode;
    /**
     * 1,2    -> low
     * 3,4,5  -> moderate
     * 6,7    -> high
     * 8,9,10 -> very high
     * >10    -> extreme
     */
    public int ultraviolet;

    @Override
    public byte[] toByteArray() {
        byte[] extra = new byte[4];
        extra[0] = (byte) temH;
        extra[1] = (byte) temL;
        extra[2] = (byte) weatherCode;
        extra[3] = (byte) ultraviolet;
        return extra;
    }
}
