package com.mxingo.driver.model;

import java.util.List;

/**
 * Created by chendeqiang on 2017/11/21 11:02
 */

public class ListBillEntity {

    /**
     * rspCode : 00
     * fine : [{"bookTime":1509706200000,"carLevel":1,"carNo":"苏E99887","cmoney":220,"createTime":1508913568000,"driverNo":"6bd0aff1e7f74dd59c","endAddr":"苏州市虎丘区苏州乐园欢乐世界","evaluate":"好的","modifyTime":1510903474000,"orderAmount":1000,"orderNo":"201710301953036339","orderType":1,"point":5,"remark":" 1\r\n\t\t\t\t\r\n\t\t\t","source":3,"startAddr":"上海市闵行区上海虹桥国际机场"}]
     * finishDriverOrderBill : [{"bookTime":1509533400000,"carLevel":1,"carNo":"苏E99887","cmoney":10,"createTime":1509329334000,"driverNo":"6bd0aff1e7f74dd59c","endAddr":"苏州市虎丘区苏州乐园欢乐世界","flowNo":"2017103010085465958","flowStatus":5,"modifyTime":1510737401000,"orderNo":"201710301953036339","orderType":6,"source":3,"startAddr":"上海市闵行区上海虹桥国际机场"}]
     * facePay : [{"bookTime":1509706200000,"carLevel":1,"carNo":"苏E99887","cmoney":0,"createTime":1508913568000,"driverNo":"6bd0aff1e7f74dd59c","endAddr":"苏州市虎丘区苏州乐园欢乐世界","evaluate":"好的","modifyTime":1510903474000,"orderAmount":1000,"orderNo":"201710301953036339","orderType":1,"point":5,"remark":" 1\r\n\t\t\t\t\r\n\t\t\t","source":3,"startAddr":"上海市闵行区上海虹桥国际机场"}]
     * rewards : [{"bookTime":1509706200000,"carLevel":1,"carNo":"苏E99887","cmoney":0,"createTime":1508913568000,"driverNo":"6bd0aff1e7f74dd59c","endAddr":"苏州市虎丘区苏州乐园欢乐世界","evaluate":"好的","modifyTime":1510903474000,"orderAmount":1000,"orderNo":"201710301953036339","orderType":1,"point":5,"remark":" 1\r\n\t\t\t\t\r\n\t\t\t","source":3,"startAddr":"上海市闵行区上海虹桥国际机场"}]
     * rspDesc : 成功
     */

    public String rspCode;
    public String rspDesc;
    public List<BillEntity> fine;//罚款列表
    public List<BillEntity> finishDriverOrderBill;//完成订单列表
    public List<BillEntity> facePay;//面付列表
    public List<BillEntity> rewards;//奖励列表

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", fine=" + fine +
                ", finishDriverOrderBill=" + finishDriverOrderBill +
                ", facePay=" + facePay +
                ", rewards=" + rewards +
                '}';
    }
}
