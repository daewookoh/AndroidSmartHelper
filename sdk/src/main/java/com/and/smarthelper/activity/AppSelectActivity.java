package com.and.smarthelper.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.and.smarthelper.R;
import com.and.smarthelper.application.MyApplication;
import com.and.smarthelper.util.AppAdapter;
import com.and.smarthelper.util.AppList;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppSelectActivity extends BaseActivity {

    private ListView lv;
    private ArrayAdapter<AppList> appAdapter;

    MyApplication common = new MyApplication(this);

    public void moveToSetting(){
        //Intent intent = new Intent(getBaseContext(), LauncherActivity.class);
        //intent.putExtra("sUrl", getResources().getString(R.string.setting_message_url));
        //startActivity(intent);
        ((LauncherActivity)LauncherActivity.mContext).refresh();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            moveToSetting();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void confirmClicked(View v){
        moveToSetting();
        //finish();
        //startActivity(new Intent(mContext, LauncherActivity.class));
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_app_list;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        appAdapter = new AppAdapter(this, getModel());

    }

    @Override
    protected void initView() {
        lv = (ListView) findViewById(R.id.lv);
        lv.setAdapter(appAdapter);
    }

    @Override
    protected void initComplete(Bundle bundle) {
    }

    private List<AppList> getModel() {
        List<AppList> list = new ArrayList<AppList>();

        Intent intent = new Intent(Intent.ACTION_MAIN,null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = this.getPackageManager().queryIntentActivities(intent,0);

        for(ResolveInfo resolveInfo : resolveInfoList){

            ActivityInfo activityInfo = resolveInfo.activityInfo;

            if(!isSystemPackage(resolveInfo)){

                String package_name = activityInfo.applicationInfo.packageName;

                try {
                    ApplicationInfo info = getPackageManager().getApplicationInfo(package_name, PackageManager.GET_META_DATA);
                    String app_name = (String) getPackageManager().getApplicationLabel(info);
                    Drawable app_icon = getPackageManager().getApplicationIcon(package_name);
                    list.add(get(app_name, package_name, app_icon));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }

        Collections.sort(list, myComparator);
        Collections.sort(list, myComparator2);

        return list;
    }

    private AppList get(String app_name, String package_name, Drawable icon) {

        Boolean is_checked=false;

        String allowed_app_list = common.getSP("allowed_app_list","EMPTY");

        if(allowed_app_list.contains(package_name))
        {
            is_checked = true;
        }

        return new AppList(app_name, package_name, icon, is_checked);
    }

    public boolean isSystemPackage(ResolveInfo resolveInfo){
        return false;
        //return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private final static Comparator<AppList> myComparator= new Comparator<AppList>() {
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(AppList o1, AppList o2) {
            return collator.compare(o1.getAppName(), o2.getAppName());
        }

    };

    private final static Comparator<AppList> myComparator2= new Comparator<AppList>() {
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(AppList o1, AppList o2) {

            String a = "C";
            String b = "C";

            if(o1.isSelected()==true) {
                a = "A"+o1.getAppName();
            }
            else if(o1.getAppName().toLowerCase().contains("message")
                    || o1.getAppName().toLowerCase().contains("sms")
                    || o1.getAppName().toLowerCase().contains("winner life")
                    || o1.getAppName().toLowerCase().contains("메세지")
                    || o1.getAppName().toLowerCase().contains("메시지")

            ) {

                Log.d("TTT", o1.getAppName().toLowerCase());
                a = "B"+o1.getAppName();
            }

            if(o2.isSelected()==true) {
                b = "A"+o1.getAppName();
            }
            else if(o2.getAppName().toLowerCase().contains("message")
                    || o2.getAppName().toLowerCase().contains("sms")
                    || o2.getAppName().toLowerCase().contains("winner life")
                    || o2.getAppName().toLowerCase().contains("메세지")
                    || o2.getAppName().toLowerCase().contains("메시지")
            ) {
                b = "B"+o1.getAppName();
            }

            return collator.compare(a,b);
        }

    };

}