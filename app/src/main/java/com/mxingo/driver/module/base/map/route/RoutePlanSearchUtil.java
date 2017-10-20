package com.mxingo.driver.module.base.map.route;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.mxingo.driver.module.base.log.LogUtils;

import java.util.ArrayList;

/**
 * Created by zhouwei on 2016/12/6.
 */

public class RoutePlanSearchUtil implements OnGetRoutePlanResultListener {
    private RoutePlanSearch mSearch = null;
    private BaiduMap mBaiduMap;
    private Context context;

    public RoutePlanSearchUtil(BaiduMap mBaiduMap, Context context) {
        this.mBaiduMap = mBaiduMap;
        this.context = context;
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
    }

    public void drivingPlan(double startLat, double startLon, double endLat, double endLon) {
        LatLng startP = new LatLng(startLat, startLon);
        LatLng endP = new LatLng(endLat, endLon);
        PlanNode stNode = PlanNode.withLocation(startP);
        PlanNode enNode = PlanNode.withLocation(endP);
        mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }

        if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            driverLine(drivingRouteResult);
        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    private void driverLine(DrivingRouteResult result) {
        mBaiduMap.clear();
        DrivingRouteOverlay overlayDistanceMin = new MyDrivingRouteOverlay(mBaiduMap);
        DrivingRouteLine drivingDistanceMin = null;
        for (int i = 0; i < result.getRouteLines().size(); i++) {
            if (drivingDistanceMin == null || drivingDistanceMin.getDistance() > result.getRouteLines().get(i).getDistance()) {
                drivingDistanceMin = result.getRouteLines().get(i);
            }

        }

        DrivingRouteOverlay overlayTimeMin = new MyDrivingRouteOverlay(mBaiduMap);
        DrivingRouteLine drivingTimeMin = null;
        for (int i = 0; i < result.getRouteLines().size(); i++) {
            if (drivingTimeMin == null || drivingTimeMin.getDuration() > result.getRouteLines().get(i).getDuration()) {
                drivingTimeMin = result.getRouteLines().get(i);
            }

        }

        overlayDistanceMin.setColorId(Color.parseColor("#00BD00"));
        overlayDistanceMin.setData(drivingDistanceMin);
        overlayDistanceMin.addToMap();
        overlayDistanceMin.zoomToSpan();

        if (drivingDistanceMin != drivingTimeMin) {
            overlayTimeMin.setColorId(Color.parseColor("#908F8F"));
            overlayTimeMin.setData(drivingTimeMin);
            overlayTimeMin.addToMap();
            overlayTimeMin.zoomToSpan();
        }

    }

    public void destroy() {
        if (mSearch != null) {
            mSearch.destroy();
        }
    }
}
