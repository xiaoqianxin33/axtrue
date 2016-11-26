package com.chinalooke.yuwan.model;

import java.util.List;

/**
 * 全局动态实体类
 * Created by xiao on 2016/11/26.
 */

public class WholeDynamic {

    /**
     * Msg :
     * Result : [{"activeId":"16","comments":"0","content":"hhahahahahah","createTime":"2016-06-06 00:00:00","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472707122","images":"http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200","isLoginUserLike":true,"likes":"2","nickName":"CD-ROM","userId":"1"},{"activeId":"18","comments":"0","content":"hhahahahahah","createTime":"2016-06-06 00:00:00","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472707122","images":"http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200","isLoginUserLike":false,"likes":"2","nickName":"CD-ROM","userId":"1"},{"activeId":"4","comments":"5","content":"HelloiOS","createTime":"2008-08-08 00:00:00","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472707122","images":"http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200","isLoginUserLike":true,"lastComment":[{"addTime":"2016-08-24 10:53:52","commentId":"459","content":"2","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472707122","nickName":"CD-ROM"},{"addTime":"2016-08-24 10:53:45","commentId":"458","content":"1","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472707122","nickName":"CD-ROM"}],"likes":"2","nickName":"CD-ROM","userId":"1"}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    /**
     * activeId : 16
     * comments : 0
     * content : hhahahahahah
     * createTime : 2016-06-06 00:00:00
     * headImg : http://oaqx2e3yr.bkt.clouddn.com/head1472707122
     * images : http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200,http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200
     * isLoginUserLike : true
     * likes : 2
     * nickName : CD-ROM
     * userId : 1
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
        private String activeId;
        private String comments;
        private String content;
        private String createTime;
        private String headImg;
        private String images;
        private boolean isLoginUserLike;
        private String likes;
        private String nickName;
        private String userId;
        private String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public boolean isLoginUserLike() {
            return isLoginUserLike;
        }

        public void setLoginUserLike(boolean loginUserLike) {
            isLoginUserLike = loginUserLike;
        }

        public String getActiveId() {
            return activeId;
        }

        public void setActiveId(String activeId) {
            this.activeId = activeId;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public String getImages() {
            return images;
        }

        public void setImages(String images) {
            this.images = images;
        }

        public boolean isIsLoginUserLike() {
            return isLoginUserLike;
        }

        public void setIsLoginUserLike(boolean isLoginUserLike) {
            this.isLoginUserLike = isLoginUserLike;
        }

        public String getLikes() {
            return likes;
        }

        public void setLikes(String likes) {
            this.likes = likes;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
