package com.chinalooke.yuwan.model;

import java.util.List;

/**
 * 圈子排名实体类
 * Created by xiao on 2016/11/25.
 */

public class CircleRanking {

    /**
     * Msg :
     * Result : [{"groupName":"","headImg":"","loginUserRanking":"","nickName":"","ranking":"","score":"","slogan":"","userId":""}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    /**
     * groupName :
     * headImg :
     * loginUserRanking :
     * nickName :
     * ranking :
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
        private String groupName;
        private String headImg;
        private String loginUserRanking;
        private String nickName;
        private String ranking;
        private String score;
        private String slogan;
        private String userId;

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public String getLoginUserRanking() {
            return loginUserRanking;
        }

        public void setLoginUserRanking(String loginUserRanking) {
            this.loginUserRanking = loginUserRanking;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getRanking() {
            return ranking;
        }

        public void setRanking(String ranking) {
            this.ranking = ranking;
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
