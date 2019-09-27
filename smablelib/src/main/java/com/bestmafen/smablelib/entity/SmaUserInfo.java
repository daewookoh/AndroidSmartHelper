package com.bestmafen.smablelib.entity;

/**
 * This class is used to send personal information of users to a Bluetooth device.
 */
public class SmaUserInfo implements ISmaCmd {
    /**
     * 0 female;1 male
     */
    public int gender;

    /**
     * 0~127
     */
    public int age;

    /**
     * 0~256 cm,step is 0.5cm
     */
    public float height;

    /**
     * 0~512 Kg,step is 0.5Kg
     */
    public float weight;

    @Override
    public byte[] toByteArray() {
        byte[] extra = new byte[4];
        extra[0] = (byte) ((gender << 7) | age);
        extra[1] = (byte) (((int) height * 2) >> 1);
        extra[2] = (byte) (((int) (weight * 2) >> 3) | (((int) height * 2) << 7));
        extra[3] = (byte) ((int) (weight * 2) << 5);
        return extra;
    }
}

