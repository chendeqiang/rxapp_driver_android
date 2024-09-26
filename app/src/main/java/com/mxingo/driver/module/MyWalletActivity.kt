package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode
import com.github.gzuliyujiang.wheelpicker.contract.OnDatePickedListener
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.impl.UnitDateFormatter
import com.github.gzuliyujiang.wheelpicker.widget.DateWheelLayout
import com.google.android.material.tabs.TabLayout
import com.mxingo.driver.R
import com.mxingo.driver.dialog.MessageDialog3
import com.mxingo.driver.model.WalletEntity
import com.mxingo.driver.model.WalletFundFlowEntity
import com.mxingo.driver.module.base.data.UserInfoPreferences
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.order.FundFlowAdapter
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.utils.TimeUtil
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.OrderFooterView
import com.mxingo.driver.widget.ShowToast
import com.squareup.otto.Subscribe
import javax.inject.Inject


/**
 * Created by deqiangchen on 2023/3/6.
 * 我的钱包
 */
class MyWalletActivity:BaseActivity(), TabLayout.OnTabSelectedListener, AbsListView.OnScrollListener {

    private lateinit var wheelLayout: DateWheelLayout
    private lateinit var ivBack: ImageView
    private lateinit var ivQuestion: ImageView
    private lateinit var ivIntro: ImageView
    private lateinit var tvWithdrawCash: TextView
    private lateinit var btnWithdrawCash1: Button
    private lateinit var tvCount: TextView
    private lateinit var tvMoneyCanWithdraw: TextView
    private lateinit var tvMoneyWait: TextView
    private lateinit var birthPicker: DatePicker
    private lateinit var lvRecords: ListView
    private lateinit var adapter: FundFlowAdapter
    private lateinit var srlRefresh: SwipeRefreshLayout
    private lateinit var orderFooterView: OrderFooterView
    private lateinit var llEmpty: LinearLayout
    private lateinit var llDateSelect: LinearLayout
    private lateinit var tvDateSelect: TextView
    private lateinit var tabStatementRecord: TabLayout

    private lateinit var datas:WalletEntity

    private var sort: Int = 1
    private var pageIndex: Int = 0
    private var pageCount: Int = 20
    private lateinit var progress: MyProgress

    @Inject
    lateinit var presenter: MyPresenter
    lateinit var driverNo: String

    companion object {
        @JvmStatic
        fun startMyWalletActivity(context: Context, driverNo: String) {
            context.startActivity(Intent(context, MyWalletActivity::class.java).putExtra(Constants.DRIVER_NO, driverNo))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_wallet)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        driverNo = intent.getStringExtra(Constants.DRIVER_NO)!!
        initView()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back) as ImageView
        ivQuestion =findViewById(R.id.iv_question) as ImageView
        ivIntro =findViewById(R.id.iv_intro) as ImageView
        btnWithdrawCash1 =findViewById(R.id.btn_withdraw_cash_1) as Button
        lvRecords = findViewById(R.id.lv_statement_record) as ListView
        srlRefresh = findViewById(R.id.srl_refresh_record) as SwipeRefreshLayout
        llEmpty = findViewById(R.id.ll_empty_record) as LinearLayout
        llDateSelect = findViewById(R.id.ll_date_select) as LinearLayout
        tabStatementRecord = findViewById(R.id.tab_statement_record) as TabLayout
        tvDateSelect =findViewById(R.id.tv_date_select) as TextView
        tvCount = findViewById(R.id.tv_count) as TextView
        tvMoneyCanWithdraw =findViewById(R.id.tv_money_can_withdraw) as TextView
        tvMoneyWait =findViewById(R.id.tv_money_wait) as TextView

        adapter = FundFlowAdapter(this, arrayListOf())
        lvRecords.adapter =adapter
        lvRecords.emptyView =llEmpty
        orderFooterView = OrderFooterView(this)
        lvRecords.addFooterView(orderFooterView)
        lvRecords.setOnScrollListener(this)

        birthPicker = DatePicker(this)
        birthPicker.setBodyWidth(240)
        wheelLayout=birthPicker.wheelLayout
        wheelLayout.setDateMode(DateMode.YEAR_MONTH)
        wheelLayout.setDateFormatter(UnitDateFormatter())
        wheelLayout.setRange(DateEntity.target(2024, 1, 1), DateEntity.target(2035, 12, 31), DateEntity.today())
        wheelLayout.setCurtainEnabled(true)
        wheelLayout.setCurtainColor(0x33CCFF)
        wheelLayout.setIndicatorEnabled(true)
        wheelLayout.setIndicatorColor(0x33CCFF)
        wheelLayout.setIndicatorSize(getResources().getDisplayMetrics().density * 2)
        wheelLayout.setTextColor(0x33CCFF)
        wheelLayout.getYearLabelView().setTextColor(0x33CCFF)
        wheelLayout.getMonthLabelView().setTextColor(0x33CCFF)
        wheelLayout.setResetWhenLinkage(false);

        tvDateSelect.text = TimeUtil.getNowTime().substring(0,4)+"年"+TimeUtil.getNowTime().substring(5,6)+"月"
        tabStatementRecord.setSelectedTabIndicatorHeight(0)
        tabStatementRecord.addOnTabSelectedListener(this)
        llDateSelect.setOnClickListener {
            birthPicker.show()
        }


        birthPicker.setOnDatePickedListener(OnDatePickedListener { year, month, day ->
            tvDateSelect.text = "$year"+"年"+"$month"+"月"
            pageIndex = 0
            adapter.clear()
            progress.show()
            presenter.listFundFlow(driverNo,sort.toString(),TimeUtil.getWalletTime(tvDateSelect.text.toString(),"年","月"),pageIndex,pageCount)        })

        ivBack.setOnClickListener {
            finish()
        }
        tvWithdrawCash = findViewById(R.id.tv_withdraw_cash) as TextView

        //提现
        btnWithdrawCash1.setOnClickListener {
            if (datas.canPickMoney.equals("0.00")){
                ShowToast.showCenter(this,"可提现余额不足，无法提现！")
            }else if (datas.state.equals(1)){
                ShowToast.showCenter(this,"钱包被冻结，无法提现！")
            }else if(datas.isDraw.equals("1")){
                ShowToast.showCenter(this,"非提现窗口期，无法提现！")
            }else if (UserInfoPreferences.getInstance().payAccount.isNullOrEmpty()||UserInfoPreferences.getInstance().payAccount.length<5){
                BindAlipayActivity.startBindAlipayActivity(this,driverNo)
            }else{
                WithdrawActivity.startWithdrawActivity(this,driverNo,datas.isDraw.toString(),datas.canPickMoney)
            }
        }

        //提现账户
        tvWithdrawCash.setOnClickListener {
            if(UserInfoPreferences.getInstance().payAccount.isNullOrEmpty()){
                BindAlipayActivity.startBindAlipayActivity(this,driverNo)//绑定支付宝
            }else{
                WithdrawalAccountActivity.startWithdrawalAccountActivity(this,driverNo)
            }
        }

        ivQuestion.setOnClickListener {
            val dialog = MessageDialog3(this)
            dialog.setOnOkClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
        ivIntro.setOnClickListener {
            WithdrawSettingActivity.startWithdrawSettingActivity(this)
        }

        srlRefresh.setOnRefreshListener {
            pageIndex = 0
            adapter.clear()
            progress.show()
            presenter.listFundFlow(driverNo,sort.toString(),TimeUtil.getWalletTime(tvDateSelect.text.toString(),"年","月"),pageIndex,pageCount)        }

        lvRecords.setOnItemClickListener { _, view, i, _ ->
            if (view!=orderFooterView){
                var data = adapter.getItem(i) as WalletFundFlowEntity.FundFlowBean
                //跳转详情页面
                FundFlowInfoActivity.startFundFlowInfoActivity(this,data.category,data.driverPrice,data.relationCode,data.categoryName,data.createTime,data.remark)
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        sort= tab!!.position+1
        pageIndex = 0
        adapter.clear()
        //请求网络
        presenter.listFundFlow(driverNo,sort.toString(),TimeUtil.getWalletTime(tvDateSelect.text.toString(),"年","月"),pageIndex,pageCount)    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onStart() {
        super.onStart()
        pageIndex = 0
        adapter.clear()
        progress.show()
        presenter.getWalletInfo(driverNo)
        presenter.listFundFlow(driverNo,sort.toString(),TimeUtil.getWalletTime(tvDateSelect.text.toString(),"年","月"),pageIndex,pageCount)

    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == WalletEntity::class){
            datas = any as WalletEntity
            if (datas.rspCode.equals("00")){
                updateView(datas)
            }
        }else if (any::class == WalletFundFlowEntity::class){
            var data = any as WalletFundFlowEntity
            if (data.rspCode.equals("00")){
                adapter.addAll(data.fundFlow)
                orderFooterView.refresh = data.fundFlow.size >= pageCount
            }
            srlRefresh.isRefreshing = false
        }
        progress.dismiss()
    }

    private fun updateView(data: WalletEntity) {
        tvCount.text = data.remainingSum
        tvMoneyCanWithdraw.text =data.canPickMoney
        tvMoneyWait.text =data.recordMoney
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
                    presenter.listFundFlow(driverNo,sort.toString(),TimeUtil.getWalletTime(tvDateSelect.text.toString(),"年","月"),pageIndex,pageCount)                }
            }
        }
    }
}