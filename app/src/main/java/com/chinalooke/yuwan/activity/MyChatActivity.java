package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;

import com.chinalooke.yuwan.R;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.ButterKnife;

public class MyChatActivity extends AutoLayoutActivity {

    private EaseConversationListFragment conversationListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        conversationListFragment = new EaseConversationListFragment();
        conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {

            @Override
            public void onListItemClicked(EMConversation conversation) {
                EMConversation.EMConversationType type = conversation.getType();
                if (type == EMConversation.EMConversationType.GroupChat)
                    startActivity(new Intent(MyChatActivity.this, EaseGroupChatActivity.class).putExtra("groupId", conversation.getUserName()));
                else if(type== EMConversation.EMConversationType.Chat)
                    startActivity(new Intent(MyChatActivity.this, EaseChatActivity.class).putExtra("userId", conversation.getUserName()));
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_chat, conversationListFragment).commit();

    }

}
