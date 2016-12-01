package com.chinalooke.yuwan.bean;

import java.util.List;

/**
 * 点赞人数实体类
 * Created by xiao on 2016/11/28.
 */

public class LikeList {

    /**
     * Msg :
     * Result : [{"nickName":"","userId":""}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    /**
     * nickName :
     * userId :
     */

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
        private String nickName;
        private String userId;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
