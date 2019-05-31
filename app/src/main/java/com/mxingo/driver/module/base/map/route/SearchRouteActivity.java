package com.mxingo.driver.module.base.map.route;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.mxingo.driver.R;
import com.mxingo.driver.model.CloseOrderEntity;
import com.mxingo.driver.model.OrderEntity;
import com.mxingo.driver.model.QryOrderEntity;
import com.mxingo.driver.module.BaseActivity;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.MyPresenter;
import com.mxingo.driver.module.base.map.BaiduMapUtil;
import com.mxingo.driver.utils.Constants;
import com.mxingo.driver.widget.MyProgress;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchRouteActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView mTvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.mapView_search)
    MapView mMapViewSearch;
    private RoutePlanSearchUtil searchUtil;
    @Inject
    MyPresenter presenter;
    private MyProgress progress;
    private String orderNo;
    private String driverNo;
    private OrderEntity order;
    private double mLat;
    private double mLon;

    public static void startSearchRouteActivity(Context context, String orderNo, String driverNo) {
        context.startActivity(new Intent(context, SearchRouteActivity.class)
                .putExtra(Constants.ORDER_NO, orderNo)
                .putExtra(Constants.DRIVER_NO, driverNo));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //百度地图
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_search_route);
        ButterKnife.bind(this);
        ComponentHolder.getAppComponent().inject(this);

        presenter.register(this);
        progress = new MyProgress(this);
        setToolbar(mToolbar);
        mTvToolbarTitle.setText("路线详情");

        orderNo = getIntent().getStringExtra(Constants.ORDER_NO);
        driverNo = getIntent().getStringExtra(Constants.DRIVER_NO);
        progress.show();
        presenter.qryOrder(orderNo);
        mLat = BaiduMapUtil.getInstance().getLat();
        mLon = BaiduMapUtil.getInstance().getLon();
    }

    @Subscribe
    public void loadData(Object object) {
        if (object.getClass() == QryOrderEntity.class) {
            qryOrder((QryOrderEntity) object);
            progress.dismiss();
        } else if (object.getClass() == CloseOrderEntity.class) {
            progress.dismiss();
//            closeOrder((CloseOrderEntity) object);
        }
    }

    private void qryOrder(QryOrderEntity orderEntity) {
        if (orderEntity.rspCode.equals("00")) {
            order = orderEntity.order;
            routePlan(order.startLat, order.startLon, order.endLat, order.endLon);
        }
    }

    //路径规划
    private void routePlan(double startLat, double startLon, double endLat, double endLon) {
        searchUtil = new RoutePlanSearchUtil(mMapViewSearch.getMap(), this);
        searchUtil.drivingPlan(startLat, startLon, endLat, endLon);
//        BaiduMapUtil.getInstance().setBaiduMap(mMapViewSearch);
//        BaiduMapUtil.getInstance().registerLocationListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapViewSearch.onResume();
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
//        if (searchUtil != null) {
//            BaiduMapUtil.getInstance().unregisterLocationListener();
//        }
        presenter.unregister(this);
    }
}
