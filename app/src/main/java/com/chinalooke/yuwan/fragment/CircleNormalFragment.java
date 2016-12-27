package com.chinalooke.yuwan.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.CircleDynamicActivity;
import com.chinalooke.yuwan.activity.CreateCircleActivity;
import com.chinalooke.yuwan.activity.LoginActivity;
import com.chinalooke.yuwan.activity.MainActivity;
import com.chinalooke.yuwan.activity.MoreCircleActivity;
import com.chinalooke.yuwan.bean.Circle;
import com.chinalooke.yuwan.bean.CircleAD;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.ImageUtils;
import com.chinalooke.yuwan.utils.LocationUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.ViewHelper;
import com.chinalooke.yuwan.view.MapContainer;
import com.chinalooke.yuwan.view.NoSlidingListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;

import static com.chinalooke.yuwan.constant.Constant.MIN_CLICK_DELAY_TIME;
import static com.chinalooke.yuwan.constant.Constant.lastClickTime;

/**
 * 圈子第一页fragment
 * Created by xiao on 2016/11/22.
 */

public class CircleNormalFragment extends Fragment implements AMapLocationListener {

    @Bind(R.id.tv_address)
    TextView mTvAddress;
    @Bind(R.id.mapview)
    MapView mMapview;
    @Bind(R.id.lv_circle)
    NoSlidingListView mLvCircle;
    @Bind(R.id.tv_more)
    TextView mTvMore;
    @Bind(R.id.gd_interest)
    GridView mGdInterest;
    @Bind(R.id.gd_hot)
    GridView mGdHot;
    @Bind(R.id.ll_interest)
    LinearLayout mLlInterest;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.rl_create_circle)
    RelativeLayout mRlCreateCircle;
    @Bind(R.id.bgabanner)
    BGABanner mBanner;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    @Bind(R.id.mapContainer)
    MapContainer mMapContainer;
    private Circle mCircle;
    private RequestQueue mQueue;
    private double mLatitude;
    private double mLongitude;
    private AMapLocationClient mAMapLocationClient;
    private List<Circle.ResultBean> mNearbyCircles = new ArrayList<>();
    private List<Circle.ResultBean> mInterestCircles = new ArrayList<>();
    private List<Circle.ResultBean> mHotCircles = new ArrayList<>();
    private List<View> mAdList = new ArrayList<>();
    private MyAdapt mMyAdapt;
    private LoginUser.ResultBean mUserInfo;
    private GridAdapt mInterestGridAdapt;
    private AMap mMap;
    private GridAdapt mHotGridAdapt;
    private MainActivity mActivity;
    private HashMap<Marker, Circle.ResultBean> mMarkerResultBeanHashMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_normal, container, false);
        ButterKnife.bind(this, view);
        mQueue = YuwanApplication.getQueue();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapview.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mMapContainer.setScrollView(mScrollView);
        mMap = mMapview.getMap();
        mMap.clear();
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getActivity(), LoginUserInfoUtils.KEY);
        initData();
        initView();
        initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapview != null) {
            mMapview.onResume();
        }
    }

    private void initEvent() {
        //附近圈子item点击事件
        mLvCircle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Circle.ResultBean resultBean = mNearbyCircles.get(position);
                Intent intent = new Intent(getActivity(), CircleDynamicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("circle", resultBean);
                //设置圈子类型为附近圈子
                intent.putExtra("circle_type", 0);
                intent.putExtras(bundle);
                startActivity(intent);
                addHits(resultBean);
            }
        });

        //兴趣圈子 item点击事件
        mGdInterest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Circle.ResultBean resultBean = mInterestCircles.get(position);
                Intent intent = new Intent(getActivity(), CircleDynamicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("circle", resultBean);
                //设置圈子类型为兴趣圈子
                intent.putExtras(bundle);
                startActivity(intent);
                addHits(resultBean);
            }
        });

        //热门圈子 item点击事件
        mGdHot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Circle.ResultBean resultBean = mHotCircles.get(position);
                Intent intent = new Intent(getActivity(), CircleDynamicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("circle", resultBean);
                //设置圈子类型为兴趣圈子
                intent.putExtras(bundle);
                startActivity(intent);
                addHits(resultBean);
            }
        });

        mMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Circle.ResultBean resultBean = mMarkerResultBeanHashMap.get(marker);
                if (resultBean != null) {
                    Intent intent = new Intent(getActivity(), CircleDynamicActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("circle", resultBean);
                    //设置圈子类型为附近圈子
                    intent.putExtra("circle_type", 0);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    addHits(resultBean);
                }
                return true;
            }
        });
    }

    //增加圈子点击量
    private void addHits(Circle.ResultBean resultBean) {
        String url = Constant.HOST + "addHits&groupId=" + resultBean.getGroupId();
        StringRequest request = new StringRequest(url, null, null);
        mQueue.add(request);
    }

    private void initView() {
        mMyAdapt = new MyAdapt();
        mLvCircle.setAdapter(mMyAdapt);
        mInterestGridAdapt = new GridAdapt(mInterestCircles);
        mGdInterest.setAdapter(mInterestGridAdapt);
        mHotGridAdapt = new GridAdapt(mHotCircles);
        mGdHot.setAdapter(mHotGridAdapt);
    }

    private void initData() {
        AMapLocation aMapLocation = LocationUtils.getAMapLocation();
        if (aMapLocation != null) {
            mLatitude = aMapLocation.getLatitude();
            mLongitude = aMapLocation.getLongitude();
            String address = aMapLocation.getDistrict() + aMapLocation.getStreet() + aMapLocation.getStreetNum();
            mTvAddress.setText(address);
            initAMap();
            getADListForSpace(aMapLocation);
            getNearbyCircle();

        } else {
            mAMapLocationClient = LocationUtils.location(getActivity(), this);
        }
        if (mUserInfo == null) {
            mLlInterest.setVisibility(View.GONE);
        } else {
            mLlInterest.setVisibility(View.VISIBLE);
            getInterestCircle();
        }

        getHotCircle();
    }

    //获取热门圈子数据
    private void getHotCircle() {
        if (NetUtil.is_Network_Available(getActivity())) {
            String uri;
            if (mUserInfo != null)
                uri = Constant.HOST + "getGroupListWithType&groupType=hot&pageNo=1&pageSize=6&userId=" + mUserInfo.getUserId();
            else
                uri = Constant.HOST + "getGroupListWithType&groupType=hot&pageNo=1&pageSize=6&userId=";
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (AnalysisJSON.analysisJson(response)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Circle>() {
                        }.getType();
                        Circle mCircle = gson.fromJson(response, type);
                        if (mCircle != null) {
                            List<Circle.ResultBean> circles = mCircle.getResult();
                            mHotCircles.addAll(circles);
                            mHotGridAdapt.notifyDataSetChanged();
                        }
                    }
                }
            }, null);
            mQueue.add(request);
        }
    }

    //获取顶部广告图片
    private void getADListForSpace(AMapLocation aMapLocation) {
        String city = aMapLocation.getCity();
        String url = null;
        try {
            url = Constant.HOST + "getADListForSpace&pageNo=1&pageSize=5&city=" + URLEncoder.encode(city, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AnalysisJSON.analysisJson(response)) {
                    mBanner.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    CircleAD circleAD = gson.fromJson(response, CircleAD.class);
                    List<CircleAD.ResultBean> result = circleAD.getResult();
                    if (result != null)
                        setBanner(result);
                } else {
                    mBanner.setVisibility(View.GONE);
                }
            }
        }, null);

        mQueue.add(request);
    }

    //初始化轮播图数据
    private void setBanner(List<CircleAD.ResultBean> result) {
        mAdList.clear();

        for (CircleAD.ResultBean resultBean : result) {
            List<String> adImg = resultBean.getADImg();
            if (adImg.size() != 0) {
                String img = adImg.get(0);
                ImageView imageView = new ImageView(getActivity());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                String loadImageUrl = ImageEngine.getLoadImageUrl(mActivity, img, MyUtils.Dp2Px(getActivity(), ViewHelper.getDisplayMetrics(getActivity()).widthPixels), 340);
                Picasso.with(getActivity()).load(loadImageUrl).into(imageView);
                mAdList.add(imageView);
            }
        }
        if (mBanner != null) {
            mBanner.setData(mAdList);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (mAMapLocationClient != null) {
            mAMapLocationClient.stopLocation();
            mAMapLocationClient.onDestroy();
        }
        if (mMapview != null)
            mMapview.onDestroy();
    }

    @OnClick({R.id.iv_refresh, R.id.tv_more, R.id.rl_create_circle})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.iv_refresh:
                    mMap.clear();
                    mAMapLocationClient = LocationUtils.location(getActivity(), this);
                    break;
                case R.id.tv_more:
                    if (mLongitude != 0 && mLatitude != 0) {
                        Intent intent = new Intent();
                        intent.putExtra("longitude", mLongitude);
                        intent.putExtra("latitude", mLatitude);
                        intent.setClass(getActivity(), MoreCircleActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.rl_create_circle:
                    if (mUserInfo != null)
                        startActivity(new Intent(getActivity(), CreateCircleActivity.class));
                    else {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        mActivity.finish();
                    }
                    break;
            }
        }
    }

    //获取附近圈子
    private void getNearbyCircle() {
        String uri = Constant.HOST + "getGroupWithGPS&lng=" + mLongitude + "&lat=" + mLatitude + "&pageNo=" + 0 + "&pageSize=2";
        StringRequest stringRequest = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mPbLoad.setVisibility(View.GONE);
                if (AnalysisJSON.analysisJson(response)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Circle>() {
                    }.getType();
                    mCircle = gson.fromJson(response, type);
                    if (mCircle != null) {
                        List<Circle.ResultBean> circles = mCircle.getResult();
                        mNearbyCircles.addAll(circles);
                        mMyAdapt.notifyDataSetChanged();
                        setAMap();
                        mTvMore.setEnabled(true);
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.getString("Msg");
                        mRlCreateCircle.setVisibility(View.VISIBLE);
                        mTvMore.setVisibility(View.VISIBLE);
                        mTvMore.setText(msg);
                        mTvMore.setEnabled(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mPbLoad.setVisibility(View.GONE);
                mTvMore.setVisibility(View.VISIBLE);
            }
        });
        mQueue.add(stringRequest);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                LocationUtils.setAMapLocation(aMapLocation);
                mLatitude = aMapLocation.getLatitude();
                mLongitude = aMapLocation.getLongitude();
                String address = aMapLocation.getDistrict() + aMapLocation.getStreet() + aMapLocation.getStreetNum();
                mTvAddress.setText(address);
                initAMap();
                getNearbyCircle();
                getADListForSpace(aMapLocation);
            }
        }
    }

    class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return mNearbyCircles.size() <= 2 ? mNearbyCircles.size() : 2;
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
                convertView = View.inflate(mActivity, R.layout.item_circle_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Circle.ResultBean resultBean = mNearbyCircles.get(position);
            String headImg = resultBean.getHeadImg();
            if (!TextUtils.isEmpty(headImg)) {
                String loadImageUrl = ImageEngine.getLoadImageUrl(mActivity, headImg, MyUtils.Dp2Px(mActivity
                        , 80), MyUtils.Dp2Px(mActivity, 80));
                Picasso.with(mActivity).load(loadImageUrl).into(viewHolder.mIvCircleImage);
            }
            viewHolder.mTvCircleName.setText(resultBean.getGroupName());
            viewHolder.mTvCircleDetails.setText(resultBean.getDetails());
            viewHolder.mTvDiscountCircle.setText(getString(R.string.distance, resultBean.getDistance()));
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

    //得到兴趣圈子
    private void getInterestCircle() {
        String uri = Constant.HOST + "getGroupListWithType&userId=" + mUserInfo.getUserId()
                + "&groupType=interest";
        Log.e("TAG", uri);
        StringRequest stringRequest = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String substring = response.substring(11, 15);
                if ("true".equals(substring)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Circle>() {
                    }.getType();
                    Circle circle = gson.fromJson(response, type);
                    if (circle != null) {
                        List<Circle.ResultBean> interestCircles = circle.getResult();
                        mInterestCircles.addAll(interestCircles);
                        mInterestGridAdapt.notifyDataSetChanged();
                    }
                }
            }
        }, null);
        mQueue.add(stringRequest);
    }

    class GridAdapt extends BaseAdapter {

        private List<Circle.ResultBean> mList;

        GridAdapt(List<Circle.ResultBean> circles) {
            this.mList = circles;
        }

        @Override
        public int getCount() {
            return mList.size() <= 6 ? mList.size() : 6;
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.item_circle_gridview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Circle.ResultBean resultBean = mList.get(position);
            viewHolder.mTvGameName.setText(resultBean.getGroupName());
            viewHolder.mTvView.setText(getString(R.string.popularity, resultBean.getViews()));
            String headImg = resultBean.getHeadImg();
            if (!TextUtils.isEmpty(headImg)) {
                String loadImageUrl = ImageEngine.getLoadImageUrl(mActivity, headImg, 200, 200);
                Picasso.with(mActivity).load(loadImageUrl)
                        .into(viewHolder.mIvGameimage);
            }
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.iv_image)
            ImageView mIvGameimage;
            @Bind(R.id.tv_name)
            TextView mTvGameName;
            @Bind(R.id.tv_view)
            TextView mTvView;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    private void setAMap() {
        if (mNearbyCircles.size() != 0) {
            for (final Circle.ResultBean resultBean : mNearbyCircles) {
                String bgImage = resultBean.getHeadImg();
                if (!TextUtils.isEmpty(bgImage)) {
                    ImageRequest request = new ImageRequest(bgImage, new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            LatLng latLng2 = new LatLng(Double.parseDouble(resultBean.getLat()), Double.parseDouble(resultBean.getLng()));
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng2);
                            markerOptions.draggable(false);
                            markerOptions.title(resultBean.getGroupName());
                            Bitmap bitmap = ImageUtils.toRound(response);
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                            Marker marker = mMap.addMarker(markerOptions);
                            mMarkerResultBeanHashMap.put(marker, resultBean);
                        }
                    }, 100, 100, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, null);
                    mQueue.add(request);
                }
            }
        }
    }

    private void initAMap() {
        if (mLatitude != 0 && mLongitude != 0) {
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    latLng,//新的中心点坐标
                    12, //新的缩放级别
                    30, //俯仰角0°~45°（垂直与地图时为0）
                    0  ////偏航角 0~360° (正北方为0)
            ));
            mMap.moveCamera(cameraUpdate);
            mMap.addMarker(new MarkerOptions().
                    position(latLng).
                    title("我的位置").
                    snippet("我"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapview != null)
            mMapview.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapview != null)
            mMapview.onSaveInstanceState(outState);
    }

}
