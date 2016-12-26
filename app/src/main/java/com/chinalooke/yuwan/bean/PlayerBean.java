package com.chinalooke.yuwan.bean;

/**
 * 游戏桌选手实体类
 * Created by xiao on 2016/12/20.
 */

public class PlayerBean {

    private String headImg;
    private boolean isLoser;
    private String status;
    private String userId;
    private String nickName;

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public boolean isLoser() {
        return isLoser;
    }

    public void setLoser(boolean loser) {
        isLoser = loser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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
}
