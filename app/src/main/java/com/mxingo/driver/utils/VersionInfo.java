package com.mxingo.driver.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.mxingo.driver.MyApplication;

/**
 * Created by zhouwei on 15/11/3.
 */
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
            getVersionCode = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getVersionCode;
    }
}
