package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.Validator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Calendar;
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

import static com.chinalooke.yuwan.constant.Constant.MIN_CLICK_DELAY_TIME;
import static com.chinalooke.yuwan.constant.Constant.lastClickTime;


public class LoginActivity extends AutoLayoutActivity implements PlatformActionListener {
    //登录按钮
    @Bind(R.id.btn_login_login)
    Button mBtnLoginLogin;
    //手机号码
    @Bind(R.id.phone_login)
    EditText mEtPhone;
    //密码
    @Bind(R.id.password_login)
    EditText mEtPassword;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    private String phone;
    private String passWord;
    private RequestQueue mQueue;
    private Toast mToast;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        //初始化Sharesdk;
        ShareSDK.initSDK(this);
        initView();
        initEvent();
    }

    private void initView() {
        mBtnLoginLogin.setBackgroundColor(getResources().getColor(R.color.grey));
        mBtnLoginLogin.setEnabled(false);
        mTvTitle.setText("登录");
        mTvSkip.setText("注册");
        mTvSkip.setTextColor(getResources().getColor(R.color.unselectcolor));
    }

    private void initEvent() {
        mEtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String phone = mEtPhone.getText().toString();
                if (hasFocus && !TextUtils.isEmpty(phone)) {
                    mBtnLoginLogin.setBackgroundColor(getResources().getColor(R.color.btn_yellow));
                    mBtnLoginLogin.setEnabled(true);
                }
            }
        });

        mEtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }
                    if (Validator.isNetworkAvailable(LoginActivity.this)) {
                        //判断是否网络未连接
                        handleLogin();
                    } else {
                        mToast.setText("亲掉线了，换个地方试试");
                        mToast.show();
                    }
                    return true;
                }


                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK();
    }

    @OnClick({R.id.QQ_login, R.id.weixin_login, R.id.weibo_login, R.id.btn_login_login,
            R.id.tv_skip, R.id.iv_back, R.id.forget_pwd_login})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                //注册按钮
                case R.id.tv_skip:
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    break;
                //登录按钮
                case R.id.btn_login_login:
                    if (Validator.isNetworkAvailable(LoginActivity.this)) {
                        //判断是否网络未连接
                        handleLogin();
                    } else {
                        mToast.setText("亲掉线了，换个地方试试");
                        mToast.show();
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
                case R.id.iv_back:
                    finish();
                    break;
                //忘记密码
                case R.id.forget_pwd_login:
                    ClickForgetPwd();
                    break;
            }
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
        phone = mEtPhone.getText().toString();
        passWord = mEtPassword.getText().toString();
        if (phone.equals("")) {
            mEtPhone.setError("请输入手机号码");
            mEtPhone.requestFocus();
        } else if (!Validator.isMobile(phone)) {
            mEtPhone.setError("请输入正确的手机号码");
            mEtPhone.requestFocus();
        } else if ("".equals(passWord)) {
            mEtPassword.setError("请输入密码");
            mEtPassword.requestFocus();
        } else {
            getHTTPIsPhoneExists();
            //弹出正在登陆
            mProgressDialog = MyUtils.initDialog("正在加载中...", LoginActivity.this);
            mProgressDialog.show();
        }

    }

    //登陆成功跳转到首界面
    private void loginSuccess() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                        //解析数据
                        if (response != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("Success");
                                if (success) {
                                    boolean result = jsonObject.getBoolean("Result");
                                    if (result) {
                                        if ("".equals(passWord)) {
                                            //判断密码是否为空
                                            mEtPassword.setError("请输入密码");
                                            mProgressDialog.dismiss();
                                        } else {
                                            //进行登录
                                            getHTTPLoginSuccess();
                                        }
                                    } else {
                                        mProgressDialog.dismiss();
                                        MyUtils.showNorDialog(LoginActivity.this, "提示", "您的手机号码" + phone + "未注册雷熊，现在就注册么？",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                                        intent.putExtra("phone", phone);
                                                        startActivity(intent);
                                                    }
                                                });
                                    }
                                } else {
                                    mProgressDialog.dismiss();
                                    mToast.setText("网络不给力啊，换个地方试试");
                                    mToast.show();
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
                mToast.setText("网络不给力啊，换个地方试试");
                mToast.show();
            }
        });
        mQueue.add(stringRequest);
    }

    // 手机号验证通过后验证登录是否成功
    //网络获取  验证手机号是否注册
    private void getHTTPLoginSuccess() {
        String URLLogin = Constant.LOGIN_RESULT + "&" + Constant.PHONE + phone + "&" + Constant.PWD + passWord;
        StringRequest stringRequest = new StringRequest(URLLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            analysisJson(response);
                        }
                        //解析数据
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "服务器抽风了，一会儿再试试", Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(stringRequest);
    }

    private void analysisJson(String response) {
        if (AnalysisJSON.analysisJson(response)) {
            Gson gson = new Gson();
            Type type = new TypeToken<LoginUser>() {
            }.getType();
            LoginUser userInfo = gson.fromJson(response, type);
            if (userInfo != null) {
                Log.e("TAG", "userInfo != null");
                LoginUserInfoUtils.getLoginUserInfoUtils().setUserInfo(userInfo.getResult());//设置userInfo
                LoginUserInfoUtils.saveObject(LoginActivity.this, LoginUserInfoUtils.KEY, userInfo.getResult());
            }
            mProgressDialog.dismiss();
            loginSuccess();//调用登录成功方法
        } else {
            mProgressDialog.dismiss();
            mToast.setText("密码错误，请重新输入");
            mToast.show();
        }
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
     * @param name name
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
    }


    //授权完成
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//        String userName = platform.getDb().getUserName();//获取第三方平台显示的名称
//        String password = platform.getDb().getUserId();//获取第三方平台的密码
//        String userIcon = platform.getDb().getUserIcon();//获取第三方平台的头标
        final String token = platform.getDb().getToken();
        String uri = Constant.HOST + "isAuthExists&auth=" + token;
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            String result = jsonObject.getString("Result");
                            JSONObject jsonObject2 = new JSONObject(result);
                            String phone = jsonObject2.getString("phone");
                            getUserInfoWithAuth(phone, token);
                        } else {
                            String msg = jsonObject.getString("Msg");
                            if ("无该签权".equals(msg)) {
                                Intent intent = new Intent(LoginActivity.this, BindPhoneActivity.class);
                                intent.putExtra("auth", token);
                                startActivity(intent);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mToast.setText("网速不给力啊，换个地方试试");
                    mToast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mToast.setText("网速不给力啊，换个地方试试");
                mToast.show();
            }
        });
        mQueue.add(request);
    }

    /**
     * 获取第三方登录过的用户资料
     */
    private void getUserInfoWithAuth(String phone, String auth) {
        String uri = Constant.HOST + "getUserInfoWithAuth&phone=" + phone + "&auth=" + auth;
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                analysisJson(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mToast.setText("网速不给力啊，换个地方试试");
                mToast.show();
            }
        });
        mQueue.add(request);
    }

    //授权出错
    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    //取消授权
    @Override
    public void onCancel(Platform platform, int i) {

    }

}
