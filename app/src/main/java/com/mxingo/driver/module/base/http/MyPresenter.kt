package com.mxingo.driver.module.base.http

import android.util.Log
import com.mxingo.driver.model.*
import com.mxingo.driver.module.base.log.LogUtils
import com.mxingo.driver.utils.TextUtil
import com.squareup.otto.Bus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by zhouwei on 2017/6/22.
 */
class MyPresenter(private val mBs: Bus, private val manger: MyManger) {

    fun register(any: Any) {
        mBs.register(any)
    }

    fun unregister(any: Any) {
        mBs.unregister(any)
    }

    fun getVcode(mobile: String) {
        if (!TextUtil.isMobileNO(mobile)) {
            val data = CommEntity()
            data.rspCode = "1000"
            data.rspDesc = "请输入正确号码"
            mBs.post(data)
            return
        }
        manger.getVcode(mobile, object : Callback<CommEntity> {
            override fun onResponse(call: Call<CommEntity>, response: Response<CommEntity>) {
                if (response.body() != null) {
                    LogUtils.d("getVcode", response.body().toString())
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<CommEntity>, t: Throwable) {
                val data = CommEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }

    fun login(mobile: String, vcode: String, orgName: String, devType: Int, devToken: String, devInfo: String) {
        if (!TextUtil.isMobileNO(mobile)) {
            val data = LoginEntity()
            data.rspCode = "1000"
            data.rspDesc = "请输入正确手机号"
            mBs.post(data)
        }

        if (TextUtil.isEmpty(orgName)) {
            val data = LoginEntity()
            data.rspCode = "1000"
            data.rspDesc = "车队名不能为空"
            mBs.post(data)
        }
        if (TextUtil.isEmpty(vcode)) {
            val data = LoginEntity()
            data.rspCode = "1000"
            data.rspDesc = "验证码不能为空"
            mBs.post(data)
        }

        manger.login(mobile, vcode, orgName, devType, devToken, devInfo, object : Callback<LoginEntity> {
            override fun onResponse(call: Call<LoginEntity>, response: Response<LoginEntity>) {
                if (response.body() != null) {
                    LogUtils.d("login", response.body().toString())
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<LoginEntity>, t: Throwable) {
                val data = LoginEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }

    fun online(no: String) {
        manger.online(no, object : Callback<OnlineEntity> {
            override fun onResponse(call: Call<OnlineEntity>, response: Response<OnlineEntity>) {

                if (response.body() != null) {
                    LogUtils.d("online", response.body().toString())
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<OnlineEntity>, t: Throwable) {
                val data = OnlineEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }

    fun logout(no: String) {
        manger.logout(no, object : Callback<LogoutEntity> {
            override fun onResponse(call: Call<LogoutEntity>, response: Response<LogoutEntity>) {
                if (response.body() != null) {
                    LogUtils.d("logout", response.body().toString())
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<LogoutEntity>, t: Throwable) {
                val data = LogoutEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }


    fun offline(no: String) {
        manger.offline(no, object : Callback<OfflineEntity> {
            override fun onResponse(call: Call<OfflineEntity>, response: Response<OfflineEntity>) {
                if (response.body() != null) {
                    LogUtils.d("offline", response.body().toString())
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<OfflineEntity>, t: Throwable) {
                val data = OfflineEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }

    fun repubOrder(orderNo: String, flowNo: String, driverNo: String) {
        manger.repubOrder(orderNo, flowNo, driverNo, object : Callback<CommEntity> {
            override fun onResponse(call: Call<CommEntity>, response: Response<CommEntity>) {
                LogUtils.d("repubOrder", "" + response.body() + "")
                if (response.body() != null) {
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<CommEntity>, t: Throwable) {
                val data = CommEntity()
                data.rspCode = "1000"
                data.rspDesc = "请输入正确号码"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }

    fun takeOrder(orderNo: String, driverNo: String) {
        manger.takeOrder(orderNo, driverNo, object : Callback<TakeOrderEntity> {
            override fun onResponse(call: Call<TakeOrderEntity>, response: Response<TakeOrderEntity>) {
                if (response.body() != null) {
                    LogUtils.d("takeOrder", response.body().toString())
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<TakeOrderEntity>, t: Throwable) {
                val data = TakeOrderEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }

    fun quoteOrder(orderNo: String, driverNo: String, minAmount: Int, amount: String) {
        if (TextUtil.isEmpty(amount) || amount.equals("0")) {
            val data = CommEntity()
            data.rspCode = "1000"
            data.rspDesc = "输入的金额不能为0"
            mBs.post(data)
            return
        }
        if (minAmount - amount.toInt() < 5) {
            val data = CommEntity()
            data.rspCode = "1000"
            data.rspDesc = "报价降幅至少5元"
            mBs.post(data)
            return
        }
        manger.quoteOrder(orderNo, driverNo, amount.toInt() * 100, object : Callback<CommEntity> {
            override fun onResponse(call: Call<CommEntity>, response: Response<CommEntity>) {
                if (response.body() != null) {
                    LogUtils.d("quoteOrder", response.body().toString())
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<CommEntity>, t: Throwable) {
                val data = CommEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }


    fun latestMyQuote(orderNo: String, no: String) {
        manger.latestMyQuote(orderNo, no, object : Callback<LatestMyQuoteEntity> {
            override fun onResponse(call: Call<LatestMyQuoteEntity>, response: Response<LatestMyQuoteEntity>) {
                if (response.body() != null) {
                    LogUtils.d("latestMyQuote", response.body().toString())
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<LatestMyQuoteEntity>, t: Throwable) {
                val data = LatestMyQuoteEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })

    }

    fun startPush(no: String) {
        manger.startPush(no, object : Callback<CommEntity> {
            override fun onResponse(call: Call<CommEntity>, response: Response<CommEntity>) {
                LogUtils.d("startPush", "" + response.body() + "")
                if (response.body() != null) {
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<CommEntity>, t: Throwable) {
                val data = CommEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }

    fun closePush(no: String) {
        manger.closePush(no, object : Callback<CommEntity> {
            override fun onResponse(call: Call<CommEntity>, response: Response<CommEntity>) {
                LogUtils.d("closePush", "" + response.body() + "")
                if (response.body() != null) {
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<CommEntity>, t: Throwable) {
                val data = CommEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }

    fun getInfo(no: String) {
        if (TextUtil.isEmpty(no)) {
            mBs.post("工号不能为空,请重新登录")
            return
        }
        manger.getInfo(no, object : Callback<DriverInfoEntity> {
            override fun onResponse(call: Call<DriverInfoEntity>, response: Response<DriverInfoEntity>) {
                if (response.body() != null) {
                    LogUtils.d("getInfo", response.body().toString())
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<DriverInfoEntity>, t: Throwable) {
                val data = DriverInfoEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }

    fun qryOrder(orderNo: String) {
        manger.qryOrder(orderNo, object : Callback<QryOrderEntity> {
            override fun onResponse(call: Call<QryOrderEntity>, response: Response<QryOrderEntity>) {
                LogUtils.d("qryOrder", "" + response.body() + "")
                if (response.body() != null) {
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<QryOrderEntity>, t: Throwable) {
                val data = QryOrderEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }


    fun listOrder(no: String, pageIndex: Int, pageCount: Int) {
        manger.listOrder(no, pageIndex, pageCount, object : Callback<ListOrderEntity> {
            override fun onResponse(call: Call<ListOrderEntity>, response: Response<ListOrderEntity>) {

                if (response.body() != null) {
                    LogUtils.d("listOrder", response.body().toString())
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<ListOrderEntity>, t: Throwable) {
                val data = ListOrderEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })

    }

    fun listDriverOrder(driverNo: String, flowStatus: Int, orderType: Int, pageIndex: Int, pageCount: Int) {
        manger.listDriverOrder(driverNo, flowStatus, orderType, pageIndex, pageCount,
                object : Callback<ListDriverOrderEntity> {
                    override fun onResponse(call: Call<ListDriverOrderEntity>, response: Response<ListDriverOrderEntity>) {
                        LogUtils.d("listDriverOrder", "" + response.body() + "")
                        if (response.body() != null) {
                            mBs.post(response.body())
                        }
                    }

                    override fun onFailure(call: Call<ListDriverOrderEntity>, t: Throwable) {
                        val data = ListDriverOrderEntity()
                        data.rspCode = "1000"
                        data.rspDesc = "网络连接失败"
                        mBs.post(data)
                        t.printStackTrace()
                    }
                })
    }

    fun startOrder(orderNo: String, flowNo: String) {
        manger.startOrder(orderNo, flowNo,
                object : Callback<StartOrderEntity> {
                    override fun onResponse(call: Call<StartOrderEntity>, response: Response<StartOrderEntity>) {
                        LogUtils.d("startOrder", "" + response.body() + "")
                        if (response.body() != null) {
                            mBs.post(response.body())
                        }
                    }

                    override fun onFailure(call: Call<StartOrderEntity>, t: Throwable) {
                        val data = StartOrderEntity()
                        data.rspCode = "1000"
                        data.rspDesc = "网络连接失败"
                        mBs.post(data)
                        t.printStackTrace()
                    }
                })
    }

    fun closeOrder(orderNo: String, flowNo: String) {
        manger.closeOrder(orderNo, flowNo, object : Callback<CloseOrderEntity> {
            override fun onResponse(call: Call<CloseOrderEntity>, response: Response<CloseOrderEntity>) {
                LogUtils.d("closeOrder", "" + response.body() + "")
                if (response.body() != null) {
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<CloseOrderEntity>, t: Throwable) {
                val data = CloseOrderEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }


    fun arrive(orderNo: String, flowNo: String, arriveLat: Double, arriveLon: Double) {
        manger.arrive(orderNo, flowNo, arriveLat, arriveLon, object : Callback<ArriveEntity> {
            override fun onResponse(call: Call<ArriveEntity>, response: Response<ArriveEntity>) {
                LogUtils.d("arrive", "" + response.body() + "")
                if (response.body() != null) {
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<ArriveEntity>, t: Throwable) {
                val data = ArriveEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }


    fun checkVersion(key: String) {
        manger.checkVersion(key, object : Callback<CheckVersionEntity> {
            override fun onResponse(call: Call<CheckVersionEntity>, response: Response<CheckVersionEntity>) {
                LogUtils.d("getDataDict", "" + response.body() + "")
                if (response.body() != null) {
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<CheckVersionEntity>, t: Throwable) {
                val data = CheckVersionEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }

    fun getCheckInfo(driverNo: String) {
        manger.getCheckInfo(driverNo, object : Callback<GetCheckInfo> {
            override fun onResponse(call: Call<GetCheckInfo>, response: Response<GetCheckInfo>) {
                LogUtils.d("getCheckInfo", "" + response.body() + "")
                if (response.body() != null) {
                    mBs.post(response.body())
                }
            }

            override fun onFailure(call: Call<GetCheckInfo>, t: Throwable) {
                val data = CheckVersionEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }
        })
    }


    fun checkInfo(driverNo: String, name: String, mobile: String, carBrand: String, carNo: String, carLevel: Int,
                     imgIdface: String, imgIdback: String, imgDriverlicense: String, imgVehiclelicense: String,
                     imgInsurance: String) {

        manger.checkInfo(driverNo, name, mobile, carBrand, carNo, carLevel, imgIdface, imgIdback, imgDriverlicense,
                imgVehiclelicense, imgInsurance, object : Callback<CommEntity> {
            override fun onFailure(call: Call<CommEntity>?, t: Throwable) {
                val data = CommEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }

            override fun onResponse(call: Call<CommEntity>?, response: Response<CommEntity>?) {
                LogUtils.d("checkInfo", "" + response!!.body() + "")
                if (response.body() != null) {
                    mBs.post(response.body())
                }

            }

        })
    }

    fun getQiNiuToken() {
        manger.getQiNiuToken(object : Callback<QiNiuTokenEntity> {
            override fun onFailure(call: Call<QiNiuTokenEntity>, t: Throwable) {
                val data = QiNiuTokenEntity()
                data.rspCode = "1000"
                data.rspDesc = "网络连接失败"
                mBs.post(data)
                t.printStackTrace()
            }

            override fun onResponse(call: Call<QiNiuTokenEntity>?, response: Response<QiNiuTokenEntity>?) {
                LogUtils.d("getQiNiuToken", "" + response!!.body() + "")
                if (response.body() != null) {
                    mBs.post(response.body())
                }

            }

        })
    }
}