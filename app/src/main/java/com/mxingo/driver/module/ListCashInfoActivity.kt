package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.model.ListCashEntity
import com.mxingo.driver.utils.Constants

/**
 * Created by deqiangchen on 2023/3/17.
 */
class ListCashInfoActivity:BaseActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var ivState: ImageView
    private lateinit var tvCashState: TextView
    private lateinit var tvName: TextView
    private lateinit var tvAlipay: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvMoney: TextView
    private lateinit var tvMsg: TextView

    lateinit var data: ListCashEntity.DriverCashBean


    companion object {
        @JvmStatic
        fun startListCashInfoActivity(context: Context, data: ListCashEntity.DriverCashBean) {
            context.startActivity(Intent(context, ListCashInfoActivity::class.java)
                    .putExtra(Constants.ACTIVITY_DATA,data))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_info)
        data = intent.getSerializableExtra(Constants.ACTIVITY_DATA) as ListCashEntity.DriverCashBean
        initView()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back_cash_info) as ImageView
        ivState = findViewById(R.id.iv_state) as ImageView
        tvCashState =findViewById(R.id.tv_cash_state) as TextView
        tvName =findViewById(R.id.tv_name) as TextView
        tvAlipay =findViewById(R.id.tv_alipay) as TextView
        tvMoney =findViewById(R.id.tv_money) as TextView
        tvMsg =findViewById(R.id.tv_msg) as TextView
        tvTime =findViewById(R.id.tv_time) as TextView

        ivBack.setOnClickListener {
            finish()
        }

        if (data.state.equals(0)){
            ivState.setImageResource(R.drawable.ic_waite)
        }else if (data.state.equals(1)){
            ivState.setImageResource(R.drawable.ic_finish)
        }else{
            ivState.setImageResource(R.drawable.ic_mistake)
        }
        tvCashState.text = data.stateName
        tvName.text =data.drivername.replaceRange(1,2,"*")
        tvAlipay.text = "支付宝("+data.driveralino.substring(0,3)+"****"+data.driveralino.substring(data.driveralino.length-4)+")"
        tvMoney.text =data.price
        tvTime.text =data.createtime
        tvMsg.text =data.flowno
    }

}