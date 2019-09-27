package com.bestmafen.smablelib.entity;

import com.bestmafen.easeblelib.util.EaseUtils;

/**
 * Created by xiaokai on 2018/7/24.
 */
public class SmaCoordinate implements ISmaCmd {
    public float longitude;
    public float latitude;
    public float altitude;

    public SmaCoordinate(float longitude, float latitude, float altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    @Override
    public byte[] toByteArray() {
        byte[] bytes = new byte[12];
        EaseUtils.writeFloat(bytes, 0, longitude, true);
        EaseUtils.writeFloat(bytes, 4, latitude, true);
        EaseUtils.writeFloat(bytes, 8, altitude, true);
        return bytes;
    }

    @Override
    public String toString() {
        return "SmaCoordinate{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                ", toByteArray=" + EaseUtils.byteArray2HexString(toByteArray()) +
                '}';
    }
}
