package com.mxingo.driver.model;

import java.io.Serializable;

/**
 * Created by zhouwei on 2017/7/10.
 */

public class OrderEntity implements Serializable {
    /**
     * bookTime : 1970-01-18 16:34:15
     * endAddr : 杭州智慧大厦
     * endLat : 30.2
     * endLon : 121.1
     * orderModel : 1
     * orderNo : 1234w5225211
     * orderStatus : 1
     * orderType : 1
     * orgId : lechiche001
     * passengerMobile : 18767126165
     * passengerName : 李shuo
     * planMileage : 0
     * remark : 备注
     * startAddr : 杭州火车东站
     * startLat : 30.1
     * startLon : 120.3
     * tripNo : G2342
     */

    public String bookTime;
    public String endAddr;
    public double endLat;
    public double endLon;
    public int orderModel;
    public String orderNo;
    public int orderStatus;
    public int orderType;
    public String orgId;
    public String passengerMobile;
    public String passengerName;
    public int planMileage;
    public String remark;
    public String startAddr;
    public double startLat;
    public double startLon;
    public String tripNo;

    /**
     * carLevel : 1
     * orderAmount : 5800
     */

    public int carLevel;
    public int orderAmount;


    /**

     * driverNo : saber001
     * orderMileage : 100
     * orderTime : 10000
     * planAmount : 1
     */
    public String driverNo;

    public int orderMileage;
    public long orderTime;
    public int planAmount;


    /**
     * arriveLat : 1
     * arriveLon : 1
     * arriveTime : 1499839509000
     * carNo : 浙A0001
     * driverNo : saber001
     * endLat : 30.2
     * endLon : 121.1
     * orderFinish` : 1499839539000
     * orderStartTime : 1499839522000
     * orderStopTime : 1499839525000
     * payAmount : 1
     * payType : 1
     */

    public double arriveLat;
    public double arriveLon;
    public long arriveTime;
//    public String arriveTime;
    public String carNo;
    public String orderFinishTime;
    public long orderStartTime;
    public long orderStopTime;
    public int payAmount;
    public int payType;
    public int source;

    public int orderQuote;
    public int bookDays;

    @Override
    public String toString() {
        return "{" +
                "bookTime='" + bookTime + '\'' +
                ", endAddr='" + endAddr + '\'' +
                ", endLat=" + endLat +
                ", endLon=" + endLon +
                ", orderModel=" + orderModel +
                ", orderNo='" + orderNo + '\'' +
                ", orderStatus=" + orderStatus +
                ", orderType=" + orderType +
                ", orgId='" + orgId + '\'' +
                ", passengerMobile='" + passengerMobile + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", planMileage=" + planMileage +
                ", remark='" + remark + '\'' +
                ", startAddr='" + startAddr + '\'' +
                ", startLat=" + startLat +
                ", startLon=" + startLon +
                ", tripNo='" + tripNo + '\'' +
                ", carLevel=" + carLevel +
                ", orderAmount=" + orderAmount +
                ", driverNo='" + driverNo + '\'' +
                ", orderMileage=" + orderMileage +
                ", orderTime=" + orderTime +
                ", planAmount=" + planAmount +
                ", arriveLat=" + arriveLat +
                ", arriveLon=" + arriveLon +
                ", arriveTime=" + arriveTime +
                ", carNo='" + carNo + '\'' +
                ", orderFinishTime=" + orderFinishTime +
                ", orderStartTime=" + orderStartTime +
                ", orderStopTime=" + orderStopTime +
                ", payAmount=" + payAmount +
                ", payType=" + payType +
                ", source=" + source +
                '}';
    }
}
