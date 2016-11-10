package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.ResultDatas;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.GetHTTPDatas;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.Validator;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.SMSSDK;

import static com.chinalooke.yuwan.constant.Constant.MIN_CLICK_DELAY_TIME;
import static com.chinalooke.yuwan.constant.Constant.lastClickTime;

/**
 * 重置密码界面
 * Created by Administrator on 2016/8/29.
 */
public class ResetPasswordActivity extends AutoLayoutActivity {
    //密码
    @Bind(R.id.new_reset_password)
    EditText mNewPassword;
    //重填密码
    @Bind(R.id.renew_reset_password)
    EditText mReNewPassword;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    private Toast mToast;
    private String mPassword;
    private ProgressDialog mProgressDialog;
    private RequestQueue mQueue;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_reset_password);
        ButterKnife.bind(this);
        mToast = YuwanApplication.getToast();
        mQueue = YuwanApplication.getQueue();
        initView();
    }

    private void initView() {
        mTvTitle.setText("重置密码");
    }

    @OnClick({R.id.btn_next_reset_password, R.id.iv_back})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.btn_next_reset_password:
                    if (checkInput()) {
                        resetPassword();
                    }
                    break;
            }
        }
    }

    private boolean checkInput() {
        mPassword = mNewPassword.getText().toString();
        String rePassword = mReNewPassword.getText().toString();
        if (TextUtils.isEmpty(mPassword)) {
            mToast.setText("请输入新密码");
            mToast.show();
            mNewPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(rePassword)) {
            mToast.setText("请再次输入密码");
            mToast.show();
            mReNewPassword.requestFocus();
            return false;
        }

        if (!mPassword.equals(rePassword)) {
            mToast.setText("两次密码输入不一致，请重新输入");
            mToast.show();
            mNewPassword.requestFocus();
            return false;
        }

        if (mPassword.length() < 6 || mPassword.length() > 16) {
            mToast.setText("密码应在6-16位之间");
            mToast.show();
            mNewPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void resetPassword() {
        //提交修改密码
        mProgressDialog = MyUtils.initDialog("正在提交...", ResetPasswordActivity.this);
        mProgressDialog.show();
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        String uri = Constant.FORGOT_PWD + "&" + Constant.PHONE + phone + "&" + Constant.PWD + mPassword;
        submitPassword(uri);
    }

    private void submitPassword(String uri) {
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                if (response != null) {
                    ResultDatas result = AnalysisJSON.getAnalysisJSON().AnalysisJSONResult(response);
                    if ("true".equals(result.getSuccess()) && "false".equals(result.getResult())) {
                        mToast.setText("修改失败");
                        mToast.show();
                    }
                    if ("true".equals(result.getSuccess()) && "true".equals(result.getResult())) {
                        Log.d("TAG", "修改成功跳到下一步");
                        successResetPassWord();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("网速不给力，换个地方试试");
                mToast.show();
            }
        });
        mQueue.add(request);
    }

    /**
     * 修改成功跳到下一步
     */
    private void successResetPassWord() {
        startActivity(new Intent(ResetPasswordActivity.this, FindPwdCompleteActivity.class));
        finish();
    }
}
