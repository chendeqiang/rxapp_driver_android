package com.mxingo.driver.module.base.http;


import com.mxingo.driver.module.base.data.UserInfoPreferences;
import com.mxingo.driver.utils.VersionInfo;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by zhouwei on 16/10/6.
 */

public class HeaderUtil {

    public static Map<String,String> getHeaders(Map<String,Object> map){
        Map<String, String> headers = new HashMap<>();
        headers.put(ApiConstants.token, UserInfoPreferences.getInstance().getToken());
        headers.put(ApiConstants.version, VersionInfo.getVersionName());
        headers.put(ApiConstants.sign, SignUtil.getSign(map,VersionInfo.getVersionName()));
        return headers;
    }
}
