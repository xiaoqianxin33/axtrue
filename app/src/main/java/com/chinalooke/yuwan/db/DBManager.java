package com.chinalooke.yuwan.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chinalooke.yuwan.model.Game;
import com.chinalooke.yuwan.model.GameMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理类
 * Created by xiao on 2016/11/9.
 */

public class DBManager {

    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    public void add(List<GameMessage.ResultBean> games) {
        db.beginTransaction();  //开始事务
        try {
            for (GameMessage.ResultBean person : games) {
                db.execSQL("INSERT INTO person VALUES(null, ?, ?, ?)", new Object[]{person.getName(), person.getGameId(), person.getThumb()});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public void closeDB() {
        db.close();
    }

    public List<GameMessage.ResultBean> query() {
        ArrayList<GameMessage.ResultBean> persons = new ArrayList<>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            GameMessage.ResultBean person = new GameMessage.ResultBean();
            person.setName(c.getString(c.getColumnIndex("name")));
            person.setGameId(c.getString(c.getColumnIndex("id")));
            person.setThumb(c.getString(c.getColumnIndex("url")));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    private Cursor queryTheCursor() {
        return db.rawQuery("SELECT * FROM person", null);
    }

    public GameMessage.ResultBean queryById(String gameId) {
        GameMessage.ResultBean resultBean = new GameMessage.ResultBean();
        Cursor c = queryTheCursorById(gameId);
        while (c.moveToNext()) {
            resultBean.setName(c.getString(c.getColumnIndex("name")));
            resultBean.setGameId(c.getString(c.getColumnIndex("id")));
            resultBean.setThumb(c.getString(c.getColumnIndex("url")));
        }
        c.close();
        return resultBean;
    }

    private Cursor queryTheCursorById(String gameId) {
        return db.rawQuery("SELECT * FROM person where id=?", new String[]{gameId});
    }

}
