package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.GetHTTPDatas;
import com.chinalooke.yuwan.utils.LeanCloudUtil;
import com.chinalooke.yuwan.utils.MyUtils;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 找回密码界面
 * Created by Administrator on 2016/8/25.
 */
public class ForgetPasswordActivity extends AutoLayoutActivity {

    //获取验证码
    @Bind(R.id.btn__get_verification_code_forget_password)
    Button mgetVerificationCode;

    //手机号码
    @Bind(R.id.phone_forget_password)
    EditText mphoneForgetPassword;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    //验证码
    @Bind(R.id.verification_code_forget)
    EditText verificationCodeForget;
    private String phone;
    private RequestQueue mQueue;
    //验证码倒计时
    private CountTimer mcountTimer;
    //验证码
    private String mVerificationCode;
    private GetHTTPDatas getHTTPDatas;
    //短信严重的回调
    EventHandler eh;
    private Toast mToast;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_forget_password);
        ButterKnife.bind(this);
        //初始化smssdk
        SMSSDK.initSDK(this, Constant.APPKEY, Constant.APPSECRET);
        mQueue = Volley.newRequestQueue(this);
        getHTTPDatas = new GetHTTPDatas();
        getHTTPDatas.setmQueue(mQueue);
        mToast = YuwanApplication.getToast();
        //设置初始化倒计时
        mcountTimer = new CountTimer(60000, 1000);
        initView();
    }

    private void initView() {
        mTvTitle.setText("验证身份");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eh != null)
            //解除注册
            SMSSDK.unregisterEventHandler(eh);
    }

    @OnClick({R.id.iv_back, R.id.btn__get_verification_code_forget_password, R.id.next_forget_password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn__get_verification_code_forget_password:
                //判断是否是手机号
                phone = mphoneForgetPassword.getText().toString();
                if (MyUtils.CheckPhoneNumber(phone))
                    //判断用户是否注册
                    getHTTPIsPhoneExists();
                else
                    mphoneForgetPassword.setError("请输入正确的手机号码");
                break;
            case R.id.next_forget_password:
                clickNextBtn();
                break;
        }
    }

    /**
     * 点击下一步按钮
     */
    private void clickNextBtn() {
        phone = mphoneForgetPassword.getText().toString();
        mVerificationCode = verificationCodeForget.getText().toString();
        if (TextUtils.isEmpty(mVerificationCode)) {
            verificationCodeForget.setError("请输入验证码");
            verificationCodeForget.requestFocus();
            return;
        }
        if (!MyUtils.CheckPhoneNumber(phone))
            mphoneForgetPassword.setError("请输入正确的手机号码");
        else {
            //提交验证码
            LeanCloudUtil.checkSMS(mVerificationCode, phone, new AVMobilePhoneVerifyCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        getHTTPNext();
                    } else {
                        verificationCodeForget.setError("验证码输入错误，请重试");
                        verificationCodeForget.requestFocus();
                    }
                }
            });
        }
    }

    /**
     * 发送短信验证码
     */
    private void sendSMSRandom() {
        LeanCloudUtil.sendSMSRandom(phone, "找回密码", new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    mToast.setText("验证码发送成功！");
                } else {
                    mToast.setText("验证码发送失败");
                    mToast.show();
                }
            }
        });
    }

    //网络获取  验证手机号是否注册
    private void getHTTPIsPhoneExists() {
        String URLPhone = Constant.IS_PHONE_EXISTS + "&" + Constant.PHONE + phone;
        StringRequest request = new StringRequest(URLPhone, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            boolean result = jsonObject.getBoolean("Result");
                            if (result) {
                                //启动倒计时
                                mcountTimer.start();
                                //获取验证码 开始获取验证码
                                sendSMSRandom();
                            } else {
                                String msg = jsonObject.getString("Msg");
                                mphoneForgetPassword.setError(msg);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(request);
    }

    /**
     * 手机验证成功之后进行下一步
     */
    public void getHTTPNext() {
        Intent intent = new Intent(ForgetPasswordActivity.this, ResetPasswordActivity.class);
        intent.putExtra("phone", phone);
        startActivity(intent);
    }


    /**
     * 时间计数器
     */
    public class CountTimer extends CountDownTimer {
        /**
         * @param millisInFuture    倒计时时间
         * @param countDownInterval 刷新的时间
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * //设置验证码倒计时
         *
         * @param millisUntilFinished 到计时时间
         */
        @Override
        public void onTick(long millisUntilFinished) {

            mgetVerificationCode.setText(millisUntilFinished / 1000 + "后重新发送");
            mgetVerificationCode.setBackgroundResource(R.drawable.btn_wait_verification_corners_bg);
            mgetVerificationCode.setClickable(false);
        }

        @Override
        public void onFinish() {
            //倒计时完成后
            mgetVerificationCode.setText("获取验证码");
            mgetVerificationCode.setBackgroundResource(R.drawable.btn_get_verification_corners_bg);
            mgetVerificationCode.setClickable(true);
        }
    }
}
