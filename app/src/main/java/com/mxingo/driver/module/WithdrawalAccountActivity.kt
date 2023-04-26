package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.mxingo.driver.R
import com.mxingo.driver.module.base.data.UserInfoPreferences
import com.mxingo.driver.utils.Constants

/**
 * Created by deqiangchen on 2023/3/7.
 * 提现账户
 */
class WithdrawalAccountActivity:BaseActivity() {
    private lateinit var ivBack: ImageView
    private lateinit var tvAlipayCount: TextView
    private lateinit var rlAlipayCount: RelativeLayout

    lateinit var driverNo: String
    companion object {
        @JvmStatic
        fun startWithdrawalAccountActivity(context: Context, driverNo: String) {
            context.startActivity(Intent(context, WithdrawalAccountActivity::class.java).putExtra(Constants.DRIVER_NO, driverNo))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdrawal)
        driverNo = intent.getStringExtra(Constants.DRIVER_NO)
        initView()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back) as ImageView
        rlAlipayCount =findViewById(R.id.rl_alipaycount)
        tvAlipayCount  =findViewById(R.id.tv_alipay_count) as TextView
        var payCount = UserInfoPreferences.getInstance().payAccount
        tvAlipayCount.text = payCount.subSequence(0,3).toString() + "****" + payCount.substring(payCount.length-4,payCount.length)

        ivBack.setOnClickListener {
            finish()
        }
        rlAlipayCount.setOnClickListener {
            SecondBindAlipayActivity.startSecondBindAlipayActivity(this,driverNo)
            finish()
        }
    }
}