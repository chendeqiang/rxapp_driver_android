package com.mxingo.driver.module.order

/**
 * Created by chendeqiang on 2017/11/7 11:06
 */
enum class OrderSource {
    XIECHENG(1, "携程"), PINGTAI(2, "平台自有"), TONGCHENG(3, "同程"), WECHAT(4, "微信"), SENGYI(5, "胜意"),
    TUNIU(6, "途牛"), TAXI(7, "出租车"), FEIZHU(8, "飞猪"), CARTEAM(9, "车队自有"), TAOBAO(10, "淘宝店铺"), RENXING(11, "任行订单");


    private var value: Int
    private var key: String

    private constructor(value: Int, key: String) {
        this.value = value
        this.key = key
    }

    companion object {
        @JvmStatic
        fun getKey(index: Int): String {
            OrderSource.values().forEach {
                if (it.value == index) {
                    return it.key
                }
            }
            return ""
        }

        const val XIECHENG = 1
        const val PINGTAI = 2
        const val TONGCHENG = 3

        const val WECHAT = 4
        const val SENGYI = 5
        const val TUNIU = 6
        const val TAXI = 7
        const val FEIZHU = 8
        const val CARTEAM = 9
        const val TAOBAO = 10
        const val RENXING = 11
    }
}