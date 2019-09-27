package com.bestmafen.smablelib.entity;

public class SmaSwitch {
    public String name;
    public int imgRes;
    public byte cmd;
    public byte key;
    public boolean enabled;

    public SmaSwitch(String name, int imgRes, byte cmd, byte key, boolean enabled) {
        this.name = name;
        this.imgRes = imgRes;
        this.cmd = cmd;
        this.key = key;
        this.enabled = enabled;
    }
}