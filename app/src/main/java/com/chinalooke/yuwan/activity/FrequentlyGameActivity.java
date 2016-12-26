package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.db.DBManager;
import com.chinalooke.yuwan.bean.GameMessage;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FrequentlyGameActivity extends AutoLayoutActivity {

    @Bind(R.id.gd_plusgame)
    GridView mGdPlusgame;
    @Bind(R.id.save_personal_info)
    Button mSavePersonalInfo;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.et_input)
    EditText mEtInput;
    private List<GameMessage.ResultBean> mResult = new ArrayList<>();
    private List<GameMessage.ResultBean> mChose = new ArrayList<>();
    private HashMap<GameMessage.ResultBean, String> mHashMap = new HashMap<>();
    private int mCount;
    private boolean isYueZhan;
    private boolean isNetbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequently_game);
        ButterKnife.bind(this);
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        mGdPlusgame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isYueZhan || isNetbar) {
                    GameMessage.ResultBean resultBean = mResult.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("choseGame", resultBean);
                    setResult(0, intent);
                    finish();
                } else {
                    GameMessage.ResultBean resultBean = (GameMessage.ResultBean) parent.getItemAtPosition(position);
                    ImageView viewById = (ImageView) view.findViewById(R.id.iv_check);
                    String s = mHashMap.get(resultBean);
                    if ("0".equals(s)) {
                        viewById.setVisibility(View.VISIBLE);
                        mHashMap.put(resultBean, "1");
                        mCount++;
                    } else if ("1".equals(s)) {
                        viewById.setVisibility(View.GONE);
                        mHashMap.put(resultBean, "0");
                        mCount--;
                    }
                    mSavePersonalInfo.setText("已选(" + mCount + ")");
                }
            }
        });

        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!TextUtils.isEmpty(input)) {
                    List<GameMessage.ResultBean> leftList = new ArrayList<>();
                    for (GameMessage.ResultBean resultBean : mResult) {
                        String name = resultBean.getName();
                        if (name.contains(input)) {
                            leftList.add(resultBean);
                        }
                    }
                    mGdPlusgame.setAdapter(new MyAdapt(leftList));
                } else {
                    mGdPlusgame.setAdapter(new MyAdapt(mResult));
                }
            }
        });
    }

    private void initData() {
        DBManager dbManager = new DBManager(getApplicationContext());
        isYueZhan = getIntent().getBooleanExtra("isYueZhan", false);
        isNetbar = getIntent().getBooleanExtra("isNetbar", false);
        if (isYueZhan || isNetbar) {
            mSavePersonalInfo.setVisibility(View.GONE);
            if (isYueZhan) {
                LoginUser.ResultBean userInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
                assert userInfo != null;
                String[] gameId = userInfo.getGameId();
                for (String id : gameId) {
                    GameMessage.ResultBean resultBean = dbManager.queryById(id);
                    mResult.add(resultBean);
                }
            } else if (isNetbar) {
                mResult = dbManager.query();
            }
        } else {
            mResult = dbManager.query();
        }
        for (int i = 0; i < mResult.size(); i++) {
            GameMessage.ResultBean resultBean = mResult.get(i);
            mHashMap.put(resultBean, "0");
        }
    }

    private void initView() {
        mGdPlusgame.setAdapter(new MyAdapt(mResult));
        mTvTitle.setText("添加游戏");
    }


    class MyAdapt extends MyBaseAdapter {

        MyAdapt(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_gamelist_gradview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            GameMessage.ResultBean resultBean = (GameMessage.ResultBean) mDataSource.get(position);
            String thumb = resultBean.getThumb();
            String name = resultBean.getName();
            if (!TextUtils.isEmpty(thumb)) {
                String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), thumb, MyUtils.Dp2Px(getApplicationContext(),
                        100), MyUtils.Dp2Px(getApplicationContext(), 100));
                Picasso.with(getApplicationContext()).load(loadImageUrl).into(viewHolder.mIvGameimage);
            }
            viewHolder.mTvGameName.setText(name);
            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.iv_gameimage)
        ImageView mIvGameimage;
        @Bind(R.id.tv_game_name)
        TextView mTvGameName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @OnClick({R.id.iv_back, R.id.save_personal_info})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.save_personal_info:
                getGameList();
                finish();
                break;
        }

    }

    private void getGameList() {
        for (Map.Entry<GameMessage.ResultBean, String> next : mHashMap.entrySet()) {
            String value = next.getValue();
            if ("1".equals(value)) {
                mChose.add(next.getKey());
            }
        }
        Intent intent = new Intent();
        intent.putExtra("list", (Serializable) mChose);
        setResult(1, intent);
        finish();
    }
}
