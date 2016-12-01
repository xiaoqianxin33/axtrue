package com.chinalooke.yuwan.bean;

import java.io.Serializable;

/**
 * 登录用户信息
 * Created by Administrator on 2016/8/23.
 */
public class UserInfo implements Serializable {

    private String userId;
    private String userType;
    private String headImg;
    private String  nickName;
    private String sex;
    private String age;
    private String  playAge;
    private String address;
    private String slogan;
    private String realName;
    private String  cardNo;
    private String[] gameId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPlayAge() {
        return playAge;
    }

    public void setPlayAge(String playAge) {
        this.playAge = playAge;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String[] getGameId() {
        return gameId;
    }

    public void setGameId(String[] gameId) {
        this.gameId = gameId;
    }
}
