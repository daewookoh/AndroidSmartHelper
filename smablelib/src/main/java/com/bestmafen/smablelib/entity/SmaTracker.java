package com.bestmafen.smablelib.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/3/20.
 */

public class SmaTracker implements Parcelable {
    public long   id;
    public String account;
    public String date;
    /**
     * 0->手机；1->设备
     */
    public int    type;
    public long   start;
    public long   time;
    public double latitude;
    public double longitude;
    public int    altitude;//手机定位获取的海拔数据
    public int    synced;

    @Override
    public String toString() {
        return "SmaTracker{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", date='" + date + '\'' +
                ", type=" + type +
                ", start=" + start +
                ", time=" + time +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", synced=" + synced +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.account);
        dest.writeString(this.date);
        dest.writeInt(this.type);
        dest.writeLong(this.start);
        dest.writeLong(this.time);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.altitude);
        dest.writeInt(this.synced);
    }

    public SmaTracker() {
    }

    protected SmaTracker(Parcel in) {
        this.id = in.readLong();
        this.account = in.readString();
        this.date = in.readString();
        this.type = in.readInt();
        this.start = in.readLong();
        this.time = in.readLong();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.altitude = in.readInt();
        this.synced = in.readInt();
    }

    public static final Parcelable.Creator<SmaTracker> CREATOR = new Parcelable.Creator<SmaTracker>() {
        @Override
        public SmaTracker createFromParcel(Parcel source) {
            return new SmaTracker(source);
        }

        @Override
        public SmaTracker[] newArray(int size) {
            return new SmaTracker[size];
        }
    };
}
