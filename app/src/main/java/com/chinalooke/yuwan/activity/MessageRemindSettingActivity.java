package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageRemindSettingActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.switch_button_message)
    EaseSwitchButton mSwitchButtonMessage;
    @Bind(R.id.switch_button_dynamic)
    EaseSwitchButton mSwitchButtonDynamic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_remid_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTvTitle.setText("消息提醒设置");
        boolean message_remind = PreferenceUtils.getPrefBoolean(getApplicationContext(), "message_remind", true);
        if (message_remind) {
            mSwitchButtonMessage.openSwitch();
        } else {
            mSwitchButtonMessage.closeSwitch();
        }
        boolean dynamic_remind = PreferenceUtils.getPrefBoolean(getApplicationContext(), "dynamic_remind", true);
        if (dynamic_remind) {
            mSwitchButtonDynamic.openSwitch();
        } else {
            mSwitchButtonDynamic.closeSwitch();
        }
    }

    @OnClick({R.id.iv_back, R.id.switch_button_message, R.id.switch_button_dynamic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.switch_button_message:
                boolean switchOpen = mSwitchButtonMessage.isSwitchOpen();
                if (switchOpen) {
                    mSwitchButtonMessage.closeSwitch();
                    PreferenceUtils.setPrefBoolean(getApplicationContext(), "message_remind", false);
                } else {
                    mSwitchButtonMessage.openSwitch();
                    PreferenceUtils.setPrefBoolean(getApplicationContext(), "message_remind", true);
                }
                break;
            case R.id.switch_button_dynamic:
                boolean switchOpen1 = mSwitchButtonDynamic.isSwitchOpen();
                if (switchOpen1) {
                    mSwitchButtonDynamic.closeSwitch();
                    PreferenceUtils.setPrefBoolean(getApplicationContext(), "dynamic_remind", false);
                } else {
                    mSwitchButtonDynamic.openSwitch();
                    PreferenceUtils.setPrefBoolean(getApplicationContext(), "dynamic_remind", true);
                }

                break;
        }
    }
}
