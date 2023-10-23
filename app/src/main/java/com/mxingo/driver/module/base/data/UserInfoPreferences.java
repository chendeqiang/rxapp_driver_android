package com.mxingo.driver.module.base.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.mxingo.driver.MyApplication;
import com.mxingo.driver.utils.Constants;


public class UserInfoPreferences {
    private static UserInfoPreferences preference = null;
    private SharedPreferences sharedPreference;
    private String packageName = "";

    private final String ISFRISTSTART = "isFristStart";
    private final String MOBILE = "mobile"; //手机号
    private final String DEVTOKEN = "devToken";//cid
    private final String DRIVERNN = "driverNo";
    private final String ORDERNN = "orderNo";
    private final String LECHTOKEN = "token";
    private final String CAR_TEAM = "car_team";
    private final String START_TIME = "start_time";//开始行程时间
    private final String PAYACCOUNT = "pay_account";//支付宝账号
    private final String SHOWWALLET = "show_wallet";//显示司机钱包

    private final String ISTRACESTARTED = "is_trace_started";
    private final String ISGATHERSTARTED = "is_gather_started";


    public static UserInfoPreferences getInstance() {
        if (preference == null) {
            synchronized (UserInfoPreferences.class) {
                preference = new UserInfoPreferences(MyApplication.application);
            }
        }
        return preference;
    }


    public UserInfoPreferences(Context context) {
        packageName = context.getPackageName() + "";
        sharedPreference = context.getSharedPreferences("renxing_conf", Context.MODE_PRIVATE);
    }

    public Boolean isFristStart(){
        return sharedPreference.getBoolean(ISFRISTSTART,true);
    }


    public void setNotFristStart(){
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putBoolean(ISFRISTSTART,false);
        edit.apply();
    }



    public void putTraceStart(){
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putBoolean(ISTRACESTARTED,true);
        edit.apply();
    }

    public boolean getTraceStart(){
        return sharedPreference.getBoolean(ISTRACESTARTED,false);
    }

    public void putGatherStart(){
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putBoolean(ISGATHERSTARTED,true);
        edit.apply();
    }

    public boolean getGatherStart(){
        return sharedPreference.getBoolean(ISGATHERSTARTED,false);
    }

    public Boolean showWallet(){
        return sharedPreference.getBoolean(SHOWWALLET,false);
    }

    public void setShowWallet(){
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putBoolean(SHOWWALLET,true);
        edit.apply();
    }

    public String getDriverNo() {
        return sharedPreference.getString(DRIVERNN, "");
    }


    public void setDriverNo(String driverNo) {
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString(DRIVERNN, driverNo);
        edit.apply();
    }

    public String getOrderNo(){
        return sharedPreference.getString(ORDERNN, "");
    }

    public void setOrderNo(String orderNo){
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString(ORDERNN,orderNo);
        edit.apply();
    }


    public String getMobile() {
        return sharedPreference.getString(MOBILE, "");
    }


    public void setMobile(String mobile) {
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString(MOBILE, mobile + "");
        edit.apply();
    }


    public String getPayAccount() {
        return sharedPreference.getString(PAYACCOUNT, "");
    }

    public void setPayAccount(String payAccount) {
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString(PAYACCOUNT, payAccount + "");
        edit.apply();
    }

    public String getDevToken() {
        return sharedPreference.getString(DEVTOKEN, "");
    }

    public void setDevtoken(String devToken) {
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString(DEVTOKEN, devToken);
        edit.apply();
    }

    public String getToken() {
        return sharedPreference.getString(LECHTOKEN, "");
    }

    public void setToken(String lechtoken) {
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString(LECHTOKEN, lechtoken + "");
        edit.apply();
    }

    public String getCarTeam() {
        return sharedPreference.getString(CAR_TEAM, "");
    }

    public void setCarTeam(String carTeam) {
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString(CAR_TEAM, carTeam + "");
        edit.apply();
    }


    public String getStartTime(){
        return sharedPreference.getString(START_TIME,"");
    }

    public void setStartTime(String startTime) {
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString(START_TIME, startTime + "");
        edit.apply();
    }

    public void clear() {
        setDriverNo("");
        setToken("");
    }

    public void remove(String s){
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.remove(s);
        edit.apply();
    }

    public void putLastLocation(String s){
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString(Constants.LAST_LOCATION, s);
        edit.apply();
    }

    public String getLastLocation(){
        return sharedPreference.getString(Constants.LAST_LOCATION,null);
    }

    public boolean contains(String s){
        return sharedPreference.contains(s);
    }

}
