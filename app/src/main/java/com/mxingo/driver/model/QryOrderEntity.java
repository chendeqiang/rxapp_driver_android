package com.mxingo.driver.model;

/**
 * Created by zhouwei on 2017/7/10.
 */

public class QryOrderEntity {

    /**
     * rspCode : 00
     * rspDesc : 成功
     * order : {"bookTime":1499655000,"endAddr":"杭州智慧大厦","endLat":30.2,"endLon":121.1,"orderModel":1,"orderNo":"1234w5225211","orderStatus":2,"orderType":1,"orgId":"lechiche001","passengerMobile":"18767126165","passengerName":"李shuo","planMileage":0,"remark":"备注","startAddr":"杭州火车东站","startLat":30.1,"startLon":120.3,"tripNo":"G2342"}
     * Conf_Reassignmentstatus: 0
     * Conf_Reassignmentstatus_Msg: 可改派时间。
     */

    public String rspCode;
    public String rspDesc;
    public OrderEntity order;
    public String Conf_Reassignmentstatus;
    public String Conf_Reassignmentstatus_Msg;

    /**
     * orderQuote : 50000
     * order : {"arriveLat":30.280255,"arriveLon":120.087743,"bookTime":1499677000,"carLevel":2,"driverNo":"saber001","endAddr":"西溪智慧大厦","endLat":30.241497,"endLon":120.443764,"orderAmount":50000,"orderModel":1,"orderNo":"1234567890test3","orderStatus":4,"orderType":3,"orgId":"lechiche001","passengerMobile":"13018806034","passengerName":"小木","planMileage":140,"remark":"带一个早餐2","startAddr":"萧山国际机场","startLat":30.241497,"startLon":120.443764,"tripNo":"MU123"}
     */

    public int orderQuote;

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ",Conf_Reassignmentstatus='" + Conf_Reassignmentstatus + '\'' +
                ",Conf_Reassignmentstatus_Msg='" + Conf_Reassignmentstatus_Msg + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", orderQuote='" + orderQuote + '\'' +
                ", order=" + order +
                '}';
    }

}
