package com.and.smarthelper.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bestmafen.smablelib.component.SimpleSmaCallback;
import com.bestmafen.smablelib.component.SmaCallback;
import com.bestmafen.smablelib.component.SmaManager;
import com.and.smarthelper.R;
import com.and.smarthelper.util.T;

public class CommandNoticeActivity extends BaseActivity implements View.OnClickListener {
    private EditText edt_title, edt_content;
    private View btn_ring, btn_offhook, btn_idle, btn_notification, btn_find_device, btn_arrive_at;

    private SmaManager  mSmaManager;
    private SmaCallback mSmaCallback;

    private boolean isFinding = false;
    private byte    mPlace    = 0x01;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_command_notice;
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
            public void onFindPhone(boolean start) {
                T.show(mContext, "onFindPhone -> " + start);
            }
        });
    }

    @Override
    protected void initView() {
        edt_title = findViewById(R.id.edt_title);
        edt_content = findViewById(R.id.edt_content);
        btn_ring = findViewById(R.id.btn_ring);
        btn_ring.setOnClickListener(this);
        btn_offhook = findViewById(R.id.btn_offhook);
        btn_offhook.setOnClickListener(this);
        btn_idle = findViewById(R.id.btn_idle);
        btn_idle.setOnClickListener(this);
        btn_notification = findViewById(R.id.btn_notification);
        btn_notification.setOnClickListener(this);
        btn_find_device = findViewById(R.id.btn_find_device);
        btn_find_device.setOnClickListener(this);
        btn_arrive_at = findViewById(R.id.btn_arrive_at);
        btn_arrive_at.setOnClickListener(this);
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
        btn_ring.setEnabled(isConnected);
        btn_offhook.setEnabled(isConnected);
        btn_idle.setEnabled(isConnected);
        btn_notification.setEnabled(isConnected);
        btn_find_device.setEnabled(isConnected);
        btn_arrive_at.setEnabled(isConnected);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ring://响铃，Developer是名字，Incoming Call是标识，不能更改
                if (!mSmaManager.getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_CALL)) {//如果已经开启来电提醒
                    T.show(mContext, "Please go to 'SET' page to enable 'Call'");
                    return;
                }
                mSmaManager.write(SmaManager.Cmd.NOTICE, SmaManager.Key.MESSAGE_v2, "Developer", "Incoming Call");
                break;
            case R.id.btn_offhook://电话已接听
                mSmaManager.write(SmaManager.Cmd.NOTICE, SmaManager.Key.CALL_OFF_HOOK);
                break;
            case R.id.btn_idle://电话已挂断
                mSmaManager.write(SmaManager.Cmd.NOTICE, SmaManager.Key.CALL_IDLE);
                break;
            case R.id.btn_notification://推送消息
                if (!mSmaManager.getEnabled(SmaManager.Cmd.SET, SmaManager.Key.ENABLE_NOTIFICATION)) {//如果已经开启消息提醒
                    T.show(mContext, "Please go to 'SET' page to enable 'Notification'");
                    return;
                }
                String title = edt_title.getText().toString();
                String content = edt_content.getText().toString();
                mSmaManager.write(SmaManager.Cmd.NOTICE, SmaManager.Key.MESSAGE_v2, title, content);
                break;
            case R.id.btn_find_device:
                mSmaManager.write(SmaManager.Cmd.NOTICE, SmaManager.Key.FIND_DEVICE, isFinding = !isFinding);
                break;
            case R.id.btn_arrive_at:
                mSmaManager.write(SmaManager.Cmd.NOTICE, SmaManager.Key.ARRIVE_AT, new byte[]{mPlace++});//0x01~0x07
                if (mPlace > 0x07) {
                    mPlace = 1;
                }
                break;
        }
    }
}
