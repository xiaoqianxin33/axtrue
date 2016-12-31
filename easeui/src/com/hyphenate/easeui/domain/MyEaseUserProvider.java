package com.hyphenate.easeui.domain;

import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.db.HxDBManager;
import com.hyphenate.easeui.model.UsersWithRoomId;


public class MyEaseUserProvider implements EaseUI.EaseUserProfileProvider {

    @Override
    public EaseUser getUser(String username) {
        EaseUser easeUser = null;
        HxDBManager hxDBManager = new HxDBManager(EaseUI.getInstance().getContext());
        UsersWithRoomId.ResultBean.PlayersBean playersBean = hxDBManager.queryByPhone(username);
        hxDBManager.closeDB();
        if (null != playersBean) {
            easeUser = new EaseUser(username);
            easeUser.setNick(playersBean.getNickName());
        }
        return easeUser;
    }
}
