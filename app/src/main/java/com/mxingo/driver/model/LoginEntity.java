package com.mxingo.driver.model;


public class LoginEntity {


    /**
     * no : saber001
     * rspCode : 00
     * rxToken : 7106
     * rspDesc : 成功
     */

    public String no;
    public String rspCode;
    public String rxToken;
    public String rspDesc;
    public String payAccount;
    public int showWallet;

    @Override
    public String toString() {
        return "{" +
                "no='" + no + '\'' +
                ", rspCode='" + rspCode + '\'' +
                ", rxToken='" + rxToken + '\'' +
                ", rspDesc='" + rspDesc + '\'' +
                ", payAccount='" + payAccount + '\'' +
                ", showWallet='" + showWallet + '\'' +
                '}';
    }
}
