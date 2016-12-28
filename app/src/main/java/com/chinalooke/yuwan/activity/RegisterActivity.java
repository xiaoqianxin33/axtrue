package com.chinalooke.yuwan.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.Auth;
import com.chinalooke.yuwan.utils.BitmapUtils;
import com.chinalooke.yuwan.utils.ImageUtils;
import com.chinalooke.yuwan.utils.LeanCloudUtil;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.Validator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.picasso.Picasso;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 注册缓冲未做，向服务端发送介绍人未写 check的选中未写
 * Created by Administrator on 2016/8/23.
 */
public class RegisterActivity extends AutoLayoutActivity implements CompoundButton.OnCheckedChangeListener, EasyPermissions.PermissionCallbacks {
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
    @Bind(R.id.rl_recommend)
    RelativeLayout mRlRecommend;
    @Bind(R.id.rl_license)
    RelativeLayout mRlLicense;
    @Bind(R.id.iv_license)
    ImageView mIvLicense;
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
    private boolean isNetbar;
    private int RC_ACCESS_FINE_LOCATION = 0;
    private ImgSelConfig mConfig;
    private String mPath;
    private UploadManager mUploadManager;
    private String mNickName;

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
        mUploadManager = YuwanApplication.getmUploadManager();
        //设置密码类型
        mcountTimer = new CountTimer(60000, 1000);
        mProgressDialog = MyUtils.initDialog("正在注册...", RegisterActivity.this);
        initView();
    }

    private void initView() {
        String phone = getIntent().getStringExtra("phone");
        if (!TextUtils.isEmpty(phone)) {
            mphoneRegister.setText(phone);
        }

        isNetbar = getIntent().getBooleanExtra("netbar", false);
        mRlRecommend.setVisibility(isNetbar ? View.GONE : View.VISIBLE);
        mRlLicense.setVisibility(isNetbar ? View.VISIBLE : View.GONE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eh != null)
            //解除注册
            SMSSDK.unregisterEventHandler(eh);
    }

    @OnClick({R.id.btn__get_verification_code_register, R.id.btn_register_register, R.id.iv_back, R.id.rl_license})
    public void onClick(View view) {
        switch (view.getId()) {
            //营业执照点击上传
            case R.id.rl_license:
                req();
                break;
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void req() {
        initPhotoPicker();
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            ImgSelActivity.startActivity(this, mConfig, 5);
        } else {
            EasyPermissions.requestPermissions(this, "需要拍照权限",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }

    //初始化图片选择器
    private void initPhotoPicker() {
        ImageLoader loader = new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Picasso.with(context).load("file://" + path).into(imageView);
            }
        };

        mConfig = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(false)
                // “确定”按钮背景色
                .btnBgColor(Color.GRAY)
                // “确定”按钮文字颜色
                .btnTextColor(Color.BLUE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 返回图标ResId
                .backResId(R.drawable.ic_back)
                // 标题
                .title("图片")
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#3F51B5"))
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(1, 1, 200, 200)
                .needCrop(false)
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(9)
                .build();
    }

    /**
     * 注册成功跳转页面
     */
    private void registerSuccess() {
        if (isNetbar) {
            Intent intent = new Intent(this, NetbarInfoActivity.class);
            intent.putExtra("netbarId", userId);
            intent.putExtra("netbarLicense", mPath);
            intent.putExtra("head", headImg);
            intent.putExtra("nickName", mNickName);
            startActivity(intent);
        } else {
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
        }
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
        String passWord = mrepasswordRegister.getText().toString();
        mIntroducePhone = mIntroducePhoneRegister.getText().toString();
        if (!MyUtils.CheckPhoneNumber(phone)) {
            mphoneRegister.setError("请输入正确的手机号码");
            return;
        }
        if (!this.passWord.equals(passWord)) {
            mrepasswordRegister.setError("请输入两次相同密码");
            mrepasswordRegister.setText("");
            mpasswordRegister.setText("");
            mpasswordRegister.requestFocus();
            return;
        }
        if (!Validator.isPassword(this.passWord)) {
            //密码不在6-16位之间
            mpasswordRegister.setError("请输入6至16位密码");
            mpasswordRegister.requestFocus();
            return;
        }

        if (isNetbar) {
            if (TextUtils.isEmpty(mPath)) {
                mToast.setText("请上传营业执照");
                mToast.show();
                return;
            }
        }

        //提交验证码
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

    private void checkSMS() {
        LeanCloudUtil.checkSMS(mCode, phone, new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    if (isNetbar) {
                        netbarRegister();
                    } else {
                        getHTTPRegister();
                    }
                } else {
                    mToast.setText("验证码错误，请重试");
                    mToast.show();
                }
            }
        });
    }

    //网吧用户注册
    private void netbarRegister() {
        Auth auth = Auth.create(Constant.QINIU_ACCESSKEY, Constant.QINIU_SECRETKEY);
        String token = auth.uploadToken("yuwan");
        final String fileName = "license" + new Date().getTime();
        Bitmap bitmap = ImageUtils.getBitmap(mPath);
        Bitmap compressBitmap = ImageEngine.getCompressBitmap(bitmap, getApplicationContext());
        if (compressBitmap == null) {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
            mToast.setText("当前设置为非wifi下不能上传图片，请连接wifi");
            mToast.show();
            return;
        }

        mUploadManager.put(BitmapUtils.toArray(compressBitmap), fileName, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.error == null) {
                    mPath = Constant.QINIU_DOMAIN + "/" + fileName;
                    getHTTPRegister();
                } else {
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                }
            }
        }, null);
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
            mbtnGetVerification.setText(getString(R.string.message_timer, (int) millisUntilFinished / 1000));
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
        String URLRegister;
        if (isNetbar) {
            URLRegister = Constant.REGISTER + "&phone=" + phone + "&pwd=" + passWord + "&userType=netbar";
        } else {
            URLRegister = Constant.REGISTER + "&phone=" + phone + "&pwd=" + passWord + "&userType=player&introducerPhone=" + mIntroducePhone;
        }
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
        userId = result.getUserId();
        headImg = result.getHeadImg();
        mNickName = result.getNickName();
        if (!isNetbar) {
            LoginUser.ResultBean resultBean = new LoginUser.ResultBean();
            resultBean.setUserId(userId);
            resultBean.setHeadImg(headImg);
            resultBean.setNickName(mNickName);
            resultBean.setUserType("player");
            try {
                LoginUserInfoUtils.saveLoginUserInfo(getApplicationContext(), LoginUserInfoUtils.KEY, resultBean);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == RC_ACCESS_FINE_LOCATION)
            ImgSelActivity.startActivity(this, mConfig, 5);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            mPath = pathList.get(0);
            Picasso.with(getApplicationContext()).load("file://" + mPath).into(mIvLicense);
        }


    }
}
