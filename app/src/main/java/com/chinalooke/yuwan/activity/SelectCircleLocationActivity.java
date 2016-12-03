package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.utils.LocationUtils;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//选择圈子位置界面
public class SelectCircleLocationActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.mapView)
    MapView mMapView;
    @Bind(R.id.list_view)
    ListView mListView;
    private double mLatitude;
    private double mLongitude;
    private AMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ciecle_location);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mMap = mMapView.getMap();
        AMapLocation aMapLocation = LocationUtils.getAMapLocation();
        if (aMapLocation != null) {
            mLatitude = aMapLocation.getLatitude();
            mLongitude = aMapLocation.getLongitude();
            initAMap();
        }
    }

    //初始化高德地图
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

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
