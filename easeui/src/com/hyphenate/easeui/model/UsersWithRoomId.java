package com.hyphenate.easeui.model;

import java.io.Serializable;
import java.util.List;

/**
 * 聊天室玩家信息实体类
 * Created by xiaoqianxin on 2016/12/30.
 */

public class UsersWithRoomId implements Serializable {


    /**
     * Success : true
     * Result : [{"players":{"userId":"238","nickName":"5850916686938","phoneNumber":"15588352355"}},{"players":{"userId":"219","nickName":"前欣老哥","phoneNumber":"15874275350"}},{"players":{"userId":"221","nickName":"砖砖见1","phoneNumber":"18860230170"}}]
     * Msg :
     */

    private boolean Success;
    private String Msg;
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
        /**
         * players : {"userId":"238","nickName":"5850916686938","phoneNumber":"15588352355"}
         */

        private PlayersBean players;

        public PlayersBean getPlayers() {
            return players;
        }

        public void setPlayers(PlayersBean players) {
            this.players = players;
        }

        public static class PlayersBean {
            /**
             * userId : 238
             * nickName : 5850916686938
             * phoneNumber : 15588352355
             */

            private String userId;
            private String nickName;
            private String phoneNumber;

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getPhoneNumber() {
                return phoneNumber;
            }

            public void setPhoneNumber(String phoneNumber) {
                this.phoneNumber = phoneNumber;
            }
        }
    }
}
