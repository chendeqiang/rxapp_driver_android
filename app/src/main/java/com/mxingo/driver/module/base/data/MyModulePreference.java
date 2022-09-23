package com.mxingo.driver.module.base.data;

import android.content.Context;

import com.mxingo.driver.MyApplication;

import net.grandcentrix.tray.TrayPreferences;

/**
 * Created by deqiangchen on 22/3/30.
 */
public class MyModulePreference extends TrayPreferences {

    private static MyModulePreference myModulePreference = null;
    private final String MOBILE = "mobile"; //手机号
    private final String DEVTOKEN = "devToken";//cid
    private final String DRIVERNN = "driverNo";
    private final String ORDERNN = "orderNo";
    private final String LECHTOKEN = "token";
    private final String CAR_TEAM = "car_team";
    private final String START_TIME = "start_time";//开始行程时间


    public static MyModulePreference getInstance() {
        if (myModulePreference == null) {
            synchronized (myModulePreference.getClass()) {
                myModulePreference = new MyModulePreference(MyApplication.application);
            }
        }
        return myModulePreference;
    }

    public MyModulePreference(final Context context) {
        super(context, "myModule", 1);
    }


    public void setDriverNo(String driverNo) {
        myModulePreference.put(DRIVERNN, driverNo);
    }

    public String getDriverNo() {
        return DRIVERNN;
    }

    public void setMobile(String mobile) {
        myModulePreference.put(MOBILE, mobile);
    }

    public String getMobile() {
        return MOBILE;
    }


    public void setDevtoken(String devToken) {
        myModulePreference.put(DEVTOKEN, devToken);
    }

    public String getDevToken() {
        return DEVTOKEN;
    }


    public void setOrderNo(String orderNo) {
        myModulePreference.put(ORDERNN, orderNo);
    }

    public String getOrderNo() {
        return ORDERNN;
    }

    public String getStartTime() {
        return START_TIME;
    }

    public void setStartTime(String startTime) {
        myModulePreference.put(START_TIME, startTime);
    }

    public String getCarTeam() {
        return CAR_TEAM;
    }

    public void setCarTeam(String carTeam) {
        myModulePreference.put(CAR_TEAM, carTeam);
    }

    public String getToken() {
        return LECHTOKEN;
    }

    public void setToken(String lechtoken) {
        myModulePreference.put(LECHTOKEN, lechtoken);
    }

    @Override
    protected void onCreate(int initialVersion) {
        super.onCreate(initialVersion);
    }

    @Override
    protected void onUpgrade(int oldVersion, int newVersion) {
        super.onUpgrade(oldVersion, newVersion);
    }

    @Override
    protected void onDowngrade(int oldVersion, int newVersion) {
        super.onDowngrade(oldVersion, newVersion);
    }
}
