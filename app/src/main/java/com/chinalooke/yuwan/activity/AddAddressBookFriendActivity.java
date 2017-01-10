package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.avos.avoscloud.AVAnalytics;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.SortAddFriendsAdapter;
import com.chinalooke.yuwan.bean.NearbyPeople;
import com.chinalooke.yuwan.bean.SortModel;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.CharacterParser;
import com.chinalooke.yuwan.utils.ContactsEngine;
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

//添加通讯录好友界面
public class AddAddressBookFriendActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    private List<HashMap<String, String>> mAllContacts = new ArrayList<>();
    private List<String> mPhoneList = new ArrayList<>();
    private RequestQueue mQueue;
    private List<NearbyPeople.ResultBean> mResult;
    private SortAddFriendsAdapter mSortAddFriendsAdapter;
    private List<SortModel> mSortModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address_book_friend);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mSortAddFriendsAdapter.setOnBtnClickListener(new SortAddFriendsAdapter.OnBtnClickListener() {
            @Override
            public void onclick(int position, View finalView) {
                NearbyPeople.ResultBean nearbyPeople = mSortModels.get(position).getNearbyPeople();
                Intent intent = new Intent(AddAddressBookFriendActivity.this, SendUpAddFriendActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("people", nearbyPeople);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        mAllContacts = ContactsEngine.getAllContacts(getApplicationContext());
        for (HashMap<String, String> map : mAllContacts) {
            String phone = map.get("phone");
            if (!TextUtils.isEmpty(phone))
                mPhoneList.add(phone);
        }
        getUsersWithPhoneArray();
    }

    //多个电话号码查找多个用户
    private void getUsersWithPhoneArray() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < mPhoneList.size(); i++) {
                if (i == mPhoneList.size() - 1)
                    stringBuilder.append(mPhoneList.get(i));
                else
                    stringBuilder.append(mPhoneList.get(i)).append(",");
            }
            String uri = Constant.HOST + "getUsersWithPhoneArray&phones=" + stringBuilder.toString();
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mPbLoad.setVisibility(View.GONE);
                    if (response != null) {
                        if (AnalysisJSON.analysisJson(response)) {
                            mTvNone.setVisibility(View.GONE);
                            Gson gson = new Gson();
                            Type type = new TypeToken<NearbyPeople>() {
                            }.getType();
                            NearbyPeople nearbyPeople = gson.fromJson(response, type);
                            if (nearbyPeople != null && nearbyPeople.getResult() != null && nearbyPeople.getResult().size() != 0) {
                                mResult = nearbyPeople.getResult();
                                classifyFriends();
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
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mPbLoad.setVisibility(View.GONE);
                    mTvNone.setVisibility(View.VISIBLE);
                    mTvNone.setText("读取通讯录功能未开启");
                }
            });
            mQueue.add(request);
        } else {
            mPbLoad.setVisibility(View.GONE);
            mTvNone.setVisibility(View.VISIBLE);
            mTvNone.setText("网络未连接");
        }
    }

    private void classifyFriends() {
        CharacterParser characterParser = new CharacterParser();
        for (NearbyPeople.ResultBean friend : mResult) {
            SortModel sortModel = new SortModel();
            sortModel.setNearbyPeople(friend);
            String selling = characterParser.getSelling(friend.getNickName());
            String sortString = selling.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortModels.add(sortModel);
        }
        Collections.sort(mSortModels, new PinyinComparator());
        mSortAddFriendsAdapter.updateListView(mSortModels);
    }

    private void initView() {
        mTvTitle.setText("添加通讯录好友");
        mSortAddFriendsAdapter = new SortAddFriendsAdapter(this, mSortModels);
        mListView.setAdapter(mSortAddFriendsAdapter);
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }

}
