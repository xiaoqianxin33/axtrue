package com.chinalooke.yuwan.config;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * 项目配置
 * Created by xiao on 2016/8/23.
 */
public class YuwanApplication extends Application {

    private static Toast mToast;
    private static Handler mHandler;
    private static RequestQueue mQueue;

    public static RequestQueue getQueue() {
        return mQueue;
    }

    public static Handler getHandler() {
        return mHandler;
    }


    public static Toast getToast() {
        return mToast;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(getApplicationContext());
        ImageLoader.getInstance().init(configuration);
        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        mHandler = new Handler();
        mQueue = Volley.newRequestQueue(getApplicationContext());
    }
}
