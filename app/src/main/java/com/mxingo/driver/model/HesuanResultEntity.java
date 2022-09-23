package com.mxingo.driver.model;

public class HesuanResultEntity {

    /**
     * rspCode : 2006
     * rspDesc : 司机资料已更新
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
