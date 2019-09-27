package com.and.smarthelper.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Toast工具类
 *
 * @author bestmafen 2016年4月17日
 */
public final class T {
    private static Toast mToast;
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void show(final Context context, final String msg, final int duration) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(context.getApplicationContext(),
                            msg, duration);
                } else {
                    mToast.setText(msg);
                    mToast.setDuration(duration);
                }
                mToast.show();
            }
        });
    }

    public static void show(final Context context, final String msg) {
        show(context, msg, Toast.LENGTH_LONG);
    }

    public static void show(final Context context, final int msgRes, int duration) {
        show(context, context.getResources().getString(msgRes), duration);
    }

    public static void show(final Context context, final int msgRes) {
        show(context, context.getResources().getString(msgRes));
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
