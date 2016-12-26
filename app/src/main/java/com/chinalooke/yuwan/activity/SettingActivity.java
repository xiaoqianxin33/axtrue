package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.utils.DataCleanManager;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//设置界面
public class SettingActivity extends AutoLayoutActivity {

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
    @Bind(R.id.rl_remind)
    RelativeLayout mRlRemind;
    @Bind(R.id.rl_image)
    RelativeLayout mRlImage;
    @Bind(R.id.rl_app)
    RelativeLayout mRlApp;
    @Bind(R.id.rl_help)
    RelativeLayout mRlHelp;
    @Bind(R.id.rl_clear_cache)
    RelativeLayout mRlClearCache;
    @Bind(R.id.tv_cache)
    TextView mTvCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTvTitle.setText("设置");
        try {
            mTvCache.setText(DataCleanManager.getTotalCacheSize(getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.iv_back, R.id.rl_head, R.id.rl_remind, R.id.rl_image, R.id.rl_app, R.id.rl_help, R.id.rl_clear_cache})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_head:
                break;
            case R.id.rl_remind:
                startActivity(new Intent(this, MessageRemindSettingActivity.class));
                break;
            case R.id.rl_image:
                startActivity(new Intent(this, ImageSettingActivity.class));
                break;
            case R.id.rl_app:
                break;
            case R.id.rl_help:
                break;
            case R.id.rl_clear_cache:
                DataCleanManager.clearAllCache(getApplicationContext());
                mTvCache.setText("0K");
                break;
        }
    }
}
