package com.bestmafen.easeblelib.scanner;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.bestmafen.easeblelib.entity.EaseDevice;
import com.bestmafen.easeblelib.util.L;

import java.util.List;

/**
 * This class is used to implement {@link EaseScanner} when API>=21.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class ScannerNew extends EaseScanner {
    private ScanCallback mScanCallback;
    private ScanSettings mScanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();

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

            if (mScanCallback == null) {
                mScanCallback = new ScanCallback() {

                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        if (result == null) return;

                        final BluetoothDevice device = result.getDevice();
                        if (device == null) return;

                        String name = device.getName();
                        String address = device.getAddress();
                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address)) return;

//                        L.v("ScannerNew -> onScanResult " + name + " " + address);
                        final int rssi = result.getRssi();
                        final byte[] scanRecord = result.getScanRecord() == null ? new byte[0] : result.getScanRecord()
                                .getBytes();

                        if (mScanOption != null) {
                            if (mScanOption.mFilterNames.contains(name)) return;

                            if (mScanOption.mFilterAddresses.contains(address)) return;

                            if (mScanOption.mSpecifiedNames.size() > 0 && !mScanOption.mSpecifiedNames.contains(name)) return;

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

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        super.onBatchScanResults(results);
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
                        super.onScanFailed(errorCode);
                    }
                };
            }

            if (mScanOption != null) {
                mPeriod = mScanOption.mPeriod;
            }
            stopScanDelay();

            BluetoothLeScanner scanner = sBluetoothAdapter.getBluetoothLeScanner();
            if (scanner != null) {
                scanner.startScan(null, mScanSettings, mScanCallback);
            }
        } else {
            removeStop();

            BluetoothLeScanner scanner = sBluetoothAdapter.getBluetoothLeScanner();
            if (scanner != null) {
                scanner.stopScan(mScanCallback);
            }
        }

        isScanning = scan;
        L.d("ScannerNew -> startScan " + scan);
        mEaseScanCallback.onScanStart(scan);
//        } catch (Exception e) {
//            e.printStackTrace();
//            isScanning = scan;
//            L.d("ScannerNew -> startScan " + scan);
//            mEaseScanCallback.onScanStart(scan);
//        }
    }
}
