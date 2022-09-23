package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.mxingo.driver.R

/**
 * Created by chendeqiang on 2018/4/17 17:53
 */
class CarRegisterActivity : BaseActivity() {

    private lateinit var btnSubmitReg:Button

    companion object {
        const val START_CAMERA = 101
        const val PERMISSION_CAMERA = 101
        const val PERMISSION_ALBUM = 102
        const val START_ALBUM = 102
        const val CROP = 100
        const val REQUEST_IMAGE = 1000

        @JvmStatic
        fun startCarRegisterActivity(context: Context) {
            context.startActivity(Intent(context, CarRegisterActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_register)
        initView()
    }


    private fun initView() {
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "车辆注册信息"

        btnSubmitReg.setOnClickListener {
        }
    }
}