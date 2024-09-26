package com.mxingo.driver.module.order

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol
import com.amap.api.location.AMapLocationListener
import com.amap.api.location.AMapLocationQualityReport
import com.mxingo.driver.OrderModel
import com.mxingo.driver.R
import com.mxingo.driver.dialog.*
import com.mxingo.driver.model.*
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.module.RecordingService
import com.mxingo.driver.module.base.data.UserInfoPreferences
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.base.map.track.TrackSearchActivity
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

    private lateinit var btnStart: Button
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
    private lateinit var tvMsg: TextView

    private lateinit var tvOrderNo: TextView
    private lateinit var tvOrderFrom: TextView
    private lateinit var tvRepub: TextView
    private lateinit var ivBack: ImageView

    private var orderStartTime: Long =0
    private var orderStopTime: Long =0

    private lateinit var btnCopy: Button


    private lateinit var orderNo: String
    private lateinit var flowNo: String
    //    private lateinit var actionChangeOrder: MenuItem
    private lateinit var driverNo: String
    private lateinit var fleetid: String

    @Inject
    lateinit var presenter: MyPresenter

    private lateinit var progress: MyProgress

    lateinit var powerManager: PowerManager
    lateinit var mMediaRecorder: MediaRecorder

    private lateinit var mLocationClient:AMapLocationClient
    private lateinit var mlocationOption:AMapLocationClientOption

    private val REQUEST_CODE_PERMISSION = 1

    private var mLat:Double = 0.0
    private var mLon:Double = 0.0


    companion object {
        @JvmStatic
        fun startOrderInfoActivity(activty: Activity, orderNo: String, flowNo: String, driverNo: String) {
            activty.startActivityForResult(Intent(activty, OrderInfoActivity::class.java).putExtra(Constants.ORDER_NO, orderNo).putExtra(Constants.FLOW_NO, flowNo).putExtra(Constants.DRIVER_NO, driverNo), 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AMapLocationClient.updatePrivacyShow(applicationContext,true,true)
        AMapLocationClient.updatePrivacyAgree(applicationContext,true)
        setContentView(R.layout.activity_order_info)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        initView()

        initLocation()

        orderNo = intent.getStringExtra(Constants.ORDER_NO) as String
        flowNo = intent.getStringExtra(Constants.FLOW_NO) as String
        driverNo = intent.getStringExtra(Constants.DRIVER_NO) as String

        Handler().postDelayed({
            progress.show()
            presenter.qryOrder(orderNo)
        }, 300)
    }

    private fun initLocation() {
        try {
            mLocationClient = AMapLocationClient(applicationContext)
            mlocationOption =getDefaultOption()
            //设置定位参数
            mLocationClient.setLocationOption(mlocationOption)
            // 设置定位监听
            mLocationClient.setLocationListener(locationListener)
        }catch (e:Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 定位监听
     */
    var locationListener = AMapLocationListener { location ->
        if (null != location) {
            mLat=location.latitude
            mLon=location.longitude
            //上传定位信息
            presenter.arrive(orderNo, flowNo, mLat, mLon)
            } else {
            ShowToast.show(this,"定位失败，loc is null")
        }
    }

    /**
     * 获取GPS状态的字符串
     * @param statusCode GPS状态码
     * @return
     */
    private fun getGPSStatusString(statusCode: Int): String? {
        var str = ""
        when (statusCode) {
            AMapLocationQualityReport.GPS_STATUS_OK -> str = "GPS状态正常"
            AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER -> str = "手机中没有GPS Provider，无法进行GPS定位"
            AMapLocationQualityReport.GPS_STATUS_OFF -> str = "GPS关闭，建议开启GPS，提高定位质量"
            AMapLocationQualityReport.GPS_STATUS_MODE_SAVING -> str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量"
            AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION -> str = "没有GPS定位权限，建议开启gps定位权限"
        }
        return str
    }
    private fun getDefaultOption(): AMapLocationClientOption {
        val mOption = AMapLocationClientOption()
        mOption.locationMode = AMapLocationMode.Hight_Accuracy //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式

        mOption.isGpsFirst = false //可选，设置是否gps优先，只在高精度模式下有效。默认关闭

        mOption.httpTimeOut = 30000 //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效

        mOption.interval = 2000 //可选，设置定位间隔。默认为2秒

        mOption.isNeedAddress = true //可选，设置是否返回逆地理地址信息。默认是true

        mOption.isOnceLocation = false //可选，设置是否单次定位。默认是false

        mOption.isOnceLocationLatest = false //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用

        AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP) //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP

        mOption.isSensorEnable = false //可选，设置是否使用传感器。默认是false

        mOption.isWifiScan = true //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差

        mOption.isLocationCacheEnable = true //可选，设置是否使用缓存定位，默认为true

        mOption.geoLanguage = AMapLocationClientOption.GeoLanguage.DEFAULT //可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）

        return mOption
    }


    private fun initView() {

        powerManager = this.getSystemService(POWER_SERVICE) as PowerManager
        ivBack = findViewById(R.id.iv_back_info) as ImageView

        ivBack.setOnClickListener {
            finish()
        }
        tvRepub = findViewById(R.id.tv_repub) as TextView

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

        btnStart = findViewById(R.id.btn_start) as Button
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
        tvMsg = findViewById(R.id.tv_msg) as TextView

        btnCopy = findViewById(R.id.btn_copy) as Button


        tvRepub.setOnClickListener {
            presenter.reassignment(fleetid, orderNo)
        }
    }

    private fun initListener(lon: Double, lat: Double, address: String) {
        btnNavigation.setOnClickListener {
            if (StartUtil.isInstallByread(this,StartUtil.baiduMapPackage) || StartUtil.isInstallByread(this,StartUtil.gaodeMapPackage)) {

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
            presenter.startOrder(orderNo, flowNo)
        }

        btnReach.setOnClickListener {
            requestPermissionsIfAboveM()
            val curTime = TimeUtil.getNowTime()
            val bookTime = tvBookTime.text.toString().substring(0, 16)
            val leftTime = TimeUtil.getTimeDifferenceHour(curTime, bookTime)
            val left = Integer.parseInt(leftTime.substring(0, leftTime.indexOf(".")))

            if (left < 2) {
                val dialog = MessageDialog(this)
                dialog.setMessageText("确定就位吗?")
                dialog.setOnCancelClickListener {
                    dialog.dismiss()
                }
                dialog.setOnOkClickListener {
                    tvEstimate.visibility = View.GONE
                    dialog.dismiss()
                    startLocation()
                    //requestPermissionsIfAboveM()
                }
                dialog.show()
            } else {
                val dialog = MessageDialog2(this)
                dialog.setMessageText("请在距用车时间2小时内点击就位")
                dialog.setOnOkClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
        }

        //查询订单 轨迹
        btnTrace.setOnClickListener {
            TrackSearchActivity.startTrackSearchActivity(this,orderStartTime,orderStopTime)
        }

        btnCopy.setOnClickListener {
            val clipboardManager = (this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, tvOrderNo.text))
            //clipboardManager.primaryClip = ClipData.newPlainText(null, tvOrderNo.text)
            ShowToast.showCenter(this, "复制成功")
        }

        //司机点击-出发去接乘客-网络请求-
        btnStart.setOnClickListener {
            val curTime = TimeUtil.getNowTime()
            val bookTime = tvBookTime.text.toString().substring(0, 16)
            val leftTime = TimeUtil.getTimeDifferenceHour(curTime, bookTime)
            val left = Integer.parseInt(leftTime.substring(0, leftTime.indexOf(".")))
            if (left>5){
                val dialog = MessageDialog(this)
                dialog.setMessageText("距离用车时间较长，确定现在出发吗?")
                dialog.setOnCancelClickListener {
                    dialog.dismiss()
                }
                dialog.setOnOkClickListener {
                    dialog.dismiss()
                    presenter.driverStart(orderNo, driverNo)
                }
                dialog.show()
            }else{
                presenter.driverStart(orderNo, driverNo)
            }
        }
    }

    //开始定位
    private fun startLocation() {
        try {
            // 设置定位参数
            mLocationClient.setLocationOption(mlocationOption)
            // 启动定位
            mLocationClient.startLocation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /**
     * 停止定位
     */
    private fun stopLocation() {
        try {
            // 停止定位
            mLocationClient.stopLocation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
        if (null != mLocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mLocationClient.onDestroy()
        }
    }

    private val permissionHintMap: MutableMap<String, String?> = HashMap()
    private fun requestPermissionsIfAboveM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val requiredPermissions: MutableMap<String, String> = HashMap()
            requiredPermissions[Manifest.permission.ACCESS_FINE_LOCATION] = "定位"
            requiredPermissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] = "存储"
            requiredPermissions[Manifest.permission.READ_PHONE_STATE] = "读取设备信息"
            requiredPermissions[Manifest.permission.RECORD_AUDIO] = "录音"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                requiredPermissions[Manifest.permission.FOREGROUND_SERVICE] = "前台服务权限"
            }
            for (permission in requiredPermissions.keys) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionHintMap[permission] = requiredPermissions[permission]
                }
            }
            if (!permissionHintMap.isEmpty()) {
                requestPermissions(permissionHintMap.keys.toTypedArray(), REQUEST_CODE_PERMISSION)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val failPermissions: MutableList<String> = LinkedList()
        for (i in grantResults.indices) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                failPermissions.add(permissions[i])
            }
        }
        if (!failPermissions.isEmpty()) {
            val sb = StringBuilder()
            for (permission in failPermissions) {
                sb.append(permissionHintMap[permission]).append("、")
            }
            sb.deleteCharAt(sb.length - 1)
            val hint = "未授予必要权限: " +
                    sb.toString() +
                    "，请前往设置页面开启权限"
            AlertDialog.Builder(this)
                    .setMessage(hint)
                    .setNegativeButton("取消") { dialog, which -> System.exit(0) }.setPositiveButton("设置") { dialog, which ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                        System.exit(0)
                    }.show()
        }
    }

    @Subscribe
    fun loadData(any: Any) {
        if (any::class == QryOrderEntity::class) {
            val data: QryOrderEntity = any as QryOrderEntity
            if (data.rspCode.equals("00")) {
                //Conf_Reassignmentstatus=0 不显示按钮,Conf_Reassignmentstatus=1 显示按钮
                if (data.Conf_Reassignmentstatus.equals("0") || data.order.orderStatus >= OrderStatus.DRIVERARRIVE_TYPE || data.order.orderStatus == OrderStatus.CANCELORDER_TYPE) {
                    tvRepub.visibility = View.GONE
                } else {
                    tvRepub.visibility = View.VISIBLE
                }

                //Conf_Reassignmentstatus_Msg 显示可改派时间 次字段可能为空
                if (!TextUtil.isEmpty(data.Conf_Reassignmentstatus_Msg)) {
                    tvMsg.visibility = View.VISIBLE
                    tvMsg.text = data.Conf_Reassignmentstatus_Msg
                } else {
                    tvMsg.visibility = View.GONE
                }
                fleetid = data.order.orgId
                orderStartTime=data.order.orderStartTime
                orderStopTime =data.order.orderStopTime
                setData(data.order)
            } else {
                ShowToast.showCenter(this, data.rspDesc)
                finish()
            }
            progress.dismiss()
        } else if (any::class == ArriveEntity::class) {
            progress.dismiss()
            val data = any as ArriveEntity
            if (data.rspCode.equals("00")) {
                stopLocation()
                presenter.qryOrder(orderNo)
            } else {
                ShowToast.showCenter(this, data.rspDesc)
            }
        } else if (any::class == StartOrderEntity::class) {
            progress.dismiss()
            val data = any as StartOrderEntity
            if (data.rspCode.equals("00")) {
                //开启录音服务
                var intent = Intent(this, RecordingService::class.java)
                intent.putExtra("orderNo", orderNo)
                startService(intent)
                //开始行程
                MapActivity.startMapActivity(this, orderNo, flowNo, driverNo)
                finish()
            } else {
                ShowToast.showCenter(this, data.rspDesc)
            }

        }else if (any::class == DriverStartEntity::class) {
            progress.dismiss()
            val data = any as DriverStartEntity
            if (data.rspCode.equals("00")){
                UserInfoPreferences.getInstance().orderNo = orderNo
                btnStart.visibility =View.GONE
                btnReach.visibility = View.VISIBLE
            }else{
                ShowToast.showCenter(this, data.rspDesc)
            }
        }else if (any::class == CommEntity::class) {
            progress.dismiss()
            val data = any as CommEntity
            if (data.rspCode.equals("00")) {
                ShowToast.showCenter(this, data.rspDesc)
                finish()
            } else {
                ShowToast.showCenter(this, data.rspDesc)
            }
        } else if (any::class == ResultEntity::class) {//改派限制
            val data = any as ResultEntity
            if (data.rspCode.equals("00")) {
                progress.show()
                presenter.getCurrentTime()
            } else {
                val dialog = MessageDialog2(this)
                dialog.setMessageText(data.rspDesc)
                dialog.setOnOkClickListener {
                    dialog.dismiss()
                }
                dialog.show()
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
        } else if (orderInfo.orderType== OrderType.DIAN_DUI_DIAN){
            llEndAddress.visibility = View.VISIBLE
            llStartAddress.visibility = View.VISIBLE
            llFlight.visibility = View.GONE
            llAddress.visibility = View.GONE
            tvBookTime.text = TextUtil.getFormatWeek2(orderInfo.bookTime.toLong())
        }
        else {
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
//        tvFee.text = "¥" + orderInfo.orderAmount / 100 + "元(此订单为一口价订单)"
        tvFee.text = "¥" + String.format("%.2f", orderInfo.orderAmount.toDouble() / 100) + "元(此订单为一口价订单)"
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
            //增加按钮-出发去接乘客
            if (orderNo.equals(UserInfoPreferences.getInstance().orderNo)){
                btnStart.visibility =View.GONE
                btnReach.visibility =View.VISIBLE
            }else{
                btnStart.visibility = View.VISIBLE
            }
//            actionChangeOrder.isVisible = true
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
//            actionChangeOrder.isVisible = false
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
