package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.ResultDatas;
import com.chinalooke.yuwan.utils.GetHTTPDatas;
import com.chinalooke.yuwan.utils.Validator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/8/25.
 */
public class ForgetPasswordActivity extends AppCompatActivity {
    //返回键
    @Bind(R.id.back_forget_password)
    ImageView mbackForgetPassword;
    //获取验证码
    @Bind(R.id.btn__get_verification_code_forget_password)
    Button mgetVerificationCode;
    //忘记密码下一步
    @Bind(R.id.next_forget_password)
    Button nextForgetPassword;
    //手机号码
    @Bind(R.id.phone_forget_password)
    EditText mphoneForgetPassword;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_forget_password);
        ButterKnife.bind(this);
        //初始化smssdk
        SMSSDK.initSDK(this, Constant.APPKEY, Constant.APPSECRET);
        mQueue = Volley.newRequestQueue(this);
        getHTTPDatas=new GetHTTPDatas();
        getHTTPDatas.setmQueue(mQueue);
        //设置初始化倒计时
        mcountTimer=new CountTimer(60000,1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(eh!=null)
            //解除注册
            SMSSDK.unregisterEventHandler(eh);
    }

    @OnClick({R.id.back_forget_password,R.id.btn__get_verification_code_forget_password,R.id.next_forget_password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_forget_password:
                finish();
                break;
            case R.id.btn__get_verification_code_forget_password:
                //判断是否是手机号
                phone=mphoneForgetPassword.getText().toString();
                if (Validator.getValidator().isMobile(phone))
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
    private  void clickNextBtn(){
        phone=mphoneForgetPassword.getText().toString();
        mVerificationCode=verificationCodeForget.getText().toString();
        if (!Validator.getValidator().isMobile(phone))
            mphoneForgetPassword.setError("请输入正确的手机号码");
        else{
            //提交验证码
            SMSSDK.submitVerificationCode("86",phone,mVerificationCode);
        }
}

    /**
     * 发送短信验证码
     */
    private void sendSMSRandom(){
        eh=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Log.i("TAG",event+"--------"+result+"-----"+data);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        Log.i("TAG","提交验证马成功");
                        //开始网上
                        getHTTPNext();

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        Log.i("TAG","获取验证马成功");
                        //获取验证码成功
                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

        SMSSDK.getVerificationCode("86",phone);//获取验证码
    }
    //网络获取  验证手机号是否注册
    private void getHTTPIsPhoneExists(){
        String URLPhone= Constant.IS_PHONE_EXISTS+"&"+ Constant.PHONE+phone;
        getHTTPDatas.getHTTPIsPhoneExists(URLPhone);

        ResultDatas result= getHTTPDatas.getResult();

        if (result!=null){
            if("true".equals(result.getSuccess()) && "false".equals(result.getResult())){
                Log.d("TAG","该号码未注册");
                mphoneForgetPassword.setError("该号码未注册");

            }
            if("true".equals(result.getSuccess()) && "true".equals(result.getResult())){
                Log.d("TAG","验证密码");
                //启动倒计时
                mcountTimer.start();
                //获取验证码 开始获取验证码
                sendSMSRandom();
            }
        }
    }
/**
 * 手机验证成功之后进行下一步
 */
   public void getHTTPNext(){
       Intent intent=new Intent(ForgetPasswordActivity.this,ResetPasswordActivity.class);
       intent.putExtra("phone",phone);
       startActivity(intent);
    }


    /**
     * 时间计数器
     */
    public class CountTimer extends CountDownTimer {
        /**
         *
         * @param millisInFuture 倒计时时间
         * @param countDownInterval 刷新的时间
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * //设置验证码倒计时
         * @param millisUntilFinished 到计时时间
         */
        @Override
        public void onTick(long millisUntilFinished) {

            mgetVerificationCode.setText(millisUntilFinished/1000+"后重新发送");
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
