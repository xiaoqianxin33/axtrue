package com.chinalooke.yuwan.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.CircleRanking;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.ImageUtils;
import com.chinalooke.yuwan.utils.LocationUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.ViewHelper;
import com.chinalooke.yuwan.view.NoSlidingListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lljjcoder.citypickerview.widget.CityPickerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//圈子排行
public class CircleRankingActivity extends AutoLayoutActivity implements AMapLocationListener {

    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_ranking)
    TextView mTvRanking;
    @Bind(R.id.rl_head)
    RelativeLayout mRlHead;
    @Bind(R.id.tv_city)
    TextView mTvCity;
    @Bind(R.id.list_view)
    NoSlidingListView mListView;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    private LoginUser.ResultBean mUserInfo;
    private int RANKING_TYPE;
    private int PAGE_NO;
    private RequestQueue mQueue;
    private List<CircleRanking.ResultBean> mRankings = new ArrayList<>();
    private MyAdapter mMyAdapter;
    private boolean isLoading = false;
    private View mFoot;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_ranking);
        ButterKnife.bind(this);
        mUserInfo = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        mQueue = YuwanApplication.getQueue();
        mMyAdapter = new MyAdapter(mRankings);
        mListView.setAdapter(mMyAdapter);
        mFoot = View.inflate(this, R.layout.foot, null);
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                String city = mTvCity.getText().toString();
                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading && !TextUtils.isEmpty(city)) {
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        isLoading = true;
        PAGE_NO++;
        getScoreList(mTvCity.getText().toString());
    }

    private void initView() {
        if (mUserInfo != null) {
            String img = mUserInfo.getHeadImg();
            if (!TextUtils.isEmpty(img)) {
                String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), img, 100, 100);
                Picasso.with(getApplicationContext()).load(loadImageUrl).into(mRoundedImageView);
                setStackBlurBackground(img);
            }
            String nickName = mUserInfo.getNickName();
            if (!TextUtils.isEmpty(nickName))
                mTvName.setText(nickName);
        }

        AMapLocation aMapLocation = LocationUtils.getAMapLocation();
        if (aMapLocation != null) {
            mRankings.clear();
            mTvCity.setText(aMapLocation.getCity());
        } else {
            LocationUtils.location(this, this);
        }

        if (mUserInfo == null) {
            mTvName.setText("尚未登录");
            mTvRanking.setVisibility(View.GONE);
        }

        mRoundedImageView.setFocusable(true);
        mRoundedImageView.setFocusableInTouchMode(true);
        mRoundedImageView.requestFocus();
    }

    //设置头部虚化背景
    private void setStackBlurBackground(String img) {
        DisplayMetrics displayMetrics = ViewHelper.getDisplayMetrics(CircleRankingActivity.this);
        int widthPixels = displayMetrics.widthPixels;
        String uri = img + "?imageView2/1/w/" + widthPixels + "/h/280";
        ImageRequest request = new ImageRequest(uri, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if (response != null) {
                    Bitmap blurBitmap = ImageUtils.fastBlur(getApplicationContext(), response, 0.5f, 0.5f);
                    mRlHead.setBackground(new BitmapDrawable(blurBitmap));
                }
            }
        }, widthPixels, 280, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, null);

        mQueue.add(request);
    }

    private void initData() {
        RANKING_TYPE = getIntent().getIntExtra("ranking_type", 0);
    }

    //获得积分排行
    private void getScoreList(String city) {
        String uri = null;
        switch (RANKING_TYPE) {
            case 0:
                if (mUserInfo != null)
                    try {
                        uri = Constant.HOST + "getScoreList&groupId=0&city=" + URLEncoder.encode(city, "UTF-8") + "&pageNo=" + PAGE_NO + "&pageSize=5"
                                + "&userId=" + mUserInfo.getUserId();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                else
                    try {
                        uri = Constant.HOST + "getScoreList&groupId=0&city=" + URLEncoder.encode(city, "UTF-8") + "&pageNo=" + PAGE_NO + "&pageSize=5";
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                break;
            case 1:
                String groupId = getIntent().getStringExtra("groupId");
                if (mUserInfo != null)
                    uri = Constant.HOST + "getScoreList&groupId=" + groupId + "&city=&pageNo=" + PAGE_NO + "&pageSize=5" + "&userId=" + mUserInfo.getUserId();
                else
                    uri = Constant.HOST + "getScoreList&groupId=" + groupId + "&city=&pageNo=" + PAGE_NO + "&pageSize=5";
                break;
        }


        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mPbLoad.setVisibility(View.GONE);
                    if (AnalysisJSON.analysisJson(response)) {
                        mTvNone.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        Type type = new TypeToken<CircleRanking>() {
                        }.getType();
                        CircleRanking circleRanking = gson.fromJson(response, type);
                        if (circleRanking != null) {
                            List<CircleRanking.ResultBean> result = circleRanking.getResult();
                            if (result != null && result.size() != 0) {
                                mRankings.addAll(result);
                                String loginUserRanking = mRankings.get(0).getLoginUserRanking();
                                if (!TextUtils.isEmpty(loginUserRanking)) {
                                    mTvRanking.setText(loginUserRanking);
                                }
                                Collections.sort(mRankings, new Comparator<CircleRanking.ResultBean>() {
                                    @Override
                                    public int compare(CircleRanking.ResultBean lhs, CircleRanking.ResultBean rhs) {
                                        if (lhs.getScore() != null && rhs.getScore() != null) {
                                            int l = Integer.parseInt(lhs.getScore());
                                            int r = Integer.parseInt(rhs.getScore());
                                            if (l >= r)
                                                return -1;
                                            else
                                                return 1;
                                        } else {
                                            return 0;
                                        }

                                    }
                                });
                                mMyAdapter.notifyDataSetChanged();
                                isLoading = false;
                                isFirst = false;
                            } else {
                                if (isFirst) {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String msg = jsonObject.getString("Msg");
                                    mTvNone.setVisibility(View.VISIBLE);
                                    mTvNone.setText(msg);
                                } else {
                                    mListView.addFooterView(mFoot);
                                }
                            }
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.getString("Msg");
                        mTvNone.setVisibility(View.VISIBLE);
                        mTvNone.setText(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                mPbLoad.setVisibility(View.GONE);
                mTvNone.setVisibility(View.VISIBLE);
                mTvNone.setText("服务器抽风了，请稍后再试");
            }
        }

        );
        mQueue.add(request);
    }

    @OnClick({R.id.iv_back, R.id.iv_arrow, R.id.tv_city})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_arrow:
                showCitySelector();
                break;
            case R.id.tv_city:
                showCitySelector();
                break;
        }
    }

    //弹出城市选择器

    private void showCitySelector() {
        CityPickerView cityPicker = new CityPickerView(this);
        cityPicker.setIsCyclic(false);
        cityPicker.setOnCityItemClickListener(new CityPickerView.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                String city = citySelected[1] + citySelected[2];
                mRankings.clear();
                getScoreList(city);
                mTvCity.setText(city);
            }
        });
        cityPicker.show();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                mRankings.clear();
                mTvCity.setText(aMapLocation.getCity() + aMapLocation.getDistrict());
            }
        }
    }

    class MyAdapter extends MyBaseAdapter {

        MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(CircleRankingActivity.this, R.layout.item_circlerangking_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CircleRanking.ResultBean resultBean = mRankings.get(position);
            if (position == 0) {
                viewHolder.mTvRanking.setVisibility(View.GONE);
                viewHolder.mIvRanking.setImageResource(R.mipmap.champions);
                viewHolder.mIvRanking.setVisibility(View.VISIBLE);
            } else if (position == 1) {
                viewHolder.mTvRanking.setVisibility(View.GONE);
                viewHolder.mIvRanking.setImageResource(R.mipmap.second);
                viewHolder.mIvRanking.setVisibility(View.VISIBLE);
            } else if (position == 2) {
                viewHolder.mTvRanking.setVisibility(View.GONE);
                viewHolder.mIvRanking.setImageResource(R.mipmap.third);
                viewHolder.mIvRanking.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mTvRanking.setVisibility(View.VISIBLE);
                viewHolder.mTvRanking.setText(position + 1 + "");
                viewHolder.mIvRanking.setVisibility(View.GONE);
            }
            String headImg = resultBean.getHeadImg();
            if (!TextUtils.isEmpty(headImg)) {
                String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), headImg, 80, 80);
                Picasso.with(getApplicationContext()).load(loadImageUrl).into(viewHolder.mRoundedImageView);
            }
            String slogan = resultBean.getSlogan();
            if (!TextUtils.isEmpty(slogan))
                viewHolder.mTvSlogen.setText(slogan);
            String score = resultBean.getScore();
            if (!TextUtils.isEmpty(score))
                viewHolder.mTvScore.setText(score);

            String groupName = resultBean.getGroupName();
            String nickName = resultBean.getNickName();
            if (!TextUtils.isEmpty(nickName)) {
                if (!TextUtils.isEmpty(groupName)) {
                    if (groupName.contains(",")) {
                        String replace = groupName.replace(",", "|");
                        viewHolder.mTvNameGame.setText(nickName + " | " + replace);
                    } else {
                        viewHolder.mTvNameGame.setText(nickName + " | " + groupName);
                    }
                } else {
                    viewHolder.mTvNameGame.setText(nickName);
                }
            }
            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.iv_ranking)
        ImageView mIvRanking;
        @Bind(R.id.tv_ranking)
        TextView mTvRanking;
        @Bind(R.id.roundedImageView)
        RoundedImageView mRoundedImageView;
        @Bind(R.id.tv_name_game)
        TextView mTvNameGame;
        @Bind(R.id.tv_slogen)
        TextView mTvSlogen;
        @Bind(R.id.tv_score)
        TextView mTvScore;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
