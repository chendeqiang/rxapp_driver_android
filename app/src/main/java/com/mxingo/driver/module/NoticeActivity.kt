package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mxingo.driver.R
import com.mxingo.driver.model.ListNoticeEntity
import com.mxingo.driver.model.NoticeEntity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.base.log.LogUtils
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.OrderFooterView
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * 公告列表
 * Created by chendeqiang on 2017/11/6 16:28
 */
class NoticeActivity : BaseActivity(), AbsListView.OnScrollListener {

    private lateinit var lvNotice: ListView
    private lateinit var srlRefresh: SwipeRefreshLayout
    private lateinit var noticeAdapter: NoticeAdapter
    private lateinit var noticeFooterView: OrderFooterView
    private lateinit var llEmpty: LinearLayout

    @Inject
    lateinit var presenter: MyPresenter
    private var pageIndex: Int = 0
    private var pageCount: Int = 20
    private lateinit var progress: MyProgress

    companion object {
        @JvmStatic
        fun startNoticeActivity(context: Context) {
            val intent = Intent(context, NoticeActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
        ComponentHolder.appComponent!!.inject(this)
        progress = MyProgress(this)
        presenter.register(this)
        initView()
    }

    private fun initView() {
        lvNotice = findViewById(R.id.lv_notice) as ListView
        srlRefresh = findViewById(R.id.srl_refresh) as SwipeRefreshLayout
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "公告"

        llEmpty = findViewById(R.id.ll_empty) as LinearLayout
        noticeAdapter = NoticeAdapter(this, arrayListOf())
        lvNotice.emptyView = llEmpty
        lvNotice.adapter = noticeAdapter

        lvNotice.setOnItemClickListener { _, view, i, _ ->
            if (view != noticeFooterView) {
                val data = noticeAdapter.getItem(i)
                NoticeInfoActivity.startNoticeInfoActivity(this, data as NoticeEntity)
                LogUtils.d("notice", data.toString())
            }
        }
        srlRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorButtonBg))
        lvNotice.setOnScrollListener(this)

        noticeFooterView = OrderFooterView(this)
        lvNotice.addFooterView(noticeFooterView)
        srlRefresh.setOnRefreshListener {
            srlRefresh.isRefreshing = false
            pageIndex = 0
            noticeAdapter.clear()
            presenter.listNotice(pageIndex, pageCount)
        }
    }

    override fun onStart() {
        super.onStart()
        pageIndex = 0
        noticeAdapter.clear()
        progress.show()
        presenter.listNotice(pageIndex, pageCount)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (totalItemCount == 0)
            return
        if (firstVisibleItem + visibleItemCount == totalItemCount) {
            val lastItemView = lvNotice.getChildAt(lvNotice.childCount - 1)
            if (lvNotice.bottom == lastItemView.bottom && noticeFooterView.refresh) {
                pageIndex += pageCount
                presenter.listNotice(pageIndex, pageCount)
            }
        }
    }

    override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == ListNoticeEntity::class) {
            var listNotice: ListNoticeEntity = any as ListNoticeEntity
            if (listNotice.rspCode.equals("00")) {
                noticeAdapter.addAll(listNotice.data)
                noticeFooterView.refresh = listNotice.data.size >= pageCount
            }
            srlRefresh.isRefreshing = false
        }
        progress.dismiss()
    }
}