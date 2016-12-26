package com.chinalooke.yuwan.bean;

import java.util.List;

/**
 * 签到历史实体类
 * Created by xiao on 2016/12/7.
 */

public class SignHistory {


    /**
     * Msg :
     * Result : ["2016-12-08 00:00:00"]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    private List<String> Result;

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

    public List<String> getResult() {
        return Result;
    }

    public void setResult(List<String> Result) {
        this.Result = Result;
    }
}
