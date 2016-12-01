package com.chinalooke.yuwan.bean;

import java.util.List;

/**
 * 好友信息实体类
 * Created by xiao on 2016/11/21.
 */

public class FriendInfo {

    /**
     * Msg :
     * Result : [{"headImg":"","nickName":"","score":"","slogan":"","userId":""}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    /**
     * headImg :
     * nickName :
     * score :
     * slogan :
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
        private String headImg;
        private String nickName;
        private String score;
        private String slogan;
        private String userId;

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getSlogan() {
            return slogan;
        }

        public void setSlogan(String slogan) {
            this.slogan = slogan;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
