package com.mxingo.driver.model;



public class WithdrawSuccessEntity{


    /**
     * rspCode : 00
     * rspDesc : 成功
     * desc : 您发起一笔提款，请等待工作人员审核，提款将在1-3工作日内到账，谢谢
     * createTime : 2023-03-13 10:00:00
     * payAccount : 15696164096
     * amount : 1
     */

    public String rspCode;
    public String rspDesc;
    public String desc;
    public String createTime;
    public String payAccount;
    public String amount;

    @Override
    public String toString() {
        return "{" +
                "rspCode='" + rspCode + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", desc='" + desc + '\'' +
                ", createTime='" + createTime + '\'' +
                ", payAccount='" + payAccount + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
