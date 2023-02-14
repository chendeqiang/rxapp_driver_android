package com.mxingo.driver.module.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.mxingo.driver.dialog.NaviSelectDialog;
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
import com.mxingo.driver.widget.ShowToast;
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
    @BindView(R.id.ll_user2)
    LinearLayout llUser2;
    @BindView(R.id.btn_user3)
    Button btnUser3;
    @BindView(R.id.tv_status_user3)
    TextView tvStatusUser3;
    @BindView(R.id.ll_user3)
    LinearLayout llUser3;
    @BindView(R.id.btn_user4)
    Button btnUser4;
    @BindView(R.id.tv_status_user4)
    TextView tvStatusUser4;
    @BindView(R.id.ll_user4)
    LinearLayout llUser4;
    private String cmainid;
    private String ccode;
    private CpOrderInfoEntity order;
    private MyProgress progress;
    private RoutePlanSearchUtil searchUtil;
    private String phone;
    private NaviSelectDialog navDialog;
    private double lat;
    private double lon;
    private String address;


    public static void startCarPoolOrderInfoActivity(Context context, String cmainid, String ccode) {
        context.startActivity(new Intent(context, CarPoolOrderInfoActivity.class)
                .putExtra(Constants.CMAINID, cmainid)
                .putExtra(Constants.CCODE, ccode));
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
        ccode = getIntent().getStringExtra(Constants.CCODE);
        presenter.carpoolOrderInfo(cmainid);

        btnOrder.setPosition(new SlippingButton.Position() {
            @Override
            public void overPosition() {
                switch (btnOrder.getTag().toString()){
                    case "0-1"://接驾中
                        presenter.orderStatusChange(order.orders.get(0).ccode, 5);
                        break;
                    case "0-5"://行程中
                        presenter.orderStatusChange(order.orders.get(0).ccode, 2);
                        break;
                    case "0-2"://完成
                        presenter.orderStatusChange(order.orders.get(0).ccode, 3);
                        break;
                    case "1-1":
                        presenter.orderStatusChange(order.orders.get(1).ccode, 5);
                        break;
                    case "1-5":
                        presenter.orderStatusChange(order.orders.get(1).ccode, 2);
                        break;
                    case "1-2":
                        presenter.orderStatusChange(order.orders.get(1).ccode, 3);
                        break;
                    case "2-1":
                        presenter.orderStatusChange(order.orders.get(2).ccode, 5);
                        break;
                    case "2-5":
                        presenter.orderStatusChange(order.orders.get(2).ccode, 2);
                        break;
                    case "2-2":
                        presenter.orderStatusChange(order.orders.get(2).ccode, 3);
                        break;
                    case "3-1":
                        presenter.orderStatusChange(order.orders.get(3).ccode, 5);
                        break;
                    case "3-5":
                        presenter.orderStatusChange(order.orders.get(3).ccode, 2);
                        break;
                    case "3-2":
                        presenter.orderStatusChange(order.orders.get(3).ccode, 3);
                        break;
                    case "0-3":
                        presenter.dpStatusChange(ccode,"2");
                        break;
                    case "0-3&1-3":
                        presenter.dpStatusChange(ccode,"2");
                        break;
                        case "0-3&1-3&2-3":
                        presenter.dpStatusChange(ccode,"2");
                        break;
                    case "0-3&1-3&2-3&3-3":
                        presenter.dpStatusChange(ccode,"2");
                        break;
                }
            }
        });
    }

    @Subscribe
    public void loadData(Object object) {
        if (object.getClass() == CpOrderInfoEntity.class) {
            order = (CpOrderInfoEntity) object;
            if (order.rspCode.equals("00")){
                initView(order);
            }else {
                ShowToast.showBottom(this,order.rspDesc);
            }
        } else if (object.getClass() == OrderStatusChangeEntity.class) {
            presenter.carpoolOrderInfo(cmainid);
            String s = btnOrder.getTag().toString();
            if (s.endsWith("1")){
                presenter.dpStatusChange(ccode,"1");
            }
        } else if (object.getClass() == DpStatusChangeEntity.class) {
            if (btnOrder.getTag().toString().endsWith("3")){
                finish();
                //跳转订单完成详情页面
                CarPoolInfoActivity.startCarPoolInfoActivity(this, cmainid);
            }
        }
    }

    private void initView(CpOrderInfoEntity order) {
        phone=order.orders.get(0).phone;
        btnUser1.setHint("尾号" + order.orders.get(0).phone.substring(7));
        tvTime.setText(order.orders.get(0).cstarttime.substring(5, 16) + "   城际拼车   " + order.orders.get(0).num + "人  ¥" + order.orders.get(0).driverprice);
        tvStartdd.setText(order.orders.get(0).cstartaddress);
        tvEndd.setText(order.orders.get(0).cendaddress);
        tvMsg.setText(order.orders.get(0).remark);
        lat = Double.parseDouble(order.orders.get(0).slat);
        lon =Double.parseDouble(order.orders.get(0).slng);
        address = order.orders.get(0).cstartaddress;
        routePlan(Double.valueOf(order.orders.get(0).slat), Double.valueOf(order.orders.get(0).slng), Double.valueOf(order.orders.get(0).elat), Double.valueOf(order.orders.get(0).elng));
        switch (order.orders.size()) {
            case 1:
                switch (order.orders.get(0).orderstatus) {
                    case 1:
                        btnOrder.setHint("去接乘客" + order.orders.get(0).phone.substring(7));
                        btnOrder.setTag("0-1");
                        break;
                    case 2://行程中
                        tvStatusUser1.setVisibility(View.VISIBLE);
                        tvStatusUser1.setText("行程中");
                        btnOrder.setHint("到达" + order.orders.get(0).phone.substring(7) + "目的地");
                        btnOrder.setTag("0-2");
                        break;
                    case 3://已完成
                        tvStatusUser1.setVisibility(View.VISIBLE);
                        tvStatusUser1.setText("已完成");
                        tvStatusUser1.setTextColor(Color.RED);
                        btnOrder.setHint("向右滑动完成订单");
                        btnOrder.setTag("0-3");
                        break;
                    case 5://接驾中
                        tvStatusUser1.setVisibility(View.VISIBLE);
                        tvStatusUser1.setText("接驾中");
                        btnOrder.setHint("乘客" + order.orders.get(0).phone.substring(7) + "已上车");
                        btnOrder.setTag("0-5");
                        break;
                }
                break;
            case 2:
                llUser2.setVisibility(View.VISIBLE);
                btnUser2.setHint("尾号" + order.orders.get(1).phone.substring(7));
                switch (order.orders.get(0).orderstatus) {
                    case 1:
                        btnOrder.setHint("去接乘客" + order.orders.get(0).phone.substring(7));
                        btnOrder.setTag("0-1");
                        break;
                    case 2://行程中
                        tvStatusUser1.setVisibility(View.VISIBLE);
                        tvStatusUser1.setText("行程中");
                        btnOrder.setHint("到达" + order.orders.get(0).phone.substring(7) + "目的地");
                        btnOrder.setTag("0-2");
                        break;
                    case 3://已完成
                        tvStatusUser1.setVisibility(View.VISIBLE);
                        tvStatusUser1.setText("已完成");
                        tvStatusUser1.setTextColor(Color.RED);
                        phone=order.orders.get(1).phone;
                        tvTime.setText(order.orders.get(1).cstarttime.substring(5, 16) + "   城际拼车   " + order.orders.get(1).num + "人  ¥" + order.orders.get(1).driverprice);
                        tvStartdd.setText(order.orders.get(1).cstartaddress);
                        tvEndd.setText(order.orders.get(1).cendaddress);
                        tvMsg.setText(order.orders.get(1).remark);
                        switch (order.orders.get(1).orderstatus){
                            case 2://行程中
                                tvStatusUser2.setVisibility(View.VISIBLE);
                                tvStatusUser2.setText("行程中");
                                btnOrder.setHint("到达" + order.orders.get(1).phone.substring(7) + "目的地");
                                btnOrder.setTag("1-2");
                                break;
                            case 3://已完成
                                tvStatusUser2.setVisibility(View.VISIBLE);
                                tvStatusUser2.setText("已完成");
                                tvStatusUser2.setTextColor(Color.RED);
                                btnOrder.setHint("向右滑动完成订单");
                                btnOrder.setTag("0-3&1-3");
                                break;
                            case 5://接驾中
                                tvStatusUser2.setVisibility(View.VISIBLE);
                                tvStatusUser2.setText("接驾中");
                                btnOrder.setHint("乘客" + order.orders.get(1).phone.substring(7) + "已上车");
                                btnOrder.setTag("1-5");
                                break;
                        }
                        break;
                    case 5://接驾中
                        tvStatusUser1.setVisibility(View.VISIBLE);
                        tvStatusUser1.setText("接驾中");
                        btnOrder.setHint("乘客" + order.orders.get(0).phone.substring(7) + "已上车");
                        btnOrder.setTag("0-5");
                        break;
                }

                switch (order.orders.get(1).orderstatus){
                    case 2://行程中
                        tvStatusUser2.setVisibility(View.VISIBLE);
                        tvStatusUser2.setText("行程中");
                        break;
                    case 3://已完成
                        tvStatusUser2.setVisibility(View.VISIBLE);
                        tvStatusUser2.setText("已完成");
                        tvStatusUser2.setTextColor(Color.RED);
                        break;
                    case 5://接驾中
                        tvStatusUser2.setVisibility(View.VISIBLE);
                        tvStatusUser2.setText("接驾中");
                        break;
                }
                break;
            case 3:
                llUser2.setVisibility(View.VISIBLE);
                llUser3.setVisibility(View.VISIBLE);
                btnUser2.setHint("尾号" + order.orders.get(1).phone.substring(7));
                btnUser3.setHint("尾号" + order.orders.get(2).phone.substring(7));
                switch (order.orders.get(0).orderstatus) {
                    case 1:
                        btnOrder.setHint("去接乘客" + order.orders.get(0).phone.substring(7));
                        btnOrder.setTag("0-1");
                        break;
                    case 2://行程中
                        tvStatusUser1.setVisibility(View.VISIBLE);
                        tvStatusUser1.setText("行程中");
                        btnOrder.setHint("到达" + order.orders.get(0).phone.substring(7) + "目的地");
                        btnOrder.setTag("0-2");
                        break;
                    case 3://已完成
                        tvStatusUser1.setVisibility(View.VISIBLE);
                        tvStatusUser1.setText("已完成");
                        tvStatusUser1.setTextColor(Color.RED);
                        switch (order.orders.get(1).orderstatus){
                            case 2://行程中
                                tvStatusUser2.setVisibility(View.VISIBLE);
                                tvStatusUser2.setText("行程中");
                                btnOrder.setHint("到达" + order.orders.get(1).phone.substring(7) + "目的地");
                                btnOrder.setTag("1-2");
                                break;
                            case 3://已完成
                                tvStatusUser2.setVisibility(View.VISIBLE);
                                tvStatusUser2.setText("已完成");
                                tvStatusUser2.setTextColor(Color.RED);
                                phone=order.orders.get(2).phone;
                                tvTime.setText(order.orders.get(2).cstarttime.substring(5, 16) + "   城际拼车   " + order.orders.get(2).num + "人  ¥" + order.orders.get(2).driverprice);
                                tvStartdd.setText(order.orders.get(2).cstartaddress);
                                tvEndd.setText(order.orders.get(2).cendaddress);
                                tvMsg.setText(order.orders.get(2).remark);
                                switch (order.orders.get(2).orderstatus){
                                    case 2://行程中
                                        tvStatusUser3.setVisibility(View.VISIBLE);
                                        tvStatusUser3.setText("行程中");
                                        btnOrder.setHint("到达" + order.orders.get(2).phone.substring(7) + "目的地");
                                        btnOrder.setTag("2-2");
                                        break;
                                    case 3://已完成
                                        tvStatusUser3.setVisibility(View.VISIBLE);
                                        tvStatusUser3.setText("已完成");
                                        tvStatusUser3.setTextColor(Color.RED);
                                        btnOrder.setHint("向右滑动完成订单");
                                        btnOrder.setTag("0-3&1-3&2-3");
                                        break;
                                    case 5://接驾中
                                        tvStatusUser3.setVisibility(View.VISIBLE);
                                        tvStatusUser3.setText("接驾中");
                                        btnOrder.setHint("乘客" + order.orders.get(2).phone.substring(7) + "已上车");
                                        btnOrder.setTag("2-5");
                                        break;
                                }
                                break;
                            case 5://接驾中
                                tvStatusUser2.setVisibility(View.VISIBLE);
                                tvStatusUser2.setText("接驾中");
                                btnOrder.setHint("乘客" + order.orders.get(1).phone.substring(7) + "已上车");
                                btnOrder.setTag("1-5");
                                break;
                        }
                        break;
                    case 5://接驾中
                        tvStatusUser1.setVisibility(View.VISIBLE);
                        tvStatusUser1.setText("接驾中");
                        btnOrder.setHint("乘客" + order.orders.get(0).phone.substring(7) + "已上车");
                        btnOrder.setTag("0-5");
                        break;
                }
                switch (order.orders.get(1).orderstatus){
                    case 2://行程中
                        tvStatusUser2.setVisibility(View.VISIBLE);
                        tvStatusUser2.setText("行程中");
                        break;
                    case 3://已完成
                        tvStatusUser2.setVisibility(View.VISIBLE);
                        tvStatusUser2.setText("已完成");
                        tvStatusUser2.setTextColor(Color.RED);
                        break;
                    case 5://接驾中
                        tvStatusUser2.setVisibility(View.VISIBLE);
                        tvStatusUser2.setText("接驾中");
                        break;
                }
                switch (order.orders.get(2).orderstatus){
                    case 2://行程中
                        tvStatusUser3.setVisibility(View.VISIBLE);
                        tvStatusUser3.setText("行程中");
                        break;
                    case 3://已完成
                        tvStatusUser3.setVisibility(View.VISIBLE);
                        tvStatusUser3.setText("已完成");
                        tvStatusUser3.setTextColor(Color.RED);
                        break;
                    case 5://接驾中
                        tvStatusUser3.setVisibility(View.VISIBLE);
                        tvStatusUser3.setText("接驾中");
                        break;
                }
                break;
            case 4:
                llUser2.setVisibility(View.VISIBLE);
                llUser3.setVisibility(View.VISIBLE);
                llUser4.setVisibility(View.VISIBLE);
                btnUser2.setHint("尾号" + order.orders.get(1).phone.substring(7));
                btnUser3.setHint("尾号" + order.orders.get(2).phone.substring(7));
                btnUser4.setHint("尾号" + order.orders.get(3).phone.substring(7));
                switch (order.orders.get(0).orderstatus) {
                    case 1:
                        btnOrder.setHint("去接乘客" + order.orders.get(0).phone.substring(7));
                        btnOrder.setTag("0-1");
                        break;
                    case 2://行程中
                        tvStatusUser1.setVisibility(View.VISIBLE);
                        tvStatusUser1.setText("行程中");
                        btnOrder.setHint("到达" + order.orders.get(0).phone.substring(7) + "目的地");
                        btnOrder.setTag("0-2");
                        break;
                    case 3://已完成
                        tvStatusUser1.setVisibility(View.VISIBLE);
                        tvStatusUser1.setText("已完成");
                        tvStatusUser1.setTextColor(Color.RED);
                        switch (order.orders.get(1).orderstatus){
                            case 1:
                                btnOrder.setHint("去接乘客" + order.orders.get(1).phone.substring(7));
                                btnOrder.setTag("1-1");
                                break;
                            case 2://行程中
                                tvStatusUser2.setVisibility(View.VISIBLE);
                                tvStatusUser2.setText("行程中");
                                btnOrder.setHint("到达" + order.orders.get(1).phone.substring(7) + "目的地");
                                btnOrder.setTag("1-2");
                                break;
                            case 3://已完成
                                tvStatusUser2.setVisibility(View.VISIBLE);
                                tvStatusUser2.setText("已完成");
                                tvStatusUser2.setTextColor(Color.RED);
                                switch (order.orders.get(2).orderstatus){
                                    case 2://行程中
                                        tvStatusUser3.setVisibility(View.VISIBLE);
                                        tvStatusUser3.setText("行程中");
                                        btnOrder.setHint("到达" + order.orders.get(2).phone.substring(7) + "目的地");
                                        btnOrder.setTag("2-2");
                                        break;
                                    case 3://已完成
                                        tvStatusUser3.setVisibility(View.VISIBLE);
                                        tvStatusUser3.setText("已完成");
                                        tvStatusUser3.setTextColor(Color.RED);
                                        phone=order.orders.get(3).phone;
                                        tvTime.setText(order.orders.get(3).cstarttime.substring(5, 16) + "   城际拼车   " + order.orders.get(3).num + "人  ¥" + order.orders.get(3).driverprice);
                                        tvStartdd.setText(order.orders.get(3).cstartaddress);
                                        tvEndd.setText(order.orders.get(3).cendaddress);
                                        tvMsg.setText(order.orders.get(3).remark);
                                        switch (order.orders.get(3).orderstatus){
                                            case 2://行程中
                                                tvStatusUser4.setVisibility(View.VISIBLE);
                                                tvStatusUser4.setText("行程中");
                                                btnOrder.setHint("到达" + order.orders.get(3).phone.substring(7) + "目的地");
                                                btnOrder.setTag("3-2");
                                                break;
                                            case 3://已完成
                                                tvStatusUser4.setVisibility(View.VISIBLE);
                                                tvStatusUser4.setText("已完成");
                                                tvStatusUser4.setTextColor(Color.RED);
                                                btnOrder.setTag("0-3&1-3&2-3&3-3");
                                                btnOrder.setHint("向右滑动完成订单");
                                                break;
                                            case 5://接驾中
                                                tvStatusUser4.setVisibility(View.VISIBLE);
                                                tvStatusUser4.setText("接驾中");
                                                btnOrder.setHint("乘客" + order.orders.get(3).phone.substring(7) + "已上车");
                                                btnOrder.setTag("3-5");
                                                break;
                                        }
                                        break;
                                    case 5://接驾中
                                        tvStatusUser3.setVisibility(View.VISIBLE);
                                        tvStatusUser3.setText("接驾中");
                                        btnOrder.setHint("乘客" + order.orders.get(2).phone.substring(7) + "已上车");
                                        btnOrder.setTag("2-5");
                                        break;
                                }
                                break;
                            case 5://接驾中
                                tvStatusUser2.setVisibility(View.VISIBLE);
                                tvStatusUser2.setText("接驾中");
                                btnOrder.setHint("乘客" + order.orders.get(1).phone.substring(7) + "已上车");
                                btnOrder.setTag("1-5");
                                break;
                        }
                        break;
                    case 5://接驾中
                        tvStatusUser1.setVisibility(View.VISIBLE);
                        tvStatusUser1.setText("接驾中");
                        btnOrder.setHint("乘客" + order.orders.get(0).phone.substring(7) + "已上车");
                        btnOrder.setTag("0-5");
                        break;
                }
                switch (order.orders.get(1).orderstatus){
                    case 2://行程中
                        tvStatusUser2.setVisibility(View.VISIBLE);
                        tvStatusUser2.setText("行程中");
                        break;
                    case 3://已完成
                        tvStatusUser2.setVisibility(View.VISIBLE);
                        tvStatusUser2.setText("已完成");
                        tvStatusUser2.setTextColor(Color.RED);
                        break;
                    case 5://接驾中
                        tvStatusUser2.setVisibility(View.VISIBLE);
                        tvStatusUser2.setText("接驾中");
                        break;
                }
                switch (order.orders.get(2).orderstatus){
                    case 2://行程中
                        tvStatusUser3.setVisibility(View.VISIBLE);
                        tvStatusUser3.setText("行程中");
                        break;
                    case 3://已完成
                        tvStatusUser3.setVisibility(View.VISIBLE);
                        tvStatusUser3.setText("已完成");
                        tvStatusUser3.setTextColor(Color.RED);
                        break;
                    case 5://接驾中
                        tvStatusUser3.setVisibility(View.VISIBLE);
                        tvStatusUser3.setText("接驾中");
                        break;
                }
                switch (order.orders.get(3).orderstatus){
                    case 2://行程中
                        tvStatusUser4.setVisibility(View.VISIBLE);
                        tvStatusUser4.setText("行程中");
                        break;
                    case 3://已完成
                        tvStatusUser4.setVisibility(View.VISIBLE);
                        tvStatusUser4.setText("已完成");
                        tvStatusUser4.setTextColor(Color.RED);
                        break;
                    case 5://接驾中
                        tvStatusUser4.setVisibility(View.VISIBLE);
                        tvStatusUser4.setText("接驾中");
                        break;
                }
                break;
        }
    }

    //路径规划
    private void routePlan(double startLat, double startLon, double endLat, double endLon) {
        searchUtil = new RoutePlanSearchUtil(mvCarpool.getMap(), this);
        searchUtil.drivingPlan(startLat, startLon, endLat, endLon);
//        BaiduMapUtil.getInstance().setBaiduMap(mvCarpool);
//        BaiduMapUtil.getInstance().registerLocationListener();
    }

    @OnClick({R.id.btn_user1, R.id.btn_user2,R.id.btn_user3, R.id.btn_user4, R.id.iv_start_nav, R.id.iv_connect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_user1:
                phone = order.orders.get(0).phone;
                tvTime.setText(order.orders.get(0).cstarttime.substring(5, 16) +  "   城际拼车   " + order.orders.get(0).num + "人  ¥" + order.orders.get(0).driverprice);
                tvStartdd.setText(order.orders.get(0).cstartaddress);
                tvEndd.setText(order.orders.get(0).cendaddress);
                tvMsg.setText(order.orders.get(0).remark);
                lat = Double.parseDouble(order.orders.get(0).slat);
                lon =Double.parseDouble(order.orders.get(0).slng);
                address = order.orders.get(0).cstartaddress;
                switch (order.orders.get(0).orderstatus) {
                    case 1:
                        btnOrder.setHint("去接乘客" + order.orders.get(0).phone.substring(7));
                        btnOrder.setTag("0-1");
                        break;
                    case 2://行程中
                        btnOrder.setHint("到达" + order.orders.get(0).phone.substring(7) + "目的地");
                        btnOrder.setTag("0-2");
                        break;
                    case 5://接驾中
                        btnOrder.setHint("乘客" + order.orders.get(0).phone.substring(7) + "已上车");
                        btnOrder.setTag("0-5");
                        break;
                }
                routePlan(Double.valueOf(order.orders.get(0).slat), Double.valueOf(order.orders.get(0).slng), Double.valueOf(order.orders.get(0).elat), Double.valueOf(order.orders.get(0).elng));
                break;
            case R.id.btn_user2:
                phone = order.orders.get(1).phone;
                tvTime.setText(order.orders.get(1).cstarttime.substring(5, 16) +  "   城际拼车   " + order.orders.get(1).num + "人  ¥" + order.orders.get(1).driverprice);
                tvStartdd.setText(order.orders.get(1).cstartaddress);
                tvEndd.setText(order.orders.get(1).cendaddress);
                tvMsg.setText(order.orders.get(1).remark);
                lat = Double.parseDouble(order.orders.get(1).slat);
                lon =Double.parseDouble(order.orders.get(1).slng);
                address = order.orders.get(1).cstartaddress;
                switch (order.orders.get(1).orderstatus) {
                    case 1:
                        btnOrder.setHint("去接乘客" + order.orders.get(1).phone.substring(7));
                        btnOrder.setTag("1-1");
                        break;
                    case 2://行程中
                        btnOrder.setHint("到达" + order.orders.get(1).phone.substring(7) + "目的地");
                        btnOrder.setTag("1-2");
                        break;
                    case 5://接驾中
                        btnOrder.setHint("乘客" + order.orders.get(1).phone.substring(7) + "已上车");
                        btnOrder.setTag("1-5");
                        break;
                }
                routePlan(Double.valueOf(order.orders.get(1).slat), Double.valueOf(order.orders.get(1).slng), Double.valueOf(order.orders.get(1).elat), Double.valueOf(order.orders.get(1).elng));
                break;
            case R.id.btn_user3:
                phone = order.orders.get(2).phone;
                tvTime.setText(order.orders.get(2).cstarttime.substring(5, 16) +  "   城际拼车   " + order.orders.get(2).num + "人  ¥" + order.orders.get(2).driverprice);
                tvStartdd.setText(order.orders.get(2).cstartaddress);
                tvEndd.setText(order.orders.get(2).cendaddress);
                tvMsg.setText(order.orders.get(2).remark);
                lat = Double.parseDouble(order.orders.get(2).slat);
                lon =Double.parseDouble(order.orders.get(2).slng);
                address = order.orders.get(2).cstartaddress;
                switch (order.orders.get(2).orderstatus) {
                    case 1:
                        btnOrder.setHint("去接乘客" + order.orders.get(2).phone.substring(7));
                        btnOrder.setTag("2-1");
                        break;
                    case 2://行程中
                        btnOrder.setHint("到达" + order.orders.get(2).phone.substring(7) + "目的地");
                        btnOrder.setTag("2-2");
                        break;
                    case 5://接驾中
                        btnOrder.setHint("乘客" + order.orders.get(2).phone.substring(7) + "已上车");
                        btnOrder.setTag("2-5");
                        break;
                }
                routePlan(Double.valueOf(order.orders.get(2).slat), Double.valueOf(order.orders.get(2).slng), Double.valueOf(order.orders.get(2).elat), Double.valueOf(order.orders.get(2).elng));
                break;
            case R.id.btn_user4:
                phone = order.orders.get(3).phone;
                tvTime.setText(order.orders.get(3).cstarttime.substring(5, 16) +  "   城际拼车   " + order.orders.get(3).num + "人  ¥" + order.orders.get(3).driverprice);
                tvStartdd.setText(order.orders.get(3).cstartaddress);
                tvEndd.setText(order.orders.get(3).cendaddress);
                tvMsg.setText(order.orders.get(3).remark);
                lat = Double.parseDouble(order.orders.get(3).slat);
                lon =Double.parseDouble(order.orders.get(3).slng);
                address = order.orders.get(3).cstartaddress;
                switch (order.orders.get(3).orderstatus) {
                    case 1:
                        btnOrder.setHint("去接乘客" + order.orders.get(3).phone.substring(7));
                        btnOrder.setTag("3-1");
                        break;
                    case 2://行程中
                        btnOrder.setHint("到达" + order.orders.get(3).phone.substring(7) + "目的地");
                        btnOrder.setTag("3-2");
                        break;
                    case 5://接驾中
                        btnOrder.setHint("乘客" + order.orders.get(3).phone.substring(7) + "已上车");
                        btnOrder.setTag("3-5");
                        break;
                }
                routePlan(Double.valueOf(order.orders.get(3).slat), Double.valueOf(order.orders.get(3).slng), Double.valueOf(order.orders.get(3).elat), Double.valueOf(order.orders.get(3).elng));
                break;
            case R.id.iv_start_nav://导航起点
                if (StartUtil.isInstallByread(StartUtil.baiduMapPackage) || StartUtil.isInstallByread(StartUtil.gaodeMapPackage)) {
                    navDialog = new NaviSelectDialog(this);
                    navDialog.setonBaiduMapClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            navDialog.dismiss();
                            StartUtil.startBaiduMap(lat, lon, address, CarPoolOrderInfoActivity.this);
                        }
                    });

                    navDialog.setonGaodeMapClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            navDialog.dismiss();
                            StartUtil.startGaoDeMap(CarPoolOrderInfoActivity.this, lat, lon);
                        }
                    });
                    navDialog.show();
                } else {
                    ShowToast.showCenter(this, "您还未安装百度地图或者高德地图");
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.unregister(this);
        }
    }
}
