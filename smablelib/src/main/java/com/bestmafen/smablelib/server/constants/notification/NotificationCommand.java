package com.bestmafen.smablelib.server.constants.notification;

/**
 * Created by Administrator on 2018/5/19/019.
 * CommandID Values
 */
public class NotificationCommand {
    public static final byte GET_NOTIFICATION_ATTRS      = 0x00;
    public static final byte GET_APPLICATION_ATTRS       = 0x01;
    public static final byte PERFORM_NOTIFICATION_ACTION = 0x02;

    public static String getCommandText(byte command) {
        switch (command) {
            case GET_NOTIFICATION_ATTRS:
                return "GET_APPLICATION_ATTRS";
            case GET_APPLICATION_ATTRS:
                return "GET_APPLICATION_ATTRS";
            case PERFORM_NOTIFICATION_ACTION:
                return "PERFORM_NOTIFICATION_ACTION";
            default:
                return "UNKNOWN";
        }
    }
}
