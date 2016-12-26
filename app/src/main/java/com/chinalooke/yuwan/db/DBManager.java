package com.chinalooke.yuwan.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chinalooke.yuwan.bean.GameMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理类
 * Created by xiao on 2016/11/9.
 */

public class DBManager {

    private SQLiteDatabase db;

    public DBManager(Context context) {
        DBHelper helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    public void add(List<GameMessage.ResultBean> games) {
        db.beginTransaction();  //开始事务
        try {
            for (GameMessage.ResultBean person : games) {
                db.execSQL("INSERT OR REPLACE INTO person VALUES(?,?,?,?,?,?,?)", new Object[]{person.getGameId(), person.getName(), person.getThumb()
                        , person.getMaxPeopleNumber(), person.getTimes(), person.getWagerMax(), person.getWagerMin()});
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
            person.setGameId(c.getString(c.getColumnIndex("gameId")));
            person.setThumb(c.getString(c.getColumnIndex("url")));
            person.setMaxPeopleNumber(c.getString(c.getColumnIndex("maxPeopleNumber")));
            person.setTimes(c.getString(c.getColumnIndex("times")));
            person.setWagerMax(c.getString(c.getColumnIndex("wagerMax")));
            person.setWagerMin(c.getString(c.getColumnIndex("wagerMin")));
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
            resultBean.setGameId(c.getString(c.getColumnIndex("gameId")));
            resultBean.setThumb(c.getString(c.getColumnIndex("url")));
            resultBean.setMaxPeopleNumber(c.getString(c.getColumnIndex("maxPeopleNumber")));
            resultBean.setTimes(c.getString(c.getColumnIndex("times")));
            resultBean.setWagerMax(c.getString(c.getColumnIndex("wagerMax")));
            resultBean.setWagerMin(c.getString(c.getColumnIndex("wagerMin")));
        }
        c.close();
        return resultBean;
    }

    private Cursor queryTheCursorById(String gameId) {
        return db.rawQuery("SELECT * FROM person WHERE gameId=?", new String[]{gameId});
    }

}
