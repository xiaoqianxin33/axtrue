package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.ResultDatas;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LeanCloudUtil;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindPhoneActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.et_phone)
    EditText mEtPhone;
    @Bind(R.id.et_code)
    EditText mEtCode;
    @Bind(R.id.btn_getCode)
    Button mBtnGetCode;
    private String mAuth;
    private Toast mToast;
    private String mPhone;
    private RequestQueue mQueue;
    private CountTimer mCountTimer;
    private ProgressDialog mProgressDialog;
    private String userId;
    private String headImg;
    private String mNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        ButterKnife.bind(this);
        mToast = YuwanApplication.getToast();
        mQueue = YuwanApplication.getQueue();
        mCountTimer = new CountTimer(60000, 1000);
        initView();
        initData();
    }

    private void initData() {
        mAuth = getIntent().getStringExtra("auth");
    }

    private void initView() {
        mTvTitle.setText("绑定手机号");
    }

    @OnClick({R.id.iv_back, R.id.btn_getCode, R.id.btn_bind_phone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_getCode:
                if (checkPhone()) {
                    mProgressDialog = MyUtils.initDialog("提交中...", BindPhoneActivity.this);
                    mProgressDialog.show();
                    getHTTPIsPhoneExists();
                }

                break;
            case R.id.btn_bind_phone:
                checkSMS();
                break;
        }
    }

    //检查验证码
    private void checkSMS() {
        String code = mEtCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            mEtCode.setError("请输入验证码");
        } else {
            LeanCloudUtil.checkSMS(code, mPhone, new AVMobilePhoneVerifyCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        getHTTPRegister();
                    } else {
                        mEtCode.setError("验证码错误，请重新输入");
                    }
                }
            });
        }
    }

    private void getHTTPIsPhoneExists() {
        String URLPhone = Constant.IS_PHONE_EXISTS + "&" + Constant.PHONE + mPhone;
        StringRequest request = new StringRequest(URLPhone, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            boolean result = jsonObject.getBoolean("Result");
                            if (result) {
                                mEtPhone.setError("该手机号码已被注册，换个试试吧");
                            } else {
                                mEtPhone.setEnabled(false);
                                //启动倒计时
                                mCountTimer.start();
                                //获取验证码 开始获取验证码
                                sendSMSRandom();
                            }
                        } else {
                            MyUtils.showMsg(mToast, response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("服务器抽风了，请稍后重试");
                mToast.show();
            }
        });
        mQueue.add(request);
    }

    private void sendSMSRandom() {
        LeanCloudUtil.sendSMSRandom(mPhone, "绑定手机号码", new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    mEtCode.requestFocus();
                    mToast.setText("短信验证码已发送！");
                    mToast.show();
                } else {
                    mEtPhone.setEnabled(true);
                    mToast.setText("短信验证码发送失败,请稍后重试！");
                    mToast.show();
                }
            }
        });
    }

    private void getHTTPRegister() {
        String randomPwd = getRandomPwd();
        String URLRegister = Constant.REGISTER + "&" + Constant.PHONE + mPhone + "&pwd=" + randomPwd + "&" +
                "userType=player&auth=" + mAuth;
        StringRequest stringRequest = new StringRequest(URLRegister,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //解析数据
                        if (response != null) {
                            ResultDatas result = AnalysisJSON.getAnalysisJSON().AnalysisJSONResult(response);
                            if (result != null) {
                                if ("true".equals(result.getSuccess())) {
                                    Log.d("TAG", "注册成功");
                                    if (mProgressDialog != null)
                                        mProgressDialog.dismiss();
                                    //获取用户id；  解析result.getResult();
                                    analyzeJson(result.getResult());
                                    //注册成功开始跳转
                                    registerSuccess();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
                mToast.setText("网速不给力啊，换个地方试试");
                mToast.show();
            }
        });
        mQueue.add(stringRequest);
    }

    private String getRandomPwd() {
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int i1 = (int) (10 * (Math.random()));
            stringBuffer.append(i1);
        }
        return stringBuffer.toString();
    }

    private boolean checkPhone() {
        mPhone = mEtPhone.getText().toString();
        if (TextUtils.isEmpty(mPhone)) {
            mToast.setText("请输入手机号码");
            mToast.show();
            return false;
        }
        if (!MyUtils.CheckPhoneNumber(mPhone)) {
            mToast.setText("请输入正确的手机号码");
            mToast.show();
            return false;
        }
        return true;
    }

    public class CountTimer extends CountDownTimer {
        CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mBtnGetCode.setText(millisUntilFinished / 1000 + "后重新发送");
            mBtnGetCode.setBackgroundResource(R.drawable.btn_wait_verification_corners_bg);
            mBtnGetCode.setClickable(false);
        }

        @Override
        public void onFinish() {
            mBtnGetCode.setText("获取短信验证码");
            mBtnGetCode.setBackgroundResource(R.drawable.btn_get_verification_corners_bg);
            mBtnGetCode.setClickable(true);
        }
    }

    private void analyzeJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            userId = jsonObject.getString("userId");
            headImg = jsonObject.getString("headImg");
            mNickName = jsonObject.getString("nickName");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("TAG", "解析失败");
        }
    }

    private void registerSuccess() {
        LoginUser.ResultBean userInfo = new LoginUser.ResultBean();
        userInfo.setUserId(userId);
        userInfo.setHeadImg(headImg);
        userInfo.setUserType("player");
        userInfo.setNickName(mNickName);
        try {
            LoginUserInfoUtils.saveLoginUserInfo(getApplicationContext(),
                    LoginUserInfoUtils.KEY, userInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        NetUtil.registerHx(mPhone, userInfo.getNickName());
        Intent intent = new Intent(this, PersonalInfoActivity.class);
        startActivity(intent);
        finish();
    }
}
