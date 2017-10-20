package com.mxingo.driver.module.order

/**
 * Created by zhouwei on 2017/7/10.
 */
enum class FlowStatus {
    CANCEL(1, "取消订单"), RECV(2, "待服务"), USEING(3, "已就位"), ARRIVE(4, "开始行程"),
    WAIT_PAY(5, "结束行程"), REPUB(6, "改派"), PAY_SUCC(100, "支付成功"), PAY_FAIL(101, "支付失败");

    private var value: Int
    private var key: String

    private constructor(value: Int, key: String) {
        this.value = value
        this.key = key
    }

    companion object {
        @JvmStatic
        fun getKey(index: Int): String {
            FlowStatus.values().forEach {
                if (it.value == index) {
                    return it.key
                }
            }
            return ""
        }

        const val CANCEL_TYPE = 1
        const val RECV_ROB_TYPE = 2
        const val USEING_QUOTE_TYPE = 3

        const val ARRIVE_TYPE = 4
        const val WAIT_PAY_TYPE = 5
        const val REPUB_TYPE = 6
        const val PAY_SUCC_TYPE = 100
        const val PAY_FAIL_TYPE = 3
    }
}