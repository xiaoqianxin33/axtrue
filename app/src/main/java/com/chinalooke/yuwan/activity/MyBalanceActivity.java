package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.UserBalance;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.autolayout.AutoLayoutActivity;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyBalanceActivity extends AutoLayoutActivity {


    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.tv_score)
    TextView mTvScore;
    private RequestQueue mQueue;
    private LoginUser.ResultBean mUser;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_balance);
        ButterKnife.bind(this);
        mToast = YuwanApplication.getToast();
        mQueue = YuwanApplication.getQueue();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        initView();
        initData();
    }

    private void initView() {
        mTvTitle.setText("余额");
        mTvSkip.setText("明细");
        mTvSkip.setTextColor(getResources().getColor(R.color.white));
    }

    private void initData() {
        String score = PreferenceUtils.getPrefString(getApplicationContext(), "score", "0");
        mTvScore.setText(score);
        getUserBalance();
    }

    //查询余额
    private void getUserBalance() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String uri = Constant.HOST + "getUserBalance&userId=" + mUser.getUserId();
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        if (AnalysisJSON.analysisJson(response)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<UserBalance>() {
                            }.getType();
                            UserBalance userBalance = gson.fromJson(response, type);
                            String payMoney = userBalance.getResult().getPayMoney();
                            PreferenceUtils.setPrefString(getApplicationContext(), "score", payMoney);
                            mTvScore.setText(payMoney);
                        } else {
                            MyUtils.showMsg(mToast, response);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mToast.setText("服务器抽风了,更新余额信息失败");
                    mToast.show();
                }
            });

            mQueue.add(request);
        } else {
            mToast.setText("网络不可用,更新余额信息失败");
            mToast.show();
        }
    }

    @OnClick({R.id.iv_back, R.id.rl_recharge, R.id.tv_skip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_skip:
                startActivity(new Intent(this, AccountDetailActivity.class));
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_recharge:
                startActivity(new Intent(this, RechargeActivity.class));
                break;
        }
    }
}
