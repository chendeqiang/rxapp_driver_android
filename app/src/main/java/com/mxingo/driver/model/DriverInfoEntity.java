package com.mxingo.driver.model;

import java.io.Serializable;

/**
 * Created by zhouwei on 2017/7/10.
 */

public class DriverInfoEntity implements Serializable{

    /**
     * rspCode : 00
     * driver : {"cdrivetype":0,"cloginid":"weishifu","cname":"魏师傅","corganizationid":"lechiche001","cpassword":"9ec97e6366c4339a71a08b8b805f378a","cphone":"13018806034","cstate":1,"cuuid":"saber001","islogin":0,"isstaff":0,"licenserank":100}
     * rspDesc : 成功
     */

    public String rspCode;
    public DriverEntity driver;
    public String rspDesc;
    /**
     * onlineStatus : 2
     */

    public int onlineStatus;
    /**
     * orgPhone : 13018806034
     */

    public String orgPhone;
    /**
     * pushStatus : 1
     * carLevel : 1
     */

    public int pushStatus;
    public int carLevel;
    /**
     * unforder : 0
     */

    public int unforder;


    public static class DriverEntity implements Serializable {
        /**
         * cdrivetype : 0
         * cloginid : weishifu
         * cname : 魏师傅
         * corganizationid : lechiche001
         * cpassword : 9ec97e6366c4339a71a08b8b805f378a
         * cphone : 13018806034
         * cstate : 1
         * cuuid : saber001
         * islogin : 0
         * isstaff : 0
         * licenserank : 100
         */

        public int cdrivetype;
        public String cloginid;
        public String cname;
        public String corganizationid;
        public String cpassword;
        public String cphone;
        public int cstate;
        public String cuuid;
        public int islogin;
        public int isstaff;
        public int licenserank;

        @Override
        public String toString() {
            return "{" +
                    "cdrivetype=" + cdrivetype +
                    ", cloginid='" + cloginid + '\'' +
                    ", cname='" + cname + '\'' +
                    ", corganizationid='" + corganizationid + '\'' +
                    ", cpassword='" + cpassword + '\'' +
                    ", cphone='" + cphone + '\'' +
                    ", cstate=" + cstate +
                    ", cuuid='" + cuuid + '\'' +
                    ", islogin=" + islogin +
                    ", isstaff=" + isstaff +
                    ", licenserank=" + licenserank +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", driver=" + driver +
                ", rspDesc='" + rspDesc + '\'' +
                ", onlineStatus=" + onlineStatus +
                ", orgPhone='" + orgPhone + '\'' +
                ", pushStatus=" + pushStatus +
                ", carLevel=" + carLevel +
                '}';
    }
}
