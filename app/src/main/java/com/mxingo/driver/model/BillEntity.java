package com.mxingo.driver.model;

import java.io.Serializable;

/**
 * Created by chendeqiang on 2017/11/21 16:48
 */

public class BillEntity implements Serializable {
    public long bookTime;
    public int carLevel;
    public String carNo;
    public int cmoney;
    public long createTime;
    public String driverNo;
    public String endAddr;
    public String flowNo;
    public int flowStatus;
    public long modifyTime;
    public String orderNo;
    public int orderType;
    public int source;
    public String startAddr;

    @Override
    public String toString() {
        return "{" +
                "bookTime=" + bookTime +
                ", carLevel=" + carLevel +
                ", carNo='" + carNo + '\'' +
                ", cmoney=" + cmoney +
                ", createTime=" + createTime +
                ", driverNo='" + driverNo + '\'' +
                ", endAddr='" + endAddr + '\'' +
                ", flowNo='" + flowNo + '\'' +
                ", flowStatus=" + flowStatus +
                ", modifyTime=" + modifyTime +
                ", orderNo='" + orderNo + '\'' +
                ", orderType=" + orderType +
                ", source=" + source +
                ", startAddr='" + startAddr + '\'' +
                '}';
    }
}
