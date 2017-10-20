package com.mxingo.driver.module.base.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.mxingo.driver.MyApplication;


/**
 * Created by zhouwei on 16/1/7.
 */
public class UserInfoPreferences {
    private static UserInfoPreferences preference = null;
    private SharedPreferences sharedPreference;
    private String packageName = "";

    private final String MOBILE = "mobile"; //手机号
    private final String DEVTOKEN = "devToken";//cid
    private final String DRIVERNN = "driverNo";
    private final String LECHTOKEN = "token";
    private final String CAR_TEAM = "car_team";


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
        sharedPreference = context.getSharedPreferences(packageName, context.MODE_MULTI_PROCESS);
    }

    public String getDriverNo() {
        return sharedPreference.getString(DRIVERNN, "");
    }


    public void setDriverNo(String driverNo) {
        SharedPreferences.Editor edit = sharedPreference.edit();
        edit.putString(DRIVERNN, driverNo);
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

    public void clear() {
        setDriverNo("");
        setToken("");
    }

}
