package com.mxingo.driver.module.base.http

import com.mxingo.driver.model.*
import com.mxingo.driver.module.base.log.LogUtils
import retrofit2.Callback
import java.util.*

/**
 * Created by zhouwei on 2017/6/22.
 */
class MyManger(val apiService: ApiService) {

    fun getVcode(mobile: String, callback: retrofit2.Callback<CommEntity>) {
        val map = TreeMap<String, Any>()
        map.put("mobile", mobile)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("getVcode 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.getVcode(map, headers).enqueue(callback)

    }

    fun login(mobile: String, vcode: String, orgName: String, devType: Int, devToken: String, devInfo: String, callback: retrofit2.Callback<LoginEntity>) {
        val map = TreeMap<String, Any>()
        map.put("mobile", mobile)
        map.put("vcode", vcode)
        map.put("orgName", orgName)
        map.put("devType", devType)
        map.put("devToken", devToken)
        map.put("devInfo", devInfo)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("login 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.login(map, headers).enqueue(callback)
    }

    fun logout(no: String, callback: retrofit2.Callback<LogoutEntity>) {
        val map = TreeMap<String, Any>()
        map.put("driverNo", no)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("logot 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.logout(map, headers).enqueue(callback)

    }


    fun online(no: String, callback: retrofit2.Callback<OnlineEntity>) {
        val map = TreeMap<String, Any>()
        map.put("driverNo", no)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("online 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.online(map, headers).enqueue(callback)
    }

    fun offline(no: String, callback: retrofit2.Callback<OfflineEntity>) {
        val map = TreeMap<String, Any>()
        map.put("driverNo", no)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("offline 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.offline(map, headers).enqueue(callback)
    }

    fun repubOrder(orderNo: String, flowNo: String, driverNo: String, callback: retrofit2.Callback<CommEntity>) {
        val map = TreeMap<String, Any>()
        map.put("orderNo", orderNo)
        map.put("flowNo", flowNo)
        map.put("driverNo", driverNo)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("repubOrder 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.repubOrder(map, headers).enqueue(callback)
    }

    fun takeOrder(orderNo: String, driverNo: String, callback: retrofit2.Callback<TakeOrderEntity>) {
        val map = TreeMap<String, Any>()
        map.put("orderNo", orderNo)
        map.put("driverNo", driverNo)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("takeOrder 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.takeOrder(map, headers).enqueue(callback)
    }

    fun quoteOrder(orderNo: String, driverNo: String, amount: Int, callback: retrofit2.Callback<CommEntity>) {
        val map = TreeMap<String, Any>()
        map.put("orderNo", orderNo)
        map.put("driverNo", driverNo)
        map.put("orderAmount", amount)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("quoteOrder 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.quoteOrder(map, headers).enqueue(callback)
    }

    fun latestMyQuote(orderNo: String, no: String, callback: retrofit2.Callback<LatestMyQuoteEntity>) {
        val map = TreeMap<String, Any>()
        map.put("orderNo", orderNo)
        map.put("driverNo", no)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("latestMyQuote 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.latestMyQuote(map, headers).enqueue(callback)
    }

    fun startPush(no: String, callback: retrofit2.Callback<CommEntity>) {
        val map = TreeMap<String, Any>()
        map.put("driverNo", no)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("startPush 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.startPush(map, headers).enqueue(callback)
    }

    fun closePush(no: String, callback: retrofit2.Callback<CommEntity>) {
        val map = TreeMap<String, Any>()
        map.put("driverNo", no)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("closePush 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.closePush(map, headers).enqueue(callback)
    }

    fun getInfo(no: String, callback: retrofit2.Callback<DriverInfoEntity>) {
        val map = TreeMap<String, Any>()
        map.put("driverNo", no)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("getInfo 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.getInfo(map, headers).enqueue(callback)

    }


    fun qryOrder(orderNo: String, callback: retrofit2.Callback<QryOrderEntity>) {
        val map = TreeMap<String, Any>()
        map.put("orderNo", orderNo)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("qryOrder 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.qryOrder(map, headers).enqueue(callback)

    }


    fun listOrder(no: String, pageIndex: Int, pageCount: Int, callback: retrofit2.Callback<ListOrderEntity>) {
        val map = TreeMap<String, Any>()
        map.put("driverNo", no)
        map.put("pageIndex", pageIndex)
        map.put("pageCount", pageCount)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("listOrder 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.listOrder(map, headers).enqueue(callback)

    }

    fun listNotice(pageIndex: Int, pageCount: Int, callback: retrofit2.Callback<ListNoticeEntity>) {
        val map = TreeMap<String, Any>()
        map.put("pageIndex", pageIndex)
        map.put("pageCount", pageCount)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("listOrder 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.listNotice(map, headers).enqueue(callback)
    }

    fun listDriverOrder(driverNo: String, flowStatus: Int, orderType: Int, pageIndex: Int, pageCount: Int, callback: retrofit2.Callback<ListDriverOrderEntity>) {
        val map = TreeMap<String, Any>()
        map.put("driverNo", driverNo)
        map.put("flowStatus", flowStatus)
        if (orderType >= 1) {
            map.put("orderType", orderType)
        }
        map.put("pageIndex", pageIndex)
        map.put("pageCount", pageCount)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("listDriverOrder 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.listDriverOrder(map, headers).enqueue(callback)

    }

    fun listBill(driverNo: String, pageIndex: Int, pageCount: Int, callback: retrofit2.Callback<ListBillEntity>) {
        val map = TreeMap<String, Any>()
        map.put("driverNo", driverNo)
        map.put("pageIndex", pageIndex)
        map.put("pageCount", pageCount)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("listBill 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.listBill(map, headers).enqueue(callback)
    }

    fun startOrder(orderNo: String, flowNo: String, callback: retrofit2.Callback<StartOrderEntity>) {
        val map = TreeMap<String, Any>()
        map.put("orderNo", orderNo)
        map.put("flowNo", flowNo)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("startOrder 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.startOrder(map, headers).enqueue(callback)

    }

    fun closeOrder(orderNo: String, flowNo: String, callback: retrofit2.Callback<CloseOrderEntity>) {
        val map = TreeMap<String, Any>()
        map.put("orderNo", orderNo)
        map.put("flowNo", flowNo)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("closeOrder 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.closeOrder(map, headers).enqueue(callback)

    }

    fun arrive(orderNo: String, flowNo: String, arriveLat: Double, arriveLon: Double, callback: retrofit2.Callback<ArriveEntity>) {
        val map = TreeMap<String, Any>()
        map.put("orderNo", orderNo)
        map.put("flowNo", flowNo)
        map.put("arriveLat", arriveLat)
        map.put("arriveLon", arriveLon)

        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("arrive 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.arrive(map, headers).enqueue(callback)

    }

    fun checkVersion(key: String, callback: retrofit2.Callback<CheckVersionEntity>) {
        val map = TreeMap<String, Any>()
        map.put("key", key)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("checkVersion 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.checkVersion(map, headers).enqueue(callback)

    }


    fun getCheckInfo(driverNo: String, callback: retrofit2.Callback<GetCheckInfo>) {
        val map = TreeMap<String, Any>()
        map.put("driverNo", driverNo)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("checkVersion 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.getCheckInfo(map, headers).enqueue(callback)

    }

    fun checkInfo(driverNo: String, name: String, mobile: String, carBrand: String, carNo: String, carLevel: Int,
                  imgIdface: String, imgIdback: String, imgDriverlicense: String, imgVehiclelicense: String,
                  imgInsurance: String, callback: retrofit2.Callback<CommEntity>) {
        val map = TreeMap<String, Any>()
        map.put("driverNo", driverNo)
        map.put("name", name)
        map.put("mobile", mobile)
        map.put("carBrand", carBrand)
        map.put("carNo", carNo)
        map.put("carLevel", carLevel)
        map.put("imgIdface", imgIdface)
        map.put("imgIdback", imgIdback)
        map.put("imgDriverlicense", imgDriverlicense)
        map.put("imgVehiclelicense", imgVehiclelicense)
        map.put("imgInsurance", imgInsurance)
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("getCheckInfo 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.checkInfo(map, headers).enqueue(callback)
    }

    fun getQiNiuToken(callback: retrofit2.Callback<QiNiuTokenEntity>) {
        val map = TreeMap<String, Any>()
        map.put("time", System.currentTimeMillis())
        val headers = HeaderUtil.getHeaders(map)
        LogUtils.d("getQiNiuToken 参数", map.toString())
        LogUtils.d("headers", headers.toString())
        apiService.getQiNiuToken(map, headers).enqueue(callback)
    }

    fun getTime(callback: retrofit2.Callback<CurrentTimeEntity>) {
        apiService.getTime().enqueue(callback)
    }
}