package com.mxingo.driver.module.order

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mxingo.driver.OrderModel
import com.mxingo.driver.R
import com.mxingo.driver.dialog.MessageDialog
import com.mxingo.driver.dialog.NaviSelectDialog
import com.mxingo.driver.dialog.RepubDialog
import com.mxingo.driver.dialog.UnRepubDialog
import com.mxingo.driver.model.*
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.base.map.BaiduMapUtil
import com.mxingo.driver.module.take.CarLevel
import com.mxingo.driver.module.take.OrderStatus
import com.mxingo.driver.module.take.OrderType

import com.mxingo.driver.utils.Constants
import com.mxingo.driver.utils.StartUtil
import com.mxingo.driver.utils.TextUtil
import com.mxingo.driver.utils.TimeUtil
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.ShowToast
import com.mxingo.driver.widget.SlippingButton
import com.squareup.otto.Subscribe
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class OrderInfoActivity : BaseActivity() {

    private lateinit var tvOrderType: TextView
    private lateinit var tvBookTime: TextView
    private lateinit var tvFlight: TextView
    private lateinit var tvStartAddress: TextView
    private lateinit var tvEndAddress: TextView
    private lateinit var btnNavigation: Button
    private lateinit var btnTrace: Button
    private lateinit var tvFee: TextView
    private lateinit var tvOrderStatus: TextView
    private lateinit var tvPassengerName: TextView
    private lateinit var tvMobile: TextView
    private lateinit var btnMobile: Button

    private lateinit var tvRemark: TextView
    private lateinit var tvEstimate: TextView
    private lateinit var btnReach: Button
    private lateinit var btnStartOrder: SlippingButton
    private lateinit var llEndAddress: LinearLayout
    private lateinit var llStartAddress: LinearLayout
    private lateinit var llAddress: LinearLayout
    private lateinit var tvAddress: TextView

    private lateinit var imgFlight: ImageView
    private lateinit var tvFlightHint: TextView
    private lateinit var llFlight: LinearLayout

    private lateinit var imgFinish: ImageView
    private lateinit var llFinish: LinearLayout
    private lateinit var tvFinishEstimate: TextView
    private lateinit var tvFinishTime: TextView

    private lateinit var tvOrderNo: TextView
    private lateinit var tvOrderFrom: TextView


    private lateinit var orderNo: String
    private lateinit var flowNo: String
    private lateinit var actionChangeOrder: MenuItem
    private lateinit var driverNo: String

    @Inject
    lateinit var presenter: MyPresenter

    private lateinit var progress: MyProgress


    companion object {
        @JvmStatic
        fun startOrderInfoActivity(activty: Activity, orderNo: String, flowNo: String, driverNo: String) {
            activty.startActivityForResult(Intent(activty, OrderInfoActivity::class.java).
                    putExtra(Constants.ORDER_NO, orderNo).
                    putExtra(Constants.FLOW_NO, flowNo).
                    putExtra(Constants.DRIVER_NO, driverNo), 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_info)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        initView()
        orderNo = intent.getStringExtra(Constants.ORDER_NO)
        flowNo = intent.getStringExtra(Constants.FLOW_NO)
        driverNo = intent.getStringExtra(Constants.DRIVER_NO)
        Handler().postDelayed({
            progress.show()
            presenter.qryOrder(orderNo)
        }, 500)

        BaiduMapUtil.getInstance().registerLocationListener()
    }


    private fun initView() {
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "订单详情"

        tvOrderType = findViewById(R.id.tv_order_type) as TextView
        tvBookTime = findViewById(R.id.tv_book_time) as TextView
        tvFlight = findViewById(R.id.tv_flight) as TextView
        tvStartAddress = findViewById(R.id.tv_start_address) as TextView
        tvEndAddress = findViewById(R.id.tv_end_address) as TextView
        btnNavigation = findViewById(R.id.btn_navigation) as Button
        btnTrace = findViewById(R.id.btn_trace) as Button
        tvFee = findViewById(R.id.tv_fee) as TextView
        tvOrderStatus = findViewById(R.id.tv_order_status) as TextView

        tvPassengerName = findViewById(R.id.tv_passenger_name) as TextView

        tvMobile = findViewById(R.id.tv_mobile) as TextView

        btnMobile = findViewById(R.id.btn_mobile) as Button
        tvRemark = findViewById(R.id.tv_remark) as TextView

        tvEstimate = findViewById(R.id.tv_estimate) as TextView
        btnReach = findViewById(R.id.btn_reach) as Button
        btnStartOrder = findViewById(R.id.btn_start_order) as SlippingButton

        llEndAddress = findViewById(R.id.ll_end_address) as LinearLayout
        llStartAddress = findViewById(R.id.ll_start_address) as LinearLayout
        llAddress = findViewById(R.id.ll_address) as LinearLayout
        tvAddress = findViewById(R.id.tv_address) as TextView


        imgFlight = findViewById(R.id.img_flight) as ImageView
        tvFlightHint = findViewById(R.id.tv_flight_hint) as TextView
        llFlight = findViewById(R.id.ll_flight) as LinearLayout

        imgFinish = findViewById(R.id.img_finish) as ImageView
        llFinish = findViewById(R.id.ll_finish) as LinearLayout
        tvFinishEstimate = findViewById(R.id.tv_finish_estimate) as TextView
        tvFinishTime = findViewById(R.id.tv_finish_time) as TextView
        tvOrderNo = findViewById(R.id.tv_order_no) as TextView
        tvOrderFrom = findViewById(R.id.tv_order_from_detail) as TextView
    }

    private fun initListener(lon: Double, lat: Double, address: String) {
        btnNavigation.setOnClickListener {
            if (StartUtil.isInstallByread(StartUtil.baiduMapPackage) || StartUtil.isInstallByread(StartUtil.gaodeMapPackage)) {

                var navDialog = NaviSelectDialog(this)
                navDialog.setonBaiduMapClickListener {
                    navDialog.dismiss()

                    StartUtil.startBaiduMap(lat, lon, address, this)
                }
                navDialog.setonGaodeMapClickListener {
                    navDialog.dismiss()
                    StartUtil.startGaoDeMap(this, lat, lon)
                }
                navDialog.show()
            } else {
                ShowToast.showCenter(this, "您还未安装百度地图或者高德地图")
            }
        }

        btnMobile.setOnClickListener {
            val dialog = MessageDialog(this)
            dialog.setMessageText("" + tvMobile.text.toString())
            dialog.setOkText("呼叫")
            dialog.setOnCancelClickListener { dialog.dismiss() }
            dialog.setOnOkClickListener {
                dialog.dismiss()
                StartUtil.startPhone(tvMobile.text.toString(), this)
            }
            dialog.show()
        }

        btnStartOrder.setPosition {
            progress.show()
            presenter.startOrder(orderNo, flowNo)
        }

        btnReach.setOnClickListener {
            val dialog = MessageDialog(this)
            dialog.setMessageText("确定就位吗?")
            dialog.setOnCancelClickListener {
                dialog.dismiss()
            }
            dialog.setOnOkClickListener {
                dialog.dismiss()
                progress.show()
                presenter.arrive(orderNo, flowNo, BaiduMapUtil.getInstance().lat, BaiduMapUtil.getInstance().lon)
            }
            dialog.show()

        }

        btnTrace.setOnClickListener {
            MapActivity.startMapActivity(this, orderNo, flowNo, driverNo)
        }

        findViewById(R.id.btn_copy).setOnClickListener {
            val clipboardManager = (this.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager)
            clipboardManager.primaryClip = ClipData.newPlainText(null, tvOrderNo.text)
            ShowToast.showCenter(this, "复制成功")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.order_info, menu)
        actionChangeOrder = menu.findItem(R.id.action_change_order)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_change_order) {
            progress.show()
            presenter.getCurrentTime()
//            val curTime = TimeUtil.getNowTime()
//            val endTime = tvBookTime.text.toString().substring(0, 15)
//            val times = TimeUtil.getTimeDifference(curTime, endTime)
//            val times1 = times.substring(0, times.indexOf("小"))
//            val times2 = Integer.parseInt(times1)
//            if (times2 < 2) {
//                val dialog = UnRepubDialog(this)
//                dialog.setOnYesClickListener {
//                    dialog.dismiss()
//                }
//                dialog.show()
//            } else {
//                val dialog = RepubDialog(this)
//                dialog.setOnCancelClickListener {
//                    dialog.dismiss()
//                }
//                dialog.setOnOkClickListener {
//                    dialog.dismiss()
//                    progress.show()
//                    presenter.repubOrder(orderNo, flowNo, driverNo)
//                }
//                dialog.show()
//            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
        BaiduMapUtil.getInstance().unregisterLocationListener()
    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == QryOrderEntity::class) {
            val data: QryOrderEntity = any as QryOrderEntity
            if (data.rspCode.equals("00")) {
                setData(data.order)
            } else {
                ShowToast.showCenter(this, data.rspDesc)
                finish()
            }
            progress.dismiss()
        } else if (any::class == ArriveEntity::class) {
            val data = any as ArriveEntity
            if (data.rspCode.equals("00")) {
                presenter.qryOrder(orderNo)
            } else {
                progress.dismiss()
                ShowToast.showCenter(this, data.rspDesc)
            }
        } else if (any::class == StartOrderEntity::class) {
            progress.dismiss()
            val data = any as StartOrderEntity
            if (data.rspCode.equals("00")) {
                MapActivity.startMapActivity(this, orderNo, flowNo, driverNo)
                finish()
            } else {
                ShowToast.showCenter(this, data.rspDesc)
            }

        } else if (any::class == CommEntity::class) {
            progress.dismiss()
            val data = any as CommEntity
            if (data.rspCode.equals("00")) {
                ShowToast.showCenter(this, "改派成功")
                finish()
            } else {
                ShowToast.showCenter(this, data.rspDesc)
            }
        } else if (any::class == CurrentTimeEntity::class) {
            progress.dismiss()
            val data = any as CurrentTimeEntity
            val curTime = TimeUtil.getDateToString(data.now)
            val endTime = tvBookTime.text.toString().substring(0, 15)
            val times = TimeUtil.getTimeDifference(curTime, endTime)
            val times1 = times.substring(0, times.indexOf("小"))
            val times2 = Integer.parseInt(times1)
            if (times2 < Integer.parseInt(data.v)) {
                val dialog = UnRepubDialog(this)
                dialog.setOnYesClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                val dialog = RepubDialog(this)
                dialog.setOnCancelClickListener {
                    dialog.dismiss()
                }
                dialog.setOnOkClickListener {
                    dialog.dismiss()
                    progress.show()
                    presenter.repubOrder(orderNo, flowNo, driverNo)
                }
                dialog.show()
            }
        }
    }

    private fun setData(orderInfo: OrderEntity) {
        if (orderInfo.orderType == OrderType.SEND_PLANE_TYPE || orderInfo.orderType == OrderType.TAKE_PLANE_TYPE) {
            tvFlightHint.text = "航班:"
            imgFlight.setImageResource(R.drawable.ic_plane)
            llEndAddress.visibility = View.VISIBLE
            llStartAddress.visibility = View.VISIBLE
            llFlight.visibility = View.VISIBLE
            llAddress.visibility = View.GONE
            tvBookTime.text = TextUtil.getFormatWeek2(orderInfo.bookTime.toLong())

        } else if (orderInfo.orderType == OrderType.SEND_TRAIN_TYPE || orderInfo.orderType == OrderType.TAKE_TRAIN_TYPE) {
            tvFlightHint.text = "车次:"
            imgFlight.setImageResource(R.drawable.ic_train)
            llEndAddress.visibility = View.VISIBLE
            llStartAddress.visibility = View.VISIBLE
            llFlight.visibility = View.VISIBLE
            llAddress.visibility = View.GONE
            tvBookTime.text = TextUtil.getFormatWeek2(orderInfo.bookTime.toLong())
        } else {
            llEndAddress.visibility = View.GONE
            llStartAddress.visibility = View.GONE
            llFlight.visibility = View.GONE
            llAddress.visibility = View.VISIBLE

            tvBookTime.text = TextUtil.getFormatString((orderInfo.bookTime)!!.toLong(), orderInfo.bookDays, "yyyy-MM-dd HH:mm")
        }

        if (orderInfo.orderModel == OrderModel.POINT_TYPE) {
            tvOrderType.text = OrderType.getKey(orderInfo.orderType)
        } else {
            tvOrderType.text = OrderType.getKey(orderInfo.orderType) + "(" + CarLevel.getKey(orderInfo.carLevel) + ")"
        }

        tvOrderNo.text = orderInfo.orderNo
        tvFlight.text = orderInfo.tripNo
        tvStartAddress.text = orderInfo.startAddr
        tvAddress.text = orderInfo.startAddr
        tvOrderFrom.text = OrderSource.getKey(orderInfo.source)

        tvEndAddress.text = orderInfo.endAddr
        tvFee.text = "¥" + orderInfo.orderAmount / 100 + "元"
        tvMobile.text = orderInfo.passengerMobile
        tvPassengerName.text = orderInfo.passengerName

        if (!TextUtil.isEmpty(orderInfo.remark) && orderInfo.remark.length > 50) {
            tvRemark.text = orderInfo.remark.substring(0, 50) + "..."
        } else if (!TextUtil.isEmpty(orderInfo.remark)) {
            tvRemark.text = orderInfo.remark
        } else {
            tvRemark.text = ""
        }
        initListener(orderInfo.startLon, orderInfo.startLat, orderInfo.startAddr)
        if (orderInfo.orderStatus == OrderStatus.TAKEORDER_TYPE) {   //接单成功
            btnNavigation.visibility = View.VISIBLE
            btnTrace.visibility = View.GONE
            tvEstimate.text = "本次行程，约${orderInfo.planMileage / 100 / 10.0}公里"
            tvEstimate.visibility = View.VISIBLE
            btnReach.visibility = View.VISIBLE
            actionChangeOrder.isVisible = true
            tvOrderStatus.text = OrderStatus.getKey(orderInfo.orderStatus)
        } else if (orderInfo.orderStatus == OrderStatus.CANCELORDER_TYPE) {  //取消订单
            btnNavigation.visibility = View.GONE
            btnTrace.visibility = View.GONE
            llFinish.visibility = View.VISIBLE
            tvFinishEstimate.visibility = View.GONE
            imgFinish.visibility = View.VISIBLE
            imgFinish.setImageResource(R.drawable.ic_order_cancel)
            tvFinishTime.text = "取消时间:${TextUtil.getFormatString(orderInfo.orderTime)}"
        } else if (orderInfo.orderStatus == OrderStatus.DRIVERARRIVE_TYPE) {  //就位
            btnNavigation.visibility = View.GONE
            btnTrace.visibility = View.GONE
            btnStartOrder.visibility = View.VISIBLE
            actionChangeOrder.isVisible = false
            btnReach.visibility = View.GONE
            btnStartOrder.setHint("向右滑动开始用车")
            tvOrderStatus.text = OrderStatus.getKey(orderInfo.orderStatus)
        } else if (orderInfo.orderStatus == OrderStatus.USING_TYPE) {   //开始行程
            MapActivity.startMapActivity(this, orderNo, flowNo, driverNo)
            finish()
        } else if (orderInfo.orderStatus >= OrderStatus.WAIT_PAY_TYPE) {   //结束行程
            btnNavigation.visibility = View.GONE
            btnTrace.visibility = View.VISIBLE
            llFinish.visibility = View.VISIBLE
            imgFinish.visibility = View.VISIBLE
            imgFinish.setImageResource(R.drawable.ic_order_finish)
            tvFinishEstimate.text = "实际里程${orderInfo.orderMileage / 100 / 10.0}公里，实际用时${orderInfo.orderTime / 1000 / 60 / 60}小时${orderInfo.orderTime / 1000 / 60 % 60}分钟"
            tvFinishTime.text = "完成时间:${TextUtil.getFormatString(orderInfo.orderStopTime)}"
            if (orderInfo.orderType == OrderType.DAY_RENTER_TYPE) {
                btnTrace.visibility = View.GONE
            }
        }

    }
}
