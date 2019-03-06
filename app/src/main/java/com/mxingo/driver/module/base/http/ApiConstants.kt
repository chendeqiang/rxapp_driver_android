package com.mxingo.driver.module.base.http

/**
 * Created by zhouwei on 2017/6/22.
 */
object ApiConstants {


    const val sign = "Rx-Sign"
    const val token = "Rx-Token"
    const val version = "Rx-Vern"

//        const val ip = "http://101.37.34.157:8018/"//线上
    const val ip = "http://101.37.202.182:8018/"//测试
//    const val ip = "http://101.37.85.68:8018/"//测试
//    const val ip = "https://wycapi.mxingo.com:443/"//线上1


    const val getVcode = "usr/driver/getvcode"//获取验证码
    const val login = "usr/driver/login"//登录
    const val logout = "usr/driver/logout"//修改密码
    const val online = "usr/driver/online"//上线

    const val offline = "usr/driver/offline"//下线
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
}
