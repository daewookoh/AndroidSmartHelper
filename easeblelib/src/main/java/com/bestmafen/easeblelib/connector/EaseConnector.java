package com.bestmafen.easeblelib.connector;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.bestmafen.easeblelib.entity.EaseDevice;
import com.bestmafen.easeblelib.scanner.EaseScanCallback;
import com.bestmafen.easeblelib.scanner.EaseScanner;
import com.bestmafen.easeblelib.scanner.ScanOption;
import com.bestmafen.easeblelib.scanner.ScannerFactory;
import com.bestmafen.easeblelib.util.L;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * This class wraps a {@link BluetoothGatt},also we use a instance of this to connect to a {@link BluetoothDevice} or an address
 * represented a remote device and process reconnection.We can communication with a Bluetooth device since {@link BluetoothGatt}
 * gets connected.
 */
public class EaseConnector {
    private static BluetoothAdapter sBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * Reconnection period,see {@link EaseConnector#connect(boolean)}.
     */
    private long mReconnectPeriod = 18 * 1000;

    private boolean mDirectly = true;

    /**
     * Reconnection runnable.
     */
    private Runnable mConnectRunnable = new Runnable() {

        @Override
        public void run() {
//            boolean isNeed = isNeedConnect();
//            if (!isNeed) {
//                connect(false);
//                return;
//            }
//
//            closeConnect(false);
//            if (!TextUtils.isEmpty(mAddress)) {
//                L.v("EaseConnector run -------> mAddress = " + mAddress);
//                mScanner.startScan(true);
//            } else if (mBluetoothDevice != null) {
//                L.v("EaseConnector run -------> mBluetoothDevice = " + mBluetoothDevice.getAddress());
//                mGatt = mBluetoothDevice.connectGatt(mContext, false, mGattCallback);
//            }
//            mHandler.postDelayed(this, mReconnectPeriod);
            closeConnect(false);
            if (!isNeedConnect()) {
                isConnecting = false;
                return;
            }

            if (!TextUtils.isEmpty(mAddress)) {
                L.v("EaseConnector run ->mDirectly=" + mDirectly + ", mAddress=" + mAddress);
                if (mDirectly) {
                    mGatt = sBluetoothAdapter.getRemoteDevice(mAddress).connectGatt(mContext, false, mGattCallback);
                } else {
                    mScanner.startScan(true);
                }
                mDirectly = !mDirectly;
            } else if (mBluetoothDevice != null) {
                L.v("EaseConnector run -> mEaseDevice = " + mBluetoothDevice.toString());
                mGatt = mBluetoothDevice.connectGatt(mContext, false, mGattCallback);
            }
            mHandler.postDelayed(this, mReconnectPeriod);
        }
    };

    /**
     * GATT.
     */
    public BluetoothGatt mGatt;

    /**
     * The target {@link BluetoothDevice} to be connected.
     */
    public BluetoothDevice mBluetoothDevice;

    /**
     * The target address of a remote device to be connected.
     */
    public String mAddress;

    /**
     * GATT callback.
     */
    private BluetoothGattCallback mGattCallback;

    private EaseScanner mScanner;
    private ScanOption mScanOption;

    private Context mContext;

    /**
     * Whether the connection is started or not.It will be true after you call {@link #connect(boolean)} with true,and be false
     * after you call {@link #connect(boolean)} with false.
     */
    private boolean isConnecting;

    public EaseConnector(Context context) {
        mContext = context;
        mScanner = ScannerFactory.createScanner().setScanOption(mScanOption = new ScanOption().scanPeriod(19000))
                .setEaseScanCallback(new EaseScanCallback() {

                    @Override
                    public void onDeviceFound(EaseDevice device) {
                        L.v("EaseConnector -> onDeviceFound " + device.toString());
                        mScanner.startScan(false);
                        mGatt = device.device.connectGatt(mContext, false, mGattCallback);
                    }

                    @Override
                    public void onBluetoothDisabled() {

                    }

                    @Override
                    public void onScanStart(boolean start) {

                    }
                });
    }

    /**
     * Set an address of a remote device to be connected,then the target {@link BluetoothDevice} is invalid.
     *
     * @param address an address of a remote device
     * @return This EaseConnector object.
     */
    public EaseConnector setAddress(String address) {
        L.v("EaseConnector setTarget -> address = " + address);
        mAddress = address;
        mScanOption.specifyAddress(address, true);
        mBluetoothDevice = null;
        return this;
    }

    /**
     * Set a {@link BluetoothDevice} to be connected,then the target address is invalid.
     *
     * @param device {@link BluetoothDevice}
     * @return This EaseConnector object.
     */
    public EaseConnector setDevice(BluetoothDevice device) {
        L.v("EaseConnector setTarget -> device = " + (device == null ? "null" : device.getAddress()));
        mBluetoothDevice = device;
        mAddress = "";
        return this;
    }

    /**
     * Set a GATT callback to receive asynchronous callbacks.
     *
     * @param callback {@link BluetoothGattCallback}
     * @return This EaseConnector object.
     */
    public EaseConnector setGattCallback(BluetoothGattCallback callback) {
        mGattCallback = callback;
        return this;
    }

    /**
     * Set reconnection period.
     *
     * @param period period
     * @return This EaseConnector object.
     */
    public EaseConnector setReconnectPeriod(long period) {
        mReconnectPeriod = period;
        return this;
    }

    /**
     * Start connecting or stop connecting.We will connect the target periodic since you call this method with true,and you need to
     * call this method with false to cancel the connection when the target gets connected.
     *
     * @param connect start connecting if true,stop connecting if false
     */
    public synchronized void connect(boolean connect) {
        if (isConnecting == connect) return;
        try {
            if (connect) {
                if (!isNeedConnect()) return;

                mHandler.post(mConnectRunnable);
            } else {
                mScanner.startScan(false);
                mHandler.removeCallbacksAndMessages(null);
            }
            L.d("EaseConnector connect -> " + connect);
            isConnecting = connect;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Close the connection.
     *
     * @param stopReconnect will continue to reconnect if true,else not
     */
    public synchronized void closeConnect(boolean stopReconnect) {
        L.i("EaseConnector closeConnect -> stopReconnect : " + stopReconnect);
        try {
            if (mGatt != null) {
                mGatt.disconnect();
                refreshDeviceCache();
                mGatt.close();
                mGatt = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (stopReconnect) connect(false);
    }

    public boolean refreshDeviceCache() {
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null) {
                final boolean success = (Boolean) refresh.invoke(mGatt);
                L.i("EaseConnector -> refreshDeviceCache : " + success);
                return success;
            }
        } catch (Exception e) {
            L.i("EaseConnector -> refreshDeviceCache : error,", e.getMessage());
        }
        return false;
    }

    /**
     * Whether need to connect the target.
     *
     * @return true if need,else false.
     */
    private boolean isNeedConnect() {
        if (!(ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED))
        {
            return false;
        }
        boolean isNeedConnect = sBluetoothAdapter.isEnabled() && (mBluetoothDevice != null || !TextUtils.isEmpty(mAddress));
        L.v("EaseConnector isNeedConnect -> " + isNeedConnect + ", isEnabled : " + sBluetoothAdapter.isEnabled() + ", device : "
                + (mBluetoothDevice != null) + ", address = " + !TextUtils.isEmpty(mAddress));
        return isNeedConnect;
    }

    /**
     * Returns a characteristic with the given UUIDs.
     *
     * @param serviceUUID        UUID of a {@link BluetoothGattService}
     * @param characteristicUUid UUID of a {@link BluetoothGattCharacteristic} you want to get
     * @return {@link BluetoothGattCharacteristic} object or null if no characteristic with the given UUID was found.
     */
    public BluetoothGattCharacteristic getGattCharacteristic(String serviceUUID, String characteristicUUid) {
        if (mGatt == null) return null;

        BluetoothGattService gattService = mGatt.getService(UUID.fromString(serviceUUID));
        if (gattService == null) return null;

        return gattService.getCharacteristic(UUID.fromString(characteristicUUid));
    }

    /**
     * Exit and release resource.
     */
    public void exit() {
        closeConnect(true);
        mScanner.exit();
    }
}
