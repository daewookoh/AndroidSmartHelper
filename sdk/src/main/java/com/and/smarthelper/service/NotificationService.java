package com.and.smarthelper.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.and.smarthelper.application.MyApplication;
import com.bestmafen.smablelib.component.SmaManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotificationService extends NotificationListenerService {
    Context context;
    MyApplication common = new MyApplication(this);

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.i("NotificationService","onCreate");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String package_name = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = extras.getString("android.text");
        Log.i("Package",package_name);

        if(title==null || text==null)
        {
            // Do Nothing
        }
        else {
            Log.i("Title", title);
            Log.i("Text", text);
            Intent msgrcv = new Intent("Msg");
            msgrcv.putExtra("package", package_name);
            msgrcv.putExtra("title", title);
            msgrcv.putExtra("text", text);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);

            String allowed_app_list = common.getSP("allowed_app_list", "EMPTY");

            if (allowed_app_list.contains("/"+package_name+"/")) {
                SmaManager.getInstance().write(SmaManager.Cmd.NOTICE, SmaManager.Key.MESSAGE_v2, title, text);
            }

        }
    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");
    }
}
