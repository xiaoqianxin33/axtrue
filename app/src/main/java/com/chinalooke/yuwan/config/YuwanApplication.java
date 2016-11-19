package com.chinalooke.yuwan.config;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVOSCloud;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * 项目初始化配置
 * Created by xiao on 2016/8/23.
 */
public class YuwanApplication extends Application {

    private static Toast mToast;
    private static Handler mHandler;
    private static RequestQueue mQueue;

    public static Context getmApplicationContext() {
        return mApplicationContext;
    }

    public static Context mApplicationContext;

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
        mApplicationContext = getApplicationContext();
        //环信初始化
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        EMClient.getInstance().init(getApplicationContext(), options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
        EaseUI.getInstance().init(getApplicationContext(), options);
        //编译分包
        MultiDex.install(this);
        //leanCould初始化
        AVOSCloud.initialize(this, "ArJkPnYSMCv1MTpGOPU3aHLU-gzGzoHsz", "MlCdUI4iB1jucLGs0GIuTwyL");
    }
}
