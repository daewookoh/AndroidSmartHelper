/*
package com.bestmafen.easeblelib.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.bestmafen.easeblelib.entity.EaseDevice;
import com.bestmafen.easeblelib.util.L;

/**
 * This class is used to implement {@link EaseScanner} when API<21.
 * /
class ScannerOld extends EaseScanner {
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    @Override
    public void startScan(boolean scan) {
//        try {
        if (mEaseScanCallback == null) return;

        if (isScanning == scan) return;

        if (scan) {
            if (!sBluetoothAdapter.isEnabled()) {
                mEaseScanCallback.onBluetoothDisabled();
                return;
            }

            if (mLeScanCallback == null) {
                mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

                    @Override
                    public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                        if (device == null) return;

                        String name = device.getName();
                        String address = device.getAddress();
                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address)) return;

//                        L.v("ScannerOld -> onLeScan " + name + " " + address);
                        if (mScanOption != null) {
                            if (mScanOption.mFilterNames.contains(name)) return;

                            if (mScanOption.mFilterAddresses.contains(address)) return;

                            if (mScanOption.mSpecifiedNames.size() > 0 && !mScanOption.mSpecifiedNames.contains(name))
                                return;

                            if (mScanOption.mSpecifiedAddresses.size() > 0 && !mScanOption.mSpecifiedAddresses.contains
                                    (address)) return;

                            if (mScanOption.mMinRssi > rssi) return;
                        }

                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (mEaseScanCallback == null) return;

                                if (!isScanning) return;

                                mEaseScanCallback.onDeviceFound(new EaseDevice(device, rssi, scanRecord));
                            }
                        });
                    }
                };
            }

            if (mScanOption != null) {
                mPeriod = mScanOption.mPeriod;
            }
            stopScanDelay();
            if (sBluetoothAdapter!=null){
                sBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            removeStop();
            if (sBluetoothAdapter != null) {
                sBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }

        isScanning = scan;
        L.d("ScannerOld -> startScan " + scan);
        mEaseScanCallback.onScanStart(scan);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
*/