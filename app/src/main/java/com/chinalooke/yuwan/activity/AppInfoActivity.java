package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.utils.AppUtils;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//关于软件界面
public class AppInfoActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_version)
    TextView mTvVersion;
    @Bind(R.id.tv_title)
    TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTvTitle.setText("关于软件");
        String appVersionName = AppUtils.getAppVersionName(getApplicationContext());
        if (!TextUtils.isEmpty(appVersionName))
            mTvVersion.setText(getString(R.string.version, appVersionName));

    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
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
