package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.mxingo.driver.R
import com.mxingo.driver.model.DriverInfoEntity
import com.mxingo.driver.utils.Constants

class AntiepidemicActivity:BaseActivity() {
    private lateinit var tvReportHs: TextView
    private lateinit var tvReportJkm: TextView
    private lateinit var driverInfo: DriverInfoEntity

    companion object {

        @JvmStatic
        fun startAntiepidemicActivity(context: Context, info: DriverInfoEntity) {
            context.startActivity(Intent(context, AntiepidemicActivity::class.java).putExtra(Constants.DRIVER_INFO, info))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_antiepidemic_report)
        initView()
    }

    private fun initView() {
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "防疫上报"
        driverInfo = intent.getSerializableExtra(Constants.DRIVER_INFO) as DriverInfoEntity

        tvReportHs =findViewById(R.id.tv_report_hs) as TextView
        tvReportJkm =findViewById(R.id.tv_report_jkm) as TextView

        tvReportHs.setOnClickListener {
            HesuanReportActivity.startHesuanReportActivity(this,driverInfo)
        }

        tvReportJkm.setOnClickListener {
            JkmReportActivity.startJkmReportActivity(this,driverInfo)
        }
    }
}