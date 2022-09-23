package com.mxingo.driver.model;

public class OrderCancelEntity {

    /**
     * title : 任行出行
     * msg : 订单取消行程
     * order : {"bookDays":0,"bookTime":"2020-05-31 10:40:00","carLevel":2,"carNo":"苏E90789","driverNo":"77a553bc301d48deb3","endAddr":"戴斯酒店(银川市-金凤区-亲水南大街与湖畔路交叉口)","endLat":38.452076,"endLon":106.21979,"orderAmount":6110,"orderModel":1,"orderNo":"202005291424315468","orderStatus":2,"orderType":1,"orgId":"6f9ee5fd25bd44d59d","passengerMobile":"18695288614","passengerName":"万镇","payAmount":0,"planMileage":33735,"source":1,"startAddr":"河东机场T3","startLat":38.326207,"startLon":106.39393,"tripNo":"9C6273","usrId":0}
     * pushType : 10001
     */

    public String title;
    public String msg;
    public OrderBean order;
    public String pushType;

    @Override
    public String toString() {
        return "{" +
                "title='" + title + '\'' +
                ", msg='" + msg + '\'' +
                ", order=" + order +
                ", pushType='" + pushType + '\'' +
                '}';
    }

    public static class OrderBean {
        /**
         * bookDays : 0
         * bookTime : 2020-05-31 10:40:00
         * carLevel : 2
         * carNo : 苏E90789
         * driverNo : 77a553bc301d48deb3
         * endAddr : 戴斯酒店(银川市-金凤区-亲水南大街与湖畔路交叉口)
         * endLat : 38.452076
         * endLon : 106.21979
         * orderAmount : 6110
         * orderModel : 1
         * orderNo : 202005291424315468
         * orderStatus : 2
         * orderType : 1
         * orgId : 6f9ee5fd25bd44d59d
         * passengerMobile : 18695288614
         * passengerName : 万镇
         * payAmount : 0
         * planMileage : 33735
         * source : 1
         * startAddr : 河东机场T3
         * startLat : 38.326207
         * startLon : 106.39393
         * tripNo : 9C6273
         * usrId : 0
         */

        public int bookDays;
        public String bookTime;
        public int carLevel;
        public String carNo;
        public String driverNo;
        public String endAddr;
        public double endLat;
        public double endLon;
        public int orderAmount;
        public int orderModel;
        public String orderNo;
        public int orderStatus;
        public int orderType;
        public String orgId;
        public String passengerMobile;
        public String passengerName;
        public int payAmount;
        public int planMileage;
        public int source;
        public String startAddr;
        public double startLat;
        public double startLon;
        public String tripNo;
        public int usrId;

        @Override
        public String toString() {
            return "{" +
                    "bookDays=" + bookDays +
                    ", bookTime='" + bookTime + '\'' +
                    ", carLevel=" + carLevel +
                    ", carNo='" + carNo + '\'' +
                    ", driverNo='" + driverNo + '\'' +
                    ", endAddr='" + endAddr + '\'' +
                    ", endLat=" + endLat +
                    ", endLon=" + endLon +
                    ", orderAmount=" + orderAmount +
                    ", orderModel=" + orderModel +
                    ", orderNo='" + orderNo + '\'' +
                    ", orderStatus=" + orderStatus +
                    ", orderType=" + orderType +
                    ", orgId='" + orgId + '\'' +
                    ", passengerMobile='" + passengerMobile + '\'' +
                    ", passengerName='" + passengerName + '\'' +
                    ", payAmount=" + payAmount +
                    ", planMileage=" + planMileage +
                    ", source=" + source +
                    ", startAddr='" + startAddr + '\'' +
                    ", startLat=" + startLat +
                    ", startLon=" + startLon +
                    ", tripNo='" + tripNo + '\'' +
                    ", usrId=" + usrId +
                    '}';
        }
    }
}
