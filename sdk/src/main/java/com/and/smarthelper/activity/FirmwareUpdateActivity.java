package com.and.smarthelper.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import com.and.smarthelper.service.DfuService;
import com.bestmafen.easeblelib.entity.EaseDevice;
import com.bestmafen.easeblelib.scanner.EaseScanCallback;
import com.bestmafen.easeblelib.scanner.EaseScanner;
import com.bestmafen.easeblelib.scanner.ScanOption;
import com.bestmafen.easeblelib.scanner.ScannerFactory;
import com.bestmafen.easeblelib.util.L;
import com.bestmafen.smablelib.component.SmaManager;
import com.bestmafen.smablelib.util.SmaBleUtils;
import com.and.smarthelper.R;
import com.and.smarthelper.util.T;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class FirmwareUpdateActivity extends BaseActivity {
    private static final int REQUEST_CODE_PICK_FILE = 0x01;

    private TextView tv;

    private          EaseScanner mScanner;
    private          SmaManager  mSmaManager;
    private volatile boolean     isDeviceFound;
    private          EaseDevice  mTarget;

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {

        @Override
        public void onDeviceConnected(String deviceAddress) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
            tv.setText(getString(R.string.format_percent, 0));
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            super.onDeviceDisconnected(deviceAddress);
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {

        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart,
                                      int partsTotal) {
            tv.setText(getString(R.string.format_percent, percent));
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            finish();
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            L.e("onError deviceAddress -> " + deviceAddress + "," + error + "," + errorType + "," + message);
            showProgress("");
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            L.e("onDfuAborted deviceAddress -> " + deviceAddress);
            showProgress("");
        }
    };

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_firmware_update;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mSmaManager = SmaManager.getInstance();
        final String address = SmaBleUtils.addressPlus1(mSmaManager.getSavedAddress());
        mScanner = ScannerFactory.createScanner().setScanOption(new ScanOption().specifyAddress(address))
                                 .setEaseScanCallback(new EaseScanCallback() {

                                     @Override
                                     public void onDeviceFound(final EaseDevice device) {
                                         L.v("FirmwareUpdateActivity -> onDeviceFound " + device.toString());
                                         L.d("搜索到目标设备 -> 开始固件升级");
                                         isDeviceFound = true;
                                         mScanner.startScan(false);
                                         mTarget = device;
                                         pickFirmwareFile();
                                     }

                                     @Override
                                     public void onBluetoothDisabled() {
                                         T.show(mContext, R.string.enable_bluetooth);
                                     }

                                     @Override
                                     public void onScanStart(boolean start) {
                                         if (!start) {
                                             if (!isDeviceFound) {
                                                 showProgress("");
                                             }
                                         }
                                     }
                                 });
    }

    @Override
    protected void initView() {
        tv = (TextView) findViewById(R.id.tv);
    }

    @Override
    protected void initComplete(Bundle bundle) {
        startScan();
    }

    public void startScan() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            T.show(mContext, R.string.enable_bluetooth);
            return;
        }

        showProgress(R.string.loading);
        isDeviceFound = false;
        //设备进入OTA模式之后，不能直接启动OTA服务，要开启扫描，扫描到目标设备之后再开启，
        //不然很多设备会出现133连接错误
        mScanner.startScan(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
    }

    @Override
    protected void onDestroy() {
        mScanner.exit();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_FILE) {
                Uri uri = data.getData();
                startOta(uri);
            }
        }
    }

    private void pickFirmwareFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
        }
    }
//
//    private String getPath(Uri uri) {
//        String path = "";
//        if (uri != null) {
//            if (TextUtils.equals("file", uri.getScheme())) {
//                path = uri.getPath();
//            } else {
//                String[] projection = {"_data"};
//                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//                if (cursor != null) {
//                    if (cursor.moveToNext()) {
//                        path = cursor.getString(0);
//                    }
//                    cursor.close();
//                }
//            }
//        }
//        L.d("getPath -> path=" + path);
//        return path;
//    }

    private void startOta(Uri uri) {
        final DfuServiceInitiator starter = new DfuServiceInitiator(mTarget.device.getAddress())
                .setDeviceName(mTarget.device.getName())
                .setKeepBond(false);
//        starter.setDisableNotification(true);
        starter.setForeground(false);
        starter.setZip(uri);
//        if (TextUtils.equals(deviceName, "SMA-Q2")) {
//            starter.setZip(R.raw.smaq2_v132);
//        } else if (TextUtils.equals(deviceName, "SM07")) {
//            starter.setZip(R.raw.sm07_pah8002_v200);
//        } else {
//            starter.setZip(R.raw.sm07_pah8002_v200);
//        }
        /*DfuServiceController controller = */
        starter.start(mContext, DfuService.class);
    }
}