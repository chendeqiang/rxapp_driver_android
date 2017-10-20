package com.mxingo.driver.module.base.map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.mxingo.driver.MyApplication;
import com.mxingo.driver.R;
import com.mxingo.driver.utils.TextUtil;

/**
 * Created by zhouwei on 16/9/30.
 */

public class BaiduMapUtil {

    private LocationClient mLocClient;
    private BDLocationListener myListener;
    private LocationClientOption option;
    private ACache aCache;
    private BaiduMap baiduMap;
    private MyLocationData locData;
    private MapView mv;
    private MyLocationConfiguration config;

    private static BaiduMapUtil instance;

    public static BaiduMapUtil getInstance() {
        if (instance == null) {
            instance = new BaiduMapUtil();
        }
        return instance;
    }


    public BaiduMapUtil() {
        initBaidu();
    }

    public void setBaiduMap(MapView baiduMapView) {
        this.baiduMap = baiduMapView.getMap();
        mv = baiduMapView;
//        this.baiduMap.setOnMapClickListener(this);
        LatLng centPoint = new LatLng(30.281307, 120.170892);
        config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_point_car));
        this.baiduMap.setMyLocationConfiguration(config);
        this.baiduMap.setMyLocationEnabled(true);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(centPoint)
                .zoom(15)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        this.baiduMap.setMapStatus(mapStatusUpdate);
        // 删除百度地图logo
        baiduMapView.removeViewAt(1);
        // 删除比例尺控件
        baiduMapView.removeViewAt(2);
        baiduMapView.showZoomControls(false);
    }

    private void initBaidu() {
        if (mLocClient == null) {
            mLocClient = new LocationClient(MyApplication.application);
        }
        if (aCache == null) {
            aCache = ACache.get(MyApplication.application);
        }
        if (myListener == null) {
            myListener = new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation == null) {
                        return;
                    }

                    if (bdLocation.getLatitude() != 0 && bdLocation.getLongitude() != 0) {
                        aCache.put("lon", bdLocation.getLongitude() + "");
                        aCache.put("lat", bdLocation.getLatitude() + "");
                    }
                    if (baiduMap != null) {
                        locData = new MyLocationData.Builder()
                                .accuracy(0)// 定位精度半径
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(bdLocation.getDirection()).latitude(bdLocation.getLatitude())// 纬度
                                .longitude(bdLocation.getLongitude()).build();// 经度
                        baiduMap.setMyLocationData(locData);

                        LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                        MapStatusUpdate su = MapStatusUpdateFactory.newLatLng(ll);
                        baiduMap.animateMapStatus(su);
                    }

//                    Log.d("lon,lat", bdLocation.getCoorType() + "," + bdLocation.getDirection());
                }

            };
        }

        if (option == null) {
            option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            option.setCoorType(CoordType.GCJ02.name());//可选，默认gcj02，设置返回的定位结果坐标系 百度坐标系
            int span = 0;
            option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            option.setOpenGps(true);//可选，默认false,设置是否使用gps
            option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            //      option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            //      option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
            option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            //      option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
            option.setNeedDeviceDirect(true);
            mLocClient.setLocOption(option);
        }
    }

    public void registerLocationListener() {
        if (myListener != null && mLocClient != null) {
            mLocClient.registerLocationListener(myListener);
            mLocClient.start();
        }
    }

    public void unregisterLocationListener() {
        if (myListener != null && mLocClient != null) {
            mLocClient.unRegisterLocationListener(myListener);
            mLocClient.stop();
        }
    }

    public double getLon() {
        if (aCache == null)
            return 0;
        String lonStr = aCache.getAsString("lon");
        if (!TextUtil.isEmpty(lonStr)) {
            return Double.valueOf(lonStr);
        } else {
            return 0;
        }
    }

    public double getLat() {
        if (aCache == null)
            return 0;
        String latStr = aCache.getAsString("lat");
        if (!TextUtil.isEmpty(latStr)) {
            return Double.valueOf(latStr);
        } else {
            return 0;
        }
    }
}
