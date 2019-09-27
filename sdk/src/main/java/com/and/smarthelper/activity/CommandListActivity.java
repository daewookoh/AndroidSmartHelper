package com.and.smarthelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.and.smarthelper.R;

/**
 * Created by Administrator on 2017/6/9.
 */

public class CommandListActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_UNBIND = 0x01;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_command_list;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_set).setOnClickListener(this);
        findViewById(R.id.btn_connect).setOnClickListener(this);
        findViewById(R.id.btn_notice).setOnClickListener(this);
        findViewById(R.id.btn_data).setOnClickListener(this);
        findViewById(R.id.btn_control).setOnClickListener(this);
    }

    @Override
    protected void initComplete(Bundle bundle) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_UNBIND && resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                startActivity(new Intent(mContext, CommandUpdateActivity.class));
                break;
            case R.id.btn_set:
                startActivity(new Intent(mContext, CommandSetActivity.class));
                break;
            case R.id.btn_connect:
                startActivityForResult(new Intent(mContext, CommandConnectActivity.class), REQUEST_UNBIND);
                break;
            case R.id.btn_notice:
                startActivity(new Intent(mContext, CommandNoticeActivity.class));
                break;
            case R.id.btn_data:
                startActivity(new Intent(mContext, CommandDataActivity.class));
                break;
            case R.id.btn_control:
                startActivity(new Intent(mContext, CommandControlActivity.class));
                break;
        }
    }
}
