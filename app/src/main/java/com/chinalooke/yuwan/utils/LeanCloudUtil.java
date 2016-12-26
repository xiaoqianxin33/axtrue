package com.chinalooke.yuwan.utils;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.RequestMobileCodeCallback;

/**
 * LeanCloud工具类
 * Created by xiao on 2016/11/18.
 */

public class LeanCloudUtil {

    /**
     * 发送验证码
     *
     * @param phone                     电话号码
     * @param action                    行为
     * @param requestMobileCodeCallback 监听回调
     */
    public static void sendSMSRandom(String phone, String action, RequestMobileCodeCallback requestMobileCodeCallback) {
        AVOSCloud.requestSMSCodeInBackground(phone, "雷熊", action, 10, requestMobileCodeCallback);
    }

    /**
     * 验证验证码
     *
     * @param mCode                       验证码
     * @param phone                       手机号码
     * @param avMobilePhoneVerifyCallback 监听回调
     */
    public static void checkSMS(String mCode, String phone, AVMobilePhoneVerifyCallback avMobilePhoneVerifyCallback) {
        AVOSCloud.verifyCodeInBackground(mCode, phone, avMobilePhoneVerifyCallback);
    }
}
