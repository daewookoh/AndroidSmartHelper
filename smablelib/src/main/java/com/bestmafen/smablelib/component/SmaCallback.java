package com.bestmafen.smablelib.component;

import android.bluetooth.BluetoothDevice;

import com.bestmafen.smablelib.entity.SmaAlarm;
import com.bestmafen.smablelib.entity.SmaBloodPressure;
import com.bestmafen.smablelib.entity.SmaCycling;
import com.bestmafen.smablelib.entity.SmaExercise;
import com.bestmafen.smablelib.entity.SmaHeartRate;
import com.bestmafen.smablelib.entity.SmaSedentarinessSettings;
import com.bestmafen.smablelib.entity.SmaSleep;
import com.bestmafen.smablelib.entity.SmaSport;
import com.bestmafen.smablelib.entity.SmaTracker;

import java.util.List;

/**
 * This class is used to receive the data from a Bluetooth device,but we use {@link SimpleSmaCallback} replaced in normal
 * conditions.
 */
public interface SmaCallback {

    /**
     * This method will be invoked when a remote device gets connected.
     *
     * @param device the remote device which gets connected
     */
    void onDeviceConnected(BluetoothDevice device);
//
//    void onBind(boolean ok);

    /**
     * This method will be invoked when a remote device gets logged in or disconnected.We prefer to use this method to
     * get whether
     * the device is connected really rather than {@link SmaCallback#onDeviceConnected(BluetoothDevice)} as that you can't
     * exchange data with a device util it has been logged in though it gets connected.
     *
     * @param ok true if gets logged in,false if gets disconnected
     */
    void onLogin(boolean ok);

    /**
     * This method will be invoked when we get alarms of a remote device
     *
     * @param alarms alarms
     */
    void onReadAlarm(List<SmaAlarm> alarms);

    /**
     * This method will be invoked when we get how much power of a remote device is left.
     *
     * @param battery the power left,range is 0~100
     */
    void onReadBattery(int battery);

    /**
     * This method will be invoked when we get the firmware version of a remote device.
     *
     * @param firmware   the firmware we get
     * @param bleVersion useless
     */
    void onReadVersion(String firmware, String bleVersion);

    void onReadFlag(String flag);

    /**
     * This method will be invoked when the device has entered into the x-mode to transfer data.
     *
     * @param ok whether the device has entered into the x-mode to transfer data
     */
//    void onIntoTransfer(boolean ok);

    /**
     * This method will be invoked when the device has received the id of watch face to be replaced.
     */
//    void onReceivedTransferAction();

    void onTransferBuffer(boolean status, int total, int completed);

    /**
     * This method will be invoked when we get the ids of watch face within the device.
     *
     * @param count the  watch face count within the device
     * @param ids   the ids of the watch face within the device
     */
    void onReadWatchFaces(int count, long[] ids);

    /**
     * This method will be invoked when get sport data form device.
     *
     * @param list the sport data we get
     */
    void onReadSportData(List<SmaSport> list);

    /**
     * This method will be invoked when get sleep data form device.
     *
     * @param list the sleep data we get
     */
    void onReadSleepData(List<SmaSleep> list);

    /**
     * This method will be invoked when get heart rate data form device.
     *
     * @param list the heart rate data we get
     */
    void onReadHeartRateData(List<SmaHeartRate> list);

    /**
     * This method will be invoked when get blood pressure data form device.
     *
     * @param list the blood pressure data we get
     */
    void onReadBloodPressure(List<SmaBloodPressure> list);

    void onReadCycling(List<SmaCycling> list);

    void onReadExercise(List<SmaExercise> list);

    void onReadTracker(List<SmaTracker> list);

    /**
     * This method will be invoked when all the data in device has been read.
     *
     * @param ok whether all the data has been read successfully
     */
    void onReadDataFinished(boolean ok);

    /**
     * This method will be invoked when the device has entered into the mode of DFU
     *
     * @param ok whether the device has entered into the mode of DFU successfully
     */
    void onOTA(boolean ok);

    /**
     * This method will be invoked when a button on the device has been pressed.You can take a photo or hang up your
     * phone when you
     * receive this event.
     *
     * @param key which button pressed
     */
    void onKeyDown(byte key);

    /**
     * This method will be invoked when the device has entered into or exit the mode of exercise.
     *
     * @param start true if enter into,false if exit
     */
    void onStartExercise(boolean start);

    void onCycling(int status);

    /**
     * This method will be invoked when the a alarm of a remote device has been changed.
     */
    void onAlarmsChange();

    /**
     * This method will be invoked when the the goal of a remote device has been changed.
     */
    void onGoalChange();

    /**
     * This method will be invoked when we get the goal of a remote device.
     *
     * @param goal the goal we get
     */
    void onReadGoal(int goal);

    /**
     * This method will be invoked when the the sedentariness of a remote device has been changed.
     */
    void onSedentarinessChange();

    /**
     * This method will be invoked when we get the sedentariness of a remote device.
     *
     * @param sedentariness the sedentariness we get,if both {@link SmaSedentarinessSettings#start1} and
     *                      {@link SmaSedentarinessSettings#end1} are equal or greater than 24,they have no meaning in
     *                      themselves,that just means {@link SmaSedentarinessSettings#enabled1} is 0,the same as
     *                      {@link SmaSedentarinessSettings#start2} and {@link SmaSedentarinessSettings#end2}.
     */
    void onReadSedentariness(SmaSedentarinessSettings sedentariness);

    /**
     * This method will be invoked when a remote device finding you phone.
     *
     * @param start start finding if true,stop finding if false
     */
    void onFindPhone(boolean start);

    /**
     * This method will be invoked when the camera icon is pressed.
     */
    void onTakePhoto();

    void onDeviceCommonRequest(int flag);
}
