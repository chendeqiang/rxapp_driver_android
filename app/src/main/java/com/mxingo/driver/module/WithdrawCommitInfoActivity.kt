package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.utils.Constants

/**
 * Created by deqiangchen on 2023/3/17.
 */
class WithdrawCommitInfoActivity:BaseActivity()  {

    private lateinit var ivBack: ImageView
    private lateinit var tvTime: TextView
    private lateinit var tvAlipay: TextView
    private lateinit var tvMoney: TextView
    private lateinit var tvMsg: TextView
    private lateinit var btnFinish: Button
    lateinit var createTime: String
    lateinit var payAccount: String
    lateinit var amount: String
    lateinit var desc: String


    companion object {
        @JvmStatic
        fun startWithdrawCommitInfoActivity(context: Context, createTime: String,payAccount: String,amount: String,desc: String) {
            context.startActivity(Intent(context, WithdrawCommitInfoActivity::class.java)
                    .putExtra(Constants.CREATETIME, createTime)
                    .putExtra(Constants.PAYACCOUNT,payAccount)
                    .putExtra(Constants.AMOUNT,amount)
                    .putExtra(Constants.DESC,desc))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw_commit_info)
        createTime = intent.getStringExtra(Constants.CREATETIME)
        payAccount = intent.getStringExtra(Constants.PAYACCOUNT)
        amount = intent.getStringExtra(Constants.AMOUNT)
        desc = intent.getStringExtra(Constants.DESC)

        initView()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back) as ImageView
        tvTime =findViewById(R.id.tv_time) as TextView
        tvAlipay =findViewById(R.id.tv_alipay) as TextView
        tvMoney =findViewById(R.id.tv_money) as TextView
        tvMsg =findViewById(R.id.tv_msg) as TextView
        btnFinish = findViewById(R.id.btn_finish) as Button

        tvTime.text = createTime
        tvAlipay.text = "支付宝（"+ payAccount.substring(0,3)+"****"+payAccount.substring(payAccount.length-4,payAccount.length)+"）"
        tvMoney.text = amount+"元"
        tvMsg.text =desc

        ivBack.setOnClickListener {
            finish()
        }

        btnFinish.setOnClickListener {
            finish()
        }

    }

}