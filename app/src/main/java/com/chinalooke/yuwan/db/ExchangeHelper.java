package com.chinalooke.yuwan.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.chinalooke.yuwan.bean.ExchangeLevels;
import com.chinalooke.yuwan.bean.GameDeskDetails;
import com.chinalooke.yuwan.bean.LevelList;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


public class ExchangeHelper extends OrmLiteSqliteOpenHelper {

    private static final String TABLE_NAME = "sqlite-pay.db";
    /**
     * userDao ，每张表对于一个
     */
    private Dao<ExchangeLevels.ResultBean, Integer> userDao;
    private Dao<GameDeskDetails.ResultBean, Integer> gameDao;
    private Dao<LevelList.ResultBean, Integer> levelDao;


    private ExchangeHelper(Context context) {
        super(context, TABLE_NAME, null, 3);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ExchangeLevels.ResultBean.class);
            TableUtils.createTable(connectionSource, GameDeskDetails.ResultBean.class);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

        try {
            TableUtils.dropTable(connectionSource, ExchangeLevels.ResultBean.class, true);
            TableUtils.dropTable(connectionSource, GameDeskDetails.ResultBean.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static ExchangeHelper instance;

    public static synchronized ExchangeHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (ExchangeHelper.class) {
                if (instance == null)
                    instance = new ExchangeHelper(context);
            }
        }

        return instance;
    }

    public Dao<ExchangeLevels.ResultBean, Integer> getUserDao() throws SQLException {
        if (userDao == null) {
            userDao = getDao(ExchangeLevels.ResultBean.class);
        }
        return userDao;
    }

    public void close() {
        super.close();
        userDao = null;
        gameDao = null;
    }


    public Dao<GameDeskDetails.ResultBean, Integer> getGameDao() throws SQLException {
        if (gameDao == null) {
            gameDao = getDao(GameDeskDetails.ResultBean.class);
        }
        return gameDao;
    }

}
