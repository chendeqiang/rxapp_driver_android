package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.model.WithdrawSettingEntity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * Created by deqiangchen on 2023/4/3.
 */
class WithdrawSettingActivity:BaseActivity()  {

    private lateinit var ivBack: ImageView
    private lateinit var tvDrawtime: TextView
    private lateinit var tvDrawcount: TextView
    private lateinit var tvLowestmoney: TextView
    private lateinit var tvTimefreeze: TextView
    @Inject
    lateinit var presenter: MyPresenter

    companion object {
        @JvmStatic
        fun startWithdrawSettingActivity(context: Context) {
            context.startActivity(Intent(context, WithdrawSettingActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_rule)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        initView()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back_wallet_rule) as ImageView
        tvDrawtime = findViewById(R.id.tv_drawtime) as TextView
        tvDrawcount =findViewById(R.id.tv_drawcount)as TextView
        tvLowestmoney =findViewById(R.id.tv_lowestmoney) as TextView
        tvTimefreeze =findViewById(R.id.tv_time_freeze) as TextView

        ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.withdrawSetting()
    }
    @Subscribe
    fun loadData(any: Any) {
        if (any::class == WithdrawSettingEntity::class){
            var data = any as WithdrawSettingEntity
            if (data.rspCode.equals("00")){
                initView2(data)
            }
        }
    }

    private fun initView2(data: WithdrawSettingEntity) {
        tvTimefreeze.text ="1、当日流水收入需要冻结"+data.freeze.toString()+"天之后可以进行提现。"
        tvDrawtime.text ="2、提现窗口期："+data.drawtimeDesc+","+data.starttime+"-"+data.endtime+"（节假日顺延）"
        tvDrawcount.text ="3、窗口期内提现次数："+data.drawcount.toString()+"次/天。"
        tvLowestmoney.text = "4、窗口期内单笔限额：最低"+data.lowestmoney+"元，最高"+data.biggestmoney+"元"
    }

}