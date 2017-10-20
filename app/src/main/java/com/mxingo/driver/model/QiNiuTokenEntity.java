package com.mxingo.driver.model;

/**
 * Created by zhouwei on 2017/10/18.
 */

public class QiNiuTokenEntity {


    /**
     * qiniuToken : IMI5m6gNALUrFOnSkPMlHa0vqDCXjaNN9ve8xuiT:ZqJMQRMyRu4C1IsNGXPyit0sRbA=:eyJzY29wZSI6InJ4ZHJpdmVyIiwiZGVhZGxpbmUiOjE1MDgzMTQzMDF9
     * rspCode : 00
     * rspDesc : 成功
     */

    public String qiniuToken;
    public String rspCode;
    public String rspDesc;
    public int index;

    @Override
    public String toString() {
        return "{" +
                "qiniuToken='" + qiniuToken + '\'' +
                ", rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                '}';
    }
}
