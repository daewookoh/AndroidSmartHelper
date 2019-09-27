package com.and.smarthelper.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bestmafen.smablelib.component.SimpleSmaCallback;
import com.bestmafen.smablelib.component.SmaCallback;
import com.bestmafen.smablelib.component.SmaManager;
import com.bestmafen.smablelib.entity.SmaStream;
import com.and.smarthelper.R;

/**
 * Created by xiaokai on 2018/8/3.
 */
public class ReplaceWatchFaceMActivity extends BaseActivity {
    private Button     btn_replace;
    private RadioGroup rg_selected;
    private RadioGroup rg_be_replaced;
    private TextView   tv_progress;

    private SmaManager  mSmaManager;
    private SmaCallback mSmaCallback;
    //[0]->the watch face you selected to replace
    //[1]->the watch face within watch to be replaced
    private String[] mExtras = new String[]{"1", "1"};

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_replace_watch_face_m;
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
            public void onTransferBuffer(final boolean status, final int total, final int completed) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (status) {
                            tv_progress.setText(String.valueOf((int) (completed * 100f / total)));
                        } else {
                            tv_progress.setText(R.string.replace_watchface_error);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void initView() {
        btn_replace = findViewById(R.id.btn_replace);
        rg_selected = findViewById(R.id.rg_selected);
        rg_be_replaced = findViewById(R.id.rg_be_replaced);
        tv_progress = findViewById(R.id.tv_progress);

        btn_replace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SmaStream stream = new SmaStream();
                int rawRes = mContext.getResources().getIdentifier(String.format("bp%s", mExtras[0]), "raw", getPackageName());
                stream.inputStream = mContext.getResources().openRawResource(rawRes);
                stream.flag = SmaStream.FLAG_M1_WATCHFACE;
                stream.extras = mExtras;
                mSmaManager.writeStream(stream);
            }
        });
        rg_selected.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_selected1:
                        mExtras[0] = "1";
                        break;
                    case R.id.rb_selected2:
                        mExtras[0] = "2";
                        break;
                    case R.id.rb_selected3:
                        mExtras[0] = "3";
                        break;
                    case R.id.rb_selected4:
                        mExtras[0] = "4";
                        break;
                }
            }
        });
        rg_be_replaced.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_replace1:
                        mExtras[1] = "1";
                        break;
                    case R.id.rb_replace2:
                        mExtras[1] = "2";
                        break;
                    case R.id.rb_replace3:
                        mExtras[1] = "3";
                        break;
                    case R.id.rb_replace4:
                        mExtras[1] = "4";
                        break;
                    case R.id.rb_replace5:
                        mExtras[1] = "5";
                        break;
                    case R.id.rb_replace6:
                        mExtras[1] = "6";
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
