package com.chinalooke.yuwan.bean;

import java.io.Serializable;

/**
 * 用户信息实体类
 * Created by xiao on 2016/11/15.
 */

public class LoginUser implements Serializable {


    /**
     * Msg :
     * Result : {"address":"河南省洛阳市涧西区","age":"0","cardNo":"430381198910130412","gameId":["7","13","11","8","12","5","9","10"],"headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"小额1","playAge":"1","realName":"肖前欣","sex":"女","slogan":"null","userId":"152","userType":"player"}
     * Success : true
     */

    private String Msg;
    /**
     * address : 河南省洛阳市涧西区
     * age : 0
     * cardNo : 430381198910130412
     * gameId : ["7","13","11","8","12","5","9","10"]
     * headImg : http://121.42.172.61/public/images/defaultAvatar.png
     * nickName : 小额1
     * playAge : 1
     * realName : 肖前欣
     * sex : 女
     * slogan : null
     * userId : 152
     * userType : player
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

    public static class ResultBean implements Serializable{
        private String address;
        private String age;
        private String cardNo;
        private String headImg;
        private String nickName;
        private String playAge;
        private String realName;
        private String sex;
        private String slogan;
        private String userId;
        private String userType;
        private String[] gameId;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardNo) {
            this.cardNo = cardNo;
        }

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

        public String getPlayAge() {
            return playAge;
        }

        public void setPlayAge(String playAge) {
            this.playAge = playAge;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
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

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String[] getGameId() {
            return gameId;
        }

        public void setGameId(String[] gameId) {
            this.gameId = gameId;
        }
    }
}
