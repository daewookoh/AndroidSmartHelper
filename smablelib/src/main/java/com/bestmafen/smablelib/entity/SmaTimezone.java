package com.bestmafen.smablelib.entity;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiaokai on 2018/7/24.
 */
public class SmaTimezone implements ISmaCmd {
    private static final long MAX_OFFSET = TimeUnit.DAYS.toMillis(1);
    /**
     * {@link TimeZone#getRawOffset()}
     */
    private long rawOffset;

    public SmaTimezone() {
        this.rawOffset = TimeZone.getDefault().getRawOffset();
    }

    @Override
    public byte[] toByteArray() {
        if (rawOffset < -MAX_OFFSET || rawOffset > MAX_OFFSET) {
            rawOffset = 0;
        }
        byte fifteenMinutes = (byte) (rawOffset / 1000 / 60 / 15);
        return new byte[]{fifteenMinutes};
    }
}
