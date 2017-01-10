package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.avos.avoscloud.AVAnalytics;
import com.chinalooke.yuwan.R;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;

public class EaseChatActivity extends AppCompatActivity {

    private String mUserId;
    private String mNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ease_group_chat);
        initData();
        initView();
    }

    private void initView() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        EaseChatFragment easeChatFragment = new EaseChatFragment();
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        args.putString(EaseConstant.EXTRA_USER_ID, mUserId);
        args.putString(EaseConstant.EXTRA_TITLE, mNickName);
        easeChatFragment.setArguments(args);
        supportFragmentManager.beginTransaction().replace(R.id.activity_ease_group_chat, easeChatFragment).commit();
    }

    private void initData() {
        mUserId = getIntent().getStringExtra("userId");
        mNickName = getIntent().getStringExtra("nickName");
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
