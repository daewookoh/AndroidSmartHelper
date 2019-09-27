package com.bestmafen.easeblelib.entity;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.bestmafen.easeblelib.util.EaseUtils;

/**
 * The class wraps a {@link BluetoothDevice}.
 */
public class EaseDevice implements Parcelable, Comparable<EaseDevice> {

    public BluetoothDevice device;
    public int rssi;
    public byte[] scanRecord;

    @Override
    public boolean equals(Object o) {
        return o instanceof EaseDevice && device.equals(((EaseDevice) o).device);
    }

    @Override
    public int hashCode() {
        if (device == null) return 0;

        if (TextUtils.isEmpty(device.getAddress())) return 0;

        return device.getAddress().hashCode();
    }

    @Override
    public int compareTo(EaseDevice o) {
        return o.rssi - rssi;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.device, flags);
        dest.writeInt(this.rssi);
        dest.writeByteArray(this.scanRecord);
    }

    public EaseDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
    }

    protected EaseDevice(Parcel in) {
        this.device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        this.rssi = in.readInt();
        this.scanRecord = in.createByteArray();
    }

    public static final Creator<EaseDevice> CREATOR = new Creator<EaseDevice>() {

        @Override
        public EaseDevice createFromParcel(Parcel source) {
            return new EaseDevice(source);
        }

        @Override
        public EaseDevice[] newArray(int size) {
            return new EaseDevice[size];
        }
    };

    @Override
    public String toString() {
        return "EaseDevice{" +
                "device=" + device +
                ", rssi=" + rssi +
                ", scanRecord=" + EaseUtils.byteArray2HexString(scanRecord) +
                '}';
    }
}
