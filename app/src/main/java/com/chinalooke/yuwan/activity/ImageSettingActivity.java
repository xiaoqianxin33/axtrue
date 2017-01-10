package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageSettingActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.switch_button_auto)
    EaseSwitchButton mSwitchButtonAuto;
    @Bind(R.id.switch_button_wifi)
    EaseSwitchButton mSwitchButtonWifi;
    @Bind(R.id.iv_auto)
    ImageView mIvAuto;
    @Bind(R.id.iv_high)
    ImageView mIvHigh;
    @Bind(R.id.iv_low)
    ImageView mIvLow;
    private int mLoad_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTvTitle.setText("图片质量设置");
        boolean auto_image = PreferenceUtils.getPrefBoolean(getApplicationContext(), "auto_image", true);
        if (auto_image) {
            mSwitchButtonAuto.openSwitch();
        } else {
            mSwitchButtonAuto.closeSwitch();
        }

        boolean wifi_image = PreferenceUtils.getPrefBoolean(getApplicationContext(), "wifi_image", false);
        if (wifi_image) {
            mSwitchButtonWifi.openSwitch();
        } else {
            mSwitchButtonWifi.closeSwitch();
        }

        mLoad_image = PreferenceUtils.getPrefInt(getApplicationContext(), "load_image", 1);
        setLoad(mLoad_image);
    }

    private void setLoad(int load_image) {
        mIvAuto.setVisibility(load_image == 1 ? View.VISIBLE : View.GONE);
        mIvHigh.setVisibility(load_image == 2 ? View.VISIBLE : View.GONE);
        mIvLow.setVisibility(load_image == 3 ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.iv_back, R.id.switch_button_auto, R.id.switch_button_wifi, R.id.rl_auto, R.id.rl_high, R.id.rl_low})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.switch_button_auto:
                boolean switchOpen = mSwitchButtonAuto.isSwitchOpen();
                if (switchOpen) {
                    mSwitchButtonAuto.closeSwitch();
                    PreferenceUtils.setPrefBoolean(getApplicationContext(), "auto_image", false);
                } else {
                    mSwitchButtonAuto.openSwitch();
                    PreferenceUtils.setPrefBoolean(getApplicationContext(), "auto_image", true);
                }
                break;
            case R.id.switch_button_wifi:
                boolean switchOpen1 = mSwitchButtonWifi.isSwitchOpen();
                if (switchOpen1) {
                    mSwitchButtonWifi.closeSwitch();
                    PreferenceUtils.setPrefBoolean(getApplicationContext(), "wifi_image", false);
                } else {
                    mSwitchButtonWifi.openSwitch();
                    PreferenceUtils.setPrefBoolean(getApplicationContext(), "wifi_image", true);
                }
                break;
            case R.id.rl_auto:
                changeLoad(1);
                break;
            case R.id.rl_high:
                changeLoad(2);
                break;
            case R.id.rl_low:
                changeLoad(3);
                break;
        }
    }

    private void changeLoad(int i) {
        mLoad_image = i;
        setLoad(i);
        PreferenceUtils.setPrefInt(getApplicationContext(), "load_image", i);
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
