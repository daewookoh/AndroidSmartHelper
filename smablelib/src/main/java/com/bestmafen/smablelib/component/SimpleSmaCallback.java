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
 * This class provides empty implementations of the methods from {@link SmaCallback}.Override this if you only care about
 * a few of the available callback methods.
 */
public class SimpleSmaCallback implements SmaCallback {

    @Override
    public void onDeviceConnected(BluetoothDevice device) {

    }
//
//    @Override
//    public void onBind(boolean ok) {
//
//    }

    @Override
    public void onLogin(boolean ok) {

    }

    @Override
    public void onReadAlarm(List<SmaAlarm> alarms) {

    }

    @Override
    public void onReadBattery(int battery) {

    }

    @Override
    public void onReadVersion(String firmware, String bleVersion) {

    }

    @Override
    public void onReadFlag(String flag) {

    }

//    @Override
//    public void onIntoTransfer(boolean ok) {
//
//    }

//    @Override
//    public void onReceivedTransferAction() {
//
//    }

    @Override
    public void onTransferBuffer(boolean status, int total, int completed) {

    }

    @Override
    public void onReadWatchFaces(int count, long[] ids) {

    }

    @Override
    public void onReadSportData(List<SmaSport> list) {

    }

    @Override
    public void onReadSleepData(List<SmaSleep> list) {

    }

    @Override
    public void onReadHeartRateData(List<SmaHeartRate> list) {

    }

    @Override
    public void onReadBloodPressure(List<SmaBloodPressure> list) {

    }

    @Override
    public void onReadCycling(List<SmaCycling> list) {

    }

    @Override
    public void onReadExercise(List<SmaExercise> list) {
    }

    @Override
    public void onReadTracker(List<SmaTracker> list) {
    }

    @Override
    public void onReadDataFinished(boolean ok) {

    }

    @Override
    public void onOTA(boolean ok) {

    }

    @Override
    public void onKeyDown(byte key) {

    }

    @Override
    public void onStartExercise(boolean start) {

    }

    @Override
    public void onCycling(int status) {
    }

    @Override
    public void onAlarmsChange() {

    }

    @Override
    public void onGoalChange() {

    }

    @Override
    public void onReadGoal(int goal) {

    }

    @Override
    public void onSedentarinessChange() {

    }

    @Override
    public void onReadSedentariness(SmaSedentarinessSettings sedentariness) {

    }

    @Override
    public void onFindPhone(boolean start) {

    }

    @Override
    public void onTakePhoto() {

    }

    @Override
    public void onDeviceCommonRequest(int flag) {

    }
}
