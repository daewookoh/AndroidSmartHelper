package com.and.smarthelper.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.and.internal.telephony.ITelephony;
import com.and.smarthelper.BuildConfig;
import com.and.smarthelper.application.MyApplication;
import com.and.smarthelper.service.SmaService;
import com.bestmafen.easeblelib.entity.EaseDevice;
import com.bestmafen.easeblelib.scanner.EaseScanCallback;
import com.bestmafen.easeblelib.scanner.EaseScanner;
import com.bestmafen.easeblelib.scanner.ScanOption;
import com.bestmafen.easeblelib.scanner.ScannerFactory;
import com.bestmafen.easeblelib.util.L;
import com.bestmafen.smablelib.component.SimpleSmaCallback;
import com.bestmafen.smablelib.component.SmaCallback;
import com.bestmafen.smablelib.component.SmaManager;
import com.bestmafen.smablelib.entity.SmaAlarm;
import com.bestmafen.smablelib.entity.SmaExercise;
import com.bestmafen.smablelib.entity.SmaHeartRate;
import com.bestmafen.smablelib.entity.SmaHeartRateSettings;
import com.bestmafen.smablelib.entity.SmaLightSettings;
import com.bestmafen.smablelib.entity.SmaSedentarinessSettings;
import com.bestmafen.smablelib.entity.SmaSleep;
import com.bestmafen.smablelib.entity.SmaSport;
import com.bestmafen.smablelib.entity.SmaStream;
import com.bestmafen.smablelib.entity.SmaTime;
import com.bestmafen.smablelib.entity.SmaTimezone;
import com.bestmafen.smablelib.entity.SmaTracker;
import com.bestmafen.smablelib.entity.SmaUserInfo;
import com.bestmafen.smablelib.entity.SmaWeatherForecast;
import com.bestmafen.smablelib.entity.SmaWeatherRealTime;
import com.bestmafen.smablelib.util.SmaBleUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.and.smarthelper.R;
import com.and.smarthelper.util.CameraSurfaceView;
import com.and.smarthelper.util.T;
import com.mocoplex.adlib.AdlibManager;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


import static android.content.ContentValues.TAG;

public class LauncherActivity extends Activity {

    Camera camera;
    boolean previewing = false;

    MyApplication common = new MyApplication(this);

    private BluetoothAdapter btAdapter;
    private static final int REQUEST_CODE_PICK_FILE = 0x01;
    private volatile boolean     isDeviceFound;
    private EaseDevice mTarget;
    private EaseScanner mScanner;
    private SmaManager mSmaManager;
    private AudioManager mAudioManager;
    private SmaCallback mSmaCallback;
    private AlertDialog mDialog;
    private String update_data_status;

    WebView webView;
    ProgressBar progressBar;
    SwipeRefreshLayout refreshLayout;
    ProgressDialog progressDialog;
    public static Context mContext;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    SQLiteDatabase myDB = null;
    private final String dbName = "record2";

    private int SMS_RECEIVE_PERMISSON = 1012;

    Intent bluetoothService;

    public int battery;

    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    //GPS
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;

    //카메라(이미지)업로드
    private final static int FCR = 1;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;

    //카카오로그인
    SessionCallback callback;

    //네이버로그인
    public static OAuthLogin mOAuthLoginModule;

    //구글피트니스
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 0x1001;

    //sns로그인공용
    String email = "";
    String nickname = "";
    String enc_id = "";
    String profile_image = "";
    String age = "";
    String gender = "";
    String id = "";
    String name = "";
    String birthday = "";

    CameraSurfaceView surfaceView;
    ImageButton stopBtn;
    private File file;
    static final int REQUEST_PERMISSION = 1003;

    private ImageView splash;
    private Animation anim;

    AdlibManager adlibManager;
    String fileDir;

    int vol = 5;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_web);

        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_CALL_LOG
        },REQUEST_PERMISSION);

        fileDir = this.getCacheDir().getAbsolutePath();
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        adlibManager = new AdlibManager(getResources().getString(R.string.adlib_id));
        adlibManager.onCreate(this);
        // 테스트 광고 노출로, 상용일 경우 꼭 제거해야 합니다.
        //adlibManager.setAdlibTestMode(true);


        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            //API26(Android 8.0) 에서 portrait 설정시 adlib광고 오류생김
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // 카카오로그인
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        splash = (ImageView) findViewById(R.id.splash);
        splash.setVisibility(View.GONE);

        surfaceView = (CameraSurfaceView)findViewById(R.id.surface_view);
        //surfaceHolder = surfaceView.getHolder();
        //surfaceHolder.addCallback(this);
        stopBtn = (ImageButton)findViewById(R.id.stop);

        mContext = this;

        webView = (WebView) findViewById(R.id.webViewMain);

        progressBar = (ProgressBar) findViewById(R.id.progressBarMain);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshMain);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //새로고침 소스
                webView.reload();
            }
        });

        initDB();
        //getToken();

        //requestSMSPermission();

        // URL 세팅
        String sUrl = getIntent().getStringExtra("sUrl");
        if(sUrl==null) {
            sUrl = getResources().getString(R.string.default_url);
        }

        // 웹뷰 옵션세팅
        setWebview(webView);

        // 웹뷰 로드
        webView.loadUrl(sUrl);

        mSmaManager = SmaManager.getInstance();
        final String address = SmaBleUtils.addressPlus1(mSmaManager.getSavedAddress());

        mScanner = ScannerFactory.createScanner().setScanOption(new ScanOption().specifyAddress(address))
                .setEaseScanCallback(new EaseScanCallback() {

                    @Override
                    public void onDeviceFound(final EaseDevice device) {
                        L.v("FirmwareUpdateActivity -> onDeviceFound " + device.toString());
                        L.d("搜索到目标设备 -> 开始固件升级");
                        isDeviceFound = true;
                        mScanner.startScan(false);
                        mTarget = device;
                        pickFirmwareFile();
                    }

                    @Override
                    public void onBluetoothDisabled() {
                        T.show(mContext, R.string.enable_bluetooth);
                    }

                    @Override
                    public void onScanStart(boolean start) {
                        if (!start) {
                            if (!isDeviceFound) {
                            }
                        }
                    }

                });

        mSmaManager = mSmaManager.addSmaCallback(mSmaCallback = new SimpleSmaCallback() {

            //AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            MediaPlayer ringPlayer = MediaPlayer.create(LauncherActivity.this, R.raw.dingdong);

            @Override
            public void onReadSportData(final List<SmaSport> list) {
                Log.d("TTTL", String.valueOf(list));

                update_data_status = common.getSP("update_data_status","");
                common.putSP("update_data_status", update_data_status + "SPORT");

                SQLiteDatabase ReadDB = openOrCreateDatabase(dbName, MODE_PRIVATE, null);
                ReadDB.execSQL("UPDATE sport SET synced=1 WHERE synced=0");

                //앱 DB 입력
                for (int i=0; i<list.size(); i++)
                {
                    insertSportDB((SmaSport)list.get(i));
                }

                //서버 DB 입력
                Cursor c = null;
                String url = getResources().getString(R.string.api_url);
                String mem_no = common.getSP("mem_no","0");
                c = ReadDB.rawQuery("SELECT * FROM sport WHERE mem_no=? AND synced=0" , new String[]{mem_no});

                if (c != null) {

                    try {
                        String enc_data = URLEncoder.encode(cursorToString(c),"utf-8");

                        if(enc_data.length()>6) {
                            ContentValues values2 = new ContentValues();
                            values2.put("action", "sendData");
                            values2.put("table_name", "sport");
                            values2.put("mem_no", mem_no);
                            values2.put("data", enc_data);
                            HttpAsyncRequest httpAsyncRequest = new HttpAsyncRequest(url, values2);
                            httpAsyncRequest.execute();
                        }


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Log.d("TTTL", "==================SEND_DB_SPORT===================");
                    if (c.moveToFirst()) {
                        do {

                            String log = "[sport_send_db]";

                            for (int j=0; j<c.getColumnCount(); j++)
                            {
                                log += c.getColumnName(j) + ":" + c.getString(c.getColumnIndex(c.getColumnName(j))) + "/";
                            }

                            Log.d("TTTL", log);

                        } while (c.moveToNext());
                    }

                }

                update_data_status = common.getSP("update_data_status","");
                update_data_status = update_data_status.replaceAll("SPORT","");
                common.putSP("update_data_status",update_data_status);

                if(update_data_status.isEmpty()) {
                    T.show(mContext, "신규 데이터 업데이트 중");
                    ReadDB.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            webView.reload();
                                        }
                                    },
                                    2500);

                        }
                    });

                }
                //readDB();

            }

            @Override
            public void onReadHeartRateData(final List<SmaHeartRate> list) {
                Log.d("TTTL", String.valueOf(list));
                update_data_status = common.getSP("update_data_status","");
                common.putSP("update_data_status", update_data_status + "HEART");

                SQLiteDatabase ReadDB = openOrCreateDatabase(dbName, MODE_PRIVATE, null);
                ReadDB.execSQL("UPDATE heart SET synced=1 WHERE synced=0");

                for (int i=0; i<list.size(); i++)
                {
                    insertHeartRateDB((SmaHeartRate)list.get(i));
                }

                //서버 DB 입력
                Cursor c = null;
                String url = getResources().getString(R.string.api_url);
                String mem_no = common.getSP("mem_no","0");
                c = ReadDB.rawQuery("SELECT * FROM heart WHERE mem_no=? AND synced=0" , new String[]{mem_no});

                if (c != null) {

                    try {
                        String enc_data = URLEncoder.encode(cursorToString(c),"utf-8");

                        if(enc_data.length()>6) {
                            ContentValues values2 = new ContentValues();
                            values2.put("action", "sendData");
                            values2.put("table_name", "heart");
                            values2.put("mem_no", mem_no);
                            values2.put("data", enc_data);
                            HttpAsyncRequest httpAsyncRequest = new HttpAsyncRequest(url, values2);
                            httpAsyncRequest.execute();
                        }


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Log.d("TTTL", "==================SEND_DB_HEART===================");
                    if (c.moveToFirst()) {
                        do {

                            String log = "[heart_send_db]";

                            for (int j=0; j<c.getColumnCount(); j++)
                            {
                                log += c.getColumnName(j) + ":" + c.getString(c.getColumnIndex(c.getColumnName(j))) + "/";
                            }

                            Log.d("TTTL", log);

                        } while (c.moveToNext());
                    }

                }

                update_data_status = common.getSP("update_data_status","");
                update_data_status = update_data_status.replaceAll("HEART","");
                common.putSP("update_data_status",update_data_status);

                if(update_data_status.isEmpty()) {
                    T.show(mContext, "신규 데이터 업데이트 중");
                    ReadDB.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            webView.reload();
                                        }
                                    },
                                    2500);

                        }
                    });

                }
                //readDB();

            }

            @Override
            public void onReadSleepData(final List<SmaSleep> list) {
                Log.d("TTTL", String.valueOf(list));
                update_data_status = common.getSP("update_data_status","");
                common.putSP("update_data_status", update_data_status + "SLEEP");

                SQLiteDatabase ReadDB = openOrCreateDatabase(dbName, MODE_PRIVATE, null);
                ReadDB.execSQL("UPDATE sleep SET synced=1 WHERE synced=0");

                for (int i=0; i<list.size(); i++)
                {
                    insertSleepDB((SmaSleep)list.get(i));
                }

                //서버 DB 입력
                Cursor c = null;
                String url = getResources().getString(R.string.api_url);
                String mem_no = common.getSP("mem_no","0");
                c = ReadDB.rawQuery("SELECT * FROM sleep WHERE mem_no=? AND synced=0" , new String[]{mem_no});

                if (c != null) {

                    try {
                        String enc_data = URLEncoder.encode(cursorToString(c),"utf-8");

                        if(enc_data.length()>6) {
                            ContentValues values2 = new ContentValues();
                            values2.put("action", "sendData");
                            values2.put("table_name", "sleep");
                            values2.put("mem_no", mem_no);
                            values2.put("data", enc_data);
                            HttpAsyncRequest httpAsyncRequest = new HttpAsyncRequest(url, values2);
                            httpAsyncRequest.execute();
                        }


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Log.d("TTTL", "==================SEND_DB_SLEEP===================");
                    if (c.moveToFirst()) {
                        do {

                            String log = "[sleep_send_db]";

                            for (int j=0; j<c.getColumnCount(); j++)
                            {
                                log += c.getColumnName(j) + ":" + c.getString(c.getColumnIndex(c.getColumnName(j))) + "/";
                            }

                            Log.d("TTTL", log);

                        } while (c.moveToNext());
                    }

                }

                update_data_status = common.getSP("update_data_status","");
                update_data_status = update_data_status.replaceAll("SLEEP","");
                common.putSP("update_data_status",update_data_status);

                if(update_data_status.isEmpty()) {
                    T.show(mContext, "신규 데이터 업데이트 중");
                    ReadDB.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            webView.reload();
                                        }
                                    },
                                    2500);

                        }
                    });

                }
                //readDB();

            }


            @Override
            public void onReadExercise(final List<SmaExercise> list) {
                Log.d("TTTL", String.valueOf(list));
                update_data_status = common.getSP("update_data_status","");
                common.putSP("update_data_status", update_data_status + "EXERCISE");

                SQLiteDatabase ReadDB = openOrCreateDatabase(dbName, MODE_PRIVATE, null);
                ReadDB.execSQL("UPDATE exercise SET synced=1 WHERE synced=0");

                for (int i=0; i<list.size(); i++)
                {
                    insertExerciseDB((SmaExercise)list.get(i));
                }

                //서버 DB 입력
                Cursor c = null;
                String url = getResources().getString(R.string.api_url);
                String mem_no = common.getSP("mem_no","0");
                c = ReadDB.rawQuery("SELECT * FROM exercise WHERE mem_no=? AND synced=0" , new String[]{mem_no});

                if (c != null) {

                    try {
                        String enc_data = URLEncoder.encode(cursorToString(c),"utf-8");

                        if(enc_data.length()>6) {
                            ContentValues values2 = new ContentValues();
                            values2.put("action", "sendData");
                            values2.put("table_name", "exercise");
                            values2.put("mem_no", mem_no);
                            values2.put("data", enc_data);
                            HttpAsyncRequest httpAsyncRequest = new HttpAsyncRequest(url, values2);
                            httpAsyncRequest.execute();
                        }


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Log.d("TTTL", "==================SEND_DB_EXERCISE===================");
                    if (c.moveToFirst()) {
                        do {

                            String log = "[exercise_send_db]";

                            for (int j=0; j<c.getColumnCount(); j++)
                            {
                                log += c.getColumnName(j) + ":" + c.getString(c.getColumnIndex(c.getColumnName(j))) + "/";
                            }

                            Log.d("TTTL", log);

                        } while (c.moveToNext());
                    }

                }

                update_data_status = common.getSP("update_data_status","");
                update_data_status = update_data_status.replaceAll("EXERCISE","");
                common.putSP("update_data_status",update_data_status);

                if(update_data_status.isEmpty()) {
                    T.show(mContext, "신규 데이터 업데이트 중");
                    ReadDB.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            webView.reload();
                                        }
                                    },
                                    2500);

                        }
                    });

                }
                //readDB();

            }

            @Override
            public void onReadTracker(final List<SmaTracker> list) {
                Log.d("TTTL", String.valueOf(list));
                update_data_status = common.getSP("update_data_status","");
                common.putSP("update_data_status", update_data_status + "TRACKER");

                SQLiteDatabase ReadDB = openOrCreateDatabase(dbName, MODE_PRIVATE, null);
                ReadDB.execSQL("UPDATE tracker SET synced=1 WHERE synced=0");

                for (int i=0; i<list.size(); i++)
                {
                    insertTrackerDB((SmaTracker)list.get(i));
                }

                //서버 DB 입력
                Cursor c = null;
                String url = getResources().getString(R.string.api_url);
                String mem_no = common.getSP("mem_no","0");
                c = ReadDB.rawQuery("SELECT * FROM tracker WHERE mem_no=? AND synced=0" , new String[]{mem_no});

                if (c != null) {

                    try {
                        String enc_data = URLEncoder.encode(cursorToString(c),"utf-8");

                        if(enc_data.length()>6) {
                            ContentValues values2 = new ContentValues();
                            values2.put("action", "sendData");
                            values2.put("table_name", "tracker");
                            values2.put("mem_no", mem_no);
                            values2.put("data", enc_data);
                            HttpAsyncRequest httpAsyncRequest = new HttpAsyncRequest(url, values2);
                            httpAsyncRequest.execute();
                        }


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Log.d("TTTL", "==================SEND_DB_TRACKER===================");
                    if (c.moveToFirst()) {
                        do {

                            String log = "[tracker_send_db]";

                            for (int j=0; j<c.getColumnCount(); j++)
                            {
                                log += c.getColumnName(j) + ":" + c.getString(c.getColumnIndex(c.getColumnName(j))) + "/";
                            }

                            Log.d("TTTL", log);

                        } while (c.moveToNext());
                    }

                }

                update_data_status = common.getSP("update_data_status","");
                update_data_status = update_data_status.replaceAll("TRACKER","");
                common.putSP("update_data_status",update_data_status);

                if(update_data_status.isEmpty()) {
                    T.show(mContext, "신규 데이터 업데이트 중");
                    ReadDB.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            webView.reload();
                                        }
                                    },
                                    2500);

                        }
                    });

                }
                //readDB();

            }

            @Override
            public void onFindPhone(boolean start) {
                //T.show(mContext, "onFindPhone -> " + start);

                if(start==true) {

                    //vol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

                    ringPlayer.setLooping(true);
                    ringPlayer.start();

                }else
                {
                    //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
                    ringPlayer.stop();
                    //ringPlayer = MediaPlayer.create(LauncherActivity.this, R.raw.ring);
                }
            }

            @Override
            public void onLogin(final boolean ok) {//设备登录返回
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        doJavascript("javascript:isConnected('"+ok+"')");
                        //doJavascript("javascript:reload()");
                        //common.putSP("last_weather_update", "2019-01-01 00:00:00");
                        //common.putSP("last_agps_update", "2019-01-01 00:00:00");
                    }
                });
                //T.show(mContext, "onLogin -> " + ok);

                /*
                if(ok==true)
                {
                    readDB();
                }
                */

            }

            @Override
            public void onAlarmsChange() {
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.READ_ALARM);
                //T.show(mContext, "onAlarmsChange");
            }

            @Override
            public void onReadAlarm(List<SmaAlarm> alarms) {
                //T.show(mContext, "onReadAlarm -> " + alarms.toString());
                Log.d("TTTL", String.valueOf(alarms));

                String mem_no = common.getSP("mem_no","0");
                String url = getResources().getString(R.string.api_url);

                if(Integer.valueOf(mem_no) > 0) {

                    for (int i=0; i<alarms.size(); i++) {
                        ContentValues values = new ContentValues();
                        values.put("action", "getAlarmsFromApp");
                        values.put("no", String.valueOf(alarms.get(i).getId()+1));
                        values.put("time", String.valueOf(alarms.get(i).getTime()));
                        values.put("enabled", String.valueOf(alarms.get(i).getEnabled()));
                        values.put("repeat", String.valueOf(alarms.get(i).getRepeat()));
                        values.put("tag", String.valueOf(alarms.get(i).getTag()));
                        values.put("mem_no", mem_no);

                        HttpAsyncRequest httpAssyncRequest = new HttpAsyncRequest(url, values);
                        httpAssyncRequest.execute();
                    }

                    ContentValues values = new ContentValues();
                    values.put("action", "getAlarmCountFromApp");
                    values.put("alarm_count", String.valueOf(alarms.size()));
                    values.put("mem_no", mem_no);

                    HttpAsyncRequest httpAssyncRequest = new HttpAsyncRequest(url, values);
                    httpAssyncRequest.execute();
                }

            }

            @Override
            public void onReadBattery(final int battery) {//读取电量返回
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        doJavascript("javascript:battery('"+battery+"')");
                    }
                });

            }

            @Override
            public void onReadVersion(final String firmware, String bleVersion) {//读取固件版本返回
                T.show(mContext, "onReadVersion -> " + firmware);
            }

            @Override
            public void onGoalChange() {
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.READ_GOAL);
                //T.show(mContext, "onGoalChange");
            }

            @Override
            public void onReadGoal(int goal) {

                //T.show(mContext, "onReadGoal -> " + goal);
                String mem_no = common.getSP("mem_no","0");

                if(Integer.valueOf(mem_no) > 0) {
                    String url = getResources().getString(R.string.api_url);
                    ContentValues values = new ContentValues();
                    values.put("action", "getGoalFromApp");
                    values.put("goal", String.valueOf(goal));
                    values.put("mem_no", mem_no);

                    HttpAsyncRequest httpAssyncRequest = new HttpAsyncRequest(url, values);
                    httpAssyncRequest.execute();
                }

            }

            @Override
            public void onSedentarinessChange() {
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.READ_SEDENTARINESS);
                //T.show(mContext, "onSedentarinessChange");
            }

            @Override
            public void onReadSedentariness(SmaSedentarinessSettings sedentariness) {

                //T.show(mContext, "onReadSedentariness -> " + sedentariness);
                Log.d("TTTL", String.valueOf(sedentariness));
                String mem_no = common.getSP("mem_no","0");

                String enabled = "1";
                if(sedentariness.getEnabled() == 0)
                {
                    enabled = "0";
                }


                if(Integer.valueOf(mem_no) > 0) {
                    String url = getResources().getString(R.string.api_url);
                    ContentValues values = new ContentValues();
                    values.put("action", "getSitFromApp");
                    values.put("interval", String.valueOf(sedentariness.getInterval()));
                    values.put("enabled", enabled);
                    values.put("mem_no", mem_no);

                    HttpAsyncRequest httpAssyncRequest = new HttpAsyncRequest(url, values);
                    httpAssyncRequest.execute();
                }
            }

            @Override
            public void onTransferBuffer(boolean status, final int total, final int completed) {
                if (status) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(completed==total) {
                                //T.show(mContext, "AGPS 업데이트 완료");
                            }
                        }
                    });
                }
            }

            @Override
            public void onTakePhoto() {
                T.show(mContext, "onTakePhoto");
            }

            @Override
            public void onKeyDown(byte key) {

                Log.d("TTTL", String.valueOf(key));
                String camera_yn = common.getSP("camera_on_yn", "N");

                Log.d("TTTL", "camera_yn" + camera_yn);
                //String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);

                if(camera_yn.equals("Y")) {

                    if (key == 1) {
                        //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);
                        MediaActionSound sound = new MediaActionSound();
                        sound.play(MediaActionSound.SHUTTER_CLICK);
                        capture();
                    } else if (key == 2) {
                        stopCamera();
                    }
                }
                else if (key == 1) {
                    if (!mSmaManager.isCalling) return;

                    TelephonyManager telMag = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    Class<TelephonyManager> c = TelephonyManager.class;
                    Method mthEndCall;

                    try {
                        //部分华为手机上,例如华为P20,虽然是Android9.0的系统版本,但依然保留了此方法,所以此处不进行API版本判断
//                        if (android.os.Build.VERSION.SDK_INT < 28) {
                        /*
                        mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
                        mthEndCall.setAccessible(true);
                        ITelephony iTel = (ITelephony) mthEndCall.invoke(telMag, (Object[]) null);
                        iTel.endCall();
                        */

                        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                        Method m1 = tm.getClass().getDeclaredMethod("getITelephony");
                        m1.setAccessible(true);
                        Object iTelephony = m1.invoke(tm);

                        //Method m2 = iTelephony.getClass().getDeclaredMethod("silenceRinger");
                        Method m3 = iTelephony.getClass().getDeclaredMethod("endCall");
                        //m2.invoke(iTelephony);
                        m3.invoke(iTelephony);

                        L.v("hang up OK");
//                        }
                    } catch (Exception e) {
                        L.e("hang up error " + e.getMessage());
                    }
                }
            }

            /*
            @Override
            public void onReadSportData(final List<SmaSport> list) {
                Log.d("TTTL", String.valueOf(list));

                for (int i=0; i<list.size(); i++)
                {
                    //Log.d("TTTL", String.valueOf(list.get(i)));
                    insertDB("sport", (SmaSport)list.get(i));
                }

                readDB();

            }
            */
        });

    }

    public void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("TTTL token : ", token);
                        common.putSP("device_token", token);
                    }
                });
    }

    public int checkMemNo(){

        String mem_no = common.getSP("mem_no","0");

        if(Integer.valueOf(mem_no)<1)
        {
            moveToLogin();
            return 0;
        }

        return Integer.valueOf(mem_no);
    }

    public void moveToLogin() {

        Log.d("TTTL","moveToLogin");
        String login_url = getResources().getString(R.string.login_url);
        webView.loadUrl(login_url);
    }

    public void getGPS() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            T.show(this, "날씨데이터 수집불가. 위치정보 접근을 허용해 주세요");

            return;
        }

        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("TTTLLocation Changes", location.toString());
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                common.putSP("longitude",Double.toString(lon));
                common.putSP("latitude",Double.toString(lat));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("TTTLStatus Changed", String.valueOf(status));
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("TTTLProvider Enabled", provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("TTTLProvider Disabled", provider);
            }
        };

        // Now first make a criteria with your requirements
        // this is done to save the battery life of the device
        // there are various other other criteria you can search for..
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        // Now create a location manager
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // This is the Best And IMPORTANT part
        final Looper looper = null;

        locationManager.requestSingleUpdate(criteria, locationListener, looper);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        String mem_no = common.getSP("mem_no","0");
                        String longitude = common.getSP("longitude", "");
                        String latitude = common.getSP("latitude", "");

                        if(Integer.valueOf(mem_no) > 0) {
                            String url = getResources().getString(R.string.api_url);
                            ContentValues values = new ContentValues();
                            values.put("action", "getGpsFromApp");
                            values.put("lon", longitude);
                            values.put("lat", latitude);
                            values.put("mem_no", mem_no);

                            common.putSP("last_longitude", longitude);
                            common.putSP("last_latitude", latitude);

                            HttpAsyncRequest httpAssyncRequest = new HttpAsyncRequest(url, values);
                            httpAssyncRequest.execute();
                        }
                    }
                },
                1000);

    }

    public void refresh(){
        webView.reload();
    }

    private boolean appInstalled(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    protected void insertSportDB(SmaSport data) {

        String mem_no = common.getSP("mem_no","0");

        if(Integer.valueOf(mem_no)<1)
        {
            moveToLogin();
        }

        try {
            SQLiteDatabase myDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            myDB.execSQL("INSERT INTO sport "
                    + " (s_date, s_time, mode, step, calorie, distance, synced, mem_no) Values "
                    + "('"+data.date+"', '"+data.time+"', "+data.mode+", "+data.step+", "+data.calorie+", "+data.distance+", "+data.synced+","+mem_no+");");

            myDB.close();
        }
        catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }
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

            if(s==null) {
                return;
            }

            super.onPostExecute(s);

            try {
                //넘어온 result 값을 JSONObject 로 변환해주고, 값을 가져오면 되는데요.
                // result 를 Log에 찍어보면 어떻게 가져와야할 지 감이 오실거에요.
                JSONObject object = new JSONObject(s);
                if(object.getString("resultcode").equals("00") && object.getString("act").equals("getWeatherFromServer")) {
                    JSONObject jsonObject = new JSONObject(object.getString("response"));
                    Log.d("TTTLjsonObject", jsonObject.toString());

                    if(
                            jsonObject.has("temperature") &&
                                    jsonObject.has("humidity") &&
                                    jsonObject.has("windSpeed") &&
                                    jsonObject.has("ultraviolet") &&
                                    jsonObject.has("precipitation") &&
                                    jsonObject.has("visibility") &&
                                    jsonObject.has("weatherCode") &&
                                    jsonObject.has("weatherCode1") &&
                                    jsonObject.has("weatherCode2") &&
                                    jsonObject.has("weatherCode3") &&
                                    jsonObject.has("min1") &&
                                    jsonObject.has("min2") &&
                                    jsonObject.has("min3") &&
                                    jsonObject.has("max1") &&
                                    jsonObject.has("max2") &&
                                    jsonObject.has("max3")
                    )
                    {

                        //sUrl += "&name=" + jsonObject.getString("name");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeZone(SmaBleUtils.getDefaultTimeZone());

                        SmaTime smaTime0 = new SmaTime();

                        SmaWeatherRealTime smaWeather = new SmaWeatherRealTime();
                        smaWeather.time = smaTime0;
                        smaWeather.temperature = jsonObject.getInt("temperature");
                        smaWeather.humidity = jsonObject.getInt("humidity");
                        smaWeather.weatherCode = jsonObject.getInt("weatherCode");
                        smaWeather.windSpeed = jsonObject.getInt("windSpeed");
                        smaWeather.precipitation = jsonObject.getInt("precipitation");
                        smaWeather.visibility = jsonObject.getInt("visibility");


                        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_WEATHER, smaWeather);

                        List<SmaWeatherForecast> forecasts = new ArrayList<>();
                        SmaWeatherForecast forecast = new SmaWeatherForecast();
                        forecast.temH = jsonObject.getInt("max1");
                        forecast.temL = jsonObject.getInt("min1");
                        forecast.weatherCode = jsonObject.getInt("weatherCode1");
                        forecast.ultraviolet = jsonObject.getInt("ultraviolet");
                        forecasts.add(forecast);
                        forecast = new SmaWeatherForecast();
                        forecast.temH = jsonObject.getInt("max2");
                        forecast.temL = jsonObject.getInt("min2");
                        forecast.weatherCode = jsonObject.getInt("weatherCode2");
                        //forecast.ultraviolet = 3;//jsonObject.getInt("ultraviolet");
                        forecasts.add(forecast);
                        forecast = new SmaWeatherForecast();
                        forecast.temH = jsonObject.getInt("max3");
                        forecast.temL = jsonObject.getInt("min3");
                        forecast.weatherCode = jsonObject.getInt("weatherCode3");
                        //forecast.ultraviolet = 6;//jsonObject.getInt("ultraviolet");
                        forecasts.add(forecast);

                        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_FORECAST, forecasts);

                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    protected void insertHeartRateDB(SmaHeartRate data) {

        String mem_no = common.getSP("mem_no","0");

        if(Integer.valueOf(mem_no)<1)
        {
            moveToLogin();
        }

        try {
            SQLiteDatabase myDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            myDB.execSQL("INSERT INTO heart "
                    + " (s_date, s_time, value, synced, mem_no) Values "
                    + "('"+data.date+"', '"+data.time+"', "+data.value+", "+data.synced+","+mem_no+");");

            myDB.close();
        }
        catch (SQLiteException se) {
            //Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }
    }

    protected void insertSleepDB(SmaSleep data) {

        String mem_no = common.getSP("mem_no","0");

        if(Integer.valueOf(mem_no)<1)
        {
            moveToLogin();
        }

        try {
            SQLiteDatabase myDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            myDB.execSQL("INSERT INTO sleep "
                    + " (s_date, s_time, mode, strong, soft, synced, mem_no) Values "
                    + "('"+data.date+"', '"+data.time+"', "+data.strong+", "+data.soft+", "+data.mode+", "+data.synced+","+mem_no+");");

            myDB.close();
        }
        catch (SQLiteException se) {
            //Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }
    }

    protected void insertTrackerDB(SmaTracker data) {

        String mem_no = common.getSP("mem_no","0");

        if(Integer.valueOf(mem_no)<1)
        {
            moveToLogin();
        }

        try {
            SQLiteDatabase myDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            myDB.execSQL("INSERT INTO tracker "
                    + " (s_date, s_time, start, type, latitude, longitude, altitude, synced, mem_no) Values "
                    + "('"+data.date+"', '"+data.time+"', '"+data.start+"', "+data.type+", "+data.latitude+", "
                    +data.longitude + ", "+data.altitude+", "+data.synced+","+mem_no+");");

            myDB.close();
        }
        catch (SQLiteException se) {
            //Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }
    }

    protected void insertExerciseDB(SmaExercise data) {

        String mem_no = common.getSP("mem_no","0");

        if(Integer.valueOf(mem_no)<1)
        {
            moveToLogin();
        }

        try {
            SQLiteDatabase myDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            myDB.execSQL("INSERT INTO exercise "
                    + " (s_date, start_time, end_time, duration, altitude, airPressure, spm, type, step, distance, speed, pace, cal, synced, mem_no) Values "
                    + "('"+data.date+"', '"+data.start+"', '"+data.end+"', "+data.duration+", "+data.altitude
                    + ", "+data.airPressure+", "+data.spm+", "+data.type+", "+data.step+", "+data.distance
                    +","+data.speed+", "+data.pace+", "+data.cal+", "+data.synced+","+mem_no+");");

            myDB.close();
        }
        catch (SQLiteException se) {
            //Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }
    }

    protected void insertDB(String tableName, SmaSport data) {

        Log.d("TTTL", String.valueOf(data.date));

        try {
            SQLiteDatabase myDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            myDB.execSQL("INSERT INTO " + tableName
                    + " (s_date, s_time, mode, step, calorie, distance, synced) Values "
                    + "('"+data.date+"', '"+data.time+"', "+data.mode+", "+data.step+", "+data.calorie+", "+data.distance+", "+data.synced+");");
        }
        catch (SQLiteException se) {
            //Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }
    }

    protected void readDB(){
        Log.d("TTTL","readDB");
        try {

            SQLiteDatabase ReadDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            List<String> table_list = new ArrayList<String>();
            //table_list.add("sport");
            //table_list.add("heart");
            //table_list.add("sleep");

            for (int i=0; i<table_list.size(); i++) {

                Cursor c = ReadDB.rawQuery("SELECT * FROM " + table_list.get(i), null);

                if (c != null) {
                    if (c.moveToFirst()) {
                        do {

                            String log = "["+table_list.get(i)+"]";

                            for (int j=0; j<c.getColumnCount(); j++)
                            {
                                log += c.getColumnName(j) + ":" + c.getString(c.getColumnIndex(c.getColumnName(j))) + "/";
                            }
                            Log.d("TTTL", log);

                        } while (c.moveToNext());
                    }
                }
            }

            ReadDB.close();

        } catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }

    }


    public void initDB(){
        Log.d("TTTL","initDB");
        try {
            myDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            //myDB.execSQL("Drop table sport" );
            //myDB.execSQL("Drop table heart" );
            //myDB.execSQL("Drop table tracker" );

            //테이블이 존재하지 않으면 새로 생성합니다.
            Log.d("TTTL","createDB");

            myDB.execSQL("CREATE TABLE IF NOT EXISTS sport "
                    + " (s_date DATETIME, s_time STRING(20), mode INT(11), step INT(11), calorie INT(11), distance INT(11), synced INT(11), mem_no INT(11));");

            myDB.execSQL("CREATE TABLE IF NOT EXISTS heart "
                    + " (s_date DATETIME, s_time STRING(20), type INT(11), value INT(11), synced INT(11), mem_no INT(11));");

            myDB.execSQL("CREATE TABLE IF NOT EXISTS sleep "
                    + " (s_date DATETIME, s_time STRING(20), mode INT(11), strong INT(11), soft INT(11), synced INT(11), mem_no INT(11));");

            myDB.execSQL("CREATE TABLE IF NOT EXISTS exercise "
                    + " (s_date DATETIME, start_time STRING(20), end_time STRING(20), duration INT(11), altitude INT(11), airPressure INT(11), spm INT(11), type INT(11), step INT(11), distance INT(11), speed INT(11), pace INT(11), cal FLOAT, synced INT(11), mem_no INT(11));");

            myDB.execSQL("CREATE TABLE IF NOT EXISTS tracker "
                    + " (s_date DATETIME, s_time STRING(20), start INT(11), type INT(11), latitude FLOAT, longitude FLOAT, altitude INT(11), synced INT(11), mem_no INT(11));");

            myDB.close();

        } catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("", se.getMessage());


        }
    }

    public void requestSMSPermission(){
        int permissonCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);

        if(permissonCheck == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "SMS 수신권한 있음", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "SMS 수신권한 없음", Toast.LENGTH_SHORT).show();

            //권한설정 dialog에서 거부를 누르면
            //ActivityCompat.shouldShowRequestPermissionRationale 메소드의 반환값이 true가 된다.
            //단, 사용자가 "Don't ask again"을 체크한 경우
            //거부하더라도 false를 반환하여, 직접 사용자가 권한을 부여하지 않는 이상, 권한을 요청할 수 없게 된다.
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)){
                //이곳에 권한이 왜 필요한지 설명하는 Toast나 dialog를 띄워준 후, 다시 권한을 요청한다.
                Toast.makeText(getApplicationContext(), "SMS권한이 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
            }
        }
    }

    private boolean isNotiPermissionAllowed() {
        Set<String> notiListenerSet = NotificationManagerCompat.getEnabledListenerPackages(this);
        String myPackageName = getPackageName();

        for(String packageName : notiListenerSet) {
            if(packageName == null) {
                continue;
            }
            if(packageName.equals(myPackageName)) {
                return true;
            }
        }

        return false;
    }

    private void pickFirmwareFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setWebview(final WebView webView)
    {
        WebSettings set = webView.getSettings();
        set.setJavaScriptEnabled(true);
        set.setLoadWithOverviewMode(true); // 한페이지에 전체화면이 다 들어가도록
        set.setJavaScriptCanOpenWindowsAutomatically(true);
        set.setSupportMultipleWindows(true); // <a>태그에서 target="_blank" 일 경우 외부 브라우저를 띄움
        set.setUserAgentString(webView.getSettings().getUserAgentString() + getResources().getString(R.string.user_agent));
        set.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        set.setGeolocationEnabled(true);
        set.setDomStorageEnabled(true); //Javascript error 무시
        set.setTextZoom(100);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                builder.setMessage("SSL 경고 : 계속하시겠습니까?");
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final android.app.AlertDialog dialog = builder.create();
                dialog.show();
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                //kcp 결제 처리
                if (url != null && (url.startsWith("vguardend:") )){
                    return false;
                }

                if (url != null && (url.startsWith("intent:") || (url.startsWith("ahnlabv3mobileplus:")))) {
                    Log.e("1번 intent://" , url);
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                        if (existPackage != null) {
                            view.getContext().startActivity(intent);
                        } else {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                            marketIntent.setData(Uri.parse("market://details?id="+intent.getPackage()));
                            view.getContext().startActivity(marketIntent);
                        }
                        return true;
                    }catch (Exception e) {
                        Log.e(TAG,e.getMessage());
                    }
                } else if (url != null && url.startsWith("market://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            view.getContext().startActivity(intent);
                        }
                        return true;
                    } catch (URISyntaxException e) {
                        Log.e(TAG,e.getMessage());
                    }
                }

                view.loadUrl(url);
                return true;
            }

            public void onPageStarted(WebView view, String url,
                                      Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                //refreshLayout.setRefreshing(true);

                clearApplicationData(mContext);
                common.putSP("cur_url", url);
            }

            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
                boolean isPermissionAllowed = isNotiPermissionAllowed();

                if(!isPermissionAllowed && url.equals(getResources().getString(R.string.default_url))) {
                    Toast.makeText(getApplicationContext(),  "Smart Helper 알림을 허용해 주세요 - 문자메세지, 푸시알림, Music Controller 사용을 위해 필요합니다", Toast.LENGTH_LONG).show();
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
                progressBar.setVisibility(View.INVISIBLE);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(true);

                String cur_url = common.getSP("cur_url","EMPTY");

                if(cur_url.contains("index.php")) {
                    sendDeviceInfo();
                }
            }

            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {

           //<a>태그에서 target="_blank" 일 경우 외부 브라우저를 띄우기 위해 필요한override
           @Override
           public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
               WebView newWebView = new WebView(LauncherActivity.this);
               WebSettings webSettings = newWebView.getSettings();
               webSettings.setJavaScriptEnabled(true);

               ((WebView.WebViewTransport) resultMsg.obj).setWebView(newWebView);
               resultMsg.sendToTarget();
               return true;
           }

           @Override
           public void onProgressChanged(WebView view, int newProgress) {
               progressBar.setProgress(newProgress);
           }

            //For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {

                grantFileUploadPermission();

                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }

                mUMA = filePathCallback;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(LauncherActivity.this.getPackageManager()) != null) {

                    File photoFile = null;

                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCM);
                    } catch (IOException ex) {
                        Log.e(TAG, "Image file creation failed", ex);
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");
                Intent[] intentArray;

                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }


                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, FCR);

                return true;
            }
       });

        webView.addJavascriptInterface(new JavaScriptInterface(), getResources().getString(R.string.js_name));

    }

    private boolean grantFileUploadPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }else{
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                return false;
            }
        }else{
            return true;
        }
    }


    public void sendDeviceInfo(){

        String device_id;
        String device_token;
        String device_model;
        String app_version;
        String longitude;
        String latitude;

        device_id = common.getSP("device_id","");

        if(TextUtils.isEmpty(device_id))
        {
            String new_device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String new_device_token = FirebaseInstanceId.getInstance().getToken();
            String new_device_model = Build.BRAND + "/" + Build.MODEL + "/" + Build.ID + "/" + Build.VERSION.RELEASE;
            String new_app_version = BuildConfig.VERSION_NAME;

            common.putSP("device_id", new_device_id);
            common.putSP("device_token", new_device_token);
            common.putSP("device_model", new_device_model);
            common.putSP("app_version", new_app_version);
        }

        device_id = common.getSP("device_id","");
        device_token = common.getSP("device_token","");
        device_model = common.getSP("device_model","");
        app_version = common.getSP("app_version","");

        //앱버젼 변경시 업데이트
        String new_app_version = BuildConfig.VERSION_NAME;
        if(new_app_version!=app_version) {
            app_version = new_app_version;
            common.putSP("app_version", new_app_version);
        }

        //getGPS();
        longitude = common.getSP("longitude","");
        latitude = common.getSP("latitude","");

        String data = "act=setAppDeviceInfo&device_type=Android"
                + "&device_id="+device_id
                + "&device_token="+device_token
                +"&device_model="+device_model
                +"&app_version="+app_version
                +"&longitude="+longitude
                +"&latitude="+latitude
                ;

        String enc_data = Base64.encodeToString(data.getBytes(), 0);

        common.log("jsNativeToServer(enc_data)");
        doJavascript("javascript:jsNativeToServer('" + enc_data + "')");

        return;

    }

    // Create an image file
    private File createImageFile() throws IOException {

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private class JavaScriptInterface {

        @JavascriptInterface
        public void appLogin(final String data) {

            //T.show(mContext, data);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String allowed_app_list = common.getSP("allowed_app_list","EMPTY");
                    String allowed_app_list2 = common.getSP("allowed_app_list2","EMPTY");

                    boolean isConnected = mSmaManager.getInstance().isLoggedIn();
                    String isCon = String.valueOf(isConnected);

                    if(data.startsWith("DATA_DETAIL__")) {

                        String[] detail = data.split("__");
                        String type = detail[1];

                        switch (type) {

                            case "CHECK_APP_INSTALLED":
                                Boolean installed = appInstalled(String.valueOf(detail[2]));
                                if(installed==true) {
                                    doJavascript("javascript:appInstalled('" + String.valueOf(detail[2]) + "')");
                                }else{
                                    doJavascript("javascript:appNotInstalled('" + String.valueOf(detail[2]) + "')");
                                }
                                break;

                            case "PHONE_REPEAT":
                                String repeat = String.valueOf(detail[2]);
                                common.putSP("phone_repeat", repeat);
                                break;

                            case "ALLOWED_APP_INSERT":
                                String package_name = "/"+String.valueOf(detail[2])+"/";
                                String package_name2 = "/"+String.valueOf(detail[2])+"_"+String.valueOf(detail[3])+"/";
                                allowed_app_list += package_name;
                                allowed_app_list2 += package_name2;

                                common.putSP("allowed_app_list", allowed_app_list);
                                common.putSP("allowed_app_list2", allowed_app_list2);
                                break;

                            case "ALLOWED_APP_DELETE":
                                String package_name3 = "/"+String.valueOf(detail[2])+"/";
                                String package_name4 = "/"+String.valueOf(detail[2])+"_"+String.valueOf(detail[3])+"/";
                                String new_list = allowed_app_list.replaceAll(package_name3,"");
                                common.putSP("allowed_app_list", new_list);
                                String new_list2 = allowed_app_list2.replaceAll(package_name4,"");
                                common.putSP("allowed_app_list2", new_list2);
                                break;

                            case "CHECK_UPDATE":

                                int mem_no_app = checkMemNo();

                                if(isCon=="true" && mem_no_app>0) {
                                    //readDeviceSetup();
                                    sendDB();

                                    /*
                                    String sport_date = detail[2];
                                    String sport_time = detail[3];
                                    String heart_date = detail[4];
                                    String heart_time = detail[5];
                                    String sleep_date = detail[6];
                                    String sleep_time = detail[7];
                                    String exercise_date = detail[8];
                                    String tracker_date = detail[9];

                                    sendDB(sport_date,sport_time,heart_date,heart_time,sleep_date, sleep_time, exercise_date, tracker_date);
                                    */
                                    /*
                                    boolean update_success = sendDB(sport_date,sport_time,heart_date,heart_time,sleep_date, sleep_time, exercise_date, tracker_date);
                                    if(update_success == true)
                                    {
                                        T.show(mContext, "신규 데이터 업데이트");
                                        doJavascript("javascript:show_loading(4000)");
                                        //doJavascript("javascript:reload_delay(3500)");
                                    }
                                    */

                                }else{
                                    //T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                //mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.READ_SEDENTARINESS);

                                break;

                            case "SETUP":
                                Log.d("TTTL", "setup");
                                if(isConnected) {
                                    String lost_yn = String.valueOf(detail[2]);
                                    String gesture_yn= String.valueOf(detail[3]);
                                    String phone_yn = String.valueOf(detail[4]);
                                    String message_yn = String.valueOf(detail[5]);

                                    Log.d("TTTL", lost_yn + gesture_yn + phone_yn + message_yn);
                                    if(lost_yn.equals("Y")) {
                                        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_ANTI_LOST,true);
                                    } else {
                                        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_ANTI_LOST,false);
                                    }

                                    if(gesture_yn.equals("Y")) {
                                        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_RAISE_ON, true);
                                    } else {
                                        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_RAISE_ON, false);
                                    }

                                    if(phone_yn.equals("Y")){
                                        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_CALL, true);
                                    }else {
                                        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_CALL, false);
                                    }

                                    if(message_yn.equals("Y")){
                                        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NOTIFICATION, true);
                                    }else {
                                        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NOTIFICATION, false);
                                    }
                                }
                                break;

                            case "SIT":
                                if(isConnected) {

                                    SmaSedentarinessSettings ss = new SmaSedentarinessSettings();

                                    ss.setStart1(Integer.parseInt(detail[2]));
                                    ss.setEnd1(Integer.parseInt(detail[3]));
                                    ss.setEnabled1(Integer.parseInt(detail[4]));
                                    ss.setStart2(Integer.parseInt(detail[5]));
                                    ss.setEnd2(Integer.parseInt(detail[6]));
                                    ss.setEnabled2(Integer.parseInt(detail[7]));
                                    ss.setRepeat(Integer.parseInt(detail[8]));
                                    ss.setInterval(Integer.parseInt(detail[9]));

                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_SEDENTARINESS, ss);
                                }

                                break;

                            case "USER":
                                if(isConnected) {
                                    SmaUserInfo userInfo = new SmaUserInfo();
                                    userInfo.gender = Integer.parseInt(detail[2]);
                                    userInfo.age = Integer.parseInt(detail[3]);
                                    userInfo.height = Float.parseFloat(detail[4]);
                                    userInfo.weight = Float.parseFloat(detail[5]);
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_USER_INFO, userInfo);

                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_GOAL, Integer.parseInt(detail[6]), 4);
                                }

                                break;

                            case "ALARM":

                                /*
                                ArrayList<SmaAlarm> list = new ArrayList<>();
                                list.clear();

                                for (int i = 0; i < 4; i++) {//max length 8
                                    SmaAlarm alarm = new SmaAlarm();
                                    Calendar cal = Calendar.getInstance(SmaBleUtils.getDefaultTimeZone());
                                    cal.set(Calendar.HOUR_OF_DAY, i + 1);
                                    alarm.setTime(cal.getTimeInMillis());
                                    alarm.setTag("TAG" + (i + 1));
                                    list.add(alarm);
                                }

                                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_ALARMS, list);
                                */


                                List<SmaAlarm> alarms = new ArrayList<>();
                                Calendar cal = Calendar.getInstance(SmaBleUtils.getDefaultTimeZone());

                                SmaAlarm alarm1 = new SmaAlarm();
                                cal.set(Calendar.HOUR, Integer.parseInt(detail[2]));
                                cal.set(Calendar.MINUTE, Integer.parseInt(detail[3]));
                                alarm1.setTime(cal.getTimeInMillis());
                                alarm1.setRepeat(Integer.parseInt(detail[4]));
                                if(detail[5].equals("null"))
                                {
                                    alarm1.setTag(" ");
                                }
                                else {
                                    alarm1.setTag(detail[5]);
                                }
                                alarm1.setEnabled(Integer.parseInt(detail[6]));

                                SmaAlarm alarm2 = new SmaAlarm();
                                cal = Calendar.getInstance(SmaBleUtils.getDefaultTimeZone());
                                cal.set(Calendar.HOUR, Integer.parseInt(detail[7]));
                                cal.set(Calendar.MINUTE, Integer.parseInt(detail[8]));
                                alarm2.setTime(cal.getTimeInMillis());
                                alarm2.setRepeat(Integer.parseInt(detail[9]));
                                if(detail[10].equals("null"))
                                {
                                    alarm2.setTag(" ");
                                }
                                else {
                                    alarm2.setTag(detail[10]);
                                }
                                alarm2.setEnabled(Integer.parseInt(detail[11]));

                                SmaAlarm alarm3 = new SmaAlarm();
                                cal = Calendar.getInstance(SmaBleUtils.getDefaultTimeZone());
                                cal.set(Calendar.HOUR, Integer.parseInt(detail[12]));
                                cal.set(Calendar.MINUTE, Integer.parseInt(detail[13]));
                                alarm3.setTime(cal.getTimeInMillis());
                                alarm3.setRepeat(Integer.parseInt(detail[14]));
                                if(detail[15].equals("null"))
                                {
                                    alarm3.setTag(" ");
                                }
                                else {
                                    alarm3.setTag(detail[15]);
                                }
                                alarm3.setEnabled(Integer.parseInt(detail[16]));

                                SmaAlarm alarm4 = new SmaAlarm();
                                cal = Calendar.getInstance(SmaBleUtils.getDefaultTimeZone());
                                cal.set(Calendar.HOUR, Integer.parseInt(detail[17]));
                                cal.set(Calendar.MINUTE, Integer.parseInt(detail[18]));
                                alarm4.setTime(cal.getTimeInMillis());
                                alarm4.setRepeat(Integer.parseInt(detail[19]));
                                if(detail[20].equals("null"))
                                {
                                    alarm4.setTag(" ");
                                }
                                else {
                                    alarm4.setTag(detail[20]);
                                }
                                alarm4.setEnabled(Integer.parseInt(detail[21]));

                                SmaAlarm alarm5 = new SmaAlarm();
                                cal = Calendar.getInstance(SmaBleUtils.getDefaultTimeZone());
                                cal.set(Calendar.HOUR, Integer.parseInt(detail[22]));
                                cal.set(Calendar.MINUTE, Integer.parseInt(detail[23]));
                                alarm5.setTime(cal.getTimeInMillis());
                                alarm5.setRepeat(Integer.parseInt(detail[24]));
                                if(detail[25].equals("null"))
                                {
                                    alarm5.setTag(" ");
                                }
                                else {
                                    alarm5.setTag(detail[25]);
                                }
                                alarm5.setEnabled(Integer.parseInt(detail[26]));

                                SmaAlarm alarm6 = new SmaAlarm();
                                cal = Calendar.getInstance(SmaBleUtils.getDefaultTimeZone());
                                cal.set(Calendar.HOUR, Integer.parseInt(detail[27]));
                                cal.set(Calendar.MINUTE, Integer.parseInt(detail[28]));
                                alarm6.setTime(cal.getTimeInMillis());
                                alarm6.setRepeat(Integer.parseInt(detail[29]));
                                if(detail[30].equals("null"))
                                {
                                    alarm6.setTag(" ");
                                }
                                else {
                                    alarm6.setTag(detail[30]);
                                }
                                alarm6.setEnabled(Integer.parseInt(detail[31]));

                                SmaAlarm alarm7 = new SmaAlarm();
                                cal = Calendar.getInstance(SmaBleUtils.getDefaultTimeZone());
                                cal.set(Calendar.HOUR, Integer.parseInt(detail[32]));
                                cal.set(Calendar.MINUTE, Integer.parseInt(detail[33]));
                                alarm7.setTime(cal.getTimeInMillis());
                                alarm7.setRepeat(Integer.parseInt(detail[34]));
                                if(detail[35].equals("null"))
                                {
                                    alarm7.setTag("null");
                                }
                                else {
                                    alarm7.setTag(detail[35]);
                                }
                                alarm7.setEnabled(Integer.parseInt(detail[36]));

                                SmaAlarm alarm8 = new SmaAlarm();
                                cal = Calendar.getInstance(SmaBleUtils.getDefaultTimeZone());
                                cal.set(Calendar.HOUR, Integer.parseInt(detail[37]));
                                cal.set(Calendar.MINUTE, Integer.parseInt(detail[38]));
                                alarm8.setTime(cal.getTimeInMillis());
                                alarm8.setRepeat(Integer.parseInt(detail[39]));
                                if(detail[40].equals("null"))
                                {
                                    alarm8.setTag(" ");
                                }
                                else {
                                    alarm8.setTag(detail[40]);
                                }
                                alarm8.setEnabled(Integer.parseInt(detail[41]));

                                int alarm_count = Integer.parseInt(detail[42]);

                                for(int i=1; i<=alarm_count; i++) {
                                    switch(i){
                                        case 1:
                                            alarms.add(alarm1);
                                            break;

                                        case 2:
                                            alarms.add(alarm2);
                                            break;

                                        case 3:
                                            alarms.add(alarm3);
                                            break;

                                        case 4:
                                            alarms.add(alarm4);
                                            break;

                                        case 5:
                                            alarms.add(alarm5);
                                            break;

                                        case 6:
                                            alarms.add(alarm6);
                                            break;

                                        case 7:
                                            alarms.add(alarm7);
                                            break;

                                        case 8:
                                            alarms.add(alarm8);
                                            break;
                                    }
                                }

                                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_ALARMS, alarms);


                                break;

                            case "HEART":
                                if(isCon=="true") {
                                    SmaHeartRateSettings hs = new SmaHeartRateSettings();
                                    hs.setStart(Integer.parseInt(detail[2]));
                                    hs.setEnd(Integer.parseInt(detail[3]));
                                    hs.setEnabled(Integer.parseInt(detail[4]));
                                    hs.setInterval(Integer.parseInt(detail[5]));

                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_HEART_RATE, hs);
                                }
                                break;

                            case "GESTURE":
                                if(isCon=="true") {
                                    SmaLightSettings light = new SmaLightSettings();
                                    light.setStart1(Integer.parseInt(detail[2]));
                                    light.setEnd1(Integer.parseInt(detail[3]));
                                    light.setEnabled1(Integer.parseInt(detail[4]));

                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_LIGHT_TIME, light);
                                }
                                break;

                            case "LOGIN":
                                String mem_no = String.valueOf(detail[2]);
                                common.putSP("mem_no", mem_no);
                                break;

                        }

                    }else{

                        switch (data) {

                            case "FINISH_APP":
                                finishApp();
                                break;

                            case "KAKAO":
                                loginKakao();
                                break;

                            case "NAVER":
                                loginNaver();
                                break;

                            case "REFRESH_UNABLE" :
                                refreshLayout.setEnabled(false);
                                break;

                            case "REFRESH_ENABLE" :
                                refreshLayout.setEnabled(true);
                                break;

                            case "CHECK_UPDATE":

                                int mem_no_app = checkMemNo();

                                if(isCon=="true" && mem_no_app>0) {
                                    //readDeviceSetup();
                                    sendDB();
                                }

                                break;

                            case "CHECK_CONNECTION":
                                doJavascript("javascript:isConnected('" + isCon + "')");

                                if(isCon=="true") {
                                    readDeviceSetup();
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }

                                break;

                            case "CHECK_CONNECTION_TO_RELOAD":

                                if(isCon=="true") {
                                    doJavascript("javascript:reload()");
                                }

                                break;

                            case "MAIN":
                                startActivity(new Intent(mContext, CommandSetActivity.class));
                                break;

                            case "GET_APP_LIST":
                                String app_list = common.getSP("allowed_app_list2","");
                                doJavascript("javascript:appList('" + app_list + "')");
                                break;

                            case "LOGOUT":

                                common.putSP("mem_no","0");
                                break;

                            case "MESSAGE_APP":

                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                progressDialog = new ProgressDialog(LauncherActivity.this);
                                                progressDialog.setIndeterminate(true);
                                                progressDialog.setMessage("잠시만 기다려 주세요");
                                                progressDialog.show();
                                            }
                                        }, 0);
                                startActivity(new Intent(mContext, AppSelectActivity.class));

                                break;

                            case "MESSAGE_ON":

                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NOTIFICATION, true);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                //T.show(mContext, "문자 & 푸시알림을 디바이스로 전달합니다");
                                break;

                            case "MESSAGE_OFF":
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NOTIFICATION, false);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "CALL" :
                                if(isCon=="true") {
                                    if (!mSmaManager.getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_CALL)) {
                                        T.show(mContext, "Please go to 'SET' page to enable 'Call'");
                                        return;
                                    }
                                    mSmaManager.write(SmaManager.Cmd.NOTICE, SmaManager.Key.MESSAGE_v2, "Developer", "Incoming Call");
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "REMOTE_ON":
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_CALL, true);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "HEART_INTERVAL":
                                if(isCon=="true") {
                                    SmaHeartRateSettings srs = new SmaHeartRateSettings();
                                    srs.setSynced(1);
                                    srs.setEnabled(1);
                                    srs.setInterval(30);
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_HEART_RATE, srs);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "SLEEP_ON":
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_DETECT_SLEEP, true);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "SLEEP_OFF":
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_DETECT_SLEEP, false);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "SET_USER":
                                if(isCon=="true") {
                                    SmaUserInfo userInfo = new SmaUserInfo();
                                    userInfo.gender = 0;
                                    userInfo.age = 20;
                                    userInfo.height = 180f;
                                    userInfo.weight = 70f;
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_USER_INFO, userInfo);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "DISTURB_ON":
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NO_DISTURB, true);
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "DISTURB_OFF":
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NO_DISTURB, false);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "GESTURE_ON":
                                if(isCon=="true") {
                                T.show(mContext, "손목을 들어올리면 화면을 켜줍니다");
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_RAISE_ON, true);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "GESTURE_OFF":
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_RAISE_ON, false);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "PHONE_ON":
                                if(isCon=="true") {
                                    //T.show(mContext, "전화가 올경우 진동알림을 줍니다");
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_CALL, true);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "PHONE_OFF":
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_CALL, false);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "SEND_MESSAGE":
                                if(isCon=="true") {
                                    if(mSmaManager.getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NOTIFICATION)) {
                                        mSmaManager.write(SmaManager.Cmd.NOTICE, SmaManager.Key.MESSAGE_v2, "title", "content");
                                    }
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;


                            case "SET_TIME":
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_SYSTEM, new byte[]{1});
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_TIMEZONE, new SmaTimezone());
                                    SmaTime smaTime = new SmaTime();
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SYNC_TIME_2_DEVICE, smaTime);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    //T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "CAMERA":
                                if(isCon=="true") {
                                    T.show(mContext, "시계의 카메라 모양 버튼을 눌러 촬영하세요");
                                    startCamera();
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;


                            case "GET_GPS":
                                getGPS();
                                break;

                            case "SET_WEATHER":
                                if(isCon=="true") {
                                    setWeather();
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }


                                break;

                            case "SIT_ON" :
                                if(isCon=="true") {
                                //mSmaManager.readData(new byte[]{SmaManager.Key.READ_SEDENTARINESS});
                                //mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.READ_SEDENTARINESS);
                                //SmaSedentarinessSettings ss = new SmaSedentarinessSettings();
                                //Log.d("TTTL", String.valueOf(ss.getRepeat()));
                                //mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_SEDENTARINESS, ss);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "SIT_OFF" :
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_SEDENTARINESS, false);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "24HOUR_ON" :
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_24HOUR, android.text.format.DateFormat.is24HourFormat(mContext));
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "24HOUR_OFF" :
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_24HOUR, !android.text.format.DateFormat.is24HourFormat(mContext));
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "LOST_ON":
                                if(isCon=="true") {
                                    T.show(mContext, "휴대전화와 블루투스 연결이 끊어지면 진동알람을 줍니다");
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_ANTI_LOST,true);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "LOST_OFF":
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_ANTI_LOST, false);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "FIND_DEVICE":
                                if(isCon=="true") {
                                    T.show(mContext, "시계에 진동을 울려줍니다");
                                    mSmaManager.write(SmaManager.Cmd.NOTICE, SmaManager.Key.FIND_DEVICE, true);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "SPORT":
                                if(isCon=="true") {
                                    mSmaManager.readData(new byte[]{SmaManager.Key.SPORT});
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "SPORT2":
                                if(isCon=="true") {
                                    mSmaManager.readData(new byte[]{SmaManager.Key.SPORT2});
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "READ_DB":
                                readDB();
                                break;

                            case "RATE":
                                if(isCon=="true") {
                                    mSmaManager.readData(new byte[]{SmaManager.Key.RATE});
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "SLEEP":
                                if(isCon=="true") {
                                    mSmaManager.readData(new byte[]{SmaManager.Key.SLEEP});
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "BATTERY":
                                if(isCon=="true") {
                                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.READ_BATTERY);
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    //T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }

                                break;

                            case "SCAN":
                                mSmaManager.unbind();
                                //SmaManager.getInstance().unbind();
                                startActivity(new Intent(mContext, BindActivity.class));

                                break;


                            case "APP_SETTING":
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                                startActivity(intent);
                                break;

                            case "AGPS":
                                if(isCon=="true") {

                                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                    Uri uri = Uri.parse("http://wepodownload.mediatek.com/EPO_GR_3_1.DAT");

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                                    String file_name = "AGPS_" + sdf.format(new Date()) + ".dat";

                                    DownloadManager.Request request = new DownloadManager.Request(uri);

                                    //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setDestinationInExternalPublicDir(fileDir, file_name);
                                    long id = downloadmanager.enqueue(request);

                                    final Timer timer = new Timer();
                                    timer.schedule(new downloadTimer(id, file_name), 1500);

                                    //startActivity(new Intent(mContext, CommandSendFileActivity.class));
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "UNBIND":
                                if(isCon=="true") {
                                    if (mSmaManager.isBond()) {
                                        if (mDialog == null) {
                                            mDialog = new AlertDialog.Builder(mContext)
                                                    .setMessage(getString(R.string.confirm_unbind))
                                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            mSmaManager.unbind();
                                                            //startActivity(new Intent(mContext, BindActivity.class));
                                                            setResult(RESULT_OK);
                                                            doJavascript("javascript:reload()");
                                                            //finish();
                                                        }
                                                    })
                                                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    }).create();
                                        }
                                        mDialog.show();
                                    }
                                }else if(btAdapter.isEnabled()!=true){
                                    T.show(mContext, "휴대폰에서 블루투스 기능을 켜주세요");
                                }else{
                                    T.show(mContext, "기기와 연결이 안되어 있습니다");
                                }
                                break;

                            case "SHOW_ADLIB_REWARD_AD" :
                                adlibManager.loadFullInterstitialAd(mContext, new Handler(){
                                    public void handleMessage(Message message) {
                                        common.log(String.valueOf(message));

                                        try {
                                            switch (message.what) {
                                                case 2:
                                                    doJavascript("javascript:rewardComplete()");
                                                    break;

                                                case AdlibManager.DID_SUCCEED:
                                                    Log.d("TTTLADLIBr", "[Interstitial] onReceiveAd " + (String) message.obj);
                                                    doJavascript("javascript:rewardLoaded()");
                                                    break;

                                                // 전면배너 스케줄링 사용시, 각각의 플랫폼의 수신 실패 이벤트를 받습니다.
                                                case AdlibManager.DID_ERROR:
                                                    Log.d("TTTLADLIBr", "[Interstitial] onFailedToReceiveAd " + (String) message.obj);
                                                    break;

                                                // 전면배너 스케줄로 설정되어있는 모든 플랫폼의 수신이 실패했을 경우 이벤트를 받습니다.
                                                case AdlibManager.INTERSTITIAL_FAILED:
                                                    Log.d("TTTLADLIBr", "[Interstitial] All Failed.");
                                                    break;

                                                case AdlibManager.INTERSTITIAL_CLOSED:
                                                    Log.d("TTTLADLIBr", "[Interstitial] onClosedAd " + (String) message.obj);
                                                    break;
                                            }

                                        } catch (Exception e) {
                                        }

                                    }
                                });
                                break;

                            case "SHOW_ADLIB_FRONT_AD" :
                                adlibManager.loadFullInterstitialAd(mContext, new Handler(){
                                    public void handleMessage(Message message) {
                                        common.log(String.valueOf(message));

                                        try {
                                            switch (message.what) {
                                                // 전면배너 스케줄링 사용시, 각각의 플랫폼의 수신 실패 이벤트를 받습니다.
                                                case AdlibManager.DID_ERROR:
                                                    Log.d("TTTLADLIBr", "[Interstitial] onFailedToReceiveAd " + (String) message.obj);
                                                    break;

                                                // 전면배너 스케줄로 설정되어있는 모든 플랫폼의 수신이 실패했을 경우 이벤트를 받습니다.
                                                case AdlibManager.INTERSTITIAL_FAILED:
                                                    Log.d("TTTLADLIBr", "[Interstitial] All Failed.");
                                                    break;
                                            }

                                        } catch (Exception e) {
                                        }

                                    }
                                });
                                break;


                        }
                    }

                }
            });
        }
    }

    public boolean downloadComplete(int[] downloadIds) {

        DownloadManager mgr = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Query query = new DownloadManager.Query();

        for(int i = 0; i < downloadIds.length; i++) {

            query.setFilterById(downloadIds[i]);

            Cursor cur = mgr.query(query);

            if(cur.moveToNext()) {

                if(cur.getInt(cur.getColumnIndex(DownloadManager.COLUMN_STATUS)) != DownloadManager.STATUS_SUCCESSFUL) {

                    return false;
                }
            }
        }

        return true;
    }

    public void readDeviceSetup(){
        //mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.READ_SEDENTARINESS);
    }


    private void capture() {
        surfaceView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    saveImage(bitmap);



                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 사진을 찍게 되면 미리보기가 중지된다. 다시 미리보기를 시작하려면...
                camera.startPreview();
                //stopCamera();
            }
        });


        T.show(this, "촬영 성공 - SMART_HELPER 폴더에 저장완료");
    }

    //public boolean sendDB(String sport_date, String sport_time, String heart_date, String heart_time, String sleep_date, String sleep_time, String exercise_date, String tracker_date){
    public void sendDB() {

        //boolean update_success = false;

        String url = getResources().getString(R.string.api_url);
        update_data_status = common.getSP("update_data_status","");
        int data_gap = getDataUpdateTimeGap();

        if(!update_data_status.isEmpty() && data_gap<10)
        {
            T.show(mContext, "데이터 업데이트 진행중입니다");
            return;
        }
        else if(data_gap<10)
        {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cur_time = sdf.format(new Date());

        common.putSP("last_data_update",cur_time);
        common.putSP("update_data_status","UPDATE");

        mSmaManager.readData(new byte[]{
                SmaManager.Key.SPORT,
                SmaManager.Key.RATE,
                SmaManager.Key.SLEEP,
                SmaManager.Key.EXERCISE2,
                SmaManager.Key.TRACKER
        });

        update_data_status = common.getSP("update_data_status","");
        update_data_status = update_data_status.replaceAll("UPDATE","");
        common.putSP("update_data_status",update_data_status);

        /*
        try {
            String mem_no = common.getSP("mem_no","0");

            if(Integer.valueOf(mem_no)<1)
            {
                moveToLogin();
            }

            SQLiteDatabase ReadDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            Cursor c;

            //readDB();

            if(!sport_date.equals("null") && !sport_time.equals("null")) {
                c = ReadDB.rawQuery("SELECT * FROM sport WHERE s_time > ? AND s_date > ? AND mem_no = ?", new String[]{sport_time, sport_date, mem_no});
            } else {
                c = ReadDB.rawQuery("SELECT * FROM sport WHERE mem_no=?" , new String[]{mem_no});
            }

            if (c != null) {

                try {
                    String enc_data = URLEncoder.encode(cursorToString(c),"utf-8");

                    if(enc_data.length()>6) {
                        ContentValues values2 = new ContentValues();
                        values2.put("action", "sendData");
                        values2.put("table_name", "sport");
                        values2.put("mem_no", mem_no);
                        values2.put("data", enc_data);
                        //httpRequest(url, values2);
                        HttpAsyncRequest httpAsyncRequest = new HttpAsyncRequest(url, values2);
                        httpAsyncRequest.execute();
                        update_success = true;
                    }


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.d("TTTL", "==================SEND_DB_SPORT===================");
                if (c.moveToFirst()) {
                    do {

                        String log = "[sport_send_db]";

                        for (int j=0; j<c.getColumnCount(); j++)
                        {
                            log += c.getColumnName(j) + ":" + c.getString(c.getColumnIndex(c.getColumnName(j))) + "/";
                        }

                        Log.d("TTTL", log);

                    } while (c.moveToNext());
                }

            }

            c = null;

            if(!heart_date.equals("null") && !heart_time.equals("null")) {
                c = ReadDB.rawQuery("SELECT * FROM heart WHERE s_time > ? AND s_date > ? AND mem_no = ?", new String[]{heart_time, heart_date,mem_no});
            } else {
                c = ReadDB.rawQuery("SELECT * FROM heart WHERE mem_no=?" , new String[]{mem_no});
            }

            if (c != null) {

                try {
                    String enc_data = URLEncoder.encode(cursorToString(c),"utf-8");

                    if(enc_data.length()>6) {
                        ContentValues values2 = new ContentValues();
                        values2.put("action", "sendData");
                        values2.put("table_name", "heart");
                        values2.put("mem_no", mem_no);
                        values2.put("data", enc_data);
                        //httpRequest(url, values2);
                        HttpAsyncRequest httpAsyncRequest = new HttpAsyncRequest(url, values2);
                        httpAsyncRequest.execute();
                        update_success = true;
                    }


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.d("TTTL", "==================SEND_DB_HEART===================");
                if (c.moveToFirst()) {
                    do {

                        String log = "[heart_send_db]";

                        for (int j=0; j<c.getColumnCount(); j++)
                        {
                            log += c.getColumnName(j) + ":" + c.getString(c.getColumnIndex(c.getColumnName(j))) + "/";
                        }

                        Log.d("TTTL", log);

                    } while (c.moveToNext());
                }

            }

            c = null;

            if(!sleep_date.equals("null") && !sleep_time.equals("null")) {
                c = ReadDB.rawQuery("SELECT * FROM sleep WHERE s_time > ? AND s_date > ? AND mem_no = ?", new String[]{sleep_time, sleep_date,mem_no});
            } else {
                c = ReadDB.rawQuery("SELECT * FROM sleep WHERE mem_no=?" , new String[]{mem_no});
            }

            if (c != null) {

                try {
                    String enc_data = URLEncoder.encode(cursorToString(c),"utf-8");

                    if(enc_data.length()>6) {
                        ContentValues values2 = new ContentValues();
                        values2.put("action", "sendData");
                        values2.put("table_name", "sleep");
                        values2.put("mem_no", mem_no);
                        values2.put("data", enc_data);
                        //httpRequest(url, values2);
                        HttpAsyncRequest httpAsyncRequest = new HttpAsyncRequest(url, values2);
                        httpAsyncRequest.execute();
                        update_success = true;
                    }


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.d("TTTL", "==================SEND_DB_SLEEP===================");
                if (c.moveToFirst()) {
                    do {

                        String log = "[sleep_send_db]";

                        for (int j=0; j<c.getColumnCount(); j++)
                        {
                            log += c.getColumnName(j) + ":" + c.getString(c.getColumnIndex(c.getColumnName(j))) + "/";
                        }

                        Log.d("TTTL", log);

                    } while (c.moveToNext());
                }

            }

            c = null;

            if(!exercise_date.equals("null")) {
                c = ReadDB.rawQuery("SELECT * FROM exercise WHERE s_date > ? AND mem_no = ?", new String[]{exercise_date,mem_no});
            } else {
                c = ReadDB.rawQuery("SELECT * FROM exercise WHERE mem_no=?" , new String[]{mem_no});
            }

            if (c != null) {

                try {
                    String enc_data = URLEncoder.encode(cursorToString(c),"utf-8");

                    if(enc_data.length()>6) {
                        ContentValues values2 = new ContentValues();
                        values2.put("action", "sendData");
                        values2.put("table_name", "exercise");
                        values2.put("mem_no", mem_no);
                        values2.put("data", enc_data);
                        //httpRequest(url, values2);
                        HttpAsyncRequest httpAsyncRequest = new HttpAsyncRequest(url, values2);
                        httpAsyncRequest.execute();
                        update_success = true;
                    }


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.d("TTTL", "==================SEND_DB_EXERCISE===================");
                if (c.moveToFirst()) {
                    do {

                        String log = "[exercise_send_db]";

                        for (int j=0; j<c.getColumnCount(); j++)
                        {
                            log += c.getColumnName(j) + ":" + c.getString(c.getColumnIndex(c.getColumnName(j))) + "/";
                        }

                        Log.d("TTTL", log);

                    } while (c.moveToNext());
                }

            }

            c = null;

            if(!tracker_date.equals("null")) {
                c = ReadDB.rawQuery("SELECT * FROM tracker WHERE s_date > ? AND mem_no = ?", new String[]{tracker_date,mem_no});
            } else {
                c = ReadDB.rawQuery("SELECT * FROM tracker WHERE mem_no=?" , new String[]{mem_no});
            }

            if (c != null) {

                try {
                    String enc_data = URLEncoder.encode(cursorToString(c),"utf-8");

                    if(enc_data.length()>6) {
                        ContentValues values2 = new ContentValues();
                        values2.put("action", "sendData");
                        values2.put("table_name", "tracker");
                        values2.put("mem_no", mem_no);
                        values2.put("data", enc_data);
                        //httpRequest(url, values2);
                        HttpAsyncRequest httpAsyncRequest = new HttpAsyncRequest(url, values2);
                        httpAsyncRequest.execute();
                        update_success = true;
                    }


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.d("TTTL", "==================SEND_DB_TRACKER===================");
                if (c.moveToFirst()) {
                    do {

                        String log = "[tracker_send_db]";

                        for (int j=0; j<c.getColumnCount(); j++)
                        {
                            log += c.getColumnName(j) + ":" + c.getString(c.getColumnIndex(c.getColumnName(j))) + "/";
                        }

                        Log.d("TTTL", log);

                    } while (c.moveToNext());
                }

            }

            ReadDB.close();

        } catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("",  se.getMessage());
        }

        */
        return;

    }

    private String cursorToString(Cursor crs) {
        JSONArray arr = new JSONArray();
        crs.moveToFirst();
        while (!crs.isAfterLast()) {
            int nColumns = crs.getColumnCount();
            JSONObject row = new JSONObject();
            for (int i = 0 ; i < nColumns ; i++) {
                String colName = crs.getColumnName(i);
                if (colName != null) {
                    String val = "";
                    try {
                        switch (crs.getType(i)) {
                            case Cursor.FIELD_TYPE_BLOB   : row.put(colName, crs.getBlob(i).toString()); break;
                            case Cursor.FIELD_TYPE_FLOAT  : row.put(colName, crs.getDouble(i))         ; break;
                            case Cursor.FIELD_TYPE_INTEGER: row.put(colName, crs.getLong(i))           ; break;
                            case Cursor.FIELD_TYPE_NULL   : row.put(colName, null)                     ; break;
                            case Cursor.FIELD_TYPE_STRING : row.put(colName, crs.getString(i))         ; break;
                        }
                    } catch (JSONException e) {
                    }
                }
            }
            arr.put(row);
            if (!crs.moveToNext())
                break;
        }
        //crs.close(); // close the cursor
        return arr.toString();
    }

    private void  saveImage(Bitmap bitmapImage){
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/SMART_HELPER");
        myDir.mkdirs();
        Random generator = new Random();

        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage = imgRotate(bitmapImage);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

    }

    private Bitmap imgRotate(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        bmp.recycle();

        return resizedBitmap;
    }

    public void stopBtnClicked(View v)
    {
        stopCamera();
    }

    private void stopCamera() {

        //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);

        common.putSP("camera_on_yn","N");

        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.INTO_TAKE_PHOTO, false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                surfaceView.setVisibility(View.GONE);
                stopBtn.setVisibility(View.GONE);
            }
        });

    }

    private void startCamera() {

        //mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        //vol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        common.putSP("camera_on_yn","Y");

        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.INTO_TAKE_PHOTO, true);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                surfaceView.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.VISIBLE);
            }
        });
        /*
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        */
    }

    public void sendMms(String message) {
        Uri uri = Uri.parse("smsto:");
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", message);
        startActivity(it);
    }

    @Override
    public void onBackPressed() {

        String camera_yn = common.getSP("camera_on_yn", "N");

        if(camera_yn.equals("Y")) {
            stopCamera();
        }

        doJavascript("javascript:androidBackBtnClicked()");
        /*
        if(
                webView.getUrl().endsWith(getResources().getString(R.string.default_url))
                ||webView.getUrl().endsWith("https://www.smarthelper.co.kr/")
                ||webView.getUrl().endsWith("https://www.smarthelper.co.kr/login.php")
        )
        {
            finishApp();
        }
        else if (webView.canGoBack()) {
            //webView.goBack();
            doJavascript("javascript:androidBackBtnClicked()");
        }
        else {
            finishApp();
        }
        */
    }

    public void finishApp()
    {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 버튼을 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //webView.reload(); //카메라 이미지 업로드시 리로드되는 상황이 발생하여 리로드처리하지 않음

        /*
        // URL 세팅
        String sUrl = getIntent().getStringExtra("sUrl");
        if(sUrl!=null) {
            webView.loadUrl(sUrl);
        }
        else{
            webView.reload();
        }
        */

        String cur_url = common.getSP("cur_url","EMPTY");

        if(cur_url.contains("index.php") || cur_url.contains("device.php")) {
            T.show(mContext, "기기 연결 확인중");
            //doJavascript("javascript:show_loading(10000)"); //기기연결된 경우 reload
            //splashView();
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if(progressDialog!=null) {
                            progressDialog.dismiss();
                        }
                    }
                }, 0);

    }

    private void splashView(){

        /*
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        webView.reload();
                    }
                },
                2000);
                */
        //doJavascript("javascript:reload_delay(2500)");

        splash.setVisibility(View.VISIBLE);
        anim = AnimationUtils.loadAnimation(this, R.anim.loading);
        splash.setAnimation(anim);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        splash.setVisibility(View.GONE);
                    }
                },
                3000);


    }

    public void setWeather() {

        int gap = getWeatherUpdateTimeGap();
        Log.d("TTTL weather_gap", String.valueOf(gap));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cur_time = sdf.format(new Date());

        if(gap>60) {
            getGPS();
            String mem_no = common.getSP("mem_no", "0");

            if(Integer.valueOf(mem_no)>0) {

                common.putSP("last_weather_update",cur_time);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                                String mem_no = common.getSP("mem_no", "0");
                                String url = getResources().getString(R.string.api_url);
                                ContentValues values = new ContentValues();
                                values.put("action", "getWeatherFromServer");
                                values.put("mem_no", mem_no);

                                HttpAsyncRequest httpAssyncRequest = new HttpAsyncRequest(url, values);
                                httpAssyncRequest.execute();
                            }
                        }, 4000);
            }

        }
    }


    public int getWeatherUpdateTimeGap(){

        String last_weather_update_time = common.getSP("last_weather_update","2019-01-01 00:00:00");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cur_time = sdf.format(new Date());

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
        String cur_time2 = sdf2.format(new Date());

        if(cur_time2.equals("00") || cur_time2.equals("01") || cur_time2.equals("03")){
            return 0;
        }

        try {
            Date start = sdf.parse(last_weather_update_time);
            Date end = sdf.parse(cur_time);

            long diff = end.getTime() - start.getTime();

            int diffmin = (int) (diff / (60 * 1000));

            return diffmin;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getDataUpdateTimeGap(){

        String last_data_update_time = common.getSP("last_data_update","2019-01-01 00:00:00");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cur_time = sdf.format(new Date());

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
        String cur_time2 = sdf2.format(new Date());

        if(cur_time2.equals("00") || cur_time2.equals("01") || cur_time2.equals("03")){
            return 0;
        }

        try {
            Date start = sdf.parse(last_data_update_time);
            Date end = sdf.parse(cur_time);

            long diff = end.getTime() - start.getTime();

            int diffsec = (int) (diff / 1000);

            return diffsec;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //카카오
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
/*
        //구글피트
        if (resultCode == Activity.RESULT_OK && requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            getStepsCount();
            return;
        }
        */

        //카메라(이미지)업로드
        if (requestCode == FCR)
        {
            Uri[] results = null;

            if (resultCode == Activity.RESULT_OK) {
                if (null == mUMA) {
                    return;
                }
                if (data == null) {
                    //Capture Photo if no image available
                    if (mCM != null) {
                        results = new Uri[]{Uri.parse(mCM)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mUMA.onReceiveValue(results);
            mUMA = null;
        }

    }

    //카카오 로그인
    private void loginKakao() {
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL,LauncherActivity.this);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Log.d("TTTL",String.valueOf(exception));
            }
        }
    }

    private void requestMe() {

        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("TTTL","b");
            }

            @Override
            public void onSuccess(MeV2Response result) {
                Log.d("TTTL", "onSuccess" + result.toString());

                long userId = result.getId();
                id = String.valueOf(userId);
                gender = "";
                email = "";//result.getKakaoAccount().getEmail();
                name = result.getNickname();
                profile_image = result.getProfileImagePath();


                String sUrl = getResources().getString(R.string.sns_callback_url)
                        + "?login_type=kakao"
                        + "&success_yn=Y"
                        + "&id=" + id
                        + "&gender=" + gender
                        + "&name=" + name
                        + "&email=" + email
                        + "&profile_image=" + profile_image
                        ;
                Log.d("TTTL", sUrl);
                webView.loadUrl(sUrl);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }
    //카카오 로그인 끝

    //네이버로그인
    private void loginNaver() {
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(this, getResources().getString(R.string.naver_client_id), getResources().getString(R.string.naver_client_secret), "clientName");
        mOAuthLoginModule.startOauthLoginActivity(LauncherActivity.this,mOAuthLoginHandler);

    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {

        @Override
        public void run(boolean success) {
            if (success) {
                final String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                ProfileTask task = new ProfileTask();
                task.execute(accessToken);

            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();

                webView.reload();
            }
        };
    };

    class ProfileTask extends AsyncTask<String, Void, String> {
        String result;
        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];// 네이버 로그인 접근 토큰;
            String header = "Bearer " + token; // Bearer 다음에 공백 추가
            try {
                String apiURL = "https://openapi.naver.com/v1/nid/me";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", header);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                result = response.toString();
                br.close();
                System.out.println(response.toString());
            } catch (Exception e) {
                System.out.println(e);
            }
            //result 값은 JSONObject 형태로 넘어옵니다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //넘어온 result 값을 JSONObject 로 변환해주고, 값을 가져오면 되는데요.
                // result 를 Log에 찍어보면 어떻게 가져와야할 지 감이 오실거에요.
                JSONObject object = new JSONObject(result);
                if(object.getString("resultcode").equals("00")) {
                    JSONObject jsonObject = new JSONObject(object.getString("response"));
                    //Log.d("jsonObject", jsonObject.toString());

                    String sUrl = getResources().getString(R.string.sns_callback_url)
                            + "?login_type=naver"
                            + "&success_yn=Y"
                            + "&id=" + jsonObject.getString("id");

                    if(jsonObject.has("name"))
                    {
                        sUrl += "&name=" + jsonObject.getString("name");
                    }

                    if(jsonObject.has("email"))
                    {
                        sUrl += "&email=" + jsonObject.getString("email");
                    }

                    if(jsonObject.has("profile_image")) {
                        sUrl += "&profile_image=" + jsonObject.getString("profile_image");
                    }

                    common.log(sUrl);
                    webView.loadUrl(sUrl);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //네이버로그인 끝

    public void doJavascript(final String msg){

        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(msg);
            }
        });

    }

    class downloadTimer extends TimerTask {

        private final long id;
        private final String file_name;


        downloadTimer ( long id, String file_name )
        {
            this.id = id;
            this.file_name = file_name;
        }

        public void run() {
            //Do stuff

            DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor cursor = downloadmanager.query(new DownloadManager.Query().setFilterById(id));

            if (cursor != null && cursor.moveToNext()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                cursor.close();

                if (status == DownloadManager.STATUS_FAILED) {
                    // do something when failed
                    common.log("failed");
                    T.show(mContext, "AGPS 다운로드 실패");
                }
                else if (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_PAUSED) {
                    // do something pending or paused
                    common.log("pending");
                    T.show(mContext, "AGPS 다운로드 실패");
                }
                else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    // do something when successful
                    common.log("success");

                    File initialFile = new File(fileDir + "/" + file_name);
                    InputStream targetStream = null;
                    try {
                        targetStream = new FileInputStream(initialFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    SmaStream stream = new SmaStream();
                    stream.inputStream = targetStream;
                    //stream.inputStream = getResources().openRawResource(R.raw.epo_gr_3_1);
                    stream.flag = SmaStream.FLAG_LOCATION_ASSISTED;

                    mSmaManager.writeStream(stream);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String cur_time = sdf.format(new Date());

                    common.putSP("last_agps_update",cur_time);
                }
                else if (status == DownloadManager.STATUS_RUNNING) {
                    // do something when running
                    common.log("running");
                    final Timer timer = new Timer();
                    timer.schedule(new downloadTimer(id, file_name), 2000);
                }
            }
        }
    }

    public static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());

        long size = (long) getDirSize(appDir);

        // 캐시(Cache) 용량이 20Mb 이상일 경우 삭제
        if (appDir.exists() && size > 20000000) {
            String[] children = appDir.list();
            for (String s : children) {

                //shared_prefs 파일은 지우지 않도록 설정
                if(s.equals("shared_prefs")) continue;

                deleteDir(new File(appDir, s));
                Log.d("TTT", "File /data/data/"+context.getPackageName()+"/" + s + size + " DELETED");
            }
        }
    }

    public static long getDirSize(File dir){
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}
