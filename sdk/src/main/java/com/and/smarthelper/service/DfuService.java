package com.and.smarthelper.service;

import android.app.Activity;

import com.and.smarthelper.activity.DfuNotificationActivity;

import no.nordicsemi.android.dfu.DfuBaseService;

public class DfuService extends DfuBaseService {
    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return DfuNotificationActivity.class;
    }
}