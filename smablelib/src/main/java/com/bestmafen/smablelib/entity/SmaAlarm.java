package com.bestmafen.smablelib.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.bestmafen.smablelib.util.SmaBleUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class presents an alarm object in the device.
 */
public class SmaAlarm implements Parcelable, ISmaCmd {
    public static final Calendar CALENDAR = Calendar.getInstance();
    /**
     * The id sent to device should be in range of 0~7,so that it is different from the id got from database.
     */
    public static byte _id = 0;

    /**
     * Id got from database.
     */
    private long id;

    /**
     * Amount of millisecond from 1970-01-01,the timezone should be "GMT+0".
     */
    private long time;

    /**
     * 00000000 00000000 00000000 00011111 -> 31 -> presents monday,tuesday,wednesday,thursday,friday
     * 00000000 00000000 00000000 01100000 -> 96 -> presents saturday,sunday
     * 00000000 00000000 00000000 00000000 -> 0  -> presents once
     * Can you get it?
     */
    private int repeat = 31;

    /**
     * Whether this alarm is enabled,it will not be sent to device if it gets disabled.
     */
    private int enabled = 1;

    /**
     * The tag of this alarm,it will be shown in device when this alarm rings.
     */
    private String tag;

    /**
     * You can use it or not
     */
    private String account;

    public SmaAlarm() {
        CALENDAR.setTimeZone(SmaBleUtils.getDefaultTimeZone());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {

        CALENDAR.setTimeInMillis(time);
        String t = String.format("%02d",CALENDAR.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d",CALENDAR.get(Calendar.MINUTE));

        return t;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public byte[] toByteArray() {
        byte[] data_item = new byte[23];
        Calendar cal = Calendar.getInstance(SmaBleUtils.getDefaultTimeZone());
        cal.setTimeInMillis(time);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        data_item[0] = (byte) (((year - 2000) << 2) | (month >> 2));
        data_item[1] = (byte) ((day << 1) | (month << 6) | (hour >> 4));
        data_item[2] = (byte) ((hour << 4) | (minute >> 2));
        data_item[3] = (byte) ((minute << 6) | (_id << 3) /*| (enabled << 2)*/);
        data_item[4] = (byte) ((enabled << 7) | repeat);
        try {
            byte[] b = tag.getBytes("utf-8");
            for (int i = 0, l = b.length; i < l; i++) {
                if (i < 18) {
                    data_item[5 + i] = b[i];
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        _id++;
        if (_id > 7) {
            _id = 0;
        }
        return data_item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.time);
        dest.writeInt(this.repeat);
        dest.writeInt(this.enabled);
        dest.writeString(this.tag);
        dest.writeString(this.account);
    }

    protected SmaAlarm(Parcel in) {
        this.id = in.readLong();
        this.time = in.readLong();
        this.repeat = in.readInt();
        this.enabled = in.readInt();
        this.tag = in.readString();
        this.account = in.readString();
    }

    public static final Creator<SmaAlarm> CREATOR = new Creator<SmaAlarm>() {

        @Override
        public SmaAlarm createFromParcel(Parcel source) {
            return new SmaAlarm(source);
        }

        @Override
        public SmaAlarm[] newArray(int size) {
            return new SmaAlarm[size];
        }
    };

    @Override
    public String toString() {
        CALENDAR.setTimeInMillis(time);
        return "SmaAlarm{" +
                "id=" + id +
                ", year=" + CALENDAR.get(Calendar.YEAR) +
                ", month=" + (CALENDAR.get(Calendar.MONTH) + 1) +
                ", day=" + CALENDAR.get(Calendar.DAY_OF_MONTH) +
                ", hour=" + CALENDAR.get(Calendar.HOUR_OF_DAY) +
                ", minute=" + CALENDAR.get(Calendar.MINUTE) +
                ", repeat=" + repeat +
                ", enabled=" + enabled +
                ", tag='" + tag + '\'' +
                ", account='" + account + '\'' +
                '}';
    }
}
