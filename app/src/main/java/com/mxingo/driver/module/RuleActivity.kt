package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.mxingo.driver.R

/**
 * Created by chendeqiang on 2017/11/23 14:59
 */
class RuleActivity : BaseActivity() {

    companion object {
        @JvmStatic
        fun startRuleActivity(context: Context) {
            context.startActivity(Intent(context, RuleActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rule)
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "规则说明"
    }
}