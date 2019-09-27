package com.and.smarthelper.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bestmafen.smablelib.component.SimpleSmaCallback;
import com.bestmafen.smablelib.component.SmaCallback;
import com.bestmafen.smablelib.component.SmaManager;
import com.bestmafen.smablelib.entity.SmaBloodPressure;
import com.bestmafen.smablelib.entity.SmaCycling;
import com.bestmafen.smablelib.entity.SmaCyclingExtra2;
import com.bestmafen.smablelib.entity.SmaExercise;
import com.bestmafen.smablelib.entity.SmaHeartRate;
import com.bestmafen.smablelib.entity.SmaSleep;
import com.bestmafen.smablelib.entity.SmaSport;
import com.bestmafen.smablelib.entity.SmaTracker;
import com.and.smarthelper.R;
import com.and.smarthelper.util.T;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommandDataActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv;
    private View     btn_read_data;

    private SmaManager  mSmaManager;
    private SmaCallback mSmaCallback;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_command_data;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
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
            public void onReadSportData(final List<SmaSport> list) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        append(list.toString());
                    }
                });
            }

            @Override
            public void onReadHeartRateData(final List<SmaHeartRate> list) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        append(list.toString());
                    }
                });
            }

            @Override
            public void onReadSleepData(final List<SmaSleep> list) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        append(list.toString());
                    }
                });
            }

            @Override
            public void onReadBloodPressure(final List<SmaBloodPressure> list) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        append(list.toString());
                    }
                });
            }

            @Override
            public void onReadCycling(final List<SmaCycling> list) {
                //Each two data is combined into a complete Cycling record;
                //The first data marks the beginning of Cycling; the second data marks the end of Cycling
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        append(list.toString());
                    }
                });
            }

            @Override
            public void onReadExercise(final List<SmaExercise> list) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        append(list.toString());
                    }
                });
            }

            @Override
            public void onReadTracker(final List<SmaTracker> list) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        append(list.toString());
                    }
                });
            }

            @Override
            public void onCycling(int status) {
                if (status == SmaCycling.START || status == SmaCycling.GOING) {
                    //start Location
                    //SmaCyclingExtra2 information needs to be sent to the device after each positioning
                    //After the device gets the information, it will display
                    SmaCyclingExtra2 cyclingExtra = new SmaCyclingExtra2();
                    cyclingExtra.speed = 10.1f;
                    cyclingExtra.altitude = 20;
                    cyclingExtra.distance = 30.3f;
                    mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SET_CYCLING_EXTRA, cyclingExtra);
                } else if (status == SmaCycling.END) {
                    //stop location
                }
            }

            @Override
            public void onReadDataFinished(final boolean ok) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        append("onReadDataFinished -> " + ok);
                    }
                });
            }
        });
    }

    @Override
    protected void initView() {
        tv = (TextView) findViewById(R.id.tv);
        btn_read_data = findViewById(R.id.btn_read_data);
        btn_read_data.setOnClickListener(this);
    }

    @Override
    protected void initComplete(Bundle bundle) {
        updateConnectionStatus(mSmaManager.isLoggedIn());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmaManager.removeSmaCallback(mSmaCallback);
    }

    private void updateConnectionStatus(boolean isConnected) {
        btn_read_data.setEnabled(isConnected);
    }

    /**
     * 在显示区域追加信息
     *
     * @param msg 追加的信息
     */
    private void append(final String msg) {
        if (tv != null) {
            if (!TextUtils.isEmpty(tv.getText())) {
                tv.append("\n" + getTime() + " : " + msg);
            } else {
                tv.append(getTime() + " : " + msg);
            }
        }
    }

    private SimpleDateFormat mDF = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    private String getTime() {
        Date d = new Date();
        return mDF.format(d);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_read_data://读取运动、心率和睡眠数据
//                mSmaManager.readData(new byte[]{SmaManager.Key.SPORT, SmaManager.Key.RATE, SmaManager.Key.SLEEP});
//                mSmaManager.readData(new byte[]{SmaManager.Key.SPORT, SmaManager.Key.RATE, SmaManager.Key.SLEEP,
//                        SmaManager.Key.CYCLING, SmaManager.Key.SWIM});
//                mSmaManager.readData(new byte[]{SmaManager.Key.SPORT, SmaManager.Key.RATE, SmaManager.Key.SLEEP,
//                        SmaManager.Key.TRACKER});
                //The code used by different devices is different. According to the function of the device, choose different keys.
                mSmaManager.readData(new byte[]{SmaManager.Key.SPORT, SmaManager.Key.RATE, SmaManager.Key.SLEEP,
                        SmaManager.Key.BLOOD_PRESSURE,
                        SmaManager.Key.CYCLING});
                tv.setText("");
                break;
        }
    }
}
