package com.mxingo.driver.model;

import java.util.List;

/**
 * Created by deqiangchen on 2023/3/16.
 */
public class WalletFundFlowEntity {


    /**
     * rspCode : 00
     * fundFlow : [{"category":"1","categoryName":"订单收入","createTime":"2023-03-13 09:00:00","createTimeHIS":"09:00:00","createTimeMD":"03-13","cuuid":"38d5ec67cee7451ebc","date":"2023-03-13 10:00:00","dateHIS":"10:00:00","dateMD":"03-13","driverPrice":"45.00","relationCode":"202303141017305687","relationId":"38d5ec67cee7451ebc","remark":"司机收入","sort":"2","sortName":"收入","source":"1"}]
     * rspDesc : 成功
     */

    public String rspCode;
    public String rspDesc;
    public List<FundFlowBean> fundFlow;

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", fundFlow=" + fundFlow +
                '}';
    }

    public static class FundFlowBean {
        /**
         * category : 1
         * categoryName : 订单收入
         * createTime : 2023-03-13 09:00:00
         * createTimeHIS : 09:00:00
         * createTimeMD : 03-13
         * cuuid : 38d5ec67cee7451ebc
         * date : 2023-03-13 10:00:00
         * dateHIS : 10:00:00
         * dateMD : 03-13
         * driverPrice : 45.00
         * relationCode : 202303141017305687
         * relationId : 38d5ec67cee7451ebc
         * remark : 司机收入
         * sort : 2
         * sortName : 收入
         * source : 1
         */

        public String category;
        public String categoryName;
        public String createTime;
        public String createTimeHIS;
        public String createTimeMD;
        public String cuuid;
        public String date;
        public String dateHIS;
        public String dateMD;
        public String driverPrice;
        public String relationCode;
        public String relationId;
        public String remark;
        public String sort;
        public String sortName;
        public String source;

        @Override
        public String toString() {
            return "{" +
                    "category='" + category + '\'' +
                    ", categoryName='" + categoryName + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", createTimeHIS='" + createTimeHIS + '\'' +
                    ", createTimeMD='" + createTimeMD + '\'' +
                    ", cuuid='" + cuuid + '\'' +
                    ", date='" + date + '\'' +
                    ", dateHIS='" + dateHIS + '\'' +
                    ", dateMD='" + dateMD + '\'' +
                    ", driverPrice='" + driverPrice + '\'' +
                    ", relationCode='" + relationCode + '\'' +
                    ", relationId='" + relationId + '\'' +
                    ", remark='" + remark + '\'' +
                    ", sort='" + sort + '\'' +
                    ", sortName='" + sortName + '\'' +
                    ", source='" + source + '\'' +
                    '}';
        }
    }
}
