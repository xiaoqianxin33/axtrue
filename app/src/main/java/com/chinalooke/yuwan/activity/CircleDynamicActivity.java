package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.Circle;
import com.chinalooke.yuwan.bean.CircleDetail;
import com.chinalooke.yuwan.bean.Dynamic;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.ViewHelper;
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

public class CircleDynamicActivity extends AutoLayoutActivity {

    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_slogen)
    TextView mTvSlogen;
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.sr)
    SwipeRefreshLayout mScrollview;
    @Bind(R.id.rl_top)
    RelativeLayout mRlTop;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    @Bind(R.id.tv_join)
    TextView mTvJoin;
    @Bind(R.id.tv_paihang)
    TextView mTvPaihang;
    private Circle.ResultBean mCircle;
    private RequestQueue mQueue;
    private DisplayMetrics mDisplayMetrics;
    private int mCircle_type;
    private int PAGE_NO;
    private LoginUser.ResultBean mUserInfo;
    private List<Dynamic.ResultBean.ListBean> mDynamics = new ArrayList<>();
    private MyDynamicAdapter mMyDynamicAdapter;
    private boolean isLoading = false;
    private boolean isRefresh = false;
    private boolean isFirst = true;
    private Toast mToast;
    private boolean mUserJoin;
    private ProgressDialog mProgressDialog;
    private CircleDetail mCircleDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_circle_dynamic);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mDisplayMetrics = ViewHelper.getDisplayMetrics(CircleDynamicActivity.this);
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mMyDynamicAdapter = new MyDynamicAdapter(mDynamics, CircleDynamicActivity.this);
        mListView.setAdapter(mMyDynamicAdapter);
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        //初始化swipeRefresh颜色
        mScrollview.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //下拉刷新事件
        mScrollview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PAGE_NO = 1;
                isRefresh = true;
                getActiveList();
                mScrollview.setRefreshing(false);
            }
        });


        //上拉加载更多
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mListView != null && mListView.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    mScrollview.setEnabled(enable);
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        PAGE_NO++;
        isLoading = true;
        getActiveList();
    }

    private void initView() {
        //设置顶部背景图片
        String bgImage = mCircle.getBgImage();
        ImageRequest request = new ImageRequest(bgImage, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if (response != null)
                    mRlTop.setBackground(new BitmapDrawable(response));
            }
        }, mDisplayMetrics.widthPixels, 400, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(request);
        String groupName = mCircle.getGroupName();
        if (!TextUtils.isEmpty(groupName))
            mTvName.setText(groupName);

        String details = mCircle.getDetails();
        if (!TextUtils.isEmpty(details))
            mTvSlogen.setText(details);

        String headImg = mCircle.getHeadImg();
        if (!TextUtils.isEmpty(headImg))
            Picasso.with(getApplicationContext()).load(headImg).resize(130, 130).centerCrop().into(mRoundedImageView);
    }

    private void setIsJoin() {
        if (mUserJoin) {
            mTvJoin.setVisibility(View.GONE);
            mTvPaihang.setVisibility(View.VISIBLE);
        } else {
            mTvJoin.setVisibility(View.VISIBLE);
            mTvPaihang.setVisibility(View.GONE);
        }
    }

    private void initData() {
        mCircle = (Circle.ResultBean) getIntent().getSerializableExtra("circle");
        mCircle_type = getIntent().getIntExtra("circle_type", 0);
        getGroupWIthId();
    }

    //获得圈子详细信息
    private void getGroupWIthId() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String url;
            if (mUserInfo != null) {
                url = Constant.HOST + "getGroupWIthId&groupId=" + mCircle.getGroupId() + "&userId=" + mUserInfo.getUserId();
            } else {
                url = Constant.HOST + "getGroupWIthId&groupId=" + mCircle.getGroupId() + "&userId=";
            }

            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (AnalysisJSON.analysisJson(response)) {
                        Gson gson = new Gson();
                        mCircleDetail = gson.fromJson(response, CircleDetail.class);
                        CircleDetail.ResultBean result = mCircleDetail.getResult();
                        mUserJoin = result.isIsUserJoin();
                        setIsJoin();
                    } else {
                        setIsJoin();
                    }
                }
            }, null);

            mQueue.add(request);
        }
    }

    //获取圈子动态信息
    private void getActiveList() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String url = Constant.HOST + "getActiveListWithGroup&groupId=" + mCircle.getGroupId()
                    + "&pageNo=" + PAGE_NO + "&pageSize=5";
            if (mUserInfo != null)
                url = url + "&userId=" + mUserInfo.getUserId();

            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mPbLoad.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            Object result = jsonObject.get("Result");
                            String s = result.toString();
                            if (s.substring(0, 1).equals("{")) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<Dynamic>() {
                                }.getType();
                                Dynamic dynamic = gson.fromJson(response, type);
                                if (dynamic.getResult() != null) {
                                    if (isRefresh)
                                        mDynamics.clear();
                                    mDynamics.addAll(dynamic.getResult().getList());
                                    mTvNone.setVisibility(View.GONE);
                                    mMyDynamicAdapter.notifyDataSetChanged();
                                    isRefresh = false;
                                    isLoading = false;
                                }
                                isFirst = false;
                            } else {
                                if (isFirst) {
                                    mTvNone.setVisibility(View.VISIBLE);
                                    mTvNone.setText("暂无动态");
                                }
                                isFirst = false;
                            }

                        } else {
                            if (isFirst) {
                                mTvNone.setVisibility(View.VISIBLE);
                                mTvNone.setText("暂无动态");
                            }
                            isFirst = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mPbLoad.setVisibility(View.GONE);
                    mTvNone.setVisibility(View.VISIBLE);
                    mTvNone.setText("服务器抽风了，请稍后重试");
                }
            });

            mQueue.add(request);
        } else {
            mPbLoad.setVisibility(View.GONE);
            mTvNone.setVisibility(View.VISIBLE);
            mTvNone.setText("网络未连接");
        }

    }

    @OnClick({R.id.iv_back, R.id.iv_camera, R.id.roundedImageView, R.id.tv_name, R.id.tv_join, R.id.tv_paihang})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_paihang:
                Intent intent1 = new Intent(this, CircleRankingActivity.class);
                intent1.putExtra("ranking_type", 1);
                intent1.putExtra("groupId", mCircle.getGroupId());
                startActivity(intent1);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_camera:
                if (mUserInfo == null) {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                    return;
                } else {
                    if (mUserJoin) {
                        Intent intent = new Intent(this, SendDynamicActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("circle", mCircle);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        mToast.setText("该圈子玩家才能发表动态");
                        mToast.show();
                    }
                }
                break;
            case R.id.roundedImageView:
                skipToInfo();
                break;
            case R.id.tv_name:
                skipToInfo();
                break;
            case R.id.tv_join:
                if (mUserJoin) {

                } else {
                    if (mUserInfo != null) {
                        joinCircle();
                    } else {
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    }
                }
                break;
        }
    }

    //加入圈子
    private void joinCircle() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            mProgressDialog = MyUtils.initDialog("", CircleDynamicActivity.this);
            mProgressDialog.show();
            final String url = Constant.HOST + "joinGroup&groupId=" + mCircle.getGroupId() + "&userId=" + mUserInfo.getUserId();
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            mToast.setText("加入成功！");
                            mToast.show();
                            mUserJoin = true;
                            setIsJoin();
                        } else {
                            String msg = jsonObject.getString("Msg");
                            mToast.setText(msg);
                            mToast.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressDialog.dismiss();
                    mToast.setText("服务器抽风了，请稍后再试");
                    mToast.show();
                }
            });

            mQueue.add(request);
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }
    }

    private void skipToInfo() {
        Intent intent = new Intent(this, CircleInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("circle", mCircle);
        bundle.putSerializable("circleDetail", mCircleDetail);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    class MyDynamicAdapter extends MyBaseAdapter {
        private Context mContext;

        MyDynamicAdapter(List dataSource, Context context) {
            super(dataSource);
            mContext = context;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DynamicViewHolder dynamicViewHolder;
            if (convertView == null) {
                dynamicViewHolder = new DynamicViewHolder();
                convertView = View.inflate(mContext, R.layout.item_circle_dynamic_listview, null);
                dynamicViewHolder.mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
                dynamicViewHolder.mTvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                dynamicViewHolder.mTvPinglun = (TextView) convertView.findViewById(R.id.tv_pinglun);
                dynamicViewHolder.mTvDianzan = (TextView) convertView.findViewById(R.id.tv_dianzan);
                dynamicViewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
                dynamicViewHolder.mTvContent = (TextView) convertView.findViewById(R.id.tv_content);
                dynamicViewHolder.mGridView = (GridView) convertView.findViewById(R.id.gridView);
                dynamicViewHolder.mIvDianzan = (ImageView) convertView.findViewById(R.id.iv_dianzan);
                dynamicViewHolder.mRoundedImageView = (RoundedImageView) convertView.findViewById(R.id.roundedImageView);
                convertView.setTag(dynamicViewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                dynamicViewHolder = (DynamicViewHolder) convertView.getTag();
            }

            Dynamic.ResultBean.ListBean resultBean = (Dynamic.ResultBean.ListBean) mDataSource.get(position);
            String headImg = resultBean.getHeadImg();
            if (!TextUtils.isEmpty(headImg))
                Picasso.with(mContext).load(headImg).resize(72, 72).centerCrop().into(dynamicViewHolder.mRoundedImageView);
            String content = resultBean.getContent();
            if (!TextUtils.isEmpty(content))
                dynamicViewHolder.mTvContent.setText(content);
            String nickName = resultBean.getNickName();
            if (!TextUtils.isEmpty(nickName))
                dynamicViewHolder.mTvName.setText(nickName);

            String images = resultBean.getImages();
            if (!TextUtils.isEmpty(images)) {
                String[] split = images.split(",");
                dynamicViewHolder.mGridView.setAdapter(new GridAdapter(split));
            }

            String likes = resultBean.getLikes();
            if (!TextUtils.isEmpty(likes)) {
                dynamicViewHolder.mTvDianzan.setText(likes);
            } else {
                dynamicViewHolder.mTvDianzan.setText("0");
            }

            String comments = resultBean.getComments();
            if (!TextUtils.isEmpty(comments))
                dynamicViewHolder.mTvPinglun.setText(comments);
            else
                dynamicViewHolder.mTvPinglun.setText("0");

            boolean isLoginUserLike = resultBean.isLoginUserLike();
            if (isLoginUserLike)
                dynamicViewHolder.mIvDianzan.setImageResource(R.mipmap.dianzanhou);
            else
                dynamicViewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);

            String address = resultBean.getAddress();
            if (!TextUtils.isEmpty(address))
                dynamicViewHolder.mTvAddress.setText(address);

            String addTime = resultBean.getAddTime();
            if (!TextUtils.isEmpty(addTime))
                dynamicViewHolder.mTvTime.setText(addTime);

            return convertView;
        }

    }

    class GridAdapter extends BaseAdapter {
        private String[] mStrings;

        GridAdapter(String[] strings) {
            this.mStrings = strings;
        }

        @Override
        public int getCount() {
            return mStrings.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageview;
            if (convertView == null) {
                imageview = new ImageView(CircleDynamicActivity.this);
                imageview.setImageResource(R.mipmap.placeholder);
                imageview.setLayoutParams(new GridView.LayoutParams(235, 235));
                imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageview.setPadding(6, 6, 6, 6);
                AutoUtils.autoSize(imageview);
            } else {
                imageview = (ImageView) convertView;
            }
            Picasso.with(CircleDynamicActivity.this).load(mStrings[position]).into(imageview);
            return imageview;
        }
    }

    static class DynamicViewHolder {
        RoundedImageView mRoundedImageView;
        TextView mTvName;
        TextView mTvTime;
        TextView mTvContent;
        GridView mGridView;
        TextView mTvAddress;
        TextView mTvPinglun;
        TextView mTvDianzan;
        ImageView mIvDianzan;
    }
}
