package com.mxingo.driver.module.take

/**
 * Created by zhouwei on 2017/7/10.
 */
enum class OrderStatus(private var value: Int, private var key: String) {
    PUBORDER(1, "派单/报价中"), CANCELORDER(2, "取消订单"),  TAKEORDER(3, "待服务"), DRIVERARRIVE(4, "已就位"),
    USING(5, "开始行程"), WAIT_PAY(6, "结束行程"), PAY_SUCC(100, "支付成功"), PAY_FAIL(101, "支付失败");

    companion object {
        @JvmStatic
        fun getKey(index: Int): String {
            OrderStatus.values().forEach {
                if (it.value == index) {
                    return it.key
                }
            }
            return ""
        }
        const val PUBORDER_TYPE = 1
        const val CANCELORDER_TYPE = 2
        const val TAKEORDER_TYPE = 3
        const val DRIVERARRIVE_TYPE = 4
        const val USING_TYPE = 5
        const val WAIT_PAY_TYPE = 6
        const val PAY_SUCC_TYPE = 100
        const val PAY_FAIL_TYPE = 101
    }
}