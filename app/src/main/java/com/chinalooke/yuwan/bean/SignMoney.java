package com.chinalooke.yuwan.bean;

import java.util.List;

/**
 * 签到奖励实体类
 * Created by xiao on 2016/12/8.
 */

public class SignMoney {

    /**
     * Msg :
     * Result : [{"days":"1","payMoney":"10"},{"days":"4","payMoney":"20"},{"days":"6","payMoney":"30"},{"days":"8","payMoney":"40"}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    private List<ResultBean> Result;

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }

    public List<ResultBean> getResult() {
        return Result;
    }

    public void setResult(List<ResultBean> Result) {
        this.Result = Result;
    }

    public static class ResultBean {
        /**
         * days : 1
         * payMoney : 10
         */

        private String days;
        private String payMoney;

        public String getDays() {
            return days;
        }

        public void setDays(String days) {
            this.days = days;
        }

        public String getPayMoney() {
            return payMoney;
        }

        public void setPayMoney(String payMoney) {
            this.payMoney = payMoney;
        }
    }
}
