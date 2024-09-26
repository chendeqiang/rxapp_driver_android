package com.mxingo.driver.module.order;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.ErrorCode;
import com.amap.api.track.OnTrackLifecycleListener;
import com.amap.api.track.TrackParam;
import com.amap.api.track.query.model.AddTerminalRequest;
import com.amap.api.track.query.model.AddTerminalResponse;
import com.amap.api.track.query.model.AddTrackRequest;
import com.amap.api.track.query.model.AddTrackResponse;
import com.amap.api.track.query.model.QueryTerminalRequest;
import com.amap.api.track.query.model.QueryTerminalResponse;
import com.mxingo.driver.OrderModel;
import com.mxingo.driver.R;
import com.mxingo.driver.dialog.MessageDialog2;
import com.mxingo.driver.dialog.NaviSelectDialog;
import com.mxingo.driver.model.CloseOrderEntity;
import com.mxingo.driver.model.CurrentTimeEntity;
import com.mxingo.driver.model.OrderEntity;
import com.mxingo.driver.model.QryOrderEntity;
import com.mxingo.driver.module.BaseActivity;
import com.mxingo.driver.module.RecordingService;
import com.mxingo.driver.module.base.data.UserInfoPreferences;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.MyPresenter;
import com.mxingo.driver.module.base.log.LogUtils;
import com.mxingo.driver.module.base.map.route.DrivingRouteOverlay;
import com.mxingo.driver.module.base.map.trace.SimpleOnTrackLifecycleListener;
import com.mxingo.driver.module.base.map.trace.SimpleOnTrackListener;
import com.mxingo.driver.module.take.CarLevel;
import com.mxingo.driver.module.take.OrderStatus;
import com.mxingo.driver.module.take.OrderType;
import com.mxingo.driver.utils.AMapUtil;
import com.mxingo.driver.utils.Constants;
import com.mxingo.driver.utils.StartUtil;
import com.mxingo.driver.utils.TextUtil;
import com.mxingo.driver.utils.TimeUtil;
import com.mxingo.driver.widget.MyProgress;
import com.mxingo.driver.widget.ShowToast;
import com.mxingo.driver.widget.SlippingButton;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mxingo.driver.widget.ShowToast.TAG;
@SuppressWarnings("deprecation")
public class MapActivity extends BaseActivity implements RouteSearch.OnRouteSearchListener {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_order_type)
    TextView tvOrderType;
    @BindView(R.id.tv_book_time)
    TextView tvBookTime;
    @BindView(R.id.tv_start_address)
    TextView tvStartAddress;
    @BindView(R.id.tv_end_address)
    TextView tvEndAddress;
    @BindView(R.id.tv_use_time)
    TextView tvUseTime;
    @BindView(R.id.tv_estimate)
    TextView tvEstimate;
    @BindView(R.id.btn_finish_order)
    SlippingButton btnFinishOrder;
    @BindView(R.id.ll_end_address)
    LinearLayout llEndAddress;
    @BindView(R.id.btn_navigation)
    Button btnNavigation;
    @BindView(R.id.img_use_time)
    ImageView imgUseTime;
    @BindView(R.id.tv_hint_mobile)
    TextView tvHintMobile;
    @BindView(R.id.ll_order_info)
    LinearLayout llOrderInfo;

    @Inject
    MyPresenter presenter;

    private MapView mapView;
    private AMap aMap;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private PowerManager powerManager;
    private static final int REQUEST_CODE_PERMISSION = 1;
    private String orderNo;
    private String flowNo;
    private String driverNo;
    private NaviSelectDialog navDialog;
    private MyProgress progress;
    private OrderEntity order;
    final long serviceId = 1008407;  // 这里填入前面创建的服务id
    final String terminalName = UserInfoPreferences.getInstance().getMobile();   // 唯一标识某个用户或某台设备的名称，可根据您的业务自行选择
    private long terminalId;
    private long trackId;
    private static final String CHANNEL_ID_SERVICE_RUNNING = "CHANNEL_ID_SERVICE_RUNNING";
    private AMapTrackClient aMapTrackClient;
    private boolean isServiceRunning = false;
    private boolean isGatherRunning = false;
    private static final String LOG_TAG = "TrackService";
    private NotificationManager notificationManager;
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null && msg.what == 1) {
                long useTime = new Date().getTime() - order.orderStartTime;
                tvUseTime.setText("" + String.format("%d小时%d分钟", useTime / 1000 / 60 / 60, useTime / 1000 / 60 % 60) + "");
            }
        }
    };

    public static void startMapActivity(Context context, String orderNo, String flowNo, String driverNo) {
        context.startActivity(new Intent(context, MapActivity.class)
                .putExtra(Constants.ORDER_NO, orderNo)
                .putExtra(Constants.FLOW_NO, flowNo)
                .putExtra(Constants.DRIVER_NO, driverNo));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //高德地图
        MapsInitializer.updatePrivacyShow(getApplicationContext(),true,true);
        MapsInitializer.updatePrivacyAgree(getApplicationContext(),true);
        AMapTrackClient.updatePrivacyAgree(getApplicationContext(),true);
        AMapTrackClient.updatePrivacyShow(getApplicationContext(),true,true);
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        ComponentHolder.getAppComponent().inject(this);
        presenter.register(this);
        progress = new MyProgress(this);
        setToolbar(toolbar);

        tvToolbarTitle.setText("用车中");
        try {
            aMapTrackClient = new AMapTrackClient(getApplicationContext());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        aMapTrackClient.setInterval(10, 60);//定位信息采集周期设置为10s，上报周期设置为60s。注意定位信息采集周期的范围应该是1s~60s，上报周期的范围是采集周期的5～50倍
        mapView= findViewById(R.id.map);
        mapView.getMap().moveCamera(CameraUpdateFactory.zoomTo(14));
        mapView.onCreate(savedInstanceState);
        // 启用地图内置定位
        mapView.getMap().setMyLocationEnabled(true);
        mapView.getMap().setMyLocationStyle(
                new MyLocationStyle()
                        .interval(2000)
                        .myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW)
        );
        initMap();
        orderNo = getIntent().getStringExtra(Constants.ORDER_NO);
        flowNo = getIntent().getStringExtra(Constants.FLOW_NO);
        driverNo = getIntent().getStringExtra(Constants.DRIVER_NO);
        progress.show();
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        presenter.qryOrder(orderNo);
        btnFinishOrder.setPosition(new SlippingButton.Position() {
            @Override
            public void overPosition() {
                progress.show();
                presenter.getCurrentTime();
            }
        });

        startTrack();
    }

    private void startTrack() {
        // 先根据Terminal名称查询Terminal ID，如果Terminal还不存在，就尝试创建，拿到Terminal ID后，
        // 用Terminal ID开启轨迹服务
        aMapTrackClient.queryTerminal(new QueryTerminalRequest(serviceId, terminalName),new SimpleOnTrackListener(){
            @Override
            public void onQueryTerminalCallback(QueryTerminalResponse queryTerminalResponse) {
                if (queryTerminalResponse.isSuccess()) {
                    if (queryTerminalResponse.isTerminalExist()) {
                        // 当前终端已经创建过，直接使用查询到的terminal id
                        terminalId = queryTerminalResponse.getTid();
                            aMapTrackClient.addTrack(new AddTrackRequest(serviceId, terminalId), new SimpleOnTrackListener() {
                                @Override
                                public void onAddTrackCallback(AddTrackResponse addTrackResponse) {
                                    if (addTrackResponse.isSuccess()) {
                                        // trackId需要在启动服务后设置才能生效，因此这里不设置，而是在startGather之前设置了track id
                                        trackId = addTrackResponse.getTrid();
                                        TrackParam trackParam = new TrackParam(serviceId, terminalId);
                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            trackParam.setNotification(createNotification());
                                        }
                                        aMapTrackClient.startTrack(trackParam, onTrackListener);
                                    } else {
                                        Toast.makeText(MapActivity.this, "网络请求失败，" + addTrackResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    } else {
                        // 当前终端是新终端，还未创建过，创建该终端并使用新生成的terminal id
                        aMapTrackClient.addTerminal(new AddTerminalRequest(terminalName, serviceId), new SimpleOnTrackListener() {
                            @Override
                            public void onCreateTerminalCallback(AddTerminalResponse addTerminalResponse) {
                                if (addTerminalResponse.isSuccess()) {
                                    terminalId = addTerminalResponse.getTid();
                                    aMapTrackClient.addTrack(new AddTrackRequest(serviceId,terminalId),new SimpleOnTrackListener(){
                                        @Override
                                        public void onAddTrackCallback(AddTrackResponse addTrackResponse) {
                                            if (addTrackResponse.isSuccess()) {
                                                trackId = addTrackResponse.getTrid();
                                                TrackParam trackParam = new TrackParam(serviceId, terminalId);
                                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    trackParam.setNotification(createNotification());
                                                }
                                                aMapTrackClient.startTrack(trackParam, onTrackListener);
                                            } else {
                                                Toast.makeText(MapActivity.this, "网络请求失败，" + addTrackResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(MapActivity.this, "网络请求失败，" + addTerminalResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(MapActivity.this, "网络请求失败，" + queryTerminalResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 在8.0以上手机，如果app切到后台，系统会限制定位相关接口调用频率
     * 可以在启动轨迹上报服务时提供一个通知，这样Service启动时会使用该通知成为前台Service，可以避免此限制
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Notification createNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_SERVICE_RUNNING, "app service", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID_SERVICE_RUNNING);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        Intent nfIntent = new Intent(MapActivity.this, MapActivity.class);
        nfIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        builder.setContentIntent(PendingIntent.getActivity(MapActivity.this, 0, nfIntent, 0))
                .setSmallIcon(R.drawable.push)
                .setContentTitle("任行约车")
                .setContentText("猎鹰服务正在运行...");
        Notification notification = builder.build();
        notificationManager.notify(1,notification);
        return notification;
    }

    private OnTrackLifecycleListener onTrackListener = new SimpleOnTrackLifecycleListener() {
        @Override
        public void onBindServiceCallback(int status, String msg) {
            Log.i(LOG_TAG, "onBindServiceCallback, status: " + status + ", msg: " + msg);
        }

        @Override
        public void onStartGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_GATHER_SUCEE) {
                Toast.makeText(MapActivity.this, "定位采集开启成功", Toast.LENGTH_SHORT).show();
                isGatherRunning = true;
            } else if (status == ErrorCode.TrackListen.START_GATHER_ALREADY_STARTED) {
                Toast.makeText(MapActivity.this, "定位采集已经开启", Toast.LENGTH_SHORT).show();
                isGatherRunning = true;
            } else {
                Log.w(TAG, "error onStartGatherCallback, status: " + status + ", msg: " + msg);
                Toast.makeText(MapActivity.this,
                        "error onStartGatherCallback, status: " + status + ", msg: " + msg,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStartTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_TRACK_SUCEE || status == ErrorCode.TrackListen.START_TRACK_SUCEE_NO_NETWORK) {
                // 成功启动
                Toast.makeText(MapActivity.this, "启动服务成功", Toast.LENGTH_SHORT).show();
                isServiceRunning = true;
                // 服务启动成功，开启收集上报
                aMapTrackClient.setTrackId(trackId);
                aMapTrackClient.startGather(this);
            }else if (status == ErrorCode.TrackListen.START_TRACK_ALREADY_STARTED){
                Toast.makeText(MapActivity.this, "服务已经启动", Toast.LENGTH_SHORT).show();
                isServiceRunning =true;
            } else {
                Log.w(TAG, "error onStartTrackCallback, status: " + status + ", msg: " + msg);
                Toast.makeText(MapActivity.this,
                        "error onStartTrackCallback, status: " + status + ", msg: " + msg,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStopGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_GATHER_SUCCE) {
                Toast.makeText(MapActivity.this, "定位采集停止成功", Toast.LENGTH_SHORT).show();
                isGatherRunning = false;
            } else {
                Log.w(TAG, "error onStopGatherCallback, status: " + status + ", msg: " + msg);
                Toast.makeText(MapActivity.this,
                        "error onStopGatherCallback, status: " + status + ", msg: " + msg,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStopTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_TRACK_SUCCE) {
                // 成功停止
                Toast.makeText(MapActivity.this, "停止服务成功", Toast.LENGTH_SHORT).show();
                isServiceRunning = false;
                isGatherRunning = false;
            } else {
                Log.w(TAG, "error onStopTrackCallback, status: " + status + ", msg: " + msg);
                Toast.makeText(MapActivity.this,
                        "error onStopTrackCallback, status: " + status + ", msg: " + msg,
                        Toast.LENGTH_LONG).show();

            }
        }
    };

    private void initMap() {
        if (aMap == null) {
            aMap= mapView.getMap();
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
        } else if (object.getClass() == CloseOrderEntity.class) {
            progress.dismiss();
            closeOrder((CloseOrderEntity) object);
        } else if (object.getClass() == CurrentTimeEntity.class) {
            compareTime((CurrentTimeEntity) object);
        }
    }

    private void compareTime(CurrentTimeEntity data) {
        String times = TimeUtil.getTimeDifferenceHour(TimeUtil.getDateToString(data.now), TimeUtil.getDateToString(Long.parseLong(order.bookTime)));
        String useTimes = tvUseTime.getText().toString();
        String minute = useTimes.substring(useTimes.indexOf("时") + 1, useTimes.indexOf("分"));
        String hour = useTimes.substring(0, useTimes.indexOf("小"));
        int min = Integer.parseInt(minute);
        int hours = Integer.parseInt(hour);

        int times2 = Integer.parseInt(times.substring(0, times.indexOf(".")));
        if (times2 > 0) {
            progress.dismiss();
            final MessageDialog2 dialog = new MessageDialog2(this);
            dialog.setMessageText("无法提前结束订单");
            dialog.setOnOkClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else if (min < 10 && hours < 1) {
            progress.dismiss();
            final MessageDialog2 dialog = new MessageDialog2(this);
            dialog.setMessageText("开始与结束间隔需大于10分钟");
            dialog.setOnOkClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            presenter.closeOrder(orderNo, flowNo);
        }
    }

    private void closeOrder(CloseOrderEntity data) {
        if (data.rspCode.equals("00")) {
            //关闭录音
            Intent intent = new Intent(this, RecordingService.class);
            stopService(intent);
            //关闭猎鹰服务
            notificationManager.cancel(1);
            aMapTrackClient.stopGather(onTrackListener);
            aMapTrackClient.stopTrack(new TrackParam(serviceId, terminalId), onTrackListener);
            OrderInfoActivity.startOrderInfoActivity(MapActivity.this, orderNo, flowNo, driverNo);
            finish();
        } else {
            ShowToast.showCenter(this, data.rspDesc);
        }
    }

    private void qryOrder(QryOrderEntity orderEntity) {
        if (orderEntity.rspCode.equals("00")) {
            order = orderEntity.order;
            timer.schedule(task, 1000, 60 * 1000);
            if (order.orderStatus == OrderStatus.USING_TYPE) {//开始行程
                if (order.orderModel == OrderModel.POINT_TYPE) {//指派模式
                    tvOrderType.setText(OrderType.getKey(order.orderType));
                } else {//抢单模式
                    tvOrderType.setText(OrderType.getKey(order.orderType) + "(" + CarLevel.getKey(order.carLevel) + ")");
                }
                if (OrderType.DAY_RENTER_TYPE == order.orderType) {//日租
                    tvBookTime.setText(TextUtil.getFormatString(Long.valueOf(order.bookTime), order.bookDays, "MM月dd日"));
                    llEndAddress.setVisibility(View.GONE);
                    tvEstimate.setVisibility(View.GONE);
                } else {
                    tvBookTime.setText(TextUtil.getFormatWeek(Long.valueOf(order.bookTime)));
                    tvEndAddress.setText(order.endAddr);
                }
                tvStartAddress.setText(order.startAddr);
                driveRoute(order.startLat, order.startLon, order.endLat, order.endLon);
                naviSelect(order.endLat, order.endLon, order.endAddr);
                long useTime = new Date().getTime() - order.orderStartTime;
                tvUseTime.setText("" + String.format("%d小时%d分钟", useTime / 1000 / 60 / 60, useTime / 1000 / 60 % 60) + "");
                tvEstimate.setText("约" + order.planMileage / 100 / 10.0 + "公里");
                tvToolbarTitle.setText("用车中");
                btnFinishOrder.setHint("向右滑动结束用车");
            }
            else {
                llOrderInfo.setVisibility(View.GONE);
                btnFinishOrder.setVisibility(View.GONE);
            }
        } else {
            ShowToast.showCenter(this, orderEntity.rspDesc);
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
            DriveRouteQuery query = new DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询

    }
    @OnClick({R.id.btn_navigation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_navigation: {
                if (StartUtil.isInstallByread(this,StartUtil.baiduMapPackage) || StartUtil.isInstallByread(this,StartUtil.gaodeMapPackage)) {
                    navDialog.show();
                } else {
                    ShowToast.showCenter(this, "您还未安装百度地图或者高德地图");
                }
                break;
            }
        }
    }

    private void naviSelect(final double toLat, final double toLon, final String addressStr) {
        navDialog = new NaviSelectDialog(this);
        navDialog.setonBaiduMapClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navDialog.dismiss();
                StartUtil.startBaiduMap(toLat, toLon, addressStr, MapActivity.this);
            }
        });
        navDialog.setonGaodeMapClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navDialog.dismiss();
                StartUtil.startGaoDeMap(MapActivity.this, toLat, toLon);
            }
        });
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this, new String[]{(Manifest.permission.ACCESS_BACKGROUND_LOCATION)}, 0);
                }
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroy() {
        if (!isServiceRunning&!isGatherRunning){
            mapView.onDestroy();
        }
        super.onDestroy();
        presenter.unregister(this);
        handler = null;
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        requestBackgroundLocationPermission();
        // 在Android 6.0及以上系统，若定制手机使用到doze模式，请求将应用添加到白名单。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            Boolean isIgnoring= powerManager.isIgnoringBatteryOptimizations(packageName);
            if (!isIgnoring) {
                Uri uri = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,uri);
                try {
                    startActivity(intent);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
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
