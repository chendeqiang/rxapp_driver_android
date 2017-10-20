package com.mxingo.driver.utils;

import android.os.Build;

/**
 * Created by zhouwei on 2017/7/10.
 */

public class DevInfo {

    /**
     * 得到UUID
     */
    public static String getInfo() {
        try {
            String devInfo = " phoneInfo:" +
                    "Product: " + android.os.Build.PRODUCT
                    + ", VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE
                    + ", BRAND: " + android.os.Build.BRAND
                    + ", DEVICE: " + Build.DEVICE;
            return devInfo;

        } catch (Exception e) {
            return "";
        }
    }
}
