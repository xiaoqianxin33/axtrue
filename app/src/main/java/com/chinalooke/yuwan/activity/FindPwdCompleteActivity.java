package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindPwdCompleteActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd_complete);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTvTitle.setText("完成");
    }

    @OnClick({R.id.iv_back, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_login:
                startActivity(new Intent(FindPwdCompleteActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }
}
