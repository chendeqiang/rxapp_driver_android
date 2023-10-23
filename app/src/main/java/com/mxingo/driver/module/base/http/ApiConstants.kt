package com.mxingo.driver.module.base.http


object ApiConstants {


    const val sign = "Rx-Sign"
    const val token = "Rx-Token"
    const val version = "Rx-Vern"
    const val ip = "http://112.124.27.106:8018/" //测试
//    const val ip = "https://wycapi.mxingo.com:443/"//生产


    const val getVcode = "usr/driver/getvcode"//获取验证码
    const val login = "usr/driver/login"//登录
    const val logout = "usr/driver/logout"//修改密码
    const val online = "usr/driver/online"//上线
    const val offline = "usr/driver/offline"//下线

    const val reassignment="usr/fleet/reassignment"//改派限制
    const val repubOrder = "usr/driver/repuborder"//改派订单
    const val takeOrder = "usr/driver/takeorder"//抢单
    const val quoteOrder = "usr/driver/quoteorder"//报价订单
    const val latestMyQuote = "usr/driver/latestmyquote"//查看最新报价
    const val startPush = "usr/driver/startpush"//开启订单推送
    const val closePush = "usr/driver/closepush"//关闭订单推送

    const val getInfo = "usr/driver/getinfo"//获取个人信息
    const val qryOrder = "usr/driver/qryorder"//获取订单信息
    const val listOrder = "usr/driver/listorder"//获取订单池列表
    const val listDriverOrder = "usr/driver/listdriverorder"//获取司机服务订单列表
    const val driverStart = "usr/driver/driverstart"//司机点击出发去接乘客
    const val startOrder = "usr/driver/startorder"//开始行程
    const val closeOrder = "usr/driver/closeorder"//结束订单
    const val arrive = "usr/driver/arrive"//司机到达指定位置
    const val checkVersion = "comm/checkversion"//检查更新

    const val checkInfo = "usr/driver/checkinfo"//网约车司机信息校验
    const val getCheckInfo = "usr/driver/getcheckinfo"//网约车司机信息获取
    const val getQiNiuToken = "usr/driver/getqiniutoken"//七牛token获取

    const val listNotice = "usr/driver/messageattentionlist"//获取公告列表

    const val driverBill = "usr/driver//driverbill"//司机账单
    const val getTime = "http://www.mxingo.com/appTime/getNow.shtml"

    const val stsServer="http://118.31.16.123:7080"

    const val listCarPool="driver/pc/dplist"//拼车行程
    const val carpoolOrderInfo="driver/pc/dplistdetail"//行程详情
    const val dpStatusChange="driver/pc/dpstatuschange"//行程状态更新
    const val orderStatusChange="driver/pc/orderstatuschange"//订单状态更新

    const val jkmUpload = "usr/driver/jkupload" //健康码上传
    const val hsUpload = "usr/driver/hsupload" //核酸上传

    const val payaccount  = "usr/driver/payaccount" //绑定支付宝
    const val wallet  = "usr/driver/wallet" //司机钱包详情
    const val fundflow  = "usr/driver/fundflow" //司机钱包流水列表
    const val cash  = "usr/driver/cash" //提现操作
    const val listCash  = "usr/driver/cashlist" //提现操作

    const val withdrawSetting  = "usr/driver/driversetting" //提现设置
}
