package com.mxingo.driver.model;

/**
 * Created by zhouwei on 2017/7/10.
 */

public class PushOrderEntity {

    /**
     * title : 任行出行
     * msg : 派单/报价->杭州火车东站
     * order : {"bookTime":"1970-01-18 16:34:15","endAddr":"杭州智慧大厦","endLat":30.2,"endLon":121.1,"orderModel":1,"orderNo":"1234w5225211","orderStatus":1,"orderType":1,"orgId":"lechiche001","passengerMobile":"18767126165","passengerName":"李shuo","planMileage":0,"remark":"备注","startAddr":"杭州火车东站","startLat":30.1,"startLon":120.3,"tripNo":"G2342"}
     * taskId : 2017071014380139013
     * pushType : 10000
     */

    public String title;
    public String msg;
    public OrderEntity order;
    public String taskId;
    public String pushType;



    @Override
    public String toString() {
        return "{" +
                "title='" + title + '\'' +
                ", msg='" + msg + '\'' +
                ", order=" + order +
                ", taskId='" + taskId + '\'' +
                ", pushType='" + pushType + '\'' +
                '}';
    }
}
