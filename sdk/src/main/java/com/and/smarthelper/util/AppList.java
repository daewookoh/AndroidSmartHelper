package com.and.smarthelper.util;

import android.graphics.drawable.Drawable;

public class AppList {
/*
    private String name;
    Drawable icon;

    public AppList(String name, Drawable icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }
*/
    private String app_name;
    private String package_name;
    Drawable icon;
    private boolean selected;

    public AppList(String app_name, String package_name, Drawable icon, Boolean is_checked) {
        this.app_name = app_name;
        this.package_name = package_name;
        this.icon = icon;

       if(is_checked==true)
       {
           selected = true;
       }
       else {
           selected = false;
       }
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return app_name;
    }

    public void setAppName(String app_name) {
        this.app_name = app_name;
    }

    public String getPackageName() {
        return package_name;
    }

    public void setPackageName(String package_name) {
        this.app_name = package_name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
