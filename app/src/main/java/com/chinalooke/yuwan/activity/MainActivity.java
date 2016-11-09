package com.chinalooke.yuwan.activity;

import android.Manifest;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.fragment.BattleFieldFragment;
import com.chinalooke.yuwan.fragment.BlackFragment;
import com.chinalooke.yuwan.fragment.CircleFragment;
import com.chinalooke.yuwan.fragment.DynamicFragment;
import com.chinalooke.yuwan.fragment.WodeFragment;
import com.chinalooke.yuwan.fragment.YueZhanFragment;
import com.chinalooke.yuwan.utils.ImageUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends FragmentActivity implements AMapLocationListener, EasyPermissions.PermissionCallbacks {

    @Bind(R.id.rg_main)
    RadioGroup mRgMain;
    @Bind(R.id.rb_battlefield)
    RadioButton mRbBattlefield;
    @Bind(R.id.rb_circle)
    RadioButton mRbCircle;
    @Bind(R.id.rb_yuezhan)
    RadioButton mRbYuezhan;
    @Bind(R.id.rb_dynamic)
    RadioButton mRbDynamic;
    @Bind(R.id.rb_wode)
    RadioButton mRbWode;
    private FragmentManager mFragmentManager;
    public RequestQueue mQueue;
    private AMapLocationClient mLocationClient;
    private double mLongitude;
    private double mLatitude;
    private Fragment mContent;
    private BattleFieldFragment mBattleFieldFragment;
    private CircleFragment mCircleFragment;
    private DynamicFragment mDynamicFragment;
    private WodeFragment mWodeFragment;
    private YueZhanFragment mYueZhanFragment;
    private BlackFragment mBlackFragment;
    private int RC_CAMERA_AND_WIFI = 1;

    public RequestQueue getQueue() {
        return mQueue;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.avtivity_main);
        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mBattleFieldFragment = new BattleFieldFragment();
        mCircleFragment = new CircleFragment();
        mDynamicFragment = new DynamicFragment();
        mWodeFragment = new WodeFragment();
        mYueZhanFragment = new YueZhanFragment();
        mBlackFragment = new BlackFragment();
        switchContent(mBlackFragment, mBattleFieldFragment);
        requirPermisson();
        initView();
        initEvent();
    }

    private void requirPermisson() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            location();
        } else {
            EasyPermissions.requestPermissions(this, "需要定位权限定位",
                    RC_CAMERA_AND_WIFI, perms);
        }
    }


    private void location() {
        mLocationClient = new AMapLocationClient(this);
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

    private void initView() {
        Drawable drawable = ImageUtils.setDrwableSize(this, R.drawable.main_battlefield_selector, 32);
        mRbBattlefield.setCompoundDrawables(null, drawable, null, null);
        Drawable drawable1 = ImageUtils.setDrwableSize(this, R.drawable.main_circle_selector, 32);
        mRbCircle.setCompoundDrawables(null, drawable1, null, null);
        Drawable drawable2 = ImageUtils.setDrwableSize(this, R.drawable.main_yuezhan_selector, 32);
        mRbYuezhan.setCompoundDrawables(null, drawable2, null, null);
        Drawable drawable3 = ImageUtils.setDrwableSize(this, R.drawable.main_dynamic_selector, 32);
        mRbDynamic.setCompoundDrawables(null, drawable3, null, null);
        Drawable drawable4 = ImageUtils.setDrwableSize(this, R.drawable.main_wode_selector, 32);
        mRbWode.setCompoundDrawables(null, drawable4, null, null);
        mRgMain.check(R.id.rb_battlefield);
    }

    private void initEvent() {

        mRgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_battlefield:
                        if (mContent != null) {
                            switchContent(mContent, mBattleFieldFragment);
                        } else {
                            switchContent(mBlackFragment, mBattleFieldFragment);
                        }
                        break;

                    case R.id.rb_circle:
                        switchContent(mContent, mCircleFragment);
                        break;

                    case R.id.rb_yuezhan:
                        switchContent(mContent, mYueZhanFragment);
                        break;

                    case R.id.rb_dynamic:
                        switchContent(mContent, mDynamicFragment);
                        break;
                    case R.id.rb_wode:
                        switchContent(mContent, mWodeFragment);
                        break;
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mLocationClient != null)
            mLocationClient.stopLocation();
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mLongitude = aMapLocation.getLongitude();
            mLatitude = aMapLocation.getLatitude();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == RC_CAMERA_AND_WIFI) {
            location();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    public interface IOnFocusListenable {
        public void onWindowFocusChanged(boolean hasFocus);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (getSupportFragmentManager().getFragments().get(0) instanceof IOnFocusListenable) {
            ((IOnFocusListenable) getSupportFragmentManager().getFragments().get(0)).onWindowFocusChanged(hasFocus);
        }
    }

    public void switchContent(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.vp_main, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }
}
