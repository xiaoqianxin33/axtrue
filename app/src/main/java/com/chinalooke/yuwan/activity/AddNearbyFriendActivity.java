package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.model.NearbyPeople;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LocationUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//添加附近战友
public class AddNearbyFriendActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    private RequestQueue mQueue;
    private LoginUser.ResultBean mUser;
    private List<NearbyPeople.ResultBean> mResult = new ArrayList<>();
    private MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nearby_friend);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        initView();
        initData();
    }


    private void initData() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            AMapLocation aMapLocation = LocationUtils.getAMapLocation();
            if (aMapLocation != null) {
                double latitude = aMapLocation.getLatitude();
                double longitude = aMapLocation.getLongitude();
                String uri = Constant.HOST + "getUsersWithGps&userId=" + mUser.getUserId() + "&lng=" + longitude + "&lat=" + latitude;
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
                                    List<NearbyPeople.ResultBean> result = nearbyPeople.getResult();
                                    mResult.addAll(result);
                                    mMyAdapter.notifyDataSetChanged();
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

                    }
                });

                mQueue.add(request);
            } else {
                mPbLoad.setVisibility(View.GONE);
                mTvNone.setVisibility(View.VISIBLE);
                mTvNone.setText("定位功能未开启");
            }
        } else {
            mPbLoad.setVisibility(View.GONE);
            mTvNone.setVisibility(View.VISIBLE);
            mTvNone.setText("网络未连接");
        }
    }

    private void initView() {
        mTvTitle.setText("添加附近战友");
        mMyAdapter = new MyAdapter(mResult);
        mListView.setAdapter(mMyAdapter);
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }


    class MyAdapter extends MyBaseAdapter {

        MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(AddNearbyFriendActivity.this, R.layout.item_add_nearby_friends_recyclerview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final NearbyPeople.ResultBean friend = mResult.get(position);
            String nickName = friend.getNickName();
            if (!TextUtils.isEmpty(nickName))
                viewHolder.mTvName.setText(nickName);

            String headImg = friend.getHeadImg();
            if (!TextUtils.isEmpty(headImg))
                Picasso.with(getApplicationContext()).load(headImg).resize(100, 100).into(viewHolder.mIvHead);
            String slogan = friend.getSlogan();
            if (!TextUtils.isEmpty(slogan))
                viewHolder.mTvSlogen.setText(slogan);

            viewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddNearbyFriendActivity.this, SendUpAddFriendActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("people", friend);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            return convertView;
        }

    }

    static class ViewHolder {
        @Bind(R.id.iv_head)
        RoundedImageView mIvHead;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_slogen)
        TextView mTvSlogen;
        @Bind(R.id.btn_add)
        Button mBtnAdd;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
