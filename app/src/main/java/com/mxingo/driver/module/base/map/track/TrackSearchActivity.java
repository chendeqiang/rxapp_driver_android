package com.mxingo.driver.module.base.map.track;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.query.entity.DriveMode;
import com.amap.api.track.query.entity.HistoryTrack;
import com.amap.api.track.query.entity.Point;
import com.amap.api.track.query.entity.Track;
import com.amap.api.track.query.entity.TrackPoint;
import com.amap.api.track.query.model.HistoryTrackRequest;
import com.amap.api.track.query.model.HistoryTrackResponse;
import com.amap.api.track.query.model.QueryTerminalRequest;
import com.amap.api.track.query.model.QueryTerminalResponse;
import com.amap.api.track.query.model.QueryTrackRequest;
import com.amap.api.track.query.model.QueryTrackResponse;
import com.mxingo.driver.R;
import com.mxingo.driver.module.BaseActivity;
import com.mxingo.driver.module.base.data.UserInfoPreferences;
import com.mxingo.driver.module.base.log.LogUtils;
import com.mxingo.driver.module.base.map.trace.SimpleOnTrackListener;
import com.mxingo.driver.utils.Constants;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by deqiangchen on 2023/10/27.
 * 轨迹查询
 */
public class TrackSearchActivity extends BaseActivity {


    private ImageView ivBack;
    private MapView mapView;
    private AMapTrackClient aMapTrackClient;
    private List<Polyline> polylines = new LinkedList<>();
    private List<Marker> endMarkers = new LinkedList<>();
    final long serviceId = 1008407;  // 这里填入前面创建的服务id
    final String terminalName = UserInfoPreferences.getInstance().getMobile();
    private long orderStartTime;
    private long orderStopTime;

    public static void startTrackSearchActivity(Context context, Long orderStartTime, Long orderStopTime) {
        context.startActivity(new Intent(context, TrackSearchActivity.class)
                .putExtra(Constants.ORDERSTARTTIME, orderStartTime)
                .putExtra(Constants.ORDERSTOPTIME, orderStopTime));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AMapTrackClient.updatePrivacyAgree(getApplicationContext(),true);
        AMapTrackClient.updatePrivacyShow(getApplicationContext(),true,true);
        MapsInitializer.updatePrivacyShow(getApplicationContext(),true,true);
        MapsInitializer.updatePrivacyAgree(getApplicationContext(),true);
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            aMapTrackClient = new AMapTrackClient(getApplicationContext());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        setContentView(R.layout.activity_track_query);
        mapView = findViewById(R.id.track_search_mapView);
        ivBack = findViewById(R.id.iv_back);
        mapView.onCreate(savedInstanceState);

        orderStartTime = getIntent().getLongExtra(Constants.ORDERSTARTTIME, 00000);
        orderStopTime= getIntent().getLongExtra(Constants.ORDERSTOPTIME,00000);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initTrack();
    }

    private void initTrack() {
        clearTracksOnMap();
        aMapTrackClient.queryTerminal(new QueryTerminalRequest(serviceId,terminalName),new SimpleOnTrackListener(){
            @Override
            public void onQueryTerminalCallback(QueryTerminalResponse queryTerminalResponse) {
                if (queryTerminalResponse.isSuccess()){
                    if (queryTerminalResponse.isTerminalExist()){
                        long tid = queryTerminalResponse.getTid();
                        HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest(
                                serviceId,
                                tid,
                                orderStartTime,
                                orderStopTime,
                                1,//轨迹纠偏
                                1,// 距离补偿
                                5000,   // 距离补偿，只有超过5km的点才启用距离补偿
                                0,  // 由旧到新排序
                                1,   // 返回第1页数据
                                999,  // 一页不超过1000条
                                ""  // 暂未实现，该参数无意义，请留空
                        );
                        aMapTrackClient.queryHistoryTrack(historyTrackRequest, new SimpleOnTrackListener() {
                            @Override
                            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
                                if (historyTrackResponse.isSuccess()) {
                                    HistoryTrack historyTrack = historyTrackResponse.getHistoryTrack();
                                    if (historyTrack == null || historyTrack.getCount() == 0) {
                                        Toast.makeText(TrackSearchActivity.this, "未获取到轨迹点", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    List<Point> points = historyTrack.getPoints();
                                    drawTrackOnMap(points, historyTrack.getStartPoint(), historyTrack.getEndPoint());
                                } else {
                                    Toast.makeText(TrackSearchActivity.this, "查询历史轨迹点失败，" + historyTrackResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(TrackSearchActivity.this, "Terminal不存在", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    showNetErrorHint(queryTerminalResponse.getErrorMsg());
                }
            }
        });
    }

    private void showNetErrorHint(String errorMsg) {
        Toast.makeText(this, "网络请求失败，错误原因: " + errorMsg, Toast.LENGTH_SHORT).show();
    }

    private void drawTrackOnMap(List<Point> points, TrackPoint startPoint, TrackPoint endPoint) {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE).width(20);

        if (startPoint != null && startPoint.getLocation() != null) {
            LatLng latLng = new LatLng(startPoint.getLocation().getLat(), startPoint.getLocation().getLng());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            endMarkers.add(mapView.getMap().addMarker(markerOptions));
        }

        if (endPoint != null && endPoint.getLocation() != null) {
            LatLng latLng = new LatLng(endPoint.getLocation().getLat(), endPoint.getLocation().getLng());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            endMarkers.add(mapView.getMap().addMarker(markerOptions));
        }

        for (Point p : points) {
            LatLng latLng = new LatLng(p.getLat(), p.getLng());
            polylineOptions.add(latLng);
            boundsBuilder.include(latLng);
        }
        Polyline polyline = mapView.getMap().addPolyline(polylineOptions);
        polylines.add(polyline);
        mapView.getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 30));
    }

    private void clearTracksOnMap() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        for (Marker marker : endMarkers) {
            marker.remove();
        }
        endMarkers.clear();
        polylines.clear();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
