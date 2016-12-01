package com.chinalooke.yuwan.bean;

import java.util.List;

public class Advertisement {

    /**
     * Success : true
     * Result : [{"title":"zxcv","content":null,"add_time":"2016-07-28 17:30:51","war_id":"73","game_title":"Dota2","address":"中国河南省洛阳市涧西区郑州路街道青滇路11号","netbar_name":"黑豹网咖1","netbar_id":"3","images":[{"title":"dawd","img":"http://oaqx2e3yr.bkt.clouddn.com/57c53788a8e30.jpg","description":""},{"title":"daw","img":"http://oaqx2e3yr.bkt.clouddn.com/57c5379ba8f9d.jpg","description":""},{"title":"sdaw","img":"http://oaqx2e3yr.bkt.clouddn.com/57c537a799c5e.jpg","description":""},{"title":"daw","img":"http://oaqx2e3yr.bkt.clouddn.com/57c537beacf46.jpg","description":""}]},{"title":"广告1","content":null,"add_time":"2016-08-28 15:50:52","war_id":"73","game_title":"Dota2","address":"中国河南省洛阳市涧西区郑州路街道青滇路11号","netbar_name":"黑豹网咖1","netbar_id":"3","images":[{"title":"图1","img":"http://oaqx2e3yr.bkt.clouddn.com/57c537eb3317d.jpg","description":""}]}]
     * Msg :
     */

    private boolean Success;
    private String Msg;
    /**
     * title : zxcv
     * content : null
     * add_time : 2016-07-28 17:30:51
     * war_id : 73
     * game_title : Dota2
     * address : 中国河南省洛阳市涧西区郑州路街道青滇路11号
     * netbar_name : 黑豹网咖1
     * netbar_id : 3
     * images : [{"title":"dawd","img":"http://oaqx2e3yr.bkt.clouddn.com/57c53788a8e30.jpg","description":""},{"title":"daw","img":"http://oaqx2e3yr.bkt.clouddn.com/57c5379ba8f9d.jpg","description":""},{"title":"sdaw","img":"http://oaqx2e3yr.bkt.clouddn.com/57c537a799c5e.jpg","description":""},{"title":"daw","img":"http://oaqx2e3yr.bkt.clouddn.com/57c537beacf46.jpg","description":""}]
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

    public static class ResultBean {
        private String title;
        private Object content;
        private String add_time;
        private String war_id;
        private String game_title;
        private String address;
        private String netbar_name;
        private String netbar_id;
        /**
         * title : dawd
         * img : http://oaqx2e3yr.bkt.clouddn.com/57c53788a8e30.jpg
         * description :
         */

        private List<ImagesBean> images;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Object getContent() {
            return content;
        }

        public void setContent(Object content) {
            this.content = content;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getWar_id() {
            return war_id;
        }

        public void setWar_id(String war_id) {
            this.war_id = war_id;
        }

        public String getGame_title() {
            return game_title;
        }

        public void setGame_title(String game_title) {
            this.game_title = game_title;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getNetbar_name() {
            return netbar_name;
        }

        public void setNetbar_name(String netbar_name) {
            this.netbar_name = netbar_name;
        }

        public String getNetbar_id() {
            return netbar_id;
        }

        public void setNetbar_id(String netbar_id) {
            this.netbar_id = netbar_id;
        }

        public List<ImagesBean> getImages() {
            return images;
        }

        public void setImages(List<ImagesBean> images) {
            this.images = images;
        }

        public static class ImagesBean {
            private String title;
            private String img;
            private String description;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }
}
