package com.mxingo.driver.model;

import java.util.List;


public class ListOrderEntity {

    /**
     * rspCode : 00
     * rspDesc : 成功
     */

    public String rspCode;
    public String rspDesc;
    public List<OrderEntity> order;

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", order=" + order +
                '}';
    }
}
