package com.bestmafen.smablelib.server;

import android.app.Notification;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.media.RemoteController;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.bestmafen.easeblelib.util.L;
import com.bestmafen.smablelib.R;
import com.bestmafen.smablelib.component.SmaManager;
import com.bestmafen.smablelib.entity.SmaTime;
import com.bestmafen.smablelib.entity.SmaWeatherForecast;
import com.bestmafen.smablelib.entity.SmaWeatherRealTime;
import com.bestmafen.smablelib.server.constants.music.MusicEntity;
import com.bestmafen.smablelib.server.constants.music.PlayerAttribute;
import com.bestmafen.smablelib.server.constants.music.TrackAttribute;
import com.bestmafen.smablelib.util.SmaBleUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //if (!SmaManager.getInstance().getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NOTIFICATION)) return;

        if (!SmaManager.getInstance().isLoggedIn()) return;

        //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences pref = this.getSharedPreferences("shared_pref", MODE_PRIVATE);
        SharedPreferences put_pref = this.getSharedPreferences("shared_pref", MODE_PRIVATE);
        SharedPreferences.Editor put_editor = put_pref.edit();

        try {
            //APP name
            //[title] content

            String packageName = sbn.getPackageName();
            String appName = getAppNameByPackage(this, packageName);

            //if (/*Arrays.asList(SMS_PACKAGES).contains(packageName) || */!SmaManager.getInstance().getPackage(packageName))
              //  return;

            L.e("TTTpackageName", packageName);
            L.e("TTTappName", appName);

            Bundle extras = sbn.getNotification().extras;

            // LG G시리즈 폰 문자메세지 중복(3회) 알림으로 수동처리
            if(packageName.equals("com.android.mms") && appName.equals("메시지"))
            {
                String extraString = String.valueOf(extras);

                if(extraString.contains("android.wearable.EXTENSIONS")){
                    put_editor.putString("support_wearable_yn", "Y");
                    put_editor.commit();
                }

                String support_wearable_yn = pref.getString("support_wearable_yn","N");

                if(support_wearable_yn.equals("Y") && !extraString.contains("android.wearable.EXTENSIONS")){
                    return;
                }
            }

            /*
            String channel = sbn.getNotification().getChannelId();
            L.e("TTTchannel", channel);
            String group = sbn.getNotification().getGroup();
            L.e("TTTgroup", group);

            Context context = getApplicationContext();
            CharSequence text = channel;
            int duration = Toast.LENGTH_SHORT;

            //Toast toast = Toast.makeText(context, channel+"\n"+group+"\n"+shortcut+"\n"+shortcut+"\n"+sortkey, duration);
            //toast.show();

            String url = "https://www.smarthelper.co.kr/coa_api.php";
            ContentValues values = new ContentValues();
            values.put("action", "getTest");
            values.put("memo", "TTT"+channel+"TTT"+group+"TTT"+shortcut+"TTT"+shortcut+"TTT"+sortkey+"TTT"+extras.getString(Notification.EXTRA_CHANNEL_ID)+"TTT"+extras.getString(Notification.EXTRA_COMPACT_ACTIONS)
                    +"TTT"+extras.getString(Notification.EXTRA_TEMPLATE)+"TTT"+extras.getString(Notification.EXTRA_NOTIFICATION_TAG)+"TTT"+extras.getString(Notification.EXTRA_TEMPLATE));

            HttpAsyncRequest httpAssyncRequest = new HttpAsyncRequest(url, values);
            httpAssyncRequest.execute();

            L.e("TTTchannel", channel);

             */

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


            String allowed_app_list = pref.getString("allowed_app_list", "");

            if (allowed_app_list.contains("/"+packageName+"/")) {

                String message_start_time = pref.getString("message_start_time","0");
                String message_end_time = pref.getString("message_end_time","24");

                int message_start = Integer.valueOf(message_start_time);
                int message_end = Integer.valueOf(message_end_time);


                SimpleDateFormat format = new SimpleDateFormat("HH");
                String hour = format.format(new Date());

                Calendar calendar = Calendar.getInstance();
                int curr_hour = calendar.get(Calendar.HOUR_OF_DAY);

                Log.d("TTT", "curr_hour" + curr_hour);
                Log.d("TTT", "message_start" + message_start);
                Log.d("TTT", "message_end" + message_end);

                if(curr_hour>message_start-1 && curr_hour<message_end) {

                    String last_message_content = pref.getString("last_message_content","");

                    L.e("TTTcontent", content);

                    if(content.startsWith("메시지 보기"))
                    {
                        L.e("TTT", "A");
                        return;
                    }

                    if(getMessageTimeGap()<5 && last_message_content.startsWith(content))
                    {
                        L.e("TTT", "B");
                        return;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String cur_time = sdf.format(new Date());

                    put_editor.putString("last_message_time", cur_time);
                    put_editor.putString("last_message_content", content);

                    put_editor.commit();

                    SmaManager.getInstance().write(SmaManager.Cmd.NOTICE, SmaManager.Key.MESSAGE_v2, appName, contents);
                }
            }


//            mLastPackageName = packageName;
//            mReceiveTime = timeNow;
        } catch (NullPointerException e) {
            e.printStackTrace();
            L.e("MyNotificationService -> NullPointerException");
        }
    }

    private int getMessageTimeGap(){

        SharedPreferences pref = this.getSharedPreferences("shared_pref", MODE_PRIVATE);
        String last_update_time = pref.getString("last_message_time", "");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cur_time = sdf.format(new Date());

        try {
            Date start = sdf.parse(last_update_time);
            Date end = sdf.parse(cur_time);

            long diff = end.getTime() - start.getTime();

            int diffsec = (int) (diff / 1000);

            //L.e("TTTdiffsec", String.valueOf(diffsec));
            return diffsec;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
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

    // 비동기식 http 통신
    public class HttpAsyncRequest extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public HttpAsyncRequest(String url, ContentValues values) {
            Log.d("TTTL", "비동기식 http 접속");
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
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
        }

    }

    // http 통신
    public String httpRequest(String _url, ContentValues _params){

        Log.d("TTTL", _url + _params);

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

            Log.d("TTTL", url + "?" + strParams);

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