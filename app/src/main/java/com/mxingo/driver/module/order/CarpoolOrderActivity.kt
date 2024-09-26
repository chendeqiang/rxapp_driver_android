package com.mxingo.driver.module.order

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
import com.google.android.material.tabs.TabLayout
import com.mxingo.driver.R
import com.mxingo.driver.model.ListCarPoolOrderEntity
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.module.LoginActivity
import com.mxingo.driver.module.base.data.UserInfoPreferences
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.OrderFooterView
import com.mxingo.driver.widget.ShowToast
import com.squareup.otto.Subscribe
import javax.inject.Inject

class CarpoolOrderActivity:BaseActivity(), AbsListView.OnScrollListener, TabLayout.OnTabSelectedListener {

    private lateinit var srlRefresh: SwipeRefreshLayout
    private lateinit var lvOrders: ListView
    private lateinit var llEmpty: LinearLayout
    private lateinit var tabOrder: TabLayout
    private lateinit var adapter:CarPoolOrderAdapter
    private lateinit var orderFooterView: OrderFooterView

    @Inject
    lateinit var presenter: MyPresenter
    lateinit var driverNo: String
    private var status: Int = 0
    private var pageIndex: Int = 0
    private var pageCount: Int = 20
    private lateinit var progress: MyProgress

    companion object {
        @JvmStatic
        fun startCarpoolOrderActivity(context: Context, driverNo: String) {
            context.startActivity(Intent(context, CarpoolOrderActivity::class.java).putExtra(Constants.DRIVER_NO, driverNo))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carpool_order)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        driverNo = intent.getStringExtra(Constants.DRIVER_NO) as String
        initView()
    }

    private fun initView() {
        lvOrders = findViewById(R.id.lv_orders) as ListView
        srlRefresh = findViewById(R.id.srl_refresh) as SwipeRefreshLayout

        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "拼车订单"

        llEmpty = findViewById(R.id.ll_empty) as LinearLayout
        lvOrders.emptyView = llEmpty

        tabOrder = findViewById(R.id.tab_cp_order) as TabLayout

        adapter= CarPoolOrderAdapter(this, arrayListOf())
        lvOrders.adapter =adapter
        lvOrders.setOnItemClickListener { _, view, i, _ ->
                if (view!=orderFooterView){
                    var data = adapter.getItem(i) as ListCarPoolOrderEntity.DataBean
                    if (data.status==0||data.status==1){
                        CarPoolOrderInfoActivity.startCarPoolOrderInfoActivity(this,data.cuuid,data.ccode)
                    }else if (data.status==2){
                        //跳转订单完成详情页面
                        CarPoolInfoActivity.startCarPoolInfoActivity(this,data.cuuid)
                    }
                }
        }

        orderFooterView = OrderFooterView(this)
        lvOrders.addFooterView(orderFooterView)
        lvOrders.setOnScrollListener(this)
        srlRefresh.setOnRefreshListener {
            pageIndex = 0
            adapter.clear()
            progress.show()
            presenter.listCarPoolOrder(driverNo, status, pageIndex, pageCount)
        }
        srlRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorButtonBg))

        tabOrder.addOnTabSelectedListener(this)
    }

    override fun onStart() {
        super.onStart()
        pageIndex = 0
        adapter.clear()
        progress.show()
        presenter.listCarPoolOrder(driverNo, status, pageIndex, pageCount)
    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == ListCarPoolOrderEntity::class) {
            var data = any as ListCarPoolOrderEntity
            if (data.rspCode.equals("00")) {
                adapter.addAll(data.data)
                orderFooterView.refresh = data.data.size >= pageCount
            } else if (data.rspCode.equals("101")) {
                ShowToast.showCenter(this, "TOKEN失效，请重新登陆")
                UserInfoPreferences.getInstance().clear()
                LoginActivity.startLoginActivity(this)
                finish()
            }
            srlRefresh.isRefreshing = false
        }
        progress.dismiss()

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (totalItemCount == 0)
            return
        if (firstVisibleItem + visibleItemCount == totalItemCount) {
            val lastItemView = lvOrders.getChildAt(lvOrders.childCount - 1)
            if (lvOrders.bottom == lastItemView.bottom) {
                if (orderFooterView.refresh) {
                    pageIndex += pageCount
                    presenter.listCarPoolOrder(driverNo, status, pageIndex, pageCount)
                }
            }
        }
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        status = tab!!.position
        pageIndex = 0
        adapter.clear()
        progress.show()
        presenter.listCarPoolOrder(driverNo,status,pageIndex,pageCount)
    }
}