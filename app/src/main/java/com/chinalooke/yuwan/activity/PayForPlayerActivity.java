package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.avos.avoscloud.AVAnalytics;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.NearbyPeople;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.MyUtils;
import com.google.gson.Gson;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayForPlayerActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.et_phone)
    EditText mEtPhone;
    private String mPhone;
    private ProgressDialog mProgressDialog;
    private RequestQueue mQueue;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_for_player);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        initView();
    }

    private void initView() {
        mTvTitle.setText("充值");
    }

    @OnClick({R.id.iv_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_next:
                if (checkInput()) {
                    getUsersWithPhoneArray();
                }
                break;
        }
    }

    //查询手机用户是否存在
    private void getUsersWithPhoneArray() {
        mProgressDialog = MyUtils.initDialog("", PayForPlayerActivity.this);
        mProgressDialog.show();
        String url = Constant.HOST + "getUsersWithPhoneArray&phones=" + mPhone;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                if (AnalysisJSON.analysisJson(response)) {
                    Gson gson = new Gson();
                    NearbyPeople nearbyPeople = gson.fromJson(response, NearbyPeople.class);
                    List<NearbyPeople.ResultBean> result = nearbyPeople.getResult();
                    if (result != null && result.size() != 0) {
                        NearbyPeople.ResultBean resultBean = result.get(0);
                        Intent intent = new Intent(PayForPlayerActivity.this, RechargeActivity.class);
                        intent.putExtra("userId", resultBean.getId());
                        startActivity(intent);
                    }
                } else {
                    MyUtils.showMsg(mToast, response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("网络不给力，请稍后再试");
                mToast.show();
            }
        });
        mQueue.add(request);
    }

    //检查输入
    private boolean checkInput() {
        mPhone = mEtPhone.getText().toString();
        if (TextUtils.isEmpty(mPhone)) {
            mEtPhone.setError("请输入手机号");
            mEtPhone.setFocusable(true);
            mEtPhone.setFocusableInTouchMode(true);
            mEtPhone.requestFocus();
            return false;
        }

        if (!MyUtils.CheckPhoneNumber(mPhone)) {
            mEtPhone.setError("请输入有效的手机号");
            mEtPhone.setFocusable(true);
            mEtPhone.setFocusableInTouchMode(true);
            mEtPhone.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }
}
