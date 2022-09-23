package com.mxingo.driver.module

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import com.mxingo.driver.MyApplication
import com.mxingo.driver.R
import com.mxingo.driver.module.base.data.UserInfoPreferences
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.module.base.log.LogUtils

class StartPageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d("classname", this::class.java.name)
        setContentView(R.layout.activity_start_page)
        if (UserInfoPreferences.getInstance().isFristStart){
            //第一次启动app，跳转隐私协议页面
            GuideActivity.startGuideActivity(this)
        }else if (MyApplication.isMainActivityLive){
            finish()
            return
        }else{
            addPermissions()
        }
        MyApplication.bus.post(Any())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        grantResults.forEach {
            if (it != -1) {
                addPermissions()
                return
            }else{
                LoginActivity.startLoginActivity(this)
                finish()
            }
        }
    }

    fun addPermissions() {

        val list = arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)
        list.filter {
            checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED//去掉集合中已授权的
        }.map {
            list.remove(it)
        }

        if (list.isNotEmpty()) {//集合中剩下的是没授权的，接着动态申请
            LogUtils.d("---------",list.isEmpty().toString())
            ActivityCompat.requestPermissions(this, list.toArray(Array<String>(list.size, { i -> i.toString() })), Constants.permissionMain)
        } else {
//            if(MyApplication.isMainActivityLive){
//                finish()
//                return
//            }
            Handler().postDelayed({
                LoginActivity.startLoginActivity(this)
                finish()
            },2000)
        }
    }
}
