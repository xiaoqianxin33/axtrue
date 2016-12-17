package com.chinalooke.yuwan.bean;

import java.io.Serializable;
import java.util.List;

public class Dynamic implements Serializable{


    /**
     * Msg :
     * Result : {"list":[{"activeId":"176","addTime":"2016-08-31 17:24:27","comments":"0","content":"发动态试试","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472707122","images":"http://oaqx2e3yr.bkt.clouddn.com/Active14726354670","lastComment":[{"addTime":"","commentId":"","content":"","headImg":"","nickName":""},{"addTime":"","commentId":"","content":"","headImg":"","nickName":""}],"likes":"0","nickName":"CD-ROM","source":"16","userId":"1"}]}
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

    public static class ResultBean implements Serializable{
        /**
         * activeId : 176
         * addTime : 2016-08-31 17:24:27
         * comments : 0
         * content : 发动态试试
         * headImg : http://oaqx2e3yr.bkt.clouddn.com/head1472707122
         * images : http://oaqx2e3yr.bkt.clouddn.com/Active14726354670
         * lastComment : [{"addTime":"","commentId":"","content":"","headImg":"","nickName":""},{"addTime":"","commentId":"","content":"","headImg":"","nickName":""}]
         * likes : 0
         * nickName : CD-ROM
         * source : 16
         * userId : 1
         */
        private boolean isUserJoin;

        public boolean isUserJoin() {
            return isUserJoin;
        }

        public void setUserJoin(boolean userJoin) {
            isUserJoin = userJoin;
        }

        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean implements Serializable{
            private String activeId;
            private String addTime;
            private String comments;
            private String content;
            private String headImg;
            private String images;
            private String likes;
            private String nickName;
            private String source;
            private String userId;
            private String address;
            private boolean isLoginUserLike;
            private List<LastCommentBean> lastComment;

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

            /**
             * addTime :
             * commentId :
             * content :
             * headImg :
             * nickName :
             */


            public String getActiveId() {
                return activeId;
            }

            public void setActiveId(String activeId) {
                this.activeId = activeId;
            }

            public String getAddTime() {
                return addTime;
            }

            public void setAddTime(String addTime) {
                this.addTime = addTime;
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

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public List<LastCommentBean> getLastComment() {
                return lastComment;
            }

            public void setLastComment(List<LastCommentBean> lastComment) {
                this.lastComment = lastComment;
            }

            public static class LastCommentBean implements Serializable{
                private String addTime;
                private String commentId;
                private String content;
                private String headImg;
                private String nickName;

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
            }
        }
    }
}
