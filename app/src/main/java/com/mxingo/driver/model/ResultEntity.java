package com.mxingo.driver.model;

/**
 * Created by zhouwei on 2017/7/7.
 */

public class ResultEntity {

    /**
     * rspCode : 00
     * rspDesc : 成功
     */

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
