package com.chinalooke.yuwan.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.DynamicDetailActivity;
import com.chinalooke.yuwan.activity.LoginActivity;
import com.chinalooke.yuwan.activity.MainActivity;
import com.chinalooke.yuwan.activity.NetbarADActivity;
import com.chinalooke.yuwan.activity.SendDynamicActivity;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.NetbarAdvertisement;
import com.chinalooke.yuwan.bean.WholeDynamic;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LocationUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.ViewHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;

public class DynamicFragment extends Fragment implements AMapLocationListener {


    @Bind(R.id.lv_dynamic)
    ListView mLvDynamic;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.tv_no)
    TextView mTvNo;
    BGABanner mBanner;

    private List<View> mAdList = new ArrayList<>();
    private int mPage;
    private boolean isLoading = false;
    private RequestQueue mQueue;
    private LoginUser.ResultBean mUserInfo;
    private boolean isFirst = true;
    private List<WholeDynamic.ResultBean> mDynamics = new ArrayList<>();
    private MyDynamicAdapter mMyListAdapater;
    private AMapLocationClient mAMapLocationClient;
    private boolean isRefresh = false;
    private View mFoot;
    private boolean isFoot = false;
    private Toast mToast;
    private boolean isSuccess = false;
    private ImageView mImageView;
    private MainActivity mActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mQueue = mActivity.getQueue();
        mToast = YuwanApplication.getToast();
        mFoot = View.inflate(mActivity, R.layout.foot, null);
        View inflate = View.inflate(mActivity, R.layout.dynamic_head_view, null);
        mLvDynamic.addHeaderView(inflate);
        mBanner = (BGABanner) inflate.findViewById(R.id.banner);
        mImageView = (ImageView) inflate.findViewById(R.id.iv_ad);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserInfo = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        initHead();
        mMyListAdapater = new MyDynamicAdapter(mDynamics, mActivity);
        mLvDynamic.setAdapter(mMyListAdapater);
        initEvent();
    }

    //初始化头部广告
    private void initHead() {
        if (NetUtil.is_Network_Available(mActivity)) {
            AMapLocation aMapLocation = LocationUtils.getAMapLocation();
            if (aMapLocation != null) {
                initBanner(aMapLocation);
            } else {
                mAMapLocationClient = LocationUtils.location(mActivity, this);
            }
        }
    }

    private void initBanner(AMapLocation aMapLocation) {
        String city = aMapLocation.getCity();
        double latitude = aMapLocation.getLatitude();
        double longitude = aMapLocation.getLongitude();
        try {
            String utf8 = URLEncoder.encode(city, "UTF-8");
            String uri = Constant.HOST + "getADList&&pageNo=1&pageSize=5&city=" + utf8
                    + "&lng=" + longitude + "&lat=" + latitude;

            Log.e("TAG", uri);
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (AnalysisJSON.analysisJson(response)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<NetbarAdvertisement>() {
                        }.getType();
                        NetbarAdvertisement netbarAdvertisement = gson.fromJson(response, type);
                        if (netbarAdvertisement != null) {
                            setBanner(netbarAdvertisement);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            mQueue.add(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //填充banner数据
    private void setBanner(NetbarAdvertisement netbarAdvertisement) {
        mAdList.clear();
        List<NetbarAdvertisement.ResultBean> result = netbarAdvertisement.getResult();
        if (result == null & result.size() == 0)
            return;
        for (NetbarAdvertisement.ResultBean resultBean : result) {
            List<String> adImg = resultBean.getADImg();
            if (adImg != null && adImg.size() != 0) {
                for (String uri : adImg) {
                    ImageView imageView = new ImageView(mActivity);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Picasso.with(mActivity).load(uri).resize(MyUtils.Dp2Px(mActivity, ViewHelper.getDisplayMetrics(mActivity).widthPixels), 300)
                            .centerCrop().into(imageView);
                    mAdList.add(imageView);
                }
            }
        }
        if (mBanner != null) {
            mBanner.setData(mAdList);
        }
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
                isRefresh = true;
                isFoot = false;
                initData();
                mSr.setRefreshing(false);
                mLvDynamic.removeFooterView(mFoot);
            }
        });

        mLvDynamic.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLvDynamic != null && mLvDynamic.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    mSr.setEnabled(enable);
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                    loadMore();
                }
            }
        });

        mLvDynamic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WholeDynamic.ResultBean resultBean = mDynamics.get(position);
                Intent intent = new Intent(mActivity, DynamicDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("dynamic", resultBean);
                intent.putExtra("dynamic_type", 0);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, NetbarADActivity.class));
            }
        });
    }

    private void loadMore() {
        isLoading = true;
        if (!isFoot) {
            mLvDynamic.removeFooterView(mFoot);
            mPage++;
            initData();
        }
    }


    private void initData() {
        if (NetUtil.is_Network_Available(mActivity)) {
            String uri;
            if (mUserInfo != null) {
                uri = Constant.HOST + "getActiveList&pageNo=" + mPage + "&pageSize=5&userId"
                        + mUserInfo.getUserId();
            } else {
                uri = Constant.HOST + "getActiveList&pageNo=" + mPage + "&pageSize=5";
            }

            StringRequest stringRequest = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<WholeDynamic>() {
                            }.getType();
                            WholeDynamic dynamic = gson.fromJson(response, type);
                            List<WholeDynamic.ResultBean> result1 = dynamic.getResult();
                            if (isRefresh)
                                mDynamics.clear();
                            mDynamics.addAll(result1);
                            mMyListAdapater.notifyDataSetChanged();
                            isRefresh = false;
                        } else {
                            if (isFirst) {
                                mTvNo.setVisibility(View.VISIBLE);
                                mTvNo.setText("暂无动态");
                            } else {
                                isFoot = true;
                                mLvDynamic.addFooterView(mFoot);
                                mTvNo.setVisibility(View.GONE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    isRefresh = false;
                    isLoading = false;
                    isFirst = false;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (isFirst) {
                        mTvNo.setVisibility(View.VISIBLE);
                        mTvNo.setText("加载失败");
                        mProgressBar.setVisibility(View.GONE);
                        isFirst = false;
                    }
                }
            });
            mQueue.add(stringRequest);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTvNo.setText("服务器抽风了，稍后再试");
            mTvNo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                LocationUtils.setAMapLocation(aMapLocation);
                initBanner(aMapLocation);
            }
        }
    }


    private void addFavour(String s, String avtiveId, final DynamicViewHolder viewHolder, boolean isLike) {

        String uri = Constant.HOST + "addFavour&" + s + "=" + avtiveId + " & userId = " + mUserInfo.getUserId();
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            boolean result = jsonObject.getBoolean("Result");
                            if (result) {
                                mToast.setText("点赞成功");
                                mToast.show();
                            }
                        } else {
                            String msg = jsonObject.getString("Msg");
                            mToast.setText("点赞失败," + msg);
                            mToast.show();
                            viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        isSuccess = false;
                    }
                } else {
                    mToast.setText("点赞失败");
                    mToast.show();
                    viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                    isSuccess = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mToast.setText("点赞失败");
                mToast.show();
                viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                isSuccess = false;
            }
        });
        mQueue.add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (mAMapLocationClient != null) {
            mAMapLocationClient.stopLocation();
            mAMapLocationClient.onDestroy();
        }

    }

    @OnClick({R.id.iv_camera})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_camera:
                if (mUserInfo != null)
                    startActivity(new Intent(mActivity, SendDynamicActivity.class));
                else
                    startActivity(new Intent(mActivity, LoginActivity.class));
                break;
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


    class MyDynamicAdapter extends MyBaseAdapter {
        private Context mContext;

        MyDynamicAdapter(List dataSource, Context context) {
            super(dataSource);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final DynamicViewHolder dynamicViewHolder;
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

            final WholeDynamic.ResultBean resultBean = (WholeDynamic.ResultBean) mDataSource.get(position);
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


            String address = resultBean.getAddress();
            if (!TextUtils.isEmpty(address))
                dynamicViewHolder.mTvAddress.setText(address);

            String addTime = resultBean.getCreateTime();
            if (!TextUtils.isEmpty(addTime))
                dynamicViewHolder.mTvTime.setText(addTime);

            if (mUserInfo != null) {
                final boolean isLoginUserLike = resultBean.isIsLoginUserLike();
                if (isLoginUserLike)
                    dynamicViewHolder.mIvDianzan.setImageResource(R.mipmap.dianzanhou);
                else
                    dynamicViewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                dynamicViewHolder.mIvDianzan.setOnClickListener(new View.OnClickListener() {
                    private boolean isLike = isLoginUserLike;

                    @Override
                    public void onClick(View v) {
                        if (isLike) {
                            dynamicViewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                            isLike = false;
                            addFavour("delFavour", resultBean.getActiveId(), dynamicViewHolder, isLike);
                        } else {
                            dynamicViewHolder.mIvDianzan.setImageResource(R.mipmap.dianzanhou);
                            isLike = true;
                            addFavour("addFavour", resultBean.getActiveId(), dynamicViewHolder, isLike);
                        }
                    }
                });
            }
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
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageview;
            if (convertView == null) {
                imageview = new ImageView(mActivity);
                imageview.setImageResource(R.mipmap.placeholder);
                imageview.setLayoutParams(new GridView.LayoutParams(235, 235));
                imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageview.setPadding(6, 6, 6, 6);
                AutoUtils.autoSize(imageview);
            } else {
                imageview = (ImageView) convertView;
            }
            Picasso.with(mActivity).load(mStrings[position]).into(imageview);
            return imageview;
        }
    }
}
