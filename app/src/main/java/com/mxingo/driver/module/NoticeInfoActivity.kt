package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.widget.Toolbar
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.model.NoticeEntity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.widget.MyProgress
import java.net.URL
import javax.inject.Inject
import android.os.*
import android.util.Log


/**
 * 公告详情
 * Created by chendeqiang on 2017/11/14 13:43
 */
class NoticeInfoActivity : BaseActivity() {

    private lateinit var progress: MyProgress
    private lateinit var noticeEntity: NoticeEntity
    private lateinit var tvContant: TextView

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
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        noticeEntity = intent.getSerializableExtra(Constants.ACTIVITY_DATA) as NoticeEntity
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "公告详情"
        tvContant = findViewById(R.id.tv_contant) as TextView
        val html = "<html><body>" + noticeEntity.ccontent + "</body></html>"
        tvContant.setMovementMethod(ScrollingMovementMethod.getInstance())// 设置可滚动
        tvContant.setMovementMethod(LinkMovementMethod.getInstance())//设置超链接可以打开网页
        tvContant.setText(Html.fromHtml(html, imgGetter, null))
    }

    //这里面的resource就是fromhtml函数的第一个参数里面的含有的url
    internal var imgGetter: Html.ImageGetter = Html.ImageGetter { source ->
        Log.i("RG", "source---?>>>" + source)
        var drawable: Drawable? = null
        val url: URL
        try {
            url = URL(source)
            Log.i("RG", "url---?>>>" + url)
            drawable = Drawable.createFromStream(url.openStream(), "") // 获取网路图片
        } catch (e: Exception) {
            e.printStackTrace()
            return@ImageGetter null
        }

        drawable!!.setBounds(0, 0, drawable.intrinsicWidth,
                drawable.intrinsicHeight)
        Log.i("RG", "url---?>>>" + url)
        drawable
    }
}