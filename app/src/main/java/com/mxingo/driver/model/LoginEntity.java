package com.mxingo.driver.model;

/**
 * Created by zhouwei on 2017/7/10.
 */

public class LoginEntity {


    /**
     * no : saber001
     * rspCode : 00
     * rxToken : 7106
     * rspDesc : 成功
     */

    public String no;
    public String rspCode;
    public String rxToken;
    public String rspDesc;

    @Override
    public String toString() {
        return "{" +
                "no='" + no + '\'' +
                ", rspCode='" + rspCode + '\'' +
                ", rxToken='" + rxToken + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                '}';
    }
}
