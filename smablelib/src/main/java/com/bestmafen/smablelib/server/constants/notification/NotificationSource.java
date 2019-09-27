package com.bestmafen.smablelib.server.constants.notification;

import android.text.TextUtils;

import com.bestmafen.easeblelib.util.EaseUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2018/5/19/019.
 * 通知事件
 */
public class NotificationSource {
    public static final int NOTIFICATION_ID_INCOMING_CALL = Integer.MAX_VALUE;
    public static final int NOTIFICATION_ID_MISSED_CALL   = Integer.MAX_VALUE - 1;

    public static final byte EVENT_ID_ADDED    = 0x00;
    public static final byte EVENT_ID_MODIFIED = 0x01;
    public static final byte EVENT_ID_REMOVED  = 0x02;

    public static final byte EVENT_FLAG_SILENT          = 0x01;
    public static final byte EVENT_FLAG_IMPORTANT       = 0x02;
    public static final byte EVENT_FLAG_PREEXISTING     = 0x04;
    public static final byte EVENT_FLAG_POSITIVE_ACTION = 0x08;
    public static final byte EVENT_FLAG_NEGATIVE_ACTION = 0x10;

    public static final byte CATEGORY_ID_OTHER                = 0x00;
    public static final byte CATEGORY_ID_INCOMING_CALL        = 0x01;
    public static final byte CATEGORY_ID_MISSED_CALL          = 0x02;
    public static final byte CATEGORY_ID_VOICE_MAIL           = 0x03;
    public static final byte CATEGORY_ID_SOCIAL               = 0x04;
    public static final byte CATEGORY_ID_SCHEDULE             = 0x05;
    public static final byte CATEGORY_ID_EMAIL                = 0x06;
    public static final byte CATEGORY_ID_NEWS                 = 0x07;
    public static final byte CATEGORY_ID_HEALTH_AND_FITNESS   = 0x08;
    public static final byte CATEGORY_ID_BUSINESS_AND_FINANCE = 0x09;
    public static final byte CATEGORY_ID_LOCATION             = 0x10;
    public static final byte CATEGORY_ID_ENTERTAINMENT        = 0x11;

    public byte eventId       = 0;
    public byte eventFlag     = 0;
    public byte categoryId    = 0;
    public byte categoryCount = 0;
    public int  id            = 0;

    /**
     * app的包名,以'\0x00'结尾
     */
    public String identifier;
    public String title;
    public String message;
    public String date;

    public byte[] toByteArray() {
        byte[] array = new byte[8];
        array[0] = this.eventId;
        array[1] = this.eventFlag;
        array[2] = this.categoryId;
        array[3] = this.categoryCount;
        array[4] = (byte) (this.id & 0xff);
        array[5] = (byte) ((this.id >> 8) & 0xff);
        array[6] = (byte) ((this.id >> 16) & 0xff);
        array[7] = (byte) (this.id >> 24);
        return array;
    }

    /**
     * @param maxLength 0，未指定maxLength; >0，指定maxLength
     */
    public byte[] getAttr(byte attr, int maxLength) {
        String attrText = "";
        switch (attr) {
            case NotificationAttr.APP_IDENTIFIER:
                attrText = !TextUtils.isEmpty(identifier) ? identifier : "";
                break;
            case NotificationAttr.TITLE:
                attrText = !TextUtils.isEmpty(title) ? title : "";
                break;
            case NotificationAttr.SUBTITLE:

                break;
            case NotificationAttr.MESSAGE:
                attrText = !TextUtils.isEmpty(message) ? message : "";
                break;
            case NotificationAttr.MESSAGE_SIZE:

                break;
            case NotificationAttr.DATE:
                attrText = !TextUtils.isEmpty(date) ? date : "";
                break;
        }
        byte[] value = new byte[0];
        try {
            value = attrText.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (maxLength != 0 && value.length > maxLength) {
            byte[] result = new byte[maxLength];
            System.arraycopy(value, 0, result, 0, maxLength);
            return result;
        }
        return value;
    }

    @Override
    public String toString() {
        return "NotificationSource{" +
                "byte[]='" + EaseUtils.byteArray2HexString(toByteArray()) + '\'' +
                ", eventId='" + getEventIDString() + '\'' +
                ", eventFlag='" + getEventFlagString() + '\'' +
                ", categoryId='" + getCategoryIDString() + '\'' +
                ", categoryCount=" + categoryCount +
                ", id=" + id +
                ", identifier='" + identifier + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    private String getEventIDString() {
        switch (eventId) {
            case EVENT_ID_ADDED:
                return "ADDED";
            case EVENT_ID_MODIFIED:
                return "MODIFIED";
            case EVENT_ID_REMOVED:
                return "REMOVED";
            default:
                return "RESERVED";
        }
    }

    private String getEventFlagString() {
        StringBuilder builder = new StringBuilder();
        if ((eventFlag & 0x01) == 1) {
            builder.append("SILENT ");
        }
        if (((eventFlag >> 1) & 0x01) == 1) {
            builder.append("IMPORTANT ");
        }
        if (((eventFlag >> 2) & 0x01) == 1) {
            builder.append("PREEXISTING ");
        }
        if (((eventFlag >> 3) & 0x01) == 1) {
            builder.append("POSITIVE_ACTION ");
        }
        if (((eventFlag >> 4) & 0x01) == 1) {
            builder.append("NEGATIVE_ACTION ");
        }
        return builder.toString();
    }

    private String getCategoryIDString() {
        switch (categoryId) {
            case CATEGORY_ID_OTHER:
                return "OTHER";
            case CATEGORY_ID_INCOMING_CALL:
                return "INCOMING_CALL";
            case CATEGORY_ID_MISSED_CALL:
                return "MISSED_CALL";
            case CATEGORY_ID_VOICE_MAIL:
                return "VOICE_MAIL";
            case CATEGORY_ID_SOCIAL:
                return "SOCIAL";
            case CATEGORY_ID_SCHEDULE:
                return "SCHEDULE";
            case CATEGORY_ID_EMAIL:
                return "EMAIL";
            case CATEGORY_ID_NEWS:
                return "NEWS";
            case CATEGORY_ID_HEALTH_AND_FITNESS:
                return "HEALTH_AND_FITNESS";
            case CATEGORY_ID_BUSINESS_AND_FINANCE:
                return "BUSINESS_AND_FINANCE";
            case CATEGORY_ID_LOCATION:
                return "LOCATION";
            case CATEGORY_ID_ENTERTAINMENT:
                return "ENTERTAINMENT";
            default:
                return "RESERVED";
        }
    }
}
