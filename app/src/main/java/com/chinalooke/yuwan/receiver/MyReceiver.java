package com.chinalooke.yuwan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.chinalooke.yuwan.bean.PushMessage;
import com.chinalooke.yuwan.db.ExchangeHelper;
import com.chinalooke.yuwan.utils.DateUtils;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String string = intent.getExtras().getString("com.avos.avoscloud.Data");
        if (!TextUtils.isEmpty(string)) {
            try {
                Gson gson = new Gson();
                ExchangeHelper helper = ExchangeHelper.getHelper(context);
                Dao<PushMessage, Integer> pushDao = helper.getPushDao();
                List<PushMessage> pushMessages = pushDao.queryForAll();
                PushMessage pushMessage = gson.fromJson(string, PushMessage.class);
                pushMessage.setId(pushMessages.size());
                pushMessage.setDate(DateUtils.getCurrentDate());
                pushDao.create(pushMessage);
                pushDao.closeLastIterator();
            } catch (SQLException | IOException e) {
                Log.e("TAG", e.getMessage());
            }
        }
    }
}
