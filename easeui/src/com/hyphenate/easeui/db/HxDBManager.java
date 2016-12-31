package com.hyphenate.easeui.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hyphenate.easeui.model.UsersWithRoomId;

import java.util.ArrayList;
import java.util.List;

public class HxDBManager {
    private SQLiteDatabase db;

    public HxDBManager(Context context) {
        HxDBHelper helper = new HxDBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }


    public void add(List<UsersWithRoomId.ResultBean.PlayersBean> playersBeen) {
        db.beginTransaction();  //开始事务
        try {
            for (UsersWithRoomId.ResultBean.PlayersBean person : playersBeen) {
                db.execSQL("INSERT OR REPLACE INTO user VALUES(?,?,?)", new Object[]{person.getUserId(), person.getNickName(), person.getPhoneNumber()});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void closeDB() {
        db.close();
    }

    public List<UsersWithRoomId.ResultBean.PlayersBean> query() {
        ArrayList<UsersWithRoomId.ResultBean.PlayersBean> persons = new ArrayList<>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            UsersWithRoomId.ResultBean.PlayersBean person = new UsersWithRoomId.ResultBean.PlayersBean();
            person.setNickName(c.getString(c.getColumnIndex("nickName")));
            person.setUserId(c.getString(c.getColumnIndex("userId")));
            person.setPhoneNumber(c.getString(c.getColumnIndex("phoneNumber")));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    public UsersWithRoomId.ResultBean.PlayersBean queryByPhone(String phone) {
        UsersWithRoomId.ResultBean.PlayersBean resultBean = new UsersWithRoomId.ResultBean.PlayersBean();
        Cursor c = queryTheCursorById(phone);
        while (c.moveToNext()) {
            resultBean.setUserId(c.getString(c.getColumnIndex("userId")));
            resultBean.setNickName(c.getString(c.getColumnIndex("nickName")));
            resultBean.setPhoneNumber(c.getString(c.getColumnIndex("phoneNumber")));
        }
        c.close();
        return resultBean;
    }

    private Cursor queryTheCursor() {
        return db.rawQuery("SELECT * FROM user", null);
    }

    private Cursor queryTheCursorById(String gameId) {
        return db.rawQuery("SELECT * FROM user WHERE phoneNumber=?", new String[]{gameId});
    }
}
