package com.mxingo.driver.module.take;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.mxingo.driver.MyApplication;
import com.mxingo.driver.R;
import com.mxingo.driver.dialog.MessageDialog;
import com.mxingo.driver.model.CheckVersionEntity;
import com.mxingo.driver.model.DriverInfoEntity;
import com.mxingo.driver.model.LogoutEntity;
import com.mxingo.driver.model.OfflineEntity;
import com.mxingo.driver.model.OnlineEntity;
import com.mxingo.driver.model.PushOrderEntity;
import com.mxingo.driver.module.AntiepidemicActivity;
import com.mxingo.driver.module.BaseActivity;
import com.mxingo.driver.module.DriverCarRegistrationActivity;
import com.mxingo.driver.module.HybridSearchActivity;
import com.mxingo.driver.module.LoginActivity;
import com.mxingo.driver.module.MyWalletActivity;
import com.mxingo.driver.module.SettingActivity;
import com.mxingo.driver.module.WebViewActivity;
import com.mxingo.driver.module.base.data.UserInfoPreferences;
import com.mxingo.driver.module.base.download.UpdateVersionActivity;
import com.mxingo.driver.module.base.download.VersionEntity;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.MyPresenter;
//import com.mxingo.driver.module.base.speech.MySpeechSynthesizer;
import com.mxingo.driver.module.base.log.LogUtils;
import com.mxingo.driver.module.base.map.trace.BaiduTrack;
import com.mxingo.driver.module.base.speech.MySpeechUtils;
import com.mxingo.driver.module.order.CarpoolOrderActivity;
import com.mxingo.driver.module.order.MyOrderActivity;
import com.mxingo.driver.module.order.OrdersActivity;
import com.mxingo.driver.utils.BitmapUtil;
import com.mxingo.driver.utils.CommonUtil;
import com.mxingo.driver.utils.Constants;
import com.mxingo.driver.utils.DisplayUtil;
import com.mxingo.driver.utils.StartUtil;
import com.mxingo.driver.utils.VersionInfo;
import com.mxingo.driver.widget.MyProgress;
import com.mxingo.driver.widget.ShowToast;
import com.squareup.otto.Subscribe;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Inject
    MyPresenter presenter;//依赖注入
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

    @BindView(R.id.fi_call)
    FrameLayout fiCall;
    @BindView(R.id.ll_antiepidemic)
    LinearLayout llAntiepidemic;

    private Button btnOffline;
    private Button btnExit;
    private TextView tvCarTeam;
    private TextView tvName;
    private TextView tvMobile;
    private TextView tvRecvNum;
    private MySpeechUtils speechUtils;
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

        PushManager.getInstance().initialize(getApplicationContext());

        checkNotificationEnabled();

        BitmapUtil.init();
        progress = new MyProgress(this);
        driverNo = UserInfoPreferences.getInstance().getDriverNo();

        initView();

        speechUtils = new MySpeechUtils();
        takeDialog = new TakeOrderDialog(this, driverNo);
        ComponentHolder.getAppComponent().inject(this);
        presenter.register(this);//注册
        gson = new Gson();
        EventBus.getDefault().register(this);
        presenter.checkVersion(Constants.RX_DRIVER_APP);
        MyApplication.isMainActivityLive = true;
    }

    private void checkNotificationEnabled() {
        boolean isEnabled = isNotificationEnabled(this);
        LogUtils.d("is notification enabled: ",isEnabled+"");
        if (!isEnabled) {
            goSetting();
        }
    }

    private void goSetting() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_go_setting, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(view);
        //确认
        dialog.findViewById(R.id.tv_go_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= 26) {
                    // android 8.0引导
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
                } else if (Build.VERSION.SDK_INT >= 21) {
                    // android 5.0-7.0
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);
                } else {
                    // 其他
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        //取消
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private boolean isNotificationEnabled(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        return notificationManagerCompat.areNotificationsEnabled();
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
        llAntiepidemic.setOnClickListener(this);

        btnOnline.setOnClickListener(this);

        View view = navView.getHeaderView(0);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvMobile = (TextView) view.findViewById(R.id.tv_mobile);
        tvCarTeam = (TextView) view.findViewById(R.id.tv_car_team);
        tvRecvNum = (TextView) view.findViewById(R.id.tv_recv_num);

        view.findViewById(R.id.ll_orders).setOnClickListener(this);
        view.findViewById(R.id.ll_my_order).setOnClickListener(this);
        view.findViewById(R.id.ll_carpool_order).setOnClickListener(this);

        if (UserInfoPreferences.getInstance().showWallet()){
            view.findViewById(R.id.ll_my_wallet).setVisibility(View.VISIBLE);
            view.findViewById(R.id.v_wallet).setVisibility(View.VISIBLE);
        }
        view.findViewById(R.id.ll_my_wallet).setOnClickListener(this);

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
            case R.id.ll_antiepidemic://通知
                AntiepidemicActivity.startAntiepidemicActivity(this, info);
                break;
            case R.id.btn_online: {//上线
//                progress.show();
//                presenter.online(driverNo);
                requestPermissions();
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
            case R.id.ll_carpool_order: {//拼车订单
                CarpoolOrderActivity.startCarpoolOrderActivity(this, driverNo);
                break;
            }
            case R.id.ll_my_wallet: {//我的钱包
                MyWalletActivity.startMyWalletActivity(this,driverNo);
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

    private void requestPermissions(){
        List list= new ArrayList<String>();
        list.add(Manifest.permission.RECORD_AUDIO);
        list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            // 前台服务权限
            list.add(Manifest.permission.FOREGROUND_SERVICE);
        }

        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            String s = (String) iterator.next();
            if (ContextCompat.checkSelfPermission(this, String.valueOf(s))== PackageManager.PERMISSION_GRANTED){
                iterator.remove();
            }
        }

        if (list.isEmpty()){
            progress.show();
            presenter.online(driverNo);
        }else {
            ActivityCompat.requestPermissions(this, (String[]) list.toArray(new String[0]),Constants.permissionMain);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0]==PackageManager.PERMISSION_DENIED){
            MessageDialog message = new MessageDialog(this);
            message.setMessageText("您的权限申请失败，请前往应用设置打开权限");
            message.setOnOkClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("package:" + getPackageName());
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,uri);
                    startActivity(intent);
                    message.dismiss();
                }
            });
            message.setOnCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    message.dismiss();
                }
            });
            message.show();
        }else {
            presenter.online(driverNo);
        }
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


    private void call110Mobile(final String mobile) {
        final MessageDialog dialog = new MessageDialog(this);
        dialog.setMessageText("" + mobile);
        dialog.setOkText("拨打110");
        dialog.setCancelText("联系客服");
        dialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                StartUtil.startPhone("4008878810", MainActivity.this);
            }
        });
        dialog.setOnOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                StartUtil.startPhone("110", MainActivity.this);
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
        if (takeDialog != null) {
            takeDialog.dismiss();
        }
        speechUtils.destroy();

        CommonUtil.saveCurrentLocation();

        if (BaiduTrack.getInstance().mClient!=null){
            if (UserInfoPreferences.getInstance().contains("is_trace_started")&&UserInfoPreferences.getInstance().getTraceStart()){
                // 退出app停止轨迹服务时，不再接收回调，将OnTraceListener置空
                BaiduTrack.getInstance().mClient.setOnTraceListener(null);
                BaiduTrack.getInstance().mClient.stopTrace(BaiduTrack.getInstance().mTrace, null);
            }else {
                BaiduTrack.getInstance().clear();
            }
        }
        BaiduTrack.getInstance().isTraceStarted =false;
        BaiduTrack.getInstance().isTraceStarted =false;
        UserInfoPreferences.getInstance().remove("is_trace_started");
        UserInfoPreferences.getInstance().remove("is_gather_started");
        BitmapUtil.clear();
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
            speechUtils.startSpeaking("上线接单啦");
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
            speechUtils.startSpeaking("停止接单啦");
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


    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void getuiPush(Intent intent) {
        String pushDataStr = intent.getStringExtra(Constants.PUSH_DATA);
//        LogUtils.d("pushData main", pushDataStr + "");
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
                    //开启新的播报
                    if (!takeDialog.isShowing()) {
                        takeDialog.show();
                        takeDialog.setPushData(pushStack);
                    }
                }
            }
        }
    };

    @OnClick(R.id.fi_call)
    public void onClick() {
        call110Mobile("如遇突发紧急情况及生命财产受到侵犯，请直接拨打110！");
    }
}
