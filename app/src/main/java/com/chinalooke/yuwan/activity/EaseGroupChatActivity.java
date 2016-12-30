package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.google.gson.Gson;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.model.UsersWithRoomId;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.Serializable;
import java.util.List;

public class EaseGroupChatActivity extends AutoLayoutActivity {

    private String mGroupId;
    private RequestQueue mQueue;
    private List<UsersWithRoomId.ResultBean> mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ease_group_chat);
        mQueue = YuwanApplication.getQueue();
        initData();
    }

    private void initView() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        EaseChatFragment easeChatFragment = new EaseChatFragment();
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
        args.putString(EaseConstant.EXTRA_USER_ID, mGroupId);
        if (mResult != null)
            args.putSerializable(EaseConstant.EXTRA_NICKNAME, (Serializable) mResult);
        easeChatFragment.setArguments(args);
        supportFragmentManager.beginTransaction().replace(R.id.activity_ease_group_chat, easeChatFragment).commit();

    }

    private void initData() {
        mGroupId = getIntent().getStringExtra("groupId");
        getuserswithroomid();
    }

    //获取房间玩家信息
    private void getuserswithroomid() {
        String url = Constant.HOST + "getuserswithroomid&roomId=" + mGroupId;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AnalysisJSON.analysisJson(response)) {
                    Gson gson = new Gson();
                    UsersWithRoomId usersWithRoomId = gson.fromJson(response, UsersWithRoomId.class);
                    mResult = usersWithRoomId.getResult();
                    initView();
                } else {
                    initView();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initView();
            }
        });

        mQueue.add(request);
    }
}
