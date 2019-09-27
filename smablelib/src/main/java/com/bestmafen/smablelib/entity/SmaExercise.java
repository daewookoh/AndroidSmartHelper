package com.bestmafen.smablelib.entity;

/**
 * Created by Administrator on 2017/11/25.
 */

public class SmaExercise {
    /**
     * Q2，B2跑步
     */
    public static final int RUNNING_Q2_B2 = -1;

    public static final int CYCLING_B2_B3 = -2;

    public static final int INDOOR_B3G   = -3;
    public static final int OUTDOOR_B3G  = -4;
    public static final int CYCLING_B3G  = -5;
    public static final int SWIMMING_B3G = -6;

    public static final int INDOOR_M   = 0;
    public static final int OUTDOOR_M  = 1;
    public static final int CYCLING_M  = 2;
    public static final int CLIMB_M    = 3;
    public static final int MARATHON_M = 4;

    public static final int SWIMMING_M3  = 5;
    public static final int FAST_WALK_M3 = 6;

    public long   id;
    public long   start;
    public long   end;
    public int    duration;
    public String date;

    /**
     * 海拔 m
     */
    public int altitude;

    /**
     * 气压 KPa
     */
    public int airPressure;

    /**
     * 步频 step/min
     */
    public int spm;
    public int type;
    public int step;

    /**
     * 距离 m
     */
    public int distance;

    /**
     * 卡路里
     */
    public double cal;

    /**
     * 速度 km/h
     */
    public int speed;

    /**
     * s/km
     */
    public int    pace;
    public String account;
    public int    synced;

    @Override
    public String toString() {
        return "SmaExercise{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", duration=" + duration +
                ", date='" + date + '\'' +
                ", altitude=" + altitude +
                ", airPressure=" + airPressure +
                ", spm=" + spm +
                ", type=" + type +
                ", step=" + step +
                ", distance=" + distance +
                ", cal=" + cal +
                ", speed=" + speed +
                ", pace=" + pace +
                ", account='" + account + '\'' +
                ", synced=" + synced +
                '}';
    }
}
