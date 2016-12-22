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
import com.chinalooke.yuwan.bean.PushMessage;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.db.ExchangeHelper;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.j256.ormlite.dao.Dao;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private List<GameDeskDetails.ResultBean> mResultBeen;
    private RequestQueue mQueue;
    private Toast mToast;
    private ProgressDialog mProgressDialog;
    private LoginUser.ResultBean mUser;
    private Dao<GameDeskDetails.ResultBean, Integer> mGameDao;
    private MyAdapter mMyAdapter;
    private List<PushMessage> mPushMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        initView();
        initData();
    }

    private void initView() {
        mTvTitle.setText("我的消息");
        mMyAdapter = new MyAdapter(mPushMessages);
        mListView.setAdapter(mMyAdapter);
    }

    private void initData() {
        ExchangeHelper helper = ExchangeHelper.getHelper(getApplicationContext());
        try {
            Dao<PushMessage, Integer> pushDao = helper.getPushDao();
            mPushMessages.clear();
            mPushMessages.addAll(pushDao.queryForAll());
            mMyAdapter.notifyDataSetChanged();
            pushDao.closeLastIterator();
            Log.e("TAG", mPushMessages.size() + "");
        } catch (SQLException | IOException e) {
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
            final PushMessage pushMessage = mPushMessages.get(position);
            String date = pushMessage.getDate();
            if (!TextUtils.isEmpty(date))
                viewHolder.mTvTime.setText(date);
            String type = pushMessage.getType();
            switch (type) {
                case "userInfo":
                    String temp = pushMessage.getTemp();
                    String content = pushMessage.getContent();
                    if (!TextUtils.isEmpty(content))
                        viewHolder.mTvMessage.setText(content);
                    final String[] split = temp.split(",");
                    if (!TextUtils.isEmpty(split[1]))
                        Picasso.with(getApplicationContext()).load(split[1]).into(viewHolder.mRoundedImageView);
                    viewHolder.mBtnReJudge.setText("拒绝");
                    viewHolder.mBtnOk.setText("同意");
                    boolean done = pushMessage.isDone();
                    viewHolder.mBtnOk.setSelected(done);
                    viewHolder.mBtnOk.setEnabled(!done);
                    viewHolder.mBtnReJudge.setVisibility(done ? View.GONE : View.VISIBLE);
                    viewHolder.mBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addFriendsClick(split[0], pushMessage, 0);
                        }
                    });
                    viewHolder.mBtnReJudge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addFriendsClick(split[0], pushMessage, 1);
                        }
                    });

                case "joindeskGameDesk":
                    break;
                case "resultGameDesk":
                    break;
                case "gameDesk":
                    break;
                case "netbarGameDesk":
                    break;
            }

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tv_time)
            TextView mTvTime;
            @Bind(R.id.roundedImageView)
            RoundedImageView mRoundedImageView;
            @Bind(R.id.tv_message)
            TextView mTvMessage;
            @Bind(R.id.btn_ok)
            Button mBtnOk;
            @Bind(R.id.btn_reJudge)
            Button mBtnReJudge;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    private void addFriendsClick(String str, final PushMessage pushMessage, int i) {
        mProgressDialog.show();
        if (!TextUtils.isEmpty(str)) {
            String url = Constant.HOST + "agreeFriend&userId=" + mUser.getUserId() + "&friendId=" + str + "&agree=" + i + "&disagreeMsg=";
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressDialog.dismiss();
                    if (AnalysisJSON.analysisJson(response)) {
                        mToast.setText("好友添加成功！");
                        mToast.show();
                        pushMessage.setDone(true);
                        ExchangeHelper helper = ExchangeHelper.getHelper(getApplicationContext());
                        try {
                            Dao<PushMessage, Integer> pushDao = helper.getPushDao();
                            pushDao.update(pushMessage);
                            helper.close();
                            initData();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        MyUtils.showMsg(mToast, response);
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


}
