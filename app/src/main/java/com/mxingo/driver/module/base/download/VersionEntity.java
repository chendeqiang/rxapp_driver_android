package com.mxingo.driver.module.base.download;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhouwei on 2017/7/24.
 */

public class VersionEntity implements Serializable {


    /**
     * versionCode : 20161220
     * version : 1.0.0
     * url : http://openbox.mobilem.360.cn/index/d/sid/3155356
     * log : 任行约车上线啦
     * size : 27033
     */

    public int versionCode;
    public String version;
    public String url;
    public String log;
    public String size;
    public List<String>forceUpdataVersions;
    public boolean isMustUpdate;

    @Override
    public String toString() {
        return "{" +
                "versionCode=" + versionCode +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                ", log='" + log + '\'' +
                ", size='" + size + '\'' +
                ", forceUpdataVersions=" + forceUpdataVersions +
                ", isMustUpdate=" + isMustUpdate +
                '}';
    }
}
