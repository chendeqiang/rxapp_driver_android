package com.mxingo.driver.model;

/**
 * Created by zhouwei on 2017/7/25.
 */

public class CheckVersionEntity {


    /**
     * rspCode : 00
     * rspDesc : 成功
     */

    public String rspCode;
    public String rspDesc;


    /**
     * data : {"createTime":1500869849000,"group":"rx_driver_app","id":1,"key":"rx_driver_android","keyDisplay":"任行约车Android更新","status":true,"value":"{\"versionCode\": 20170725,\"version\":\"1.0.2\",\"url\":\"http://pre.im/rxdriver\",\"log\":\"任行约车上线啦\",\"size\":\"10.2M\",\"forceUpdataVersions\": [1.0.0]}","valueDisplay":"20170724"}
     */

    public DataEntity data;

    public static class DataEntity {
        /**
         * createTime : 1500869849000
         * group : rx_driver_app
         * id : 1
         * key : rx_driver_android
         * keyDisplay : 任行约车Android更新
         * status : true
         * value : {"versionCode": 20170725,"version":"1.0.2","url":"http://pre.im/rxdriver","log":"任行约车上线啦","size":"10.2M","forceUpdataVersions": [1.0.0]}
         * valueDisplay : 20170724
         */

        public long createTime;
        public String group;
        public int id;
        public String key;
        public String keyDisplay;
        public boolean status;
        public String value;
        public String valueDisplay;

        @Override
        public String toString() {
            return "{" +
                    "createTime=" + createTime +
                    ", group='" + group + '\'' +
                    ", id=" + id +
                    ", key='" + key + '\'' +
                    ", keyDisplay='" + keyDisplay + '\'' +
                    ", status=" + status +
                    ", value='" + value + '\'' +
                    ", valueDisplay='" + valueDisplay + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", data=" + data +
                '}';
    }
}
