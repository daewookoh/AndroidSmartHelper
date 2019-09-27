package com.bestmafen.smablelib.server;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.media.RemoteController;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.bestmafen.easeblelib.util.L;
import com.bestmafen.smablelib.component.SmaManager;
import com.bestmafen.smablelib.server.constants.music.MusicEntity;
import com.bestmafen.smablelib.server.constants.music.PlayerAttribute;
import com.bestmafen.smablelib.server.constants.music.TrackAttribute;

import static java.security.AccessController.getContext;

/**
 * Created by xiaokai on 2018/3/31.
 * 用来音乐控制以及推送通知栏的消息，需要用户允许通知栏使用权限
 */
public class MyNotificationService extends NotificationListenerService
        implements RemoteController.OnClientUpdateListener {
    private boolean isContainBracket = false;

    private boolean mHasPlayer;
    private boolean mPlaying;
    private boolean mAutoPlay;

    /**
     * NotificationListenerService这个系统服务，只有在用户允许了通知栏使用权限之后，才会触发onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        RemoteController controller = new RemoteController(this, this);
        MyBleServer.getInstance().setRemoteController(controller);
        MyBleServer.getInstance().setPlayClickListener(new MyBleServer.OnPlayClickListener() {

            @Override
            public void onPlay() {
                if (mHasPlayer) {
                    if (!mPlaying) {
                        MyBleServer.getInstance().sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY);
                    }
                } else {
                    //startActivity(new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER));
                    Intent intent=Intent.makeMainSelectorActivity(Intent.ACTION_MAIN,
                            Intent.CATEGORY_APP_MUSIC);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    mAutoPlay = true;
                }
            }
        });
        try {
            //没有允许通知栏使用权限会触发SecurityException
            AudioManager mAudioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
            if (mAudioManager != null) {
                mAudioManager.registerRemoteController(controller);
                L.v("MyNotificationService -> RemoteController注册成功");
                //By default an RemoteController.OnClientUpdateListener implementation will not receive bitmaps for album
                // art. Use setArtworkConfiguration(int, int) to receive images as well.
//                mRemoteController.setArtworkConfiguration(60, 60);
            }
        } catch (SecurityException exception) {
            exception.printStackTrace();
            L.e("MyNotificationService -> RemoteController注册失败，没有允许通知栏使用权限");
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //if (!SmaManager.getInstance().getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NOTIFICATION)) return;

        if (!SmaManager.getInstance().isLoggedIn()) return;

        try {
            //APP name
            //[title] content

            String packageName = sbn.getPackageName();
            String appName = getAppNameByPackage(this, packageName);

            //if (/*Arrays.asList(SMS_PACKAGES).contains(packageName) || */!SmaManager.getInstance().getPackage(packageName))
              //  return;

            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString(Notification.EXTRA_TITLE);
            if (TextUtils.isEmpty(title)) {
                //索尼的手机要这么搞，不然拿不到title
                title = String.valueOf(extras.get(Notification.EXTRA_TITLE));
            }
            title = !TextUtils.isEmpty(title) ? replace(title) : "";

            CharSequence cs = extras.getCharSequence(Notification.EXTRA_TEXT);
            String content = !TextUtils.isEmpty(cs) ? replace(cs.toString()) : "";
            L.d("onNotificationPosted -> " + packageName + ",title = " + title + ",content = " + content);
            if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) return;

            if(packageName.equals("winnerdiet.android.com") && content.equals("오늘도 건강한 하루 되세요 ^^")) return;

            if ("null".equalsIgnoreCase(title) && TextUtils.isEmpty(content)) return;

            String contents = "[" + title + "] " + content;

            //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences pref = this.getSharedPreferences("shared_pref", MODE_PRIVATE);
            String allowed_app_list = pref.getString("allowed_app_list", "");

            if (allowed_app_list.contains("/"+packageName+"/")) {
                SmaManager.getInstance().write(SmaManager.Cmd.NOTICE, SmaManager.Key.MESSAGE_v2, appName, contents);
            }


//            mLastPackageName = packageName;
//            mReceiveTime = timeNow;
        } catch (NullPointerException e) {
            e.printStackTrace();
            L.e("MyNotificationService -> NullPointerException");
        }
    }

    public String replace(String s)
    {
        String ret = "";
        ret = s.replaceAll("\u2069","");
        ret = ret.replaceAll("\u2068","");
        ret = ret.replaceAll("<제목:\u200E 제목없음>\u200E","");
        ret = ret.replaceAll("\u200E","");
        ret = ret.replaceAll("\u200E ","");

        return ret;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
//
    }

    @Override
    public void onListenerDisconnected() {
        L.v("MyNotificationService -> onListenerDisconnected");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestRebind(new ComponentName(this, MyNotificationService.class));
        }
    }

    @Override
    public void onClientChange(boolean clearing) {
        L.v("onClientChange -> clearing=" + clearing);
        if (clearing) {//音乐app离开
            mHasPlayer = false;
            mPlaying = false;
            MyBleServer.getInstance().whenMusicUpdate(MusicEntity.TRACK, TrackAttribute.DURATION, 0);
        } else {//音乐app开启
            mHasPlayer = true;
            if (mAutoPlay) {
                MyBleServer.getInstance().sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY);
                mAutoPlay = false;
            }
        }
    }

    @Override
    public void onClientPlaybackStateUpdate(int state) {
        L.v("onClientPlaybackStateUpdate -> state=" + getStateText(state));
        MyBleServer server = MyBleServer.getInstance();
        switch (state) {
            case RemoteControlClient.PLAYSTATE_PAUSED:
                mPlaying = false;
                server.whenMusicUpdate(MusicEntity.PLAYER, PlayerAttribute.PLAYBACK_INFO, PlayerAttribute.PlaybackState.PAUSED);
                break;
            case RemoteControlClient.PLAYSTATE_PLAYING:
                mPlaying = true;
                server.whenMusicUpdate(MusicEntity.PLAYER, PlayerAttribute.PLAYBACK_INFO, PlayerAttribute.PlaybackState.PLAYING);
                break;
            case RemoteControlClient.PLAYSTATE_FAST_FORWARDING:
                mPlaying = true;
                server.whenMusicUpdate(MusicEntity.PLAYER, PlayerAttribute.PLAYBACK_INFO, PlayerAttribute.PlaybackState
                        .FAST_FORWARDING);
                break;
            case RemoteControlClient.PLAYSTATE_REWINDING:
                mPlaying = true;
                server.whenMusicUpdate(MusicEntity.PLAYER, PlayerAttribute.PLAYBACK_INFO, PlayerAttribute.PlaybackState.REWINDING);
                break;
            case RemoteControlClient.PLAYSTATE_STOPPED:
                mPlaying = false;
                break;
        }
    }

    @Override
    public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
        L.v("onClientPlaybackStateUpdate -> state=" + getStateText(state) + ", stateChangeTimeMs=" + stateChangeTimeMs
                + ", " + "currentPosMs=" + currentPosMs + ", speed=" + speed);
        MyBleServer server = MyBleServer.getInstance();
        switch (state) {
            case RemoteControlClient.PLAYSTATE_PAUSED:
                mPlaying = false;
                server.whenMusicUpdate(MusicEntity.PLAYER, PlayerAttribute.PLAYBACK_INFO, PlayerAttribute.PlaybackState
                        .PAUSED, speed, currentPosMs / 1000);
                break;
            case RemoteControlClient.PLAYSTATE_PLAYING:
                mPlaying = true;
                server.whenMusicUpdate(MusicEntity.PLAYER, PlayerAttribute.PLAYBACK_INFO, PlayerAttribute.PlaybackState
                        .PLAYING, speed, currentPosMs / 1000);
                break;
            case RemoteControlClient.PLAYSTATE_FAST_FORWARDING:
                mPlaying = true;
                server.whenMusicUpdate(MusicEntity.PLAYER, PlayerAttribute.PLAYBACK_INFO, PlayerAttribute.PlaybackState
                        .FAST_FORWARDING, speed, currentPosMs / 1000);
                break;
            case RemoteControlClient.PLAYSTATE_REWINDING:
                mPlaying = true;
                server.whenMusicUpdate(MusicEntity.PLAYER, PlayerAttribute.PLAYBACK_INFO, PlayerAttribute.PlaybackState
                        .REWINDING, speed, currentPosMs / 1000);
                break;
            case RemoteControlClient.PLAYSTATE_STOPPED:
                mPlaying = false;
                break;
        }
    }

    @Override
    public void onClientTransportControlUpdate(int transportControlFlags) {
//        L.v("onClientTransportControlUpdate -> transportControlFlags=" + transportControlFlags);
    }

    @Override
    public void onClientMetadataUpdate(RemoteController.MetadataEditor metadataEditor) {
//        android.media.MediaMetadataRetriever.METADATA_KEY_ALBUM;
        mPlaying = true;
        String artist = metadataEditor.getString(2, " ");
        String album = metadataEditor.getString(1, " ");
        String title = metadataEditor.getString(7, " ");
        long duration = metadataEditor.getLong(9, 0) / 1000;
        L.v("onClientMetadataUpdate -> METADATA_KEY_ARTIST=" + artist + ", METADATA_KEY_ALBUM=" + album
                + ", METADATA_KEY_TITLE=" + title + ", METADATA_KEY_DURATION=" + duration + "}");
        MyBleServer server = MyBleServer.getInstance();
        server.whenMusicUpdate(MusicEntity.TRACK, TrackAttribute.ARTIST, artist);
        server.whenMusicUpdate(MusicEntity.TRACK, TrackAttribute.ALBUM, album);
        server.whenMusicUpdate(MusicEntity.TRACK, TrackAttribute.TITLE, title);
        server.whenMusicUpdate(MusicEntity.TRACK, TrackAttribute.DURATION, duration);
    }

    private boolean isValidate(String packageName, String title, String content) {
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) return false;

        if ("null".equalsIgnoreCase(title) && TextUtils.isEmpty(content)) return false;

        if (title.contains("正在运行")) {
            if (title.contains("短信") || title.contains("酷狗音乐")) return false;
        }

        if (content.contains("QQ正在后台运行")) return false;

        //防止小宝客户 用viber接打电话时不停推送，只过滤的中文、英语和俄语
        if (TextUtils.equals(packageName, "com.viber.voip")) {
            if (content.contains("Выполняется вызов") || content.contains("Call in progress")
                    || content.contains("正在通话中")) return false;
        }

//            long timeNow = System.currentTimeMillis();
//            1.当What's app有多条未读消息时，每条消息会推送如下两条通知，导致实际的content看不到了,所以要做下处理：
//            com.whatsapp,extraTitle = +86 155 1096 1750 (2 条信息): ​,content = 7
//            com.whatsapp,extraTitle = +86 155 1096 1750,content = 2 条新信息（备注：这条消息在手机上是不可见的）
        if (TextUtils.equals("com.whatsapp", packageName)) {
            if (isContainBracket) {
                isContainBracket = false;
                return false;
            }

            if (title.contains("(") && title.contains(")")) {
                isContainBracket = true;
            }
        }

        return true;
    }

    private static String getStateText(int state) {
        switch (state) {
            case RemoteControlClient.PLAYSTATE_PAUSED:
                return "PLAYSTATE_PAUSED";
            case RemoteControlClient.PLAYSTATE_PLAYING:
                return "PLAYSTATE_PLAYING";
            case RemoteControlClient.PLAYSTATE_FAST_FORWARDING:
                return "PLAYSTATE_FAST_FORWARDING";
            case RemoteControlClient.PLAYSTATE_REWINDING:
                return "PLAYSTATE_REWINDING";
            case RemoteControlClient.PLAYSTATE_STOPPED:
                return "PLAYSTATE_STOPPED";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * 通过包名获得APP name
     *
     * @param context     上下文
     * @param packageName 包名
     * @return APP name
     */
    private String getAppNameByPackage(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        String name = "";
        try {
            name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
}