package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.GameDeskDetails;
import com.chinalooke.yuwan.db.ExchangeHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//我的推送消息界面
public class MyMessageActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.list_view)
    ListView mListView;
    private ExchangeHelper mHelper;
    private List<GameDeskDetails.ResultBean> mResultBeen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        mHelper = ExchangeHelper.getHelper(getApplicationContext());
        initView();
        initData();
    }

    private void initView() {
        mTvTitle.setText("我的消息");
    }

    private void initData() {
        try {
            Dao<GameDeskDetails.ResultBean, Integer> gameDao = mHelper.getGameDao();

            JSONObject json = new JSONObject(getIntent().getExtras().getString("com.avos.avoscloud.Data"));
            if (json != null) {
                String gameDeskDetails = json.getString("gameDeskDetails");
                Gson gson = new Gson();
                Type type = new TypeToken<GameDeskDetails>() {
                }.getType();
                GameDeskDetails gameDeskDetails1 = gson.fromJson(gameDeskDetails, type);
                GameDeskDetails.ResultBean result = gameDeskDetails1.getResult();
                if (result != null) {
                    gameDao.createOrUpdate(result);
                }
            }
            mResultBeen = gameDao.queryForAll();
            MyAdapter myAdapter = new MyAdapter(mResultBeen);
            mListView.setAdapter(myAdapter);
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }

    class MyAdapter extends MyBaseAdapter {


        MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

}
