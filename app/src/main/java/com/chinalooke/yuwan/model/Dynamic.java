package com.chinalooke.yuwan.model;

import java.util.List;

public class Dynamic {


    /**
     * Msg :
     * Result : [{"activeId":"33","address":"河南省涧西","comments":"1","content":"开学了，大家都来上学呀哈哈哈哈哈哈","createTime":"2016-09-03 17:56:35","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472707122","images":"http://oaqx2e3yr.bkt.clouddn.com/Active14728965951,http://oaqx2e3yr.bkt.clouddn.com/Active14728965950,http://oaqx2e3yr.bkt.clouddn.com/Active14728965952,http://oaqx2e3yr.bkt.clouddn.com/Active14728965953","lastComment":[{"addTime":"2016-09-04 10:26:16","commentId":"502","content":"Resghh ccvbfbb fcvnkyt","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472707122","nickName":"CD-ROM"}],"likes":"2","nickName":"CD-ROM"},{"activeId":"32","comments":"0","content":"来咯娱乐女可恶可以撒额醉的与他","createTime":"2016-09-01 11:15:19","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472546611","images":"http://oaqx2e3yr.bkt.clouddn.com/Active14726997192,http://oaqx2e3yr.bkt.clouddn.com/Active14726997190,http://oaqx2e3yr.bkt.clouddn.com/Active14726997193,http://oaqx2e3yr.bkt.clouddn.com/Active14726997204,http://oaqx2e3yr.bkt.clouddn.com/Active14726997191,http://oaqx2e3yr.bkt.clouddn.com/Active14726997205","likes":"0","nickName":"哈哈哈"},{"activeId":"31","comments":"0","content":"测试合并代码是否出错","createTime":"2016-08-31 10:28:00","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472455719","images":"","likes":"2","nickName":"DK"},{"activeId":"30","comments":"1","content":"测试合并代码是否出错","createTime":"2016-08-31 10:26:12","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472455719","images":"","lastComment":[{"addTime":"2016-09-02 14:57:25","commentId":"496","content":"宇宛","headImg":"http://121.42.172.61/public/images/defaultAvatar.png","nickName":"南极熊"}],"likes":"1","nickName":"DK"},{"activeId":"29","comments":"0","content":"\u2026","createTime":"2016-08-31 10:19:22","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472455719","images":"","likes":"0","nickName":"DK"},{"activeId":"28","comments":"0","content":"Ghkh","createTime":"2016-08-30 18:48:45","headImg":"http://121.42.172.61/public/images/defaultAvatar.png","images":"","likes":"0","nickName":"123"},{"activeId":"27","comments":"0","content":"Dyhht","createTime":"2016-08-30 16:31:15","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472707122","images":"","likes":"1","nickName":"CD-ROM"},{"activeId":"26","comments":"0","content":"1231231231231321231231231231321321321321321321231321321321","createTime":"2016-08-30 15:22:11","headImg":"http://121.42.172.61/public/images/defaultAvatar.png","images":"","likes":"1","nickName":"123"},{"activeId":"25","comments":"0","content":"删了哈","createTime":"2016-08-30 11:53:15","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472546611","images":"","likes":"1","nickName":"哈哈哈"},{"activeId":"24","comments":"0","content":"接力","createTime":"2016-08-29 17:43:39","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472707122","images":"http://oaqx2e3yr.bkt.clouddn.com/Active14724638190","likes":"1","nickName":"CD-ROM"}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    /**
     * activeId : 33
     * address : 河南省涧西
     * comments : 1
     * content : 开学了，大家都来上学呀哈哈哈哈哈哈
     * createTime : 2016-09-03 17:56:35
     * headImg : http://oaqx2e3yr.bkt.clouddn.com/head1472707122
     * images : http://oaqx2e3yr.bkt.clouddn.com/Active14728965951,http://oaqx2e3yr.bkt.clouddn.com/Active14728965950,http://oaqx2e3yr.bkt.clouddn.com/Active14728965952,http://oaqx2e3yr.bkt.clouddn.com/Active14728965953
     * lastComment : [{"addTime":"2016-09-04 10:26:16","commentId":"502","content":"Resghh ccvbfbb fcvnkyt","headImg":"http://oaqx2e3yr.bkt.clouddn.com/head1472707122","nickName":"CD-ROM"}]
     * likes : 2
     * nickName : CD-ROM
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
        private String address;
        private String comments;
        private String content;
        private String createTime;
        private String headImg;
        private String images;
        private String likes;
        private String nickName;
        private boolean isLoginUserLike;

        public boolean isLoginUserLike() {
            return isLoginUserLike;
        }

        public void setLoginUserLike(boolean loginUserLike) {
            isLoginUserLike = loginUserLike;
        }

        /**
         * addTime : 2016-09-04 10:26:16
         * commentId : 502
         * content : Resghh ccvbfbb fcvnkyt
         * headImg : http://oaqx2e3yr.bkt.clouddn.com/head1472707122
         * nickName : CD-ROM
         */

        private List<LastCommentBean> lastComment;

        public String getActiveId() {
            return activeId;
        }

        public void setActiveId(String activeId) {
            this.activeId = activeId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
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

        public List<LastCommentBean> getLastComment() {
            return lastComment;
        }

        public void setLastComment(List<LastCommentBean> lastComment) {
            this.lastComment = lastComment;
        }

        public static class LastCommentBean {
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
