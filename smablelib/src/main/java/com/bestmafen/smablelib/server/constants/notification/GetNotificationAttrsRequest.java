package com.bestmafen.smablelib.server.constants.notification;

import android.util.ArrayMap;

/**
 * Created by Administrator on 2018/5/19/019.
 * 获取通知属性的请求
 */
public class GetNotificationAttrsRequest {
    /**
     * 通知的id
     */
    public int notificationId;
    /**
     * 需要读取通知的属性以及长度
     */
    public ArrayMap<Byte, Short> mAttrsMap = new ArrayMap<>();

    public static GetNotificationAttrsRequest create(byte[] data) {
        GetNotificationAttrsRequest request = new GetNotificationAttrsRequest();
        int notificationId = data[4] << 24;//id为正的int值，data[4]的范围为0~127，不用&0xff
        notificationId |= (data[3] & 0xff) << 16;
        notificationId |= (data[2] & 0xff) << 8;
        notificationId |= data[1] & 0xff;
        request.notificationId = notificationId;
        int index = 5;
        while (index < data.length) {
            byte attr = data[index];
            if (NotificationAttr.needMaxLength(attr)) {
                if (index > data.length - 3) break;
                //实际上协议中最大长度应该是2个byte的无符号整数，java中应该用int来表示，但是考虑长度也不会超过
                //short的最大值，所以就直接用short表示了
                request.mAttrsMap.put(attr, (short) (((data[index + 2] & 0xff) << 8) | (data[index + 1] & 0xff)));
                index += 3;
            } else {
                request.mAttrsMap.put(attr, (short) 0);
                index++;
            }
        }
        return request;
    }
}
