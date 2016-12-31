package com.hyphenate.easeui.domain;

import android.util.Log;

import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.db.HxDBManager;
import com.hyphenate.easeui.model.UsersWithRoomId;


public class MyEaseUserProvider implements EaseUI.EaseUserProfileProvider {

    @Override
    public EaseUser getUser(String username) {
        Log.e("TAG", "getUser");
        EaseUser easeUser = null;
        HxDBManager hxDBManager = new HxDBManager(EaseUI.getInstance().getContext());
        UsersWithRoomId.ResultBean.PlayersBean playersBean = hxDBManager.queryByPhone(username);
        if (null != playersBean) {
            easeUser = new EaseUser(username);
            easeUser.setNick(playersBean.getNickName());
        }
        return easeUser;
    }
}
