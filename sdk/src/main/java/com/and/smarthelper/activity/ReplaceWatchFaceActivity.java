package com.and.smarthelper.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bestmafen.smablelib.component.SimpleSmaCallback;
import com.bestmafen.smablelib.component.SmaCallback;
import com.bestmafen.smablelib.component.SmaManager;
import com.and.smarthelper.R;

/**
 * Created by xiaokai on 2018/8/3.
 */
public class ReplaceWatchFaceActivity extends BaseActivity {
    private Button     btn_replace;
    private RadioGroup rg_selected;
    private RadioGroup rg_be_replaced;
    private Button     btn_switch_font;
    private RadioGroup rg_font;
    private TextView   tv_progress;

    private SmaManager  mSmaManager;
    private SmaCallback mSmaCallback;
    private byte mTransferAction = SmaManager.Key.XMODEM_ACTION_REPLACE_WATCHFACE1;
    private int  mWatchfaceRaw   = R.raw.watch_000005;
    private int  mFontRaw        = R.raw.font_simple;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_replace_watch_face;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mSmaManager = SmaManager.getInstance();
        mSmaManager.addSmaCallback(mSmaCallback = new SimpleSmaCallback() {

            @Override
            public void onLogin(final boolean ok) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        btn_replace.setEnabled(ok);
                    }
                });
            }

            @Override
            public void onTransferBuffer(boolean status, final int total, final int completed) {
                if (status) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            tv_progress.setText(String.valueOf((int) (completed * 100f / total)));
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void initView() {
        btn_replace = findViewById(R.id.btn_replace);
        rg_selected = findViewById(R.id.rg_selected);
        rg_be_replaced = findViewById(R.id.rg_be_replaced);
        btn_switch_font = findViewById(R.id.btn_switch_font);
        rg_font = findViewById(R.id.rg_font);
        tv_progress = findViewById(R.id.tv_progress);

        btn_replace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSmaManager.writeXMode(mTransferAction, getResources().openRawResource(mWatchfaceRaw));
            }
        });
        rg_selected.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_selected1:
                        mWatchfaceRaw = R.raw.watch_000005;
                        break;
                    case R.id.rb_selected2:
                        mWatchfaceRaw = R.raw.watch_000006;
                        break;
                    case R.id.rb_selected3:
                        mWatchfaceRaw = R.raw.watch_000009;
                        break;
                }
            }
        });
        rg_be_replaced.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_replace1:
                        mTransferAction = SmaManager.Key.XMODEM_ACTION_REPLACE_WATCHFACE1;
                        break;
                    case R.id.rb_replace2:
                        mTransferAction = SmaManager.Key.XMODEM_ACTION_REPLACE_WATCHFACE2;
                        break;
                    case R.id.rb_replace3:
                        mTransferAction = SmaManager.Key.XMODEM_ACTION_REPLACE_WATCHFACE3;
                        break;
                }
            }
        });

        btn_switch_font.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSmaManager.writeXMode(SmaManager.Key.XMODEM_ACTION_UPDATE_FONT, getResources().openRawResource(mFontRaw));
            }
        });
        rg_font.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_font_simple:
                        mFontRaw = R.raw.font_simple;
                        break;
                    case R.id.rb_font_whole:
                        mFontRaw = R.raw.font_whole;
                        break;
                }
            }
        });
    }

    @Override
    protected void initComplete(Bundle bundle) {

    }

    @Override
    protected void onDestroy() {
        mSmaManager.removeSmaCallback(mSmaCallback);
        super.onDestroy();
    }
}
