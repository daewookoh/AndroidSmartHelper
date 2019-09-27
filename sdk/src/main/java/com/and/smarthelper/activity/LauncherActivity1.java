package com.and.smarthelper.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.bestmafen.smablelib.component.SmaManager;
import com.and.smarthelper.R;

public class LauncherActivity1 extends BaseActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_launcher;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initComplete(Bundle bundle) {
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        findViewById(R.id.v).animate().scaleX(1.06f).scaleY(1.06f).setDuration(2000).setListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                if (SmaManager.getInstance().isBond()) {
                    startActivity(new Intent(mContext, CommandListActivity.class));
                } else {
                    startActivity(new Intent(mContext, BindActivity.class));
                }
                finish();
            }
        });
    }
}
