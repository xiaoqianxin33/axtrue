package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.Circle;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoreCircleActivity extends AppCompatActivity {

    @Bind(R.id.lv_morecircle)
    ListView mLvMorecircle;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.tv_no)
    TextView mTvNo;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.activity_more_circle)
    LinearLayout mActivityMoreCircle;
    private RequestQueue mQueue;
    private Toast mToast;
    private MyAdapt mMyAdapt;
    private int mPage = 1;
    private boolean isLoading = false;
    private double mLongitude;
    private double mLatitude;
    private Circle mMyCircle;
    private List<Circle.ResultBean> mMyCircleResult = new ArrayList<>();
    private boolean isFirst = true;
    private LoginUser.ResultBean mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_circle);
        ButterKnife.bind(this);
        mQueue = Volley.newRequestQueue(this);
        mToast = YuwanApplication.getToast();
        mMyAdapt = new MyAdapt();
        mLvMorecircle.setAdapter(mMyAdapt);
        mLongitude = getIntent().getDoubleExtra("longitude", 0);
        mLatitude = getIntent().getDoubleExtra("latitude", 0);
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mTvTitle.setText("更多圈子");
        initEvent();
    }

    private void initEvent() {
        mSr.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                mMyCircleResult.clear();
                initData();
                mSr.setRefreshing(false);
            }
        });

        mLvMorecircle.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLvMorecircle != null && mLvMorecircle.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    mSr.setEnabled(enable);
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        isLoading = true;
        initData();
    }

    private void initData() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            if (mLatitude != 0 && mLongitude != 0) {
                getCirData();
            } else {
                mProgressBar.setVisibility(View.GONE);
                mTvNo.setVisibility(View.VISIBLE);
                mTvNo.setText("定位失败");
            }
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTvNo.setVisibility(View.VISIBLE);
            mTvNo.setText("网络未连接");
        }

    }

    private void getCirData() {
        String uri = Constant.HOST + "getGroupWithGPS&lng=" + mLongitude + "&lat=" + mLatitude + "&pageNo=" + mPage + "&pageSize=10";
        StringRequest stringRequest = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressBar.setVisibility(View.GONE);
                String substring = response.substring(11, 15);
                if ("true".equals(substring)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Circle>() {
                    }.getType();
                    mMyCircle = gson.fromJson(response, type);
                    if (mMyCircle != null) {
                        mTvNo.setVisibility(View.GONE);
                        mMyCircleResult.addAll(mMyCircle.getResult());
                        mMyAdapt.notifyDataSetChanged();
                        mPage++;
                    } else {
                        if (isFirst) {
                            mTvNo.setText("没有加入的圈子");
                            mTvNo.setVisibility(View.VISIBLE);
                        } else {
                            mTvNo.setVisibility(View.GONE);
                            mToast.setText("没有更多了");
                            mToast.show();
                        }
                    }
                } else {
                    if (isFirst) {
                        try {
                            mTvNo.setVisibility(View.VISIBLE);
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            mToast.setText(msg);
                            mToast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mTvNo.setVisibility(View.GONE);
                        mToast.setText("没有更多了");
                        mToast.show();
                    }
                }
                isLoading = false;
                isFirst = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTvNo.setVisibility(View.VISIBLE);
                mTvNo.setText("加载失败");
                mProgressBar.setVisibility(View.GONE);
                isFirst = false;
            }
        });
        mQueue.add(stringRequest);
    }

    @OnClick({R.id.iv_create_circle, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_create_circle:
                if (mUserInfo != null)
                    startActivity(new Intent(this, CreateCircleActivity.class));
                else
                    startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    private class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return mMyCircleResult.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_circle_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Circle.ResultBean resultBean = mMyCircleResult.get(position);
            Picasso.with(getApplicationContext()).load(resultBean.getHeadImg()).resize(MyUtils.Dp2Px(getApplicationContext()
                    , 80), MyUtils.Dp2Px(getApplicationContext(), 80)).centerCrop().into(viewHolder.mIvCircleImage);
            viewHolder.mTvCircleName.setText(resultBean.getGroupName());
            viewHolder.mTvCircleDetails.setText(resultBean.getDetails());
            viewHolder.mTvDiscountCircle.setText(resultBean.getDistance() + "m");
            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.iv_image)
        ImageView mIvCircleImage;
        @Bind(R.id.tv_name)
        TextView mTvCircleName;
        @Bind(R.id.tv_slogen)
        TextView mTvCircleDetails;
        @Bind(R.id.tv_distance)
        TextView mTvDiscountCircle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
