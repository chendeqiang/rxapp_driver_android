package com.mxingo.driver.model;

import java.util.List;


public class ListDriverOrderEntity {


    /**
     * rspCode : 00
     * rspDesc : 成功
     * order : [{"bookTime":1499655000,"createTime":1499765140000,"driverNo":"saber001","endAddr":"杭州智慧大厦","flowNo":"2017071117254024342","flowStatus":2,"modifyTime":1499765140000,"orderNo":"1234567891","orderType":1,"startAddr":"杭州火车东站"},{"bookTime":1499655000,"createTime":1499759757000,"driverNo":"saber001","endAddr":"杭州智慧大厦","flowNo":"2017071115555713358","flowStatus":2,"modifyTime":1499759757000,"orderNo":"121291","orderType":1,"startAddr":"杭州火车东站"},{"bookTime":1499655000,"createTime":1499759519000,"driverNo":"saber001","endAddr":"杭州智慧大厦","flowNo":"2017071115515910099","flowStatus":2,"modifyTime":1499759519000,"orderNo":"1252291","orderType":1,"startAddr":"杭州火车东站"}]
     */

    public String rspCode;
    public String rspDesc;
    public List<OrderEntity> order;

    public static class OrderEntity {
        /**
         * bookTime : 1499655000
         * createTime : 1499765140000
         * driverNo : saber001
         * endAddr : 杭州智慧大厦
         * flowNo : 2017071117254024342
         * flowStatus : 2
         * modifyTime : 1499765140000
         * orderNo : 1234567891
         * orderType : 1
         * startAddr : 杭州火车东站
         */

        public long bookTime;
        public long createTime;
        public String driverNo;
        public String endAddr;
        public String flowNo;
        public int flowStatus;
        public long modifyTime;
        public String orderNo;
        public int orderType;
        public String startAddr;
        public int source;
        /**
         * carLevel : 1
         * orderAmount : 5800
         */

        public int carLevel;
        public int orderAmount;

        @Override
        public String toString() {
            return "{" +
                    "bookTime=" + bookTime +
                    ", createTime=" + createTime +
                    ", driverNo='" + driverNo + '\'' +
                    ", endAddr='" + endAddr + '\'' +
                    ", flowNo='" + flowNo + '\'' +
                    ", flowStatus=" + flowStatus +
                    ", modifyTime=" + modifyTime +
                    ", orderNo='" + orderNo + '\'' +
                    ", orderType=" + orderType +
                    ", startAddr='" + startAddr + '\'' +
                    ", carLevel=" + carLevel +
                    ", orderAmount=" + orderAmount +
                    ", source=" + source +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", order=" + order +
                '}';
    }
}
