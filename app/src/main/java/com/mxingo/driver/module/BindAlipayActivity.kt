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
import com.mxingo.driver.dialog.BindAlipayDialog
import com.mxingo.driver.dialog.BindSuccessDialog
import com.mxingo.driver.model.BindSuccessEntity
import com.mxingo.driver.model.CommEntity
import com.mxingo.driver.module.base.data.UserInfoPreferences
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.utils.TextUtil
import com.mxingo.driver.widget.ShowToast
import com.squareup.otto.Subscribe
import javax.inject.Inject

/**
 * Created by deqiangchen on 2023/3/6.
 */
class BindAlipayActivity:BaseActivity(){

    private lateinit var ivBack: ImageView
    private lateinit var ivBack2: ImageView
    private lateinit var btnBind: Button
    private lateinit var etAliPay: EditText
    private lateinit var etAliPayName: EditText
    private lateinit var etVcode: EditText
    private lateinit var llVerification: LinearLayout
    private lateinit var tvVcodes: List<TextView>
    private lateinit var tvCountDown: Button

    var countDown: MyCountDownTimer = MyCountDownTimer()

    @Inject
    lateinit var presenter: MyPresenter
    lateinit var driverNo: String

    companion object {
        @JvmStatic
        fun startBindAlipayActivity(context: Context, driverNo: String) {
            context.startActivity(Intent(context, BindAlipayActivity::class.java).putExtra(Constants.DRIVER_NO, driverNo))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_alipay)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        driverNo = intent.getStringExtra(Constants.DRIVER_NO)!!
        initView()
    }

    private fun initView() {
        ivBack = findViewById(R.id.iv_back) as ImageView
        ivBack2 =findViewById(R.id.iv_back_2) as ImageView
        btnBind =findViewById(R.id.btn_bind) as Button
        etAliPay =findViewById(R.id.et_alipay) as EditText
        etAliPayName =findViewById(R.id.et_alipay_name) as EditText
        etVcode =findViewById(R.id.et_vcode) as EditText
        tvCountDown =findViewById(R.id.tv_count_down) as Button
        llVerification =findViewById(R.id.ll_verification) as LinearLayout
        tvVcodes = arrayListOf(findViewById(R.id.tv_vcode_1) as TextView
                , findViewById(R.id.tv_vcode_2) as TextView
                , findViewById(R.id.tv_vcode_3) as TextView
                , findViewById(R.id.tv_vcode_4) as TextView)

        etAliPay.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnBind.setBackgroundResource(R.drawable.btn_blue_round_20)
            }

            override fun afterTextChanged(s: Editable?) {
                btnBind.isEnabled = true
            }
        })
        ivBack.setOnClickListener {
            finish()
        }

        ivBack2.setOnClickListener {
            finish()
        }

        tvCountDown.setOnClickListener {
            //获取短信验证，完成绑定
            presenter.getVcode(UserInfoPreferences.getInstance().mobile)
        }

        btnBind.setOnClickListener {
            if (etAliPay.text.isNotEmpty()&&etAliPayName.text.isNotEmpty()){
                //获取短信验证，完成绑定
                presenter.getVcode(UserInfoPreferences.getInstance().mobile)
            }else if (etAliPay.text.isNullOrEmpty()){
                ShowToast.showCenter(this,"请输入支付宝账号")
            }else {
                ShowToast.showCenter(this,"请输入支付宝姓名")
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
                    bindAlipay()
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

    private fun bindAlipay() {
        //请求网络，执行绑定操作
        presenter.bindPayAccount(UserInfoPreferences.getInstance().mobile,etVcode.text.toString().trim(),UserInfoPreferences.getInstance().carTeam,etAliPay.text.toString(),etAliPayName.text.toString())

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

    override fun onStart() {
        super.onStart()
        val dialog = BindAlipayDialog(this)
        dialog.setOnOkClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == CommEntity::class) {
            verification(any)
        }else if (any::class == BindSuccessEntity::class){
            bindSuccess(any)
        }
    }

    private fun bindSuccess(any: Any) {
        val successEntity = any as BindSuccessEntity
        if (successEntity.rspCode.equals("00")){
            UserInfoPreferences.getInstance().payAccount =etAliPay.text.toString()
            val dialog = BindSuccessDialog(this)
            dialog.setOnOkClickListener {
                dialog.dismiss()
                finish()
            }
            dialog.show()
        }
    }

    private fun verification(any: Any) {
        val commEntity = any as CommEntity
        if (commEntity.rspCode.equals("00")){
            btnBind.visibility =View.GONE
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