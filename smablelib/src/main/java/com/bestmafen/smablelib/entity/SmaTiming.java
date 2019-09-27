package com.bestmafen.smablelib.entity;

public class SmaTiming implements ISmaCmd {
    public static final int CMD_STOP = 0x00;
    public static final int CMD_PREPARE = 0x01;
    public static final int CMD_CANCEL = 0x02;
    public static final int CMD_POINTER = 0x03;
    public static final int CMD_START_NO_PARAM = 0x04;
    public static final int CMD_START2_WITH_PARAM = 0x05;

    public int cmd, hour, minute, second;

    @Override
    public byte[] toByteArray() {
        byte[] extra = new byte[4];
        extra[0] = (byte) cmd;
        extra[1] = (byte) hour;
        extra[2] = (byte) minute;
        extra[3] = (byte) second;
        return extra;
    }
}