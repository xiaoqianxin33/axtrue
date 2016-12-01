package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.GameMessage;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.db.DBManager;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;

import java.lang.reflect.Type;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mQueue = YuwanApplication.getQueue();
        getGameMessage();
        LoginUser.ResultBean user = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(this, LoginUserInfoUtils.KEY);
        if (user != null) {
            LoginUserInfoUtils.getLoginUserInfoUtils().setUserInfo(user);
        }

        //环信load本地对话和群组
        EMClient.getInstance().chatManager().loadAllConversations();
        EMClient.getInstance().groupManager().loadAllGroups();
        //leanCloud推送服务
        AVInstallation.getCurrentInstallation().saveInBackground();
        PushService.setDefaultPushCallback(this, MainActivity.class);
        mHandler.sendEmptyMessageDelayed(1, 2000);
    }

    private void getGameMessage() {
        String uri = Constant.HOST + "getGameList";
        StringRequest stringRequest = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String substring = response.substring(11, 15);
                if ("true".equals(substring)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<GameMessage>() {
                    }.getType();
                    GameMessage mGameMessage = gson.fromJson(response, type);
                    if (mGameMessage != null) {
                        List<GameMessage.ResultBean> result = mGameMessage.getResult();
                        DBManager dbManager = new DBManager(SplashActivity.this);
                        dbManager.add(result);
                        dbManager.closeDB();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(stringRequest);
    }
}
