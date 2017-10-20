package com.mxingo.driver.model;

/**
 * Created by zhouwei on 2017/7/12.
 */

public class TakeOrderEntity extends CommEntity {
    public String flowNo;

    @Override
    public String toString() {
        return "{" +
                "flowNo='" + flowNo + '\'' +
                '}';
    }
}
