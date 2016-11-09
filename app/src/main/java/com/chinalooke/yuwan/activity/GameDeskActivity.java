package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.GameDeskDetails;
import com.chinalooke.yuwan.model.UserInfo;
import com.chinalooke.yuwan.utils.DialogUtil;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.chinalooke.yuwan.view.NoSlidingListView;
import com.chinalooke.yuwan.view.RoundImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 游戏桌详情
 */
public class GameDeskActivity extends AppCompatActivity {

    @Bind(R.id.tv_game_name)
    TextView mTvGameName;
    @Bind(R.id.lv_gamedesk_yuezhan)
    NoSlidingListView mLvGamedeskYuezhan;
    @Bind(R.id.lv_gamedesk_yingzhan)
    NoSlidingListView mLvGamedeskYingzhan;
    @Bind(R.id.iv_bkImage)
    ImageView mIvBkImage;
    @Bind(R.id.btn_desk)
    TextView mBtnDesk;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_pay)
    TextView mTvPay;
    @Bind(R.id.tv_number)
    TextView mTvNumber;
    @Bind(R.id.tv_rule)
    TextView mTvRule;
    @Bind(R.id.iv_cup)
    ImageView mIvCup;
    @Bind(R.id.tv_winner)
    TextView mTvWinner;
    private GameDeskDetails.ResultBean mResult;

    private List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> mLeftBeen = new ArrayList<>();
    private List<GameDeskDetails.ResultBean.PlayersBean.RightBean> mRight = new ArrayList<>();
    private GameDeskDetails.ResultBean.PlayersBean.RightBean mRightBean;
    private GameDeskDetails.ResultBean.PlayersBean.LeftBean mLeftBean;
    private int mPeopleNumer;
    private int mLeftSize;
    private int mRightSize;
    private String mStatus;
    private String mWiner;
    private RequestQueue mQueue;
    private int mWidthPixels;
    private UserInfo user;
    private Toast mToast;
    private String mGameDeskId;
    private ProgressDialog mProgressDialog;
    private MyLeftAdapter mMyLeftAdapter;
    private MyRightAdapter mMyRightAdapter;
    private int mLeftUser = -1;
    private int mRightUser = -1;
    private String getGameDeskWithId = Constant.mainUri + "getGameDeskWithId&gameDeskId=";
    private GameDeskDetails mGameDeskDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_desk);
        ButterKnife.bind(this);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        DisplayMetrics displayMetrics = MyUtils.getDisplayMetrics(this);
        mWidthPixels = displayMetrics.widthPixels;
        mToast = YuwanApplication.getToast();
        user = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        mGameDeskId = getIntent().getStringExtra("gameDeskId");
        mResult = (GameDeskDetails.ResultBean) getIntent().getExtras().getSerializable("mResultBean");
        initData();
        initView();
    }

    private void initView() {
        mStatus = mResult.getStatus();
        switch (mStatus) {
            case "pedding":
                mBtnDesk.setBackground(getResources().getDrawable(R.drawable.button_blue_shape));
                mBtnDesk.setTextColor(getResources().getColor(R.color.btnblue));
                mBtnDesk.setText("应战中");
                break;
            case "doing":
                mBtnDesk.setBackground(getResources().getDrawable(R.drawable.button_compat_shape));
                mBtnDesk.setTextColor(getResources().getColor(R.color.btncompat));
                mBtnDesk.setText("进行中");
                break;
            case "done":
                mTvWinner.setText(mResult.getWiner());
                mIvCup.setVisibility(View.VISIBLE);
                mBtnDesk.setBackground(getResources().getDrawable(R.drawable.shape));
                mBtnDesk.setTextColor(Color.RED);
                mBtnDesk.setText("已结束");
                break;
        }
        mWiner = mResult.getWiner();
        mTvGameName.setText(mResult.getGameName());

        if (!TextUtils.isEmpty(mResult.getBgImage()))
            Picasso.with(getApplicationContext()).load(mResult.getBgImage()).resize(mWidthPixels, MyUtils.Dp2Px(getApplicationContext(), 160))
                    .centerCrop().into(mIvBkImage);
        mTvNumber.setText(mPeopleNumer / 2 + " vs " + mPeopleNumer / 2);
        mTvPay.setText(mResult.getGamePay());
        mTvTime.setText(mResult.getStartTime());
        mTvRule.setText(mResult.getDetails());
        mMyLeftAdapter = new MyLeftAdapter();
        mMyRightAdapter = new MyRightAdapter();
        mLvGamedeskYuezhan.setAdapter(mMyRightAdapter);
        mLvGamedeskYingzhan.setAdapter(mMyLeftAdapter);
    }

    private void initData() {
        if (mResult != null) {
            String peopleNumber = mResult.getPeopleNumber();
            if (!TextUtils.isEmpty(peopleNumber)) {
                mPeopleNumer = Integer.parseInt(peopleNumber);
            }
            List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> left = mResult.getPlayers().getLeft();
            if (left != null) {
                mLeftSize = mResult.getPlayers().getLeft().size();
            } else {
                mLeftSize = 0;
            }
            List<GameDeskDetails.ResultBean.PlayersBean.RightBean> right = mResult.getPlayers().getRight();
            if (right != null) {
                mRightSize = mResult.getPlayers().getRight().size();
            } else {
                mRightSize = 0;
            }
        }

        if (mResult.getPlayers() != null) {
            mLeftBeen = mResult.getPlayers().getLeft();
            mRight = mResult.getPlayers().getRight();
        }


        if (user != null) {
            if (mLeftBeen != null) {
                for (int i = 0; i < mLeftBeen.size(); i++) {
                    GameDeskDetails.ResultBean.PlayersBean.LeftBean leftBean = mLeftBeen.get(i);
                    String userId = leftBean.getUserId();
                    if (user.getUserId().equals(userId)) {
                        mLeftUser = i;
                    }
                }
            }

            if (mRight != null) {
                for (int i = 0; i < mRight.size(); i++) {
                    GameDeskDetails.ResultBean.PlayersBean.RightBean leftBean = mRight.get(i);
                    String userId = leftBean.getUserId();
                    if (user.getUserId().equals(userId)) {
                        mRightUser = i;
                    }
                }
            }
        }

    }

    class MyLeftAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mPeopleNumer / 2 <= mLeftSize) {
                return mPeopleNumer / 2;
            } else {
                return mLeftSize + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(GameDeskActivity.this, R.layout.item_gamedesk_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            setDetails(viewHolder, position);
            return convertView;
        }
    }

    class MyRightAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mPeopleNumer / 2 <= mRightSize) {
                return mPeopleNumer / 2;
            } else {
                return mRightSize + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(GameDeskActivity.this, R.layout.item_gamedesk_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            setDetailsR(viewHolder, position);
            return convertView;
        }
    }

    private void setDetails(ViewHolder viewHolder, int position) {
        Log.e("TAG", mLeftUser + "");

        if (position == 0) {
            viewHolder.mTvYuezhanListview.setText("应战方");
            viewHolder.mTvYuezhanListview.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mTvYuezhanListview.setVisibility(View.INVISIBLE);
        }

        if ("left".equals(mWiner)) {
            viewHolder.mIvCrown.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mIvCrown.setVisibility(View.INVISIBLE);
        }

        if (position < mLeftSize) {
            viewHolder.mBtnJoin.setVisibility(View.INVISIBLE);
            viewHolder.mTvVcant.setVisibility(View.INVISIBLE);
            viewHolder.mEditText.setVisibility(View.VISIBLE);
            viewHolder.mView.setVisibility(View.VISIBLE);
            if (mLeftUser == position) {
                viewHolder.mBtnJoin.setText("退出应战方");
                viewHolder.mEditText.setVisibility(View.VISIBLE);
                viewHolder.mView.setVisibility(View.VISIBLE);
                viewHolder.mBtnJoin.setVisibility(View.VISIBLE);
                viewHolder.mImageView.setVisibility(View.INVISIBLE);
                viewHolder.mImageView2.setVisibility(View.INVISIBLE);
                showDialog2(viewHolder, "确定退出吗？");
            }
            if (mLeftBeen != null) {
                mLeftBean = mLeftBeen.get(position);
                viewHolder.mEditText.setText(mLeftBean.getNickName());
                Picasso.with(getApplicationContext()).load(mLeftBean.getHeadImg()).resize(50, 50).centerCrop().into(viewHolder.mView);
            }
        } else {
            viewHolder.mEditText.setVisibility(View.INVISIBLE);
            viewHolder.mImageView.setVisibility(View.INVISIBLE);
            viewHolder.mView.setVisibility(View.INVISIBLE);
            viewHolder.mImageView2.setVisibility(View.INVISIBLE);
            showDialog(viewHolder, "确定加入应战方吗?");
        }


    }

    private void setDetailsR(ViewHolder viewHolder, int position) {

        Log.e("TAG", mRightUser + "");

        if (position == 0) {
            viewHolder.mTvYuezhanListview.setText("约战方");
            viewHolder.mTvYuezhanListview.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mTvYuezhanListview.setVisibility(View.INVISIBLE);
        }

        if ("right".equals(mWiner)) {

            viewHolder.mIvCrown.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mIvCrown.setVisibility(View.INVISIBLE);
        }

        if (position < mRightSize) {
            viewHolder.mBtnJoin.setVisibility(View.INVISIBLE);
            viewHolder.mTvVcant.setVisibility(View.INVISIBLE);
            mRightBean = mRight.get(position);
            viewHolder.mEditText.setText(mRightBean.getNickName());
            Picasso.with(getApplicationContext()).load(mRightBean.getHeadImg()).resize(50, 50).centerCrop().into(viewHolder.mView);
            if (mRightUser == position) {
                viewHolder.mBtnJoin.setText("退出约战方");
                viewHolder.mBtnJoin.setVisibility(View.VISIBLE);
                viewHolder.mImageView.setVisibility(View.INVISIBLE);
                viewHolder.mImageView2.setVisibility(View.INVISIBLE);
                showDialog2(viewHolder, "确定退出吗？");
            } else {
                showDialog(viewHolder, "确定加入约战方吗?");
            }
        } else {
            viewHolder.mEditText.setVisibility(View.INVISIBLE);
            viewHolder.mImageView.setVisibility(View.INVISIBLE);
            viewHolder.mView.setVisibility(View.INVISIBLE);
            viewHolder.mImageView2.setVisibility(View.INVISIBLE);
            showDialog(viewHolder, "确定加入约战方吗?");
        }


    }

    //加入游戏桌
    private void showDialog(ViewHolder viewHolder, final String s) {
        viewHolder.mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user != null) {
                    DialogUtil.showSingerDialog(GameDeskActivity.this, "提示", s, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (NetUtil.is_Network_Available(getApplicationContext())) {
                                mProgressDialog = DialogUtil.initDialog("加入中...", GameDeskActivity.this);
                                mProgressDialog.show();
                                sendInternet(s);
                            } else {
                                mToast.setText("网络不可用，请检查网络连接");
                                mToast.show();
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    mToast.setText("请先登录");
                    mToast.show();
                }
            }
        });
    }

    //退出游戏桌
    private void showDialog2(ViewHolder viewHolder, final String s) {
        viewHolder.mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    DialogUtil.showSingerDialog(GameDeskActivity.this, "提示", s, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (NetUtil.is_Network_Available(getApplicationContext())) {
                                mProgressDialog = DialogUtil.initDialog("", GameDeskActivity.this);
                                mProgressDialog.show();
                                exitDesk();
                            } else {
                                mToast.setText("网络不可用，请检查网络连接");
                                mToast.show();
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    mToast.setText("请先登录");
                    mToast.show();
                }
            }
        });
    }

    //退出游戏桌
    private void exitDesk() {
        StringRequest stringRequest = new StringRequest(Constant.HOST + "exitGameDesk&userId="
                + user.getUserId() + "&gameDeskId=" + mGameDeskId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("Success");
                    if (success) {
                        boolean result = jsonObject.getBoolean("Result");
                        if (result) {
                            mToast.setText("退出成功！");
                            mToast.show();
                            mLeftUser = -1;
                            mRightUser = -1;
                            refreshUI();
                        } else {
                            String msg = jsonObject.getString("Msg");
                            mToast.setText("退出失败，" + msg);
                            mToast.show();
                        }
                    } else {
                        String msg = jsonObject.getString("Msg");
                        mToast.setText("退出失败，" + msg);
                        mToast.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("退出失败，请稍后重试");
                mToast.show();
            }
        });

        mQueue.add(stringRequest);
    }

    /**
     * 加入对战
     *
     * @param s 判断约战应战方
     */
    private void sendInternet(String s) {
        String role = null;
        if ("确定加入约战方吗?".equals(s))
            role = "2";
        if (("确定加入应战方吗?").equals(s))
            role = "1";

        String netBarId = getIntent().getStringExtra("netBarId");
        String uri;
        if (TextUtils.isEmpty(netBarId)) {
            uri = Constant.mainUri + "takePartInGameDesk&userId=" + user.getUserId()
                    + "&gameDeskId=" + mGameDeskId + "&role=" + role;
        } else {
            uri = Constant.mainUri + "takePartInGameDesk&userId=" + user.getUserId()
                    + "&gameDeskId=" + mGameDeskId + "&role=" + role + "&netbarId=" + netBarId;
        }

        StringRequest stringRequest = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                String substring = response.substring(11, 16);
                if (!"false".equals(substring)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<GameDeskDetails>() {
                    }.getType();
                    mGameDeskDetails = gson.fromJson(response, type);
                    if (!mGameDeskDetails.isSuccess()) {
                        mToast.setText("加入失败!");
                        mToast.show();
                    } else {
                        mToast.setText("加入成功!");
                        mToast.show();
                        mLeftUser = -1;
                        mRightUser = -1;
                        refreshUI();
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.getString("Msg");
                        mToast.setText("加入失败，" + msg);
                        mToast.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("加入失败，请重试。");
                mToast.show();
            }
        });
        mQueue.add(stringRequest);
    }


    static class ViewHolder {
        @Bind(R.id.tv_yuezhan_listview)
        TextView mTvYuezhanListview;
        @Bind(R.id.view)
        RoundImageView mView;
        @Bind(R.id.editText)
        TextView mEditText;
        @Bind(R.id.imageView)
        ImageView mImageView;
        @Bind(R.id.imageView2)
        ImageView mImageView2;
        @Bind(R.id.button_join)
        Button mBtnJoin;
        @Bind(R.id.tv_vacant)
        TextView mTvVcant;
        @Bind(R.id.iv_crown)
        ImageView mIvCrown;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @OnClick(R.id.iv_wirte_back)
    public void onClick() {
        finish();
    }

    private void refreshUI() {
        StringRequest stringRequest = new StringRequest(getGameDeskWithId + mGameDeskId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String substring = response.substring(11, 15);
                        if (substring.equals("true")) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<GameDeskDetails>() {
                            }.getType();
                            mGameDeskDetails = gson.fromJson(response, type);
                            if (!mGameDeskDetails.isSuccess()) {
                                mToast.setText("获取数据失败!");
                                mToast.show();
                            } else {
                                mResult = mGameDeskDetails.getResult();
                                initData();
                                initView();
                            }

                        } else {
                            mToast.setText("获取数据失败!");
                            mToast.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mToast.setText("获取数据失败!");
                mToast.show();
            }
        });
        mQueue.add(stringRequest);
    }
}
