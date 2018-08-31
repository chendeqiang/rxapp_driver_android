package com.mxingo.driver.model;

/**
 * Created by chendeqiang on 2018/1/29 13:51
 */

public class CurrentTimeEntity {

    /**
     * v : 2
     * now : 1517204893000
     */

    public String v;
    public long now;

    @Override
    public String toString() {
        return "{" +
                "v='" + v + '\'' +
                ", now=" + now +
                '}';
    }
}
