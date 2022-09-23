package com.mxingo.driver.model;

import java.util.List;

public class ListCarPoolOrderEntity {

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", data=" + data +
                '}';
    }

    /**
     * rspCode : 00
     * data : [{"allprice":684,"carnum":5,"ccode":"202208291119083634","ccreater":"31","ccreatime":"2022-08-29 11:19:08","cendarea":"闵行区","cendcity":"上海市","cstartarea":"虎丘区","cstartcity":"苏州市","cstarttime":"2022-07-26 15:43:00","cupdater":"31","cupdatime":"2022-08-29 11:19:33","cuuid":"4967ab778fe8414590","driverid":"d37579d60cc6403789","drivername":"李亚","num":4,"orgid":"ffcf2166b02642e4bf","orgname":"汽车人","platenumber":"苏E98765","status":0}]
     * rspDesc : 成功
     */

    public String rspCode;
    public String rspDesc;
    public List<DataBean> data;



    public static class DataBean {
        /**
         * allprice : 684
         * carnum : 5
         * ccode : 202208291119083634
         * ccreater : 31
         * ccreatime : 2022-08-29 11:19:08
         * cendarea : 闵行区
         * cendcity : 上海市
         * cstartarea : 虎丘区
         * cstartcity : 苏州市
         * cstarttime : 2022-07-26 15:43:00
         * cupdater : 31
         * cupdatime : 2022-08-29 11:19:33
         * cuuid : 4967ab778fe8414590
         * driverid : d37579d60cc6403789
         * drivername : 李亚
         * num : 4
         * orgid : ffcf2166b02642e4bf
         * orgname : 汽车人
         * platenumber : 苏E98765
         * status : 0
         */

        public double allprice;
        public int carnum;
        public String ccode;
        public String ccreater;
        public String ccreatime;
        public String cendarea;
        public String cendcity;
        public String cstartarea;
        public String cstartcity;
        public String cstarttime;
        public String cupdater;
        public String cupdatime;

        @Override
        public String toString() {
            return "{" +
                    "allprice=" + allprice +
                    ", carnum=" + carnum +
                    ", ccode='" + ccode + '\'' +
                    ", ccreater='" + ccreater + '\'' +
                    ", ccreatime='" + ccreatime + '\'' +
                    ", cendarea='" + cendarea + '\'' +
                    ", cendcity='" + cendcity + '\'' +
                    ", cstartarea='" + cstartarea + '\'' +
                    ", cstartcity='" + cstartcity + '\'' +
                    ", cstarttime='" + cstarttime + '\'' +
                    ", cupdater='" + cupdater + '\'' +
                    ", cupdatime='" + cupdatime + '\'' +
                    ", cuuid='" + cuuid + '\'' +
                    ", driverid='" + driverid + '\'' +
                    ", drivername='" + drivername + '\'' +
                    ", num=" + num +
                    ", orgid='" + orgid + '\'' +
                    ", orgname='" + orgname + '\'' +
                    ", platenumber='" + platenumber + '\'' +
                    ", status=" + status +
                    '}';
        }

        public String cuuid;
        public String driverid;
        public String drivername;
        public int num;
        public String orgid;
        public String orgname;
        public String platenumber;
        public int status;


    }
}
