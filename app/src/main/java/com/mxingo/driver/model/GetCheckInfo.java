package com.mxingo.driver.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhouwei on 2017/10/18.
 */

public class GetCheckInfo {

    /**
     * rspCode : 00
     * carNo : 浙A10086
     * carBand : 奔驰GLC
     * rspDesc : 成功
     */

    public String rspCode;
    public String carNo;
    public String rspDesc;
    /**
     * carBrand : 奔驰GLC
     * driverCheckInfo : {"carBrand":"奔驰GLC","carLevel":5,"carNo":"浙A10086","createTime":1508314609000,"driverNo":"6279c7f4ad22412fb8","imgDriverlicense":"rxdriver/1508314607661.png","imgIdback":"rxdriver/1508314607162.png","imgIdface":"rxdriver/1508314606642.png","imgInsurance":"rxdriver/1508314608642.png","imgVehiclelicense":"rxdriver/1508314608132.png","mobile":"18767126193","name":"saber","status":0}
     */

    public String carBrand;
    public DriverCheckInfoEntity driverCheckInfo;

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", carNo='" + carNo + '\'' +
                ", carBand='" + carBrand + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", driverCheckInfo='" + driverCheckInfo + '\'' +
                '}';
    }


    public static class DriverCheckInfoEntity {
        /**
         * carBrand : 奔驰GLC
         * carLevel : 5
         * carNo : 浙A10086
         * createTime : 1508314609000
         * driverNo : 6279c7f4ad22412fb8
         * imgDriverlicense : rxdriver/1508314607661.png
         * imgIdback : rxdriver/1508314607162.png
         * imgIdface : rxdriver/1508314606642.png
         * imgInsurance : rxdriver/1508314608642.png
         * imgVehiclelicense : rxdriver/1508314608132.png
         * mobile : 18767126193
         * name : saber
         * status : 0
         */

        public String carBrand;
        public int carLevel;
        @SerializedName("carNo")
        public String carNoX;
        public long createTime;
        public String driverNo;
        public String imgDriverlicense;
        public String imgIdback;
        public String imgIdface;
        public String imgInsurance;
        public String imgVehiclelicense;
        public String img_wycdriver;
        public String img_wyccar;
        public String mobile;
        public String name;
        public int status;

        @Override
        public String toString() {
            return "{" +
                    "carBrand='" + carBrand + '\'' +
                    ", carLevel=" + carLevel +
                    ", carNoX='" + carNoX + '\'' +
                    ", createTime=" + createTime +
                    ", driverNo='" + driverNo + '\'' +
                    ", imgDriverlicense='" + imgDriverlicense + '\'' +
                    ", imgIdback='" + imgIdback + '\'' +
                    ", imgIdface='" + imgIdface + '\'' +
                    ", imgInsurance='" + imgInsurance + '\'' +
                    ", imgVehiclelicense='" + imgVehiclelicense + '\'' +
                    ", img_wycdriver='" + img_wycdriver + '\'' +
                    ", img_wyccar='" + img_wyccar + '\'' +
                    ", mobile='" + mobile + '\'' +
                    ", name='" + name + '\'' +
                    ", status=" + status +
                    '}';
        }
    }
}
