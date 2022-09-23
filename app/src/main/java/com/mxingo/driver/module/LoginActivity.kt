package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mxingo.driver.R
import com.mxingo.driver.model.CommEntity
import com.mxingo.driver.model.LoginEntity
import com.mxingo.driver.module.WebViewActivity.Companion.startWebViewActivity
import com.mxingo.driver.module.base.data.MyModulePreference
import com.mxingo.driver.module.base.data.UserInfoPreferences
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.base.log.LogUtils
import com.mxingo.driver.module.take.MainActivity
import com.mxingo.driver.utils.DevInfo

import com.mxingo.driver.utils.TextUtil
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.ShowToast
import com.squareup.otto.Subscribe
import javax.inject.Inject


class LoginActivity : BaseActivity() {
    private lateinit var etVCode: EditText
    private lateinit var etPhone: EditText
    private lateinit var etCarTeam: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnGetVCode: Button
    private lateinit var progress: MyProgress
    private lateinit var ckAgreement: CheckBox
    private lateinit var tvUserAgreement:TextView
    private lateinit var tvPrivacyPolicy:TextView

    private var countDown: CountDownTimer = MyCountDownTimer()

    @Inject
    lateinit var presenter: MyPresenter

    companion object {
        @JvmStatic
        fun startLoginActivity(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    inner class MyCountDownTimer : CountDownTimer(60000, 1000) {
        override fun onFinish() {
            btnGetVCode.isClickable = true
            btnGetVCode.setBackgroundResource(R.drawable.btn_red_box)
            btnGetVCode.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.text_color_red))
            btnGetVCode.text = "获取验证码"
        }

        override fun onTick(time: Long) {
            btnGetVCode.text = (time / 1000).toString() + "s"
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        LogUtils.d("classname", this::class.java.name)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        initView()
    }

    private fun initView() {
        etCarTeam = findViewById(R.id.et_car_team) as EditText
        etPhone = findViewById(R.id.et_phone) as EditText
        etVCode = findViewById(R.id.et_vcode) as EditText
        btnLogin = findViewById(R.id.btn_sign) as Button
        btnGetVCode = findViewById(R.id.btn_get_vcode) as Button
        btnRegister = findViewById(R.id.btn_register) as Button
        ckAgreement = findViewById(R.id.ck_agreement) as CheckBox
        tvUserAgreement =findViewById(R.id.tv_user_agreement) as TextView
        tvPrivacyPolicy =findViewById(R.id.tv_privacy_policy) as TextView

//        if (!TextUtils.isEmpty(UserInfoPreferences.getInstance().driverNo)) {
//            MainActivity.startMainActivity(this)
//            finish()
//        }
//        ckAgreement.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked){
//                btnLogin.isEnabled = true
//            }else{
//                btnLogin.isEnabled = false
//                ShowToast.showBottom(this,"请勾选同意后再进行登录")
//            }
//        }
        if (!TextUtils.isEmpty(UserInfoPreferences.getInstance().driverNo)) {
            MainActivity.startMainActivity(this)
            finish()
        }

        tvUserAgreement.setOnClickListener {
            //服务规范
            startWebViewActivity(this, "服务规范", "http://www.mxingo.com/mxnet/app/serviceSpec.html")
        }

        tvPrivacyPolicy.setOnClickListener {
            startWebViewActivity(this,"隐私政策","http://www.mxingo.com/mxnet/app/yinsi.html")
        }
        btnRegister.setOnClickListener {
            DriverRegisterActivity.startRegisterActivity(this)
        }
        btnLogin.setOnClickListener {
            var devToken: String = UserInfoPreferences.getInstance().devToken
            val devInfo: String = DevInfo.getInfo()
            if (etPhone.text.isNullOrEmpty()){
                ShowToast.showCenter(this, "请输入手机号")
            }else if (etVCode.text.isNullOrEmpty()){
                ShowToast.showCenter(this, "请输入验证码")
            }else if (etCarTeam.text.isNullOrEmpty()){
                ShowToast.showCenter(this, "请输入车队名")
            }else if (ckAgreement.isChecked){
                progress.show()
                presenter.login(etPhone.text.toString().trim(), etVCode.text.toString().trim(), etCarTeam.text.toString().trim(), 1, devToken, devInfo)
            }else{
                ShowToast.showCenter(this,"请勾选同意后再进行登录")
            }
        }
        btnGetVCode.setOnClickListener {
            countDown.start()
            btnGetVCode.isClickable = false
            btnGetVCode.setBackgroundResource(R.drawable.btn_gray_box)
            btnGetVCode.setTextColor(ContextCompat.getColor(this, R.color.text_color_gray))
            presenter.getVcode(etPhone.text.toString())
        }
        etCarTeam.append(UserInfoPreferences.getInstance().carTeam)
        etPhone.append(UserInfoPreferences.getInstance().mobile)
    }


    @Subscribe
    fun loadData(any: Any) {
        if (any::class == LoginEntity::class) {
            login(any)
        } else if (any::class == CommEntity::class) {
            getVcode(any)
        }
    }

    private fun login(any: Any) {
        progress.dismiss()
        val loginEntity: LoginEntity = any as LoginEntity
        if (loginEntity.rspCode.equals("00")) {
            if (!TextUtil.isEmpty(loginEntity.no)) {
                UserInfoPreferences.getInstance().driverNo = loginEntity.no
                UserInfoPreferences.getInstance().token = loginEntity.rxToken
                UserInfoPreferences.getInstance().mobile = etPhone.text.toString()
                UserInfoPreferences.getInstance().carTeam = etCarTeam.text.toString()

                MainActivity.startMainActivity(this)
                finish()
            } else {
                ShowToast.showCenter(this, "数据有误，请联系服务人员")
            }
        }else if (loginEntity.rspCode.equals("200")){
            ShowToast.showCenter(this, "验证码错误")
        }else if (loginEntity.rspCode.equals("2001")){
            ShowToast.showCenter(this, "司机不存在")
        }
    }

    private fun getVcode(any: Any) {
        val commEntity = any as CommEntity
        if (!commEntity.rspCode.equals("00")) {
            ShowToast.showCenter(this, commEntity.rspDesc)
        } else {
            ShowToast.showCenter(this, "发送成功")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}

