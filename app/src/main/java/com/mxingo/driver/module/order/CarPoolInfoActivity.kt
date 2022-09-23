package com.mxingo.driver.module.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.mxingo.driver.R
import com.mxingo.driver.model.CpOrderInfoEntity
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.widget.MyProgress
import com.squareup.otto.Subscribe
import javax.inject.Inject

class CarPoolInfoActivity:BaseActivity() {

    @Inject
    lateinit var presenter: MyPresenter
    private lateinit var cmainid: String
    private lateinit var progress: MyProgress

    private lateinit var tvCost :TextView
    private lateinit var tvUser1Num :TextView
    private lateinit var tvUser1Num_ :TextView
    private lateinit var tvUser1Sd :TextView
    private lateinit var tvUser1Ed :TextView
    private lateinit var tvUser1Cost :TextView
    private lateinit var tvUser2Num :TextView
    private lateinit var tvUser2Num_ :TextView
    private lateinit var tvUser2Sd :TextView
    private lateinit var tvUser2Ed :TextView
    private lateinit var tvUser2Cost :TextView

    companion object {
        @JvmStatic
        fun startCarPoolInfoActivity(context: Context, cmainid: String) {
            context.startActivity(Intent(context, CarPoolInfoActivity::class.java).putExtra(Constants.CMAINID, cmainid))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carpool_info)
        cmainid = intent.getStringExtra(Constants.CMAINID) as String
        ComponentHolder.appComponent!!.inject(this)
        progress = MyProgress(this)
        presenter.register(this)

        presenter.carpoolOrderInfo(cmainid)
        initView()
    }

    private fun initView() {
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "订单详情"
        tvCost=findViewById(R.id.tv_cost) as TextView
        tvUser1Num=findViewById(R.id.tv_user1_num) as TextView
        tvUser1Num_=findViewById(R.id.tv_user1_num_) as TextView
        tvUser1Sd=findViewById(R.id.tv_user1_sd) as TextView
        tvUser1Ed=findViewById(R.id.tv_user1_ed) as TextView
        tvUser1Cost=findViewById(R.id.tv_user1_cost) as TextView
        tvUser2Num=findViewById(R.id.tv_user2_num) as TextView
        tvUser2Num_=findViewById(R.id.tv_user2_num_) as TextView
        tvUser2Sd=findViewById(R.id.tv_user2_sd) as TextView
        tvUser2Ed=findViewById(R.id.tv_user2_ed) as TextView
        tvUser2Cost=findViewById(R.id.tv_user2_cost) as TextView

    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == CpOrderInfoEntity::class){
            val data:CpOrderInfoEntity =any as CpOrderInfoEntity
            if (data.rspCode.equals("00")){
                tvCost.text = (data.orders[0].driverprice+data.orders[1].driverprice).toString()
                tvUser1Num.text ="尾号"+data.orders[0].cphone.subSequence(7,11)
                tvUser1Num_.text ="尾号"+data.orders[0].cphone.subSequence(7,11)
                tvUser1Sd.text =data.orders[0].cstartaddress
                tvUser1Ed.text =data.orders[0].cendaddress
                tvUser1Cost.text ="¥"+data.orders[0].driverprice.toString()
                tvUser2Num.text ="尾号"+data.orders[1].cphone.subSequence(7,11)
                tvUser2Num_.text ="尾号"+data.orders[1].cphone.subSequence(7,11)
                tvUser2Sd.text =data.orders[1].cstartaddress
                tvUser2Ed.text =data.orders[1].cendaddress
                tvUser2Cost.text ="¥"+data.orders[1].driverprice.toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }

}