package com.mxingo.driver.model;

/**
 * Created by deqiangchen on 2023/3/16.
 */
public class WalletEntity {

    /**
     * rspCode : 00
     * careerMoney : 100000.89
     * stateDesc : 正常
     * canPickMoney : 10000.68
     * remainingSum : 30001.57
     * recordMoney : 20000.89
     * driverName : 姜楠楠
     * state : 0
     * isDraw : 1
     * driverNo : 38d5ec67cee7451ebc
     * rspDesc : 成功
     */

    public String rspCode;
    public String careerMoney;
    public String stateDesc;
    public String canPickMoney;
    public String remainingSum;
    public String recordMoney;
    public String driverName;
    public int state;
    public int isDraw;
    public String driverNo;
    public String rspDesc;

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", careerMoney='" + careerMoney + '\'' +
                ", stateDesc='" + stateDesc + '\'' +
                ", canPickMoney='" + canPickMoney + '\'' +
                ", remainingSum='" + remainingSum + '\'' +
                ", recordMoney='" + recordMoney + '\'' +
                ", driverName='" + driverName + '\'' +
                ", state=" + state +
                ", isDraw=" + isDraw +
                ", driverNo='" + driverNo + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                '}';
    }
}
