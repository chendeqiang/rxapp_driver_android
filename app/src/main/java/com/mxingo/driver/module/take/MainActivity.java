package com.mxingo.driver.module.take;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mxingo.driver.MyApplication;
import com.mxingo.driver.R;
import com.mxingo.driver.dialog.MessageDialog;
import com.mxingo.driver.model.CheckVersionEntity;
import com.mxingo.driver.model.DriverInfoEntity;
import com.mxingo.driver.model.LogoutEntity;
import com.mxingo.driver.model.OfflineEntity;
import com.mxingo.driver.model.OnlineEntity;
import com.mxingo.driver.model.PushOrderEntity;
import com.mxingo.driver.module.BaseActivity;
import com.mxingo.driver.module.DriverCarRegistrationActivity;
import com.mxingo.driver.module.HybridSearchActivity;
import com.mxingo.driver.module.LoginActivity;
import com.mxingo.driver.module.MyBillActivity;
import com.mxingo.driver.module.NoticeActivity;
import com.mxingo.driver.module.SettingActivity;
import com.mxingo.driver.module.WebViewActivity;
import com.mxingo.driver.module.base.data.UserInfoPreferences;
import com.mxingo.driver.module.base.download.UpdateVersionActivity;
import com.mxingo.driver.module.base.download.VersionEntity;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.MyPresenter;
import com.mxingo.driver.module.base.log.LogUtils;
import com.mxingo.driver.module.base.map.trace.MyTrace;
import com.mxingo.driver.module.base.speech.MySpeechSynthesizer;
import com.mxingo.driver.module.order.MyOrderActivity;
import com.mxingo.driver.module.order.OrdersActivity;
import com.mxingo.driver.utils.Constants;
import com.mxingo.driver.utils.DisplayUtil;
import com.mxingo.driver.utils.StartUtil;
import com.mxingo.driver.utils.VersionInfo;
import com.mxingo.driver.widget.MyProgress;
import com.mxingo.driver.widget.ShowToast;
import com.squareup.otto.Subscribe;

import org.greenrobot.eventbus.EventBus;

import java.util.Stack;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Inject
    MyPresenter presenter;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.btn_online)
    Button btnOnline;
    @BindView(R.id.img_taking_order)
    ImageView imgTakingOrder;
    @BindView(R.id.rl_notice)
    RelativeLayout rlNotice;

    private Button btnOffline;
    private Button btnExit;
    private TextView tvCarTeam;
    private TextView tvName;
    private TextView tvMobile;
    private TextView tvRecvNum;
    private MySpeechSynthesizer speechSynthesizer;
    private TakeOrderDialog takeDialog = null;

    private String driverNo;
    private Gson gson;
    private final Stack<PushOrderEntity> pushStack = new Stack<>();
    private final int stackSize = 5;
    private MyProgress progress;
    private DriverInfoEntity info;

    public static void startMainActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        progress = new MyProgress(this);
        driverNo = UserInfoPreferences.getInstance().getDriverNo();
        initView();

        speechSynthesizer = new MySpeechSynthesizer();
        takeDialog = new TakeOrderDialog(this, driverNo);

        ComponentHolder.getAppComponent().inject(this);
        presenter.register(this);

        gson = new Gson();
        EventBus.getDefault().register(this);
        MyTrace.getInstance().startTrace();

        presenter.checkVersion(Constants.RX_DRIVER_APP);

        MyApplication.isMainActivityLive = true;
    }

    private void initView() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        tvToolbarTitle.setText(R.string.app_name);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationIcon(R.drawable.ic_header);
        rlNotice.setOnClickListener(this);

        btnOnline.setOnClickListener(this);

        View view = navView.getHeaderView(0);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvMobile = (TextView) view.findViewById(R.id.tv_mobile);
        tvCarTeam = (TextView) view.findViewById(R.id.tv_car_team);
        tvRecvNum = (TextView) view.findViewById(R.id.tv_recv_num);

        view.findViewById(R.id.ll_orders).setOnClickListener(this);
        view.findViewById(R.id.ll_my_order).setOnClickListener(this);
        view.findViewById(R.id.ll_my_bill).setOnClickListener(this);
        view.findViewById(R.id.ll_flight).setOnClickListener(this);
        view.findViewById(R.id.ll_driver_car_registration).setOnClickListener(this);
        view.findViewById(R.id.ll_rule).setOnClickListener(this);
        view.findViewById(R.id.ll_setting).setOnClickListener(this);
        tvCarTeam = (TextView) view.findViewById(R.id.tv_car_team);

        RelativeLayout rlOffline = (RelativeLayout) view.findViewById(R.id.rl_offline);
        ViewGroup.LayoutParams params = rlOffline.getLayoutParams();
        int height = DisplayUtil.getWindow(this).heightPixels - DisplayUtil.dip2px(this, 170 + 7 * 45 + 8 + 20) - 7;
        if (height < DisplayUtil.dip2px(this, 100)) {
            height = DisplayUtil.dip2px(this, 100);
        }
        params.height = height;
        rlOffline.setLayoutParams(params);
        view.findViewById(R.id.ll_kefu).setOnClickListener(this);
        view.findViewById(R.id.ll_car_team).setOnClickListener(this);
        btnExit = (Button) view.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(this);

        btnOffline = (Button) view.findViewById(R.id.btn_offline);
        btnOffline.setOnClickListener(this);
        btnOffline.setVisibility(View.GONE);
        imgTakingOrder.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_notice://通知
                NoticeActivity.startNoticeActivity(this);
                break;
            case R.id.btn_online: {//上线
                progress.show();
                presenter.online(driverNo);
                break;
            }
            case R.id.btn_offline: {//下线
                progress.show();
                presenter.offline(driverNo);
                break;
            }

            case R.id.btn_exit: {//退出
                progress.show();
                presenter.logout(driverNo);
                break;
            }
            case R.id.ll_orders: {//订单池
                OrdersActivity.startOrdersActivity(this, driverNo);
                break;
            }
            case R.id.ll_my_order: {//我的订单
                MyOrderActivity.startMyOrderActivity(this, driverNo);
                break;
            }
            case R.id.ll_my_bill: {//我的账单
                MyBillActivity.startMyOrderActivity(this, driverNo);
                break;
            }
            case R.id.ll_flight: {//航班动态
                HybridSearchActivity.startHybridSearchActivity(this);
                break;
            }
            case R.id.ll_driver_car_registration: {//网约车认证
                DriverCarRegistrationActivity.startDriverCarRegistrationActivity(this, info);
                break;
            }
            case R.id.ll_rule: {//服务规范
                WebViewActivity.startWebViewActivity(this, "服务规范", "http://www.mxingo.com/mxnet/app/serviceSpec.html");
                break;
            }
            case R.id.ll_setting: {//用户设置
                SettingActivity.startSettingActivity(this, driverNo);
                break;
            }
            case R.id.ll_kefu: {//联系客服
                callMobile("4008878810");
                break;
            }
            case R.id.ll_car_team: {//联系车队
                callMobile(tvCarTeam.getText().toString());
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void callMobile(final String mobile) {
        final MessageDialog dialog = new MessageDialog(this);
        dialog.setMessageText("" + mobile);
        dialog.setOkText("呼叫");
        dialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                StartUtil.startPhone(mobile, MainActivity.this);
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.unregister(this);
        }
        EventBus.getDefault().unregister(this);
        MyApplication.isMainActivityLive = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.getInfo(driverNo);
    }

    @Subscribe
    public void loadData(Object o) {
        if (o.getClass() == OnlineEntity.class) {
            online(o);
            progress.dismiss();
        } else if (o.getClass() == OfflineEntity.class) {
            offline(o);
            progress.dismiss();
        } else if (o.getClass() == DriverInfoEntity.class) {
            driverInfo(o);
            progress.dismiss();
        } else if (o.getClass() == LogoutEntity.class) {
            progress.dismiss();
            MyTrace.getInstance().stopTrace();
            UserInfoPreferences.getInstance().clear();
            LoginActivity.startLoginActivity(this);
            finish();
        } else if (o.getClass() == CheckVersionEntity.class) {
            checkVersion((CheckVersionEntity) o);
        }

    }

    private void checkVersion(CheckVersionEntity data) {
        if (data.rspCode.equals("00")) {
            CheckVersionEntity.DataEntity dataEntity = data.data;
            final VersionEntity versionEntity = new Gson().fromJson(dataEntity.value, VersionEntity.class);
            if (VersionInfo.getVersionCode() < versionEntity.versionCode && Constants.RX_DRIVER_APP.equals(dataEntity.key)) {
                versionEntity.isMustUpdate = versionEntity.forceUpdataVersions.contains(VersionInfo.getVersionName());
                UpdateVersionActivity.startUpdateVersionActivity(this, versionEntity);
            }
        }
    }

    private void online(Object o) {
        OnlineEntity data = (OnlineEntity) o;
        if (data.rspCode.equals("00")) {
            onlineView();
            speechSynthesizer.startSpeaking("上线接单啦");
        } else if (data.rspCode.equals("101")) {
            ShowToast.showCenter(this, "TOKEN失效，请重新登陆");
            UserInfoPreferences.getInstance().clear();
            LoginActivity.startLoginActivity(this);
            finish();
        } else {
            ShowToast.showCenter(this, data.rspDesc);
        }
    }

    private void onlineView() {
        btnOnline.setVisibility(View.GONE);
        btnOffline.setVisibility(View.VISIBLE);
        btnExit.setVisibility(View.GONE);
        imgTakingOrder.setVisibility(View.VISIBLE);
        Glide.with(this).load(R.drawable.taking_order_bg).into(imgTakingOrder);
    }

    private void offline(Object o) {
        OfflineEntity data = (OfflineEntity) o;
        if (data.rspCode.equals("00")) {
            offlineView();
            speechSynthesizer.startSpeaking("停止接单啦");
        } else if (data.rspCode.equals("101")) {
            ShowToast.showCenter(this, "TOKEN失效，请重新登陆");
            UserInfoPreferences.getInstance().clear();
            LoginActivity.startLoginActivity(this);
            finish();
        } else {
            ShowToast.showCenter(this, data.rspDesc);
        }
    }

    private void offlineView() {
        btnOnline.setVisibility(View.VISIBLE);
        btnOffline.setVisibility(View.GONE);
        btnExit.setVisibility(View.VISIBLE);
        imgTakingOrder.setVisibility(View.GONE);
    }

    private void driverInfo(Object o) {
        DriverInfoEntity data = (DriverInfoEntity) o;
        if (data.rspCode.equals("00")) {
            info = data;
            tvName.setText(data.driver.cname);
            tvMobile.setText(data.driver.cphone);
            tvCarTeam.setText(data.orgPhone);
            tvRecvNum.setText("" + data.unforder + "");
            if (data.onlineStatus == 2) {
                onlineView();
            } else {
                offlineView();
            }
        } else if (data.rspCode.equals("101")) {
            ShowToast.showCenter(this, "TOKEN失效，请重新登陆");

            UserInfoPreferences.getInstance().clear();
            LoginActivity.startLoginActivity(this);
            finish();
        }
    }


    @org.greenrobot.eventbus.Subscribe
    public void getuiPush(Intent intent) {
        String pushDataStr = intent.getStringExtra(Constants.PUSH_DATA);
        LogUtils.d("pushData main", pushDataStr + "");
        int pushType = intent.getIntExtra(Constants.PUSH_TYPE, 0);
        if (pushType == Constants.P_D_PUB) {
            Message msg = handler.obtainMessage();
            msg.what = pushType;
            msg.obj = pushDataStr;
            handler.sendMessage(msg);
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constants.P_D_PUB) {
                PushOrderEntity pushOrderEntity = gson.fromJson(msg.obj.toString(), PushOrderEntity.class);
                synchronized (pushStack) {
                    if (pushStack.size() > stackSize) {
                        pushStack.remove(stackSize - 1);
                        pushStack.push(pushOrderEntity);
                    } else {
                        pushStack.push(pushOrderEntity);
                    }

                    //如果当前是订单播报状态，停止当前播报
                    if (takeDialog.isShowing()){
                        takeDialog.dismiss();
                    }
                    //开启新的播报
                    if (!takeDialog.isShowing()) {
                        takeDialog.show();
                        takeDialog.setPushData(pushStack);
                    }
                }
            }
        }
    };
}
