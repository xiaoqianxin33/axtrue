package com.chinalooke.yuwan.constant;

import com.chinalooke.yuwan.R;

/**
 * Created by xiao on 2016/8/23.
 */
public class Constant {

    public static int[] battleFieldImage = {R.mipmap.battlefield_lb, R.mipmap.battlefield_lb3, R.mipmap.battlefield_lb2};
    public static String mainUri = "http://121.42.172.61/index.php?c=api&a=";
    public static final String HOST = "http://121.42.172.61/index.php?c=api&a=";

    //验证手机号是否存在
    public static final String PHONE = "phone=";
    //密码
    public static final String PWD = "pwd=";
    //介绍人电话号码
    public static final String INTRODUCE_PHONE = "introducerPhone=";
    public static final String IS_PHONE_EXISTS = HOST + "isPhoneExists";
    //验证是否注册成功
    public static final String REGISTER = HOST + "register";
    //验证是否登陆成功
    public static final String LOGIN_RESULT = HOST + "getUserInfo";
    //忘记密码
    public static final String FORGOT_PWD = HOST + "forgotPwd";

    public static final String GET_QRCODE_WITH_DATA = HOST + "getQRCodeWithData&qrString=";
    //短信验证的appkey与AppSecret
    public static final String APPKEY = "16529ca8e94af";
    public static final String APPSECRET = "532befe9d0d33bdec76";

    public static int MIN_CLICK_DELAY_TIME = 300;
    public static long lastClickTime = 0;
}
