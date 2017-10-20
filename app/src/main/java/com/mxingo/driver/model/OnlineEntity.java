package com.mxingo.driver.model;

/**
 * Created by zhouwei on 2017/7/10.
 */

public class OnlineEntity {
    public String rspCode;
    public String rspDesc;

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                '}';
    }
}
