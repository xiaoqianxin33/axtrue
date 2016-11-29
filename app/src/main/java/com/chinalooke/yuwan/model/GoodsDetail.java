package com.chinalooke.yuwan.model;

import java.util.List;

/**
 * 商品详情实体类
 * Created by xiao on 2016/11/29.
 */

public class GoodsDetail {


    /**
     * Success : true
     * Result : [{"title":"天下激活码1","summary":"天下手游内测激活码","paymoney":"100.00","price":"105.00","sales":"0","details":"&lt;p&gt;厉565656&lt;/p&gt;","url":"http://163.com","images":["./Public/upload/20161125/thumbnail/IMG_0539.JPG","s:47:\"./Public/upload/20161125/thumbnail/IMG_0539.JPG\";"],"add_time":"2016-11-28 11:04:05","goodsId":"3"},{"title":"天下激活码","summary":"天下手游内测激活码","paymoney":"100.00","price":"105.00","sales":"0","details":"&lt;p&gt;厉害444&lt;/p&gt;","url":"http://163.com","images":["./Public/upload/20161125/thumbnail/IMG_0539.JPG","s:47:\"./Public/upload/20161125/thumbnail/IMG_0539.JPG\";"],"add_time":"2016-11-28 11:03:49","goodsId":"2"},{"title":"天下激活码","summary":"天下手游内测激活码","paymoney":"100.00","price":"105.00","sales":"4","details":"<span>厉害<\/span>","url":"http://163.com","images":["http://picm.photophoto.cn/005/008/007/0080071498.jpg","http://121.42.172.61/public/images/defaultAvatar.png","http://121.42.172.61/public/images/defaultAvatar.png","http://121.42.172.61/public/images/defaultAvatar.png"],"add_time":"2016-11-25 16:14:17","goodsId":"1"}]
     * Msg :
     */

    private boolean Success;
    private String Msg;
    /**
     * title : 天下激活码1
     * summary : 天下手游内测激活码
     * paymoney : 100.00
     * price : 105.00
     * sales : 0
     * details : &lt;p&gt;厉565656&lt;/p&gt;
     * url : http://163.com
     * images : ["./Public/upload/20161125/thumbnail/IMG_0539.JPG","s:47:\"./Public/upload/20161125/thumbnail/IMG_0539.JPG\";"]
     * add_time : 2016-11-28 11:04:05
     * goodsId : 3
     */

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
        private String title;
        private String summary;
        private String paymoney;
        private String price;
        private String sales;
        private String details;
        private String url;
        private String add_time;
        private String goodsId;
        private List<String> images;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getPaymoney() {
            return paymoney;
        }

        public void setPaymoney(String paymoney) {
            this.paymoney = paymoney;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getSales() {
            return sales;
        }

        public void setSales(String sales) {
            this.sales = sales;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }
}
