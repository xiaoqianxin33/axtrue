package com.chinalooke.yuwan.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * 网络工具类
 * Created by xiao on 2016/8/19.
 */
public class NetUtil {

    public static boolean is_Network_Available(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        if (anInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            return true;
                        } else if (anInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //注册环信账号
    public static void registerHx(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(phone, phone);
                    loginHx(phone);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    loginHx(phone);
                }
            }
        }).start();
    }


    //登录环信
    public static void loginHx(final String phone) {
        EMClient.getInstance().login(phone, phone, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.e("TAG", "登录聊天服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.e("TAG", "登录聊天服务器失败！");
            }
        });
    }
}
