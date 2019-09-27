package com.bestmafen.easeblelib.scanner;

import com.bestmafen.easeblelib.entity.EaseDevice;

/**
 * Interface used to process scan result.
 */
public interface EaseScanCallback {

    /**
     * This method will be invoked when a expected device found.
     *
     * @param device a expected device
     */
    void onDeviceFound(EaseDevice device);

    /**
     * This method will be invoked when you start a scan with Bluetooth adapter being disabled.
     */
    void onBluetoothDisabled();

    /**
     * This method will be invoked when a scan starts or stops.
     *
     * @param start true if start,false if stop
     */
    void onScanStart(boolean start);
}
