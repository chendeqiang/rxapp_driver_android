package com.mxingo.driver.module

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.WindowManager
import com.mxingo.driver.MyApplication
import com.mxingo.driver.R
import com.mxingo.driver.module.base.http.HttpUtil
import com.mxingo.driver.module.take.MainActivity

import com.umeng.analytics.MobclickAgent


@Suppress("DEPRECATED_IDENTITY_EQUALS")
open class BaseActivity : AppCompatActivity() {


    fun setToolbar(toolbar: Toolbar) {
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_whilte_arrow)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        HttpUtil.checkNetwork(this)
        if (this::class.java.equals(StartPageActivity::class.java)) {
            MobclickAgent.onPageStart(this.packageName)
        }
        MobclickAgent.onResume(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        MyApplication.currActivity = this::class.java.name
    }

    override fun onPause() {
        super.onPause()
        if (this::class.java.equals(StartPageActivity::class.java)) {
            MobclickAgent.onPageEnd(this.packageName)
        }
        MobclickAgent.onPause(this)
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::class.java.name.equals(MainActivity::class.java.name) && MyApplication.currActivity.equals(MainActivity::class.java.name)) {
            MyApplication.currActivity = ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }
}
