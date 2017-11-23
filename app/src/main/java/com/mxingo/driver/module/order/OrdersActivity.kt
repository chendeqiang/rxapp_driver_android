package com.mxingo.driver.module.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.model.ListOrderEntity
import com.mxingo.driver.model.OrderEntity
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.take.TakeOrderActivity

import com.mxingo.driver.utils.Constants
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.OrderFooterView
import com.squareup.otto.Subscribe
import javax.inject.Inject

class OrdersActivity : BaseActivity(), AbsListView.OnScrollListener {

    private lateinit var lvOrders: ListView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var srlRefresh: SwipeRefreshLayout
    private lateinit var orderFooterView: OrderFooterView
    private lateinit var llEmpty: LinearLayout

    @Inject
    lateinit var presenter: MyPresenter

    private var pageIndex: Int = 0
    private var pageCount: Int = 20
    private lateinit var driverNo: String

    private lateinit var progress: MyProgress

    companion object {
        @JvmStatic
        fun startOrdersActivity(context: Context, driverNo: String) {
            context.startActivity(Intent(context, OrdersActivity::class.java).putExtra(Constants.DRIVER_NO, driverNo))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        driverNo = intent.getStringExtra(Constants.DRIVER_NO)
        ComponentHolder.appComponent!!.inject(this)
        progress = MyProgress(this)
        presenter.register(this)
        initView()
    }

    private fun initView() {
        lvOrders = findViewById(R.id.lv_orders) as ListView
        srlRefresh = findViewById(R.id.srl_refresh) as SwipeRefreshLayout

        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "车队订单"

        llEmpty = findViewById(R.id.ll_empty) as LinearLayout

        orderAdapter = OrderAdapter(this, arrayListOf())
        lvOrders.emptyView = llEmpty
        lvOrders.adapter = orderAdapter

        lvOrders.setOnItemClickListener { _, view, i, _ ->
            if (view != orderFooterView) {
                val data = orderAdapter.getItem(i)
                TakeOrderActivity.startTakeOrderActivity(this, data as OrderEntity, driverNo)
            }
        }

        srlRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorButtonBg))
        lvOrders.setOnScrollListener(this)

        orderFooterView = OrderFooterView(this)
        lvOrders.addFooterView(orderFooterView)
        lvOrders.setOnScrollListener(this)
        srlRefresh.setOnRefreshListener {
            srlRefresh.isRefreshing = false
            pageIndex = 0
            orderAdapter.clear()
            presenter.listOrder(driverNo, pageIndex, pageCount)
        }

    }

    override fun onStart() {
        super.onStart()
        pageIndex = 0
        orderAdapter.clear()
        progress.show()
        presenter.listOrder(driverNo, pageIndex, pageCount)
    }

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (totalItemCount == 0)
            return
        if (firstVisibleItem + visibleItemCount == totalItemCount) {
            val lastItemView = lvOrders.getChildAt(lvOrders.childCount - 1)
            if (lvOrders.bottom == lastItemView.bottom && orderFooterView.refresh) {
                pageIndex += pageCount
                presenter.listOrder(driverNo, pageIndex, pageCount)
            }
        }
    }

    override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == ListOrderEntity::class) {
            var listOrder: ListOrderEntity = any as ListOrderEntity
            if (listOrder.rspCode.equals("00")) {
                orderAdapter.addAll(listOrder.order)
                //动态设置listview底部view,当获取的数据大于或等于20条，orderFooterView.refresh的状态为true,底部显示刷新状态，相反为false，底部显示没有更多
                orderFooterView.refresh = listOrder.order.size >= pageCount
            }
            srlRefresh.isRefreshing = false
        }
        progress.dismiss()
    }
}
