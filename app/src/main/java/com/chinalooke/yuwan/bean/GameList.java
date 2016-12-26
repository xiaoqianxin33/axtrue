package com.chinalooke.yuwan.bean;

import java.util.List;

/**
 * Created by xiao on 2016/9/19.
 */
public class GameList {

    /**
     * Msg :
     * Result : [{"gameId":"5","gameName":"魔兽世界","maxPeopleNumber":"10","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57c3f5ded1475.jpg","times":"5","wagerMax":"500.00","wagerMin":"100.00"},{"gameId":"7","gameName":"魔兽争霸","maxPeopleNumber":"10","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57c3f6a0267f9.jpg","times":"5","wagerMax":"400.00","wagerMin":"200.00"}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    /**
     * gameId : 5
     * gameName : 魔兽世界
     * maxPeopleNumber : 10
     * thumb : http://oaqx2e3yr.bkt.clouddn.com/57c3f5ded1475.jpg
     * times : 5
     * wagerMax : 500.00
     * wagerMin : 100.00
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
        private String gameId;
        private String gameName;
        private String maxPeopleNumber;
        private String thumb;
        private String times;
        private String wagerMax;
        private String wagerMin;

        public String getGameId() {
            return gameId;
        }

        public void setGameId(String gameId) {
            this.gameId = gameId;
        }

        public String getGameName() {
            return gameName;
        }

        public void setGameName(String gameName) {
            this.gameName = gameName;
        }

        public String getMaxPeopleNumber() {
            return maxPeopleNumber;
        }

        public void setMaxPeopleNumber(String maxPeopleNumber) {
            this.maxPeopleNumber = maxPeopleNumber;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getTimes() {
            return times;
        }

        public void setTimes(String times) {
            this.times = times;
        }

        public String getWagerMax() {
            return wagerMax;
        }

        public void setWagerMax(String wagerMax) {
            this.wagerMax = wagerMax;
        }

        public String getWagerMin() {
            return wagerMin;
        }

        public void setWagerMin(String wagerMin) {
            this.wagerMin = wagerMin;
        }
    }
}
