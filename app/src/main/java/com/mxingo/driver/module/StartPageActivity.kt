package com.mxingo.driver.module

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import com.baidu.trace.LBSTraceClient
import com.mxingo.driver.MyApplication
import com.mxingo.driver.R
import com.mxingo.driver.module.base.data.UserInfoPreferences
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.module.base.log.LogUtils

class StartPageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //LogUtils.d("classname", this::class.java.name)
        setContentView(R.layout.activity_start_page)
        LBSTraceClient.setAgreePrivacy(applicationContext,false)
        if (UserInfoPreferences.getInstance().isFristStart){
            //第一次启动app，跳转隐私协议页面
            GuideActivity.startGuideActivity(this)
        }else if (MyApplication.isMainActivityLive){
            finish()
            return
        }else{
            LoginActivity.startLoginActivity(this)
        }
    }
}
