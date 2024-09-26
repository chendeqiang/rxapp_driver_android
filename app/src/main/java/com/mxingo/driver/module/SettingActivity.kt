package com.mxingo.driver.module

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.mxingo.driver.R
import com.mxingo.driver.dialog.MessageDialog2
import com.mxingo.driver.model.CheckVersionEntity
import com.mxingo.driver.model.CommEntity
import com.mxingo.driver.model.DriverInfoEntity
import com.mxingo.driver.module.base.download.UpdateVersionActivity
import com.mxingo.driver.module.base.download.VersionEntity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.base.log.LogUtils
import com.mxingo.driver.module.take.CarLevel
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.utils.VersionInfo
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.MySwitch
import com.mxingo.driver.widget.ShowToast
import com.squareup.otto.Subscribe
import javax.inject.Inject

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SettingActivity : BaseActivity() {

    private lateinit var tvCarType: TextView
    private lateinit var mySwitch: MySwitch
    private lateinit var tvCheckVersion: TextView
    @Inject
    lateinit var presenter: MyPresenter
    private lateinit var driverNo: String
    private lateinit var progress: MyProgress

    private lateinit var rlCheckVersion:RelativeLayout
    private lateinit var rlPrivacyPolicy:RelativeLayout
    private lateinit var rlCancelUser:RelativeLayout

    companion object {
        @JvmStatic
        fun startSettingActivity(context: Context, driverNo: String) {
            context.startActivity(Intent(context, SettingActivity::class.java).putExtra(Constants.DRIVER_NO, driverNo))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)

        initView()

        driverNo = intent.getStringExtra(Constants.DRIVER_NO)!!
        progress = MyProgress(this)
        progress.show()
        presenter.getInfo(driverNo)
    }

    private fun initView() {
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "用户设置"

        tvCarType = findViewById(R.id.tv_car_type) as TextView

        mySwitch = findViewById(R.id.my_switch) as MySwitch

        rlCheckVersion =findViewById(R.id.rl_check_version) as RelativeLayout

        rlPrivacyPolicy=findViewById(R.id.rl_privacy_policy) as RelativeLayout

        rlCancelUser = findViewById(R.id.rl_cancel_user) as RelativeLayout

        tvCheckVersion = findViewById(R.id.tv_check_version) as TextView

        mySwitch.setResult { isSwitch ->

            if (isSwitch) {
                progress.show()
                presenter.startPush(driverNo)
            } else {
                progress.show()
                presenter.closePush(driverNo)
            }

        }

        tvCheckVersion.text = "v_" + VersionInfo.getVersionName()

        rlCheckVersion.setOnClickListener {
            progress.show()
            presenter.checkVersion(Constants.RX_DRIVER_APP)
        }

        rlCancelUser.setOnClickListener {
            val dialog = MessageDialog2(this)
            dialog.setMessageText("账号更正、删除、注销请联系车队处理")
            dialog.setOnOkClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        rlPrivacyPolicy.setOnClickListener {
            WebViewActivity.startWebViewActivity(this,"隐私政策","http://www.mxingo.com/mxnet/app/yinsi.html");
        }
    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == DriverInfoEntity::class) {
            val data = any as DriverInfoEntity
            if (data.rspCode.equals("00")) {
                tvCarType.text = CarLevel.getKey(data.carLevel)
                mySwitch.setSwitch(data.pushStatus.equals(1))
            } else {
                ShowToast.showCenter(this, data.rspDesc)
            }
        } else if (any::class == CommEntity::class) {
            val data = any as CommEntity
            if (!data.rspCode.equals("00")) {
                mySwitch.setSwitch(!mySwitch.isSwitch)
                ShowToast.showCenter(this, data.rspDesc)
            }
        } else if (any::class.java == CheckVersionEntity::class.java) {

            checkVersion(any as CheckVersionEntity)
        }
        progress.dismiss()
    }

    private fun checkVersion(data: CheckVersionEntity) {
        if (data.rspCode == "00") {
            val dataEntity = data.data
            val versionEntity = Gson().fromJson(dataEntity.value, VersionEntity::class.java)
            if (VersionInfo.getVersionCode() < versionEntity.versionCode && Constants.RX_DRIVER_APP == dataEntity.key) {
                versionEntity.isMustUpdate = versionEntity.forceUpdataVersions.contains(VersionInfo.getVersionName())
                UpdateVersionActivity.startUpdateVersionActivity(this, versionEntity)
            } else {
                ShowToast.showCenter(this, "您已经是最新版本")
            }
        } else {
            ShowToast.showCenter(this, data.rspDesc)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }
}
