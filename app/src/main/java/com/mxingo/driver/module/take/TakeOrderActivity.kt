package com.mxingo.driver.module.take

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.baidu.location.LocationClient
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.mxingo.driver.OrderModel
import com.mxingo.driver.R
import com.mxingo.driver.model.*
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.base.map.route.SearchRouteActivity
import com.mxingo.driver.module.order.OrderInfoActivity
import com.mxingo.driver.module.order.OrderSource

import com.mxingo.driver.utils.Constants
import com.mxingo.driver.utils.TextUtil
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.ShowToast
import com.squareup.otto.Subscribe
import javax.inject.Inject

class TakeOrderActivity : BaseActivity(), TextWatcher {
    private lateinit var tvOrderType: TextView
    private lateinit var tvBookTime: TextView
    private lateinit var tvFlight: TextView
    private lateinit var imgFlight: ImageView
    private lateinit var tvFlightHint: TextView
    private lateinit var tvStartAddress: TextView
    private lateinit var tvEndAddress: TextView
    private lateinit var tvOrderHint: TextView
    private lateinit var etQuote: EditText
    private lateinit var llQuote: LinearLayout
    private lateinit var btnTake: Button
    private lateinit var llEndAddress: LinearLayout
    private lateinit var llStartAddress: LinearLayout
    private lateinit var llAddress: LinearLayout
    private lateinit var tvAddress: TextView

    private lateinit var llFlight: LinearLayout
    private lateinit var tvFee: TextView
    private lateinit var tvQuoteHint: TextView
    private lateinit var tvOrderNo: TextView
    private lateinit var llMilleage: LinearLayout
    private lateinit var tvPlanMilleage: TextView
    private lateinit var tvOpenMap: TextView
    private lateinit var tvRemarks: TextView
    private lateinit var tvOrderFrom: TextView

    private lateinit var orderEntity: OrderEntity
    private lateinit var driverNo: String
    private lateinit var progress: MyProgress


    @Inject
    lateinit var presenter: MyPresenter

    companion object {
        @JvmStatic
        fun startTakeOrderActivity(context: Context, order: OrderEntity, driverNo: String) {
            val intent: Intent = Intent(context, TakeOrderActivity::class.java)
                    .putExtra(Constants.ACTIVITY_DATA, order)
                    .putExtra(Constants.DRIVER_NO, driverNo)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_order)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        initView()

        orderEntity = intent.getSerializableExtra(Constants.ACTIVITY_DATA) as OrderEntity
        driverNo = intent.getStringExtra(Constants.DRIVER_NO) as String
        progress.show()
        presenter.qryOrder(orderEntity.orderNo)

        etQuote.addTextChangedListener(this)
    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == QryOrderEntity::class) {
            val data = any as QryOrderEntity
            if (data.rspCode.equals("00")) {
                initData(data)
            } else {
                ShowToast.showCenter(this, data.rspDesc)
            }
            progress.dismiss()
        } else if (any::class == LatestMyQuoteEntity::class) {
            val data = any as LatestMyQuoteEntity
            if (data.rspCode.equals("00")) {
                tvQuoteHint.text = "您已出过最低价" + data.orderQuote.myQuote / 100 + "元"
            }
        } else if (any::class == TakeOrderEntity::class) {
            progress.dismiss()
            val data = any as TakeOrderEntity
            if (data.rspCode.equals("00")) {
                ShowToast.showCenter(this, "抢单成功")
                OrderInfoActivity.startOrderInfoActivity(this, orderEntity.orderNo, data.flowNo, driverNo)
                finish()
            } else {
                ShowToast.showCenter(this, data.rspDesc)
                finish()
            }
        } else if (any::class == CommEntity::class) {
            val data = any as CommEntity
            if (data.rspCode.equals("00")) {
                progress.dismiss()
                ShowToast.showCenter(this, "报价成功")
                finish()
            } else {
                ShowToast.showCenter(this, data.rspDesc)
                presenter.qryOrder(orderEntity.orderNo)
            }
        }

    }

    private fun initData(qryOrderEntity: QryOrderEntity) {
        val order = qryOrderEntity.order
        orderEntity = order
        tvFlight.text = order.tripNo
        tvOrderFrom.text=OrderSource.getKey(order.source)
        tvOrderType.text = OrderType.getKey(order.orderType) + "(" + CarLevel.getKey(order.carLevel) + ")"
        tvBookTime.text = TextUtil.getFormatWeek(order.bookTime!!.toLong())
        tvFee.text = "¥ " + String.format("%.2f",order.orderAmount.toDouble() / 100) + " 元"
        tvOrderNo.text = "订单号: " + order.orderNo
        tvPlanMilleage.text = "${order.planMileage / 100 / 10.0}公里"
        if (OrderType.SEND_PLANE_TYPE == order.orderType || OrderType.TAKE_PLANE_TYPE == order.orderType ) {
            tvFlightHint.text = "航班:"
            imgFlight.setImageResource(R.drawable.ic_plane)
            llEndAddress.visibility = View.VISIBLE
            llAddress.visibility = View.GONE
        } else if (OrderType.SEND_TRAIN_TYPE == order.orderType || OrderType.TAKE_TRAIN_TYPE == order.orderType) {
            tvFlightHint.text = "车次:"
            imgFlight.setImageResource(R.drawable.ic_train)
            llEndAddress.visibility = View.VISIBLE
            llAddress.visibility = View.GONE
        }else if (OrderType.DIAN_DUI_DIAN==order.orderType){
            llFlight.visibility = View.GONE
            llEndAddress.visibility = View.VISIBLE
            llAddress.visibility = View.GONE
        }
        else {
            llEndAddress.visibility = View.GONE
            llFlight.visibility = View.GONE
            llStartAddress.visibility = View.GONE
            llQuote.visibility = View.GONE
            tvBookTime.text = TextUtil.getFormatString((order.bookTime)!!.toLong(), order.bookDays, "yyyy-MM-dd HH:mm")

        }
        if (!TextUtil.isEmpty(order.startAddr)) {
            tvStartAddress.text = order.startAddr
            tvAddress.text = order.startAddr
        }
        if (!TextUtil.isEmpty(order.endAddr)) {
            tvEndAddress.text = order.endAddr
        }
        if (order.orderModel == OrderModel.ROB_TYPE || order.orderModel == OrderModel.POINT_TYPE) {
            tvFee.text = "¥ " + String.format("%.2f",order.orderAmount.toDouble() / 100) + " 元"
            robOrder()
        } else if (order.orderModel == OrderModel.QUOTE_TYPE) {
            quoteOrder(qryOrderEntity)
            presenter.latestMyQuote(orderEntity.orderNo, driverNo)
        }
        if (!TextUtil.isEmpty(order.remark) && order.remark.length > 50) {
            tvRemarks.text = order.remark.substring(0, 50) + "..."
        } else if (!TextUtil.isEmpty(order.remark)) {
            tvRemarks.text = order.remark
        } else {
            tvRemarks.text = ""
        }
    }

    private fun initView() {
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "接单"

        tvOrderType = findViewById(R.id.tv_order_type) as TextView
        tvBookTime = findViewById(R.id.tv_book_time) as TextView
        tvFlight = findViewById(R.id.tv_flight) as TextView
        imgFlight = findViewById(R.id.img_flight) as ImageView
        tvFlightHint = findViewById(R.id.tv_flight_hint) as TextView
        tvStartAddress = findViewById(R.id.tv_start_address) as TextView
        tvEndAddress = findViewById(R.id.tv_end_address) as TextView
        tvOrderHint = findViewById(R.id.tv_order_hint) as TextView
        etQuote = findViewById(R.id.et_quote) as EditText
        llQuote = findViewById(R.id.ll_quote) as LinearLayout
        btnTake = findViewById(R.id.btn_take) as Button
        llEndAddress = findViewById(R.id.ll_end_address) as LinearLayout
        llFlight = findViewById(R.id.ll_flight) as LinearLayout
        tvFee = findViewById(R.id.tv_fee) as TextView
        tvQuoteHint = findViewById(R.id.tv_quote_hint) as TextView
        llAddress = findViewById(R.id.ll_address) as LinearLayout
        llStartAddress = findViewById(R.id.ll_start_address) as LinearLayout
        tvAddress = findViewById(R.id.tv_address) as TextView
        tvOrderNo = findViewById(R.id.tv_order_no) as TextView
        llMilleage = findViewById(R.id.ll_milleage) as LinearLayout
        tvPlanMilleage = findViewById(R.id.tv_plan_milleage) as TextView
        tvOpenMap = findViewById(R.id.tv_open_map) as TextView
        tvRemarks = findViewById(R.id.tv_remarks) as TextView
        tvOrderFrom = findViewById(R.id.tv_order_from) as TextView

        tvOpenMap.setOnClickListener {
            SearchRouteActivity.startSearchRouteActivity(this, orderEntity.orderNo, driverNo)
        }

        btnTake.setOnClickListener {
            if (OrderModel.ROB_TYPE == orderEntity.orderModel) {
                progress.show()
                presenter.takeOrder(orderEntity.orderNo, driverNo)
            } else if (OrderModel.QUOTE_TYPE == orderEntity.orderModel) {
                progress.show()
                presenter.quoteOrder(orderEntity.orderNo, driverNo, orderEntity.orderQuote, etQuote.text.toString())
            }
        }
    }

    override fun afterTextChanged(editable: Editable?) {
        if (editable != null) {
            if (editable.toString().length == 2) {
                val first = editable.first()
                val last = editable.last()
                if (first.toString().equals("0")) {
                    etQuote.text = editable.replace(0, 2, last.toString())
                    etQuote.setSelection(1)
                }
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }


    //抢单
    private fun robOrder() {
        btnTake.text = "抢单"
        tvOrderHint.visibility = View.GONE
        llQuote.visibility = View.GONE
        llMilleage.visibility = View.VISIBLE
    }

    //报价
    private fun quoteOrder(qryOrderEntity: QryOrderEntity) {
        btnTake.text = "报价"
        llMilleage.visibility = View.GONE
        tvOrderHint.visibility = View.VISIBLE
        llQuote.visibility = View.VISIBLE
        orderEntity.orderQuote = qryOrderEntity.orderQuote / 100
        tvFee.text = "¥ " + qryOrderEntity.orderQuote / 100 + " 元"
        if (qryOrderEntity.orderQuote / 100 - 5 < 1) {
            qryOrderEntity.orderQuote = 1
        } else {
            qryOrderEntity.orderQuote = qryOrderEntity.orderQuote / 100 - 5
        }
        val quoteVaule = (qryOrderEntity.orderQuote).toString().toCharArray()
        etQuote.setText(quoteVaule, 0, quoteVaule.size)
        etQuote.setSelection(etQuote.text.length)
    }
}
