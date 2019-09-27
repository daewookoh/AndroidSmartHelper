package com.and.smarthelper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bestmafen.easeblelib.util.L;
import com.bestmafen.smablelib.component.SimpleSmaCallback;
import com.bestmafen.smablelib.component.SmaCallback;
import com.bestmafen.smablelib.component.SmaManager;
import com.bestmafen.smablelib.entity.SmaAlarm;
import com.bestmafen.smablelib.entity.SmaHeartRateSettings;
import com.bestmafen.smablelib.entity.SmaSedentarinessSettings;
import com.bestmafen.smablelib.entity.SmaTime;
import com.bestmafen.smablelib.entity.SmaTimezone;
import com.bestmafen.smablelib.entity.SmaUserInfo;
import com.bestmafen.smablelib.entity.SmaWeatherForecast;
import com.bestmafen.smablelib.entity.SmaWeatherRealTime;
import com.bestmafen.smablelib.server.MyBleServer;
import com.bestmafen.smablelib.util.SmaBleUtils;
import com.and.smarthelper.R;
import com.and.smarthelper.util.T;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CommandSetActivity extends BaseActivity implements View.OnClickListener, CompoundButton
        .OnCheckedChangeListener {
    private View btn_sync_time, btn_set_user, btn_read_battery, btn_read_version, btn_read_version_m, btn_read_goal,
            btn_set_goal, btn_unit, btn_language, btn_switch_24hour, btn_set_time_zone, btn_read_sedentariness,
            btn_set_sedentariness, btn_heart_rate, btn_pair, btn_set_system, btn_set_anti_lost_tel, btn_read_alarm,
            btn_set_alarm, btn_timing, btn_weather_unit, btn_real_time_weather, btn_weather_forecast, btn_settings_completed;
    private CheckBox cb_antilost, cb_nodisturb, cb_call, cb_notification, cb_display_vertical, cb_raise_on;

    private AlertDialog         mDialog;
    private AlertDialog.Builder mBuilder;

    private SmaManager  mSmaManager;
    private SmaCallback mSmaCallback;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_command_set;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBuilder = new AlertDialog.Builder(mContext);

        mSmaManager = SmaManager.getInstance().addSmaCallback(mSmaCallback = new SimpleSmaCallback() {

            @Override
            public void onLogin(final boolean ok) {//设备登录返回
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        updateConnectionStatus(ok);
                    }
                });
                T.show(mContext, "onLogin -> " + ok);
            }

            @Override
            public void onAlarmsChange() {
                T.show(mContext, "onAlarmsChange");
            }

            @Override
            public void onReadAlarm(List<SmaAlarm> alarms) {
                T.show(mContext, "onReadAlarm -> " + alarms.toString());
            }

            @Override
            public void onReadBattery(final int battery) {//读取电量返回
                T.show(mContext, "onReadBattery -> " + battery + "%");
            }

            @Override
            public void onReadVersion(final String firmware, String bleVersion) {//读取固件版本返回
                T.show(mContext, "onReadVersion -> " + firmware);
            }

            @Override
            public void onGoalChange() {
                T.show(mContext, "onGoalChange");
            }

            @Override
            public void onReadGoal(int goal) {
                T.show(mContext, "onReadGoal -> " + goal);
            }

            @Override
            public void onSedentarinessChange() {
                T.show(mContext, "onSedentarinessChange");
            }

            @Override
            public void onReadSedentariness(SmaSedentarinessSettings sedentariness) {
                T.show(mContext, "onReadSedentariness -> " + sedentariness);
            }

            @Override
            public void onTakePhoto() {
                T.show(mContext, "onTakePhoto");
            }
        });
    }

    @Override
    protected void initView() {
        btn_sync_time = findViewById(R.id.btn_sync_time);
        btn_sync_time.setOnClickListener(this);
        btn_set_user = findViewById(R.id.btn_set_user);
        btn_set_user.setOnClickListener(this);
        btn_read_battery = findViewById(R.id.btn_read_battery);
        btn_read_battery.setOnClickListener(this);
        btn_read_version = findViewById(R.id.btn_read_version);
        btn_read_version.setOnClickListener(this);
        btn_read_version_m = findViewById(R.id.btn_read_version_m);
        btn_read_version_m.setOnClickListener(this);
        btn_read_goal = findViewById(R.id.btn_read_goal);
        btn_read_goal.setOnClickListener(this);
        btn_set_goal = findViewById(R.id.btn_set_goal);
        btn_set_goal.setOnClickListener(this);
        btn_unit = findViewById(R.id.btn_unit);
        btn_unit.setOnClickListener(this);
        btn_language = findViewById(R.id.btn_language);
        btn_language.setOnClickListener(this);
        btn_switch_24hour = findViewById(R.id.btn_24hour);
        btn_switch_24hour.setOnClickListener(this);
        btn_set_time_zone = findViewById(R.id.btn_time_zone);
        btn_set_time_zone.setOnClickListener(this);
        btn_read_sedentariness = findViewById(R.id.btn_read_sedentariness);
        btn_read_sedentariness.setOnClickListener(this);
        btn_set_sedentariness = findViewById(R.id.btn_set_sedentariness);
        btn_set_sedentariness.setOnClickListener(this);
        btn_pair = findViewById(R.id.btn_pair);
        btn_pair.setOnClickListener(this);
        btn_set_system = findViewById(R.id.btn_set_system);
        btn_set_system.setOnClickListener(this);
        btn_set_anti_lost_tel = findViewById(R.id.btn_set_anti_lost_tel);
        btn_set_anti_lost_tel.setOnClickListener(this);
        btn_heart_rate = findViewById(R.id.btn_heart_rate);
        btn_heart_rate.setOnClickListener(this);
        btn_read_alarm = findViewById(R.id.btn_read_alarm);
        btn_read_alarm.setOnClickListener(this);
        btn_set_alarm = findViewById(R.id.btn_set_alarm);
        btn_set_alarm.setOnClickListener(this);
        btn_timing = findViewById(R.id.btn_timing);
        btn_timing.setOnClickListener(this);
        btn_weather_unit = findViewById(R.id.btn_weather_unit);
        btn_weather_unit.setOnClickListener(this);
        btn_real_time_weather = findViewById(R.id.btn_real_time_weather);
        btn_real_time_weather.setOnClickListener(this);
        btn_weather_forecast = findViewById(R.id.btn_set_weather_forecast);
        btn_weather_forecast.setOnClickListener(this);
        findViewById(R.id.btn_enable_notification_listener).setOnClickListener(this);
        btn_settings_completed = findViewById(R.id.btn_settings_completed);
        btn_settings_completed.setOnClickListener(this);

        cb_antilost = (CheckBox) findViewById(R.id.cb_antilost);
        cb_antilost.setChecked(mSmaManager.getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_ANTI_LOST));
        cb_antilost.setOnCheckedChangeListener(this);
        cb_nodisturb = (CheckBox) findViewById(R.id.cb_nodisturb);
        cb_nodisturb.setChecked(mSmaManager.getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NO_DISTURB));
        cb_nodisturb.setOnCheckedChangeListener(this);
        cb_call = (CheckBox) findViewById(R.id.cb_call);
        cb_call.setChecked(mSmaManager.getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_CALL));
        cb_call.setOnCheckedChangeListener(this);
        cb_notification = (CheckBox) findViewById(R.id.cb_notification);
        cb_notification.setChecked(mSmaManager.getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NOTIFICATION));
        cb_notification.setOnCheckedChangeListener(this);
        cb_display_vertical = (CheckBox) findViewById(R.id.cb_display_vertical);
        cb_display_vertical.setChecked(mSmaManager.getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_DISPLAY_VERTICAL));
        cb_display_vertical.setOnCheckedChangeListener(this);
        cb_raise_on = (CheckBox) findViewById(R.id.cb_raise_on);
        cb_raise_on.setChecked(mSmaManager.getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_RAISE_ON));
        cb_raise_on.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initComplete(Bundle bundle) {
        updateConnectionStatus(mSmaManager.isLoggedIn());
    }

    @Override
    protected void onDestroy() {
        mSmaManager.removeSmaCallback(mSmaCallback);
        super.onDestroy();
    }

    private void updateConnectionStatus(boolean isConnected) {
        btn_sync_time.setEnabled(isConnected);
        btn_set_user.setEnabled(isConnected);
        btn_read_battery.setEnabled(isConnected);
        btn_read_version.setEnabled(isConnected);
        btn_read_version_m.setEnabled(isConnected);
        btn_read_goal.setEnabled(isConnected);
        btn_set_goal.setEnabled(isConnected);
        btn_read_sedentariness.setEnabled(isConnected);
        btn_set_sedentariness.setEnabled(isConnected);
        btn_pair.setEnabled(isConnected);
        btn_set_system.setEnabled(isConnected);
        btn_set_anti_lost_tel.setEnabled(isConnected);
        btn_heart_rate.setEnabled(isConnected);
        btn_read_alarm.setEnabled(isConnected);
        btn_set_alarm.setEnabled(isConnected);
        btn_timing.setEnabled(isConnected);
        btn_weather_unit.setEnabled(isConnected);
        btn_real_time_weather.setEnabled(isConnected);
        btn_weather_forecast.setEnabled(isConnected);
        btn_unit.setEnabled(isConnected);
        btn_language.setEnabled(isConnected);
        btn_switch_24hour.setEnabled(isConnected);
        btn_set_time_zone.setEnabled(isConnected);
        btn_settings_completed.setEnabled(isConnected);

        cb_antilost.setEnabled(isConnected);
        cb_nodisturb.setEnabled(isConnected);
        cb_call.setEnabled(isConnected);
        cb_notification.setEnabled(isConnected);
        cb_display_vertical.setEnabled(isConnected);
        cb_raise_on.setEnabled(isConnected);
    }

    private void setGoal() {
        mDialog = mBuilder.setSingleChoiceItems(R.array.goals, 0, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!SmaManager.getInstance().isLoggedIn()) return;

                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_GOAL, (which + 1) * 1000, 4);//0~0xffffffff
                mDialog.dismiss();
            }
        }).create();
        mDialog.show();
    }

    private void setUnit() {
        mDialog = mBuilder.setSingleChoiceItems(R.array.units, 0, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!SmaManager.getInstance().isLoggedIn()) return;

                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_UNIT, which, 1);//Metric 0,Imperial 1
                mDialog.dismiss();
            }
        }).create();
        mDialog.show();
    }

    private void setLanguage() {
        mDialog = mBuilder.setSingleChoiceItems(R.array.Languages, 0, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!SmaManager.getInstance().isLoggedIn()) return;

                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_LANGUAGE, which, 1);
//                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_LANGUAGE, SmaBleUtils.getLanguageCode(), 1);
                mDialog.dismiss();
            }
        }).create();
        mDialog.show();
    }

    private void setHourFormat() {
        mDialog = mBuilder.setSingleChoiceItems(R.array.hour_format, 0, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!SmaManager.getInstance().isLoggedIn()) return;

                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_24HOUR, which == 0);
//               mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_24HOUR, !android.text.format.DateFormat
// .is24HourFormatn
// (mContext));
                mDialog.dismiss();
            }
        }).create();
        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sync_time://同步手机上的时间到设备
                Calendar c = Calendar.getInstance();
                SmaTime smaTime = new SmaTime();
                smaTime.year = (byte) (c.get(Calendar.YEAR) - 2000);
                smaTime.month = (byte) (c.get(Calendar.MONTH) + 1);
                smaTime.day = (byte) c.get(Calendar.DAY_OF_MONTH);
                smaTime.hour = (byte) c.get(Calendar.HOUR_OF_DAY);
                smaTime.minute = (byte) c.get(Calendar.MINUTE);
                smaTime.second = (byte) c.get(Calendar.SECOND);
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SYNC_TIME_2_DEVICE, smaTime);
                break;
            case R.id.btn_set_user://设置个人信息到设备
                SmaUserInfo userInfo = new SmaUserInfo();
                userInfo.gender = 0;
                userInfo.age = 20;
                userInfo.height = 180f;
                userInfo.weight = 70f;
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_USER_INFO, userInfo);
                break;
            case R.id.btn_read_battery://读取设备电量
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.READ_BATTERY);
                break;
            case R.id.btn_read_version://读取设备固件版本
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.READ_VERSION);
                break;
            case R.id.btn_read_version_m://读取设备固件版本（巴西圆表）
                mSmaManager.read(SmaManager.UUID_SERVICE_FIRM_FLAG, SmaManager.UUID_CHARACTER_M_VERSION);
                break;
            case R.id.btn_read_goal://读取运动目标到设备
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.READ_GOAL);
                break;
            case R.id.btn_set_goal://设置运动目标到设备
                setGoal();
                break;
            case R.id.btn_unit://设置单位
                setUnit();
                break;
            case R.id.btn_language://设置语言
                setLanguage();
                break;
            case R.id.btn_24hour://设置小时制
                setHourFormat();
                break;
            case R.id.btn_time_zone://设置时区
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_TIMEZONE, new SmaTimezone());
                break;
            case R.id.btn_timing:
                startActivity(new Intent(this, TimingActivity.class));
                break;
            case R.id.btn_weather_unit:
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_WEATHER_UNIT, SmaWeatherRealTime.WEATHER_UNIT_METRIC, 1);
                break;
            case R.id.btn_real_time_weather:
                SmaWeatherRealTime weather = new SmaWeatherRealTime();
                weather.time = new SmaTime();
                weather.temperature = 30;
                weather.weatherCode = SmaWeatherRealTime.SUNNY;
                weather.precipitation = 20;
                weather.visibility = 3;
                weather.windSpeed = 10;
                weather.humidity = 75;
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_WEATHER, weather);
                break;
            case R.id.btn_set_weather_forecast:
                List<SmaWeatherForecast> forecasts = new ArrayList<>();
                SmaWeatherForecast forecast = new SmaWeatherForecast();
                forecast.temH = 31;
                forecast.temL = 21;
                forecast.weatherCode = SmaWeatherRealTime.SUNNY;
                forecast.ultraviolet = 1;
                forecasts.add(forecast);
                forecast = new SmaWeatherForecast();
                forecast.temH = 32;
                forecast.temL = 22;
                forecast.weatherCode = SmaWeatherRealTime.SNOWY;
                forecast.ultraviolet = 2;
                forecasts.add(forecast);
                forecast = new SmaWeatherForecast();
                forecast.temH = 33;
                forecast.temL = 23;
                forecast.weatherCode = SmaWeatherRealTime.FOGGY;
                forecast.ultraviolet = 3;
                forecasts.add(forecast);
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_FORECAST, forecasts);
                break;
            case R.id.btn_read_sedentariness:
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.READ_SEDENTARINESS);
                break;
            case R.id.btn_set_sedentariness://久坐设置
                SmaSedentarinessSettings ss = new SmaSedentarinessSettings();
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_SEDENTARINESS, ss);
                L.d("SET_ALARMS " + ss.toString());
                break;
            case R.id.btn_pair:
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.REQUEST_PAIR);
                break;
            case R.id.btn_set_system:
                //Android[1] IOS[2]
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_SYSTEM, new byte[]{1});
                break;
            case R.id.btn_set_anti_lost_tel:
                mSmaManager.setAntiLostTel("nick name", "010-1234-4567");//nickname.getByte("UTF-8").length<=21
                break;
            case R.id.btn_heart_rate://心率监测设置
                SmaHeartRateSettings srs = new SmaHeartRateSettings();
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_HEART_RATE, srs);
                break;
            case R.id.btn_read_alarm:
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.READ_ALARM);
                break;
            case R.id.btn_set_alarm://闹钟
                //Note:although the alarm is disabled, you should write it to device if your device is SMA-09/Round
                //watch series,otherwise you do not need to write it to device.
                ArrayList<SmaAlarm> list = new ArrayList<>();
                for (int i = 0; i < 8; i++) {//max length 8
                    SmaAlarm alarm = new SmaAlarm();
                    Calendar cal = Calendar.getInstance(SmaBleUtils.getDefaultTimeZone());
                    cal.set(Calendar.HOUR_OF_DAY, i + 1);
                    alarm.setTime(cal.getTimeInMillis());
                    alarm.setTag("TAG" + (i + 1));
                    list.add(alarm);
                }
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_ALARMS, list);
//                L.d("SET_ALARMS " + list.toString());
                break;
            case R.id.btn_enable_notification_listener:
                if (MyBleServer.getInstance().isNotificationListenerEnabled()) {
                    T.show(mContext, "Notification listener has been enabled");
                } else {
                    MyBleServer.getInstance().enableNotificationListener(CommandSetActivity.this);
                }
                break;
            case R.id.btn_settings_completed:
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SETTINGS_COMPLETED);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        byte key = 0;
        switch (buttonView.getId()) {
            case R.id.cb_antilost:
                key = SmaManager.Key.ENABLE_ANTI_LOST;
                break;
            case R.id.cb_nodisturb:
                key = SmaManager.Key.ENABLE_NO_DISTURB;
                break;
            case R.id.cb_call:
                key = SmaManager.Key.ENABLE_CALL;
                break;
            case R.id.cb_notification:
                key = SmaManager.Key.ENABLE_NOTIFICATION;
                break;
            case R.id.cb_display_vertical:
                key = SmaManager.Key.ENABLE_DISPLAY_VERTICAL;
                break;
            case R.id.cb_raise_on:
                key = SmaManager.Key.ENABLE_RAISE_ON;
                break;
        }
        mSmaManager.write(SmaManager.Cmd.SET, key, isChecked);
    }
}
