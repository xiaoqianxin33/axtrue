package com.chinalooke.yuwan.bean;

import java.util.List;

/**
 * 游戏分类列表实体类
 * Created by xiao on 2016/12/9.
 */

public class GameType {

    /**
     * Msg :
     * Result : [{"gameTypeId":"1","gameTypeName":"网游","typeLevel":"1","upTypeId":"0"},{"gameTypeId":"2","gameTypeName":"手游","typeLevel":"1","upTypeId":"0"},{"gameTypeId":"3","gameTypeName":"FPS","typeLevel":"2","upTypeId":"1"},{"gameTypeId":"4","gameTypeName":"竞技","typeLevel":"2","upTypeId":"1"}]
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
         * gameTypeId : 1
         * gameTypeName : 网游
         * typeLevel : 1
         * upTypeId : 0
         */

        private String gameTypeId;
        private String gameTypeName;
        private String typeLevel;
        private String upTypeId;

        public String getGameTypeId() {
            return gameTypeId;
        }

        public void setGameTypeId(String gameTypeId) {
            this.gameTypeId = gameTypeId;
        }

        public String getGameTypeName() {
            return gameTypeName;
        }

        public void setGameTypeName(String gameTypeName) {
            this.gameTypeName = gameTypeName;
        }

        public String getTypeLevel() {
            return typeLevel;
        }

        public void setTypeLevel(String typeLevel) {
            this.typeLevel = typeLevel;
        }

        public String getUpTypeId() {
            return upTypeId;
        }

        public void setUpTypeId(String upTypeId) {
            this.upTypeId = upTypeId;
        }
    }
}
