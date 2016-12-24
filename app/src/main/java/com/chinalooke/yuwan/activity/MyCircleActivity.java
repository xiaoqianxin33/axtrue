package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.bean.Circle;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

public class MyCircleActivity extends AutoLayoutActivity {

    @Bind(R.id.lv_mycircle)
    ListView mLvMycircle;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.tv_no)
    TextView mTvNo;
    private RequestQueue mQueue;
    private Toast mToast;
    private LoginUser.ResultBean mUserInfo;
    private Circle mMyCircle;
    private List<Circle.ResultBean> mMyCircleResult = new ArrayList<>();
    private MyAdapt mMyAdapt;
    private int mPage = 1;
    private boolean isLoading = false;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_circle);
        ButterKnife.bind(this);
        mQueue = Volley.newRequestQueue(this);
        mToast = YuwanApplication.getToast();
        mUserInfo = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        mMyAdapt = new MyAdapt();
        mLvMycircle.setAdapter(mMyAdapt);
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

        mLvMycircle.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLvMycircle != null && mLvMycircle.getChildCount() > 0) {
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
            if (mUserInfo == null) {
                mProgressBar.setVisibility(View.GONE);
                mTvNo.setText("请登录查看");
                mTvNo.setVisibility(View.VISIBLE);
            } else {
                getCircleData();
            }
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTvNo.setText("网络错误");
            mTvNo.setVisibility(View.VISIBLE);
        }
    }

    private void getCircleData() {
        String uri = Constant.HOST + "getGroupListWithType&userId=" + mUserInfo.getUserId()
                + "&mygroup&pageNo=" + mPage + "pageSize=" + 10;
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
                        mMyCircleResult = mMyCircle.getResult();
                        mMyAdapt.notifyDataSetChanged();
                        mPage++;
                    } else {
                        if (isFirst) {
                            mTvNo.setText("没有加入的圈子");
                            mTvNo.setVisibility(View.VISIBLE);
                        } else {
                            mTvNo.setVisibility(View.GONE);
                        }
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.getString("Msg");
                        mToast.setText(msg);
                        mToast.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                convertView = View.inflate(MyCircleActivity.this, R.layout.item_circle_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Circle.ResultBean resultBean = mMyCircleResult.get(position);
            String headImg = resultBean.getHeadImg();
            if (!TextUtils.isEmpty(headImg)) {
                String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), headImg, MyUtils.Dp2Px(getApplicationContext()
                        , 80), MyUtils.Dp2Px(getApplicationContext(), 80));
                Picasso.with(getApplicationContext()).load(loadImageUrl).into(viewHolder.mIvCircleImage);
            }
            viewHolder.mTvCircleName.setText(resultBean.getGroupName());
            viewHolder.mTvCircleDetails.setText(resultBean.getDetails());
            viewHolder.mTvDiscountCircle.setVisibility(View.GONE);
            return convertView;
        }

    }

    static class ViewHolder {
        @Bind(R.id.iv_image)
        ImageView mIvCircleImage;
        @Bind(R.id.tv_circle_name)
        TextView mTvCircleName;
        @Bind(R.id.tv_slogen)
        TextView mTvCircleDetails;
        @Bind(R.id.tv_distance)
        TextView mTvDiscountCircle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    @OnClick(R.id.back_personal_info)
    public void onClick() {
        finish();
    }
}
