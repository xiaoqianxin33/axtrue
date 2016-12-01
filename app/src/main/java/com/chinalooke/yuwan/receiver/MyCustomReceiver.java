package com.chinalooke.yuwan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.chinalooke.yuwan.activity.GameDeskActivity;
import com.chinalooke.yuwan.bean.GameDesk;
import com.chinalooke.yuwan.bean.GameDeskDetails;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * 接收推送消息receiver
 * Created by xiao on 2016/11/19.
 */

public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");
            //获取消息内容
            JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
            receiverZCResult(context, channel, json);
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }

    //接收战场胜负推送时的处理
    private void receiverZCResult(Context context, String channel, JSONObject json) throws JSONException {
        LoginUser.ResultBean resultBean = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(context, LoginUserInfoUtils.KEY);
        if (resultBean != null) {
            String userId = resultBean.getUserId();
            assert channel != null;
            if (channel.equals(userId + "game_result")) {
                String gameDesk = json.getString("gameDesk");
                String gameDeskDetails = json.getString("gameDeskDetails");
                Gson gson = new Gson();
                Type type = new TypeToken<GameDesk>() {
                }.getType();
                Type type1 = new TypeToken<GameDeskDetails>() {
                }.getType();
                GameDesk gameDesk1 = gson.fromJson(gameDesk, type);
                GameDeskDetails gameDeskDetails1 = gson.fromJson(gameDeskDetails, type1);
                Intent intent1 = new Intent(context, GameDeskActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("gameDesk", gameDesk1);
                bundle.putSerializable("gameDeskDetails", gameDeskDetails1);
                intent1.putExtras(bundle);
                intent1.putExtra("isReceiver", true);
                context.startActivity(intent1);
            }
        }
    }
}

