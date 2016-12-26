package com.chinalooke.yuwan.bean;

/**
 * 用户余额积分实体类
 * Created by xiao on 2016/11/29.
 */

public class UserBalance {

    /**
     * Msg :
     * Result : {"payMoney":"","score":""}
     * Success : true
     */

    private String Msg;
    /**
     * payMoney :
     * score :
     */

    private ResultBean Result;
    private boolean Success;

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public ResultBean getResult() {
        return Result;
    }

    public void setResult(ResultBean Result) {
        this.Result = Result;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }

    public static class ResultBean {
        private String payMoney;
        private String score;

        public String getPayMoney() {
            return payMoney;
        }

        public void setPayMoney(String payMoney) {
            this.payMoney = payMoney;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }
    }
}
