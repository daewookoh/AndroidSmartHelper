package com.and.smarthelper.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bestmafen.smablelib.component.SmaManager;
import com.bestmafen.smablelib.entity.SmaTiming;
import com.and.smarthelper.R;

public class TimingActivity extends BaseActivity {
    private EditText edt_hour, edt_minute, edt_second;

    private SmaManager mSmaManager;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_timing;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mSmaManager = SmaManager.getInstance();
    }

    @Override
    protected void initView() {
        edt_hour = findViewById(R.id.edt_hour);
        edt_minute = findViewById(R.id.edt_minute);
        edt_second = findViewById(R.id.edt_second);
        findViewById(R.id.btn_timing).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    int hour = Integer.parseInt(edt_hour.getText().toString()) % 12;
                    int minute = Integer.parseInt(edt_minute.getText().toString()) % 60;
                    int second = Integer.parseInt(edt_second.getText().toString()) % 60;

                    if (mSmaManager.isLoggedIn()) {
                        SmaTiming timing = new SmaTiming();
                        timing.cmd = SmaTiming.CMD_POINTER;
                        timing.hour = hour;
                        timing.minute = minute;
                        timing.second = second;
                        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SYNC_TIME, timing);

                        timing.cmd = SmaTiming.CMD_START_NO_PARAM;
                        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SYNC_TIME, timing);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SmaTiming timing = new SmaTiming();
        timing.cmd = SmaTiming.CMD_CANCEL;
        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SYNC_TIME, timing);
    }

    @Override
    protected void initComplete(Bundle bundle) {
        SmaTiming timing = new SmaTiming();
        timing.cmd = SmaTiming.CMD_STOP;
        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SYNC_TIME, timing);

        timing.cmd = SmaTiming.CMD_PREPARE;
        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.SYNC_TIME, timing);
    }
}
