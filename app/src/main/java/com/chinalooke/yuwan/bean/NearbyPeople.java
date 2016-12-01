package com.chinalooke.yuwan.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 附近战友实体类
 * Created by xiao on 2016/11/29.
 */

public class NearbyPeople {

    /**
     * Msg :
     * Result : [{"headImg":"","id":"","nickName":"","slogan":""}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    /**
     * headImg :
     * id :
     * nickName :
     * slogan :
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

    public static class ResultBean implements Serializable{
        private String headImg;
        private String id;
        private String nickName;
        private String slogan;

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getSlogan() {
            return slogan;
        }

        public void setSlogan(String slogan) {
            this.slogan = slogan;
        }
    }
}
