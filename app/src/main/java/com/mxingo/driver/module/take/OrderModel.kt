package com.mxingo.driver


enum class OrderModel(private var value: Int, private var key: String) {
   ROB(1, "抢单"), QUOTE(2, "报价"),POINT(3, "指派");

    companion object {
        @JvmStatic
        fun getKey(index: Int): String {
            OrderModel.values().forEach {
                if (it.value == index) {
                    return it.key
                }
            }
            return ""
        }
        const val ROB_TYPE = 1
        const val QUOTE_TYPE = 2
        const val POINT_TYPE = 3
    }

}