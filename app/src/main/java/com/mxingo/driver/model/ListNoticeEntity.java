package com.mxingo.driver.model;

import java.util.List;

/**
 * Created by chendeqiang on 2017/7/10.
 */

public class ListNoticeEntity {
    /**
     * rspCode : 00
     * data : [{"ccode":"201711071549468072","ccontent":"<p>< img src=\"http://localhost:8080/MyPlatform//ueditor/jsp/upload/image/20171107/1510040977828065347.png\" title=\"1510040977828065347.png\" alt=\"pg.png\"/><\/p >","creciver":"","creciver_name":"","csender":"3","csendtime":"2017-11-07 15:49:46.0","csendtype":"0","cuuid":"7b9b856fe11742e1bd","isread":"1","title":"app通知测试"}]
     * rspDesc : 成功
     */

    public String rspCode;
    public String rspDesc;
    public List<NoticeEntity> data;

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", data=" + data +
                '}';
    }
}
