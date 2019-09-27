package com.bestmafen.smablelib.server.constants.music;

/**
 * Created by xiaokai on 2018/4/4.
 * QueueAttribute的枚举值
 */
public class QueueAttribute {
    public static final byte INDEX        = 0x00;
    public static final byte COUNT        = 0x01;
    public static final byte SHUFFLE_MODE = 0x02;
    public static final byte REPEAT_MODE  = 0x03;

    public static class ShuffleMode {
        public static final byte OFF = 0x00;
        public static final byte ONE = 0x01;
        public static final byte ALL = 0x02;
    }

    public static class RepeatAttribute {
        public static final byte OFF = 0x00;
        public static final byte ONE = 0x01;
        public static final byte ALL = 0x02;
    }
}