package com.mxingo.driver.model;

/**
 * Created by deqiangchen on 2023/3/31.
 */
public class WithdrawSettingEntity {


    /**
     * lowestmoney : 50
     * rspCode : 00
     * freeze : 7
     * drawtime : 4
     * drawtimeDesc : 周四
     * endtime : 19:00
     * starttime : 09:00
     * biggestmoney : 5000
     * drawcount : 3
     * rspDesc : 成功
     */

    public String lowestmoney;
    public String rspCode;
    public int freeze;
    public String drawtime;
    public String drawtimeDesc;
    public String endtime;
    public String starttime;
    public String biggestmoney;
    public int drawcount;
    public String rspDesc;

    @Override
    public String toString() {
        return "{" +
                "lowestmoney='" + lowestmoney + '\'' +
                ", rspCode='" + rspCode + '\'' +
                ", freeze=" + freeze +
                ", drawtime='" + drawtime + '\'' +
                ", drawtimeDesc='" + drawtimeDesc + '\'' +
                ", endtime='" + endtime + '\'' +
                ", starttime='" + starttime + '\'' +
                ", biggestmoney='" + biggestmoney + '\'' +
                ", drawcount=" + drawcount +
                ", rspDesc='" + rspDesc + '\'' +
                '}';
    }
}
