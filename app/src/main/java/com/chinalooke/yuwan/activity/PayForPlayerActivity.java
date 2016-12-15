package com.chinalooke.yuwan.activity;

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

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.utils.MyUtils;
import com.zhy.autolayout.AutoLayoutActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_for_player);
        ButterKnife.bind(this);
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
                    Intent intent = new Intent(this, RechargeActivity.class);
                    intent.putExtra("phone", mPhone);
                    startActivity(intent);
                }
                break;
        }
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
