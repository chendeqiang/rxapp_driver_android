package com.mxingo.driver.module.order;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.common.BaiduMapSDKException;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.track.AddPointResponse;
import com.baidu.trace.api.track.AddPointsResponse;
import com.baidu.trace.api.track.ClearCacheTrackResponse;
import com.baidu.trace.api.track.DistanceResponse;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.QueryCacheTrackResponse;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.BaseRequest;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.StatusCodes;
import com.mxingo.driver.R;
import com.mxingo.driver.model.QryOrderEntity;
import com.mxingo.driver.module.BaseActivity;
import com.mxingo.driver.module.base.data.UserInfoPreferences;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.MyPresenter;
import com.mxingo.driver.module.base.log.LogUtils;
import com.mxingo.driver.module.base.map.BaiduMapUtil;
import com.mxingo.driver.utils.CommonUtil;
import com.mxingo.driver.utils.Constants;
import com.mxingo.driver.utils.MapUtil;
import com.mxingo.driver.widget.ShowToast;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;


/**
 * Created by deqiangchen on 2023/4/14.
 * 轨迹查询
 */
public class TrackQueryActivity extends BaseActivity {

    private ImageView ivBack;
    /**
     * 地图工具
     */
    private MapUtil mapUtil = null;
    private MapView mapView = null;

    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    /**
     * 轨迹服务ID
     */
    public long serviceId = 145188;

    /**
     * 轨迹客户端
     */
    public LBSTraceClient mClient = null;

    /**
     * 轨迹服务
     */

    /**
     * 历史轨迹请求
     */
    private HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest();

    /**
     * 轨迹监听器（用于接收历史轨迹回调）
     */
    private OnTrackListener mTrackListener = null;
    /**
     * 查询轨迹的开始时间
     */
    private long startTime = CommonUtil.getCurrentTime();

    /**
     * 查询轨迹的结束时间
     */
    private long endTime = CommonUtil.getCurrentTime();

    /**
     * 轨迹点集合
     */
    private List<LatLng> trackPoints = new ArrayList<>();
    /**
     * 轨迹排序规则
     */
    private SortType sortType = SortType.asc;

    private int pageIndex = 1;
    @Inject
    MyPresenter presenter;
    private String orderNo;

    public static void startTrackQueryActivity(Context context, String orderNo) {
        context.startActivity(new Intent(context, TrackQueryActivity.class)
                .putExtra(Constants.ORDER_NO, orderNo));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //百度地图
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        LBSTraceClient.setAgreePrivacy(getApplicationContext(),true);
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        try {
            SDKInitializer.initialize(getApplicationContext());
        }catch (BaiduMapSDKException e){

        }
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_track_query);
        ComponentHolder.getAppComponent().inject(this);
        presenter.register(this);
        mapUtil = MapUtil.getInstance();
        orderNo = getIntent().getStringExtra(Constants.ORDER_NO);
        presenter.qryOrder(orderNo);

        if (mClient==null) {
            try {
                mClient = new LBSTraceClient(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        init();
    }

    private void init() {
        mapView = findViewById(R.id.track_query_mapView);
        ivBack = findViewById(R.id.iv_back);
        BaiduMapUtil.getInstance().setBaiduMap(mapView);


        mTrackListener = new OnTrackListener() {
            @Override
            public void onAddPointCallback(AddPointResponse addPointResponse) {
                super.onAddPointCallback(addPointResponse);
            }

            @Override
            public void onAddPointsCallback(AddPointsResponse addPointsResponse) {
                super.onAddPointsCallback(addPointsResponse);
            }

            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse response) {
                int total = response.getTotal();
                if (StatusCodes.SUCCESS != response.getStatus()) {
                    ShowToast.showCenter(TrackQueryActivity.this,response.getMessage());
                } else if (0 == total) {
                    LogUtils.d("onHistoryTrackCallback", response.getMessage());
                    ShowToast.showCenter(TrackQueryActivity.this,"未查询到轨迹");
                } else {
                    List<TrackPoint> points = response.getTrackPoints();
                    if (null != points) {
                        for (TrackPoint trackPoint : points) {
                            if (!CommonUtil.isZeroPoint(trackPoint.getLocation().getLatitude(),
                                    trackPoint.getLocation().getLongitude())) {
                                trackPoints.add(MapUtil.convertTrace2Map(trackPoint.getLocation()));
                            }
                        }
                    }
                }

                if (total > Constants.PAGE_SIZE * pageIndex) {
                    historyTrackRequest.setPageIndex(++pageIndex);
                    queryHistoryTrack();
                } else {
                    mapUtil.drawHistoryTrack(trackPoints, sortType);
                }
            }

            @Override
            public void onDistanceCallback(DistanceResponse distanceResponse) {
                super.onDistanceCallback(distanceResponse);
            }

            @Override
            public void onLatestPointCallback(LatestPointResponse latestPointResponse) {
                super.onLatestPointCallback(latestPointResponse);
            }

            @Override
            public void onQueryCacheTrackCallback(QueryCacheTrackResponse queryCacheTrackResponse) {
                super.onQueryCacheTrackCallback(queryCacheTrackResponse);
            }

            @Override
            public void onClearCacheTrackCallback(ClearCacheTrackResponse clearCacheTrackResponse) {
                super.onClearCacheTrackCallback(clearCacheTrackResponse);
            }
        };
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Subscribe
    public void loadData(Object object) {
        if (object.getClass() == QryOrderEntity.class) {
            qryOrder((QryOrderEntity) object);
        }
    }

    private void qryOrder(QryOrderEntity orderEntity) {
        if (orderEntity.rspCode.equals("00")) {
            startTime = orderEntity.order.orderStartTime;
            endTime = orderEntity.order.orderStopTime;
            queryHistoryTrack();
        }
    }
    /**
     * 查询历史轨迹
     */
    private void queryHistoryTrack() {
        MapUtil.getInstance().init(mapView);
        if (mClient == null) {
            return;
        }
        initRequest(historyTrackRequest);
        historyTrackRequest.setEntityName(UserInfoPreferences.getInstance().getMobile());
        historyTrackRequest.setStartTime(startTime/1000);
        historyTrackRequest.setEndTime(endTime/1000);
        historyTrackRequest.setPageIndex(pageIndex);
        historyTrackRequest.setPageSize(Constants.PAGE_SIZE);
        mClient.queryHistoryTrack(historyTrackRequest, mTrackListener);
    }

    /**
     * 初始化请求公共参数
     *
     * @param request
     */
    public void initRequest(BaseRequest request) {
        request.setTag(getTag());
        request.setServiceId(serviceId);
    }

    /**
     * 获取请求标识
     *
     * @return
     */
    public int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }

}
