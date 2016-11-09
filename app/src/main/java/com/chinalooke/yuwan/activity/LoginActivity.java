package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.ResultDatas;
import com.chinalooke.yuwan.model.UserInfo;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.Validator;
import com.chinalooke.yuwan.view.LoadingProgressDialogView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;


public class LoginActivity extends AppCompatActivity implements PlatformActionListener {
    //登录按钮
    @Bind(R.id.btn_login_login)
    Button mbtnLoginLogin;
    //手机号码
    @Bind(R.id.phone_login)
    EditText mphoneLogin;
    //密码
    @Bind(R.id.password_login)
    EditText mpasswordLogin;
    //QQ
    @Bind(R.id.QQ_login)
    ImageView mQQLogin;
    //微信
    @Bind(R.id.weixin_login)
    ImageView mWeixinLogin;
    //微博
    @Bind(R.id.weibo_login)
    ImageView mWeiboLogin;
    //忘记密码
    @Bind(R.id.forget_pwd_login)
    TextView forgetPwdLogin;
    private String phone;
    private String passWord;
    private RequestQueue mQueue;
    //加载的弹出框
    private LoadingProgressDialogView dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mQueue = Volley.newRequestQueue(this);
        //初始化Sharesdk;
        ShareSDK.initSDK(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁ShareSDk
        ShareSDK.stopSDK();
    }

    @OnClick({R.id.QQ_login, R.id.weixin_login, R.id.weibo_login, R.id.btn_login_login, R.id.back_login, R.id.forget_pwd_login})
    public void onClick(View view) {
        switch (view.getId()) {
            //登录按钮
            case R.id.btn_login_login:
                if(Validator.isNetworkAvailable(LoginActivity.this)) {
                    //判断是否网络未连接
                    handleLogin();
                }else{
                    Toast.makeText(LoginActivity.this,"亲掉线了，换个地方试试",Toast.LENGTH_SHORT).show();
                }
                break;
            //QQ登录
            case R.id.QQ_login:
                QQLogin();
                break;
            //微信登录
            case R.id.weixin_login:
                weixinLogin();
                break;
            //微博登
            case R.id.weibo_login:
                sinaLogin();
                break;
            //返回按钮
            case R.id.back_login:
                finish();
                break;
            //忘记密码
            case R.id.forget_pwd_login:
                ClickForgetPwd();
                break;
        }
    }

    /**
     * 点击忘记密码
     */
    private void ClickForgetPwd() {
        Intent intent = new Intent(this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

    /**
     * 加载登录
     */
    private void handleLogin() {
        phone = mphoneLogin.getText().toString();
        passWord = mpasswordLogin.getText().toString();
        if (phone.equals("")) {
            mphoneLogin.setError("请输入手机号码");
        } else
        if (!Validator.getValidator().isMobile(phone))
            mphoneLogin.setError("请输入正确的手机号码");
        else if ("".equals(passWord)) {
            mpasswordLogin.setError("请输入密码");
        } else {
            getHTTPIsPhoneExists();
            //弹出正在登陆
            showMyDialog(mbtnLoginLogin);
            mbtnLoginLogin.setClickable(false);
        }

    }

    //登陆成功跳转到首界面
    private void loginSuccess(){
        Intent intent =new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    //网络获取  验证手机号是否注册
    private void getHTTPIsPhoneExists() {
        String URLPhone = Constant.IS_PHONE_EXISTS + "&" + Constant.PHONE + phone;
        Log.d("TAG", "电话网址----------" + URLPhone);
        StringRequest stringRequest = new StringRequest(URLPhone,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        //解析数据

                        if (response != null) {

                            ResultDatas result = AnalysisJSON.getAnalysisJSON().AnalysisJSONResult(response);
                            if (result != null) {
                                if ("true".equals(result.getSuccess()) && "false".equals(result.getResult())) {
                                    Log.d("TAG", "该号码未注册");
                                    mphoneLogin.setError("该号码未注册，请先注册");
                                    dialog.dismiss();
                                }
                                if ("true".equals(result.getSuccess()) && "true".equals(result.getResult())) {
                                    Log.d("TAG", "验证密码");

                                    if ("".equals(passWord)) {
                                        //判断密码是否为空
                                        mpasswordLogin.setError("请输入密码");
                                        dialog.dismiss();
                                    } else {
                                        //进行登录
                                        getHTTPLoginSuccess();
                                    }


                                }
                            }


                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(stringRequest);
    }

    // 手机号验证通过后验证登录是否成功
    //网络获取  验证手机号是否注册
    private void getHTTPLoginSuccess() {

        String URLLogin = Constant.LOGIN_RESULT + "&" + Constant.PHONE + phone + "&" + Constant.PWD + passWord;
        Log.d("TAG", "登录网址----------" + URLLogin);
        StringRequest stringRequest = new StringRequest(URLLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        //解析数据
                        if (response != null) {

                            ResultDatas result = AnalysisJSON.getAnalysisJSON().AnalysisJSONResult(response);
                            if (result != null) {
                                if ("true".equals(result.getSuccess())) {
                                    Log.d("TAG", "登录成功");
                                    //Gson解析
                                    Gson gson = new Gson();

                                    UserInfo userInfo=gson.fromJson(result.getResult(), UserInfo.class);
                                    if (userInfo!=null) {
                                        LoginUserInfoUtils.getLoginUserInfoUtils().setUserInfo(userInfo);//设置userInfo
                                        try {
                                            LoginUserInfoUtils.getLoginUserInfoUtils().saveLoginUserInfo(LoginActivity.this,LoginUserInfoUtils.KEY,userInfo);
                                        } catch (IOException e) {
                                            Log.d("TAG","-------存储失败");
                                            e.printStackTrace();
                                        }
                                    }
                                    Log.d("TAG", "用户名" + userInfo.getUserId() + userInfo.getCardNo());
                                    dialog.dismiss();
                                    loginSuccess();//调用登录成功方法

                                }
                                if ("false".equals(result.getSuccess())) {
                                    Log.d("TAG", "验证密码");
                                    dialog.dismiss();
                                    mpasswordLogin.setError("密码输入错误");

                                }
                            }

                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "服务器抽风了，一会儿再试试", Toast.LENGTH_SHORT).show();
                mbtnLoginLogin.setClickable(true);
            }
        });
        mQueue.add(stringRequest);
    }

    /**
     * QQ登录
     */
    private void QQLogin() {
        disanfangLogin(QQ.NAME);

    }

    /**
     * 微信登录
     */
    private void weixinLogin() {
        disanfangLogin(Wechat.NAME);
        Log.d("TAG", "微信登录");

    }

    /**
     * 微博登录
     */
    private void sinaLogin() {
        disanfangLogin(SinaWeibo.NAME);
    }

    /**
     * 第三方登录
     *
     * @param name
     */
    private void disanfangLogin(String name) {
        Platform platform = ShareSDK.getPlatform(this, name);
        platform.setPlatformActionListener(this);//授权监听
        platform.authorize();
        //判断是否验证（判断是否成功登录）
        if (platform.isValid()) {//通过验证
            String userName = platform.getDb().getUserName();//获取第三方平台显示的名称
            String password = platform.getDb().getUserId();//获取第三方平台的密码
            Log.i("TAG", "验证通-----------------过-----" + userName + password);
        } else {
            //没有通过验证

        }
        platform.showUser(null);//授权并获取用户信息

    /*    private void authorize(Platform platform) {
            if (platform == null) {
                popupOthers();
                return;
            }
//判断指定平台是否已经完成授权
            if(plat.isAuthValid()) {
                String userId = plat.getDb().getUserId();
                if (userId != null) {
                    UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
                    login(plat.getName(), userId, null);
                    return;
                }
            }
            plat.setPlatformActionListener(this);
            // true不使用SSO授权，false使用SSO授权
            plat.SSOSetting(true);
            //获取用户资料
            plat.showUser(null);
        }*/
    }


    //授权完成
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        String userName = platform.getDb().getUserName();//获取第三方平台显示的名称
        String password = platform.getDb().getUserId();//获取第三方平台的密码
        String userIcon = platform.getDb().getUserIcon();//获取第三方平台的头标

        Log.i("TAG", userName + "========" + password + "=====" + userIcon + "------" + i + "------" + hashMap.toString());
    }

    //授权出错
    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    //取消授权
    @Override
    public void onCancel(Platform platform, int i) {

    }

    /**
     * 返回按钮
     */
    private void backLogin() {


    }


    public void showMyDialog(View v) {
        dialog = new LoadingProgressDialogView(this, "正在加载中...", R.anim.operating);
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mbtnLoginLogin.setClickable(true);
            }
        }, 5000);
    }
}
