package com.bestmafen.smablelib.server.constants.notification;

/**
 * Created by Administrator on 2018/5/19/019.
 * NotificationAttributeID Values
 */
public class NotificationAttr {
    public static final byte APP_IDENTIFIER = 0x00;
    public static final byte TITLE          = 0x01;
    public static final byte SUBTITLE       = 0x02;
    public static final byte MESSAGE        = 0x03;
    public static final byte MESSAGE_SIZE   = 0x04;
    public static final byte DATE           = 0x05;

    /**
     * 确认指定attr是否需要携带2个byte的最大长度
     *
     * @param attr attr
     */
    public static boolean needMaxLength(byte attr) {
        return attr == TITLE || attr == SUBTITLE || attr == MESSAGE;
    }
}
