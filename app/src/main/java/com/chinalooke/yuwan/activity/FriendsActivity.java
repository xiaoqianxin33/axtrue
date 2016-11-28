package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.SortAdapter;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.FriendsList;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.model.SortModel;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.CharacterParser;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PinyinComparator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendsActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    private LoginUser.ResultBean mUser;
    private RequestQueue mQueue;
    private SortAdapter mSortAdapter;
    private List<SortModel> mSortModels = new ArrayList<>();
    private List<FriendsList.ResultBean> mFriends;
    private HashMap<SortModel, String> mHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ButterKnife.bind(this);
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mQueue = YuwanApplication.getQueue();
        initView();
        initData();
    }

    private void initData() {
        getFriendsWithUserId();
    }

    //获得好友数据
    private void getFriendsWithUserId() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String uri = Constant.HOST + "getFriendsWithUserId&userId=" + mUser.getUserId();
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mPbLoad.setVisibility(View.GONE);
                    if (AnalysisJSON.analysisJson(response)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<FriendsList>() {
                        }.getType();
                        FriendsList friendsList = gson.fromJson(response, type);
                        if (friendsList != null && friendsList.getResult() != null && friendsList.getResult().size() != 0) {
                            mTvNone.setVisibility(View.GONE);
                            mFriends = friendsList.getResult();
                            classifyFriends();
                        } else {
                            mTvNone.setVisibility(View.VISIBLE);
                            mTvNone.setText("暂未添加好友");
                        }
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            mTvNone.setVisibility(View.VISIBLE);
                            mTvNone.setText(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mPbLoad.setVisibility(View.GONE);
                    mTvNone.setVisibility(View.VISIBLE);
                    mTvNone.setText("服务器抽风了，获取好友列表失败");
                }
            });
            mQueue.add(request);
        } else {
            mPbLoad.setVisibility(View.GONE);
            mTvNone.setVisibility(View.VISIBLE);
            mTvNone.setText("网络不可用，获取好友列表失败");
        }
    }

    private void initView() {
        mTvTitle.setText("我的战友");
        mSortAdapter = new SortAdapter(this, mSortModels, 1);
        mListView.setAdapter(mSortAdapter);
    }


    @OnClick({R.id.iv_back, R.id.iv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_add:
                startActivity(new Intent(this, AddFriendsActivity.class));
                break;
        }
    }

    //分类好友
    private void classifyFriends() {
        CharacterParser characterParser = new CharacterParser();
        for (FriendsList.ResultBean friend : mFriends) {
            SortModel sortModel = new SortModel();
            sortModel.setFriends(friend);
            String selling = characterParser.getSelling(friend.getNickName());
            String sortString = selling.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortModels.add(sortModel);
            mHashMap.put(sortModel, "0");
        }
        Collections.sort(mSortModels, new PinyinComparator());
        mSortAdapter.updateListView(mSortModels);
    }
}
