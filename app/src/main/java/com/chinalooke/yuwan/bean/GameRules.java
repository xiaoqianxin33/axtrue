package com.chinalooke.yuwan.bean;

import java.util.List;

/**
 * 游戏规则实体类
 * Created by xiaoqianxin on 2016/12/29.
 */

public class GameRules {

    /**
     * Success : true
     * Result : [{"ruleId":"","title":"","Content":""}]
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
         * ruleId :
         * title :
         * Content :
         */

        private String ruleId;
        private String title;
        private String Content;

        public String getRuleId() {
            return ruleId;
        }

        public void setRuleId(String ruleId) {
            this.ruleId = ruleId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String Content) {
            this.Content = Content;
        }
    }
}
