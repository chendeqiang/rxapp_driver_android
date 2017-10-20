package com.mxingo.driver.module.take

/**
 * Created by zhouwei on 2017/7/10.
 */
enum class OrderType{
    TAKE_PLANE(1, "接机"), SEND_PLANE(2, "送机"),  TAKE_TRAIN(3, "接站"), SEND_TRAIN(4, "送站"), DAY_RENTER(5, "日租");

    private var value: Int
    private  var key: String
    private constructor(value: Int, key: String){
        this.value = value
        this.key = key
    }
    companion object {
        @JvmStatic
        fun getKey(index: Int): String {
            OrderType.values().forEach {
                if (it.value == index) {
                    return it.key
                }
            }
            return ""
        }
        const val TAKE_PLANE_TYPE = 1
        const val SEND_PLANE_TYPE = 2
        const val TAKE_TRAIN_TYPE = 3
        const val SEND_TRAIN_TYPE = 4
        const val DAY_RENTER_TYPE = 5
    }
}