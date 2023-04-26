package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.qqtheme.framework.picker.DatePicker
import com.mxingo.driver.R
import com.mxingo.driver.model.ListCashEntity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.order.ListCashAdapter
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.utils.TimeUtil
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.OrderFooterView
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * Created by deqiangchen on 2023/3/8.
 */
class WithdrawRecordActivity:BaseActivity(), AbsListView.OnScrollListener {
    private lateinit var birthPicker: DatePicker
    private lateinit var lvRecords: ListView
    private lateinit var srlRefresh: SwipeRefreshLayout
    private lateinit var orderFooterView: OrderFooterView
    private lateinit var llEmpty: LinearLayout
    private lateinit var llDateSelect: LinearLayout
    private lateinit var tvDateSelect: TextView
    private lateinit var adapter:ListCashAdapter

    @Inject
    lateinit var presenter: MyPresenter

    private var pageIndex: Int = 0
    private var pageCount: Int = 20
    private lateinit var driverNo: String

    private lateinit var progress: MyProgress
    companion object {
        @JvmStatic
        fun startWithdrawRecordActivity(context: Context, driverNo: String) {
            context.startActivity(Intent(context, WithdrawRecordActivity::class.java).putExtra(Constants.DRIVER_NO, driverNo))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw_record)
        driverNo = intent.getStringExtra(Constants.DRIVER_NO) as String
        ComponentHolder.appComponent!!.inject(this)
        progress = MyProgress(this)
        presenter.register(this)
        initView()
    }

    private fun initView() {
        lvRecords = findViewById(R.id.lv_withdraw_record) as ListView
        srlRefresh = findViewById(R.id.srl_refresh_record) as SwipeRefreshLayout
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "提现记录"
        llEmpty = findViewById(R.id.ll_empty_record) as LinearLayout
        llDateSelect = findViewById(R.id.ll_date_select) as LinearLayout
        tvDateSelect =findViewById(R.id.tv_date_select) as TextView

        adapter = ListCashAdapter(this, arrayListOf())
        lvRecords.adapter =adapter
        lvRecords.emptyView =llEmpty
        orderFooterView = OrderFooterView(this)
        lvRecords.addFooterView(orderFooterView)
        lvRecords.setOnScrollListener(this)
        birthPicker = DatePicker(this,1)
        birthPicker.setTitleText("请选择日期")
        birthPicker.setCanceledOnTouchOutside(true)
        birthPicker.setRangeEnd(2028, 12)
        birthPicker.setRangeStart(2023, 1)
        birthPicker.setSelectedItem(TimeUtil.getNowTime().substring(0,4).toInt(), TimeUtil.getNowTime().substring(5,7).toInt())
        tvDateSelect.text = TimeUtil.getNowTime().substring(0,4)+"年"+TimeUtil.getNowTime().substring(5,7)+"月"
        birthPicker.setResetWhileWheel(false)

        llDateSelect.setOnClickListener {
            birthPicker.show()
        }

        birthPicker.setOnDatePickListener(DatePicker.OnYearMonthPickListener {
            year, month -> tvDateSelect.text = "$year"+"年"+"$month"+"月"
            pageIndex = 0
            adapter.clear()
            progress.show()
            presenter.listCash(driverNo, tvDateSelect.text.toString().substring(0,4)+"-"+tvDateSelect.text.toString().substring(5,7), pageIndex, pageCount)
        })

        srlRefresh.setOnRefreshListener {
            pageIndex = 0
            adapter.clear()
            progress.show()
            presenter.listCash(driverNo, tvDateSelect.text.toString().substring(0,4)+"-"+tvDateSelect.text.toString().substring(5,7), pageIndex, pageCount)
        }

        lvRecords.setOnItemClickListener { _, view, i, _ ->
            if (view!=orderFooterView){
                var data = adapter.getItem(i) as ListCashEntity.DriverCashBean
                ListCashInfoActivity.startListCashInfoActivity(this,data)
            }
        }

    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == ListCashEntity::class){
            var data = any as ListCashEntity
            if (data.rspCode.equals("00")){
                adapter.addAll(data.driverCash)
                orderFooterView.refresh = data.driverCash.size >= pageCount
            }
            srlRefresh.isRefreshing = false
        }
        progress.dismiss()
    }

    override fun onStart() {
        super.onStart()
        pageIndex = 0
        adapter.clear()
        progress.show()
        presenter.listCash(driverNo,tvDateSelect.text.toString().substring(0,4)+"-"+tvDateSelect.text.toString().substring(5,7),pageIndex,pageCount)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

    }

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (totalItemCount == 0)
            return
        if (firstVisibleItem + visibleItemCount == totalItemCount) {
            val lastItemView = lvRecords.getChildAt(lvRecords.childCount - 1)
            if (lvRecords.bottom == lastItemView.bottom) {
                if (orderFooterView.refresh) {
                    pageIndex += pageCount
                    presenter.listCash(driverNo, tvDateSelect.text.toString().substring(0,4)+"-"+tvDateSelect.text.toString().substring(5,7), pageIndex, pageCount)
                }
            }
        }
    }
}