package com.mxingo.driver.model;

/**
 * Created by zhouwei on 2017/7/10.
 */

public class LatestMyQuoteEntity {

    /**
     * rspCode : 00
     * rspDesc : 成功
     * orderQuote : {"createTime":1499750600000,"curQuote":40000,"driverNo":"saber001","id":0,"modifyTime":1499750602000,"myQuote":41000,"orderNo":"201707106666"}
     */

    public String rspCode;
    public String rspDesc;
    public OrderQuoteEntity orderQuote;

    public static class OrderQuoteEntity {
        /**
         * createTime : 1499750600000
         * curQuote : 40000
         * driverNo : saber001
         * id : 0
         * modifyTime : 1499750602000
         * myQuote : 41000
         * orderNo : 201707106666
         */

        public long createTime;
        public int curQuote;
        public String driverNo;
        public int id;
        public long modifyTime;
        public int myQuote;
        public String orderNo;

        @Override
        public String toString() {
            return "{" +
                    "createTime=" + createTime +
                    ", curQuote=" + curQuote +
                    ", driverNo='" + driverNo + '\'' +
                    ", id=" + id +
                    ", modifyTime=" + modifyTime +
                    ", myQuote=" + myQuote +
                    ", orderNo='" + orderNo + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", orderQuote=" + orderQuote +
                '}';
    }
}
