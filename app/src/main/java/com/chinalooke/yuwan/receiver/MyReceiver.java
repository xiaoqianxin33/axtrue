package com.chinalooke.yuwan.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVBroadcastReceiver;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.MyMessageActivity;
import com.chinalooke.yuwan.bean.PushMessage;
import com.chinalooke.yuwan.db.ExchangeHelper;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MyReceiver extends AVBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getExtras() != null) {
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
                    boolean leanMessage = PreferenceUtils.getPrefBoolean(context, "leanMessage", true);
                    if (leanMessage) {
                        Intent intent1 = new Intent(context, MyMessageActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //PendingIntent主要用来处理即将发生的事,相当于Intent的延时,在这里是用来发送广播通知
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        Notification notification = builder
                                .setContentTitle(pushMessage.getTitle())//标题
                                .setContentText(pushMessage.getContent())//内容
                                .setWhen(System.currentTimeMillis())//通知时间，系统时间
                                .setSmallIcon(R.mipmap.icon_512)//标题栏上显示的通知icon
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_512))//通知显示的icon
                                .setDefaults(Notification.DEFAULT_ALL)//DEFAULT_VIBRATE默认震动，DEFAULT_SOUND默认声音,DEFAULT_LIGHTS默认灯光
                                .setColor(Color.parseColor("#98903B"))//smallIcon的背景色
                                .setContentIntent(pendingIntent)
                                .build();
                        notification.flags |= Notification.FLAG_AUTO_CANCEL;
                        manager.notify(1, notification);
                    }
                } catch (SQLException | IOException e) {
                    Log.e("TAG", e.getMessage());
                }
            }
        }
    }

}
