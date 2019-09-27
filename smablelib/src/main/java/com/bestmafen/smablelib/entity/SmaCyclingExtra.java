package com.bestmafen.smablelib.entity;

/**
 * 不带gps的设备，需要手机返回gps信息，b3 v0.1.8以下，b3 tft v0.6.1以下适用。其他用{@link SmaCyclingExtra2}
 */
public class SmaCyclingExtra implements ISmaCmd {
    public int speed;//km/h
    public int altitude;//m
    public int distance;//0.1 km

    @Override
    public byte[] toByteArray() {
        if (speed < 0) {
            speed = 0;
        }
        if (distance < 0) {
            distance = 0;
        }

        byte[] extra = new byte[5];
        extra[0] = (byte) speed;
        extra[1] = (byte) ((altitude >> 8) & 0xff);
        extra[2] = (byte) (altitude & 0xff);
        extra[3] = (byte) ((distance >> 8) & 0xff);
        extra[4] = (byte) (distance & 0xff);
        return extra;
    }

    @Override
    public String toString() {
        return "SmaCyclingExtra{" +
                "speed=" + speed +
                ", altitude=" + altitude +
                ", distance=" + distance +
                '}';
    }
}
