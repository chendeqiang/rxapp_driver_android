package com.mxingo.driver.module.order;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.mxingo.driver.OrderModel;
import com.mxingo.driver.R;
import com.mxingo.driver.dialog.MessageDialog2;
import com.mxingo.driver.dialog.NaviSelectDialog;
import com.mxingo.driver.model.CloseOrderEntity;
import com.mxingo.driver.model.CurrentTimeEntity;
import com.mxingo.driver.model.OrderEntity;
import com.mxingo.driver.model.QryOrderEntity;
import com.mxingo.driver.model.StsEntity;
import com.mxingo.driver.module.BaseActivity;
import com.mxingo.driver.module.RecordingService;
import com.mxingo.driver.module.base.data.UserInfoPreferences;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.MyPresenter;
import com.mxingo.driver.module.base.log.LogUtils;
import com.mxingo.driver.module.base.map.BaiduMapUtil;
import com.mxingo.driver.module.base.map.route.RoutePlanSearchUtil;
import com.mxingo.driver.module.base.map.trace.MyTrace;
import com.mxingo.driver.module.take.CarLevel;
import com.mxingo.driver.module.take.OrderStatus;
import com.mxingo.driver.module.take.OrderType;
import com.mxingo.driver.utils.Constants;
import com.mxingo.driver.utils.StartUtil;
import com.mxingo.driver.utils.TextUtil;
import com.mxingo.driver.utils.TimeUtil;
import com.mxingo.driver.widget.MyProgress;
import com.mxingo.driver.widget.ShowToast;
import com.mxingo.driver.widget.SlippingButton;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Single;

public class MapActivity extends BaseActivity {

    @BindView(R.id.mv_driver)
    MapView mvDriver;
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


    private RoutePlanSearchUtil searchUtil;
    private String orderNo;
    private String flowNo;
    private String driverNo;
    private String startTime;
    private NaviSelectDialog navDialog;
    private MyProgress progress;
    private OrderEntity order;

    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
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
        //百度地图
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        ComponentHolder.getAppComponent().inject(this);

        presenter.register(this);
        progress = new MyProgress(this);
        setToolbar(toolbar);
        tvToolbarTitle.setText("用车中");

        startTime = UserInfoPreferences.getInstance().getStartTime();
        orderNo = getIntent().getStringExtra(Constants.ORDER_NO);
        flowNo = getIntent().getStringExtra(Constants.FLOW_NO);
        driverNo = getIntent().getStringExtra(Constants.DRIVER_NO);
        progress.show();

        presenter.qryOrder(orderNo);
        btnFinishOrder.setPosition(new SlippingButton.Position() {
            @Override
            public void overPosition() {
                progress.show();
                presenter.getCurrentTime();

            }
        });
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
        String curTime = TimeUtil.getDateToString(data.now);
        String endTime = TimeUtil.getDateToString(Long.parseLong(order.bookTime));
        String times = TimeUtil.getTimeDifferenceHour(curTime, endTime);
        String useTimes = tvUseTime.getText().toString();
        String minute = useTimes.substring(useTimes.indexOf("时") + 1, useTimes.indexOf("分"));
        String hour = useTimes.substring(0, useTimes.indexOf("小"));
        int min = Integer.parseInt(minute);
        int hours = Integer.parseInt(hour);

        int times2 = Integer.parseInt(times.substring(0, times.indexOf(".")));
//        if (times2 > 0) {
//            progress.dismiss();
//            final MessageDialog2 dialog = new MessageDialog2(this);
//            dialog.setMessageText("无法提前结束订单");
//            dialog.setOnOkClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
//                }
//            });
//            dialog.show();
//        } else if (min < 10 && hours < 1) {
//            progress.dismiss();
//            final MessageDialog2 dialog = new MessageDialog2(this);
//            dialog.setMessageText("开始与结束间隔需大于10分钟");
//            dialog.setOnOkClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
//                }
//            });
//            dialog.show();
//        } else {
            presenter.closeOrder(orderNo, flowNo);
//        }
    }

    private void closeOrder(CloseOrderEntity data) {
        if (data.rspCode.equals("00")) {
            //关闭录音
            Intent intent = new Intent(this, RecordingService.class);
            stopService(intent);
            //关闭鹰眼服务
            MyTrace.getInstance().stopTrace();
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
                    MyTrace.getInstance().startTrace();
                }
                tvStartAddress.setText(order.startAddr);

                routePlan(order.startLat, order.startLon, order.endLat, order.endLon);
                naviSelect(order.endLat, order.endLon, order.endAddr);
                long useTime = new Date().getTime() - order.orderStartTime;
                tvUseTime.setText("" + String.format("%d小时%d分钟", useTime / 1000 / 60 / 60, useTime / 1000 / 60 % 60) + "");
                tvEstimate.setText("约" + order.planMileage / 100 / 10.0 + "公里");
                tvToolbarTitle.setText("用车中");
                btnFinishOrder.setHint("向右滑动结束用车");
            } else if (order.orderStatus >= OrderStatus.WAIT_PAY_TYPE) {
                llOrderInfo.setVisibility(View.GONE);
                btnFinishOrder.setVisibility(View.GONE);
                track(order.orderStartTime, order.orderStopTime, UserInfoPreferences.getInstance().getMobile());
                tvToolbarTitle.setText("轨迹查询");
                ShowToast.showCenter(this, "此订单已结束");
            } else {
                llOrderInfo.setVisibility(View.GONE);
                btnFinishOrder.setVisibility(View.GONE);
            }
        } else {
            ShowToast.showCenter(this, orderEntity.rspDesc);
        }

    }


    //路径规划
    private void routePlan(double startLat, double startLon, double endLat, double endLon) {
        searchUtil = new RoutePlanSearchUtil(mvDriver.getMap(), this);
        searchUtil.drivingPlan(startLat, startLon, endLat, endLon);
        BaiduMapUtil.getInstance().setBaiduMap(mvDriver);
        BaiduMapUtil.getInstance().registerLocationListener();
    }

    //轨迹查询
    private void track(long startTime, long endTime, String mobile) {
        BaiduMapUtil.getInstance().setBaiduMap(mvDriver);
        MyTrace.getInstance().queryHistoryTrack(mobile, startTime / 1000, endTime / 1000, mvDriver);
    }

    @OnClick({R.id.btn_navigation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_navigation: {
                if (StartUtil.isInstallByread(StartUtil.baiduMapPackage) || StartUtil.isInstallByread(StartUtil.gaodeMapPackage)) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mvDriver.onDestroy();
        if (searchUtil != null) {
            BaiduMapUtil.getInstance().unregisterLocationListener();
        }
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
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mvDriver.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mvDriver.onPause();
    }
}
