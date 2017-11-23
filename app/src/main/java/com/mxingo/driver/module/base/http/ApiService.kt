package com.mxingo.driver.module.base.http

import com.mxingo.driver.model.*
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import java.util.*

/**
 * Created by zhouwei on 2017/6/22.
 */
interface ApiService {

    @FormUrlEncoded
    @POST(ApiConstants.getVcode)
    fun getVcode(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<CommEntity>

    @FormUrlEncoded
    @POST(ApiConstants.login)
    fun login(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<LoginEntity>

    @FormUrlEncoded
    @POST(ApiConstants.logout)
    fun logout(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<LogoutEntity>

    @FormUrlEncoded
    @POST(ApiConstants.online)
    fun online(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<OnlineEntity>

    @FormUrlEncoded
    @POST(ApiConstants.offline)
    fun offline(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<OfflineEntity>

    @FormUrlEncoded
    @POST(ApiConstants.repubOrder)
    fun repubOrder(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<CommEntity>

    @FormUrlEncoded
    @POST(ApiConstants.takeOrder)
    fun takeOrder(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<TakeOrderEntity>

    @FormUrlEncoded
    @POST(ApiConstants.quoteOrder)
    fun quoteOrder(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<CommEntity>

    @FormUrlEncoded
    @POST(ApiConstants.latestMyQuote)
    fun latestMyQuote(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<LatestMyQuoteEntity>

    @FormUrlEncoded
    @POST(ApiConstants.startPush)
    fun startPush(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<CommEntity>

    @FormUrlEncoded
    @POST(ApiConstants.closePush)
    fun closePush(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<CommEntity>

    @FormUrlEncoded
    @POST(ApiConstants.qryOrder)
    fun qryOrder(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<QryOrderEntity>


    @FormUrlEncoded
    @POST(ApiConstants.getInfo)
    fun getInfo(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<DriverInfoEntity>

    @FormUrlEncoded
    @POST(ApiConstants.listOrder)
    fun listOrder(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<ListOrderEntity>

    @FormUrlEncoded
    @POST(ApiConstants.listDriverOrder)
    fun listDriverOrder(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<ListDriverOrderEntity>

    @FormUrlEncoded
    @POST(ApiConstants.startOrder)
    fun startOrder(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<StartOrderEntity>

    @FormUrlEncoded
    @POST(ApiConstants.closeOrder)
    fun closeOrder(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<CloseOrderEntity>

    @FormUrlEncoded
    @POST(ApiConstants.arrive)
    fun arrive(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<ArriveEntity>

    @FormUrlEncoded
    @POST(ApiConstants.checkVersion)
    fun checkVersion(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<CheckVersionEntity>

    @FormUrlEncoded
    @POST(ApiConstants.getCheckInfo)
    fun getCheckInfo(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<GetCheckInfo>

    @FormUrlEncoded
    @POST(ApiConstants.checkInfo)
    fun checkInfo(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<CommEntity>

    @FormUrlEncoded
    @POST(ApiConstants.getQiNiuToken)
    fun getQiNiuToken(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<QiNiuTokenEntity>

    @FormUrlEncoded
    @POST(ApiConstants.listNotice)
    fun listNotice(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<ListNoticeEntity>

    @FormUrlEncoded
    @POST(ApiConstants.driverBill)
    fun listBill(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<ListBillEntity>
}
