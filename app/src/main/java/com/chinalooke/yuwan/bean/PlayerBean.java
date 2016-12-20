package com.chinalooke.yuwan.bean;

import java.util.List;

/**
 * 游戏桌选手实体类
 * Created by xiao on 2016/12/20.
 */

public class PlayerBean {

    private List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> mLeftBeen;
    private List<GameDeskDetails.ResultBean.PlayersBean.RightBean> mRightBeen;

    public List<GameDeskDetails.ResultBean.PlayersBean.RightBean> getRightBeen() {
        return mRightBeen;
    }

    public void setRightBeen(List<GameDeskDetails.ResultBean.PlayersBean.RightBean> rightBeen) {
        mRightBeen = rightBeen;
    }

    public List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> getLeftBeen() {
        return mLeftBeen;
    }

    public void setLeftBeen(List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> leftBeen) {
        mLeftBeen = leftBeen;
    }


}
