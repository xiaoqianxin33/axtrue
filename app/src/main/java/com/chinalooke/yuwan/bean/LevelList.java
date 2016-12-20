package com.chinalooke.yuwan.bean;

import java.util.List;

/**
 * 积分级别实体类
 * Created by xiao on 2016/12/20.
 */

public class LevelList {


    /**
     * Msg :
     * Result : [{"least":"0","levelId":"1","levelName":"1","max":"500"},{"least":"500","levelId":"2","levelName":"2","max":"1000"},{"least":"1000","levelId":"3","levelName":"3","max":"1500"},{"least":"1500","levelId":"4","levelName":"4","max":"2000"},{"least":"2000","levelId":"5","levelName":"5","max":"2500"},{"least":"2500","levelId":"6","levelName":"6","max":"3000"},{"least":"3000","levelId":"7","levelName":"7","max":"3500"},{"least":"20000","levelId":"8","levelName":"8","max":"25000"}]
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
         * least : 0
         * levelId : 1
         * levelName : 1
         * max : 500
         */

        private String least;
        private String levelId;
        private String levelName;
        private String max;

        public String getLeast() {
            return least;
        }

        public void setLeast(String least) {
            this.least = least;
        }

        public String getLevelId() {
            return levelId;
        }

        public void setLevelId(String levelId) {
            this.levelId = levelId;
        }

        public String getLevelName() {
            return levelName;
        }

        public void setLevelName(String levelName) {
            this.levelName = levelName;
        }

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }
    }
}
