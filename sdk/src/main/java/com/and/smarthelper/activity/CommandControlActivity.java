package com.and.smarthelper.activity;

import android.os.Bundle;
import android.view.View;

import com.bestmafen.smablelib.component.SimpleSmaCallback;
import com.bestmafen.smablelib.component.SmaCallback;
import com.bestmafen.smablelib.component.SmaManager;
import com.and.smarthelper.R;
import com.and.smarthelper.util.T;

public class CommandControlActivity extends BaseActivity implements View.OnClickListener {
    private View btn_ring, btn_take_photo;
    private SmaManager mSmaManager;
    private SmaCallback mSmaCallback;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_command_control;
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
            public void onKeyDown(byte key) {
                T.show(mContext, "onKeyDown -> " + key);
            }
        });
    }

    @Override
    protected void initView() {
        btn_ring = findViewById(R.id.btn_ring);
        btn_ring.setOnClickListener(this);
        btn_take_photo = findViewById(R.id.btn_take_photo);
        btn_take_photo.setOnClickListener(this);
    }

    @Override
    protected void initComplete(Bundle bundle) {
        updateConnectionStatus(mSmaManager.isLoggedIn());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.INTO_TAKE_PHOTO, false);
        mSmaManager.removeSmaCallback(mSmaCallback);
    }

    private void updateConnectionStatus(boolean isConnected) {
        btn_ring.setEnabled(isConnected);
        btn_take_photo.setEnabled(isConnected);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ring:
                if (!mSmaManager.getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_CALL)) {//如果已经开启来电提醒
                    T.show(mContext, "Please go to 'SET' page to enable 'Call'");
                    return;
                }
                mSmaManager.write(SmaManager.Cmd.NOTICE, SmaManager.Key.MESSAGE_v2, "Developer", "Incoming Call");
                break;
            case R.id.btn_take_photo:
                mSmaManager.write(SmaManager.Cmd.SET, SmaManager.Key.INTO_TAKE_PHOTO, true);
                break;
        }
    }
}
