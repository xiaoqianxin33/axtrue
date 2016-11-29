package com.chinalooke.yuwan.model;

import java.util.List;

/**
 * 商城商品实体类
 * Created by xiao on 2016/11/29.
 */

public class Goods {


    /**
     * Msg :
     * Result : [{"add_time":"2016-11-28 11:04:05","goodsId":"3","image":"./Public/upload/20161125/thumbnail/IMG_0539.JPG","paymoney":"100.00","price":"105.00","sales":"0","summary":"天下手游内测激活码","title":"天下激活码1"},{"add_time":"2016-11-28 11:03:49","goodsId":"2","image":"./Public/upload/20161125/thumbnail/IMG_0539.JPG","paymoney":"100.00","price":"105.00","sales":"0","summary":"天下手游内测激活码","title":"天下激活码"},{"add_time":"2016-11-25 16:14:17","goodsId":"1","image":"http://picm.photophoto.cn/005/008/007/0080071498.jpg","paymoney":"100.00","price":"105.00","sales":"4","summary":"天下手游内测激活码","title":"天下激活码"}]
     * Success : true
     */

    private String Msg;
    private boolean Success;
    /**
     * add_time : 2016-11-28 11:04:05
     * goodsId : 3
     * image : ./Public/upload/20161125/thumbnail/IMG_0539.JPG
     * paymoney : 100.00
     * price : 105.00
     * sales : 0
     * summary : 天下手游内测激活码
     * title : 天下激活码1
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
        private String add_time;
        private String goodsId;
        private String image;
        private String paymoney;
        private String price;
        private String sales;
        private String summary;
        private String title;

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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
