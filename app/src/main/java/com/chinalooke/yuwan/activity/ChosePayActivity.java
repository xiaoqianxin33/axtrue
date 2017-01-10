package com.chinalooke.yuwan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.avos.avoscloud.AVAnalytics;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.ExchangeLevels;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.pingplusplus.android.Pingpp;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChosePayActivity extends AutoLayoutActivity {

    @Bind(R.id.iv_wx_check)
    ImageView mIvWxCheck;
    @Bind(R.id.iv_ali_check)
    ImageView mIvAliCheck;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.btn_pay)
    Button mBtnPay;
    private ExchangeLevels.ResultBean mResultBean;
    private RequestQueue mQueue;
    private Toast mToast;
    private String mUserId;
    private int mPayMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_pay);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        initData();
    }

    private void initData() {
        mResultBean = (ExchangeLevels.ResultBean) getIntent().getSerializableExtra("pay");

        mUserId = getIntent().getStringExtra("userId");
        String money = mResultBean.getMoney();
        if (!TextUtils.isEmpty(money)) {
            mTvPrice.setText(getString(R.string.yuan, money));
            float parseFloat = Float.parseFloat(money) * 100;
            mPayMoney = (int) parseFloat;
        }
    }

    @OnClick({R.id.fl_x, R.id.rl_wx, R.id.rl_ali, R.id.btn_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_x:
                finish();
                break;
            case R.id.rl_wx:
                mIvWxCheck.setVisibility(View.VISIBLE);
                mIvAliCheck.setVisibility(View.GONE);
                break;
            case R.id.rl_ali:
                mIvWxCheck.setVisibility(View.GONE);
                mIvAliCheck.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_pay:
                if (mResultBean != null) {
                    mBtnPay.setEnabled(false);
                    getUUID();
                }
                break;
        }
    }

    //生成uuId为订单号
    private void getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
        String orderId = temp.substring(0, 20);
        payment(orderId);
    }

    //支付调用ping++
    private void payment(String orderId) {
        try {
            String url = Constant.HOST + "payment&orderNo=" + orderId + "&amount=" + mPayMoney
                    + "&subject=" + URLEncoder.encode("雷熊币充值", "UTF-8") + "&body=" + URLEncoder.encode("雷熊币充值", "UTF-8") + "&userId" + mUserId;
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {
                    if (!TextUtils.isEmpty(response)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Pingpp.createPayment(ChosePayActivity.this, response);
                            }
                        }).start();
                    } else {
                        mToast.setText("网络不给力，请稍后再试");
                        mToast.show();
                        mBtnPay.setEnabled(true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mToast.setText("网络不给力，请稍后再试");
                    mToast.show();
                    mBtnPay.setEnabled(true);
                }
            });

            mQueue.add(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
            /* 处理返回值
             * "success" - 支付成功
             * "fail"    - 支付失败
             * "cancel"  - 取消支付
             * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
             */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                assert result != null;
                switch (result) {
                    case "success":
                        // TODO: 2017/1/9 做一个支付成功页面
                        mToast.setText("支付成功");
                        mToast.show();
                        finish();
                        break;
                    case "fail":
                        Log.e("TAG", errorMsg + ":" + extraMsg);
                        mToast.setText("支付失败" + errorMsg + extraMsg);
                        mToast.show();
                        finish();
                        break;
                    case "cancel":
                        mToast.setText("支付取消");
                        mToast.show();
                        finish();
                        break;
                    case "invalid":
                        mToast.setText("支付插件未安装");
                        mToast.show();
                        finish();
                        break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
