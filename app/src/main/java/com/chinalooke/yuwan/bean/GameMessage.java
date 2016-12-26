package com.chinalooke.yuwan.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 游戏信息实体类
 * Created by xiao on 2016/9/19.
 */
public class GameMessage implements Serializable {

    /**
     * Msg :
     * Result : [{"gameId":"5","maxPeopleNumber":"10","name":"魔兽世界","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57c3f5ded1475.jpg","times":"5","wagerMax":"500.00","wagerMin":"100.00"},{"gameId":"13","maxPeopleNumber":"20","name":"风暴英雄","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57c3f8ea8bf5e.jpg","times":"2","wagerMax":"1000.00","wagerMin":"10.00"},{"gameId":"12","maxPeopleNumber":"2","name":"炉石传说","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57c3f7e6705c7.jpg","times":"10","wagerMax":"20.00","wagerMin":"1.00"},{"gameId":"11","maxPeopleNumber":"20","name":"使命召唤online","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57c3f79a87031.jpg","times":"1","wagerMax":"100.00","wagerMin":"2.00"},{"gameId":"10","maxPeopleNumber":"10","name":"Dota2","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57b57627b1f64.jpg","times":"5","wagerMax":"1000.00","wagerMin":"200.00"},{"gameId":"9","maxPeopleNumber":"12","name":"守望先锋","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57b575f7d0415.jpeg","times":"5","wagerMax":"500.00","wagerMin":"300.00"},{"gameId":"8","maxPeopleNumber":"10","name":"斗地主","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57c3f71d32875.jpg","times":"5","wagerMax":"300.00","wagerMin":"100.00"},{"gameId":"7","maxPeopleNumber":"10","name":"魔兽争霸","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57c3f6a0267f9.jpg","times":"5","wagerMax":"400.00","wagerMin":"200.00"},{"gameId":"6","maxPeopleNumber":"6","name":"LOL英雄联盟","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57c3f41d1be79.jpg","times":"10","wagerMax":"200.00","wagerMin":"100.00"},{"gameId":"14","maxPeopleNumber":"10","name":"星际争霸2","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57c3f90ae7aa3.jpg","times":"6","wagerMax":"200.00","wagerMin":"5.00"}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    /**
     * gameId : 5
     * maxPeopleNumber : 10
     * name : 魔兽世界
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

    public static class ResultBean implements Serializable {
        private String gameId;
        private String maxPeopleNumber;
        private String name;
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

        public String getMaxPeopleNumber() {
            return maxPeopleNumber;
        }

        public void setMaxPeopleNumber(String maxPeopleNumber) {
            this.maxPeopleNumber = maxPeopleNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
