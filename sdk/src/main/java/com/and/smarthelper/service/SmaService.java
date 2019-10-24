package com.and.smarthelper.service;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.and.smarthelper.activity.LauncherActivity;
import com.and.smarthelper.application.MyApplication;
import com.and.smarthelper.util.T;
import com.bestmafen.smablelib.component.SmaManager;
import com.bestmafen.smablelib.entity.SmaStream;
import com.bestmafen.smablelib.entity.SmaTime;
import com.bestmafen.smablelib.entity.SmaWeatherForecast;
import com.bestmafen.smablelib.entity.SmaWeatherRealTime;
import com.bestmafen.smablelib.util.SmaBleUtils;
import com.and.smarthelper.R;
import com.and.smarthelper.receiver.AppReceiver;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Timer;
import java.util.TimerTask;

public class SmaService extends Service {

    MyApplication common = new MyApplication(this);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SmaManager.getInstance().connect(true);
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        return super.onStartCommand(intent, flags, startId);
    }

    private final AppReceiver mReceiver = new AppReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_TICK)) {

                Log.d("TTTS", "tick");

                if(SmaManager.getInstance().isLoggedIn()) {
                    Log.d("TTTS", "isLoggedIn");
                    setWeather(); // 1시간마다 갱신
                    setAgps(); // 3시간마다 갱신
                }
                else
                {
                    Log.d("TTTS", "isLoggedOut");
                }

            }
        }
    };

    private int getWeatherTimeGap(){

        String last_update_time = common.getSP("last_weather_update","2019-01-01 00:00:00");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cur_time = sdf.format(new Date());

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
        String cur_time2 = sdf2.format(new Date());

        if(cur_time2.equals("01") || cur_time2.equals("02") || cur_time2.equals("03")){
            return 0;
        }

        try {
            Date start = sdf.parse(last_update_time);
            Date end = sdf.parse(cur_time);

            long diff = end.getTime() - start.getTime();

            int diffmin = (int) (diff / (60 * 1000));

            return diffmin;

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return 0;
    }

    private int getAgpsTimeGap(){

        String last_update_time = common.getSP("last_agps_update","2019-01-01 00:00:00");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cur_time = sdf.format(new Date());

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
        String cur_time2 = sdf2.format(new Date());

        if(cur_time2.equals("01") || cur_time2.equals("02") || cur_time2.equals("03")){
            return 0;
        }

        try {
            Date start = sdf.parse(last_update_time);
            Date end = sdf.parse(cur_time);

            long diff = end.getTime() - start.getTime();

            int diffmin = (int) (diff / (60 * 1000));

            return diffmin;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void getGPS() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("TTTSLocation Changes", location.toString());
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                common.putSP("longitude",Double.toString(lon));
                common.putSP("latitude",Double.toString(lat));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("TTTSStatus Changed", String.valueOf(status));
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("TTTSProvider Enabled", provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("TTTSProvider Disabled", provider);
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

                        Log.d("TTTSgetGPS","getGPS");
                        Boolean flag = false;
                        String mem_no = common.getSP("mem_no","0");
                        String last_longitude = common.getSP("last_longitude", "0");
                        String last_latitude = common.getSP("last_latitude", "0");
                        String longitude = common.getSP("longitude", "0");
                        String latitude = common.getSP("latitude", "0");

                        if(last_longitude=="0" && last_latitude=="0")
                        {
                            flag = true;
                        }
                        else if(last_longitude!="0" && last_latitude!="0") {
                            double lat_gap = Math.abs(Double.valueOf(last_latitude)-Double.valueOf(latitude));
                            double lon_gap = Math.abs(Double.valueOf(last_longitude)-Double.valueOf(longitude));
                            double gps_gap = lat_gap+lon_gap;

                            Log.d("TTTSlat_gap", String.valueOf(lat_gap));
                            Log.d("TTTSlon_gap", String.valueOf(lon_gap));
                            Log.d("TTTSgps_gap", String.valueOf(gps_gap));

                            if(gps_gap>0.1)
                            {
                                flag = true;
                            }
                        }

                        if(Integer.valueOf(mem_no) > 0 && flag==true) {
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
                2000);

    }

    private void setWeather(){

        int gap = getWeatherTimeGap();
        Log.d("TTTS weather_gap", String.valueOf(gap));

        //1시간마다 업데이트
        if(gap>60*1){

            getGPS();
            String mem_no=common.getSP("mem_no","0");

            if(Integer.valueOf(mem_no)>0){

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                                String mem_no=common.getSP("mem_no","0");
                                String url=getResources().getString(R.string.api_url);
                                ContentValues values=new ContentValues();
                                values.put("action","getWeatherFromServer");
                                values.put("mem_no",mem_no);

                                HttpAsyncRequest httpAssyncRequest=new HttpAsyncRequest(url,values);
                                httpAssyncRequest.execute();
                            }
                        }, 4000);


            }
        }
    }

    // 비동기식 http 통신
    private class HttpAsyncRequest extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public HttpAsyncRequest(String url, ContentValues values) {

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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                //넘어온 result 값을 JSONObject 로 변환해주고, 값을 가져오면 되는데요.
                // result 를 Log에 찍어보면 어떻게 가져와야할 지 감이 오실거에요.
                JSONObject object = new JSONObject(result);
                if(object.getString("resultcode").equals("00") && object.getString("act").equals("getWeatherFromServer")) {
                    JSONObject jsonObject = new JSONObject(object.getString("response"));
                    Log.d("TTTSjsonObject", jsonObject.toString());

                    if(
                        jsonObject.has("temperature") &&
                                jsonObject.has("humidity") &&
                                jsonObject.has("windSpeed") &&
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
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String cur_time = sdf.format(new Date());

                        common.putSP("last_weather_update",cur_time);

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

                        SmaManager.getInstance().write(SmaManager.Cmd.SET, SmaManager.Key.SET_WEATHER, smaWeather);

                        List<SmaWeatherForecast> forecasts = new ArrayList<>();
                        SmaWeatherForecast forecast = new SmaWeatherForecast();
                        forecast.temH = jsonObject.getInt("max1");
                        forecast.temL = jsonObject.getInt("min1");
                        forecast.weatherCode = jsonObject.getInt("weatherCode1");
                        forecast.ultraviolet = 1;
                        forecasts.add(forecast);
                        forecast = new SmaWeatherForecast();
                        forecast.temH = jsonObject.getInt("max2");
                        forecast.temL = jsonObject.getInt("min2");
                        forecast.weatherCode = jsonObject.getInt("weatherCode2");
                        forecast.ultraviolet = 2;
                        forecasts.add(forecast);
                        forecast = new SmaWeatherForecast();
                        forecast.temH = jsonObject.getInt("max3");
                        forecast.temL = jsonObject.getInt("min3");
                        forecast.weatherCode = jsonObject.getInt("weatherCode3");
                        forecast.ultraviolet = 3;
                        forecasts.add(forecast);

                        SmaManager.getInstance().write(SmaManager.Cmd.SET, SmaManager.Key.SET_FORECAST, forecasts);

                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            }

    }

    // http 통신
    private String httpRequest(String _url, ContentValues _params){

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

    private void setAgps(){

        int gap = getAgpsTimeGap();
        Log.d("TTTS agps gap", String.valueOf(gap));

        //6시간 마다 업데이트
        if(gap>60*6) {
            DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("http://wepodownload.mediatek.com/EPO_GR_3_1.DAT");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String file_name = "AGPS_" + sdf.format(new Date()) + ".dat";


            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalPublicDir(getApplicationContext().getCacheDir().getAbsolutePath(), file_name);
            long id = downloadmanager.enqueue(request);

            final Timer timer = new Timer();
            timer.schedule(new downloadTimer(id, file_name), 1500);
        }
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
                }
                else if (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_PAUSED) {
                    // do something pending or paused
                    common.log("pending");
                }
                else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    // do something when successful
                    common.log("success");

                    File initialFile = new File(Environment.getExternalStorageDirectory() + getApplicationContext().getCacheDir().getAbsolutePath() + "/" + file_name);
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

                    SmaManager.getInstance().writeStream(stream);

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


}
