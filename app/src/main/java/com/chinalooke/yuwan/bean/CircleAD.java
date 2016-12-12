package com.chinalooke.yuwan.bean;

import java.util.List;

/**
 * 圈子顶部广告实体类
 * Created by xiao on 2016/12/12.
 */

public class CircleAD {

    /**
     * Msg :
     * Result : [{"ADImg":["http://oaqx2e3yr.bkt.clouddn.com/57c537d9a573a.jpg"],"addTime":"2016-08-10 01:14:17","description":"12124121251512","title":"广告"},{"ADImg":[null],"addTime":"2016-12-01 20:50:04","title":"这是圈子广告标题"}]
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
         * ADImg : ["http://oaqx2e3yr.bkt.clouddn.com/57c537d9a573a.jpg"]
         * addTime : 2016-08-10 01:14:17
         * description : 12124121251512
         * title : 广告
         */

        private String addTime;
        private String description;
        private String title;
        private List<String> ADImg;

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getADImg() {
            return ADImg;
        }

        public void setADImg(List<String> ADImg) {
            this.ADImg = ADImg;
        }
    }
}
