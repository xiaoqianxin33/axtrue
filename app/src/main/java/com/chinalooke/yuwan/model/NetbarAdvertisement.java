package com.chinalooke.yuwan.model;

import java.util.List;

/**
 * 全市网吧广告实体类
 * Created by xiao on 2016/11/26.
 */

public class NetbarAdvertisement {

    /**
     * Msg :
     * Result : [{"ADImg":["http://oaqx2e3yr.bkt.clouddn.com/57c53788a8e30.jpg","http://oaqx2e3yr.bkt.clouddn.com/57c5379ba8f9d.jpg","http://oaqx2e3yr.bkt.clouddn.com/57c537a799c5e.jpg","http://oaqx2e3yr.bkt.clouddn.com/57c537beacf46.jpg"],"addTime":"","description":"","gameDeskId":"","gameName":"","netbarAddress":"","netbarId":"","netbarName":"","title":"zxcv"},{"ADImg":["http://oaqx2e3yr.bkt.clouddn.com/57c537eb3317d.jpg"],"title":"广告1"}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    /**
     * ADImg : ["http://oaqx2e3yr.bkt.clouddn.com/57c53788a8e30.jpg","http://oaqx2e3yr.bkt.clouddn.com/57c5379ba8f9d.jpg","http://oaqx2e3yr.bkt.clouddn.com/57c537a799c5e.jpg","http://oaqx2e3yr.bkt.clouddn.com/57c537beacf46.jpg"]
     * addTime :
     * description :
     * gameDeskId :
     * gameName :
     * netbarAddress :
     * netbarId :
     * netbarName :
     * title : zxcv
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
        private String addTime;
        private String description;
        private String gameDeskId;
        private String gameName;
        private String netbarAddress;
        private String netbarId;
        private String netbarName;
        private String title;
        private List<String> ADImg;

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public String getNetbarAddress() {
            return netbarAddress;
        }

        public void setNetbarAddress(String netbarAddress) {
            this.netbarAddress = netbarAddress;
        }

        public String getNetbarId() {
            return netbarId;
        }

        public void setNetbarId(String netbarId) {
            this.netbarId = netbarId;
        }

        public String getNetbarName() {
            return netbarName;
        }

        public void setNetbarName(String netbarName) {
            this.netbarName = netbarName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getADImg() {
            return ADImg;
        }

        public void setADImg(List<String> ADImg) {
            this.ADImg = ADImg;
        }
    }
}
