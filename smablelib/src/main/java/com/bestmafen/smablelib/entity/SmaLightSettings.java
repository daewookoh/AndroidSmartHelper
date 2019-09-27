package com.bestmafen.smablelib.entity;

public class SmaLightSettings implements ISmaCmd {
    private int repeat = 127;  //UI界面当前没有设置星期的选项,默认全开
    private int start1 = 9;
    private int end1 = 23;
    private int enabled1 = 1;
    private String account;
    private int synced;

    public SmaLightSettings() {
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

    public int getEnabled1() {
        return enabled1;
    }

    public void setEnabled1(int enabled1) {
        this.enabled1 = enabled1;
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
        byte[] extra = new byte[5];
        extra[0] = 0;
        extra[1] = (byte) ((enabled1 == 1) ? 1 : 0);
        extra[2] = (byte) start1;
        extra[3] = (byte) end1;
        extra[4] = (byte) repeat;
        return extra;
    }

    @Override
    public String toString() {
        return "SmaSedentarinessSettings{" +
                "repeat=" + repeat +
                ", start1=" + start1 +
                ", end1=" + end1 +
                ", enabled1=" + enabled1 +
                ", account='" + account + '\'' +
                ", synced=" + synced +
                '}';
    }
}
