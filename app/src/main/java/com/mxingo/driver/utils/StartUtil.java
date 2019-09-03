package com.mxingo.driver.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.mxingo.driver.widget.ShowToast;

import java.io.File;


/**
 * Created by zhouwei on 2016/11/4.
 */

public class StartUtil {
    public static double pi = 3.1415926535897932384626 * 3000.0 / 180.0;
    public final static String baiduMapPackage = "com.baidu.BaiduMap";
    public final static String gaodeMapPackage = "com.autonavi.minimap";

    public static void startBaiduMap(double toLat, double toLon, String addressStr, Context context) {
        Intent intentNavigate = null;
        try {
            if (!isInstallByread(baiduMapPackage)) {
                ShowToast.showCenter(context, "您未安装百度地图app");
                return;
            }
            String toAddress = "intent://map/direction?origin=我的位置&destination=";
            if (toLat != 0 && toLon != 0) {


//                CoordinateConverter converter = new CoordinateConverter();
//                converter.from(CoordinateConverter.CoordType.COMMON);
//                // sourceLatLng待转换坐标
//                LatLng sourceLatLng = new LatLng(toLat, toLon);
//                converter.coord(sourceLatLng);
//                LatLng desLatLng = converter.convert();
//                toAddress += "" + desLatLng.latitude + "," + desLatLng.longitude;
//
//

                toAddress += "" + toLat + "," + toLon;

            } else {
                if (!TextUtil.isEmpty(addressStr)) {
                    toAddress += "" + addressStr;
                } else {
                    Toast.makeText(context, "地址错误", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            toAddress += "&mode=driving#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
            Log.d("address", toAddress);
            intentNavigate = Intent.getIntent(toAddress);
            context.startActivity(intentNavigate);
        } catch (Exception e) {
            ShowToast.showCenter(context, "您未安装百度地图app");
        }
    }

    /**
     * 打开高德地图
     *
     * @param context
     * @param lat
     * @param log
     */

    public static void startGaoDeMap(Context context, double lat, double log) {
        try {
            if (isInstallByread(gaodeMapPackage)) {
                if (lat != 0 && log != 0) {

                    double x = log - 0.0065, y = lat - 0.006;
                    double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
                    double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
                    double gg_log = z * Math.cos(theta);
                    double gg_lat = z * Math.sin(theta);


//                    CoordinateConverter converter = new CoordinateConverter();
//                    //CoordType.GPS 待转换坐标类型
//                    converter.from(CoordinateConverter.CoordType.BD09LL);
//                    //sourceLatLng待转换坐标点 DPoint类型
//                    LatLng sourceLatLng = new LatLng(lat, log);
//                    converter.coord(sourceLatLng);
//                    //执行转换操作
//                    LatLng desLatLng = converter.convert();
//                    Intent intent = new Intent("android.intent.action.VIEW",
//                            Uri.parse("androidamap://navi?sourceApplication=积金专车司机端&lat=" + desLatLng.latitude + "&lon=" + desLatLng.longitude + "&dev=0&style=2"));

                    Intent intent = new Intent("android.intent.action.VIEW",
                            Uri.parse("androidamap://navi?sourceApplication=积金专车司机端&lat=" + gg_lat + "&lon=" + gg_log + "&dev=0&style=2"));
                    intent.setPackage(gaodeMapPackage);
                    context.startActivity(intent); // 启动调用
                } else {
                    Toast.makeText(context, "地址错误", Toast.LENGTH_SHORT).show();
                    return;
                }
//                Intent intent = new Intent("android.intent.action.VIEW",
//                        Uri.parse("androidamap://navi?sourceApplication=积金专车司机端&lat=" + lat + "&lon=" + log + "&dev=0&style=2"));
//                intent.setPackage(gaodeMapPackage);
//                context.startActivity(intent); // 启动调用
            } else {
                ShowToast.showCenter(context, "您未安装高德地图app");
            }
        } catch (Exception e) {
            ShowToast.showCenter(context, "您未安装高德地图app");
        }

    }

    //判断是否安装目标应用
    public static boolean isInstallByread(String packageName) {
        try {
            return new File("/data/data/" + packageName).exists();
        } catch (Exception e) {
            return false;
        }

    }

    public static void startPhone(String mobile, Activity activity) {
        if (TextUtil.isEmpty(mobile)) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
            activity.startActivity(intent);
        }
    }
}
