package com.bestmafen.smablelib.entity;

/**
 * This class is used to enable or disable automatic heart rate detecting.
 */
public class SmaHeartRateSettings implements ISmaCmd {
    /**
     * The start oclock.
     */
    private int start;

    /**
     * The end oclock.
     */
    private int end = 0;

    /**
     * Whether this automatic heart rate detecting is enabled.
     */
    private int enabled = 1;

    /**
     * The interval of this automatic heart rate detecting.
     */
    private int interval = 60;

    private String account;
    private int synced;

    public SmaHeartRateSettings() {
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    @Override
    public byte[] toByteArray() {
        byte[] extra = new byte[8];
        byte repeat;
        if (enabled == 1) {
            repeat = 0x7F & 0xff;
        } else {
            repeat = (byte) (0x80 & 0xff);
        }
        extra[0] = repeat;
        extra[1] = (byte) interval;
        extra[2] = (byte) end;
        extra[3] = (byte) start;
        extra[4] = (byte) 0x19;
        extra[5] = (byte) 0x19;
        extra[6] = (byte) 0x19;
        extra[7] = (byte) 0x19;
        return extra;
    }

    @Override
    public String toString() {
        return "SmaHeartRateSettings{" +
                "start=" + start +
                ", end=" + end +
                ", enabled=" + enabled +
                ", interval=" + interval +
                ", account='" + account + '\'' +
                ", synced=" + synced +
                '}';
    }
}
