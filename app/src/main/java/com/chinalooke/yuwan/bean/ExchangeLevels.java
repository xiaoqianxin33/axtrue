package com.chinalooke.yuwan.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * 现金购买鱼丸的额度梯度实体类
 * Created by xiao on 2016/11/30.
 */
public class ExchangeLevels implements Serializable{

    /**
     * Msg :
     * Result : [{"exchange":"","money":""}]
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

    @DatabaseTable(tableName = "tb_exchange")
    public static class ResultBean implements Serializable {
        /**
         * exchange :
         * money :
         */
        @DatabaseField(columnName = "exchange")
        private String exchange;
        @DatabaseField(columnName = "money")
        private String money;
        @DatabaseField(generatedId = true)
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }
    }
}
