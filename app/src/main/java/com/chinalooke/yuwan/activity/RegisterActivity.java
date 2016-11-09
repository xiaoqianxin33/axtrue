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
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.ResultDatas;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.GetHTTPDatas;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.Validator;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

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
    //验证码
    private String mVerificationCode;
    //介绍人的电话号码
    private String mIntroducePhone;
    private GetHTTPDatas getHTTPDatas;
    //短信严重的回调
    EventHandler eh;
    String userId;
    String headImg;
    private Toast mToast;
    private ProgressDialog mProgressDialog;

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
        getHTTPDatas = new GetHTTPDatas();
        getHTTPDatas.setmQueue(mQueue);
        mToast = YuwanApplication.getToast();
        //设置密码类型
        mcountTimer = new CountTimer(60000, 1000);
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
                if (Validator.getValidator().isMobile(phone))
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
        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PersonalInfoActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("headImg", headImg);
        startActivity(intent);
        finish();
    }

    /**
     * 发送短信验证码
     */
    private void sendSMSRandom() {


        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Log.i("TAG", event + "--------" + result + "-----" + data);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        Log.i("TAG", "提交验证码成功");
                        //开始网上注册

                        getHTTPRegister();

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Log.i("TAG", "获取验证码成功");
                        //获取验证码成功
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        //注册短信回调
        SMSSDK.registerEventHandler(eh);
        //获取验证码
        SMSSDK.getVerificationCode("86", phone);
    }

    /**
     * 开始注册
     */

    private void beginRegister() {
        mVerificationCode = mETVerification.getText().toString();
        Log.i("TAG", "验证码为-----" + mVerificationCode);
        //重新获取当前信息
        phone = mphoneRegister.getText().toString();
        String introducePhone = mIntroducePhoneRegister.getText().toString();
        passWord = mpasswordRegister.getText().toString();
        String repassWord = mrepasswordRegister.getText().toString();
        mIntroducePhone = mIntroducePhoneRegister.getText().toString();
        if (!Validator.getValidator().isMobile(phone))
            mphoneRegister.setError("请输入正确的手机号码");
        else if (!passWord.equals(repassWord)) {
            mrepasswordRegister.setError("请输入两次相同密码");
            mrepasswordRegister.setText("");
            mpasswordRegister.setText("");
            mpasswordRegister.requestFocus();
        } else if (!Validator.getValidator().isPassword(passWord)) {
            //密码不在6-16位之间
            mpasswordRegister.setError("请输入6至16位密码");
            mpasswordRegister.requestFocus();
        } else {
            //提交验证码
            mProgressDialog = MyUtils.initDialog("正在注册...", RegisterActivity.this);
            mProgressDialog.show();
            if (!TextUtils.isEmpty(mIntroducePhone)) {
                if (!Validator.getValidator().isMobile(mIntroducePhone))
                    mIntroducePhoneRegister.setError("推荐人手机号码错误，改改试试吧");
                else {
                    SMSSDK.submitVerificationCode("86", phone, mVerificationCode);
                }
            } else {
                SMSSDK.submitVerificationCode("86", phone, mVerificationCode);
            }
        }

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
        public CountTimer(long millisInFuture, long countDownInterval) {
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
        getHTTPDatas.getHTTPIsPhoneExists(URLPhone);
        ResultDatas result = getHTTPDatas.getResult();
        if (result != null) {
            if ("true".equals(result.getSuccess()) && "false".equals(result.getResult())) {
                Log.d("TAG", "该号码未注册");
                mphoneRegister.setEnabled(false);
                //启动倒计时
                mcountTimer.start();
                //获取验证码 开始获取验证码
                sendSMSRandom();
            }
            if ("true".equals(result.getSuccess()) && "true".equals(result.getResult())) {
                Log.d("TAG", "验证密码");
                mphoneRegister.setError("该号码已注册");
                mphoneRegister.requestFocus();
            }
        }
    }

    private void getHTTPRegister() {
        String URLRegister = Constant.REGISTER + "&" + Constant.PHONE + phone + "&" + Constant.PWD + passWord + "&" + Constant.INTRODUCE_PHONE + mIntroducePhone;
        Log.d("TAG", "电话网址----------" + URLRegister);
        StringRequest stringRequest = new StringRequest(URLRegister,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
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

    private void analyzeJson(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            userId = jsonObject.getString("userId");
            headImg = jsonObject.getString("headImg");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("TAG", "解析失败");
        }

    }


}
