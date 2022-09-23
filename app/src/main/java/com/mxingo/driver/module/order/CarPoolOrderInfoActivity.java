package com.mxingo.driver.module.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.common.BaiduMapSDKException;
import com.baidu.mapapi.map.MapView;
import com.mxingo.driver.R;
import com.mxingo.driver.dialog.MessageDialog;
import com.mxingo.driver.model.CpOrderInfoEntity;
import com.mxingo.driver.model.DpStatusChangeEntity;
import com.mxingo.driver.model.OrderStatusChangeEntity;
import com.mxingo.driver.module.BaseActivity;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.MyPresenter;
import com.mxingo.driver.module.base.map.route.RoutePlanSearchUtil;
import com.mxingo.driver.utils.Constants;
import com.mxingo.driver.utils.StartUtil;
import com.mxingo.driver.widget.MyProgress;
import com.mxingo.driver.widget.SlippingButton;
import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CarPoolOrderInfoActivity extends BaseActivity {

    @Inject
    MyPresenter presenter;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mv_carpool)
    MapView mvCarpool;
    @BindView(R.id.btn_user1)
    Button btnUser1;
    @BindView(R.id.tv_status_user1)
    TextView tvStatusUser1;
    @BindView(R.id.ll_user1)
    LinearLayout llUser1;
    @BindView(R.id.btn_user2)
    Button btnUser2;
    @BindView(R.id.tv_status_user2)
    TextView tvStatusUser2;
    @BindView(R.id.iv_start_nav)
    ImageView ivStartNav;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv_connect)
    ImageView ivConnect;
    @BindView(R.id.tv_startdd)
    TextView tvStartdd;
    @BindView(R.id.tv_endd)
    TextView tvEndd;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.btn_order)
    SlippingButton btnOrder;
    @BindView(R.id.tv_user_cost)
    TextView tvUserCost;
    private String cmainid;
    private String ccode;
    private CpOrderInfoEntity order;
    private MyProgress progress;
    private RoutePlanSearchUtil searchUtil;
    private String phone;


    public static void startCarPoolOrderInfoActivity(Context context, String cmainid,String ccode) {
        context.startActivity(new Intent(context, CarPoolOrderInfoActivity.class)
                .putExtra(Constants.CMAINID, cmainid)
                .putExtra(Constants.CCODE,ccode));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //百度地图
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        try {
            SDKInitializer.initialize(getApplicationContext());
        } catch (BaiduMapSDKException e) {

        }
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_carpool_map);
        ButterKnife.bind(this);

        ComponentHolder.getAppComponent().inject(this);

        presenter.register(this);
        progress = new MyProgress(this);
        setToolbar(toolbar);
        tvToolbarTitle.setText("拼车订单");

        cmainid = getIntent().getStringExtra(Constants.CMAINID);
        ccode =getIntent().getStringExtra(Constants.CCODE);
        presenter.carpoolOrderInfo(cmainid);

        btnOrder.setPosition(new SlippingButton.Position() {
            @Override
            public void overPosition() {
                switch (order.orders.get(0).orderstatus) {
                    case 1:
                        presenter.orderStatusChange(order.orders.get(0).ccode, 5);
                        break;
                    case 5:
                        presenter.orderStatusChange(order.orders.get(0).ccode, 2);
                        break;
                    case 2:
                        presenter.orderStatusChange(order.orders.get(0).ccode, 3);
                        break;
                }

                switch (order.orders.get(1).orderstatus) {
                    case 1:
                        presenter.orderStatusChange(order.orders.get(1).ccode, 5);
                        break;
                    case 5:
                        presenter.orderStatusChange(order.orders.get(1).ccode, 2);
                        break;
                    case 2:
                        presenter.orderStatusChange(order.orders.get(1).ccode, 3);
                        break;
                }
            }
        });
    }

    @Subscribe
    public void loadData(Object object) {
        if (object.getClass() == CpOrderInfoEntity.class) {
            order = (CpOrderInfoEntity) object;
            if (order.orders.get(0).orderstatus == 3 && order.orders.get(1).orderstatus == 3) {
                presenter.dpStatusChange(ccode, "2");
            } else {
                initView((CpOrderInfoEntity) object);
            }
        } else if (object.getClass() == OrderStatusChangeEntity.class) {
            presenter.carpoolOrderInfo(cmainid);
        } else if (object.getClass() == DpStatusChangeEntity.class) {
            finish();
            //跳转订单完成详情页面
            CarPoolInfoActivity.startCarPoolInfoActivity(this, cmainid);
        }
    }

    private void initView(CpOrderInfoEntity object) {
        switch (order.orders.get(0).orderstatus) {
            case 1:
                btnOrder.setHint("去接乘客" + order.orders.get(0).cphone.substring(7));
                break;
            case 2://行程中
                tvStatusUser1.setVisibility(View.VISIBLE);
                tvStatusUser1.setText("行程中");
                btnOrder.setHint("到达" + order.orders.get(0).cphone.substring(7) + "目的地");
                break;
            case 3://已完成
                tvStatusUser1.setVisibility(View.VISIBLE);
                tvStatusUser1.setText("已完成");
                switch (order.orders.get(1).orderstatus) {
                    case 1:
                        btnOrder.setHint("去接乘客" + order.orders.get(1).cphone.substring(7));
                        break;
                    case 2://行程中
                        btnOrder.setHint("到达" + order.orders.get(1).cphone.substring(7) + "目的地");
                        break;
                    case 5://接驾中
                        btnOrder.setHint("乘客" + order.orders.get(1).cphone.substring(7) + "已上车");
                        break;
                }
                break;
            case 5://接驾中
                tvStatusUser1.setVisibility(View.VISIBLE);
                tvStatusUser1.setText("接驾中");
                btnOrder.setHint("乘客" + order.orders.get(0).cphone.substring(7) + "已上车");
                break;
        }

        switch (order.orders.get(1).orderstatus) {
            case 2://行程中
                tvStatusUser2.setVisibility(View.VISIBLE);
                tvStatusUser2.setText("行程中");
                break;
            case 3://已完成
                tvStatusUser2.setVisibility(View.VISIBLE);
                tvStatusUser2.setText("已完成");
                break;
            case 5://接驾中
                tvStatusUser2.setVisibility(View.VISIBLE);
                tvStatusUser2.setText("接驾中");
                break;
        }

        phone = order.orders.get(0).cphone;
        tvTime.setText(order.orders.get(0).cstarttime.substring(5, 16));
        tvUserCost.setText(order.orders.get(0).driverprice);
        btnUser1.setHint("尾号" + order.orders.get(0).cphone.substring(7));
        btnUser2.setHint("尾号" + order.orders.get(1).cphone.substring(7));
        tvStartdd.setText(order.orders.get(0).cstartaddress);
        tvEndd.setText(order.orders.get(0).cendaddress);
        tvMsg.setText(order.orders.get(0).remark);

        routePlan(Double.valueOf(object.orders.get(0).slat), Double.valueOf(object.orders.get(0).slng), Double.valueOf(object.orders.get(0).elat), Double.valueOf(object.orders.get(0).elng));
    }

    //路径规划
    private void routePlan(double startLat, double startLon, double endLat, double endLon) {
        searchUtil = new RoutePlanSearchUtil(mvCarpool.getMap(), this);
        searchUtil.drivingPlan(startLat, startLon, endLat, endLon);
//        BaiduMapUtil.getInstance().setBaiduMap(mvCarpool);
//        BaiduMapUtil.getInstance().registerLocationListener();
    }

    @OnClick({R.id.btn_user1, R.id.btn_user2, R.id.iv_start_nav, R.id.iv_connect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_user1:
                phone = order.orders.get(0).cphone;
                tvTime.setText(order.orders.get(0).cstarttime.substring(5, 16));
                tvStartdd.setText(order.orders.get(0).cstartaddress);
                tvEndd.setText(order.orders.get(0).cendaddress);
                tvMsg.setText(order.orders.get(0).remark);
                switch (order.orders.get(0).orderstatus) {
                    case 1:
                        btnOrder.setHint("去接乘客" + order.orders.get(0).cphone.substring(7));
                        break;
                    case 2://行程中
                        btnOrder.setHint("到达" + order.orders.get(0).cphone.substring(7) + "目的地");
                        break;
                    case 5://接驾中
                        btnOrder.setHint("乘客" + order.orders.get(0).cphone.substring(7) + "已上车");
                        break;
                }
                routePlan(Double.valueOf(order.orders.get(0).slat), Double.valueOf(order.orders.get(0).slng), Double.valueOf(order.orders.get(0).elat), Double.valueOf(order.orders.get(0).elng));
                break;
            case R.id.btn_user2:
                phone = order.orders.get(1).cphone;
                tvTime.setText(order.orders.get(1).cstarttime.substring(5, 16));
                tvUserCost.setText(order.orders.get(1).driverprice);
                tvStartdd.setText(order.orders.get(1).cstartaddress);
                tvEndd.setText(order.orders.get(1).cendaddress);
                tvMsg.setText(order.orders.get(1).remark);
                switch (order.orders.get(1).orderstatus) {
                    case 1:
                        btnOrder.setHint("去接乘客" + order.orders.get(1).cphone.substring(7));
                        break;
                    case 2://行程中
                        btnOrder.setHint("到达" + order.orders.get(1).cphone.substring(7) + "目的地");
                        break;
                    case 5://接驾中
                        btnOrder.setHint("乘客" + order.orders.get(1).cphone.substring(7) + "已上车");
                        break;
                }
                routePlan(Double.valueOf(order.orders.get(1).slat), Double.valueOf(order.orders.get(1).slng), Double.valueOf(order.orders.get(1).elat), Double.valueOf(order.orders.get(1).elng));
                break;
            case R.id.iv_start_nav://导航起点

                break;
            case R.id.iv_connect:
                final MessageDialog dialog = new MessageDialog(this);
                dialog.setMessageText("联系尾号" + phone.substring(7) + "乘客");
                dialog.setOkText("呼叫");
                dialog.setOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setOnOkClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        StartUtil.startPhone(phone, CarPoolOrderInfoActivity.this);
                    }
                });
                dialog.show();
                break;
        }
    }
}
