package com.and.smarthelper.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.and.smarthelper.service.SmaService;
import com.bestmafen.smablelib.component.SmaManager;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

/**
 * Created by Administrator on 2017/5/15.
 */

public class MyApplication extends Application {

    private Context mContext;

    public MyApplication() {
    }

    public MyApplication(Context context) {

        this.mContext = context;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        SmaManager.getInstance().init(this, true);
        // You can initialize without "true" to disable the music control function if your device dose not support it.
        //SmaManager.getInstance().init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("TTTS", "A");
            startForegroundService(new Intent(this, SmaService.class));
        } else {
            Log.d("TTTS", "B");
            startService(new Intent(this, SmaService.class));
        }


        //startService(new Intent(this, NotificationService.class));
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public void putSP(String name, String value){
        SharedPreferences put_pref = mContext.getSharedPreferences("shared_pref", MODE_PRIVATE);
        SharedPreferences.Editor put_editor = put_pref.edit();
        put_editor.putString(name, value);
        log("putSP - " + name + ":" + value);
        put_editor.commit();
    }


    public String getSP(String name, String default_result) {
        SharedPreferences get_pref = mContext.getSharedPreferences("shared_pref", MODE_PRIVATE);
        String result = get_pref.getString(name, default_result);
        log("getSP - " + name + ":" + result);
        return result;
    }

    public void log(String string){

        if(string.length()>350) {
            string = string.substring(0, 350);
        }
        Log.d("TTT", String.valueOf(string));
    }

    private class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return MyApplication.this.getApplicationContext();
                }
            };
        }
    }

}
