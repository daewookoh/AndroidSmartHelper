package com.and.smarthelper.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.bestmafen.smablelib.component.SmaManager;
import com.and.smarthelper.R;

/**
 * Created by Administrator on 2017/6/9.
 */
public class CommandConnectActivity extends BaseActivity implements View.OnClickListener {
    private AlertDialog mDialog;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_command_connect;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_bind).setOnClickListener(this);
        findViewById(R.id.btn_unbind).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_connect_classic).setOnClickListener(this);
    }

    @Override
    protected void initComplete(Bundle bundle) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bind:

                break;
            case R.id.btn_unbind:
                if (SmaManager.getInstance().isBond()) {
                    if (mDialog == null) {
                        mDialog = new AlertDialog.Builder(mContext)
                                .setMessage(getString(R.string.confirm_unbind))
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SmaManager.getInstance().unbind();
                                        startActivity(new Intent(mContext, BindActivity.class));
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create();
                    }
                    mDialog.show();
                }
                break;
            case R.id.btn_login:

                break;
            case R.id.btn_connect_classic:
                SmaManager.getInstance().connectClassic();
                break;
        }
    }
}
