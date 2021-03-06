package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.avos.avoscloud.AVAnalytics;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.PushMessage;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.db.ExchangeHelper;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DateUtils;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    private RequestQueue mQueue;
    private Toast mToast;
    private ProgressDialog mProgressDialog;
    private LoginUser.ResultBean mUser;
    private MyAdapter mMyAdapter;
    private List<PushMessage> mPushMessages = new ArrayList<>();
    private Dao<PushMessage, Integer> mPushDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        ExchangeHelper helper = ExchangeHelper.getHelper(getApplicationContext());
        try {
            mPushDao = helper.getPushDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initView();
        initData();
    }

    private void initView() {
        mTvTitle.setText("我的消息");
        mMyAdapter = new MyAdapter(mPushMessages);
        mListView.setAdapter(mMyAdapter);
        mProgressDialog = MyUtils.initDialog("正在提交", MyMessageActivity.this);
    }

    private void initData() {
        try {
            mPushMessages.clear();
            mPushMessages.addAll(mPushDao.queryForAll());
            Collections.sort(mPushMessages, new Comparator<PushMessage>() {
                @Override
                public int compare(PushMessage lhs, PushMessage rhs) {
                    Date ldate = DateUtils.getDate(lhs.getDate(), "yyyy-MM-dd");
                    Date rdate = DateUtils.getDate(rhs.getDate(), "yyyy-MM-dd");
                    if (ldate != null && rdate != null) {
                        if (ldate.before(rdate))
                            return 1;
                        else
                            return -1;
                    }
                    return 0;
                }
            });
            mMyAdapter.notifyDataSetChanged();
            if (mPushMessages.size() == 0) {
                mTvNone.setText("暂无消息");
                mTvNone.setVisibility(View.VISIBLE);
            } else {
                mTvNone.setVisibility(View.GONE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mPushDao != null)
            try {
                mPushDao.closeLastIterator();
            } catch (IOException e) {
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

            boolean done = pushMessage.isDone();
            viewHolder.mBtnOk.setSelected(done);
            viewHolder.mBtnOk.setEnabled(!done);
            viewHolder.mBtnReJudge.setVisibility(done ? View.GONE : View.VISIBLE);
            String type = pushMessage.getType();
            final String content = pushMessage.getContent();
            if (!TextUtils.isEmpty(content))
                viewHolder.mTvMessage.setText(content);
            final String temp = pushMessage.getTemp();
            final String[] split = temp.split(",");
            String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), split[1], 100, 100);
            Picasso.with(getApplicationContext()).load(loadImageUrl).into(viewHolder.mRoundedImageView);
            switch (type) {
                case "userInfo":
                    viewHolder.mBtnReJudge.setText("拒绝");
                    viewHolder.mBtnOk.setText("同意");
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
                    break;
                case "userGameDesk":
                    viewHolder.mBtnOk.setText("同意");
                    viewHolder.mBtnReJudge.setText("申请重判");
                    viewHolder.mBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loserConfirm(split[0], pushMessage);
                        }
                    });

                    viewHolder.mBtnReJudge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (NetUtil.is_Network_Available(getApplicationContext())) {
                                Intent intent = new Intent(MyMessageActivity.this, ApplyJudgeActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("message", pushMessage);
                                intent.putExtras(bundle);
                                intent.putExtra("id", split[0]);
                                startActivity(intent);
                            } else {
                                mToast.setText("网络不可用，请检查网络连接");
                                mToast.show();
                            }
                        }
                    });
                    break;
                case "gameDesk":
                    viewHolder.mBtnReJudge.setVisibility(View.GONE);
                    viewHolder.mBtnOk.setText("查看");
                    View.OnClickListener gameDeskIdListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pushMessage.setDone(true);
                            try {
                                mPushDao.update(pushMessage);
                                initData();
                                Intent intent = new Intent(MyMessageActivity.this, GameDeskActivity.class);
                                intent.putExtra("gameDeskId", split[0]);
                                startActivity(intent);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    viewHolder.mBtnOk.setOnClickListener(gameDeskIdListener);
                    viewHolder.mTvMessage.setOnClickListener(gameDeskIdListener);
                    break;
                case "netbarGameDesk":
                    viewHolder.mBtnReJudge.setVisibility(View.GONE);
                    viewHolder.mBtnOk.setText("查看");
                    viewHolder.mBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pushMessage.setDone(true);
                            try {
                                mPushDao.update(pushMessage);
                                initData();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(MyMessageActivity.this, JudgeActivity.class);
                            intent.putExtra("gameDeskId", split[0]);
                            intent.putExtra("count", split[2]);
                            startActivity(intent);
                        }
                    });
                    break;
                case "winnerConfirm":
                    viewHolder.mBtnReJudge.setVisibility(View.GONE);
                    viewHolder.mBtnOk.setText("确认");
                    viewHolder.mBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            winnerConfirm(split[0], pushMessage);
                        }
                    });
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

    //玩家确认奖金到位
    private void winnerConfirm(String deskId, final PushMessage pushMessage) {
        mProgressDialog.show();
        String url = Constant.HOST + "winnerConfirm&userId=" + mUser.getUserId() + "&gameDeskId=" + deskId;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("Success");
                    if (success) {
                        boolean result = jsonObject.getBoolean("Result");
                        if (result) {
                            pushMessage.setDone(true);
                            mPushDao.update(pushMessage);
                            initData();
                        } else {
                            MyUtils.showMsg(mToast, response);
                        }
                    } else {
                        MyUtils.showMsg(mToast, response);
                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("服务器抽风了，请稍后重试");
                mToast.show();
            }
        });
        mQueue.add(request);
    }

    //同意添加好友事件
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
                        try {
                            mPushDao.update(pushMessage);
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
    private void loserConfirm(String gameDeskId, final PushMessage pushMessage) {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            mProgressDialog.show();
            String url = Constant.HOST + "loserConfirm&userId=" + mUser.getUserId() + "&gameDeskId=" + gameDeskId;
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressDialog.dismiss();
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("Success");
                            if (success) {
                                pushMessage.setDone(true);
                                mPushDao.update(pushMessage);
                                boolean result = jsonObject.getBoolean("Result");
                                if (result) {
                                    MyUtils.showDialog(MyMessageActivity.this, "提示", "已确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            initData();
                                        }
                                    });
                                }
                            } else {
                                MyUtils.showMsg(mToast, response);
                            }
                        } catch (JSONException | SQLException e) {
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

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }
}
