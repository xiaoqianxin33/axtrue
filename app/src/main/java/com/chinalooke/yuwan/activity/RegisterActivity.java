package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.Register;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LeanCloudUtil;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.Validator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 注册缓冲未做，向服务端发送介绍人未写 check的选中未写
 * Created by Administrator on 2016/8/23.
 */
public class RegisterActivity extends AutoLayoutActivity implements CompoundButton.OnCheckedChangeListener {
    //注册按钮
    @Bind(R.id.btn_register_register)
    Button mbtnRegister;
    //手机号码
    @Bind(R.id.phone_register)
    EditText mphoneRegister;
    //介绍人手机号码
    @Bind(R.id.introduce_sponsor_phone_register)
    EditText mIntroducePhoneRegister;
    //密码
    @Bind(R.id.password_register)
    EditText mpasswordRegister;
    //重填密码
    @Bind(R.id.repassword_register)
    EditText mrepasswordRegister;
    //获取验证码
    @Bind(R.id.btn__get_verification_code_register)
    Button mbtnGetVerification;
    //验证码
    @Bind(R.id.input_verification_code_register)
    EditText mETVerification;
    //验证码
    CheckBox mcheckUserPro;


    private String phone;
    private String passWord;
    private RequestQueue mQueue;
    //验证码倒计时
    private CountTimer mcountTimer;
    //介绍人的电话号码
    private String mIntroducePhone;
    //短信严重的回调
    EventHandler eh;
    String userId;
    String headImg;
    private Toast mToast;
    private ProgressDialog mProgressDialog;
    private String mCode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        ButterKnife.bind(this);
        mcheckUserPro = (CheckBox) findViewById(R.id.checkbox_user_protocol_register);//验证码
        assert mcheckUserPro != null;
        mcheckUserPro.setOnCheckedChangeListener(this);
        //初始化smssdk
        SMSSDK.initSDK(this, Constant.APPKEY, Constant.APPSECRET);
        mQueue = Volley.newRequestQueue(this);
        mToast = YuwanApplication.getToast();
        //设置密码类型
        mcountTimer = new CountTimer(60000, 1000);
        initView();
    }

    private void initView() {
        String phone = getIntent().getStringExtra("phone");
        if (!TextUtils.isEmpty(phone)) {
            mphoneRegister.setText(phone);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eh != null)
            //解除注册
            SMSSDK.unregisterEventHandler(eh);
    }

    @OnClick({R.id.btn__get_verification_code_register, R.id.btn_register_register, R.id.iv_back})
    public void onClick(View view) {

        switch (view.getId()) {
            //获取验证码
            case R.id.btn__get_verification_code_register:
                //判断是否是手机号
                phone = mphoneRegister.getText().toString();
                if (MyUtils.CheckPhoneNumber(phone))
                    getHTTPIsPhoneExists(); //判断用户是否注册
                else {
                    mphoneRegister.setError("请输入正确的手机号码");
                    mphoneRegister.requestFocus();
                }
                break;
            //注册
            case R.id.btn_register_register:
                beginRegister();
                break;
            //返回
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * 注册成功跳转页面
     */
    private void registerSuccess() {
        LoginUser.ResultBean userInfo = new LoginUser.ResultBean();
        userInfo.setUserId(userId);
        userInfo.setHeadImg(headImg);
        userInfo.setUserType("player");
        try {
            LoginUserInfoUtils.saveLoginUserInfo(getApplicationContext(),
                    LoginUserInfoUtils.KEY, userInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        NetUtil.registerHx(phone);
        Intent intent = new Intent(this, PersonalInfoActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 开始注册
     */

    private void beginRegister() {
        mCode = mETVerification.getText().toString();
        if (TextUtils.isEmpty(mCode)) {
            mToast.setText("请输入验证码");
            mToast.show();
            return;
        }
        Log.i("TAG", "验证码为-----" + mCode);
        //重新获取当前信息
        phone = mphoneRegister.getText().toString();
        passWord = mpasswordRegister.getText().toString();
        String repassWord = mrepasswordRegister.getText().toString();
        mIntroducePhone = mIntroducePhoneRegister.getText().toString();
        if (!MyUtils.CheckPhoneNumber(phone))
            mphoneRegister.setError("请输入正确的手机号码");
        else if (!passWord.equals(repassWord)) {
            mrepasswordRegister.setError("请输入两次相同密码");
            mrepasswordRegister.setText("");
            mpasswordRegister.setText("");
            mpasswordRegister.requestFocus();
        } else if (!Validator.isPassword(passWord)) {
            //密码不在6-16位之间
            mpasswordRegister.setError("请输入6至16位密码");
            mpasswordRegister.requestFocus();
        } else {
            //提交验证码
            mProgressDialog = MyUtils.initDialog("正在注册...", RegisterActivity.this);
            mProgressDialog.show();
            if (!TextUtils.isEmpty(mIntroducePhone)) {
                if (!MyUtils.CheckPhoneNumber(mIntroducePhone))
                    mIntroducePhoneRegister.setError("推荐人手机号码错误，改改试试吧");
                else {
                    checkSMS();
                }
            } else {
                checkSMS();
            }
        }

    }

    private void checkSMS() {
        LeanCloudUtil.checkSMS(mCode, phone, new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    getHTTPRegister();
                } else {
                    mToast.setText("验证码错误，请重试");
                    mToast.show();
                }
            }
        });
    }


    /**
     * check的监听事件
     * todo 弹出用户协议
     */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mbtnRegister.setClickable(true);
        } else mbtnRegister.setClickable(false);
    }


    public class CountTimer extends CountDownTimer {
        CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mbtnGetVerification.setText(millisUntilFinished / 1000 + "后重新发送");
            mbtnGetVerification.setBackgroundResource(R.drawable.btn_wait_verification_corners_bg);
            mbtnGetVerification.setClickable(false);
        }

        @Override
        public void onFinish() {
            mbtnGetVerification.setText("获取验证码");
            mbtnGetVerification.setBackgroundResource(R.drawable.btn_get_verification_corners_bg);
            mbtnGetVerification.setClickable(true);
        }
    }

    //网络获取  验证手机号是否注册
    private void getHTTPIsPhoneExists() {
        String URLPhone = Constant.IS_PHONE_EXISTS + "&" + Constant.PHONE + phone;
        StringRequest request = new StringRequest(URLPhone, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            boolean result = jsonObject.getBoolean("Result");
                            if (result) {
                                mphoneRegister.setError("该号码已注册");
                                mphoneRegister.requestFocus();
                            } else {
                                mphoneRegister.setEnabled(false);
                                //启动倒计时
                                mcountTimer.start();
                                //获取验证码 开始获取验证码
                                LeanCloudUtil.sendSMSRandom(phone, "注册账号", new RequestMobileCodeCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            mETVerification.requestFocus();
                                        } else {
                                            Log.e("Home.OperationVerify", e.getMessage());
                                        }
                                    }
                                });
                            }
                        } else {
                            String msg = jsonObject.getString("Msg");
                            mToast.setText(msg);
                            mToast.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, null);
        mQueue.add(request);
    }

    private void getHTTPRegister() {
        String URLRegister = Constant.REGISTER + "&phone=" + phone + "&pwd=" + passWord + "&userType=player&introducerPhone=" + mIntroducePhone;
        StringRequest stringRequest = new StringRequest(URLRegister,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //解析数据
                        mProgressDialog.dismiss();
                        if (response != null) {
                            if (AnalysisJSON.analysisJson(response)) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<Register>() {
                                }.getType();
                                Register register = gson.fromJson(response, type);
                                analyzeJson(register.getResult());
                                //注册成功开始跳转
                                registerSuccess();
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String msg = jsonObject.getString("Msg");
                                    mToast.setText(msg);
                                    mToast.show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
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

    private void analyzeJson(Register.ResultBean result) {
        userId = result.getId();
        headImg = result.getAvatar();
        LoginUser.ResultBean resultBean = new LoginUser.ResultBean();
        resultBean.setUserId(userId);
        resultBean.setHeadImg(headImg);
        try {
            LoginUserInfoUtils.saveLoginUserInfo(getApplicationContext(), LoginUserInfoUtils.KEY, resultBean);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
