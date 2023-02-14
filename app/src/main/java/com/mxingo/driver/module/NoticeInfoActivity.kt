package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.model.NoticeEntity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.widget.MyProgress
import javax.inject.Inject
import android.os.*
import android.webkit.WebView
import androidx.appcompat.widget.Toolbar


/**
 * 公告详情
 * Created by chendeqiang on 2017/11/14 13:43
 */
class NoticeInfoActivity : BaseActivity() {

    private lateinit var progress: MyProgress
    private lateinit var noticeEntity: NoticeEntity
    private lateinit var wv: WebView

    @Inject
    lateinit var presenter: MyPresenter

    companion object {
        @JvmStatic
        fun startNoticeInfoActivity(context: Context, notice: NoticeEntity) {
            val intent: Intent = Intent(context, NoticeInfoActivity::class.java)
                    .putExtra(Constants.ACTIVITY_DATA, notice)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_notice)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        noticeEntity = intent.getSerializableExtra(Constants.ACTIVITY_DATA) as NoticeEntity
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "公告详情"
//        wv = findViewById(R.id.web_noticeInfo) as WebView
//        wv.loadData(noticeEntity.ccontent, "text/html; charset=UTF-8", null)
//
//        val webSetting = wv.settings
//        webSetting.savePassword =false
//        webSetting.defaultTextEncodingName = "UTF -8"
//        webSetting.javaScriptEnabled = true
//        webSetting.setSupportZoom(false)
//        webSetting.domStorageEnabled = true
    }
}