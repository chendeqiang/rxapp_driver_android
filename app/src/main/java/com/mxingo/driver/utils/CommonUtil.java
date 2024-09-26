package com.mxingo.driver.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.mxingo.driver.module.base.data.UserInfoPreferences;
import com.mxingo.driver.module.base.map.CurrentLocation;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class CommonUtil {
    private static DecimalFormat df = new DecimalFormat("######0.00");

    public static final double DISTANCE = 0.0001;

    /**
     * 定位时间（单位：秒）
     */
    public static long locTime;

    /**
     * 纬度
     */
    public static double latitude;

    /**
     * 经度
     */
    public static double longitude;

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    /**
     * 获取当前时间戳(单位：秒)
     *
     * @return
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 校验double数值是否为0
     *
     * @param value
     * @return
     */
    public static boolean isEqualToZero(double value) {
        return Math.abs(value - 0.0) < 0.01 ? true : false;
    }

    /**
     * 经纬度是否为(0,0)点
     *
     * @return
     */
    public static boolean isZeroPoint(double latitude, double longitude) {
        return isEqualToZero(latitude) && isEqualToZero(longitude);
    }

    /**
     * 将字符串转为时间戳
     */
    public static long toTimeStamp(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        Date date;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return date.getTime() / 1000;
    }

    /**
     * 获取时分秒
     *
     * @param timestamp 时间戳（单位：毫秒）
     * @return
     */
    public static String getHMS(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            return sdf.format(new Timestamp(timestamp));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(timestamp);
    }

    /**
     * 获取年月日 时分秒
     *
     * @param timestamp 时间戳（单位：毫秒）
     * @return
     */
    public static String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.format(new Timestamp(timestamp));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(timestamp);
    }

    public static String formatSecond(int second) {
        String format = "%1$,02d:%2$,02d:%3$,02d";
        Integer hours = second / (60 * 60);
        Integer minutes = second / 60 - hours * 60;
        Integer seconds = second - minutes * 60 - hours * 60 * 60;
        Object[] array = new Object[]{hours, minutes, seconds};
        return String.format(format, array);
    }

    public static final String formatDouble(double doubleValue) {
        return df.format(doubleValue);
    }

    /**
     * 计算x方向每次移动的距离
     */
    public static double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    /**
     * 保存当前定位点
     */
    public static void saveCurrentLocation() {
        StringBuffer locationInfo = new StringBuffer();
        locationInfo.append(CurrentLocation.locTime);
        locationInfo.append(";");
        locationInfo.append(CurrentLocation.latitude);
        locationInfo.append(";");
        locationInfo.append(CurrentLocation.longitude);
        UserInfoPreferences.getInstance().putLastLocation(locationInfo.toString());
    }

    /**
     * 获取设备IMEI码
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getImei(Context context) {
        String imei;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                imei = tm.getImei();
            }else {
                imei = tm.getDeviceId();
            }

        } catch (Exception e) {
            imei = "myTrace";
        }
        return imei;
    }
}
