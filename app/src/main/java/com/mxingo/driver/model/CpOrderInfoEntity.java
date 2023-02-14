package com.mxingo.driver.model;

import java.util.List;

public class CpOrderInfoEntity {

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", orders=" + orders +
                '}';
    }

    /**
     * rspCode : 00
     * orders : [{"ccode":"202207221558391111","cdate":"2022-07-24 15:43:00","cendaddress":"东川路800号上海交通大学闵行校区","cendarea":"闵行区","cendcity":"上海市","cmainid":"4967ab778fe8414590","cname":"东方不败","cphone":"15251707110","cstartaddress":"华山路95号高新区人民医院","cstartarea":"虎丘区","cstartcity":"苏州市","cstarttime":"2022-07-26 15:43:00","cuuid":"bc33b934f1594b90be","driverid":"d37579d60cc6403789","drivername":"李亚","driverprice":342,"elat":"31.032487","elng":"121.440358","num":2,"ordermodel":1,"orderprice":300,"orderstatus":1,"ordertype":0,"orgid":"ffcf2166b02642e4bf","orgname":"汽车人","orgprice":0.1,"paytype":1,"phone":"15696164096","platenumber":"苏E98765","pname":"风清扬","remark":"尽快安排","renxingprice":380,"slat":"31.326892","slng":"120.610422","tccode":"202207221558191031"},{"ccode":"202207221518314334","cdate":"2022-07-25 16:43:00","cendaddress":"昆阳路3959号上海市国家检察官学院2","cendarea":"闵行区","cendcity":"上海市","cmainid":"4967ab778fe8414590","cname":"老三","cphone":"13390887239","cstartaddress":"华山路95号高新区人民医院2","cstartarea":"虎丘区","cstartcity":"苏州市","cstarttime":"2022-07-26 16:30:00","cuuid":"bc33b934f1594c97be","driverid":"d37579d60cc6403789","drivername":"李亚","driverprice":342,"elat":"31.039047","elng":"121.385166","num":2,"ordermodel":1,"orderprice":300,"orderstatus":1,"ordertype":0,"orgid":"ffcf2166b02642e4bf","orgname":"汽车人","orgprice":0.1,"paytype":1,"phone":"13902163381","platenumber":"苏E98765","pname":"岳老二","remark":"尽快安排","renxingprice":380,"slat":"31.326892","slng":"120.550422","tccode":"202207221658293333"}]
     * rspDesc : 成功
     */

    public String rspCode;
    public String rspDesc;
    public List<OrdersBean> orders;


    public static class OrdersBean {
        /**
         * ccode : 202207221558391111
         * cdate : 2022-07-24 15:43:00
         * cendaddress : 东川路800号上海交通大学闵行校区
         * cendarea : 闵行区
         * cendcity : 上海市
         * cmainid : 4967ab778fe8414590
         * cname : 东方不败
         * cphone : 15251707110
         * cstartaddress : 华山路95号高新区人民医院
         * cstartarea : 虎丘区
         * cstartcity : 苏州市
         * cstarttime : 2022-07-26 15:43:00
         * cuuid : bc33b934f1594b90be
         * driverid : d37579d60cc6403789
         * drivername : 李亚
         * driverprice : 342
         * elat : 31.032487
         * elng : 121.440358
         * num : 2
         * ordermodel : 1
         * orderprice : 300
         * orderstatus : 1
         * ordertype : 0
         * orgid : ffcf2166b02642e4bf
         * orgname : 汽车人
         * orgprice : 0.1
         * paytype : 1
         * phone : 15696164096
         * platenumber : 苏E98765
         * pname : 风清扬
         * remark : 尽快安排
         * renxingprice : 380
         * slat : 31.326892
         * slng : 120.610422
         * tccode : 202207221558191031
         */

        public String ccode;

        @Override
        public String toString() {
            return "{" +
                    "ccode='" + ccode + '\'' +
                    ", cdate='" + cdate + '\'' +
                    ", cendaddress='" + cendaddress + '\'' +
                    ", cendarea='" + cendarea + '\'' +
                    ", cendcity='" + cendcity + '\'' +
                    ", cmainid='" + cmainid + '\'' +
                    ", cname='" + cname + '\'' +
                    ", cphone='" + cphone + '\'' +
                    ", cstartaddress='" + cstartaddress + '\'' +
                    ", cstartarea='" + cstartarea + '\'' +
                    ", cstartcity='" + cstartcity + '\'' +
                    ", cstarttime='" + cstarttime + '\'' +
                    ", cuuid='" + cuuid + '\'' +
                    ", driverid='" + driverid + '\'' +
                    ", drivername='" + drivername + '\'' +
                    ", driverprice=" + driverprice +
                    ", elat='" + elat + '\'' +
                    ", elng='" + elng + '\'' +
                    ", num=" + num +
                    ", ordermodel=" + ordermodel +
                    ", orderprice=" + orderprice +
                    ", orderstatus=" + orderstatus +
                    ", ordertype=" + ordertype +
                    ", orgid='" + orgid + '\'' +
                    ", orgname='" + orgname + '\'' +
                    ", orgprice=" + orgprice +
                    ", paytype=" + paytype +
                    ", phone='" + phone + '\'' +
                    ", platenumber='" + platenumber + '\'' +
                    ", pname='" + pname + '\'' +
                    ", remark='" + remark + '\'' +
                    ", renxingprice=" + renxingprice +
                    ", slat='" + slat + '\'' +
                    ", slng='" + slng + '\'' +
                    ", tccode='" + tccode + '\'' +
                    '}';
        }

        public String cdate;
        public String cendaddress;
        public String cendarea;
        public String cendcity;
        public String cmainid;
        public String cname;
        public String cphone;
        public String cstartaddress;
        public String cstartarea;
        public String cstartcity;
        public String cstarttime;
        public String cuuid;
        public String driverid;
        public String drivername;
        public double driverprice;
        public String elat;
        public String elng;
        public int num;
        public int ordermodel;
        public double orderprice;
        public int orderstatus;
        public int ordertype;
        public String orgid;
        public String orgname;
        public double orgprice;
        public int paytype;
        public String phone;
        public String platenumber;
        public String pname;
        public String remark;
        public double renxingprice;
        public String slat;
        public String slng;
        public String tccode;
    }
}
