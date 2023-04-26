package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.utils.Constants

/**
 * Created by deqiangchen on 2023/3/17.
 */
class FundFlowInfoActivity:BaseActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var tvType: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvMoney: TextView
    private lateinit var tvMsg: TextView
    private lateinit var tvRemarkInfo: TextView

    lateinit var fundFlowCreateTime: String
    lateinit var category: String
    lateinit var driverPrice: String
    lateinit var relationCode: String
    lateinit var categoryName: String
    lateinit var remark: String


    companion object {
        @JvmStatic
        fun startFundFlowInfoActivity(context: Context, category: String, driverPrice: String, relationCode: String, categoryName: String,createTime:String,remark:String) {
            context.startActivity(Intent(context, FundFlowInfoActivity::class.java)
                    .putExtra(Constants.CATEGORY,category)
                    .putExtra(Constants.REMARK,remark)
                    .putExtra(Constants.DRIVERPRICE,driverPrice)
                    .putExtra(Constants.RELATIONCODE, relationCode)
                    .putExtra(Constants.CATEGORYNAME,categoryName)
                    .putExtra(Constants.FUNDFLOWCREATETIME,createTime))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fundflow_info)
        category = intent.getStringExtra(Constants.CATEGORY) as String
        driverPrice = intent.getStringExtra(Constants.DRIVERPRICE) as String
        relationCode = intent.getStringExtra(Constants.RELATIONCODE) as String
        categoryName = intent.getStringExtra(Constants.CATEGORYNAME) as String
        fundFlowCreateTime = intent.getStringExtra(Constants.FUNDFLOWCREATETIME) as String
        remark = intent.getStringExtra(Constants.REMARK) as String
        initView()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back) as ImageView
        tvType =findViewById(R.id.tv_type) as TextView
        tvMoney =findViewById(R.id.tv_money) as TextView
        tvMsg =findViewById(R.id.tv_msg) as TextView
        tvTime =findViewById(R.id.tv_time) as TextView
        tvRemarkInfo =findViewById(R.id.tv_remark_info) as TextView

        if (category.equals("1")||category.equals("3")){
            tvMoney.text = "+"+driverPrice
        }else{
            tvMoney.text = driverPrice
        }

        tvMoney.text = driverPrice
        tvTime.text = fundFlowCreateTime
        tvMsg.text =relationCode
        tvType.text = categoryName
        tvRemarkInfo.text =remark

        ivBack.setOnClickListener {
            finish()
        }

    }

}