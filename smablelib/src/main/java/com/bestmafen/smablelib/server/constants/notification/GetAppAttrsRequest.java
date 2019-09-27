package com.bestmafen.smablelib.server.constants.notification;

import java.util.Arrays;

/**
 * Created by Administrator on 2018/5/21/019.
 * 获取App属性的请求
 */
public class GetAppAttrsRequest {
    /**
     * app的包名,以'\0x00'结尾
     */
    public byte[] mAppIdentifier;
    /**
     * 需要读取app的属性
     */
    public byte[] mAttrs;

    public static GetAppAttrsRequest create(byte[] value) {
        for (int i = 1, l = value.length; i < l; i++) {
            if (value[i] == 0x00) {
                GetAppAttrsRequest request = new GetAppAttrsRequest();
                request.mAppIdentifier = Arrays.copyOfRange(value, 1, i + 1);
                request.mAttrs = Arrays.copyOfRange(value, i + 1, value.length);
                return request;
            }
        }

        return null;
    }
}
