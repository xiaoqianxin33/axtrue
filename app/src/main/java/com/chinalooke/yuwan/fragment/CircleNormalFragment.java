package com.chinalooke.yuwan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.CircleDynamicActivity;
import com.chinalooke.yuwan.activity.MoreCircleActivity;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.Circle;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LocationUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.view.NoSlidingListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.chinalooke.yuwan.constant.Constant.MIN_CLICK_DELAY_TIME;
import static com.chinalooke.yuwan.constant.Constant.lastClickTime;

/**
 * 圈子第一页fragment
 * Created by xiao on 2016/11/22.
 */

public class CircleNormalFragment extends Fragment implements AMapLocationListener {

    @Bind(R.id.iv_image)
    ImageView mIvImage;
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
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.ll_interest)
    LinearLayout mLlInterest;
    private Circle mCircle;
    private RequestQueue mQueue;
    private Toast mToast;
    private double mLatitude;
    private double mLongitude;
    private AMapLocationClient mAMapLocationClient;
    private List<Circle.ResultBean> mNearbyCircles = new ArrayList<>();
    private List<Circle.ResultBean> mInterestCircles = new ArrayList<>();
    private List<Circle.ResultBean> mHotCircles = new ArrayList<>();
    private MyAdapt mMyAdapt;
    private LoginUser.ResultBean mUserInfo;
    private GridAdapt mInterestGridAdapt;
    private GridAdapt mHotGridAdapt;
    private AMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_normal, container, false);
        ButterKnife.bind(this, view);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getActivity(), LoginUserInfoUtils.KEY);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapview.onCreate(savedInstanceState);
        mMap = mMapview.getMap();
        initView();
        initData();
        initEvent();
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
                intent.putExtra("circle_type", 0);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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

    @OnClick({R.id.iv_refresh, R.id.tv_more})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.iv_refresh:
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
            }
        }
    }


    private void getNearbyCircle() {
        String uri = Constant.HOST + "getGroupWithGPS&lng=" + mLongitude + "&lat=" + mLatitude + "&pageNo=" + 0 + "&pageSize=2";

        StringRequest stringRequest = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                    }
                } else {
                    MyUtils.showMsg(mToast, response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mPbLoad.setVisibility(View.GONE);
                mTvMore.setVisibility(View.VISIBLE);
                mToast.setText("网络不给力啊，换个地方试试");
                mToast.show();
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
                convertView = View.inflate(getActivity(), R.layout.item_circle_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Circle.ResultBean resultBean = mNearbyCircles.get(position);
            Picasso.with(getActivity()).load(resultBean.getHeadImg()).resize(MyUtils.Dp2Px(getActivity()
                    , 80), MyUtils.Dp2Px(getActivity(), 80)).centerCrop().into(viewHolder.mIvCircleImage);
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


    private void getInterestCircle() {
        String[] strings = mUserInfo.getGameId();
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i == strings.length - 1) {
                stringBuffer.append(strings[i]);
            } else {
                stringBuffer.append(strings[i]).append(",");
            }
        }
        String uri = Constant.HOST + "getGroupListWithType&userId=" + mUserInfo.getUserId()
                + "&interest=" + stringBuffer.toString();
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
                } else {
                    MyUtils.showMsg(mToast, response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
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
                convertView = View.inflate(getActivity(), R.layout.item_circle_gridview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Circle.ResultBean resultBean = mList.get(position);
            viewHolder.mTvGameName.setText(resultBean.getGroupName());
            viewHolder.mTvView.setText("人气  " + resultBean.getViews());
            Picasso.with(getActivity()).load(resultBean.getHeadImg()).resize(120, 120).centerCrop()
                    .into(viewHolder.mIvGameimage);
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
            for (Circle.ResultBean resultBean : mNearbyCircles) {
                LatLng latLng2 = new LatLng(Double.parseDouble(resultBean.getLat()), Double.parseDouble(resultBean.getLng()));
                mMap.addMarker(new MarkerOptions().position(latLng2).title(resultBean.getGroupName()).snippet("DefaultMarker"));
            }
        }
    }

    private void initAMap() {
        if (mLatitude != 0 && mLongitude != 0) {
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    latLng,//新的中心点坐标
                    15, //新的缩放级别
                    30, //俯仰角0°~45°（垂直与地图时为0）
                    0  ////偏航角 0~360° (正北方为0)
            ));
            mMap.moveCamera(cameraUpdate);
            mMap.addMarker(new MarkerOptions().
                    position(latLng).
                    title("我的位置").
                    snippet("DefaultMarker"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapview != null)
            mMapview.onResume();
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
