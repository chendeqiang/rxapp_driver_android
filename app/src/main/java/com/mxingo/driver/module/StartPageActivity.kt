package com.mxingo.driver.module

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.mxingo.driver.MyApplication
import com.mxingo.driver.R
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.module.base.log.LogUtils

class StartPageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.d("classname", this::class.java.name)
        setContentView(R.layout.activity_start_page)
        addPermissions()
        MyApplication.bus.post(Any())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        grantResults.forEach {
            if (it != -1) {
                addPermissions()
                return
            }
        }
        LoginActivity.startLoginActivity(this)
        finish()
    }

    fun addPermissions() {

        val list = arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)
        list.filter {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }.map {
            list.remove(it)
        }

        if (list.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, list.toArray(Array<String>(list.size, { i -> i.toString() })), Constants.permissionMain)
        } else {
            if(MyApplication.isMainActivityLive){
                finish()
                return
            }
            Handler().postDelayed(Runnable {
                LoginActivity.startLoginActivity(this)
                finish()
            },4000)
        }
    }


}
