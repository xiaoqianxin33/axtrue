package com.chinalooke.yuwan.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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
import android.widget.RelativeLayout;
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
import com.chinalooke.yuwan.activity.ImagePagerActivity;
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
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.LocationUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
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
    private ImageView mImageView;
    private MainActivity mActivity;
    private boolean isBanner = true;


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
        mBanner = (BGABanner) inflate.findViewById(R.id.bgabanner);
        mImageView = (ImageView) inflate.findViewById(R.id.iv_ad);
        initHead();
        mMyListAdapater = new MyDynamicAdapter(mDynamics, mActivity);
        mLvDynamic.setAdapter(mMyListAdapater);
        initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserInfo = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
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
            String uri = Constant.HOST + "getADList&&pageNo=1&pageSize=5"
                    + "&lng=" + longitude + "&lat=" + latitude + "&city=" + utf8;
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (AnalysisJSON.analysisJson(response)) {
                        isBanner = true;
                        mBanner.setVisibility(View.VISIBLE);
                        Gson gson = new Gson();
                        Type type = new TypeToken<NetbarAdvertisement>() {
                        }.getType();
                        NetbarAdvertisement netbarAdvertisement = gson.fromJson(response, type);
                        if (netbarAdvertisement != null) {
                            setBanner(netbarAdvertisement);
                        }
                    } else {
                        isBanner = false;
                        mBanner.setVisibility(View.GONE);
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
        if (result == null || result.size() == 0)
            return;
        for (NetbarAdvertisement.ResultBean resultBean : result) {
            List<String> adImg = resultBean.getADImg();
            if (adImg != null && adImg.size() != 0) {
                for (String uri : adImg) {
                    ImageView imageView = new ImageView(mActivity);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    String loadImageUrl = ImageEngine.getLoadImageUrl(mActivity, uri, ViewHelper.getDisplayMetrics(mActivity).widthPixels, 200);
                    Picasso.with(mActivity).load(loadImageUrl)
                            .into(imageView);
                    AutoUtils.autoSize(imageView);
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
                isFirst = true;
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
                if (isBanner) {
                    if (position != 0) {
                        WholeDynamic.ResultBean resultBean = mDynamics.get(position - 1);
                        Intent intent = new Intent(mActivity, DynamicDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dynamic", resultBean);
                        intent.putExtra("dynamic_type", 0);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    WholeDynamic.ResultBean resultBean = mDynamics.get(position - 1);
                    Intent intent = new Intent(mActivity, DynamicDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("dynamic", resultBean);
                    intent.putExtra("dynamic_type", 0);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
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
                uri = Constant.HOST + "getActiveList&pageNo=" + mPage + "&pageSize=5&userId="
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

    private void addFavour(String s, String avtiveId, final DynamicViewHolder viewHolder, final boolean isLike, final WholeDynamic.ResultBean resultBean) {

        String url = Constant.HOST + s + "&activeId=" + avtiveId + "&userId=" + mUserInfo.getUserId() + "&activeType=";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            if (isLike)
                                mToast.setText("取消点赞成功");
                            else
                                mToast.setText("点赞成功");
                            resultBean.setIsLoginUserLike(!isLike);
                            boolean loginUserLike = resultBean.isLoginUserLike();
                            viewHolder.mIvDianzan.setImageResource(loginUserLike ? R.mipmap.dianzanhou : R.mipmap.dianzan);
                            mToast.show();

                        } else {
                            String msg = jsonObject.getString("Msg");
                            mToast.setText("点赞失败," + msg);
                            mToast.show();
                            boolean loginUserLike = resultBean.isLoginUserLike();
                            viewHolder.mIvDianzan.setImageResource(loginUserLike ? R.mipmap.dianzanhou : R.mipmap.dianzan);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mToast.setText("点赞失败");
                    mToast.show();
                    viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mToast.setText("点赞失败");
                mToast.show();
                viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
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
        RelativeLayout mRelativeLayout;
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
                dynamicViewHolder.mRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.rl_dianzan);
                convertView.setTag(dynamicViewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                dynamicViewHolder = (DynamicViewHolder) convertView.getTag();
            }

            final WholeDynamic.ResultBean resultBean = (WholeDynamic.ResultBean) mDataSource.get(position);
            String headImg = resultBean.getHeadImg();
            if (!TextUtils.isEmpty(headImg)) {
                String loadImageUrl = ImageEngine.getLoadImageUrl(mActivity, headImg, 72, 72);
                Picasso.with(mContext).load(loadImageUrl).into(dynamicViewHolder.mRoundedImageView);
            }
            String content = resultBean.getContent();
            if (!TextUtils.isEmpty(content))
                dynamicViewHolder.mTvContent.setText(content);
            String nickName = resultBean.getNickName();
            if (!TextUtils.isEmpty(nickName))
                dynamicViewHolder.mTvName.setText(nickName);

            String images = resultBean.getImages();
            if (!TextUtils.isEmpty(images)) {
                dynamicViewHolder.mGridView.setVisibility(View.VISIBLE);
                final String[] split = images.split(",");
                dynamicViewHolder.mGridView.setAdapter(new GridAdapter(split));
                //图片点击事件监听
                dynamicViewHolder.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(mActivity, ImagePagerActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putStringArray("url", split);
                        intent.putExtras(bundle);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                });
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
            if (!TextUtils.isEmpty(addTime)) {
                DateUtils.setDynamicTime(addTime, dynamicViewHolder.mTvTime);
            }

            if (mUserInfo != null) {
                final boolean isLoginUserLike = resultBean.isIsLoginUserLike();
                if (isLoginUserLike)
                    dynamicViewHolder.mIvDianzan.setImageResource(R.mipmap.dianzanhou);
                else
                    dynamicViewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                dynamicViewHolder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                    private boolean isLike = isLoginUserLike;

                    @Override
                    public void onClick(View v) {
                        if (isLike) {
                            dynamicViewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                            isLike = false;
                            addFavour("delFavour", resultBean.getActiveId(), dynamicViewHolder, isLike, resultBean);
                        } else {
                            dynamicViewHolder.mIvDianzan.setImageResource(R.mipmap.dianzanhou);
                            isLike = true;
                            addFavour("addFavour", resultBean.getActiveId(), dynamicViewHolder, isLike, resultBean);
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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.item_dynamic_gridview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String loadImageUrl = ImageEngine.getLoadImageUrl(mActivity, mStrings[position], 235, 235);
            Picasso.with(mActivity).load(loadImageUrl).into(viewHolder.mImage);
            return convertView;
        }

    }

    static class ViewHolder {
        @Bind(R.id.image)
        ImageView mImage;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
