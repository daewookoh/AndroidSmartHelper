package com.bestmafen.smablelib.entity;

/**
 * This interface presents an extra data,see
 * {@link com.bestmafen.smablelib.component.SmaManager#write(byte, byte, ISmaCmd)} and
 * {@link com.bestmafen.smablelib.component.SmaManager#write(byte, byte, byte[])}
 */
public interface ISmaCmd {
    byte[] toByteArray();
}
