package com.bestmafen.smablelib.server.constants.music;

/**
 * Created by xiaokai on 2018/4/4.
 * 音乐控制的命令的枚举值
 */
public class MusicCommand {
    public static final byte PLAY        = 0X00;
    public static final byte PAUSE       = 0X01;
    public static final byte TOGGLE      = 0X02;
    public static final byte NEXT        = 0X03;
    public static final byte PRE         = 0X04;
    public static final byte VOLUME_UP   = 0X05;
    public static final byte VOLUME_DOWN = 0X06;

    public static String getCommandText(byte command) {
        switch (command) {
            case PLAY:
                return "PLAY";
            case PAUSE:
                return "PAUSE";
            case TOGGLE:
                return "TOGGLE";
            case NEXT:
                return "NEXT";
            case PRE:
                return "PRE";
            case VOLUME_UP:
                return "VOLUME_UP";
            case VOLUME_DOWN:
                return "VOLUME_DOWN";
            default:
                return "UNKNOWN";
        }
    }
}

