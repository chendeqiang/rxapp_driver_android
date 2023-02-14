package com.mxingo.driver;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.common.BaiduMapSDKException;
import com.baidu.trace.LBSTraceClient;
import com.igexin.sdk.PushManager;
import com.mxingo.driver.module.base.http.AppComponent;
import com.mxingo.driver.module.base.http.AppModule;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.DaggerAppComponent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;


public class MyApplication extends Application {

    public static Application application;
    public static Bus bus;
    public static String currActivity = "";
    public static boolean isMainActivityLive = false;

    @Override
    public void onCreate() {
        super.onCreate();
        //UMConfigure.setLogEnabled(true);
        UMConfigure.preInit(getApplicationContext(),"595dd4039f06fd77fd0004e3","Channel ID");
        AppComponent appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        ComponentHolder.setAppComponent(appComponent);
        application = this;
        bus = new Bus();
        bus.register(this);

    }

    @Subscribe
    public void startApp(Object o) {

        LBSTraceClient.setAgreePrivacy(getApplicationContext(),true);

        //个推初始化
        PushManager.getInstance().initialize(this.getApplicationContext());

        //百度地图
        // 是否同意隐私政策，默认为false
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        try {
            // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
            SDKInitializer.initialize(getApplicationContext());
        } catch (BaiduMapSDKException e) {

        }
        SDKInitializer.setCoordType(CoordType.BD09LL);

        //讯飞语音
//        Setting.setShowLog(false);
//        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=" + Constants.speechAppId);

        //umeng
        UMConfigure.init(getApplicationContext(),UMConfigure.DEVICE_TYPE_PHONE,"");
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setDebugMode(false);
        MobclickAgent.setCatchUncaughtExceptions(true);
        MobclickAgent.enableEncrypt(true);//6.0.0版本及以后
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        bus.unregister(this);

    }

}
