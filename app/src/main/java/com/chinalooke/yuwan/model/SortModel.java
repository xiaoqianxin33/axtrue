package com.chinalooke.yuwan.model;

import java.io.Serializable;

/**
 * friends字母排列实体类
 * Created by xiao on 2016/11/21.
 */

public class SortModel implements Serializable {

    private FriendsList.ResultBean mFriends;
    private FriendInfo.ResultBean friend;   //显示的数据
    private String sortLetters;  //显示数据拼音的首字母

    public FriendsList.ResultBean getFriends() {
        return mFriends;
    }

    public void setFriends(FriendsList.ResultBean friends) {
        mFriends = friends;
    }

    public FriendInfo.ResultBean getFriend() {
        return friend;
    }

    public void setFriend(FriendInfo.ResultBean friend) {
        this.friend = friend;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
