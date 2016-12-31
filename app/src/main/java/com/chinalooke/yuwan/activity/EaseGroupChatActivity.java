package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ProgressBar;

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
import com.hyphenate.easeui.db.HxDBManager;
import com.hyphenate.easeui.model.UsersWithRoomId;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EaseGroupChatActivity extends AutoLayoutActivity {

    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    private String mGroupId;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ease_group_chat);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        initData();
    }

    private void initView() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        EaseChatFragment easeChatFragment = new EaseChatFragment();
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
        args.putString(EaseConstant.EXTRA_USER_ID, mGroupId);
        easeChatFragment.setArguments(args);
        supportFragmentManager.beginTransaction().replace(R.id.activity_ease_group_chat, easeChatFragment).commit();

    }

    private void initData() {
        mGroupId = getIntent().getStringExtra("groupId");
        getUsersWithRoomId();
    }

    //获取房间玩家信息
    private void getUsersWithRoomId() {
        String url = Constant.HOST + "getuserswithroomid&roomId=" + mGroupId;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AnalysisJSON.analysisJson(response)) {
                    Gson gson = new Gson();
                    UsersWithRoomId usersWithRoomId = gson.fromJson(response, UsersWithRoomId.class);
                    List<UsersWithRoomId.ResultBean> result = usersWithRoomId.getResult();
                    if (result != null && result.size() != 0) {
                        List<UsersWithRoomId.ResultBean.PlayersBean> list = new ArrayList<>();
                        for (UsersWithRoomId.ResultBean resultBean : result) {
                            list.add(resultBean.getPlayers());
                        }
                        HxDBManager hxDBManager = new HxDBManager(getApplicationContext());
                        hxDBManager.add(list);
                        hxDBManager.closeDB();
                    }
                    mPbLoad.setVisibility(View.GONE);
                    initView();
                } else {
                    mPbLoad.setVisibility(View.GONE);
                    initView();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mPbLoad.setVisibility(View.GONE);
                initView();
            }
        });

        mQueue.add(request);
    }
}
