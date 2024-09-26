package com.mxingo.driver;

import android.app.Application;
import android.content.Context;
import com.igexin.sdk.PushManager;
import com.mxingo.driver.module.base.http.AppComponent;
import com.mxingo.driver.module.base.http.AppModule;
import com.mxingo.driver.module.base.http.ComponentHolder;
import com.mxingo.driver.module.base.http.DaggerAppComponent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;


public class MyApplication extends Application {

    public static Application application;
    public static Bus bus;
    public Context mContext;
    public static String currActivity = "";
    public static boolean isMainActivityLive = false;

    @Override
    public void onCreate() {
        super.onCreate();
        //在onCreate()方法中唯一一次初始化了AppComponent对象，并放入了ComponentHolder中
        AppComponent appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        ComponentHolder.setAppComponent(appComponent);
        application = this;
        mContext = getApplicationContext();
        bus = new Bus();
        bus.register(this);
    }

    @Subscribe
    public void startApp(Object o) {

        //个推初始化
        PushManager.getInstance().preInit(this.getApplicationContext());
        PushManager.getInstance().initialize(this.getApplicationContext());

        bus.unregister(this);

    }

}
