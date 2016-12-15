package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

    @Bind(R.id.iv_back)
    FrameLayout mIvBack;
    @Bind(R.id.iv_arrow_head)
    ImageView mIvArrowHead;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.rl_head)
    RelativeLayout mRlHead;
    @Bind(R.id.et_phone)
    EditText mEtPhone;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.activity_pay_for_player)
    LinearLayout mActivityPayForPlayer;
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
}
