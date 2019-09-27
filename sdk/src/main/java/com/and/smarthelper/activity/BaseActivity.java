package com.and.smarthelper.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

/**
 * Activity的基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Context mContext;
    protected boolean isExit = false;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
        init(savedInstanceState);
        setContentView(getLayoutRes());
        initView();
        initComplete(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        isExit = true;
        if (mDialog != null) {
            mDialog.dismiss();
        }
        super.onDestroy();
    }

    protected abstract int getLayoutRes();

    protected abstract void init(Bundle savedInstanceState);

    protected abstract void initView();

    protected abstract void initComplete(Bundle bundle);

    protected void showProgress(String msg) {
        if (TextUtils.isEmpty(msg)) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        } else {
            if (mDialog == null) {
                mDialog = new ProgressDialog(mContext);
                mDialog.setCancelable(false);
            }
            mDialog.setMessage(msg);
            mDialog.show();
        }
    }

    protected void showProgress(int stringRes) {
        showProgress(getString(stringRes));
    }
}
