package com.bestmafen.smablelib.entity;

/**
 * Created by Administrator on 2017/9/23.
 */

public class SmaCycling {
    public static final int START = 0;
    public static final int GOING = 1;
    public static final int END   = 2;

    public long   id;
    public long   time;
    public String date;
    public long   start;
    public String dateStart;
    public long   end;
    public String dateEnd;
    public int    cal;
    public int    rate;
    public String account;
    public int    synced;

    @Override
    public String toString() {
        return "SmaCycling{" +
                "id=" + id +
                ", time=" + time +
                ", date='" + date + '\'' +
                ", start=" + start +
                ", dateStart='" + dateStart + '\'' +
                ", end=" + end +
                ", dateEnd='" + dateEnd + '\'' +
                ", cal=" + cal +
                ", rate=" + rate +
                ", account='" + account + '\'' +
                ", synced=" + synced +
                '}';
    }
}
