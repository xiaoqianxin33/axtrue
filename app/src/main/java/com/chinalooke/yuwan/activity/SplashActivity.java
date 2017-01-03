package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

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
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.zhy.autolayout.AutoLayoutActivity;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;

public class SplashActivity extends AutoLayoutActivity {

    private RequestQueue mQueue;
    private MyHandler mMyHandler = new MyHandler(this);
    private DBManager mDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mQueue = YuwanApplication.getQueue();
        mDbManager = new DBManager(SplashActivity.this);
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
        getLevelList();
        mMyHandler.sendEmptyMessageDelayed(1, 2000);
    }

    //查询所有积分级别
    private void getLevelList() {
        String url = Constant.HOST + "getlevelList";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AnalysisJSON.analysisJson(response)) {
                    PreferenceUtils.setPrefString(getApplicationContext(), "level", response);
                }
            }
        }, null);
        mQueue.add(request);
    }

    private void getGameMessage() {
        String uri = Constant.HOST + "getGameList";
        StringRequest stringRequest = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AnalysisJSON.analysisJson(response)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<GameMessage>() {
                    }.getType();
                    GameMessage mGameMessage = gson.fromJson(response, type);
                    if (mGameMessage != null) {
                        List<GameMessage.ResultBean> result = mGameMessage.getResult();
                        if (result != null && result.size() != 0) {
                            mDbManager.add(result);
                            mDbManager.closeDB();
                        }
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

    private static class MyHandler extends Handler {
        WeakReference<SplashActivity> mActivity;

        MyHandler(SplashActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity theActivity = mActivity.get();
            switch (msg.what) {
                case 1:
                    theActivity.startActivity(new Intent(theActivity, MainActivity.class));
                    theActivity.finish();
                    break;
            }
        }
    }
}
