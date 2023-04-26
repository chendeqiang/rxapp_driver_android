package com.mxingo.driver.model;

import java.io.Serializable;

/**
 * Created by chendeqiang on 2017/11/14 13:50
 */

public class NoticeEntity implements Serializable{
    /**
     * ccode : 201711071549468072
     * ccontent : <p>< img src="http://localhost:8080/MyPlatform//ueditor/jsp/upload/image/20171107/1510040977828065347.png" title="1510040977828065347.png" alt="pg.png"/></p >
     * creciver :
     * creciver_name :
     * csender : 3
     * csendtime : 2017-11-07 15:49:46.0
     * csendtype : 0
     * cuuid : 7b9b856fe11742e1bd
     * isread : 1
     * title : app通知测试
     */
    public String ccode;
    public String ccontent;
    public String creciver;
    public String creciver_name;


    public String csender;
    public String csendtime;
    public String csendtype;
    public String cuuid;
    public String isread;
    public String title;

    @Override
    public String toString() {
        return "{" +
                "ccode='" + ccode + '\'' +
                ", ccontent='" + ccontent + '\'' +
                ", creciver='" + creciver + '\'' +
                ", creciver_name='" + creciver_name + '\'' +
                ", csender='" + csender + '\'' +
                ", csendtime='" + csendtime + '\'' +
                ", csendtype='" + csendtype + '\'' +
                ", cuuid='" + cuuid + '\'' +
                ", isread='" + isread + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
