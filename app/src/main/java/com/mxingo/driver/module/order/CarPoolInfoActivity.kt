package com.mxingo.driver.module.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
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
    private lateinit var tvUser1Type :TextView
    private lateinit var tvUser2Type :TextView
    private lateinit var tvUser3Type :TextView
    private lateinit var tvUser4Type :TextView
    private lateinit var tvUser1Sd :TextView
    private lateinit var tvUser1Ed :TextView
    private lateinit var tvUser1Cost :TextView
    private lateinit var tvUser2Num :TextView
    private lateinit var tvUser2Num_ :TextView
    private lateinit var tvUser2Sd :TextView
    private lateinit var tvUser2Ed :TextView
    private lateinit var tvUser2Cost :TextView
    private lateinit var ll_user2 :LinearLayout
    private lateinit var ll_user3 :LinearLayout
    private lateinit var ll_user4 :LinearLayout
    private lateinit var tvUser3Num :TextView
    private lateinit var tvUser3Num_ :TextView
    private lateinit var tvUser3Sd :TextView
    private lateinit var tvUser3Ed :TextView
    private lateinit var tvUser3Cost :TextView
    private lateinit var tvUser4Num :TextView
    private lateinit var tvUser4Num_ :TextView
    private lateinit var tvUser4Sd :TextView
    private lateinit var tvUser4Ed :TextView
    private lateinit var tvUser4Cost :TextView


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
        tvUser1Type =findViewById(R.id.tv_user1_type) as TextView
        tvUser1Sd=findViewById(R.id.tv_user1_sd) as TextView
        tvUser1Ed=findViewById(R.id.tv_user1_ed) as TextView
        tvUser1Cost=findViewById(R.id.tv_user1_cost) as TextView
        tvUser2Num=findViewById(R.id.tv_user2_num) as TextView
        tvUser2Num_=findViewById(R.id.tv_user2_num_) as TextView
        tvUser2Sd=findViewById(R.id.tv_user2_sd) as TextView
        tvUser2Ed=findViewById(R.id.tv_user2_ed) as TextView
        tvUser2Cost=findViewById(R.id.tv_user2_cost) as TextView
        tvUser2Type =findViewById(R.id.tv_user2_type) as TextView

        tvUser3Num=findViewById(R.id.tv_user3_num) as TextView
        tvUser3Num_=findViewById(R.id.tv_user3_num_) as TextView
        tvUser3Sd=findViewById(R.id.tv_user3_sd) as TextView
        tvUser3Ed=findViewById(R.id.tv_user3_ed) as TextView
        tvUser3Cost=findViewById(R.id.tv_user3_cost) as TextView
        tvUser3Type =findViewById(R.id.tv_user3_type) as TextView

        tvUser4Num=findViewById(R.id.tv_user4_num) as TextView
        tvUser4Num_=findViewById(R.id.tv_user4_num_) as TextView
        tvUser4Sd=findViewById(R.id.tv_user4_sd) as TextView
        tvUser4Ed=findViewById(R.id.tv_user4_ed) as TextView
        tvUser4Cost=findViewById(R.id.tv_user4_cost) as TextView
        tvUser4Type =findViewById(R.id.tv_user4_type) as TextView

        ll_user2 = findViewById<LinearLayout>(R.id.linearLayout2) as LinearLayout
        ll_user3 = findViewById<LinearLayout>(R.id.linearLayout3) as LinearLayout
        ll_user4 = findViewById<LinearLayout>(R.id.linearLayout4) as LinearLayout

    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == CpOrderInfoEntity::class){
            val data:CpOrderInfoEntity =any as CpOrderInfoEntity
            if (data.rspCode.equals("00")){
                if (data.orders.size==1){
                    tvCost.text = data.orders[0].driverprice.toString()
                    tvUser1Num.text ="尾号"+data.orders[0].phone.subSequence(7,11)
                    tvUser1Num_.text ="尾号"+data.orders[0].phone.subSequence(7,11)
                    tvUser1Type.text ="城际拼车"+data.orders[0].num+"人"
                    tvUser1Cost.text = " ¥ "+data.orders[0].driverprice.toString()
                    tvUser1Sd.text =data.orders[0].cstartaddress
                    tvUser1Ed.text =data.orders[0].cendaddress
                }else if (data.orders.size==2){
                    ll_user2.visibility =View.VISIBLE
                    tvCost.text = (data.orders[0].driverprice+data.orders[1].driverprice).toString()
                    tvUser1Num.text ="尾号"+data.orders[0].phone.subSequence(7,11)
                    tvUser1Num_.text ="尾号"+data.orders[0].phone.subSequence(7,11)
                    tvUser1Sd.text =data.orders[0].cstartaddress
                    tvUser1Ed.text =data.orders[0].cendaddress
                    tvUser1Cost.text =" ¥ "+data.orders[0].driverprice.toString()
                    tvUser1Type.text ="城际拼车"+data.orders[0].num+"人"
                    tvUser2Type.text ="城际拼车"+data.orders[1].num+"人"
                    tvUser2Num.text ="尾号"+data.orders[1].phone.subSequence(7,11)
                    tvUser2Num_.text ="尾号"+data.orders[1].phone.subSequence(7,11)
                    tvUser2Sd.text =data.orders[1].cstartaddress
                    tvUser2Ed.text =data.orders[1].cendaddress
                    tvUser2Cost.text =" ¥ "+data.orders[1].driverprice.toString()
                }else if (data.orders.size==3){
                    ll_user2.visibility =View.VISIBLE
                    ll_user3.visibility =View.VISIBLE
                    tvCost.text = (data.orders[0].driverprice+data.orders[1].driverprice+data.orders[2].driverprice).toString()
                    tvUser1Num.text ="尾号"+data.orders[0].phone.subSequence(7,11)
                    tvUser1Num_.text ="尾号"+data.orders[0].phone.subSequence(7,11)
                    tvUser1Sd.text =data.orders[0].cstartaddress
                    tvUser1Ed.text =data.orders[0].cendaddress
                    tvUser1Cost.text =" ¥ "+data.orders[0].driverprice.toString()
                    tvUser2Num.text ="尾号"+data.orders[1].phone.subSequence(7,11)
                    tvUser2Num_.text ="尾号"+data.orders[1].phone.subSequence(7,11)
                    tvUser2Sd.text =data.orders[1].cstartaddress
                    tvUser2Ed.text =data.orders[1].cendaddress
                    tvUser1Type.text ="城际拼车"+data.orders[0].num+"人"
                    tvUser2Type.text ="城际拼车"+data.orders[1].num+"人"
                    tvUser3Type.text ="城际拼车"+data.orders[2].num+"人"
                    tvUser2Cost.text =" ¥ "+data.orders[1].driverprice.toString()
                    tvUser3Num.text ="尾号"+data.orders[2].phone.subSequence(7,11)
                    tvUser3Num_.text ="尾号"+data.orders[2].phone.subSequence(7,11)
                    tvUser3Sd.text =data.orders[2].cstartaddress
                    tvUser3Ed.text =data.orders[2].cendaddress
                    tvUser3Cost.text =" ¥ "+data.orders[2].driverprice.toString()
                }else if (data.orders.size==4){
                    ll_user2.visibility =View.VISIBLE
                    ll_user3.visibility =View.VISIBLE
                    ll_user4.visibility =View.VISIBLE
                    tvCost.text = (data.orders[0].driverprice+data.orders[1].driverprice+data.orders[2].driverprice+data.orders[3].driverprice).toString()
                    tvUser1Num.text ="尾号"+data.orders[0].phone.subSequence(7,11)
                    tvUser1Num_.text ="尾号"+data.orders[0].phone.subSequence(7,11)
                    tvUser1Sd.text =data.orders[0].cstartaddress
                    tvUser1Ed.text =data.orders[0].cendaddress
                    tvUser1Cost.text =" ¥ "+data.orders[0].driverprice.toString()
                    tvUser2Num.text ="尾号"+data.orders[1].phone.subSequence(7,11)
                    tvUser2Num_.text ="尾号"+data.orders[1].phone.subSequence(7,11)
                    tvUser2Sd.text =data.orders[1].cstartaddress
                    tvUser2Ed.text =data.orders[1].cendaddress
                    tvUser2Cost.text =" ¥ "+data.orders[1].driverprice.toString()
                    tvUser3Num.text ="尾号"+data.orders[2].phone.subSequence(7,11)
                    tvUser3Num_.text ="尾号"+data.orders[2].phone.subSequence(7,11)
                    tvUser3Sd.text =data.orders[2].cstartaddress
                    tvUser3Ed.text =data.orders[2].cendaddress
                    tvUser1Type.text ="城际拼车"+data.orders[0].num+"人"
                    tvUser2Type.text ="城际拼车"+data.orders[1].num+"人"
                    tvUser3Type.text ="城际拼车"+data.orders[2].num+"人"
                    tvUser4Type.text ="城际拼车"+data.orders[3].num+"人"
                    tvUser3Cost.text =" ¥ "+data.orders[2].driverprice.toString()
                    tvUser4Num.text ="尾号"+data.orders[3].phone.subSequence(7,11)
                    tvUser4Num_.text ="尾号"+data.orders[3].phone.subSequence(7,11)
                    tvUser4Sd.text =data.orders[3].cstartaddress
                    tvUser4Ed.text =data.orders[3].cendaddress
                    tvUser4Cost.text =" ¥ "+data.orders[3].driverprice.toString()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }

}