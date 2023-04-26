package com.mxingo.driver.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by deqiangchen on 2023/3/20.
 */
public class ListCashEntity {

    /**
     * rspCode : 00
     * rspDesc : 成功
     * driverCash : [{"ccode":"202303161531034246","createtime":"2023-03-16 15:31:03","cuuid":"83201ceda47c4849b2","driveralino":"18721655801","driverid":"38d5ec67cee7451ebc","drivername":"姜楠楠","driverphone":"18721655801","flowno":"","paytime":"","price":"100.00","remark":"","state":0,"stateName":"处理中"}]
     */

    public String rspCode;
    public String rspDesc;
    public List<DriverCashBean> driverCash;

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", driverCash=" + driverCash +
                '}';
    }

    public static class DriverCashBean implements Serializable {
        /**
         * ccode : 202303161531034246
         * createtime : 2023-03-16 15:31:03
         * cuuid : 83201ceda47c4849b2
         * driveralino : 18721655801
         * driverid : 38d5ec67cee7451ebc
         * drivername : 姜楠楠
         * driverphone : 18721655801
         * flowno :
         * paytime :
         * price : 100.00
         * remark :
         * state : 0
         * stateName : 处理中
         */

        public String ccode;
        public String createtime;
        public String cuuid;
        public String driveralino;
        public String driverid;
        public String drivername;
        public String driverphone;
        public String flowno;
        public String paytime;
        public String price;
        public String remark;
        public int state;
        public String stateName;

        @Override
        public String toString() {
            return "{" +
                    "ccode='" + ccode + '\'' +
                    ", createtime='" + createtime + '\'' +
                    ", cuuid='" + cuuid + '\'' +
                    ", driveralino='" + driveralino + '\'' +
                    ", driverid='" + driverid + '\'' +
                    ", drivername='" + drivername + '\'' +
                    ", driverphone='" + driverphone + '\'' +
                    ", flowno='" + flowno + '\'' +
                    ", paytime='" + paytime + '\'' +
                    ", price='" + price + '\'' +
                    ", remark='" + remark + '\'' +
                    ", state=" + state +
                    ", stateName='" + stateName + '\'' +
                    '}';
        }
    }
}
