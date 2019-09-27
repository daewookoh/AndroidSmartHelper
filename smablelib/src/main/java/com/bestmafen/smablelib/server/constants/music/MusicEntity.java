package com.bestmafen.smablelib.server.constants.music;

/**
 * Created by xiaokai on 2018/4/4.
 * 音乐控制的Entity的枚举值
 */
public class MusicEntity {
    public static final byte PLAYER = 0x00;
    public static final byte QUEUE  = 0x01;
    public static final byte TRACK  = 0x02;

    public static String getEntityText(byte entity) {
        switch (entity) {
            case PLAYER:
                return "PLAYER";
            case QUEUE:
                return "QUEUE";
            case TRACK:
                return "TRACK";
            default:
                return "UNKNOWN";
        }
    }

    public static String getAttrText(byte entity, byte attr) {
        switch (entity) {
            case PLAYER:
                if (attr == PlayerAttribute.NAME) return "NAME";
                if (attr == PlayerAttribute.PLAYBACK_INFO) return "PLAYBACK_INFO";
                if (attr == PlayerAttribute.VOLUME) return "VOLUME";
            case QUEUE:
                if (attr == QueueAttribute.INDEX) return "INDEX";
                if (attr == QueueAttribute.COUNT) return "COUNT";
                if (attr == QueueAttribute.SHUFFLE_MODE) return "SHUFFLE_MODE";
                if (attr == QueueAttribute.REPEAT_MODE) return "REPEAT_MODE";
            case TRACK:
                if (attr == TrackAttribute.ARTIST) return "ARTIST";
                if (attr == TrackAttribute.ALBUM) return "ALBUM";
                if (attr == TrackAttribute.TITLE) return "TITLE";
                if (attr == TrackAttribute.DURATION) return "DURATION";
            default:
                return "UNKNOWN";
        }
    }

    public static String getEntityAndAttrs(byte[] data) {
        if (data == null || data.length < 2) return "data length<2";

        StringBuilder builder = new StringBuilder(getEntityText(data[0])).append(" -> ");
        for (int i = 1, len = data.length; i < len; i++) {
            builder.append(getAttrText(data[0], data[i])).append(" ");
        }
        return builder.toString();
    }
}
