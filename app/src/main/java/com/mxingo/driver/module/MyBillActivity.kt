package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import com.mxingo.driver.R
import com.mxingo.driver.model.BillEntity
import com.mxingo.driver.model.ListBillEntity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.order.OrderInfoActivity
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.OrderFooterView
import com.mxingo.driver.widget.ShowToast
import com.squareup.otto.Subscribe
import javax.inject.Inject


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * Created by chendeqiang on 2017/11/21 14:06
 */
class MyBillActivity : BaseActivity(), TabLayout.OnTabSelectedListener {


    private lateinit var lvOrders: ListView
    private lateinit var srlRefresh: SwipeRefreshLayout
    private lateinit var llEmpty: LinearLayout
    private lateinit var tabBill: TabLayout
    private lateinit var orderFooterView: OrderFooterView
    private lateinit var adapter: MyBillAdapter
    private lateinit var ivBack: ImageView
    private lateinit var ivRule: ImageView
    @Inject
    lateinit var presenter: MyPresenter
    lateinit var driverNo: String
    private var pageIndex: Int = 0
    private var pageCount: Int = 20
    private var tabAction: Int = 1

    private lateinit var progress: MyProgress

    companion object {
        @JvmStatic
        fun startMyOrderActivity(context: Context, driverNo: String) {
            context.startActivity(Intent(context, MyBillActivity::class.java).putExtra(Constants.DRIVER_NO, driverNo))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bill)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        driverNo = intent.getStringExtra(Constants.DRIVER_NO)!!
        initView()
    }

    private fun initView() {
        lvOrders = findViewById(R.id.lv_orders_bill) as ListView
        srlRefresh = findViewById(R.id.srl_refresh) as SwipeRefreshLayout

        ivBack = findViewById(R.id.iv_back_bill) as ImageView
        ivBack.setOnClickListener {
            finish()
        }
        ivRule = findViewById(R.id.iv_rule_bill) as ImageView
        ivRule.setOnClickListener {
            RuleActivity.startRuleActivity(this)
        }

        llEmpty = findViewById(R.id.ll_empty) as LinearLayout
        lvOrders.emptyView = llEmpty
        tabBill = findViewById(R.id.tab_bill) as TabLayout
        adapter = MyBillAdapter(this)
        orderFooterView = OrderFooterView(this)
        lvOrders.emptyView = llEmpty
        lvOrders.adapter = adapter
        lvOrders.addFooterView(orderFooterView, null, false)
        tabBill.addOnTabSelectedListener(this)
        srlRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorButtonBg))
        srlRefresh.setOnRefreshListener {
            pageIndex = 0
            adapter.clear()
            presenter.listBill(driverNo, pageIndex, pageCount)
        }

        lvOrders.setOnItemClickListener { _, view, i, _ ->
            if (view != orderFooterView) {
                var data = adapter.getItem(i) as BillEntity
                OrderInfoActivity.startOrderInfoActivity(this, data.orderNo, data.flowNo, driverNo)
            }
        }

        lvOrders.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (totalItemCount == 0)
                    return
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    val lastItemView = lvOrders.getChildAt(lvOrders.childCount - 1)
                    if (lvOrders.bottom == lastItemView.bottom) {
                        if (orderFooterView.refresh) {
                            pageIndex += pageCount
                            presenter.listBill(driverNo, pageIndex, pageCount)
                        }
                    }
                }
            }

            override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
            }

        })
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tabAction = tab!!.position + 1
        adapter.clear()
        pageIndex = 0
        presenter.listBill(driverNo, pageIndex, pageCount)
    }

    override fun onStart() {
        super.onStart()
        pageIndex = 0
        adapter.clear()
        progress.show()
        presenter.listBill(driverNo, pageIndex, pageCount)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }

    @Subscribe
    fun loadData(listBill: ListBillEntity) {
        progress.dismiss()
        srlRefresh.isRefreshing = false
        if (listBill.rspCode == "00") {
            if (tabAction == 1) {
                adapter.addAll(listBill.finishDriverOrderBill)
                orderFooterView.refresh = listBill.finishDriverOrderBill.size >= pageCount
            }
            if (tabAction == 2) {
                adapter.addAll(listBill.rewards)
                orderFooterView.refresh = listBill.rewards.size >= pageCount
            }
            if (tabAction == 3) {
                adapter.addAll(listBill.fine)
                orderFooterView.refresh = listBill.fine.size >= pageCount
            }
            if (tabAction == 4) {
                adapter.addAll(listBill.facePay)
                orderFooterView.refresh = listBill.facePay.size >= pageCount
            }
//            orderFooterView.refresh = listBill.finishDriverOrderBill.size >= pageCount
        } else {
            ShowToast.showCenter(this, listBill.rspDesc)
        }
    }
}