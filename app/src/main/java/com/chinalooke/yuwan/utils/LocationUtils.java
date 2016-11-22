package com.chinalooke.yuwan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 定位工具类
 * Created by xiao on 2016/8/9.
 */
public class LocationUtils {

    private static final double EARTH_RADIUS = 6378137.0;
    private static LocationManager mLocationManager;
    private static Activity mActivity;
    private static AMapLocation mAMapLocation;

    public static AMapLocation getAMapLocation() {
        return mAMapLocation;
    }

    public static void setAMapLocation(AMapLocation AMapLocation) {
        mAMapLocation = AMapLocation;
    }

    public LocationUtils(Activity activity, LocationManager locationManager) {
        this.mActivity = activity;
        this.mLocationManager = locationManager;
    }


    public void CheckGPSisOpen() {

        boolean isOpen = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isOpen) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivityForResult(intent, 0);
        }
    }


    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


    public static AMapLocationClient location(Activity activity, AMapLocationListener aMapLocationListener) {

        AMapLocationClient mLocationClient = new AMapLocationClient(activity);

        mLocationClient.setLocationListener(aMapLocationListener);

        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mLocationOption.setInterval(3000);

        mLocationOption.setWifiActiveScan(false);

        mLocationOption.setOnceLocationLatest(true);

        mLocationClient.setLocationOption(mLocationOption);

        mLocationClient.startLocation();

        return mLocationClient;

    }
}


