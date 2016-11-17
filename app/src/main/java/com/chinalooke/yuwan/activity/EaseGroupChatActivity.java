package com.chinalooke.yuwan.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chinalooke.yuwan.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.exceptions.HyphenateException;

public class EaseGroupChatActivity extends AppCompatActivity {

    private String mGroupId;

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
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
        args.putString(EaseConstant.EXTRA_USER_ID, mGroupId);
        easeChatFragment.setArguments(args);
        supportFragmentManager.beginTransaction().replace(R.id.activity_ease_group_chat, easeChatFragment).commit();

    }

    private void initData() {
        mGroupId = getIntent().getStringExtra("groupId");
        EMGroup groupLocal = EMClient.getInstance().groupManager().getGroup(mGroupId);
        if (groupLocal == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(mGroupId);

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
