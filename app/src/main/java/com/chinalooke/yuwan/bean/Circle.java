package com.chinalooke.yuwan.bean;

import java.io.Serializable;
import java.util.List;


public class Circle {


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
        private String groupId;
        private String groupName;
        private String bgImage;
        private String address;
        private String distance;
        private String headImg;
        private String details;
        private String views;
        private String lng;
        private String lat;
        private String createTime;
        private String userId;
        private boolean isUserJoin;

        public boolean isUserJoin() {
            return isUserJoin;
        }

        public void setUserJoin(boolean userJoin) {
            isUserJoin = userJoin;
        }


        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getBgImage() {
            return bgImage;
        }

        public void setBgImage(String bgImage) {
            this.bgImage = bgImage;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getViews() {
            return views;
        }

        public void setViews(String views) {
            this.views = views;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
