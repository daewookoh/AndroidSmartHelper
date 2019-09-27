package com.and.smarthelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bestmafen.smablelib.component.SimpleSmaCallback;
import com.bestmafen.smablelib.component.SmaCallback;
import com.bestmafen.smablelib.component.SmaManager;
import com.and.smarthelper.R;
import com.and.smarthelper.util.T;

public class CommandUpdateActivity extends BaseActivity implements View.OnClickListener {
    private View btn_ota, btn_replace_watch_face, btn_update_m, btn_replace_watch_face_m;
    private SmaManager  mSmaManager;
    private SmaCallback mSmaCallback;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_command_update;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mSmaManager = SmaManager.getInstance().addSmaCallback(mSmaCallback = new SimpleSmaCallback() {

            @Override
            public void onLogin(final boolean ok) {//设备登录返回
                T.show(mContext, "onLogin -> " + ok);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        updateConnectionStatus(ok);
                    }
                });
            }

            @Override
            public void onOTA(boolean ok) {
                T.show(mContext, "onOTA -> " + ok);
                if (ok) {
                    startActivity(new Intent(mContext, FirmwareUpdateActivity.class));
                }
            }

            @Override
            public void onReadFlag(final String flag) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        btn_update_m.setEnabled(true);
                        Intent intent = new Intent(mContext, FirmwareUpdateMActivity.class);
                        intent.putExtra("flag", flag);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    protected void initView() {
        btn_ota = findViewById(R.id.btn_ota);
        btn_ota.setOnClickListener(this);
        btn_replace_watch_face = findViewById(R.id.btn_replace_watch_face);
        btn_update_m = findViewById(R.id.btn_update_m);
        btn_update_m.setOnClickListener(this);
        btn_replace_watch_face.setOnClickListener(this);
        btn_replace_watch_face_m = findViewById(R.id.btn_replace_watch_face_m);
        btn_replace_watch_face_m.setOnClickListener(this);
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
        btn_ota.setEnabled(isConnected);
        btn_replace_watch_face.setEnabled(isConnected);
        btn_update_m.setEnabled(isConnected);
        btn_replace_watch_face_m.setEnabled(isConnected);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ota:
                mSmaManager.write(SmaManager.Cmd.UPDATE, SmaManager.Key.OTA);
                break;
            case R.id.btn_replace_watch_face:
                startActivity(new Intent(mContext, ReplaceWatchFaceActivity.class));
                break;
            case R.id.btn_update_m:
                mSmaManager.read(SmaManager.UUID_SERVICE_FIRM_FLAG_M, SmaManager.UUID_CHARACTER_FIRM_FLAG_M);
                btn_update_m.setEnabled(false);
                break;
            case R.id.btn_replace_watch_face_m:
                startActivity(new Intent(mContext, ReplaceWatchFaceMActivity.class));
                break;
        }
    }
}
