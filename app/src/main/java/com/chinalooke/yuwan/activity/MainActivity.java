package com.chinalooke.yuwan.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.fragment.BattleFieldFragment;
import com.chinalooke.yuwan.fragment.BlackFragment;
import com.chinalooke.yuwan.fragment.CircleFragment;
import com.chinalooke.yuwan.fragment.DynamicFragment;
import com.chinalooke.yuwan.fragment.WodeFragment;
import com.chinalooke.yuwan.fragment.YueZhanFragment;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.LocationUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

import static com.chinalooke.yuwan.constant.Constant.MIN_CLICK_DELAY_TIME;
import static com.chinalooke.yuwan.constant.Constant.lastClickTime;

public class MainActivity extends AutoLayoutActivity implements AMapLocationListener, EasyPermissions.PermissionCallbacks {

    @Bind(R.id.iv_zc)
    ImageView mIvZc;
    @Bind(R.id.tv_zc)
    TextView mTvZc;
    @Bind(R.id.iv_qz)
    ImageView mIvQz;
    @Bind(R.id.tv_qz)
    TextView mTvQz;
    @Bind(R.id.iv_yz)
    ImageView mIvYz;
    @Bind(R.id.tv_yz)
    TextView mTvYz;
    @Bind(R.id.iv_dt)
    ImageView mIvDt;
    @Bind(R.id.tv_dt)
    TextView mTvDt;
    @Bind(R.id.iv_wd)
    ImageView mIvWd;
    @Bind(R.id.tv_wd)
    TextView mTvWd;
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
    private long exitTime = 0;
    private Toast mToast;
    private LoginUser.ResultBean mUserInfo;
    private int RC_ACCESS_FINE_LOCATION = 1;

    public RequestQueue getQueue() {
        return mQueue;
    }

    public LoginUser.ResultBean getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(LoginUser.ResultBean userInfo) {
        mUserInfo = userInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_main);
        ButterKnife.bind(this);
        mToast = YuwanApplication.getToast();
        mFragmentManager = getSupportFragmentManager();
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mBattleFieldFragment = new BattleFieldFragment();
        mCircleFragment = new CircleFragment();
        mDynamicFragment = new DynamicFragment();
        mWodeFragment = new WodeFragment();
        mYueZhanFragment = new YueZhanFragment();
        mBlackFragment = new BlackFragment();
        requestLocationPermission();
        mUserInfo = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        initView();
    }

    private void initView() {
        switchContent(mBlackFragment, mBattleFieldFragment);
        setSelected(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                LocationUtils.setAMapLocation(aMapLocation);
                mLongitude = aMapLocation.getLongitude();
                PreferenceUtils.setPrefString(getApplicationContext(), "longitude", mLongitude + "");
                mLatitude = aMapLocation.getLatitude();
                PreferenceUtils.setPrefString(getApplicationContext(), "latitude", mLatitude + "");
                mBattleFieldFragment.getADListWithGPS();
                if (mUserInfo != null)
                    updateUserGPS();
            }
        }
    }

    //向服务端更新用户位置
    private void updateUserGPS() {
        String uri = Constant.HOST + "updateUserGPS&lng=" + mLongitude + "&lat=" + mLatitude + "&userId="
                + mUserInfo.getUserId() + "&updateTime=" + DateUtils.getCurrentDateTime();
        StringRequest request = new StringRequest(uri, null, null);
        mQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserInfo = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
    }

    @OnClick({R.id.rl_zc, R.id.rl_qz, R.id.rl_yz, R.id.rl_dt, R.id.rl_wd})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.rl_zc:
                    setSelected(1);
                    switchContent(mContent, mBattleFieldFragment);
                    break;
                case R.id.rl_qz:
                    setSelected(2);
                    switchContent(mContent, mCircleFragment);
                    break;
                case R.id.rl_yz:
                    setSelected(3);
                    switchContent(mContent, mYueZhanFragment);
                    break;
                case R.id.rl_dt:
                    setSelected(4);
                    switchContent(mContent, mDynamicFragment);
                    break;
                case R.id.rl_wd:
                    setSelected(5);
                    switchContent(mContent, mWodeFragment);
                    break;
            }
        }
    }

    private void setSelected(int i) {
        mIvZc.setSelected(i == 1);
        mTvZc.setSelected(i == 1);
        mIvQz.setSelected(i == 2);
        mTvQz.setSelected(i == 2);
        mIvYz.setSelected(i == 3);
        mTvYz.setSelected(i == 3);
        mIvDt.setSelected(i == 4);
        mTvDt.setSelected(i == 4);
        mIvWd.setSelected(i == 5);
        mTvWd.setSelected(i == 5);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == RC_ACCESS_FINE_LOCATION)
            mLocationClient = LocationUtils.location(this, this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            mToast.setText("再按一次退出程序");
            mToast.show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    //请求定位权限
    private void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            mLocationClient = LocationUtils.location(this, this);
        } else {
            EasyPermissions.requestPermissions(this, "需要定位权限",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }


}
