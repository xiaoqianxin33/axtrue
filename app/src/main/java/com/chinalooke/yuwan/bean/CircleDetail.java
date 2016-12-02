package com.chinalooke.yuwan.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 圈子详情实体类
 * Created by xiao on 2016/12/2.
 */

public class CircleDetail implements Serializable {


    /**
     * Msg :
     * Result : {"address":"中国河南省洛阳市涧西区郑州路街道青滇路11号","createTime":"2016-08-26 16:15:10","details":"我们的","games":[{"gameId":"7","gameName":"魔兽争霸","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57c3f6a0267f9.jpg"},{"gameId":"9","gameName":"守望先锋","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57b575f7d0415.jpeg"}],"groupId":"41","groupName":"测试2","headImg":"http://oaqx2e3yr.bkt.clouddn.com/Group1472199309","isUserJoin ":true,"lat":"34.6665","lng":"112.391977","ownerId":"1","userId":"1","views":"2"}
     * Success : true
     */

    private String Msg;
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
        /**
         * address : 中国河南省洛阳市涧西区郑州路街道青滇路11号
         * createTime : 2016-08-26 16:15:10
         * details : 我们的
         * games : [{"gameId":"7","gameName":"魔兽争霸","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57c3f6a0267f9.jpg"},{"gameId":"9","gameName":"守望先锋","thumb":"http://oaqx2e3yr.bkt.clouddn.com/57b575f7d0415.jpeg"}]
         * groupId : 41
         * groupName : 测试2
         * headImg : http://oaqx2e3yr.bkt.clouddn.com/Group1472199309
         * isUserJoin  : true
         * lat : 34.6665
         * lng : 112.391977
         * ownerId : 1
         * userId : 1
         * views : 2
         */

        private String address;
        private String createTime;
        private String details;
        private String groupId;
        private String groupName;
        private String headImg;
        private boolean isUserJoin;
        private String lat;
        private String lng;
        private String ownerId;
        private String userId;
        private String views;
        private List<GamesBean> games;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
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

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public boolean isIsUserJoin() {
            return isUserJoin;
        }

        public void setIsUserJoin(boolean isUserJoin) {
            this.isUserJoin = isUserJoin;
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

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getViews() {
            return views;
        }

        public void setViews(String views) {
            this.views = views;
        }

        public List<GamesBean> getGames() {
            return games;
        }

        public void setGames(List<GamesBean> games) {
            this.games = games;
        }

        public static class GamesBean {
            /**
             * gameId : 7
             * gameName : 魔兽争霸
             * thumb : http://oaqx2e3yr.bkt.clouddn.com/57c3f6a0267f9.jpg
             */

            private String gameId;
            private String gameName;
            private String thumb;

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

            public String getThumb() {
                return thumb;
            }

            public void setThumb(String thumb) {
                this.thumb = thumb;
            }
        }
    }
}
