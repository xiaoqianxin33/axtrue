package com.chinalooke.yuwan.config;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
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
import com.qiniu.android.common.Zone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UploadManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 项目初始化配置
 * Created by xiao on 2016/8/23.
 */
public class YuwanApplication extends Application {

    private static Toast mToast;
    private static Handler mHandler;
    private static RequestQueue mQueue;
    private static UploadManager mUploadManager;

    public static Context getmApplicationContext() {
        return mApplicationContext;
    }

    public static Context mApplicationContext;

    public static UploadManager getmUploadManager() {
        return mUploadManager;
    }

    public static RequestQueue getQueue() {
        return mQueue;
    }

    public static Handler getHandler() {
        return mHandler;
    }


    public static Toast getToast() {
        return mToast;
    }

    /**
     * 维护Activity 的list
     */
    private static List<Activity> mActivitys = Collections
            .synchronizedList(new LinkedList<Activity>());


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

        //七牛初始化
        Configuration config = new Configuration.Builder().zone(Zone.zone1).build();
        mUploadManager = new UploadManager(config);
        registerActivityListener();
    }

    public void pushActivity(Activity activity) {
        mActivitys.add(activity);
    }

    public static void finishAllActivity() {
        if (mActivitys == null) {
            return;
        }
        for (Activity activity : mActivitys) {
            activity.finish();
        }
        mActivitys.clear();
    }

    private void registerActivityListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    /**
                     *  监听到 Activity创建事件 将该 Activity 加入list
                     */
                    pushActivity(activity);

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (null == mActivitys && mActivitys.isEmpty()) {
                        return;
                    }
                    if (mActivitys.contains(activity)) {
                        /**
                         *  监听到 Activity销毁事件 将该Activity 从list中移除
                         */
                        popActivity(activity);
                    }
                }
            });
        }
    }

    /**
     * @param activity 作用说明 ：删除一个activity在管理里
     */
    public void popActivity(Activity activity) {
        mActivitys.remove(activity);
    }

}
