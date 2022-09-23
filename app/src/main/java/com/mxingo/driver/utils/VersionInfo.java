package com.mxingo.driver.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.mxingo.driver.MyApplication;

public class VersionInfo {

    public static String getVersionName() {
        String versionName = "1.0.0";
        try {
            PackageManager packageManager = MyApplication.application.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(MyApplication.application.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getVersionCode() {
        int getVersionCode = 0;
        try {
            PackageManager packageManager = MyApplication.application.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(MyApplication.application.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                getVersionCode = (int) info.getLongVersionCode();
            }else {
                getVersionCode = info.versionCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return getVersionCode;
    }
}
