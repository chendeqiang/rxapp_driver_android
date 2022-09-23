package com.mxingo.driver.module.base.http

import com.mxingo.driver.model.*
import retrofit2.Call
import retrofit2.http.*
import java.util.*


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
    @POST(ApiConstants.reassignment)
    fun reassignment(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>):Call<ResultEntity>

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
    @POST(ApiConstants.listCarPool)
    fun listCarPool(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<ListCarPoolOrderEntity>

    @FormUrlEncoded
    @POST(ApiConstants.carpoolOrderInfo)
    fun carpoolOrderInfo(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<CpOrderInfoEntity>

    @FormUrlEncoded
    @POST(ApiConstants.dpStatusChange)
    fun dpStatusChange(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<DpStatusChangeEntity>

    @FormUrlEncoded
    @POST(ApiConstants.orderStatusChange)
    fun orderStatusChange(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<OrderStatusChangeEntity>

    @FormUrlEncoded
    @POST(ApiConstants.driverStart)
    fun driverStart(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<DriverStartEntity>

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
    @POST(ApiConstants.hsUpload)
    fun hsUpload(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<HesuanResultEntity>

    @FormUrlEncoded
    @POST(ApiConstants.jkmUpload)
    fun jkmUpload(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<HesuanResultEntity>

    @FormUrlEncoded
    @POST(ApiConstants.getQiNiuToken)
    fun getQiNiuToken(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<QiNiuTokenEntity>

    @FormUrlEncoded
    @POST(ApiConstants.listNotice)
    fun listNotice(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<ListNoticeEntity>

    @FormUrlEncoded
    @POST(ApiConstants.driverBill)
    fun listBill(@FieldMap map: TreeMap<String, Any>, @HeaderMap headers: Map<String, String>): Call<ListBillEntity>


    @GET(ApiConstants.getTime)
    fun getTime(): Call<CurrentTimeEntity>


    @GET(ApiConstants.stsServer)
    fun stsServer():Call<StsEntity>
}
