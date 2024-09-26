package com.mxingo.driver.module.base.map.route;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.mxingo.driver.R;
import com.mxingo.driver.model.OrderEntity;
import com.mxingo.driver.model.QryOrderEntity;
import com.mxingo.driver.module.BaseActivity;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.MyPresenter;
import com.mxingo.driver.utils.AMapUtil;
import com.mxingo.driver.utils.Constants;
import com.mxingo.driver.widget.MyProgress;
import com.mxingo.driver.widget.ShowToast;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 导航路线图层类。
 */
@SuppressWarnings("deprecation")
public class SearchRouteActivity extends BaseActivity implements RouteSearch.OnRouteSearchListener {

    @BindView(R.id.tv_toolbar_title)
    TextView mTvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.mapView_search)
    MapView mMapViewSearch;
    @Inject
    MyPresenter presenter;
    private MyProgress progress;
    private String orderNo;
    private String driverNo;
    private OrderEntity order;

    private AMap aMap;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;

    public static void startSearchRouteActivity(Context context, String orderNo, String driverNo) {
        context.startActivity(new Intent(context, SearchRouteActivity.class)
                .putExtra(Constants.ORDER_NO, orderNo)
                .putExtra(Constants.DRIVER_NO, driverNo));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //高德地图
        MapsInitializer.updatePrivacyShow(getApplicationContext(),true,true);
        MapsInitializer.updatePrivacyAgree(getApplicationContext(),true);
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_search_route);
        ButterKnife.bind(this);
        ComponentHolder.getAppComponent().inject(this);

        presenter.register(this);
        progress = new MyProgress(this);
        setToolbar(mToolbar);
        mTvToolbarTitle.setText("路线详情");

        mMapViewSearch.onCreate(savedInstanceState);
        initMap();
        orderNo = getIntent().getStringExtra(Constants.ORDER_NO);
        driverNo = getIntent().getStringExtra(Constants.DRIVER_NO);
        progress.show();
        presenter.qryOrder(orderNo);
    }

    private void initMap() {
        if (aMap == null) {
            aMap= mMapViewSearch.getMap();
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        }
        try {
            mRouteSearch = new RouteSearch(this);
            mRouteSearch.setRouteSearchListener(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void loadData(Object object) {
        if (object.getClass() == QryOrderEntity.class) {
            qryOrder((QryOrderEntity) object);
            progress.dismiss();
        }
    }

    private void qryOrder(QryOrderEntity orderEntity) {
        if (orderEntity.rspCode.equals("00")) {
            order = orderEntity.order;
            driveRoute(order.startLat, order.startLon, order.endLat, order.endLon);
        }
    }


    //路径规划2
    private void driveRoute(double startLat, double startLon, double endLat, double endLon) {
        LatLonPoint mStartPoint = new LatLonPoint(startLat, startLon);//起点
        LatLonPoint mEndPoint = new LatLonPoint(endLat, endLon);//终点
        aMap.addMarker(new MarkerOptions().position(AMapUtil.convertToLatLng(mStartPoint)).icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        aMap.addMarker(new MarkerOptions().position(AMapUtil.convertToLatLng(mEndPoint)).icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
        if (mStartPoint == null) {
            ShowToast.show(this, "定位中，稍后再试...");
            return;
        }
        if (mEndPoint == null) {
            ShowToast.show(this, "终点未设置");
        }
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        // 驾车路径规划
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null,
                null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapViewSearch.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapViewSearch.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapViewSearch.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapViewSearch.onDestroy();
        presenter.unregister(this);
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    if(drivePath == null) {
                        return;
                    }
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            getApplicationContext(), aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();

                } else if (result != null && result.getPaths() == null) {
                    ShowToast.show(this,R.string.no_result);
                }

            } else {
                ShowToast.show(this,R.string.no_result);
            }
        } else {
            ShowToast.showerror(this,errorCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}
