package com.mxingo.driver.module.take

/**
 * Created by zhouwei on 2017/7/11.
 */
enum class CarLevel{
    ECONOMIC(1, "经济型"), COMFORT(2, "舒适型"),  BUSINESS(3, "商务型"), BUICK(4, "别克GL8"), DELUXE_CAR(5, "豪华型"), LUXURY(6, "奢华型"),
    DELUXE_BUSINESS(7, "豪华商务型"), EIGHT_MINIBUSES(20, "8座小巴"),TEN_MINIBUSES(30, "10座小巴"), FIFTEEN_MINIBUSES(40, "15座中巴"),
    TWENTY_BUSINESS(50, "20座中巴"), THIRTY_FIVE_BUSINESS(80, "35座大巴"), FORTY_BUSINESS(90, "40座大巴"),FORTY_FIVE_BUSINESS(100, "45座大巴"),
    FIFTY_BUSINESS(110, "50座大巴"), FIFTY_TWO_BUSINESS(120, "52座大巴");
    private var value: Int
    private  var key: String
    private constructor(value: Int, key: String){
        this.value = value
        this.key = key
    }
    companion object {
        @JvmStatic
        fun getKey(index: Int): String {
            CarLevel.values().forEach {
                if (it.value == index) {
                    return it.key
                }
            }
            return ""
        }
        const val ECONOMIC_TYPE = 1
        const val COMFORT_TYPE = 2
        const val BUSINESS_TYPE = 3
        const val BUICK_TYPE = 4
        const val DELUXE_CAR_TYPE = 5
        const val LUXURY_TYPE = 6
        const val DELUXE_BUSINESS_TYPE = 7
        const val EIGHT_MINIBUSES_TYPE = 20


        const val TEN_MINIBUSES_TYPE = 30
        const val FIFTEEN_MINIBUSES_TYPE = 40
        const val TWENTY_BUSINESS_TYPE = 50
        const val THIRTY_FIVE_BUSINESS_TYPE = 80
        const val FORTY_BUSINESS_TYPE = 90
        const val FORTY_FIVE_BUSINESS_TYPE = 100
        const val FIFTY_BUSINESS_TYPE = 110
        const val FIFTY_TWO_BUSINESS_TYPE = 120

    }
}