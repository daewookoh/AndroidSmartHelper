package com.bestmafen.smablelib.component;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Build;
import android.os.SystemClock;

import com.bestmafen.easeblelib.util.EaseUtils;
import com.bestmafen.easeblelib.util.L;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * This class is used to communication with Bluetooth device.
 */
public class SmaMessenger {
    /**
     * The queue holds normal messages,such as reading a characteristic,writing a characteristic and enabling or disabling a
     * notification.
     */
    private LinkedBlockingQueue<SmaMessage> mMessages = new LinkedBlockingQueue<>();

    /**
     * The semaphore to keep every small package of data sent to device is processed sequentially.
     */
    private Semaphore mPackageSemaphore = new Semaphore(1);

    /**
     * The semaphore to keep every whole command sent to device is processed sequentially.
     */
    private Semaphore mReceiveACKSemaphore = new Semaphore(1);

    /**
     * The queue holds messages of ACK,see {@link SmaManager#isNeedReturnACK(byte, byte)}
     */
    private LinkedBlockingQueue<SmaMessage> mACKs = new LinkedBlockingQueue<>();

    /**
     * The semaphore to keep every ACK sent to device is processed sequentially.
     */
    private Semaphore mReturnACKSemaphore = new Semaphore(0);

    private          boolean mNeedReturnAck = false;
    private volatile boolean isExit         = false;

    public int mMtu = 20;

    /**
     * The type of messages.
     */
    public enum MessageType {
        /**
         * to write a characteristic
         */
        WRITE,

        /**
         * to read a characteristic
         */
        READ,

        /**
         * to enable or disable a notification
         */
        NOTIFY,

        /**
         * request maximum transmission unit
         */
        REQUEST_MTU,

        /**
         * request connection priority
         */
        REQUEST_CONNECTION_PRIORITY
    }

    SmaMessenger() {
        /**
         * The thread used to process normal messages,it will be blocking when {@link SmaMessenger#mMessages} gets empty.
         */
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (!isExit) {
                        SmaMessage message = mMessages.take();
                        L.v("SmaMessenger take -> " + message.toString());
                        boolean ok;

                        switch (message.mType) {
                            case READ:
                                L.v("SmaMessenger acquire mPackageSemaphore -> " + mPackageSemaphore.availablePermits());
                                mPackageSemaphore.acquire();
                                Thread.sleep(1000);//MTK平台读特征值时，不延迟的话会读到错误的数据，固件那边解决不了，只能加上延迟
                                if (message.mGatt == null) {
                                    L.w("SmaMessenger READ message.mGatt == null");
                                    releasePackageSemaphore();
                                    continue;
                                }

                                if (message.mCharacteristic == null) {
                                    L.w("SmaMessenger READ message.mCharacteristic == null");
                                    releasePackageSemaphore();
                                    continue;
                                }

                                ok = message.mGatt.readCharacteristic(message.mCharacteristic);
                                if (!ok) {
                                    L.w("SmaMessenger READ false");
                                    releasePackageSemaphore();
                                }
                                break;
                            case NOTIFY:
                                L.v("SmaMessenger acquire mPackageSemaphore -> " + mPackageSemaphore.availablePermits());
                                mPackageSemaphore.acquire();
                                if (message.mGatt == null) {
                                    L.w("SmaMessenger NOTIFY message.mGatt == null");
                                    releasePackageSemaphore();
                                    continue;
                                }

                                if (message.mCharacteristic == null) {
                                    L.w("SmaMessenger NOTIFY message.mCharacteristic == null");
                                    releasePackageSemaphore();
                                    continue;
                                }

                                BluetoothGattDescriptor descriptor = message.mCharacteristic.getDescriptor(UUID.fromString
                                        ("00002902-0000-1000-8000-00805f9b34fb"));
                                if (descriptor == null) {
                                    L.w("SmaMessenger NOTIFY message.descriptor == null");
                                    releasePackageSemaphore();
                                    continue;
                                }

                                message.mGatt.setCharacteristicNotification(message.mCharacteristic, message.mData != null);
                                descriptor.setValue(message.mData != null ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                        : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                                ok = message.mGatt.writeDescriptor(descriptor);
                                if (!ok) {
                                    L.w("SmaMessenger NOTIFY false");
                                    releasePackageSemaphore();
                                } else {
                                    L.v("SmaMessenger NOTIFY true");
                                }
                                break;
                            case WRITE:
                                boolean acquireReturnACKSemaphore = false;
                                if (message.isNeedReceiveACK) {
                                    if (mNeedReturnAck) {
                                        L.v("SmaMessenger acquire mReturnACKSemaphore -> " + mReturnACKSemaphore.availablePermits
                                                ());
                                        acquireReturnACKSemaphore = true;
                                        mReturnACKSemaphore.acquire();
                                    }
                                    mNeedReturnAck = message.isNeedReturnACK;

                                    L.v("SmaMessenger acquire mReceiveACKSemaphore -> " + mReceiveACKSemaphore.availablePermits());
                                    mReceiveACKSemaphore.acquire();
                                }
                                L.v("SmaMessenger acquire mPackageSemaphore -> " + mPackageSemaphore.availablePermits());
                                mPackageSemaphore.acquire();
                                if (message.mGatt == null) {
                                    L.w("SmaMessenger WRITE message.mGatt == null");
                                    releasePackageSemaphore();
                                    if (message.isNeedReceiveACK) {
                                        releaseReceiveSemaphore();
                                    }
                                    if (acquireReturnACKSemaphore) {
                                        releaseReturnSemaphore();
                                    }
                                    continue;
                                }

                                if (message.mCharacteristic == null) {
                                    L.w("SmaMessenger WRITE message.mCharacteristic == null");
                                    releasePackageSemaphore();
                                    if (message.isNeedReceiveACK) {
                                        releaseReceiveSemaphore();
                                    }
                                    if (acquireReturnACKSemaphore) {
                                        releaseReturnSemaphore();
                                    }
                                    continue;
                                }

                                message.mCharacteristic.setValue(message.mData);
                                ok = message.mGatt.writeCharacteristic(message.mCharacteristic);
                                if (!ok) {
                                    L.w("SmaMessenger WRITE false");
                                    releasePackageSemaphore();
                                    if (message.isNeedReceiveACK) {
                                        releaseReceiveSemaphore();
                                    }
                                    if (acquireReturnACKSemaphore) {
                                        releaseReturnSemaphore();
                                    }
                                } else {
                                    //xmodem退出后，要稍等一下才能发命令不然会卡住
                                    if (message.mData != null && message.mData.length == 1 && message.mData[0] == SmaManager.Key
                                            .XMODEM_EXIT) {
                                        SystemClock.sleep(1200);
                                    }
                                }
                                break;
                            case REQUEST_MTU:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    L.v("SmaMessenger acquire mPackageSemaphore -> " + mPackageSemaphore.availablePermits());
                                    mPackageSemaphore.acquire();
                                    if (message.mGatt == null) {
                                        L.w("SmaMessenger REQUEST_MTU message.mGatt == null");
                                        releasePackageSemaphore();
                                        continue;
                                    }

                                    int mtu = ((message.mData[0] & 0xff) << 8) | (message.mData[1] & 0xff);
                                    ok = message.mGatt.requestMtu(mtu);
                                    L.v("SmaMessenger REQUEST_MTU " + mtu + " -> " + ok);
                                    if (!ok) {
                                        L.w("SmaMessenger REQUEST_MTU false");
                                        releasePackageSemaphore();
                                    }
                                }
                                break;
                            case REQUEST_CONNECTION_PRIORITY:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    if (message.mGatt == null) {
                                        L.w("SmaMessenger REQUEST_CONNECTION_PRIORITY message.mGatt == null");
                                        continue;
                                    }

                                    boolean enabled = message.mData != null;
                                    L.v("SmaMessenger REQUEST_CONNECTION_PRIORITY -> " + enabled);
                                    message.mGatt.requestConnectionPriority(enabled ? BluetoothGatt
                                            .CONNECTION_PRIORITY_HIGH : BluetoothGatt.CONNECTION_PRIORITY_BALANCED);
                                }
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    L.v("SmaMessenger Exception -> " + e.toString());
                }
            }
        }, "messenger_msg").start();

        /**
         * The thread used to write ACK ,it will be blocking when {@link SmaMessenger#mACKs} gets empty.
         */
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (!isExit) {
                        SmaMessage message = mACKs.take();
                        L.v("SmaMessenger ack acquire mPackageSemaphore -> " + mPackageSemaphore.availablePermits());
                        mPackageSemaphore.acquire();
                        L.v("SmaMessenger ack take -> " + message.toString());
                        if (message.mGatt == null) {
                            L.w("SmaMessenger ack message.mGatt == null");
                            releasePackageSemaphore();
                            continue;
                        }

                        if (message.mCharacteristic == null) {
                            L.w("SmaMessenger ack message.mCharacteristic == null");
                            releasePackageSemaphore();
                            continue;
                        }

                        message.mCharacteristic.setValue(message.mData);
                        boolean ok = message.mGatt.writeCharacteristic(message.mCharacteristic);
                        if (!ok) {
                            L.w("SmaMessenger ack WRITE false");
                            releasePackageSemaphore();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    L.v("SmaMessenger ack Exception -> " + e.toString());
                }
            }
        }, "messenger_ack").start();
    }

    /**
     * Add a message to the queue {@link SmaMessenger#mMessages} or {@link SmaMessenger#mACKs}
     *
     * @param message the message to be added
     */
    synchronized void addMessage(SmaMessage message) {
        try {
            L.v("SmaMessenger -> addMessage >>>> " + message.toString());
            if (message.isACK) {
                mACKs.put(message);
//                L.v("SmaMessenger -> addMessage >>>> all isACK=" + mACKs.toString());
                return;
            }

            switch (message.mType) {
                case READ:
                case NOTIFY:
                case REQUEST_MTU:
                case REQUEST_CONNECTION_PRIORITY:
                    mMessages.put(message);
                    break;
                case WRITE:
                    byte[] data = message.mData;
                    if (data == null || data.length < 1) return;

                    int count = data.length % mMtu == 0 ? data.length / mMtu : data.length / mMtu + 1;
                    for (int i = 0; i < count; i++) { //拆分
                        byte[] packet;
                        if (i == count - 1) {
                            packet = Arrays.copyOfRange(data, i * mMtu, data.length);
                        } else {
                            packet = Arrays.copyOfRange(data, i * mMtu, (i + 1) * mMtu);
                        }

                        SmaMessage msg = new SmaMessage(message.mGatt, message.mCharacteristic, packet, SmaMessenger.MessageType
                                .WRITE);
                        if (i == 0) {
                            if (message.isNeedReturnACK) {
                                msg.isNeedReturnACK = true;
                            }
                            if (message.isNeedReceiveACK) {
                                msg.isNeedReceiveACK = true;
                            }
                        }

                        mMessages.put(msg);
                    }
                    break;
            }
//            L.v("SmaMessenger -> addMessage >>>> all mMessages=" + mMessages.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove all tasks in task queue.
     */
    synchronized void clearAllTask() {
        mNeedReturnAck = false;
        mMessages.clear();
        mACKs.clear();
        L.v("SmaMessenger clearAllTask 之前 -> " + mPackageSemaphore.availablePermits() + "," + mReceiveACKSemaphore.availablePermits
                () + "," + mReturnACKSemaphore.availablePermits());
        if (mPackageSemaphore.availablePermits() == 0) {
            mPackageSemaphore.release();
        }
        if (mReceiveACKSemaphore.availablePermits() == 0) {
            mReceiveACKSemaphore.release();
        }
        if (mReturnACKSemaphore.availablePermits() == 1) {
            try {
                mReturnACKSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        L.v("SmaMessenger clearAllTask 之后 -> " + mPackageSemaphore.availablePermits() + "," + mReceiveACKSemaphore.availablePermits
                () + "," + mReturnACKSemaphore.availablePermits());
    }

    void releasePackageSemaphore() {
        if (mPackageSemaphore.availablePermits() > 0) return;

        mPackageSemaphore.release();
        L.v("SmaMessenger -> release mPackageSemaphore " + mPackageSemaphore.availablePermits());
    }

    void releaseReceiveSemaphore() {
        if (mReceiveACKSemaphore.availablePermits() > 0) return;

        mReceiveACKSemaphore.release();
        L.v("SmaMessenger -> release mReceiveACKSemaphore " + mReceiveACKSemaphore.availablePermits());
    }

    void releaseReturnSemaphore() {
        if (mReturnACKSemaphore.availablePermits() > 0) return;

        mReturnACKSemaphore.release();
        L.v("SmaMessenger -> release mReturnACKSemaphore " + mReturnACKSemaphore.availablePermits());
    }

    void exit() {
        isExit = true;
    }

    static class SmaMessage {
        /**
         * GATT
         */
        BluetoothGatt mGatt;

        /**
         * characteristic
         */
        BluetoothGattCharacteristic mCharacteristic;

        /**
         * if type of the message is {@link MessageType#WRITE},this field is the data will be sent to device;
         * if type of the message is {@link MessageType#NOTIFY},enable if this is not null,disable if is null.
         * if type of the message is {@link MessageType#READ},this field is useless.
         */
        byte[] mData;

        MessageType mType;

        /**
         * see {@link SmaManager#isNeedReturnACK(byte, byte)}
         */
        boolean isNeedReturnACK;
        boolean isNeedReceiveACK;
        boolean isACK;

        SmaMessage(BluetoothGatt gatt, BluetoothGattCharacteristic bc, byte[] data, MessageType type, boolean isNeedReturnACK,
                   boolean isACK, boolean isNeedReceiveACK) {
            this.mGatt = gatt;
            this.mCharacteristic = bc;
            this.mData = data;
            this.mType = type;
            this.isNeedReturnACK = isNeedReturnACK;
            this.isACK = isACK;
            this.isNeedReceiveACK = isNeedReceiveACK;
        }

        SmaMessage(BluetoothGatt gatt, BluetoothGattCharacteristic bc, byte[] data, MessageType type, boolean isNeedReturnACK,
                   boolean isACK) {
            this(gatt, bc, data, type, isNeedReturnACK, isACK, false);
        }

        SmaMessage(BluetoothGatt gatt, BluetoothGattCharacteristic bc, byte[] data, MessageType type, boolean isNeedReturnACK) {
            this(gatt, bc, data, type, isNeedReturnACK, false);
        }

        SmaMessage(BluetoothGatt gatt, BluetoothGattCharacteristic bc, byte[] data, MessageType type) {
            this(gatt, bc, data, type, false);
        }

        @Override
        public String toString() {
            return "SmaMessage{" +
                    "mType=" + mType +
                    ", isNeedReceiveACK=" + isNeedReceiveACK +
                    ", isNeedReturnACK=" + isNeedReturnACK +
                    ", isACK=" + isACK +
                    ", mData=" + EaseUtils.byteArray2HexString(mData) +
                    '}';
        }
    }
}
