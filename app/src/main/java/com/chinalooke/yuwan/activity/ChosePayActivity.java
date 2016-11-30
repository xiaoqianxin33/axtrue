package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChosePayActivity extends AutoLayoutActivity {

    @Bind(R.id.fl_x)
    FrameLayout mFlX;
    @Bind(R.id.iv1)
    ImageView mIv1;
    @Bind(R.id.iv_wx_check)
    ImageView mIvWxCheck;
    @Bind(R.id.rl_wx)
    RelativeLayout mRlWx;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.iv_ali_check)
    ImageView mIvAliCheck;
    @Bind(R.id.rl_ali)
    RelativeLayout mRlAli;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.btn_pay)
    Button mBtnPay;
    @Bind(R.id.activity_chose_pay)
    RelativeLayout mActivityChosePay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_pay);
        ButterKnife.bind(this);

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
                break;
        }
    }
}
