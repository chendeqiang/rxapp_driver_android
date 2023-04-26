package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.mxingo.driver.R
import com.mxingo.driver.model.CommEntity
import com.mxingo.driver.model.WithdrawSuccessEntity
import com.mxingo.driver.module.base.data.UserInfoPreferences
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.base.log.LogUtils
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.utils.TextUtil
import com.mxingo.driver.widget.ShowToast
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * Created by deqiangchen on 2023/3/7.
 * 提现页面
 */
class WithdrawActivity:BaseActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var ivBack2: ImageView
    private lateinit var tvWithdrawCount: TextView
    private lateinit var tvWithdrawRecord: TextView
    private lateinit var tvPayCount: TextView
    private lateinit var tv11: TextView
    private lateinit var tvCk: TextView
    private lateinit var tvTxCan: TextView
    private lateinit var etWriteMoney: EditText
    private lateinit var tvAll: TextView
    private lateinit var btnTx: Button
    private lateinit var rlAliPay: RelativeLayout
    @Inject
    lateinit var presenter: MyPresenter
    lateinit var driverNo: String
    private lateinit var isDraw: String
    private lateinit var canPickMoney: String
    private lateinit var tvCountDown: Button
    private lateinit var etVcode: EditText
    private lateinit var llVerification: LinearLayout
    private lateinit var tvVcodes: List<TextView>

    var countDown: MyCountDownTimer = MyCountDownTimer()

    companion object {
        @JvmStatic
        fun startWithdrawActivity(context: Context, driverNo: String, draw: String, canPickMoney: String) {
            context.startActivity(Intent(context, WithdrawActivity::class.java).putExtra(Constants.DRIVER_NO, driverNo).putExtra(Constants.ISDRAW,draw).putExtra(Constants.CANPICKMONEY,canPickMoney))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        driverNo = intent.getStringExtra(Constants.DRIVER_NO)
        isDraw = intent.getStringExtra(Constants.ISDRAW)
        canPickMoney =intent.getStringExtra(Constants.CANPICKMONEY)
        initView()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back) as ImageView
        ivBack2 =findViewById(R.id.iv_back_2) as ImageView
        tvWithdrawCount =findViewById(R.id.tv_withdraw_count) as TextView
        tvWithdrawRecord = findViewById(R.id.tv_withdraw_record) as TextView
        tv11 = findViewById(R.id.tv_11) as TextView
        tvCk = findViewById(R.id.tv_ck) as TextView
        tvTxCan = findViewById(R.id.tv_tx_can) as TextView
        tvAll = findViewById(R.id.tv_all) as TextView
        etWriteMoney =findViewById(R.id.et_write_money) as EditText

        rlAliPay = findViewById(R.id.rl_alipay) as RelativeLayout
        tvPayCount =findViewById(R.id.tv_pay_count) as TextView
        btnTx =findViewById(R.id.btn_tx) as Button

        etVcode =findViewById(R.id.et_vcode) as EditText
        tvCountDown =findViewById(R.id.tv_count_down) as Button
        llVerification =findViewById(R.id.ll_verification_tx) as LinearLayout
        tvVcodes = arrayListOf(findViewById(R.id.tv_vcode_1) as TextView
                , findViewById(R.id.tv_vcode_2) as TextView
                , findViewById(R.id.tv_vcode_3) as TextView
                , findViewById(R.id.tv_vcode_4) as TextView)

        tvTxCan.text = canPickMoney
        if (isDraw.equals("0")){//可以提现
            tvCk.visibility = View.GONE
            etWriteMoney.visibility =View.VISIBLE
            tv11.setTextColor(this.resources.getColor(R.color.text_color_black))
            tvAll.setTextColor(this.resources.getColor(R.color.colorPrimary))
            tvAll.isClickable = true
            tvAll.isEnabled =true
        }

        etWriteMoney.setOnClickListener {
            etWriteMoney.requestFocus()
            etWriteMoney.isCursorVisible =true
            showInput()
            tv11.setTextColor(this.resources.getColor(R.color.colorPrimary))
        }

        etWriteMoney.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()){
                    tvWithdrawCount.text = "0"
                }else{
                    tvWithdrawCount.text = s.toString()
                    btnTx.isEnabled =true
                    btnTx.setBackgroundResource(R.drawable.btn_blue_round_20)
                }
            }
        })

        tvAll.setOnClickListener {
            tvWithdrawCount.text =canPickMoney
            etWriteMoney.setText(canPickMoney)
            btnTx.isEnabled =true
            btnTx.setBackgroundResource(R.drawable.btn_blue_round_20)
        }

        var payCount = UserInfoPreferences.getInstance().payAccount
        tvPayCount.text = payCount.subSequence(0,3).toString() + "****" + payCount.substring(payCount.length-4,payCount.length)

        ivBack.setOnClickListener {
            finish()
        }

        ivBack2.setOnClickListener {
            finish()
        }
        rlAliPay.setOnClickListener {
            WithdrawalAccountActivity.startWithdrawalAccountActivity(this,driverNo)
        }

        tvWithdrawRecord.setOnClickListener {
            WithdrawRecordActivity.startWithdrawRecordActivity(this,driverNo)
        }

        tvCountDown.setOnClickListener {
            //获取短信验证，完成绑定
            presenter.getVcode(UserInfoPreferences.getInstance().mobile)
        }

        btnTx.setOnClickListener {
            if (tvWithdrawCount.text.toString()!="0" && tvWithdrawCount.text.toString().toDouble() < canPickMoney.toDouble()||tvWithdrawCount.text.toString().toDouble()==canPickMoney.toDouble()){
                presenter.getVcode(UserInfoPreferences.getInstance().mobile)
            }else{
                ShowToast.showCenter(this,"提现金额无效")
            }
        }

        etVcode.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                initVcode(s.toString())
                if (s.toString().length == 4) {
                    withdraw()
                }
            }
        })

        tvVcodes.forEach {
            it.setOnClickListener {
                etVcode.requestFocus()
                showInput()
            }
        }
    }

    private fun withdraw() {
        presenter.cash(driverNo,tvWithdrawCount.text.toString(),etVcode.text.toString().trim(),UserInfoPreferences.getInstance().mobile)
    }

    private fun initVcode(text: String) {
        if (TextUtil.isEmpty(text)) {
            tvVcodes.forEach {
                it.isSelected = false
                it.text = ""
            }
            return
        } else {
            for (i in 0..text.length - 1) {
                tvVcodes[i].isSelected = true
                tvVcodes[i].text = text.split("")[i + 1]
            }
            for (i in text.length..tvVcodes.size - 1) {
                tvVcodes[i].isSelected = false
                tvVcodes[i].text = ""
            }
        }
    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == CommEntity::class) {
            verification(any)
        }else if (any::class == WithdrawSuccessEntity::class){
            withdrawSuccess(any)
        }
    }

    private fun withdrawSuccess(any: Any) {
        val withdrawSuccessEntity = any as WithdrawSuccessEntity
        if (withdrawSuccessEntity.rspCode.equals("00")){
            WithdrawCommitInfoActivity.startWithdrawCommitInfoActivity(this,withdrawSuccessEntity.createTime,withdrawSuccessEntity.payAccount,withdrawSuccessEntity.amount,withdrawSuccessEntity.desc)
            finish()
        }else if (withdrawSuccessEntity.rspCode.equals("10001")){
            ShowToast.showCenter(this,"司机钱包账户不存在")
        }else if (withdrawSuccessEntity.rspCode.equals("10006")){
            ShowToast.showCenter(this,"当前司机钱包状态冻结")
        }else if (withdrawSuccessEntity.rspCode.equals("10008")){
            ShowToast.showCenter(this,"司机提现金额小于零")
        }else if (withdrawSuccessEntity.rspCode.equals("10009")){
            ShowToast.showCenter(this,"司机体现金额超过可提现金额")
        }else if (withdrawSuccessEntity.rspCode.equals("10011")){
            ShowToast.showCenter(this,"当前时间不是提现周期")
        }else if (withdrawSuccessEntity.rspCode.equals("10015")){
            ShowToast.showCenter(this,"有一笔提款在进行中")
        }else if (withdrawSuccessEntity.rspCode.equals("10017")){
            ShowToast.showCenter(this,"提现次数超限，请在下一个提现周期内，再次操作")
        }else if (withdrawSuccessEntity.rspCode.equals("200")){
            ShowToast.showCenter(this,"验证码错误")
        }

    }

    private fun verification(any: Any) {
        val commEntity = any as CommEntity
        if (commEntity.rspCode.equals("00")){
            btnTx.visibility =View.GONE
            llVerification.visibility =View.VISIBLE
            countDown.start()
            etVcode.requestFocus()
            showInput()
        }
    }

    private fun showInput() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
    inner class MyCountDownTimer : CountDownTimer(60000, 1000) {
        override fun onFinish() {
            tvCountDown.text = "获取验证码"
            tvCountDown.isEnabled =true
        }

        override fun onTick(downTime: Long) {
            tvCountDown.text = "${downTime / 1000}秒后重新获取"
            tvCountDown.isEnabled = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }
}