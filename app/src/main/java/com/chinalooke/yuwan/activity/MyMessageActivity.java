package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.GameDeskDetails;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.db.ExchangeHelper;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.j256.ormlite.dao.Dao;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
    @Bind(R.id.tv_none)
    TextView mTvNone;
    private ExchangeHelper mHelper;
    private List<GameDeskDetails.ResultBean> mResultBeen;
    private RequestQueue mQueue;
    private Toast mToast;
    private ProgressDialog mProgressDialog;
    private LoginUser.ResultBean mUser;
    private Dao<GameDeskDetails.ResultBean, Integer> mGameDao;
    private MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        mHelper = ExchangeHelper.getHelper(getApplicationContext());
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        initView();
        initData();
    }

    private void initView() {
        mTvTitle.setText("我的消息");
    }

    private void initData() {
        try {
//            mGameDao = mHelper.getGameDao();
            String string = getIntent().getExtras().getString("com.avos.avoscloud.Data");
            if (!TextUtils.isEmpty(string)) {
                JSONObject json = new JSONObject(string);
                Log.e("TAG", string);
            }
//            if (json != null) {
//                String gameDeskDetails = json.getString("gameDeskDetails");
//                Gson gson = new Gson();
//                Type type = new TypeToken<GameDeskDetails>() {
//                }.getType();
//                GameDeskDetails gameDeskDetails1 = gson.fromJson(gameDeskDetails, type);
//                GameDeskDetails.ResultBean result = gameDeskDetails1.getResult();
//                if (result != null) {
//                    mGameDao.createOrUpdate(result);
//                }
//            }
//            mResultBeen = mGameDao.queryForAll();
//            if (mResultBeen.size() != 0) {
//                mMyAdapter = new MyAdapter(mResultBeen);
//                mListView.setAdapter(mMyAdapter);
//                mTvNone.setVisibility(View.VISIBLE);
//            } else {
//                mTvNone.setVisibility(View.GONE);
//            }
        } catch (JSONException e) {
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
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(MyMessageActivity.this, R.layout.item_my_message_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final GameDeskDetails.ResultBean resultBean = mResultBeen.get(position);
            String gameName = resultBean.getGameName();
            String netBarName = resultBean.getNetBarName();
            String startTime = resultBean.getStartTime();
            viewHolder.mTvMessage.setText("今天你在" + netBarName + "所参与的" + gameName + "游戏，已失败，对方胜利，确定？");
            viewHolder.mTvTime.setText(startTime);
            viewHolder.mBtnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean selected = viewHolder.mBtnOk.isSelected();
                    if (!selected) {
                        loserConfirm(resultBean);
                    }
                }
            });
            return convertView;
        }

    }

    // 输家确定输
    private void loserConfirm(final GameDeskDetails.ResultBean resultBean) {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            mProgressDialog = MyUtils.initDialog("正在提交", MyMessageActivity.this);
            mProgressDialog.show();
            String url = Constant.HOST + "loserConfirm&userId=" + mUser.getUserId() + "&gameDeskId=" + resultBean.getDeskId();
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressDialog.dismiss();
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("Success");
                            if (success) {
                                boolean result = jsonObject.getBoolean("Result");
                                if (result) {
                                    MyUtils.showDialog(MyMessageActivity.this, "提示", "已确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            upDateDB(resultBean);
                                        }
                                    });
                                }
                            } else {
                                String msg = jsonObject.getString("Msg");
                                mToast.setText(msg);
                                mToast.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressDialog.dismiss();
                    mToast.setText("服务器抽风了，请稍后再试");
                    mToast.show();
                }
            });

            mQueue.add(request);
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }
    }

    //更新数据库
    private void upDateDB(GameDeskDetails.ResultBean resultBean) {
        try {
            mGameDao.update(resultBean);
            mResultBeen.clear();
            mResultBeen.addAll(mGameDao.queryForAll());
            mMyAdapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static class ViewHolder {
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_message)
        TextView mTvMessage;
        @Bind(R.id.btn_ok)
        Button mBtnOk;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
