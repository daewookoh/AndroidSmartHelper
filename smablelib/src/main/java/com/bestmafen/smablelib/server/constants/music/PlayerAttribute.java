package com.bestmafen.smablelib.server.constants.music;

/**
 * Created by xiaokai on 2018/4/4.
 * PlayerAttribute的枚举值
 */
public class PlayerAttribute {
    public static final byte NAME          = 0x00;
    public static final byte PLAYBACK_INFO = 0x01;
    public static final byte VOLUME        = 0x02;

    public static class PlaybackState {
        public static final byte PAUSED          = 0x00;
        public static final byte PLAYING         = 0x01;
        public static final byte REWINDING       = 0x02;
        public static final byte FAST_FORWARDING = 0x03;
    }
}