package com.bestmafen.smablelib.component;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.bestmafen.easeblelib.connector.EaseConnector;
import com.bestmafen.easeblelib.util.EaseUtils;
import com.bestmafen.easeblelib.util.L;
import com.bestmafen.smablelib.entity.ISmaCmd;
import com.bestmafen.smablelib.entity.SmaAlarm;
import com.bestmafen.smablelib.entity.SmaBloodPressure;
import com.bestmafen.smablelib.entity.SmaCycling;
import com.bestmafen.smablelib.entity.SmaExercise;
import com.bestmafen.smablelib.entity.SmaHeartRate;
import com.bestmafen.smablelib.entity.SmaSedentarinessSettings;
import com.bestmafen.smablelib.entity.SmaSleep;
import com.bestmafen.smablelib.entity.SmaSport;
import com.bestmafen.smablelib.entity.SmaStream;
import com.bestmafen.smablelib.entity.SmaStreamInfo;
import com.bestmafen.smablelib.entity.SmaTime;
import com.bestmafen.smablelib.entity.SmaTimezone;
import com.bestmafen.smablelib.entity.SmaTracker;
import com.bestmafen.smablelib.entity.SmaWeatherForecast;
import com.bestmafen.smablelib.entity.SmaWeatherRealTime;
import com.bestmafen.smablelib.server.MyBleServer;
import com.bestmafen.smablelib.server.MyNotificationService;
import com.bestmafen.smablelib.util.SmaBleHelper;
import com.bestmafen.smablelib.util.SmaBleUtils;
import com.bestmafen.smablelib.util.SmaConsts;
import com.bestmafen.smablelib.util.UpdateMUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

/**
 * This is a core class which we used to manager a Bluetooth device.
 */
public class SmaManager {
    /**
     * UUID of the main {@link BluetoothGattService}.
     */
    public static final String UUID_SERVICE_MAIN = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";

    /**
     * UUID of the {@link BluetoothGattCharacteristic} for writing.
     */
    public static final String UUID_CHARACTER_WRITE = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    /**
     * UUID of the {@link BluetoothGattCharacteristic} for reading.
     */
    public static final String UUID_CHARACTER_READ = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";

    /**
     * UUID of the main {@link BluetoothGattService} for round watch.
     */
    public static final String UUID_SERVICE_MAIN_ROUND = "0000ff60-0000-1000-8000-00805f9b34fb";

    /**
     * UUID of the {@link BluetoothGattCharacteristic} for writing to round watch.
     */
    public static final String UUID_CHARACTER_WRITE_ROUND = "0000ff61-0000-1000-8000-00805f9b34fb";

    /**
     * UUID of the {@link BluetoothGattCharacteristic} for reading to round watch.
     */
    public static final String UUID_CHARACTER_READ_ROUND = "0000ff62-0000-1000-8000-00805f9b34fb";

    /**
     * UUID of the {@link BluetoothGattService} for reading firmware flag.
     */
    public static final String UUID_SERVICE_FIRM_FLAG = "0000180a-0000-1000-8000-00805f9b34fb";

    /**
     * UUID of the {@link BluetoothGattCharacteristic} for reading firmware flag.
     */
    public static final String UUID_CHARACTER_FIRM_FLAG = "00002a29-0000-1000-8000-00805f9b34fb";

    public static final String UUID_CHARACTER_CLASSIC_ADDRESS = "00002a23-0000-1000-8000-00805f9b34fb";

    /**
     * UUID of the {@link BluetoothGattCharacteristic} for reading firmware of TECHNOS_M1
     */
    public static final String UUID_CHARACTER_M_VERSION = "00002a26-0000-1000-8000-00805f9b34fb";

    /**
     * 以下两条用于读取M1设备的信息
     */
    public static final String UUID_SERVICE_FIRM_FLAG_M   = "C6A22905-F821-18BF-9704-0266F20E80FD";
    public static final String UUID_CHARACTER_FIRM_FLAG_M = "C6A22916-F821-18BF-9704-0266F20E80FD";

    /**
     * The key to save a remote Bluetooth device's name.
     */
    public static final String SP_DEVICE_NAME = "sp_device_name";

    /**
     * The key to save a remote Bluetooth device's address.
     */
    public static final String SP_DEVICE_ADDRESS = "sp_device_address";

    /**
     * The key to save a remote classic Bluetooth device's address.
     */
    public static final String SP_CLASSIC_ADDRESS = "sp_classic_address";

    /**
     * The key to save a remote Bluetooth device's type.
     */
    public static final String SP_DEVICE_TYPE = "sp_device_type";

    /**
     * The key to save a remote Bluetooth device's firmware version.
     */
    public static final String SP_FIRMWARE = "sp_firmware";

    /**
     * The key to save a remote Bluetooth device's firmware flag.
     */
    public static final String SP_FIRMWARE_FLAG = "sp_firmware_flag";

    public static final String SP_PHONE_FLAG = "sp_phone_flag";

    /**
     * See {@link SmaManager#write(byte, byte, byte[])}
     */
    public static final class Cmd {
        public static final byte NONE    = 0x00;
        public static final byte UPDATE  = 0x01;
        public static final byte SET     = 0x02;
        public static final byte CONNECT = 0x03;
        public static final byte NOTICE  = 0x04;
        public static final byte DATA    = 0x05;
        public static final byte CONTROL = 0x07;
    }

    /**
     * See {@link SmaManager#write(byte, byte, byte[])}
     */
    public static final class Key {
        public static final byte NONE     = 0x00;
        //UPDATE
        public static final byte OTA      = 0x01;
        static final        byte OTA_BACK = 0x02;

        public static final byte GET_WATCH_FACES      = 0x31;
        public static final byte GET_WATCH_FACES_BACK = 0x32;

        private static final byte INTO_XMODEM                      = 0x21;
        public static final  byte INTO_XMODEM_BACK                 = 0x22;
        public static final  byte XMODEM_WAITING                   = 0x43;
        public static final  byte XMODEM_ACTION_REPLACE_WATCHFACE1 = '1';//Replace watchface1 0x31
        public static final  byte XMODEM_ACTION_REPLACE_WATCHFACE2 = '2';//Replace watchface2 0x32
        public static final  byte XMODEM_ACTION_REPLACE_WATCHFACE3 = '3';//Replace watchface3 0x33
        public static final  byte XMODEM_ACTION_UPDATE_FONT        = 'o';//Send font file     0x6F
        public static final  byte XMODEM_ACTION_SEND_AGPS          = 'g';//Send agps file     0x67
        public static final  byte XMODEM_ACK                       = 0x06;
        public static final  byte XMODEM_COMPLETED                 = 0x04;
        public static final  byte XMODEM_EXIT                      = 'q';//                   0x71

        //SET
        public static final byte SYNC_TIME_2_DEVICE = 0x01;
        public static final byte SYNC_TIME          = 0x46;
        public static final byte SET_USER_INFO      = 0x10;
        public static final byte READ_ALARM         = 0x2D;
        public static final byte READ_ALARM_BACK    = 0x2E;
        public static final byte SET_GOAL           = 0x05;
        public static final byte READ_BATTERY       = 0x08;
        static final        byte READ_BATTERY_BACK  = 0x09;
        public static final byte READ_VERSION       = 0x0A;
        static final        byte READ_VERSION_BACK  = 0x0B;

        public static final byte ENABLE_ANTI_LOST        = 0x20;
        public static final byte ENABLE_NO_DISTURB       = 0x2B;
        public static final byte ENABLE_CALL             = 0x26;
        public static final byte ENABLE_NOTIFICATION     = 0x27;
        public static final byte ENABLE_RAISE_ON         = 0x35;
        public static final byte ENABLE_DISPLAY_VERTICAL = 0x36;
        public static final byte ENABLE_DETECT_SLEEP     = 0x39;
        public static final byte INTO_TAKE_PHOTO         = 0x42;

        public static final byte SET_SEDENTARINESS = 0x2A;
        public static final byte SET_ALARMS        = 0x2C;
        public static final byte SET_HEART_RATE    = 0x44;
        public static final byte SET_UNIT          = 0x45;
        public static final byte SET_WEATHER_UNIT  = 0x4C;
        static final        byte SET_VIBRATION     = 0x2F;
        public static final byte SET_BACK_LIGHT    = 0x29;
        public static final byte SET_LANGUAGE      = 0x34;
        public static final byte SET_24HOUR        = 0x3B;
        public static final byte SET_NAME_GROUP    = 0x11;//IMED
        public static final byte SET_LIGHT_TIME    = 0x6B;

        public static final byte ALARMS_CHANGE             = 0x60;
        public static final byte GOAL_CHANGE               = 0x61;
        public static final byte READ_GOAL                 = 0x62;
        public static final byte READ_GOAL_BACK            = 0x63;
        public static final byte SEDENTARINESS_CHANGE      = 0x64;
        public static final byte READ_SEDENTARINESS        = 0x65;
        public static final byte READ_SEDENTARINESS_BACK   = 0x66;
        public static final byte CAMERA_PRESSED            = 0x67;
        public static final byte SET_TIMEZONE              = 0x68;
        public static final byte SET_COORDINATE            = 0x69;
        public static final byte SET_DEVICE_COMMON_REQUEST = 0x6A;
        public static final byte SET_SYSTEM                = 0x23;
        public static final byte REQUEST_PAIR              = 0x47;
        public static final byte SET_ANTI_LOST_TEL         = 0x48;
        public static final byte SET_WEATHER               = 0x49;
        public static final byte SET_CYCLING_EXTRA         = 0x4A;
        public static final byte SET_FORECAST              = 0x4B;
        public static final byte SETTINGS_COMPLETED        = 0x4A;

        //CONNECT
        public static final byte BIND       = 0x01;
        static final        byte BIND_BACK  = 0x02;
        static final        byte LOGIN      = 0x03;
        static final        byte LOGIN_BACK = 0x04;
        public static final byte UNBIND     = 0x05;

        // NOTICE
        public static final byte CALL_INCOMING = 0x01;
        public static final byte CALL_OFF_HOOK = 0x02;
        public static final byte CALL_IDLE     = 0x03;
        public static final byte MESSAGE       = 0x51;
        public static final byte MESSAGE_v2    = 0x52;
        public static final byte FIND_PHONE    = 0x60;
        public static final byte FIND_DEVICE   = 0x61;
        public static final byte ARRIVE_AT     = 0x53;

        // DATA
        public static final byte EXERCISE                    = 0x34;
        public static final byte EXERCISE2                   = 0x35;
        public static final byte EXERCISE2_BACK              = 0x36;
        public static final byte SPORT                       = 0x41;
        static final        byte SPORT_BACK                  = 0x42;
        public static final byte RATE                        = 0x43;
        static final        byte RATE_BACK                   = 0x44;
        public static final byte SLEEP                       = 0x45;
        static final        byte SLEEP_BACK                  = 0x46;
        public static final byte TRACKER                     = 0x47;
        static final        byte TRACKER_BACK                = 0x48;
        public static final byte BLOOD_PRESSURE              = 0x49;
        static final        byte BLOOD_PRESSURE_BACK         = 0x4A;
        public static final byte CYCLING                     = 0x4C;
        public static final byte CYCLING_BACK                = 0x4D;
        public static final byte SPORT2                      = 0x54;
        public static final byte SPORT2_BACK                 = 0x55;
        public static final byte REPLY_DEVICE_COMMON_REQUEST = 0x60;
    }

    public static final byte VIBRATION_TYPE_ALARM = 0x5;

    /**
     * Amount of millisecond between 1970-01-01~2000-01-01
     */
    private static final Long MS = 946684800000L;

    public           EaseConnector   mEaseConnector;
    private          Context         mContext;
    private          BluetoothSocket mBluetoothSocket;
    /**
     * Whether the device is logged in.
     */
    private volatile boolean         isLoggedIn;

    private static BluetoothAdapter sBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static Handler          sHandler          = new Handler(Looper.getMainLooper());

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            for (SmaCallback connectorCallback : mSmaCallbacks) {
                connectorCallback.onReadDataFinished(false);
            }
        }
    };

    /**
     * The data you want read
     */
    private byte[] mDataKes;

    /**
     * The UUID of main {@link BluetoothGattService},{@link SmaManager#UUID_SERVICE_MAIN} or
     * {@link SmaManager#UUID_SERVICE_MAIN_ROUND}
     */
    private String mUUIDMainService;

    /**
     * UUID of the {@link BluetoothGattCharacteristic} for writing,{@link SmaManager#UUID_CHARACTER_WRITE} or
     * {@link SmaManager#UUID_CHARACTER_WRITE_ROUND}
     */
    private String mUUIDWrite;

    /**
     * UUID of the {@link BluetoothGattCharacteristic} for reading,{@link SmaManager#UUID_CHARACTER_READ} or
     * {@link SmaManager#UUID_CHARACTER_READ_ROUND}
     */
    private String mUUIDRead;

    private SmaMessenger      mSmaMessenger;
    private List<SmaCallback> mSmaCallbacks = new ArrayList<>();

    /**
     * has entered into x-mode transferring mode
     */
    private boolean isTransferring = false;
    private int     mTransferTotal;
    private int     mTransferCompleted;

    private SharedPreferences        mPreferences;
    private SharedPreferences.Editor mEditor;
    private DateFormat               mDateFormat;
    private boolean                  isExit    = false;
    /**
     * Receiver to receive broadcast with action {@link BluetoothAdapter#ACTION_STATE_CHANGED},so that you can start
     * connecting
     * when Bluetooth adapter get enabled when a remote device has been bond.
     */
    private BroadcastReceiver        mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                L.d("SmaManager -> BluetoothAdapter.ACTION_STATE_CHANGED , state = " + state);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        if (isBond()) mEaseConnector.setAddress(getSavedAddress()).connect(true);
                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:
//                        isLoggedIn = false;
//                        isOta = false;
//                        mEaseConnector.connect(false);
                        break;
                }
            }
        }
    };

    public static synchronized SmaManager getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static SmaManager sInstance = new SmaManager();
    }

    private SmaManager() {
        /**
         * Keep the timezone of {@link SmaManager#mDateFormat} as same as devices's.
         */
        mDateFormat = new SimpleDateFormat(SmaConsts.DATE_FORMAT_yyyy_MM_dd_HH_mm_ss, Locale.getDefault());
        mDateFormat.setTimeZone(SmaBleUtils.getDefaultTimeZone());
    }

    public SmaManager init(Context context) {
        return init(context, false);
    }

    public SmaManager init(Context context, boolean enableMusicControl) {
        isExit = false;
        mContext = context.getApplicationContext();
        mContext.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        mSmaMessenger = new SmaMessenger();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mPreferences.edit();

        mEaseConnector = new EaseConnector(mContext).setGattCallback(new BluetoothGattCallback() {

            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                L.v("SmaManager onConnectionStateChange -> status = " + status + ",newState = " + newState);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        L.d("SmaManager -> STATE_CONNECTED1");
                        MyBleServer.getInstance().init(mContext/*, true*/);
                        gatt.discoverServices();
                        clearAllTask();
                    }
                }

                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    L.d("SmaManager -> STATE_DISCONNECTED");
                    mEaseConnector.closeConnect(!sBluetoothAdapter.isEnabled());
                    mEaseConnector.connect(sBluetoothAdapter.isEnabled() && !isExit);

                    if (isLoggedIn) {
                        for (SmaCallback bc : mSmaCallbacks) {
                            bc.onLogin(false);
                        }
                    }
                    isLoggedIn = false;
                    checkTransfer();
                    clearAllTask();
                }
            }

            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                L.d("SmaManager -> onServicesDiscovered");
                selectUUID(gatt);
                setNotify(mUUIDMainService, mUUIDRead, true);
                requestMtu(512);
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                L.d("SmaManager -> onDescriptorWrite");
                mSmaMessenger.releasePackageSemaphore();
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                L.d("SmaManager onCharacteristicRead " + characteristic.getUuid().toString());
                L.d("SmaManager -> onCharacteristicRead " + EaseUtils.byteArray2HexString(characteristic.getValue()));
                mSmaMessenger.releasePackageSemaphore();
                byte[] value = characteristic.getValue();
                if (value == null || value.length < 1) return;

                try {
                    if (characteristic.getUuid().equals(UUID.fromString(UUID_CHARACTER_FIRM_FLAG))
                            || characteristic.getUuid().equals(UUID.fromString(UUID_CHARACTER_FIRM_FLAG_M))) {//读到厂商号
                        String flag = new String(value, "UTF-8");
                        L.d("FIRMWARE_FLAG -> " + flag);
                        for (SmaCallback callback : mSmaCallbacks) {
                            callback.onReadFlag(flag);
                        }
                        String[] items = flag.split(";");
                        if (items.length > 6) {
                            String version = items[6].substring(4);
                            L.d("READ_VERSION_BACK -> " + version);
                            saveFirmwareVersion(version);
                            for (SmaCallback bc : mSmaCallbacks) {
                                bc.onReadVersion(version, version);
                            }
                        }
                    } else if (characteristic.getUuid().equals(UUID.fromString(UUID_CHARACTER_CLASSIC_ADDRESS))) {
                        if (value.length != 6) return;

                        //读到圆表的经典蓝牙地址，通过地址直接连到经典蓝牙
                        final String address = String.format("%02X:%02X:%02X:%02X:%02X:%02X", value[0], value[1], value[2],
                                value[3], value[4], value[5]);
                        saveClassicAddress(address);
                        L.d("CLASSIC_ADDRESS -> " + address);
                    } else if (characteristic.getUuid().equals(UUID.fromString(UUID_CHARACTER_M_VERSION))) {
                        String version = new String(value, "UTF-8");
                        L.d("READ_VERSION_BACK -> " + version);
                        saveFirmwareVersion(version);
                        for (SmaCallback bc : mSmaCallbacks) {
                            bc.onReadVersion(version, version);
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                byte[] data = characteristic.getValue();
                L.d("SmaManager -> onCharacteristicWrite " + EaseUtils.byteArray2HexString(data));
                mSmaMessenger.releasePackageSemaphore();
                if (characteristic.getUuid().equals(UUID.fromString(mUUIDWrite))) {
                    if (data.length == 8 && ((data[0] & 0xff) == 0xAB)) {
                        if (((data[1] & 0xff) == 0x10 || (data[1] & 0xff) == 0x30)) {
                            mSmaMessenger.releaseReturnSemaphore();
                        }
                    } else if (data.length >= 15) {//回调传输文件的进度
                        if ((data[0] & 0Xff) == 0xAB && (data[8] & 0xff) == Cmd.DATA
                                && (data[10] & 0xff) == Key.REPLY_DEVICE_COMMON_REQUEST) {
                            int index = ((data[13] & 0xff) << 8) | (data[14] & 0xff);
                            if (index == 0 && data.length >= 23) {
                                mTransferCompleted = 0;
                                mTransferTotal = ((data[21] & 0xff) << 8) | (data[22] & 0xff);
                            } else {
                                if (mTransferTotal > 0) {
                                    mTransferCompleted++;
                                    checkTransfer();
                                }
                            }
                        }
                    }
                } else if (characteristic.getUuid().equals(UUID.fromString(UPDATE_M_CH_DATA))) {
                    mTransferCompleted++;
                    checkTransfer();
                } else if (characteristic.getUuid().equals(UUID.fromString(UPDATE_M_CH_MD5))) {

                }
            }

            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                if (characteristic.getUuid().equals(UUID.fromString(mUUIDRead))) {
                    byte[] data = characteristic.getValue();
                    if (data == null || data.length < 1) return;

                    L.d("SmaManager -> onCharacteristicChanged " + EaseUtils.byteArray2HexString(data));
                    if (isTransferring) {
                        handleTransferByXMode(data);
                    } else {
                        receiveData(data);
                    }
                }
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                L.d("SmaManager -> onMtuChanged mtu=" + mtu);
                mSmaMessenger.releasePackageSemaphore();
//                if (mtu > 183) {
//                    mtu = 183;
//                }
                mSmaMessenger.mMtu = mtu - 3;
                if (isBond()) {
                    sendLoginCmd();
                } else {
                    sendBindCmd();
                }
                for (SmaCallback bc : mSmaCallbacks) {
                    bc.onDeviceConnected(gatt.getDevice());
                }
            }
        });

        if (enableMusicControl) {
            //重置SmaNotificationService，以便能接收到通知栏消息
            PackageManager pm = mContext.getPackageManager();
            pm.setComponentEnabledSetting(new ComponentName(mContext, MyNotificationService.class), PackageManager
                    .COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(new ComponentName(mContext, MyNotificationService.class), PackageManager
                    .COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            MyBleServer.getInstance().init(mContext/*, true*/);
        }

        return this;
    }

    /**
     * See {@link EaseConnector#connect(boolean)}
     *
     * @param connect
     */
    public void connect(boolean connect) {
        L.d("SmaManager -> connect " + connect);
        if (connect) {
            if (!isBond() || !sBluetoothAdapter.isEnabled() || isLoggedIn) return;

            mEaseConnector.setAddress(getSavedAddress()).connect(true);
        } else {
            mEaseConnector.connect(false);
        }
    }

    public void connectClassic() {
        final String address = getClassicAddress();
        if (!BluetoothAdapter.checkBluetoothAddress(address)) return;

        L.v("SmaManager connectClassic -> " + address);
        new Thread(new Runnable() {

            @Override
            public void run() {
                BluetoothDevice device = sBluetoothAdapter.getRemoteDevice(address);
                try {
                    if (mBluetoothSocket != null) {
                        mBluetoothSocket.close();
                        mBluetoothSocket = null;
                    }
                    mBluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString
                            ("00001101-0000-1000-8000-00805F9B34FB"));
                    if (mBluetoothSocket != null) {
                        mBluetoothSocket.connect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (mBluetoothSocket != null) {
                        try {
                            mBluetoothSocket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } finally {
                            mBluetoothSocket = null;
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * See {@link EaseConnector#closeConnect(boolean)}
     *
     * @param stopReconnect
     */
    public void close(boolean stopReconnect) {
        isLoggedIn = false;
        mEaseConnector.closeConnect(stopReconnect);
    }

    /**
     * Exit and release resource.
     */
    public void exit() {
        L.d("SmaManager -> exit");
        isExit = true;
        mContext.unregisterReceiver(mReceiver);
        mSmaMessenger.exit();
        mEaseConnector.exit();
        isLoggedIn = false;
    }

    /**
     * See {@link EaseConnector#setDevice(BluetoothDevice)}
     *
     * @param device
     */
    public void bindWithDevice(BluetoothDevice device) {
        L.d("SmaManager  bindWithDevice -> " + device.getName() + ", " + device.getAddress());
        mEaseConnector.setDevice(device);
        mEaseConnector.connect(true);
    }

    /**
     * unbind with the bond device and clear all its information saved in local.
     */
    public void unbind() {
        L.d("SmaManager -> unbind");
        clearAllTask();

        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mBluetoothSocket = null;
            }
        }
        removeDevice(getClassicAddress());
        saveClassicAddress("");

        if (isLoggedIn()) {
            write(Cmd.CONNECT, Key.UNBIND, new byte[]{0});
            for (SmaCallback bc : mSmaCallbacks) {
                bc.onLogin(false);
            }
        }

        mEaseConnector.setAddress("");
        mEaseConnector.setDevice(null);
        isLoggedIn = false;

        final String address = getSavedAddress();
        saveNameAndAddress("", "");
        saveFirmwareVersion("");
        saveFirmwareFlag("");

        sHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                if (!TextUtils.isEmpty(address)) {
                    mEaseConnector.closeConnect(true);
                        removeDevice(address);
                }
            }
        }, 1600);
    }

    /**
     * Write data to device to bind with it
     */
    private void sendBindCmd() {
        L.d("SmaManager -> sendBindCmd");
        read(SmaManager.UUID_SERVICE_FIRM_FLAG, SmaManager.UUID_CHARACTER_CLASSIC_ADDRESS);

        byte[] extra = new byte[32];
        extra[0] = (byte) -1;
        write(Cmd.CONNECT, Key.BIND, extra);
    }

    /**
     * Write data to the remote device to login it.
     */
    private void sendLoginCmd() {
        L.d("SmaManager -> sendLoginCmd");
        byte[] extra = new byte[32];
        extra[0] = (byte) -1;
        write(Cmd.CONNECT, Key.LOGIN, extra);
    }

    /**
     * Process the replacing of watch face.
     *
     * @param data the data received
     */
    private void handleTransferByXMode(byte[] data) {
        if (data.length == 9) {
            if (data[0] == 0x58 && data[1] == 0x6D && data[2] == 0x6F && data[3] == 0x64 && data[4] == 0x65
                    && data[5] == 0x6D && data[6] == 0x0D && data[7] == 0x0A && data[8] == 0x00) {
                write(SmaManager.Cmd.NONE, SmaManager.Key.NONE, mXModeAction, 1);
            }
            return;
        }

        if (data.length == 7) {
            if (data[0] == 0x51 && data[1] == 0x75 && data[2] == 0x69 && data[3] == 0x74 && data[4] == 0x0D
                    && data[5] == 0x0A && data[6] == 0x00) {
                mSmaMessenger.releaseReceiveSemaphore();
                isTransferring = false;
            }
            return;
        }

        int key = data[0] & 0xff;
        switch (key) {
            case Key.XMODEM_WAITING:
                if (mXModeStream != null) {
                    mSmaMessenger.releaseReceiveSemaphore();
                    try {
                        List<byte[]> buffers = SmaBleUtils.getBuffer4XMode(mXModeStream);
                        if (buffers == null || buffers.isEmpty()) {
                            mXModeStream.close();
                            return;
                        }

                        mXModeStream = null;
                        requestHighSpeedMode(true);
                        mTransferTotal = buffers.size();
                        L.i("XMODEM_WAITING -> size=" + mTransferTotal);
                        mTransferCompleted = 0;
                        for (byte[] buffer : buffers) {
                            mSmaMessenger.addMessage(new SmaMessenger.SmaMessage(mEaseConnector.mGatt, mEaseConnector
                                    .getGattCharacteristic(mUUIDMainService, mUUIDWrite), buffer, SmaMessenger.MessageType.WRITE,
                                    false, false, true));
                        }
                        write(Cmd.NONE, Key.NONE, Key.XMODEM_COMPLETED, 1);
                        //必须等待xmodem退出，才能继续发送协议层面的数据，否则设备无响应，返回退出xmodem可能都没用，必须手动延迟
                        //已经在{@link SmaMessenger}中加了延迟
                        write(Cmd.NONE, Key.NONE, Key.XMODEM_EXIT, 1);
                        requestHighSpeedMode(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Key.XMODEM_ACK:
                mSmaMessenger.releaseReceiveSemaphore();
                if (mTransferTotal > 0 && mTransferCompleted < mTransferTotal) {
                    mTransferCompleted++;
                    checkTransfer();
                }
                break;
        }
    }

    private byte[] mData   = new byte[0];
    private int    mLength = 0;

    /**
     * Process the data received.
     *
     * @param back the data sent by the bond device
     */
    private void receiveData(byte[] back) {
        if (back == null || back.length < 1) return;

        if ((back[0] & 0xFF) == 0xAB && back.length == 8 && mLength == 0) {//以0xAB开头，且长度为8
            if (((back[1] & 0xff) == 0x10 || (back[1] & 0xff) == 0x30)) {//如果是ACK或NACK
                //L.d("收到ACK或NACK" + Arrays.toString(back));
                mSmaMessenger.releaseReceiveSemaphore();
            } else {//不是ACK或NACK
                // L.d("receiveData>>>>返回的不是ACK和NACK = " +Arrays.toString(back));
                mData = new byte[0];
                mLength = (back[2] & 0xff) << 8;
                mLength |= back[3] & 0xff;
                mLength += 8;
            }
        }

        if (back.length <= mLength && mData.length < mLength) {
            mData = EaseUtils.concat(mData, back);
            if (mData.length == mLength) {
                mLength = 0;
                parseData();
            }
        }
    }

    /**
     * Parse the data received.
     */
    private synchronized void parseData() {
        L.d("parseData -> " + EaseUtils.byteArray2HexString(mData));
        int size = mData.length;
        byte[] crc = new byte[size];
        System.arraycopy(mData, 0, crc, 0, size);
        crc[4] = 0;
        crc[5] = 0;
        byte[] crcBytes = SmaBleHelper.cmdCRC(crc);

        String crcRt = EaseUtils.bytesArray2HexStringWithout0x(crcBytes);
        String dataRt = EaseUtils.bytesArray2HexStringWithout0x(mData);
        if (TextUtils.equals(crcRt, dataRt)) {
            returnACK(mData);
        }

        byte cmd = (byte) (mData[8] & 0xff);
        byte key = (byte) (mData[10] & 0xff);
        switch (cmd) {
            case Cmd.UPDATE:
                if (key == Key.OTA_BACK) {
                    boolean ok = mData[13] == 0;// 0 成功,1 失败
                    L.d("OTA_BACK -> " + ok);
                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onOTA(ok);
                    }
                } else if (key == Key.INTO_XMODEM_BACK) {
                    boolean ok = mData[13] == 0;
                    L.d("INTO_XMODEM_BACK -> " + ok);
                    if (ok) {
                        isTransferring = true;
                    } else {
                        isTransferring = false;
                        for (SmaCallback bc : mSmaCallbacks) {
                            bc.onTransferBuffer(false, 0, 0);
                        }
                    }
                } else if (key == Key.GET_WATCH_FACES_BACK) {
                    int count = mData[13] & 0xff;
                    long[] ids = new long[count];
                    for (int i = 0; i < count; i++) {
                        ids[i] = (mData[14 + i * 4] & 0xFF) << 24
                                | (mData[15 + i * 4] & 0xFF) << 16
                                | (mData[16 + i * 4] & 0xFF) << 8
                                | (mData[17 + i * 4] & 0xFF);
                    }
                    L.d("GET_WATCH_FACES_BACK -> count=" + count + ", ids=" + Arrays.toString(ids));
                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onReadWatchFaces(count, ids);
                    }
                }
                break;

            case Cmd.SET:
                if (key == Key.READ_ALARM_BACK) {
                    int count = (((mData[11] & 0xff) << 8) | mData[12] & 0xff) / 23;
                    if (count > 0) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeZone(SmaBleUtils.getDefaultTimeZone());
                        List<SmaAlarm> alarms = new ArrayList<>();
                        byte[] b;
                        for (int i = 0; i < count; i++) {
                            SmaAlarm alarm = new SmaAlarm();
                            b = Arrays.copyOfRange(mData, 23 * i + 13, 23 * i + 36);
                            calendar.set(((b[0] >> 2) + 2000), (((b[0] & 0b11) << 2) | ((b[1] >> 6) & 0b11)) - 1,
                                    (b[1] >> 1) & 0b11111, ((b[1] & 0b1) << 4) | (((b[2] >> 4) & 0b1111)), ((b[2] &
                                            0b1111) << 2) | ((b[3] & 0xff) >> 6));
                            alarm.setTime(calendar.getTimeInMillis());
                            alarm.setId((b[3] & 0b111111) >> 3);
//                            alarm.setEnabled((b[3] & 0b111) >> 2);
                            alarm.setEnabled((b[4] & 0xff) >> 7);
                            alarm.setRepeat(b[4] & 0b1111111);
                            try {
                                String tag = new String(Arrays.copyOfRange(b, 5, 24), "UTF-8");
                                int index = tag.indexOf('\u0000');
                                alarm.setTag(tag.substring(0, index));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            alarms.add(alarm);
                        }
                        L.d("READ_ALARM_BACK -> " + alarms.toString());
                        for (SmaCallback bc : mSmaCallbacks) {
                            bc.onReadAlarm(alarms);
                        }
                    } else {
                        L.d("READ_ALARM_BACK -> " + 0);
                        for (SmaCallback bc : mSmaCallbacks) {
                            bc.onReadAlarm(null);
                        }
                    }
                } else if (key == Key.ALARMS_CHANGE) {
                    L.d("ALARMS_CHANGE");
                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onAlarmsChange();
                    }
                } else if (key == Key.READ_VERSION_BACK) {
                    int a = mData[13] & 0xff;
                    int b = mData[14] & 0xff;
                    int c = mData[15] & 0xff;
                    String version = a + "." + b + "." + c;
                    L.d("READ_VERSION_BACK -> " + version);
                    saveFirmwareVersion(version);
                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onReadVersion(version, version);
                    }
                } else if (key == Key.READ_BATTERY_BACK) {
                    int battery = mData[13] & 0xff;
                    L.d("READ_BATTERY_BACK -> " + battery);
                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onReadBattery(battery);
                    }
                } else if (key == Key.GOAL_CHANGE) {
                    L.d("GOAL_CHANGE");
                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onGoalChange();
                    }
                } else if (key == Key.READ_GOAL_BACK) {
                    byte[] bytes = Arrays.copyOfRange(mData, 13, 17);
                    int goal = EaseUtils.bytesToInt(bytes);
                    L.d("READ_GOAL_BACK -> " + goal);
                    if (goal == 0) return;

                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onReadGoal(goal);
                    }
                } else if (key == Key.SEDENTARINESS_CHANGE) {
                    L.d("SEDENTARINESS_CHANGE");
                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onSedentarinessChange();
                    }
                } else if (key == Key.READ_SEDENTARINESS_BACK) {
                    byte[] b = Arrays.copyOfRange(mData, 13, 21);
                    SmaSedentarinessSettings ss = new SmaSedentarinessSettings();
                    ss.setRepeat(b[0]);
                    ss.setEnd2((b[1] & 0xff) >> 3);
                    ss.setStart2(((b[1] & 0b111) << 2) | ((b[2] & 0xff) >> 6));
                    ss.setEnd1((b[2] & 0b111111) >> 1);
                    ss.setStart1(((b[2] & 0b1) << 4) | ((b[3] & 0xff) >> 4));
                    ss.setInterval(((b[3] & 0b1111) << 4) | ((b[4] & 0xff) >> 4));
                    ss.setEnabled(b[7]);
                    L.d("READ_SEDENTARINESS_BACK -> " + ss.toString());
                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onReadSedentariness(ss);
                    }
                } else if (key == Key.CAMERA_PRESSED) {
                    L.d("CAMERA_PRESSED");
                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onTakePhoto();
                    }
                } else if (key == Key.SET_DEVICE_COMMON_REQUEST) {
                    int flag = ((mData[13] & 0xff) << 8) | (mData[14] & 0xff);
                    L.d("SET_DEVICE_COMMON_REQUEST -> flag=" + flag);
                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onDeviceCommonRequest(flag);
                    }
                }
                break;

            case Cmd.CONNECT:
                boolean result = mData[13] == 0;
                if (key == Key.BIND_BACK) {
                    L.d("BIND_BACK -> " + result);
                    if (result) {
                        saveNameAndAddress(mEaseConnector.mBluetoothDevice.getName(), mEaseConnector.mBluetoothDevice
                                .getAddress());
                        mEaseConnector.setAddress(mEaseConnector.mBluetoothDevice.getAddress());
                        saveFirmwareVersion("");
                        saveFirmwareFlag("");

                        SharedPreferences put_pref = mContext.getSharedPreferences("shared_pref", MODE_PRIVATE);
                        SharedPreferences.Editor put_editor = put_pref.edit();

                        put_editor.putString("last_weather_update", "2019-01-01 00:00:00");
                        put_editor.putString("last_agps_update", "2019-01-01 00:00:00");
                        put_editor.putString("last_longitude", "0");
                        put_editor.putString("last_latitude", "0");

                        put_editor.commit();

                        sendLoginCmd();
                    }else {
                        for (SmaCallback bc : mSmaCallbacks) {
                            bc.onLogin(result);
                        }
                    }
                } else if (key == Key.LOGIN_BACK) {
                    L.d("LOGIN_BACKa -> " + result);
                    if (result) {
                        isLoggedIn = true;
                        mEaseConnector.connect(false);

                    }

                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onLogin(result);
                    }
                }
                break;

            case Cmd.NOTICE:
                if (key == Key.FIND_PHONE) {
                    L.d("FIND_PHONE");
                    for (SmaCallback bc : mSmaCallbacks) {
                        bc.onFindPhone(mData[13] == 1);
                    }
                }
                break;

            case Cmd.DATA:
                int count = 0;
                if (key == Key.EXERCISE) {
                    count = (((mData[11] & 0xff) << 8) | mData[12] & 0xff) / 8;
                    if (count > 0) {
                        byte[] b;
                        for (int i = 0; i < count; i++) {
                            b = Arrays.copyOfRange(mData, 8 * i + 13, 8 * i + 21);
                            int mode = b[4] & 0xff;
//                            L.d(SmaBleUtils.byteArray2HexString(b));
                            if (mode == SmaSport.Mode.START) {
                                for (SmaCallback bc : mSmaCallbacks) {
                                    bc.onStartExercise(true);
                                }
                            } else if (mode == SmaSport.Mode.GOING) {
                                for (SmaCallback bc : mSmaCallbacks) {
                                    bc.onStartExercise(true);
                                }
                            }
                        }
                    }
                } else {
                    if (key == Key.SPORT_BACK) {
                        count = (((mData[11] & 0xff) << 8) | mData[12] & 0xff) / 8;
                        if (count > 0) {
                            List<SmaSport> list = new ArrayList<>();
                            byte[] b;
                            for (int i = 0; i < count; i++) {
                                SmaSport sport = new SmaSport();

                                b = Arrays.copyOfRange(mData, 8 * i + 13, 8 * i + 21);
                                long seconds = Long.parseLong(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b,
                                        0, 4)), 16);
                                int step = Integer.parseInt(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b, 5,
                                        8)), 16);
                                sport.mode = b[4] & 0xff;
                                sport.time = MS + seconds * 1000;
                                sport.date = mDateFormat.format(new Date(sport.time));
                                sport.step = step;
                                list.add(sport);

                                if (sport.mode == SmaSport.Mode.START) {
                                    for (SmaCallback bc : mSmaCallbacks) {
                                        bc.onStartExercise(true);
                                    }
                                } else if (sport.mode == SmaSport.Mode.END) {
                                    for (SmaCallback bc : mSmaCallbacks) {
                                        bc.onStartExercise(false);
                                    }
                                }
                            }
                            L.d("SPORT_BACK -> " + list.toString());
                            for (SmaCallback bc : mSmaCallbacks) {
                                bc.onReadSportData(list);
                            }
                        }
                    } else if (key == Key.SLEEP_BACK) {
                        count = (((mData[11] & 0xff) << 8) | mData[12] & 0xff) / 7;
                        if (count > 0) {
                            List<SmaSleep> list = new ArrayList<>();
                            byte[] b;
                            for (int i = 0; i < count; i++) {
                                SmaSleep sleep = new SmaSleep();

                                b = Arrays.copyOfRange(mData, 7 * i + 13, 7 * i + 20);
                                long seconds = Long.parseLong(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b,
                                        0, 4)), 16);
                                int mode = b[4] & 0xff;
                                int soft = b[5] & 0xff;
                                int strong = b[6] & 0xff;

                                sleep.time = MS + seconds * 1000;
                                sleep.date = mDateFormat.format(new Date(sleep.time));
                                sleep.soft = soft;
                                sleep.mode = mode;
                                sleep.strong = strong;
                                list.add(sleep);
                            }
                            L.d("SLEEP_BACK -> " + list.toString());
                            for (SmaCallback bc : mSmaCallbacks) {
                                bc.onReadSleepData(list);
                            }
                        }
                    } else if (key == Key.RATE_BACK) {
                        count = (((mData[11] & 0xff) << 8) | mData[12] & 0xff) / 5;
                        if (count > 0) {
                            List<SmaHeartRate> list = new ArrayList<>();
                            byte[] b;
                            for (int i = 0; i < count; i++) {
                                SmaHeartRate rate = new SmaHeartRate();

                                b = Arrays.copyOfRange(mData, 5 * i + 13, 5 * i + 18);
                                long seconds = Long.parseLong(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b,
                                        0, 4)), 16);
                                if ((seconds % 60) == 1) {//如果秒数为1，为锻炼模式的心率
                                    rate.type = SmaHeartRate.Type.EXERCISE;
                                } else if ((seconds % 60) == 2) {//如果秒数为2，为i-MED锻炼模式的心率
                                    rate.type = SmaHeartRate.Type.EXERCISE_IMED;
                                }
                                int value = b[4] & 0xff;

                                rate.time = MS + seconds * 1000;
                                rate.date = mDateFormat.format(new Date(rate.time));
                                rate.value = value;
                                list.add(rate);
                            }
                            L.d("RATE_BACK -> " + list.toString());
                            for (SmaCallback bc : mSmaCallbacks) {
                                bc.onReadHeartRateData(list);
                            }
                        }
                    } else if (key == Key.BLOOD_PRESSURE_BACK) {
                        count = (((mData[11] & 0xff) << 8) | mData[12] & 0xff) / 6;
                        if (count > 0) {
                            List<SmaBloodPressure> list = new ArrayList<>();
                            byte[] b;
                            for (int i = 0; i < count; i++) {
                                SmaBloodPressure pressure = new SmaBloodPressure();

                                b = Arrays.copyOfRange(mData, 6 * i + 13, 6 * i + 19);
                                long seconds = Long.parseLong(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b,
                                        0, 4)), 16);
                                pressure.systolic = b[4] & 0xff;
                                pressure.diastolic = b[5] & 0xff;
                                pressure.time = MS + seconds * 1000;
                                pressure.date = mDateFormat.format(new Date(pressure.time));
                                list.add(pressure);
                            }
                            L.d("BLOOD_PRESSURE_BACK -> " + list.toString());
                            for (SmaCallback bc : mSmaCallbacks) {
                                bc.onReadBloodPressure(list);
                            }
                        }
                    } else if (key == Key.CYCLING_BACK) {
                        count = (((mData[11] & 0xff) << 8) | mData[12] & 0xff) / 7;
                        if (count > 0) {
                            List<SmaCycling> list = new ArrayList<>();
                            byte[] b;
                            for (int i = 0; i < count; i++) {
                                SmaCycling cycling = new SmaCycling();
                                b = Arrays.copyOfRange(mData, 7 * i + 13, 7 * i + 20);
                                long seconds = Long.parseLong(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b,
                                        0, 4)), 16);
                                if (seconds == 0 || seconds == 0xFFFFFFFEL || seconds == 0xFFFFFFFFL) {
                                    int status;
                                    if (seconds == 0) {
                                        status = SmaCycling.START;
                                    } else if (seconds == 0xFFFFFFFEL) {
                                        status = SmaCycling.GOING;
                                    } else {
                                        status = SmaCycling.END;
                                    }
                                    L.w("onCycling -> " + status);
                                    for (SmaCallback bc : mSmaCallbacks) {
                                        bc.onCycling(status);
                                    }
                                    return;
                                }
                                cycling.time = MS + seconds * 1000;
                                cycling.date = mDateFormat.format(new Date(cycling.time));
                                cycling.cal = ((b[4] & 0xff) << 8) | (b[5] & 0xff);
                                cycling.rate = b[6] & 0xff;
                                list.add(cycling);
                            }
                            L.d("CYCLING_BACK|SWIM_BACK -> " + list.toString());
                            for (SmaCallback bc : mSmaCallbacks) {
                                bc.onReadCycling(list);
                            }
                        }
                    } else if (key == Key.EXERCISE2_BACK) {
                        count = (((mData[11] & 0xff) << 8) | mData[12] & 0xff) / 34;
                        if (count > 0) {
                            List<SmaExercise> list = new ArrayList<>();
                            byte[] b;
                            for (int i = 0; i < count; i++) {
                                SmaExercise exercise = new SmaExercise();
                                b = Arrays.copyOfRange(mData, 34 * i + 13, 34 * i + 47);
                                long start = Long.parseLong(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b,
                                        0, 4)), 16);
                                exercise.start = MS + start * 1000;
                                exercise.date = mDateFormat.format(new Date(exercise.start));

                                long end = Long.parseLong(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b,
                                        4, 8)), 16);
                                exercise.end = MS + end * 1000;
                                exercise.duration = Integer.parseInt(EaseUtils.bytesArray2HexStringWithout0x(Arrays
                                        .copyOfRange(b, 8, 12)), 16);
                                exercise.altitude = ((b[12] & 0xff) << 8) | (b[13] & 0xff);
                                exercise.airPressure = ((b[14] & 0xff) << 8) | (b[15] & 0xff);
                                exercise.spm = b[16] & 0xff;
                                exercise.type = b[17] & 0xff;
                                exercise.step = Integer.parseInt(EaseUtils.bytesArray2HexStringWithout0x(Arrays
                                        .copyOfRange(b, 18, 22)), 16);
                                exercise.distance = Integer.parseInt(EaseUtils.bytesArray2HexStringWithout0x(Arrays
                                        .copyOfRange(b, 22, 26)), 16);
                                exercise.cal = Integer.parseInt(EaseUtils.bytesArray2HexStringWithout0x(Arrays
                                        .copyOfRange(b, 26, 30)), 16);
                                exercise.speed = ((b[30] & 0xff) << 8) | (b[31] & 0xff);
                                exercise.pace = ((b[32] & 0xff) << 8) | (b[33] & 0xff);
                                list.add(exercise);
                            }
                            L.d("EXERCISE2_BACK -> " + list.toString());
                            for (SmaCallback bc : mSmaCallbacks) {
                                bc.onReadExercise(list);
                            }
                        }
                    } else if (key == Key.TRACKER_BACK) {
                        count = (((mData[11] & 0xff) << 8) | mData[12] & 0xff) / 16;
                        if (count > 0) {
                            List<SmaTracker> list = new ArrayList<>();
                            byte[] b;
                            for (int i = 0; i < count; i++) {
                                SmaTracker tracker = new SmaTracker();
                                b = Arrays.copyOfRange(mData, 16 * i + 13, 16 * i + 29);
                                long timestamp = Long.parseLong(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b,
                                        0, 4)), 16);
                                tracker.time = MS + timestamp * 1000;
                                tracker.date = mDateFormat.format(new Date(tracker.time));
                                tracker.type = 1;
                                tracker.altitude = (short) (((b[6] & 0xff) << 8) | (b[7] & 0xff));
                                tracker.longitude = Float.intBitsToFloat(EaseUtils.bytesToInt(Arrays.copyOfRange(b, 8, 12)));
                                tracker.latitude = Float.intBitsToFloat(EaseUtils.bytesToInt(Arrays.copyOfRange(b, 12, 16)));
                                list.add(tracker);
                            }
                            L.d("TRACKER_BACK -> " + list.toString());
                            for (SmaCallback bc : mSmaCallbacks) {
                                bc.onReadTracker(list);
                            }
                        }
                    } else if (key == Key.SPORT2_BACK) {
                        count = (((mData[11] & 0xff) << 8) | mData[12] & 0xff) / 16;
                        if (count > 0) {
                            List<SmaSport> list = new ArrayList<>();
                            byte[] b;
                            for (int i = 0; i < count; i++) {
                                SmaSport sport = new SmaSport();
                                b = Arrays.copyOfRange(mData, 16 * i + 13, 16 * i + 29);
                                long timestamp = Long.parseLong(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b,
                                        0, 4)), 16);
                                sport.time = MS + timestamp * 1000;
                                sport.date = mDateFormat.format(new Date(sport.time));
                                sport.mode = b[4] & 0xff;
                                sport.step = Integer.parseInt(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b, 5,
                                        8)), 16);
                                sport.calorie = Integer.parseInt(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b, 8,
                                        12)), 16) / 10000.0;
                                sport.distance = Integer.parseInt(EaseUtils.bytesArray2HexStringWithout0x(Arrays.copyOfRange(b, 12,
                                        16)), 16) / 10000;
                                list.add(sport);
                            }
                            L.d("SPORT2_BACK -> " + list.toString());
                            for (SmaCallback bc : mSmaCallbacks) {
                                bc.onReadSportData(list);
                            }
                        }
                    }

                    onDataRead((byte) (key - 1), count);
                }
                break;
            case Cmd.CONTROL:
                if (key == 0x01) {
                    L.d("CONTROL -> " + mData[13]);
                    for (SmaCallback connectorCallback : mSmaCallbacks) {
                        connectorCallback.onKeyDown(mData[13]);
                    }
                }
                break;
        }
    }

    /**
     * Return ACK to device when you receive data from it.
     *
     * @param rt
     */
    private void returnACK(byte[] rt) {
        byte[] ack = new byte[8];
        ack[0] = (byte) 0xAB;
        ack[1] = 0x10;
        ack[6] = rt[6];
        ack[7] = rt[7];
        L.d("SmaManager returnACK -> " + EaseUtils.byteArray2HexString(ack));
        mSmaMessenger.addMessage(new SmaMessenger.SmaMessage(mEaseConnector.mGatt, mEaseConnector.getGattCharacteristic
                (mUUIDMainService, mUUIDWrite), ack, SmaMessenger.MessageType.WRITE, false, true));
    }

    /**
     * @return True if the bond device is logged in,else false,see {@link SmaCallback#onLogin(boolean)}
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean isCalling = false;

    /**
     * Save the name and address of the remote device.
     *
     * @param name    name of the remote device
     * @param address address of the remote device
     */
    public void saveNameAndAddress(String name, String address) {
        mEditor.putString(SP_DEVICE_NAME, name).apply();
        mEditor.putString(SP_DEVICE_ADDRESS, address).apply();

        if(!name.isEmpty() && !address.isEmpty()) {
            SharedPreferences pref = mContext.getSharedPreferences("shared_pref", MODE_PRIVATE);
            //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
            String mem_no = pref.getString("mem_no", "0");

            String url = "https://www.smarthelper.co.kr/coa_api.php";
            ContentValues values = new ContentValues();
            values.put("action", "getDeviceNameAndAddress");
            values.put("device_name", name);
            values.put("device_address", address);
            values.put("mem_no", mem_no);

            HttpAsyncRequest httpAssyncRequest = new HttpAsyncRequest(url, values);
            httpAssyncRequest.execute();
        }
    }

    /**
     * Save the address of the remote classic Bluetooth device.
     *
     * @param address address of the remote classic Bluetooth device
     */
    private void saveClassicAddress(String address) {
        mEditor.putString(SP_CLASSIC_ADDRESS, address).apply();
    }

    /**
     * Return the name of the bond device.
     *
     * @return If a device has been bond,return its name,or a empty String.
     */
    public String getSavedName() {
        return mPreferences.getString(SP_DEVICE_NAME, "");
    }

    /**
     * Return the address of the bond device.
     *
     * @return If a device has been bond,return its address,or a empty String.
     */
    public String getSavedAddress() {
        return mPreferences.getString(SP_DEVICE_ADDRESS, "");
    }

    /**
     * Return the address of the bond classic device.
     *
     * @return If a classic device has been bond,return its address,or a empty String.
     */
    public String getClassicAddress() {
        return mPreferences.getString(SP_CLASSIC_ADDRESS, "");
    }

    /**
     * Whether any device has been bond.
     *
     * @return true if a device has been bond,else false.
     */
    public boolean isBond() {
        return !TextUtils.isEmpty(getSavedAddress());
    }

    /**
     * Save the device type of the remote device.
     *
     * @param deviceType the device type of the remote device
     */
    public void saveDeviceType(String deviceType) {
        mEditor.putString(SP_DEVICE_TYPE, deviceType).apply();
    }

    /**
     * Return the device type of the bond device.
     *
     * @return If a device has been bond,return its device type,or a empty String.
     */
    public String getDeviceType() {
        return mPreferences.getString(SP_DEVICE_TYPE, "");
    }

    /**
     * Save the firmware version of the remote device.
     *
     * @param version the firmware version of the remote device
     */
    public void saveFirmwareVersion(String version) {
        mEditor.putString(SP_FIRMWARE, version).apply();
    }

    /**
     * Return the firmware version of the bond device.
     *
     * @return If a device has been bond,return its firmware version,or a empty String.
     */
    public String getFirmwareVersion() {
        return mPreferences.getString(SP_FIRMWARE, "");
    }

    /**
     * Save the firmware flag of the remote device.
     *
     * @param flag the firmware flag of the remote device
     */
    public void saveFirmwareFlag(String flag) {
        mEditor.putString(SP_FIRMWARE_FLAG, flag).apply();
    }

    /**
     * Return the firmware flag of the bond device.
     *
     * @return If a device has been bond,return its firmware flag,or a empty String.
     */
    public String getFirmwareFlag() {
        return mPreferences.getString(SP_FIRMWARE_FLAG, "");
    }

    public void setPhoneFlag(int flag) {
        mEditor.putInt(SP_PHONE_FLAG, flag).apply();
    }

    public int getPhoneFlag() {
        return mPreferences.getInt(SP_PHONE_FLAG, 1);
    }

    /**
     * 利用反射
     * 将设备从手机蓝牙设置中的已配对设备中移除，防止三星J5008等手机解除绑定后，在手机蓝牙设置中没有清除已配对设备信息，导致再次连接时会被动断开。
     *
     * @param address 设备地址
     */
    private void removeDevice(String address) {
        if (TextUtils.isEmpty(address)) {
            L.i("SmaManager removeDevice -> address empty");
            return;
        }

        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            L.i("SmaManager removeDevice -> address invalid");
            return;
        }

        try {
            BluetoothDevice device = sBluetoothAdapter.getRemoteDevice(address);
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            boolean success = (Boolean) m.invoke(device, (Object[]) null);
            L.i("SmaManager removeDevice -> " + success);
        } catch (Exception e) {
            L.e("SmaManager removeDevice -> " + e.getMessage());
        }
    }

    /**
     * Add a callback to the list of callbacks to receive the result of communication with Bluetooth device.
     *
     * @param cb the callback to be added
     * @return This SmaManager object.
     */
    public SmaManager addSmaCallback(SmaCallback cb) {
        mSmaCallbacks.add(cb);
        return this;
    }

    /**
     * Remove a callback from the list of callbacks.
     *
     * @param cb the callback to be removed
     */
    public void removeSmaCallback(SmaCallback cb) {
        mSmaCallbacks.remove(cb);
    }

    private static final String UPDATE_M_SERVICE = "C6A2B98B-F821-18BF-9704-0266F20E80FD";
    private static final String UPDATE_M_CH_SIZE = "C6A22920-F821-18BF-9704-0266F20E80FD";
    private static final String UPDATE_M_CH_FLAG = "C6A22922-F821-18BF-9704-0266F20E80FD";
    private static final String UPDATE_M_CH_DATA = "C6A22924-F821-18BF-9704-0266F20E80FD";
    private static final String UPDATE_M_CH_MD5  = "C6A22926-F821-18BF-9704-0266F20E80FD";

    public void updateM(final File file) {
        if (!isLoggedIn || file == null || !file.exists() || !file.isFile()) {
            for (SmaCallback callback : mSmaCallbacks) {
                callback.onTransferBuffer(false, 0, 0);
            }
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                requestHighSpeedMode(true);
                int fileSize = (int) file.length();
                mTransferTotal = fileSize % 180 == 0 ? fileSize / 180 : fileSize / 180 + 1;
                L.w("updateM -> fileSize=" + fileSize + ", mTransferTotal=" + mTransferTotal);
                mTransferCompleted = 0;
                mSmaMessenger.addMessage(new SmaMessenger.SmaMessage(mEaseConnector.mGatt, mEaseConnector.getGattCharacteristic
                        (UPDATE_M_SERVICE, UPDATE_M_CH_SIZE), UpdateMUtils.intToByte(fileSize), SmaMessenger.MessageType
                        .WRITE));
                mSmaMessenger.addMessage(new SmaMessenger.SmaMessage(mEaseConnector.mGatt, mEaseConnector.getGattCharacteristic
                        (UPDATE_M_SERVICE, UPDATE_M_CH_FLAG), new byte[]{0x01}, SmaMessenger.MessageType.WRITE));

                try {
                    byte[] buffers = UpdateMUtils.getBytesByFile(file);
                    for (int i = 0; i < mTransferTotal; i++) {
                        byte[] buffer;
                        if (i < mTransferTotal - 1) {
                            buffer = Arrays.copyOfRange(buffers, i * 180, (i + 1) * 180);
                        } else {
                            buffer = Arrays.copyOfRange(buffers, i * 180, fileSize);
                        }
                        SmaMessenger.SmaMessage msg = new SmaMessenger.SmaMessage(mEaseConnector.mGatt, mEaseConnector
                                .getGattCharacteristic(UPDATE_M_SERVICE, UPDATE_M_CH_DATA), buffer, SmaMessenger.MessageType
                                .WRITE);
                        mSmaMessenger.addMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String md5 = "b3b27696771768c6648f237a43c37a39";
                mSmaMessenger.addMessage(new SmaMessenger.SmaMessage(mEaseConnector.mGatt, mEaseConnector.getGattCharacteristic
                        (UPDATE_M_SERVICE, UPDATE_M_CH_FLAG), new byte[]{0x02}, SmaMessenger.MessageType.WRITE));
                SmaMessenger.SmaMessage msg = new SmaMessenger.SmaMessage(mEaseConnector.mGatt, mEaseConnector.getGattCharacteristic
                        (UPDATE_M_SERVICE, UPDATE_M_CH_MD5), md5.getBytes(), SmaMessenger.MessageType.WRITE);
                mSmaMessenger.addMessage(msg);
                requestHighSpeedMode(false);
            }
        }).start();
    }

    public void writeStream(SmaStream smaStream) {
        if (!SmaManager.getInstance().isLoggedIn) return;

        try {
            if (smaStream == null) return;

            InputStream stream = smaStream.inputStream;
            if (stream == null || stream.available() < 1) return;

            byte[] buffer = new byte[stream.available()];
            int count = stream.read(buffer);
            stream.close();
            if (count < 1) return;

            requestHighSpeedMode(true);
            int length = buffer.length;
            SmaStreamInfo info = new SmaStreamInfo();
            info.size = length;
            info.flag = smaStream.flag;
            if (smaStream.flag == SmaStream.FLAG_LOCATION_ASSISTED) {
                int hours = (buffer[2] & 0xff) << 16;
                hours |= (buffer[1] & 0xff) << 8;
                hours |= buffer[0] & 0xff;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
                calendar.set(1980, Calendar.JANUARY, 6, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.add(Calendar.HOUR, hours);
                DateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", Locale.getDefault());
                format.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
                //M1 agps文件的附加信息（有效时间）需要从文件中获取
                info.extras = new String[]{format.format(calendar.getTime())};
            } else {
                info.extras = smaStream.extras;
            }

            L.i("SmaStream -> " + info.toString());
            write(Cmd.DATA, Key.REPLY_DEVICE_COMMON_REQUEST, info);

            count = length % 2033 == 0 ? length / 2033 : length / 2033 + 1;
            for (int i = 0; i < count; i++) { //拆分
                byte[] index = new byte[]{(byte) (((i + 1) >> 8) & 0xff), (byte) ((i + 1) & 0xff)};
                byte[] packet;
                if (i == count - 1) {
                    packet = Arrays.copyOfRange(buffer, i * 2033, length);
                } else {
                    packet = Arrays.copyOfRange(buffer, i * 2033, (i + 1) * 2033);
                }
                write(Cmd.DATA, Key.REPLY_DEVICE_COMMON_REQUEST, EaseUtils.concat(index, packet));
            }
            requestHighSpeedMode(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * See {@link SmaManager#write(byte, byte, byte[])}
     *
     * @param cmd
     * @param key
     */
    public void write(byte cmd, byte key) {
        write(cmd, key, new byte[]{});
    }

    /**
     * See {@link SmaManager#write(byte, byte, byte[])}.
     *
     * @param cmd
     * @param key
     * @param b
     */
    public void write(byte cmd, byte key, boolean b) {
        write(cmd, key, new byte[]{(byte) (b ? 1 : 0)});
    }

    /**
     * The data written to device depends on {@link Cmd},{@link Key} and extra data.It will generate a byte array by
     * invoking  {@link SmaBleHelper#getBytes(byte, byte, byte[])}.We write the result to the bond device by invoking
     * this method.If the length of the result is greater than 20,it will be split in several byte arrays those length
     * are less than 21.And then,these data will be written to device sequentially.
     *
     * @param cmd   {@link Cmd}
     * @param key   {@link Key}
     * @param extra the extra data
     */
    public void write(byte cmd, byte key, byte[] extra) {
//        L.d("write -> cmd = " + cmd + ",key = " + key + ",data = " + EaseUtils.byteArray2HexString(extra));
        if (cmd != Cmd.CONNECT && !isLoggedIn) return;

        if (cmd == Cmd.NONE && key == Key.NONE) {
            SmaMessenger.SmaMessage message = new SmaMessenger.SmaMessage(mEaseConnector.mGatt, mEaseConnector.getGattCharacteristic
                    (mUUIDMainService, mUUIDWrite), extra, SmaMessenger.MessageType.WRITE);
            if (extra != null && extra.length == 1) {
                message.isNeedReceiveACK = true;
            }
            mSmaMessenger.addMessage(message);
        } else {
//            if (key == Key.CALL_IDLE || key == Key.CALL_OFF_HOOK) {
//                if (mSmaMessenger.isTransferring) {
//                    return;
//                }
//            }

            if (key == Key.ENABLE_NO_DISTURB) {
                byte[] tem = new byte[13];
                tem[0] = extra[0];
                if (extra[0] == 1) {
                    tem[3] = 23;
                    tem[4] = 59;
                }
                byte[] data = SmaBleHelper.getBytes(cmd, key, tem);
                mSmaMessenger.addMessage(new SmaMessenger.SmaMessage(mEaseConnector.mGatt, mEaseConnector
                        .getGattCharacteristic(mUUIDMainService, mUUIDWrite), data, SmaMessenger.MessageType.WRITE,
                        isNeedReturnACK(cmd, key), false, true));
            } else {
                byte[] data = SmaBleHelper.getBytes(cmd, key, extra);
                mSmaMessenger.addMessage(new SmaMessenger.SmaMessage(mEaseConnector.mGatt, mEaseConnector
                        .getGattCharacteristic(mUUIDMainService, mUUIDWrite), data, SmaMessenger.MessageType.WRITE,
                        isNeedReturnACK(cmd, key), false, true));
            }
        }
        if (cmd == Cmd.SET) {
            if (key == Key.ENABLE_ANTI_LOST || key == Key.ENABLE_NO_DISTURB || key == Key.ENABLE_CALL || key
                    == Key.ENABLE_NOTIFICATION || key == Key.ENABLE_DISPLAY_VERTICAL || key == Key
                    .ENABLE_DETECT_SLEEP || key == Key.ENABLE_RAISE_ON) {
                mEditor.putBoolean("enabled" + cmd + String.valueOf(key), extra[0] == 1).apply();
            }
            if (key == Key.SET_BACK_LIGHT) {
                mEditor.putInt(SmaConsts.SP_BACK_LIGHT, extra[0]).apply();
            }
        }
    }

    /**
     * See {@link SmaManager#write(byte, byte, byte[])}.
     *
     * @param cmd   cmd
     * @param key   key
     * @param value value
     * @param digit sometimes the value you set may be out of range -128~127,at this case we should convert it to a byte
     *              array by
     *              invoking {@link EaseUtils#intToBytes(int)},so that we should know how many bytes can hold the value.
     */
    public void write(byte cmd, byte key, int value, int digit) {
        byte[] extra;
        if (digit == 1) {
            extra = new byte[]{(byte) value};
        } else if (digit == 4) {
            extra = EaseUtils.intToBytes(value);
        } else {
            extra = new byte[0];
        }
        write(cmd, key, extra);
    }

    /**
     * See {@link SmaManager#write(byte, byte, byte[])}.
     *
     * @param cmd
     * @param key
     * @param smaCmd
     */
    public void write(byte cmd, byte key, ISmaCmd smaCmd) {
        write(cmd, key, smaCmd.toByteArray());
    }

    /**
     * See {@link SmaManager#write(byte, byte, byte[])}.
     *
     * @param cmd
     * @param key
     * @param list
     */
    public void write(byte cmd, byte key, List<? extends ISmaCmd> list) {
        byte[] data = new byte[0];
        for (ISmaCmd smaCmd : list) {
            data = EaseUtils.concat(data, smaCmd.toByteArray());
        }
        write(cmd, key, data);
    }

    /**
     * See {@link SmaManager#write(byte, byte, byte[])}.
     *
     * @param cmd
     * @param key
     * @param title
     * @param content
     */
    public void write(byte cmd, byte key, String title, String content) {
//        if (mSmaMessenger.isTransferring) {
//            return;
//        }
        try {
            if (TextUtils.isEmpty(title)) {
                title = "";
            }
            if (TextUtils.isEmpty(content)) {
                content = "";
            }
            byte[] titles = title.getBytes("UTF-8");
            byte[] contents = content.getBytes("UTF-8");

            L.d("SmaManager write -> title=" + title + ", content=" + content);
            if (cmd == Cmd.SET && key == Key.SET_NAME_GROUP) {
                if (titles.length > 32) {
                    titles = Arrays.copyOf(titles, 32);
                }
                if (contents.length > 32) {
                    contents = Arrays.copyOf(contents, 32);
                }

                byte[] name = new byte[32];
                System.arraycopy(titles, 0, name, 0, titles.length);

                byte[] group = new byte[32];
                System.arraycopy(contents, 0, group, 0, contents.length);

                write(cmd, key, EaseUtils.concat(name, group));
                return;
            }

            byte[] extra = new byte[32 + (contents.length > 199 ? 199 : contents.length)];
            for (int i = 0, l = extra.length; i < l; i++) {
                if (i < 32) {
                    if (i < titles.length) {
                        extra[i] = titles[i];
                    }
                } else {
                    extra[i] = contents[i - 32];
                }
            }
            write(cmd, key, extra);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private int         mXModeAction;
    private InputStream mXModeStream;

    public boolean writeXMode(byte action, InputStream inputStream) {
        try {
            if (inputStream == null) return false;

            if (!isLoggedIn || inputStream.available() < 1) {
                inputStream.close();
                return false;
            }

            mXModeAction = action;
            mXModeStream = inputStream;
            write(SmaManager.Cmd.UPDATE, SmaManager.Key.INTO_XMODEM);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean writeXMode(byte action, File file) {
        try {
            return writeXMode(action, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean writeXMode(byte action, int rawRes) {
        return writeXMode(action, mContext.getResources().openRawResource(rawRes));
    }

    public void readData(byte[] keys) {
        if (keys == null || keys.length < 1) return;

        this.mDataKes = keys;
        write(Cmd.DATA, keys[0]);
        sHandler.postDelayed(mRunnable, 8000);
    }

    private void onDataRead(byte key, int count) {
        sHandler.removeCallbacks(mRunnable);

        int max = getMaxItem(key);
        if (!isLastData(key)) {
            if (count == max) {
                write(Cmd.DATA, key);
            } else {
                int index = 0;
                for (byte cKey : mDataKes) {
                    index++;
                    if (cKey == key) break;
                }
                write(Cmd.DATA, mDataKes[index]);
            }
        } else {
            if (count == max) {
                write(Cmd.DATA, key);
            }
        }

        if (isLastData(key) && count < max) {
            for (SmaCallback callback : mSmaCallbacks) {
                callback.onReadDataFinished(true);
            }
        } else {
            sHandler.postDelayed(mRunnable, 8000);
        }
    }

    private boolean isLastData(byte key) {
        return key == mDataKes[mDataKes.length - 1];
    }

    private int getMaxItem(byte key) {
        if (key == Key.EXERCISE2) return 5;
        if (key == Key.TRACKER || key == Key.SPORT2) return 15;
        return 20;
    }

    private void requestMtu(int mtu) {
        if (mtu < 20) return;

        mSmaMessenger.addMessage(new SmaMessenger.SmaMessage(mEaseConnector.mGatt, null,
                new byte[]{(byte) ((mtu >> 8) & 0xff), (byte) (mtu & 0xff)}, SmaMessenger.MessageType.REQUEST_MTU));
    }

    private void requestHighSpeedMode(boolean enabled) {
        mSmaMessenger.addMessage(new SmaMessenger.SmaMessage(mEaseConnector.mGatt, null, enabled ? new byte[0] : null,
                SmaMessenger.MessageType.REQUEST_CONNECTION_PRIORITY));
    }

    /**
     * Enable or disable notifications for given UUIDs.
     *
     * @param serviceUUID        UUID of {@link BluetoothGattService}
     * @param CharacteristicUUID UUID of {@link BluetoothGattCharacteristic}
     * @param enable             Set true to enable notifications,false to disable notifications
     */
    private void setNotify(String serviceUUID, String CharacteristicUUID, boolean enable) {
        mSmaMessenger.addMessage(new SmaMessenger.SmaMessage(mEaseConnector.mGatt, mEaseConnector.getGattCharacteristic
                (serviceUUID, CharacteristicUUID), enable ? new byte[0] : null, SmaMessenger.MessageType.NOTIFY));
    }

    /**
     * Read requested the characteristic for given UUIDs.
     *
     * @param serviceUUID        UUID of {@link BluetoothGattService}
     * @param CharacteristicUUID UUID of {@link BluetoothGattCharacteristic}
     */
    public void read(String serviceUUID, String CharacteristicUUID) {
        mSmaMessenger.addMessage(new SmaMessenger.SmaMessage(mEaseConnector.mGatt, mEaseConnector.getGattCharacteristic
                (serviceUUID, CharacteristicUUID), null, SmaMessenger.MessageType.READ));
    }

    /**
     * Remove all tasks in task queue.We call this method when the device get connected or disconnected,even thought the
     * tasks
     * have not been processed at that moment.
     */
    public void clearAllTask() {
        L.d("SmaManager -> clearAllTask");
        mData = new byte[0];
        mLength = 0;
        mSmaMessenger.clearAllTask();
        isTransferring = false;
        mTransferTotal = -1;
        mTransferCompleted = -1;
        if (mXModeStream != null) {
            try {
                mXModeStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return whether the function is enabled
     *
     * @param cmd {@link Cmd}
     * @param key {@link Key}
     * @return True if enabled,else false.
     */
    public boolean getEnabled(byte cmd, byte key) {
        return mPreferences.getBoolean("enabled" + cmd + String.valueOf(key), false);
    }

    /**
     * Enable or disable notifications of an application,this method has nothing to do with device,it just saves a flag
     * in local.
     *
     * @param packageName package name of a application
     * @param enabled     Whether to enable notification of the given package name
     */
    public void putPackage(String packageName, boolean enabled) {
        L.d("SmaManager putPackage -> " + packageName + ", " + enabled);
        mEditor.putBoolean(packageName + "isPush", enabled).apply();
    }

    /**
     * Return whether notifications of an application is enabled
     *
     * @param packageName package name of a application
     * @return True if enabled,else false.
     */
    public boolean getPackage(String packageName) {
        boolean isPush = mPreferences.getBoolean(packageName + "isPush", false);
        L.d("SmaManager getPackage -> " + packageName + ", " + isPush);
        return isPush;
    }

    /**
     * Set the times of vibration.
     *
     * @param type  the type of vibration,there is just only a type {@link SmaManager#VIBRATION_TYPE_ALARM} we have used
     *              for the
     *              time being
     * @param times the times of vibration you want to set
     */
    public void setVibrationTimes(int type, int times) {
        L.d("SmaManager setVibrationTimes -> " + type + ", " + times);
        mEditor.putInt(SmaConsts.SP_VIBRATION + type, times).apply();

        byte[] extra = new byte[2];
        extra[0] = (byte) type;
        extra[1] = (byte) times;

        write(Cmd.SET, Key.SET_VIBRATION, extra);
    }

    /**
     * Get the times of vibration
     *
     * @param type the type of vibration,there is only one type {@link SmaManager#VIBRATION_TYPE_ALARM} we have used for the
     *             time being
     * @return Times of vibration for given type.
     */
    public int getVibrationTimes(int type) {
        int times = mPreferences.getInt(SmaConsts.SP_VIBRATION + type, 4);
        L.d("SmaManager getVibrationTimes -> " + type + ", " + times);
        return times;
    }

    /**
     * Get back light time of the device.
     *
     * @return Amount of seconds the back light holds.
     */
    public int getBackLight() {
        int time = mPreferences.getInt(SmaConsts.SP_BACK_LIGHT, 2);
        L.d("SmaManager getBackLight -> " + time);
        return time;
    }

    private void checkTransfer() {
        if (!isLoggedIn) {
            if (mTransferTotal > 0 && mTransferCompleted > 0 && mTransferTotal > mTransferCompleted) {
                mTransferTotal = -1;
                mTransferCompleted = -1;
                for (SmaCallback callback : mSmaCallbacks) {
                    callback.onTransferBuffer(false, 0, 0);
                }
            }
        } else {
            if (mTransferTotal > 0 && mTransferCompleted > 0) {
                L.i("SmaManager onTransferBuffer -> true, mTransferTotal=" + mTransferTotal + ", mTransferCompleted=" +
                        mTransferCompleted);
                for (SmaCallback callback : mSmaCallbacks) {
                    callback.onTransferBuffer(true, mTransferTotal, mTransferCompleted);
                }
                if (mTransferTotal == mTransferCompleted) {
                    mTransferTotal = -1;
                    mTransferCompleted = -1;
                }
            }
        }
    }

    public void setAntiLostTel(String nickName, String tel) {
        if (TextUtils.isEmpty(nickName) || TextUtils.isEmpty(tel)) return;

        try {
            byte[] nicknames = nickName.getBytes("UTF-8");
            byte[] tels = tel.getBytes("UTF-8");

            byte[] extra = new byte[22 + tels.length];
            for (int i = 0, l = extra.length; i < l; i++) {
                if (i < 22) {
                    if (i < nicknames.length) {
                        extra[i] = nicknames[i];
                    }
                } else {
                    extra[i] = tels[i - 22];
                }
            }
            write(Cmd.SET, Key.SET_ANTI_LOST_TEL, extra);
            L.d("SmaManager setAntiLostTel -> " + nickName + ", " + tel);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * The UUIDs of {@link BluetoothGattService} and {@link BluetoothGattCharacteristic} are not the same in different
     * device,we
     * should select UUIDs according whether they are offered by a device.
     *
     * @param gatt GATT
     */
    private void selectUUID(BluetoothGatt gatt) {
        if (gatt == null) return;

        BluetoothGattService gattService = gatt.getService(UUID.fromString(UUID_SERVICE_MAIN_ROUND));
        if (gattService != null) {
            mUUIDMainService = UUID_SERVICE_MAIN_ROUND;
            mUUIDRead = UUID_CHARACTER_READ_ROUND;
            mUUIDWrite = UUID_CHARACTER_WRITE_ROUND;
        } else {
            mUUIDMainService = UUID_SERVICE_MAIN;
            mUUIDRead = UUID_CHARACTER_READ;
            mUUIDWrite = UUID_CHARACTER_WRITE;
        }
    }

    /**
     * If you will receive data asynchronous after you write data to the associated remote device with given {@link Cmd} and
     * {@link Key},you should return a ACK to the device to tell it that you have received the data it sent.This method
     * return
     * whether you need to return ACK when you write data to the associated remote device with given {@link Cmd} and
     * {@link Key}.
     *
     * @param cmd {@link Cmd}
     * @param key {@link Key}
     * @return True if need,else false.
     */
    public boolean isNeedReturnACK(byte cmd, byte key) {
        switch (cmd) {
            case Cmd.UPDATE:
                return key == Key.OTA || key == Key.INTO_XMODEM || key == Key.GET_WATCH_FACES;
            case Cmd.CONNECT:
                return key == Key.BIND || key == Key.LOGIN;
            case Cmd.SET:
                return key == Key.READ_ALARM || key == Key.READ_GOAL || key == Key.READ_BATTERY || key == Key.READ_VERSION ||
                        key == Key.READ_SEDENTARINESS;
            case Cmd.DATA:
                return key == Key.SPORT || key == Key.SPORT2 || key == Key.RATE || key == Key.SLEEP || key == Key.BLOOD_PRESSURE ||
                        key == Key.CYCLING || key == Key.EXERCISE2 || key == Key.TRACKER;
            default:
                return false;
        }
    }

    // 비동기식 http 통신
    public class HttpAsyncRequest extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public HttpAsyncRequest(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            //RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            //result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            result = httpRequest(url, values);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }

    // http 통신
    public String httpRequest(String _url, ContentValues _params){

        // HttpURLConnection 참조 변수.
        HttpURLConnection urlConn = null;
        // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();

        /**
         * 1. StringBuffer에 파라미터 연결
         * */
        // 보낼 데이터가 없으면 파라미터를 비운다.
        if (_params == null)
            sbParams.append("");
            // 보낼 데이터가 있으면 파라미터를 채운다.
        else {
            // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.
            boolean isAnd = false;
            // 파라미터 키와 값.
            String key;
            String value;

            for(Map.Entry<String, Object> parameter : _params.valueSet()){
                key = parameter.getKey();
                value = parameter.getValue().toString();

                // 파라미터가 두개 이상일때, 파라미터 사이에 &를 붙인다.
                if (isAnd)
                    sbParams.append("&");

                sbParams.append(key).append("=").append(value);

                // 파라미터가 2개 이상이면 isAnd를 true로 바꾸고 다음 루프부터 &를 붙인다.
                if (!isAnd)
                    if (_params.size() >= 2)
                        isAnd = true;
            }
        }

        /**
         * 2. HttpURLConnection을 통해 web의 데이터를 가져온다.
         * */
        try{
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();

            // [2-1]. urlConn 설정.
            urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
            urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            // [2-2]. parameter 전달 및 데이터 읽어오기.
            String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1&pw=123;
            BufferedReader reader;
            String line;
            String page;
            try (OutputStream os = urlConn.getOutputStream()) {
                os.write(strParams.getBytes("UTF-8")); // 출력 스트림에 출력.
                os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
                os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.
            }

            // [2-3]. 연결 요청 확인.
            // 실패 시 null을 리턴하고 메서드를 종료.
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            // [2-4]. 읽어온 결과물 리턴.
            // 요청한 URL의 출력물을 BufferedReader로 받는다.
            reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            // 출력물의 라인과 그 합에 대한 변수.
            page = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null){
                page += line;
            }

            return page;

        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }

        return null;

    }
}
