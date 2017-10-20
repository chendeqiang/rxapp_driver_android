package com.mxingo.driver.module;

/**
 * Created by zhouwei on 2017/10/18.
 */

public enum DriverCheckInfoStatus {
    UNSUBMIT(-1, "未认证"),UNFINISHED(0, "待审核"), FINISHED(1, "已认证"), CANTPASS(2, "未通过");

    public final int status;

    public final String desc;

    DriverCheckInfoStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static String getDesc(int status) {
        String desc = "";
        for (DriverCheckInfoStatus data : values()) {
            if (status == data.status) {
                desc = data.desc;
            }
        }
        return desc;
    }
}
