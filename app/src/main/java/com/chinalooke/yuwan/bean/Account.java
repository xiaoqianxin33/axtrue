package com.chinalooke.yuwan.bean;

import java.util.List;

/**
 * 收支统计实体类
 * Created by xiao on 2016/12/1.
 */

public class Account {


    /**
     * Msg :
     * Result : [{"createTime":"","money":"","moneyType":"","payType":""}]
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
         * createTime :
         * money :
         * moneyType :
         * payType :
         */

        private String createTime;
        private String money;
        private String moneyType;
        private String payType;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getMoneyType() {
            return moneyType;
        }

        public void setMoneyType(String moneyType) {
            this.moneyType = moneyType;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }
    }
}
