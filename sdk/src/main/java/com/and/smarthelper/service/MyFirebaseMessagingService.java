package com.and.smarthelper.service;

import android.app.LauncherActivity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.and.smarthelper.R;
import com.and.smarthelper.application.MyApplication;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "TTT";
    MyApplication common = new MyApplication(this);

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        int channel_id = 1;

        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        long start_time = System.currentTimeMillis();
        common.log(Long.toString(start_time));

        Map<String, String> data = remoteMessage.getData();

        //you can get your text message here.
        String body= data.get("body");
        String title= data.get("title");
        String sound= data.get("sound");

        if(!TextUtils.isEmpty(sound) && sound.equals("ring.mp3"))
        {
            channel_id = 2;
        }

        sendNotification(title, body, channel_id);

    }
    // [END receive_message]

    private void sendNotification(String title, String body, int channel_id) {
        Intent intent = new Intent(this, LauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = String.valueOf(channel_id);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri sound2 = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring);

        if(channel_id==2){
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring);
        }


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(sound)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "PUSH 알림", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);

            NotificationChannel channel2 = new NotificationChannel("2", "간헐적단식", NotificationManager.IMPORTANCE_HIGH);
            channel2.setSound(sound2,null);
            notificationManager.createNotificationChannel(channel2);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
