package com.chinalooke.yuwan.bean;

/**
 * 游戏桌玩家资料实体类
 * Created by xiao on 2016/11/17.
 */

public class DeskUserInfo {


    /**
     * Msg :
     * Result : {"BreakCount":"0","address":"北京市北京市东城区","age":"22","cardNo":"410724199403280040","gameId":"","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472707122","lat":"112.368","lng":"34.6672","loseCount":"5","nickName":"CD-ROM","phone":"15088888888","playAge":"1","realName":"vcdjjnn","score":"100","sex":"女","slogan":"bh","userId":"1","winCount":"10"}
     * Success : true
     */

    private String Msg;
    /**
     * BreakCount : 0
     * address : 北京市北京市东城区
     * age : 22
     * cardNo : 410724199403280040
     * gameId :
     * headImg : http://oaqx2e3yr.bkt.clouddn.com/head1472707122
     * lat : 112.368
     * lng : 34.6672
     * loseCount : 5
     * nickName : CD-ROM
     * phone : 15088888888
     * playAge : 1
     * realName : vcdjjnn
     * score : 100
     * sex : 女
     * slogan : bh
     * userId : 1
     * winCount : 10
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
        private String BreakCount;
        private String address;
        private String age;
        private String cardNo;
        private String gameId;
        private String headImg;
        private String lat;
        private String lng;
        private String loseCount;
        private String nickName;
        private String phone;
        private String playAge;
        private String realName;
        private String score;
        private String sex;
        private String slogan;
        private String userId;
        private String winCount;
        private String sumPlayCount;
        private String level;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getSumPlayCount() {
            return sumPlayCount;
        }

        public void setSumPlayCount(String sumPlayCount) {
            this.sumPlayCount = sumPlayCount;
        }

        public String getBreakCount() {
            return BreakCount;
        }

        public void setBreakCount(String BreakCount) {
            this.BreakCount = BreakCount;
        }

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

        public String getGameId() {
            return gameId;
        }

        public void setGameId(String gameId) {
            this.gameId = gameId;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getLoseCount() {
            return loseCount;
        }

        public void setLoseCount(String loseCount) {
            this.loseCount = loseCount;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
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

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
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

        public String getWinCount() {
            return winCount;
        }

        public void setWinCount(String winCount) {
            this.winCount = winCount;
        }
    }
}
