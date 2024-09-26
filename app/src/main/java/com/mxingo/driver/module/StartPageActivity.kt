package com.mxingo.driver.module

import android.os.Bundle
import com.mxingo.driver.MyApplication
import com.mxingo.driver.R
import com.mxingo.driver.module.base.data.UserInfoPreferences

class StartPageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //LogUtils.d("classname", this::class.java.name)
        setContentView(R.layout.activity_start_page)
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
