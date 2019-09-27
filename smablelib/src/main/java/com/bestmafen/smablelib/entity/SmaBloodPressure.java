package com.bestmafen.smablelib.entity;

/**
 * Created by Administrator on 2017/8/29.
 */

public class SmaBloodPressure {
    public long id;
    public String date;
    public long time;
    public int diastolic;
    public int systolic;
    public String account;
    public int synced;

    @Override
    public String toString() {
        return "SmaBloodPressure{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", time=" + time +
                ", diastolic=" + diastolic +
                ", systolic=" + systolic +
                ", account=" + systolic + '\'' +
                ", synced=" + synced +
                '}';
    }
}
