package com.mxingo.driver.utils

import com.mxingo.driver.module.base.data.UserInfoPreferences

/**
 * Created by cdq on 2017/6/23.
 */
object Constants {

    const val PAGE_SIZE = 5000
    const val permissionMain: Int = 1
    const val REQUEST_PERMISSION_SDCARD_6_0: Int = 2
    const val REQUEST_PERMISSION_SDCARD_8_0: Int = 3
    const val speechAppId = "595b2bbf"

    const val TITLE = "title"
    const val INDEX = "index"
    const val URL = "url"
    const val URI = "uri"
    const val ACTIVITY_DATA = "activity_data"
    const val ORDER_NO = "order_no"
    const val ORDERSTARTTIME = "order_start_time"
    const val ORDERSTOPTIME = "order_stop_time"
    const val ISDRAW = "is_draw"
    const val CANPICKMONEY = "can_PickMoney"
    const val FLOW_NO = "flow_no"
    const val FLOW_Stu = "flow_stu"
    const val DRIVER_NO = "driver_no"
    const val DRIVER_INFO = "driver_info"

    const val TAG = "order_tag"

    const val CREATETIME = "create_time"
    const val PAYACCOUNT = "pay_account"
    const val AMOUNT = "amount"
    const val DESC = "desc"


    const val FUNDFLOWCREATETIME = "fundflow_create_time"
    const val CATEGORY = "category"
    const val REMARK = "remark"
    const val DRIVERPRICE = "driverPrice"
    const val RELATIONCODE = "relationCode"
    const val CATEGORYNAME = "categoryName"



    const val CMAINID="cmainid"
    const val CCODE="ccode"

    const val P_D_PUB = 10000 // 派单/报价推送
    const val P_D_CANCEL = 10001 // 取消订单推送
    const val P_D_POINT = 10002 // 指派订单推送

    const val GETUI_ACTION = "getui_action" // 指派订单推送
    const val PUSH_TYPE = "pushType" // 指派订单推送
    const val PUSH_DATA = "pushData" // 指派订单推送

    const val RX_DRIVER_APP = "rx_driver_android"

    const val RX_VERSION = "rx_version"

    const val pictureSmall = "imageView2/0/w/100/h/100/q/75|imageslim"
//    const val pictureIp = "http://oxwbws6ys.bkt.clouddn.com/"
    const val pictureIp = "http://rxdriver.mxingo.com/"


    const val DEFAULT_GATHER_INTERVAL = 5

    /**
     * 默认打包周期
     */
    const val DEFAULT_PACK_INTERVAL = 30

    /**
     * 实时定位间隔(单位:秒)
     */
    const val LOC_INTERVAL = 10

    /**
     * 最后一次定位信息
     */
    const val LAST_LOCATION = "last_location"

    /**
     * 权限说明标示Key
     */
    const val PERMISSIONS_DESC_KEY = "permission_desc"
}
