package com.bestmafen.smablelib.entity;

public class SmaSedentarinessSettings implements ISmaCmd {
    private int enabled = 1;
    private int repeat = 31;
    private int start1 = 9;
    private int end1 = 12;
    private int enabled1 = 1;
    private int start2 = 14;
    private int end2 = 18;
    private int enabled2 = 1;
    private int threshold = 30;//默认阈值，一般不更改
    private int interval = 60;
    private String account;
    private int synced;

    public SmaSedentarinessSettings() {
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getStart1() {
        return start1;
    }

    public void setStart1(int start1) {
        this.start1 = start1;
    }

    public int getEnd1() {
        return end1;
    }

    public void setEnd1(int end1) {
        this.end1 = end1;
    }

    public int getStart2() {
        return start2;
    }

    public void setStart2(int start2) {
        this.start2 = start2;
    }

    public int getEnd2() {
        return end2;
    }

    public void setEnd2(int end2) {
        this.end2 = end2;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getEnabled1() {
        return enabled1;
    }

    public void setEnabled1(int enabled1) {
        this.enabled1 = enabled1;
    }


    public int getEnabled2() {
        return enabled2;
    }

    public void setEnabled2(int enabled2) {
        this.enabled2 = enabled2;
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
        int s1, e1, s2, e2;
        if (enabled1 == 1) {
            s1 = start1;
            e1 = end1;
        } else {
            s1 = 24;
            e1 = 24;
        }
        if (enabled2 == 1) {
            s2 = start2;
            e2 = end2;
        } else {
            s2 = 24;
            e2 = 24;
        }
        byte[] extra = new byte[8];
        extra[0] = (byte) repeat;
        extra[1] = (byte) (e2 << 3 | s2 >> 2);
        extra[2] = (byte) ((s2 & 3) << 6 | e1 << 1 | s1 >> 4);
        extra[3] = (byte) ((s1 & 15) << 4 | interval >> 4);
        extra[4] = (byte) ((interval & 15) << 4);
        extra[5] = 1;
        extra[6] = (byte) ((threshold & 15) << 4);
        extra[7] = (byte) (((enabled1 == 1 || enabled2 == 1) ? 1 : 0) << 4);
        return extra;
    }

    @Override
    public String toString() {
        return "SmaSedentarinessSettings{" +
                "repeat=" + repeat +
                ", enabled=" + enabled +
                ", start1=" + start1 +
                ", end1=" + end1 +
                ", enabled1=" + enabled1 +
                ", start2=" + start2 +
                ", end2=" + end2 +
                ", enabled2=" + enabled2 +
                ", threshold=" + threshold +
                ", interval=" + interval +
                ", account='" + account + '\'' +
                ", synced=" + synced +
                '}';
    }
}
