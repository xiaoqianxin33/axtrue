package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.bean.NetbarAdvertisement;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LocationUtils;
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

public class NetbarADActivity extends AutoLayoutActivity implements AMapLocationListener {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    private AMapLocationClient mAMapLocationClient;
    private RequestQueue mQueue;
    private int PAGE_NO;
    private List<NetbarAdvertisement.ResultBean> mADList = new ArrayList<>();
    private MyAdapter mMyAdapter;
    private boolean isRefresh = false;
    private boolean isFoot = false;
    private View mFoot;
    private boolean isLoading = false;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netbar_ad);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mFoot = View.inflate(this, R.layout.foot, null);
        initView();
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
                PAGE_NO = 1;
                isRefresh = true;
                isFoot = false;
                initData();
                mSr.setRefreshing(false);
                mListView.removeFooterView(mFoot);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mListView != null && mListView.getChildCount() > 0) {
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
        if (!isFoot) {
            mListView.removeFooterView(mFoot);
            PAGE_NO++;
            initData();
        }
    }

    private void initData() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            AMapLocation aMapLocation = LocationUtils.getAMapLocation();
            if (aMapLocation != null) {
                getADList(aMapLocation);
            } else {
                mAMapLocationClient = LocationUtils.location(this, this);
            }
        } else {
            mTvNone.setText("网络不可用，请检查网络连接");
            mTvNone.setVisibility(View.VISIBLE);
            mPbLoad.setVisibility(View.GONE);
        }
    }


    //获取广告数据
    private void getADList(AMapLocation aMapLocation) {
        String city = aMapLocation.getCity();
        double latitude = aMapLocation.getLatitude();
        double longitude = aMapLocation.getLongitude();
        String uri = Constant.HOST + "getADList&&pageNo=1&pageSize=5&city=" + city + "&pageNo=" + PAGE_NO + "&pageSize=5"
                + "&lng=" + longitude + "&lat=" + latitude;
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mPbLoad.setVisibility(View.GONE);
                if (AnalysisJSON.analysisJson(response)) {
                    mTvNone.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    Type type = new TypeToken<NetbarAdvertisement>() {
                    }.getType();
                    NetbarAdvertisement netbarAdvertisement = gson.fromJson(response, type);
                    if (netbarAdvertisement != null) {
                        List<NetbarAdvertisement.ResultBean> result = netbarAdvertisement.getResult();
                        if (result != null && result.size() != 0) {
                            if (isRefresh)
                                mADList.clear();
                            mADList.addAll(result);
                            mMyAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    if (isFirst) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            mTvNone.setText(jsonObject.getString("Msg"));
                            mTvNone.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        isFoot = true;
                        mListView.addFooterView(mFoot);
                        mTvNone.setVisibility(View.GONE);
                    }
                }
                isRefresh = false;
                isLoading = false;
                isFirst = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTvNone.setText("服务器抽风了，请稍后再试");
                mTvNone.setVisibility(View.VISIBLE);
                mPbLoad.setVisibility(View.GONE);
                isRefresh = false;
                isLoading = false;
                isFirst = false;
            }
        });
        mQueue.add(request);
    }

    private void initView() {
        mTvTitle.setText("广告列表");
        mMyAdapter = new MyAdapter(mADList);
        mListView.setAdapter(mMyAdapter);
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                LocationUtils.setAMapLocation(aMapLocation);
                getADList(aMapLocation);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mAMapLocationClient != null) {
            mAMapLocationClient.stopLocation();
            mAMapLocationClient.onDestroy();
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
                convertView = View.inflate(NetbarADActivity.this, R.layout.item_netbarad_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            NetbarAdvertisement.ResultBean ad = (NetbarAdvertisement.ResultBean) mDataSource.get(position);
            List<String> adImg = ad.getADImg();
            if (adImg != null && adImg.size() != 0) {
                String uri = adImg.get(0);
                String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), uri, 270, 200);
                Picasso.with(getApplicationContext()).load(loadImageUrl).into(viewHolder.mIvImage);
            }

            String title = ad.getTitle();
            if (!TextUtils.isEmpty(title))
                viewHolder.mTvTitle.setText(title);

            String description = ad.getDescription();
            if (!TextUtils.isEmpty(description))
                viewHolder.mTvContent.setText(description);

            String addTime = ad.getAddTime();
            if (!TextUtils.isEmpty(addTime))
                viewHolder.mTvTime.setText(addTime);

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.iv_image)
            ImageView mIvImage;
            @Bind(R.id.tv_title)
            TextView mTvTitle;
            @Bind(R.id.tv_content)
            TextView mTvContent;
            @Bind(R.id.tv_time)
            TextView mTvTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
