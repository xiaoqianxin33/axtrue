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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
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
import com.chinalooke.yuwan.activity.MainActivity;
import com.chinalooke.yuwan.activity.MoreCircleActivity;
import com.chinalooke.yuwan.activity.MyCircleActivity;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.Circle;
import com.chinalooke.yuwan.model.UserInfo;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.view.GrapeGridview;
import com.chinalooke.yuwan.view.NoSlidingListView;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CircleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CircleFragment extends Fragment implements AMapLocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.mapview)
    MapView mMapview;
    @Bind(R.id.lv_circle)
    NoSlidingListView mLvCircle;
    @Bind(R.id.tv_more)
    TextView mTvMore;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.gv_circle)
    GrapeGridview mGvCircle;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private double mLongitude;
    private double mLatitude;
    private AMapLocationClient mLocationClient;
    private RequestQueue mQueue;
    private Toast mToast;
    private Circle mCircle;
    private AMap mMap;
    private MyAdapt mMyAdapt;
    List<Circle.ResultBean> mCircles = new ArrayList<>();
    private int mPage = 1;
    private UserInfo mUserInfo;
    private Circle mMyCircle;
    private List<Circle.ResultBean> mMyCircleResult = new ArrayList<>();
    private GridAdapt mGridAdapt;


    public CircleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CircleFragment.
     */
    public static CircleFragment newInstance(String param1, String param2) {
        CircleFragment fragment = new CircleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapview.onCreate(savedInstanceState);
        mMap = mMapview.getMap();
        mUserInfo = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        mQueue = ((MainActivity) getActivity()).getQueue();
        mToast = YuwanApplication.getToast();
        mLatitude = (((MainActivity) getActivity()).getLatitude());
        mLongitude = ((MainActivity) getActivity()).getLongitude();
        mMyAdapt = new MyAdapt();
        mLvCircle.setAdapter(mMyAdapt);
        mGridAdapt = new GridAdapt();
        mGvCircle.setAdapter(mGridAdapt);
        initData();
    }

    private void initView() {
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

        List<Circle.ResultBean> result = mCircle.getResult();

        if (result != null && result.size() != 0) {
            mCircles.addAll(result);
            mMyAdapt.notifyDataSetChanged();
            for (Circle.ResultBean resultBean : result) {
                LatLng latLng2 = new LatLng(Double.parseDouble(resultBean.getLat()), Double.parseDouble(resultBean.getLng()));
                mMap.addMarker(new MarkerOptions().position(latLng2).title(resultBean.getGroupName()).snippet("DefaultMarker"));
            }
        }

        initEvent();
    }

    private void initEvent() {
        mLvCircle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }


    private void initData() {
        if (mLatitude != 0 && mLongitude != 0) {
            if (NetUtil.is_Network_Available(getActivity())) {
                getNearbyCircle();
                if (mUserInfo != null)
                    getMyCircle();
            } else {
                mToast.setText("网络不可用，请检查网络连接");
                mToast.show();
            }
        } else {
            location();
        }
    }

    private void getMyCircle() {
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
                    mMyCircle = gson.fromJson(response, type);
                    if (mMyCircle != null) {
                        mMyCircleResult = mMyCircle.getResult();
                        mGridAdapt.notifyDataSetChanged();
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(stringRequest);
    }

    private void getNearbyCircle() {
        String uri = Constant.HOST + "getGroupWithGPS&lng=" + mLongitude + "&lat=" + mLatitude + "&pageNo=" + mPage + "&pageSize=2";

        StringRequest stringRequest = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressBar.setVisibility(View.GONE);
                mTvMore.setVisibility(View.VISIBLE);
                String substring = response.substring(11, 15);
                if ("true".equals(substring)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Circle>() {
                    }.getType();
                    mCircle = gson.fromJson(response, type);
                    if (mCircle != null) {
                        initView();
                    }

                } else {
                    MyUtils.showMsg(mToast, response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                mTvMore.setVisibility(View.VISIBLE);
            }
        });

        mQueue.add(stringRequest);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null)
            mLocationClient.stopLocation();
        if (mMapview != null)
            mMapview.onDestroy();
    }

    private void location() {
        mLocationClient = new AMapLocationClient(getActivity());
        //初始化定位参数
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        //设置定位监听

        locationOption.setOnceLocation(true);
        mLocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        locationOption.setInterval(2000);
        //设置定位参数
        mLocationClient.setLocationOption(locationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mLongitude = aMapLocation.getLongitude();
            mLatitude = aMapLocation.getLatitude();
            if (NetUtil.is_Network_Available(getActivity())) {
                getNearbyCircle();
            } else {
                mToast.setText("网络不可用，请检查网络连接");
                mToast.show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.tv_more, R.id.iv_wodequanzi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_more:
                if (mLongitude != 0 && mLatitude != 0) {
                    Intent intent = new Intent();
                    intent.putExtra("longitude", mLongitude);
                    intent.putExtra("latitude", mLatitude);
                    intent.setClass(getActivity(), MoreCircleActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.iv_wodequanzi:
                startActivity(new Intent(getActivity(), MyCircleActivity.class));
                break;
        }
    }

    class MyAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return mCircles.size();
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
            Circle.ResultBean resultBean = mCircles.get(position);
            Picasso.with(getActivity()).load(resultBean.getHeadImg()).resize(MyUtils.Dp2Px(getActivity()
                    , 80), MyUtils.Dp2Px(getActivity(), 80)).centerCrop().into(viewHolder.mIvCircleImage);
            viewHolder.mTvCircleName.setText(resultBean.getGroupName());
            viewHolder.mTvCircleDetails.setText(resultBean.getDetails());
            viewHolder.mTvDiscountCircle.setText(resultBean.getDistance() + "m");
            return convertView;
        }

    }

    class GridAdapt extends BaseAdapter {
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
                convertView = View.inflate(getActivity(), R.layout.item_gamelist_gradview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Circle.ResultBean resultBean = mMyCircleResult.get(position);
            viewHolder.mTvGameName.setText(resultBean.getGroupName());
            Picasso.with(getActivity()).load(resultBean.getHeadImg()).resize(120, 120).centerCrop()
                    .into(viewHolder.mIvGameimage);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.iv_gameimage)
            ImageView mIvGameimage;
            @Bind(R.id.tv_game_name)
            TextView mTvGameName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    static class ViewHolder {
        @Bind(R.id.iv_circle_image)
        ImageView mIvCircleImage;
        @Bind(R.id.tv_circle_name)
        TextView mTvCircleName;
        @Bind(R.id.tv_circle_details)
        TextView mTvCircleDetails;
        @Bind(R.id.tv_discount_circle)
        TextView mTvDiscountCircle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
