package com.and.smarthelper.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bestmafen.easeblelib.entity.EaseDevice;
import com.bestmafen.easeblelib.scanner.EaseScanCallback;
import com.bestmafen.easeblelib.scanner.EaseScanner;
import com.bestmafen.easeblelib.scanner.ScanOption;
import com.bestmafen.easeblelib.scanner.ScannerFactory;
import com.bestmafen.easeblelib.util.L;
import com.bestmafen.smablelib.component.SimpleSmaCallback;
import com.bestmafen.smablelib.component.SmaCallback;
import com.bestmafen.smablelib.component.SmaManager;
import com.and.smarthelper.R;
import com.and.smarthelper.util.T;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BindActivity extends BaseActivity implements View.OnClickListener {
    private static final int  REQUEST_LOCATION_PERMISSION = 0x01;
    private static final long BIND_PERIOD                 = 40000;

    private ListView      lv;
    private DeviceAdapter mDeviceAdapter = new DeviceAdapter();
    private TextView      tv_scan;
    private AlertDialog   mAlertDialog;
    private AlertDialog   mUnpairDialog;

    private SmaManager  mSmaManager;
    private SmaCallback mSmaCallback;
    private EaseScanner mScanner;

    private volatile boolean  isFailed;
    private          Handler  mHandler            = new Handler();
    private          Runnable mCancelBindRunnable = new Runnable() {

        @Override
        public void run() {
            isFailed = true;
            showProgress(null);
            mSmaManager.close(true);
            mSmaManager.unbind();

            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
        }
    };

    @Override
    public void onBackPressed() {
        Log.d("TTT","back");
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        startActivity(new Intent(mContext, LauncherActivity.class));
        finish();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_bind;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mSmaManager = SmaManager.getInstance().addSmaCallback(mSmaCallback = new SimpleSmaCallback() {

            @Override
            public void onDeviceConnected(final BluetoothDevice device) {
                if (!isFailed) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
//                            postBindFailDelay();

//                            TextView tv = new TextView(mContext);
//                            tv.setText(R.string.bind_tip);

                            if (mAlertDialog == null) {
                                mAlertDialog = new AlertDialog.Builder(mContext).setMessage(getString(R.string.bind_tip)).create();

                                mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });

                                //mAlertDialog.setCancelable(true);
                            }
                            mAlertDialog.show();
                        }
                    });
                }
            }

            @Override
            public void onLogin(final boolean ok) {
                if (ok) {
                    T.show(mContext, "연결 성공");
                }
                else {
                    T.show(mContext, "연결 취소");
                }

                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                }
                startActivity(new Intent(mContext, LauncherActivity.class));
                finish();
            }

            @Override
            public void onKeyDown(byte key) {
                Log.d("TTT","onLogin"+String.valueOf(key));

            }


        });

        mScanner = ScannerFactory.createScanner().setScanOption(new ScanOption().scanPeriod(5000).minRssi(-80))
                                 .setEaseScanCallback(new EaseScanCallback() {

                                     @Override
                                     public void onDeviceFound(EaseDevice device) {
                                         L.v("BindActivity -> onDeviceFound " + device.toString());
                                         mDeviceAdapter.add(device);
                                     }

                                     @Override
                                     public void onBluetoothDisabled() {
                                         T.show(mContext, R.string.enable_bluetooth);
                                     }

                                     @Override
                                     public void onScanStart(boolean start) {
                                         if (start) {
                                             mDeviceAdapter.clear();
                                             tv_scan.setText(R.string.scanning);
                                         } else {
                                             tv_scan.setText(R.string.scan);
                                         }
                                     }
                                 });
    }

    @Override
    protected void initView() {
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(mDeviceAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isFailed = false;
                mScanner.startScan(false);

                EaseDevice device = mDeviceAdapter.get(position);
                int state = device.device.getBondState();
                if (state == BluetoothDevice.BOND_BONDED) {
                    if (mUnpairDialog == null) {
                        mUnpairDialog = new AlertDialog.Builder(mContext)
                                .setMessage(R.string.unpair_tip)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                                        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                                            startActivity(intent);
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .create();
                    }
                    mUnpairDialog.show();
                } else {
                    showProgress(R.string.loading);
                    new BindThread(mDeviceAdapter.get(position).device).start();
                }
            }
        });

        tv_scan = (TextView) findViewById(R.id.tv_scan);
        tv_scan.setOnClickListener(this);
    }

    @Override
    protected void initComplete(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(BindActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mScanner.startScan(true);
        } else {
            ActivityCompat.requestPermissions(BindActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    protected void onDestroy() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        mHandler.removeCallbacksAndMessages(null);
        mSmaManager.removeSmaCallback(mSmaCallback);
        if (!mSmaManager.isBond()) {
            mSmaManager.unbind();
        }
        mScanner.exit();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mScanner.startScan(true);
            } else {
                finish();
            }
        }
    }

    private void postBindFailDelay() {
        mHandler.removeCallbacks(mCancelBindRunnable);
        mHandler.postDelayed(mCancelBindRunnable, BIND_PERIOD);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_scan:
                mScanner.startScan(!mScanner.isScanning);
                break;
        }
    }

    private class DeviceAdapter extends BaseAdapter {
        private List<EaseDevice> mDevices = new ArrayList<>();

        @Override
        public int getCount() {
            return mDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder vh;
            EaseDevice device = mDevices.get(position);
            if (v == null) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
                vh = new ViewHolder(v);
            } else {
                vh = (ViewHolder) v.getTag();
            }

            vh.tv_name.setText(device.device.getName());
            vh.tv_address.setText(device.device.getAddress());
            vh.iv_rssi.setImageLevel(-device.rssi);

            return v;
        }

        public void add(EaseDevice device) {
            if(device.device.getName().startsWith("POWER") || device.device.getName().startsWith("M3D")) {
                if (mDevices.contains(device)) {
                    mDevices.set(mDevices.indexOf(device), device);
                } else {
                    mDevices.add(device);
                }
                Collections.sort(mDevices);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            mDevices.clear();
            notifyDataSetChanged();
        }

        public EaseDevice get(int position) {
            return (EaseDevice) getItem(position);
        }
    }

    class ViewHolder {
        TextView tv_name, tv_address;
        ImageView iv_rssi;

        public ViewHolder(View v) {
            this.tv_name = (TextView) v.findViewById(R.id.tv_name);
            this.tv_address = (TextView) v.findViewById(R.id.tv_address);
            this.iv_rssi = (ImageView) v.findViewById(R.id.iv_rssi);
            v.setTag(this);
        }
    }

    /**
     * 绑定前调用startDiscovery()，可以让配对提示弹在前台，而不是通知栏
     */
    public class BindThread extends Thread {
        private BluetoothDevice mDevice;

        public BindThread(BluetoothDevice device) {
            this.mDevice = device;
        }

        @Override
        public void run() {
            super.run();
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
            //Give it some time before cancelling the discovery
            SystemClock.sleep(1000);
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            mSmaManager.bindWithDevice(mDevice);
            postBindFailDelay();
        }
    }
}