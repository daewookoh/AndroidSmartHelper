package com.bestmafen.smablelib.entity;

/**
 * 不带gps的设备，需要手机返回gps信息，b3 v0.1.8及以上，b3 tft v0.6.1及以上适用。其他用{@link SmaCyclingExtra}
 */
public class SmaCyclingExtra2 implements ISmaCmd {
    public float speed;//km/h
    public int   altitude;//m
    public float distance;//km

    @Override
    public byte[] toByteArray() {
        if (speed < 0) {
            speed = 0;
        }
        if (distance < 0) {
            distance = 0;
        }

        byte[] extra = new byte[10];
        int s = Float.floatToIntBits(speed);
        extra[0] = (byte) ((s >> 24) & 0xff);
        extra[1] = (byte) ((s >> 16) & 0xff);
        extra[2] = (byte) ((s >> 8) & 0xff);
        extra[3] = (byte) (s & 0xff);
        extra[4] = (byte) ((altitude >> 8) & 0xff);
        extra[5] = (byte) (altitude & 0xff);
        int d = Float.floatToIntBits(distance);
        extra[6] = (byte) ((d >> 24) & 0xff);
        extra[7] = (byte) ((d >> 16) & 0xff);
        extra[8] = (byte) ((d >> 8) & 0xff);
        extra[9] = (byte) (d & 0xff);
        return extra;
    }

    @Override
    public String toString() {
        return "SmaCyclingExtra2{" +
                "speed=" + speed +
                ", altitude=" + altitude +
                ", distance=" + distance +
                '}';
    }
}
