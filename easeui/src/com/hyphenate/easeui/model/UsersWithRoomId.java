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
     * Result : [{"players":{"userId":"5","nickName":"弑天","phoneNumber":"13015580263"}},{"players":{"userId":"2","nickName":null,"phoneNumber":"18790117059"}},{"players":{"userId":"3","nickName":"跪下给我唱征服","phoneNumber":"13838895007"}},{"players":{"userId":"1","nickName":"本王不退位づ尔等都是臣","phoneNumber":"15088888888"}},{"players":{"userId":"153","nickName":null,"phoneNumber":"18790117057"}},{"players":{"userId":"152","nickName":null,"phoneNumber":"18790117058"}},{"players":{"userId":"155","nickName":null,"phoneNumber":"18790117056"}},{"players":{"userId":"156","nickName":null,"phoneNumber":"15588652455"}}]
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

    public static class ResultBean implements Serializable {
        /**
         * players : {"userId":"5","nickName":"弑天","phoneNumber":"13015580263"}
         */

        private PlayersBean players;

        public PlayersBean getPlayers() {
            return players;
        }

        public void setPlayers(PlayersBean players) {
            this.players = players;
        }

        public static class PlayersBean implements Serializable {
            /**
             * userId : 5
             * nickName : 弑天
             * phoneNumber : 13015580263
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
