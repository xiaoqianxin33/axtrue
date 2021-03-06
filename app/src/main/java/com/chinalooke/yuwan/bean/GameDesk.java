package com.chinalooke.yuwan.bean;

import java.io.Serializable;
import java.util.List;


public class GameDesk implements Serializable {


    /**
     * Success : true
     * Result : [{"ownerName":"笑笑","gamePay":"100.00","gameDeskId":"13","gameName":"游戏002","netBarId":"3","gameImage":"http://oaqx2e3yr.bkt.clouddn.com/57ac211a68dc4.jpg","startTime":"2016-09-06 00:00:00","netBarName":"黑豹网咖1"},{"ownerName":"笑笑","gamePay":"100.00","gameDeskId":"21","gameName":"游戏002","netBarId":"3","gameImage":"http://oaqx2e3yr.bkt.clouddn.com/57ac211a68dc4.jpg","startTime":"2016-09-06 00:00:00","netBarName":"黑豹网咖1"},{"ownerName":null,"gamePay":"310.00","gameDeskId":"22","gameName":"游戏002","netBarId":"2","gameImage":"http://oaqx2e3yr.bkt.clouddn.com/57ac211a68dc4.jpg","startTime":"2016-08-08 03:28:00","netBarName":null},{"ownerName":null,"gamePay":"310.00","gameDeskId":"23","gameName":"游戏002","netBarId":"2","gameImage":"http://oaqx2e3yr.bkt.clouddn.com/57ac211a68dc4.jpg","startTime":"2016-08-08 03:28:00","netBarName":null},{"ownerName":null,"gamePay":"0.00","gameDeskId":"24","gameName":"游戏002","netBarId":"2","gameImage":"http://oaqx2e3yr.bkt.clouddn.com/57ac211a68dc4.jpg","startTime":"2016-08-08 03:28:00","netBarName":null},{"ownerName":null,"gamePay":"110.00","gameDeskId":"25","gameName":"游戏002","netBarId":"3","gameImage":"http://oaqx2e3yr.bkt.clouddn.com/57ac211a68dc4.jpg","startTime":"2016-08-08 03:28:00","netBarName":"黑豹网咖1"},{"ownerName":null,"gamePay":"310.00","gameDeskId":"26","gameName":"游戏002","netBarId":"3","gameImage":"http://oaqx2e3yr.bkt.clouddn.com/57ac211a68dc4.jpg","startTime":"2016-08-08 03:28:00","netBarName":"黑豹网咖1"},{"ownerName":null,"gamePay":"40.00","gameDeskId":"27","gameName":"Dota2","netBarId":null,"gameImage":"http://oaqx2e3yr.bkt.clouddn.com/57b57627b1f64.jpg","startTime":"0000-00-00 00:00:00","netBarName":null},{"ownerName":null,"gamePay":"310.00","gameDeskId":"28","gameName":"游戏002","netBarId":"3","gameImage":"http://oaqx2e3yr.bkt.clouddn.com/57ac211a68dc4.jpg","startTime":"2016-08-08 03:28:00","netBarName":"黑豹网咖1"},{"ownerName":null,"gamePay":"310.00","gameDeskId":"29","gameName":null,"netBarId":"3","gameImage":null,"startTime":"2016-08-08 03:28:00","netBarName":"黑豹网咖1"},{"ownerName":null,"gamePay":"310.00","gameDeskId":"30","gameName":null,"netBarId":"3","gameImage":null,"startTime":"2016-08-08 03:28:00","netBarName":"黑豹网咖1"},{"ownerName":"笑笑","gamePay":"310.00","gameDeskId":"31","gameName":null,"netBarId":"3","gameImage":null,"startTime":"2016-08-08 03:28:00","netBarName":"黑豹网咖1"},{"ownerName":null,"gamePay":"310.00","gameDeskId":"32","gameName":null,"netBarId":"3","gameImage":null,"startTime":"2016-08-08 03:28:00","netBarName":"黑豹网咖1"},{"ownerName":null,"gamePay":"310.00","gameDeskId":"33","gameName":null,"netBarId":"3","gameImage":null,"startTime":"2016-08-08 03:28:00","netBarName":"黑豹网咖1"},{"ownerName":null,"gamePay":"210.00","gameDeskId":"34","gameName":null,"netBarId":"3","gameImage":null,"startTime":"2016-08-08 03:28:00","netBarName":"黑豹网咖1"},{"ownerName":"","gamePay":"310.00","gameDeskId":"35","gameName":null,"netBarId":"0","gameImage":null,"startTime":"2016-08-08 03:28:00","netBarName":null},{"ownerName":"笑笑","gamePay":"310.00","gameDeskId":"36","gameName":null,"netBarId":"0","gameImage":null,"startTime":"2016-08-08 03:28:00","netBarName":null}]
     * Msg :
     */

    private boolean Success;
    private String Msg;
    /**
     * ownerName : 笑笑
     * gamePay : 100.00
     * gameDeskId : 13
     * gameName : 游戏002
     * netBarId : 3
     * gameImage : http://oaqx2e3yr.bkt.clouddn.com/57ac211a68dc4.jpg
     * startTime : 2016-09-06 00:00:00
     * netBarName : 黑豹网咖1
     */

    private List<ResultBean> Result;

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public List<ResultBean> getResult() {
        return Result;
    }

    public void setResult(List<ResultBean> Result) {
        this.Result = Result;
    }

    public static class ResultBean implements Serializable {
        private String ownerName;
        private String gamePay;
        private String gameDeskId;
        private String gameName;
        private String netBarId;
        private String gameImage;
        private String startTime;
        private String netBarName;
        private String winnerTeam;
        private String curPlayNum;
        private String playerNum;
        private String cup;
        private String ownerId;
        private boolean isUserWin;
        private List<Winer> winers;

        public List<Winer> getWiners() {
            return winers;
        }

        public void setWiners(List<Winer> winers) {
            this.winers = winers;
        }

        public boolean isUserWin() {
            return isUserWin;
        }

        public void setUserWin(boolean userWin) {
            isUserWin = userWin;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        public String getCup() {
            return cup;
        }

        public void setCup(String cup) {
            this.cup = cup;
        }

        public String getWinnerTeam() {
            return winnerTeam;
        }

        public void setWinnerTeam(String winnerTeam) {
            this.winnerTeam = winnerTeam;
        }

        public String getCurPlayNum() {
            return curPlayNum;
        }

        public void setCurPlayNum(String curPlayNum) {
            this.curPlayNum = curPlayNum;
        }

        public String getPlayerNum() {
            return playerNum;
        }

        public void setPlayerNum(String playerNum) {
            this.playerNum = playerNum;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }

        public String getGamePay() {
            return gamePay;
        }

        public void setGamePay(String gamePay) {
            this.gamePay = gamePay;
        }

        public String getGameDeskId() {
            return gameDeskId;
        }

        public void setGameDeskId(String gameDeskId) {
            this.gameDeskId = gameDeskId;
        }

        public String getGameName() {
            return gameName;
        }

        public void setGameName(String gameName) {
            this.gameName = gameName;
        }

        public String getNetBarId() {
            return netBarId;
        }

        public void setNetBarId(String netBarId) {
            this.netBarId = netBarId;
        }

        public String getGameImage() {
            return gameImage;
        }

        public void setGameImage(String gameImage) {
            this.gameImage = gameImage;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getNetBarName() {
            return netBarName;
        }

        public void setNetBarName(String netBarName) {
            this.netBarName = netBarName;
        }

        public static class Winer implements Serializable {

            private String userId;
            private String rating;
            private String nickName;
            private String money;

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getRating() {
                return rating;
            }

            public void setRating(String rating) {
                this.rating = rating;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getMoney() {
                return money;
            }

            public void setMoney(String money) {
                this.money = money;
            }
        }
    }
}
