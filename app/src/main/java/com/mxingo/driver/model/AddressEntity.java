package com.mxingo.driver.model;

import java.util.List;

/**
 * Created by zhouwei on 2017/8/9.
 */

public class AddressEntity {

    /**
     * area_code : 2
     * area_name : 黑龙江省
     * area_type : 1
     */

    public int area_code;
    public String area_name;
    public int area_type;
    public List<CityEntity> sub;

    public static class CityEntity {
        /**
         * area_code : 38
         * area_name : 大兴安岭地区
         * area_type : 2
         */

        public int area_code;
        public String area_name;
        public int area_type;
        public List<AreaEntity> sub;

        public static class AreaEntity {
            /**
             * area_code : 400
             * area_name : 塔河县
             * area_type : 3
             */

            public int area_code;
            public String area_name;
            public int area_type;
        }
    }
}
