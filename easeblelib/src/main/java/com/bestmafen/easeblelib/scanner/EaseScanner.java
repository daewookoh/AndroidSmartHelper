package com.bestmafen.easeblelib.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

/**
 * This is an abstract class which is used to replace {@link BluetoothAdapter#startLeScan(BluetoothAdapter.LeScanCallback)} and
 * {@link android.bluetooth.le.BluetoothLeScanner#startScan(List, ScanSettings, ScanCallback)}.There are different
 * implementations on different API level,see {@link ScannerNew} and {@link ScannerOld}.
 */

public abstract class EaseScanner {
    static BluetoothAdapter sBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    protected  Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * Scan period.A scan will be stopped after this appointed time,also you can stop it manually when this appointed time has not
     * arrived.
     */
    long mPeriod = 5000;

    /**
     * Scan option.
     */
    ScanOption mScanOption;

    /**
     * Scan callback.
     */
    EaseScanCallback mEaseScanCallback;

    /**
     * Whether is scanning.
     */
    public volatile boolean isScanning = false;

    /**
     * The runnable to stop a scan.
     */
    private Runnable mStopScanRunnable = new Runnable() {

        @Override
        public void run() {
            startScan(false);
        }
    };

    /**
     * Start or stop a scan.
     *
     * @param scan start if true,stop if false.
     */
    public abstract void startScan(boolean scan);

    /**
     * Stop scanning and set the {@link EaseScanCallback} to null to avoid memory leaking.
     */
    public void exit() {
        startScan(false);
        mEaseScanCallback = null;
    }

    /**
     * Set the {@link ScanOption}.
     *
     * @param option scan option
     * @return This EaseScanner object.
     */
    public EaseScanner setScanOption(ScanOption option) {
        mScanOption = option;
        return this;
    }

    /**
     * Set the {@link EaseScanCallback}.
     *
     * @param easeScanCallback scan callback
     * @return This EaseScanner object.
     */
    public EaseScanner setEaseScanCallback(EaseScanCallback easeScanCallback) {
        mEaseScanCallback = easeScanCallback;
        return this;
    }

    /**
     * Stop a scanning when its {@link EaseScanner#mPeriod} has arrived.
     */
    void stopScanDelay() {
        mHandler.postDelayed(mStopScanRunnable, mPeriod);
    }

    /**
     * Remove the runnable which to stop scanning.
     */
    void removeStop() {
        mHandler.removeCallbacksAndMessages(null);
    }
}
