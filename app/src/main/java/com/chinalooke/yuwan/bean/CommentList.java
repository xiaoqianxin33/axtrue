package com.chinalooke.yuwan.bean;

import java.util.List;

/**
 * 评论列表实体类
 * Created by xiao on 2016/11/28.
 */

public class CommentList {


    /**
     * Msg :
     * Result : [{"addTime":"2016-12-23 16:01:34","commentId":"534","content":"来一个","headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"肖前欣","replayName":""},{"addTime":"2016-12-23 16:09:01","commentId":"535","content":"厕所","headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"肖前欣","replayName":"肖前欣"},{"addTime":"2016-12-23 16:10:01","commentId":"536","content":"回复的回复改了没有","headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"肖前欣","replayName":"肖前欣"},{"addTime":"2016-12-23 16:17:07","commentId":"537","content":"谷歌","headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"肖前欣","replayName":"肖前欣"},{"addTime":"2016-12-23 16:17:37","commentId":"538","content":"这个？","headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"肖前欣","replayName":"肖前欣"}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
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
        /**
         * addTime : 2016-12-23 16:01:34
         * commentId : 534
         * content : 来一个
         * headImg : http://121.42.172.61/public/images/defaultAvatar.png
         * nickName : 肖前欣
         * replayName :
         */

        private String addTime;
        private String commentId;
        private String content;
        private String headImg;
        private String nickName;
        private String replayName;

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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

        public String getReplayName() {
            return replayName;
        }

        public void setReplayName(String replayName) {
            this.replayName = replayName;
        }
    }
}
