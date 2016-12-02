package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.SortAdapter;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.bean.FriendInfo;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.SortModel;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.CharacterParser;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.PinyinComparator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.chinalooke.yuwan.constant.Constant.MIN_CLICK_DELAY_TIME;
import static com.chinalooke.yuwan.constant.Constant.lastClickTime;

public class AddFriendActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    @Bind(R.id.list_view)
    ListView mListView;
    private LoginUser.ResultBean mUserInfo;
    private RequestQueue mQueue;
    private List<FriendInfo.ResultBean> mFriends = new ArrayList<>();
    private List<SortModel> mSortModels = new ArrayList<>();
    private List<SortModel> mChose = new ArrayList<>();
    private SortAdapter mSortAdapter;
    private String mMaxPeopleNumber;
    private int SELECT_COUNT;
    private HashMap<SortModel, String> mHashMap = new HashMap<>();
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        //listView item点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SELECT_COUNT > Integer.parseInt(mMaxPeopleNumber)) {
                    mToast.setText("本游戏最多只能添加" + mMaxPeopleNumber + "位好友");
                    mToast.show();
                    return;
                }
                ImageView IvCheck = (ImageView) view.findViewById(R.id.iv_check);
                SortModel sortModel = (SortModel) parent.getItemAtPosition(position);
                String s = mHashMap.get(sortModel);
                if ("0".equals(s)) {
                    IvCheck.setVisibility(View.VISIBLE);
                    mHashMap.put(sortModel, "1");
                    mChose.add(sortModel);
                    SELECT_COUNT++;
                } else if ("1".equals(s)) {
                    IvCheck.setVisibility(View.GONE);
                    mHashMap.put(sortModel, "0");
                    mChose.remove(sortModel);
                    SELECT_COUNT--;
                }
                mTvSkip.setText("已选(" + SELECT_COUNT + ")");
            }
        });
    }

    private void initView() {
        mTvTitle.setText("添加战友");
        mTvSkip.setTextColor(getResources().getColor(R.color.white));
        mTvSkip.setText("已选");
        mSortAdapter = new SortAdapter(getApplicationContext(), mSortModels, 0);
        mListView.setAdapter(mSortAdapter);
    }

    private void initData() {
        getFriends();
        mMaxPeopleNumber = getIntent().getStringExtra("maxPeopleNumber");
    }

    //取得用户的所有好友
    private void getFriends() {
        String uri = Constant.HOST + "getFriendsWithUserId&userId=" + mUserInfo.getUserId();
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mPbLoad.setVisibility(View.GONE);
                if (response != null) {
                    if (AnalysisJSON.analysisJson(response)) {
                        mTvNone.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        Type type = new TypeToken<FriendInfo>() {
                        }.getType();
                        FriendInfo friendInfo = gson.fromJson(response, type);
                        if (friendInfo != null) {
                            mFriends = friendInfo.getResult();
                            if (mFriends != null)
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
                } else {
                    mTvNone.setVisibility(View.VISIBLE);
                    mTvNone.setText("获取好友列表失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mPbLoad.setVisibility(View.GONE);
                mTvNone.setVisibility(View.VISIBLE);
                mTvNone.setText("网络不给力，获取好友列表失败");
            }
        });
        mQueue.add(request);
    }

    //分类好友
    private void classifyFriends() {
        CharacterParser characterParser = new CharacterParser();
        for (FriendInfo.ResultBean friend : mFriends) {
            SortModel sortModel = new SortModel();
            sortModel.setFriend(friend);
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

    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.tv_ok:
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mChose", (Serializable) mChose);
                    intent.putExtras(bundle);
                    setResult(3, intent);
                    break;
            }
        }
    }
}
