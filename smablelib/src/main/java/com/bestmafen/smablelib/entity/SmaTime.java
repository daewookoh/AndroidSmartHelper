package com.bestmafen.smablelib.entity;

import java.util.Calendar;

/**
 * This class is used to send the time of your cellphone to a Bluetooth device.
 */
public class SmaTime implements ISmaCmd {
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second;

    public SmaTime() {
        this(Calendar.getInstance());
    }

    public SmaTime(Calendar c) {
        this.year = (byte) (c.get(Calendar.YEAR) - 2000);
        this.month = (byte) (c.get(Calendar.MONTH) + 1);
        this.day = (byte) c.get(Calendar.DAY_OF_MONTH);
        this.hour = (byte) c.get(Calendar.HOUR_OF_DAY);
        this.minute = (byte) c.get(Calendar.MINUTE);
        this.second = (byte) c.get(Calendar.SECOND);
    }

    @Override
    public byte[] toByteArray() {
        byte[] extra = new byte[4];
        extra[0] = (byte) ((month >> 2) | (year << 2));
        extra[1] = (byte) ((hour >> 4) | (day << 1) | (month << 6));
        extra[2] = (byte) ((minute >> 2) | (hour << 4));
        extra[3] = (byte) (second | (minute << 6));
        return extra;
    }

    @Override
    public String toString() {
        return "SmaTime{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                '}';
    }
}
