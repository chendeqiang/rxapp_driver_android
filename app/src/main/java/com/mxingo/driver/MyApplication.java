package com.mxingo.driver;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.igexin.sdk.PushManager;
import com.mxingo.driver.module.base.http.AppComponent;
import com.mxingo.driver.module.base.http.AppModule;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.DaggerAppComponent;
import com.mxingo.driver.module.base.push.MyPushService;
import com.mxingo.driver.module.base.push.PushIntentService;

import com.mxingo.driver.utils.Constants;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by zhouwei on 2017/6/22.
 */

public class MyApplication extends Application {

    public static Application application;
    public static Bus bus;
    public static String currActivity = "";
    public static boolean isMainActivityLive = false;

    @Override
    public void onCreate() {
        super.onCreate();
        AppComponent appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        ComponentHolder.setAppComponent(appComponent);
        application = this;
        bus = new Bus();
        bus.register(this);


    }

    @Subscribe
    public void startApp(Object o) {
        //个推初始化
        PushManager.getInstance().initialize(this.getApplicationContext(), MyPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), PushIntentService.class);

        //百度地图
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);

        //讯飞语音
        Setting.setShowLog(false);
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=" + Constants.speechAppId);

        //umeng
        MobclickAgent.setDebugMode(false);
        MobclickAgent.setCatchUncaughtExceptions(true);
        MobclickAgent.enableEncrypt(true);//6.0.0版本及以后
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        bus.unregister(this);

    }

}
