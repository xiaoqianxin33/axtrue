package com.chinalooke.yuwan.bean;

/**
 * 注册返回实体类
 * Created by xiao on 2016/12/2.
 */

public class Register {

    /**
     * Msg :
     * Result : {"avatar":"http://121.42.172.61/public/images/defaultAvatar.png","id":"174"}
     * Success : true
     */

    private String Msg;
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
        /**
         * avatar : http://121.42.172.61/public/images/defaultAvatar.png
         * id : 174
         */

        private String avatar;
        private String id;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
