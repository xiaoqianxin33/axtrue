package com.chinalooke.yuwan.view;

import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;

/**
 * 自定义messageList item
 * Created by xiaoqianxin on 2016/12/30.
 */

public class CustomChatRowProvider implements EaseCustomChatRowProvider {
    @Override
    public int getCustomChatRowTypeCount() {
        return 0;
    }

    @Override
    public int getCustomChatRowType(EMMessage message) {
        return 0;
    }

    @Override
    public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
        return null;
    }
}
