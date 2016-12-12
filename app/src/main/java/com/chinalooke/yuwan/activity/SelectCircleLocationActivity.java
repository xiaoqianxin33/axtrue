package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.utils.LocationUtils;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.id.progress;

//选择圈子位置界面
public class SelectCircleLocationActivity extends AutoLayoutActivity implements GeocodeSearch.OnGeocodeSearchListener, PoiSearch.OnPoiSearchListener {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.mapView)
    MapView mMapView;
    @Bind(R.id.list_view)
    ListView mListView;
    private double mLatitude;
    private double mLongitude;
    private AMap mMap;
    private PoiSearch.Query mQuery;
    private List<PoiItem> mList = new ArrayList<>();
    private MyAdapter mMyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ciecle_location);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiItem poiItem = mList.get(position);
                String address = poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getSnippet();
                Intent intent = new Intent();
                intent.putExtra("address", address);
                setResult(5, intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mMapView != null)
            mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

    private void initView() {
        mTvTitle.setText("选择位置");
        mMyAdapter = new MyAdapter(mList);
        mListView.setAdapter(mMyAdapter);
    }

    private void initData() {
        mMap = mMapView.getMap();
        AMapLocation aMapLocation = LocationUtils.getAMapLocation();
        if (aMapLocation != null) {
            mLatitude = aMapLocation.getLatitude();
            mLongitude = aMapLocation.getLongitude();
            initAMap(aMapLocation);
        }
    }

    //初始化高德地图
    private void initAMap(AMapLocation aMapLocation) {
        if (mLatitude != 0 && mLongitude != 0) {
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    latLng,//新的中心点坐标
                    16, //新的缩放级别
                    30, //俯仰角0°~45°（垂直与地图时为0）
                    0  ////偏航角 0~360° (正北方为0)
            ));
            mMap.moveCamera(cameraUpdate);
            Marker marker = mMap.addMarker(new MarkerOptions().
                    position(latLng).
                    title("我的位置").
                    snippet("我").draggable(true));

            mMap.addCircle(new CircleOptions().
                    center(latLng).
                    radius(100).
                    fillColor(Color.argb(progress, 1, 1, 1)).
                    strokeColor(Color.argb(progress, 1, 1, 1)).
                    strokeWidth(15));

            mQuery = new PoiSearch.Query("", "地名地址信息", aMapLocation.getCity());
            PoiSearch poiSearch = new PoiSearch(this, mQuery);
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(marker.getPosition().latitude,
                    marker.getPosition().longitude), 500));//设置周边搜索的中心点以及半径
            mQuery.setPageSize(5);
            mQuery.setPageNum(1);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();
        }
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }


    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(mQuery)) {// 是否是同一条
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = result
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        mMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(mMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                        mList.addAll(poiItems);
                        mMyAdapter.notifyDataSetChanged();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                    }
                }
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    class MyAdapter extends MyBaseAdapter {

        public MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(SelectCircleLocationActivity.this, R.layout.item_selectlocation_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            PoiItem poiItem = mList.get(position);
            String title = poiItem.getTitle();
            if (!TextUtils.isEmpty(title))
                viewHolder.mName.setText(title);

            String address = poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getSnippet();
            if (!TextUtils.isEmpty(address))
                viewHolder.mTvAddress.setText(address);
            return convertView;
        }

    }

    static class ViewHolder {
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.tv_address)
        TextView mTvAddress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
