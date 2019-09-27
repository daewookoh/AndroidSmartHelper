package com.and.smarthelper.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bestmafen.smablelib.component.SimpleSmaCallback;
import com.bestmafen.smablelib.component.SmaCallback;
import com.bestmafen.smablelib.component.SmaManager;
import com.and.smarthelper.R;
import com.and.smarthelper.util.T;

public class CommandSendFileActivity extends BaseActivity implements View.OnClickListener {
    private TextView    tv_progress;
    private View        btn_send_agps;
    private SmaManager  mSmaManager;
    private SmaCallback mSmaCallback;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_command_send_file;
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
            public void onTransferBuffer(boolean status, final int total, final int completed) {
                if (status) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            tv_progress.setText(getString(R.string.percent_d, completed * 100 / total));
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void initView() {
        tv_progress = findViewById(R.id.tv_progress);
        btn_send_agps = findViewById(R.id.btn_send_agps);
        btn_send_agps.setOnClickListener(this);
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
        btn_send_agps.setEnabled(isConnected);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_agps:
                //SmaStream stream = new SmaStream();
                // In your app, you should download the agps file on
                // http://wepodownload.mediatek.com/EPO_GR_3_1.DAT instead of using the raw resource.
                // In addition, you should sync timezone and time first.
                //stream.inputStream = getResources().openRawResource(R.raw.epo_gr_3_1);
                //stream.flag = SmaStream.FLAG_LOCATION_ASSISTED;
                //mSmaManager.writeStream(stream);
                break;
        }
    }
}
