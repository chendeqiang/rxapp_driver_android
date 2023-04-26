package com.mxingo.driver.module.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import com.mxingo.driver.R
import com.mxingo.driver.model.ListDriverOrderEntity
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.module.LoginActivity
import com.mxingo.driver.module.base.data.UserInfoPreferences
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter

import com.mxingo.driver.utils.Constants
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.MyTagButton
import com.mxingo.driver.widget.OrderFooterView
import com.mxingo.driver.widget.ShowToast
import com.squareup.otto.Subscribe
import javax.inject.Inject

class MyOrderActivity : BaseActivity(), View.OnClickListener, AbsListView.OnScrollListener, TabLayout.OnTabSelectedListener {

    private lateinit var lvOrders: ListView
    private lateinit var adapter: MyOrderAdapter
    private lateinit var tagButtons: Array<MyTagButton>
    private lateinit var orderFooterView: OrderFooterView
    private lateinit var srlRefresh: SwipeRefreshLayout
    private lateinit var llEmpty: LinearLayout
    private lateinit var hsvOrderType: HorizontalScrollView
    private lateinit var tabOrder: TabLayout

    @Inject
    lateinit var presenter: MyPresenter
    lateinit var driverNo: String
    private var flowStatus: Int = 1
    private var orderType: Int = 0
    private var pageIndex: Int = 0
    private var pageCount: Int = 20


    private lateinit var progress: MyProgress

    companion object {
        @JvmStatic
        fun startMyOrderActivity(context: Context, driverNo: String) {
            context.startActivity(Intent(context, MyOrderActivity::class.java).putExtra(Constants.DRIVER_NO, driverNo))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order)
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
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "我的订单"

        llEmpty = findViewById(R.id.ll_empty) as LinearLayout
        hsvOrderType = findViewById(R.id.hsv_order_type) as HorizontalScrollView

        lvOrders.emptyView = llEmpty

        tabOrder = findViewById(R.id.tab_order) as TabLayout

        tagButtons = arrayOf(findViewById(R.id.btn_all) as MyTagButton,
                findViewById(R.id.btn_take_plane) as MyTagButton,
                findViewById(R.id.btn_send_plane) as MyTagButton,
                findViewById(R.id.btn_take_train) as MyTagButton,
                findViewById(R.id.btn_send_train) as MyTagButton,
                findViewById(R.id.btn_day_renter) as MyTagButton,
                findViewById(R.id.btn_d_d) as MyTagButton)

        adapter = MyOrderAdapter(this, arrayListOf())
        lvOrders.adapter = adapter
        lvOrders.setOnItemClickListener { _, view, i, _ ->
            if (view != orderFooterView) {
                var data = adapter.getItem(i) as ListDriverOrderEntity.OrderEntity
                if (data.flowStatus == FlowStatus.ARRIVE_TYPE) {
//                    presenter.getCurrentTime()
                    MapActivity.startMapActivity(this, data.orderNo, data.flowNo, driverNo)
                } else {
                    OrderInfoActivity.startOrderInfoActivity(this, data.orderNo, data.flowNo, driverNo)
                }
            }
        }

        tagButtons.forEach {
            it.setOnClickListener(this)
        }

        orderFooterView = OrderFooterView(this)
        lvOrders.addFooterView(orderFooterView)
        lvOrders.setOnScrollListener(this)
        srlRefresh.setOnRefreshListener {
            pageIndex = 0
            adapter.clear()
            progress.show()
            presenter.listDriverOrder(driverNo, flowStatus, orderType, pageIndex, pageCount)
        }
        srlRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorButtonBg))

        tabOrder.addOnTabSelectedListener(this)
        tagButtons[0].isSelected = true
    }

    override fun onClick(view: View?) {
        tagButtons.forEach {
            it.isSelected = false
            if (it == view) {
                orderType = tagButtons.indexOf(view)
            }
        }
        if (view != null) {
            (view as MyTagButton).isSelected = true
        }

        pageIndex = 0
        adapter.clear()
        progress.show()
        if (orderType==6){
            presenter.listDriverOrder(driverNo, flowStatus, 100, pageIndex, pageCount)
        }else{
            presenter.listDriverOrder(driverNo, flowStatus, orderType, pageIndex, pageCount)
        }
    }

    override fun onStart() {
        super.onStart()
        pageIndex = 0
        adapter.clear()
        progress.show()
        presenter.listDriverOrder(driverNo, flowStatus, orderType, pageIndex, pageCount)
    }

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (totalItemCount == 0)
            return
        if (firstVisibleItem + visibleItemCount == totalItemCount) {
            val lastItemView = lvOrders.getChildAt(lvOrders.childCount - 1)
            if (lvOrders.bottom == lastItemView.bottom) {
                if (orderFooterView.refresh) {
                    pageIndex += pageCount
                    presenter.listDriverOrder(driverNo, flowStatus, orderType, pageIndex, pageCount)
                }
            }
        }
    }

    override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        flowStatus = tab!!.position + 1
        pageIndex = 0
        adapter.clear()
        onClick(tagButtons[0])
    }


    @Subscribe
    fun loadData(any: Any) {
        if (any::class == ListDriverOrderEntity::class) {
            var data = any as ListDriverOrderEntity
            if (data.rspCode.equals("00")) {
                adapter.addAll(data.order)
                orderFooterView.refresh = data.order.size >= pageCount
            } else if (data.rspCode.equals("101")) {
                ShowToast.showCenter(this, "TOKEN失效，请重新登陆")
                UserInfoPreferences.getInstance().clear()
//                MyModulePreference.getInstance().driverNo=""
//                MyModulePreference.getInstance().token=""
                LoginActivity.startLoginActivity(this)
                finish()
            }
            srlRefresh.isRefreshing = false
        }
//        else if (any::class == CurrentTimeEntity::class) {
//            val data = any as CurrentTimeEntity
//            val curTime = TimeUtil.getDateToString(data.now)
//            UserInfoPreferences.getInstance().startTime = curTime
//        }
        progress.dismiss()

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }
}

