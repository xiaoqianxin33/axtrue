package com.chinalooke.yuwan.model;

import java.util.List;

/**
 * 评论列表实体类
 * Created by xiao on 2016/11/28.
 */

public class CommentList {

    /**
     * Msg :
     * Result : [{"addTime":"","commentId":"","content":"","headImg":"","nickName":"","replies":[{"content":"","nickName":"","replyTime":"","userId":""}]}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    /**
     * addTime :
     * commentId :
     * content :
     * headImg :
     * nickName :
     * replies : [{"content":"","nickName":"","replyTime":"","userId":""}]
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
        private String commentId;
        private String content;
        private String headImg;
        private String nickName;
        /**
         * content :
         * nickName :
         * replyTime :
         * userId :
         */

        private List<RepliesBean> replies;

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

        public List<RepliesBean> getReplies() {
            return replies;
        }

        public void setReplies(List<RepliesBean> replies) {
            this.replies = replies;
        }

        public static class RepliesBean {
            private String content;
            private String nickName;
            private String replyTime;
            private String userId;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getReplyTime() {
                return replyTime;
            }

            public void setReplyTime(String replyTime) {
                this.replyTime = replyTime;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }
        }
    }
}
