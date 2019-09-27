package com.bestmafen.smablelib.server;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.RemoteController;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.KeyEvent;

import com.bestmafen.easeblelib.util.EaseUtils;
import com.bestmafen.easeblelib.util.L;
import com.bestmafen.smablelib.component.SmaManager;
import com.bestmafen.smablelib.server.constants.music.MusicCommand;
import com.bestmafen.smablelib.server.constants.music.MusicEntity;
import com.bestmafen.smablelib.server.constants.music.PlayerAttribute;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Created by xiaokai on 2018/3/31.
 * Ble服务端，用来实现音乐控制和通知推送
 * 音乐控制需要 android.permission.MEDIA_CONTENT_CONTROL 权限
 */
public class MyBleServer {
    private static final String TAG                     = "MyBleServer";
    private static final String UUID_MUSIC_SERVICE      = "89D3502B-0F36-433A-8EF4-C502AD55F8DC";
    private static final String UUID_MUSIC_CH_COMMAND   = "9B3C81D8-57B1-4A8A-B8DF-0E56F7CA51C2";
    private static final String UUID_MUSIC_CH_UPDATE    = "2F7CABCE-808D-411F-9A0C-BB92BA96C102";
    private static final String UUID_MUSIC_CH_ATTRIBUTE = "C6B2F38C-23AB-46D8-A6AB-A3A870BBD5D7";

    private static String getUuidText(UUID uuid) {
        if (uuid == null) return "UUID=null";

        return getUuidText(uuid.toString());
    }

    private static String getUuidText(String uuid) {
        if (uuid == null) return "UUID=null";

        if (uuid.equalsIgnoreCase(UUID_MUSIC_SERVICE)) {
            return "MUSIC_SERVICE";
        } else if (uuid.equalsIgnoreCase(UUID_MUSIC_CH_COMMAND)) {
            return "MUSIC_CH_COMMAND";
        } else if (uuid.equalsIgnoreCase(UUID_MUSIC_CH_UPDATE)) {
            return "MUSIC_CH_UPDATE";
        } else if (uuid.equalsIgnoreCase(UUID_MUSIC_CH_ATTRIBUTE)) {
            return "MUSIC_CH_ATTRIBUTE";
        }

        return "UNKNOWN";
    }

    private Handler mHandler = new Handler();

    private Context           mContext;
    private BroadcastReceiver mReceiver;
    private boolean           isInited = false;

    private OnPlayClickListener mPlayClickListener;

    //    private boolean isAdvertise = false;
//
//    private BluetoothLeAdvertiser mLeAdvertiser;
//    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
//
//        @Override
//        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
//            L.v(TAG, "startAdvertising -> onStartSuccess");
//        }
//
//        @Override
//        public void onStartFailure(int errorCode) {
//            L.v(TAG, "startAdvertising -> onStartFailure: errorCode=" + errorCode);
//        }
//    };

    private BluetoothGattServer         mBluetoothGattServer;
    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            L.d(TAG, "onConnectionStateChange " + device + " -> status=" + getStatusText(status)
                    + ", newState=" + getStateText(newState));
//            if (!TextUtils.equals(SmaManager.getInstance().getSavedAddress(), device.getAddress())) return;
//
//            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
//                mBluetoothDevice = device;
//            } else {
//                mBluetoothDevice = null;
//            }
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            L.d(TAG, "onServiceAdded " + getUuidText(service.getUuid()) + " -> " + getStatusText(status));
            if (mSemaphore.availablePermits() == 0) {
                mSemaphore.release();
            }
        }

        @Override
        public void onCharacteristicReadRequest(
                BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            L.v(TAG, "onCharacteristicReadRequest " + getUuidText(characteristic.getUuid()));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic
                characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            if (value == null || value.length < 1) return;

            L.v(TAG, "onCharacteristicWriteRequest " + getUuidText(characteristic.getUuid()) + " -> "
                    + EaseUtils.byteArray2HexString(value));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
            if (UUID_MUSIC_CH_COMMAND.equalsIgnoreCase(characteristic.getUuid().toString())) {
                byte command = value[0];
                L.d(TAG, "receive music command=" + MusicCommand.getCommandText(command));
                switch (command) {
                    case MusicCommand.PLAY:
                        if (mPlayClickListener != null) {
                            mPlayClickListener.onPlay();
                        }
//                        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY);
                        break;
                    case MusicCommand.PAUSE:
                        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PAUSE);
                        break;
                    case MusicCommand.TOGGLE:
                        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                        break;
                    case MusicCommand.NEXT:
                        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
                        break;
                    case MusicCommand.PRE:
                        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                        break;
                    case MusicCommand.VOLUME_UP:
                        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager
                                .FX_KEYPRESS_STANDARD);
                        updateVolume();
                        break;
                    case MusicCommand.VOLUME_DOWN:
                        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager
                                .FX_KEYPRESS_STANDARD);
                        updateVolume();
                        break;
                }
            } else if (UUID_MUSIC_CH_UPDATE.equalsIgnoreCase(characteristic.getUuid().toString())) {
                L.d(TAG, "receive music subscribe=" + MusicEntity.getEntityAndAttrs(value));
                if (value[0] == MusicEntity.TRACK) {
                    reset();
                }
                mUpdateMap.put(value[0], Arrays.copyOfRange(value, 1, value.length));
                if (value[0] == MusicEntity.PLAYER) {
                    updateVolume();
                }
            } else if (UUID_MUSIC_CH_ATTRIBUTE.equalsIgnoreCase(characteristic.getUuid().toString())) {
                L.d(TAG, "retrieve extended attribute=" + MusicEntity.getEntityAndAttrs(value));
            }
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            L.v(TAG, "onDescriptorReadRequest -> " + getUuidText(descriptor.getCharacteristic().getUuid()));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, descriptor.getValue());
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor
                descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            L.d(TAG, "onDescriptorWriteRequest -> " + getUuidText(descriptor.getCharacteristic().getUuid()));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {

        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            L.v(TAG, "onNotificationSent -> " + getStatusText(status));
            if (mSemaphore.availablePermits() == 0) {
                mSemaphore.release();
            }
        }
    };

    private LinkedBlockingQueue<NotifyTask> mGattTasks = new LinkedBlockingQueue<>();
    /**
     * 通知和添加服务时，必须一个一个执行
     */
    private Semaphore                       mSemaphore = new Semaphore(1);

    private ArrayMap<Byte, byte[]> mUpdateMap    = new ArrayMap<>();
    private StringBuffer           mUpdateBuffer = new StringBuffer("");
    private String                 mLastVolume   = "";

    private RemoteController mRemoteController;
    private AudioManager     mAudioManager;

    private MyBleServer() {

    }

    public static synchronized MyBleServer getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak") private static MyBleServer sInstance = new MyBleServer();
    }

    public void init(Context context/*, final boolean advertise*/) {
        if (!isInited) {
            L.v(TAG, "MyBleServer -> init");
            this.isInited = true;
            this.mContext = context;
//            this.isAdvertise = advertise;

            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                createServer();
            }

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction("android.media.VOLUME_CHANGED_ACTION");
            mContext.registerReceiver(mReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (TextUtils.equals(intent.getAction(), BluetoothAdapter.ACTION_STATE_CHANGED)) {
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                        switch (state) {
                            case BluetoothAdapter.STATE_ON:
                                createServer();
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                closeSever();
                                break;
                        }
                    } else if (TextUtils.equals(intent.getAction(), "android.media.VOLUME_CHANGED_ACTION")) {
                        updateVolume();
                    }
                }
            }, filter);

            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (isInited) {
                        try {
                            NotifyTask task = mGattTasks.take();
                            task.handleTask();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    public void exit() {
        if (isInited) {
            isInited = false;
            mContext.unregisterReceiver(mReceiver);
        }
    }

    public void setRemoteController(RemoteController controller) {
        this.mRemoteController = controller;
    }

    public boolean isNotificationListenerEnabled() {
        if (!isInited) return false;

        String list = Settings.Secure.getString(
                mContext.getContentResolver(), "enabled_notification_listeners");
        return !TextUtils.isEmpty(list) && list
                .contains(mContext.getPackageName() + "/" + MyNotificationService.class.getName());
    }

    public void enableNotificationListener(Activity activity) {
        activity.startActivity(new Intent("android.settings" + ".ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }

    /**
     * Should be delayed,otherwise the mBluetoothGattServer is null.The reason is in BluetoothGattServer#openGattServer(),
     * IBluetoothGatt iGatt = managerService.getBluetoothGatt();
     * if (iGatt == null) {
     * Log.e(TAG, "Fail to get GATT Server connection");
     * return null;
     * }
     */
    private void createServer() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                L.v(TAG, "MyBleServer -> createServer");
//        if (isAdvertise) {
//            mLeAdvertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
//            if (mLeAdvertiser != null) {
//                AdvertiseSettings settings = new AdvertiseSettings.Builder()
//                        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
//                        .setConnectable(true)
//                        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
//                        .build();
//                AdvertiseData data = new AdvertiseData.Builder()
//                        .setIncludeDeviceName(true)
//                        .addServiceUuid(ParcelUuid.fromString(UUID_MUSIC_SERVICE))
////                .addServiceData(ParcelUuid.fromString(UUID_MUSIC_SERVICE), new byte[]{0x22, 0x22})
////                .addManufacturerData(0x0059, new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff})
////                .setIncludeTxPowerLevel(true)
//                        .build();
//                mLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
//            }
//        }
                reset();
                openGattServer();
                addServices();
            }
        }, 1200);
    }

    private void openGattServer() {
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) return;

        mBluetoothGattServer = bluetoothManager.openGattServer(mContext, mGattServerCallback);
    }

    private void addServices() {
        if (mBluetoothGattServer == null) return;

        BluetoothGattService bluetoothGattService;
        BluetoothGattCharacteristic characteristic;
        //以下是音乐控制相关
        bluetoothGattService = new BluetoothGattService(UUID.fromString(UUID_MUSIC_SERVICE),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        characteristic = new BluetoothGattCharacteristic(UUID.fromString(UUID_MUSIC_CH_COMMAND),
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE | BluetoothGattCharacteristic
                        .PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE | BluetoothGattCharacteristic.PERMISSION_READ);
        characteristic.addDescriptor(new BluetoothGattDescriptor(UUID.fromString
                ("00002902-0000-1000-8000-00805f9b34fb"), BluetoothGattDescriptor.PERMISSION_WRITE));
        bluetoothGattService.addCharacteristic(characteristic);

        characteristic = new BluetoothGattCharacteristic(UUID.fromString(UUID_MUSIC_CH_UPDATE),
                BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE | BluetoothGattCharacteristic.PERMISSION_READ);
        characteristic.addDescriptor(new BluetoothGattDescriptor(UUID.fromString
                ("00002902-0000-1000-8000-00805f9b34fb"), BluetoothGattDescriptor.PERMISSION_WRITE));
        bluetoothGattService.addCharacteristic(characteristic);

        characteristic = new BluetoothGattCharacteristic(UUID.fromString(UUID_MUSIC_CH_ATTRIBUTE),
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE,
                BluetoothGattCharacteristic.PERMISSION_WRITE | BluetoothGattCharacteristic.PERMISSION_READ);
        bluetoothGattService.addCharacteristic(characteristic);
        addAddServiceTask(bluetoothGattService);
    }

    private void closeSever() {
//        if (isAdvertise) {
//            if (mLeAdvertiser != null) {
//                mLeAdvertiser.stopAdvertising(mAdvertiseCallback);
//            }
//        }
        reset();
        if (mBluetoothGattServer != null) {
            mBluetoothGattServer.clearServices();
            mBluetoothGattServer.close();
            mBluetoothGattServer = null;
        }
    }

    private void reset() {
        L.i(TAG, "MyBleServer -> reset");
        if (mSemaphore.availablePermits() == 0) {
            mSemaphore.release();
        }
        mGattTasks.clear();
        mUpdateMap.clear();
    }

    public void setPlayClickListener(OnPlayClickListener listener) {
        mPlayClickListener = listener;
    }

    public void whenMusicUpdate(byte entity, byte attr, Object... args) {
//            if (entity == MusicEntity.PLAYER && attr == PlayerAttribute.PLAYBACK_INFO) {
//                if ((byte) args[0] == PlayerAttribute.PlaybackState.PAUSED) {
//                    isPlaying = false;
//                } else if ((byte) args[0] == PlayerAttribute.PlaybackState.PLAYING) {
//                    isPlaying = true;
//                }
//            }

        if (!filterMusicUpdate(entity, attr)) {
            L.w("whenMusicUpdate not subscribed -> " + MusicEntity.getEntityText(entity) + ", "
                    + MusicEntity.getAttrText(entity, attr));
            return;
        }

        if (args == null || args.length < 1) return;

        if (args.length == 1) {
            if (args[0] instanceof String) {
                notifyMusicChange(entity, attr, (String) args[0]);
            } else {
                notifyMusicChange(entity, attr, String.valueOf(args[0]));
            }
        } else {
            mUpdateBuffer.delete(0, mUpdateBuffer.length());
            for (Object arg : args) {
                mUpdateBuffer.append(arg).append(",");
            }
            mUpdateBuffer.deleteCharAt(mUpdateBuffer.length() - 1);
            notifyMusicChange(entity, attr, mUpdateBuffer.toString());
        }
    }

    /**
     * 过滤未订阅的update
     *
     * @param entity entity
     * @param attr   attr
     * @return true已订阅；false未订阅
     */
    private boolean filterMusicUpdate(byte entity, byte attr) {
        byte[] update = mUpdateMap.get(entity);
        //订阅的时候发送过来的是按升序排列的 [0x02, 0x00, 0x02, 0x03]，所以能使用二分查找
        return update != null && Arrays.binarySearch(update, attr) > -1;
    }

    private void notifyMusicChange(byte entity, byte attr, String value) {
        try {
            if (TextUtils.isEmpty(value)) return;

            L.v(TAG, "notifyMusicChange -> " + MusicEntity.getEntityText(entity) + ", " + MusicEntity.getAttrText(entity, attr)
                    + ", " + value);
            byte[] data = value.getBytes("UTF-8");
            int length = data.length;
            byte[] packet1;
            byte[] packet2;
            if (length <= 17) {
                packet1 = new byte[]{entity, attr, 0};
                packet2 = Arrays.copyOfRange(data, 0, data.length);
            } else {
                packet1 = new byte[]{entity, attr, 1};
                packet2 = Arrays.copyOfRange(data, 0, 17);
                BluetoothGattService service = mBluetoothGattServer.getService(UUID.fromString(UUID_MUSIC_SERVICE));
                if (service == null) return;

                BluetoothGattCharacteristic characteristic =
                        service.getCharacteristic(UUID.fromString(UUID_MUSIC_CH_ATTRIBUTE));
                if (characteristic == null) return;

                characteristic.setValue(Arrays.copyOfRange(data, 0, data.length));
            }
            addNotifyTask(UUID_MUSIC_SERVICE, UUID_MUSIC_CH_UPDATE, EaseUtils.concat(packet1, packet2));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void sendMediaKeyEvent(int keyCode) {
        if (mRemoteController == null) return;

        mRemoteController.sendMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
        mRemoteController.sendMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
    }

    private void updateVolume() {
        if (mAudioManager != null) {
            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            String value = String.format(Locale.getDefault(), "%.2f", (float) volume / max);
            if (TextUtils.equals(mLastVolume, value)) return;

            whenMusicUpdate(MusicEntity.PLAYER, PlayerAttribute.VOLUME, value);
            mLastVolume = value;
        }
    }

    private void addAddServiceTask(BluetoothGattService service) {
        if (mBluetoothGattServer == null || service == null) return;

        try {
            L.v(TAG, "addAddServiceTask -> " + getUuidText(service.getUuid()));
            mGattTasks.put(new NotifyTask(TASK_ADD_SERVICE, service, null, null));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addNotifyTask(String serviceUUID, String characteristicUUDI, byte[] value) {
        try {
            if (!SmaManager.getInstance().isLoggedIn()) return;

            if (value == null || value.length < 1) return;

            if (mBluetoothGattServer == null) return;

            BluetoothGattService service = mBluetoothGattServer.getService(UUID.fromString(serviceUUID));
            if (service == null) return;

            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(characteristicUUDI));
            if (characteristic == null) return;

            L.v(TAG, "addNotifyTask -> " + getUuidText(serviceUUID) + ", " + getUuidText(characteristicUUDI));
            mGattTasks.put(new NotifyTask(TASK_NOTIFY_CHANGE, service, characteristic, value));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final int TASK_ADD_SERVICE   = 0;
    private static final int TASK_NOTIFY_CHANGE = 1;

    @Retention(RetentionPolicy.SOURCE)
    @interface TaskType {
    }

    class NotifyTask {
        int                         mType;
        BluetoothGattService        mGattService;
        BluetoothGattCharacteristic mGattCharacteristic;
        byte[]                      mValue;

        NotifyTask(@TaskType int type, BluetoothGattService gattService, BluetoothGattCharacteristic gattCharacteristic,
                   byte[] value) {
            mType = type;
            mGattService = gattService;
            mGattCharacteristic = gattCharacteristic;
            mValue = value;
        }

        void handleTask() {
            try {
                if (mBluetoothGattServer == null) return;

                if (mType == TASK_NOTIFY_CHANGE) {
                    if (mValue == null || mValue.length < 1) return;

                    if (mGattService == null || mGattCharacteristic == null) return;

                    String address = SmaManager.getInstance().getSavedAddress();
                    if (TextUtils.isEmpty(address)) return;

                    mGattCharacteristic.setValue(mValue);
                    mSemaphore.acquire();
                    BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
                    if (!mBluetoothGattServer.notifyCharacteristicChanged(device, mGattCharacteristic, false)) {
//                        L.w(TAG, "handleTask TASK_NOTIFY_CHANGE " + getUuidText(mGattCharacteristic.getUuid()) + ", "
//                                + EaseUtils.byteArray2HexString(mValue) + " -> false");
                        L.d(TAG, "handleTask TASK_NOTIFY_CHANGE -> false");
                        mSemaphore.release();
                    } else {
//                        L.v(TAG, "handleTask TASK_NOTIFY_CHANGE " + getUuidText(mGattCharacteristic.getUuid()) + ", "
//                                + EaseUtils.byteArray2HexString(mValue) + " -> ok");
                        L.d(TAG, "handleTask TASK_NOTIFY_CHANGE -> ok");
                    }
                } else {
                    SystemClock.sleep(1000);
                    mSemaphore.acquire();
                    if (mBluetoothGattServer != null && !mBluetoothGattServer.addService(mGattService)) {
                        L.w(TAG, "handleTask TASK_ADD_SERVICE " + getUuidText(mGattService.getUuid()) + " -> false");
                        mSemaphore.release();
                    } else {
                        L.v(TAG, "handleTask TASK_ADD_SERVICE " + getUuidText(mGattService.getUuid()) + " -> ok");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getStatusText(int status) {
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:
                return "GATT_SUCCESS";
            case BluetoothGatt.GATT_FAILURE:
                return "GATT_FAILURE";
            default:
                return "OTHERS";
        }
    }

    private static String getStateText(int state) {
        switch (state) {
            case BluetoothProfile.STATE_CONNECTED:
                return "STATE_CONNECTED";
            case BluetoothProfile.STATE_DISCONNECTED:
                return "STATE_DISCONNECTED";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "STATE_DISCONNECTING";
            default:
                return "UNKNOWN";
        }
    }

    public interface OnPlayClickListener {

        void onPlay();
    }
}
