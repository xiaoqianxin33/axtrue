package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
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
import com.chinalooke.yuwan.view.RoundImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;

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
public class GameDeskActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_game_name)
    TextView mTvGameName;
    @Bind(R.id.tv_chat)
    TextView mTvChat;
    @Bind(R.id.game_name)
    TextView mGameName;
    @Bind(R.id.owner_type)
    TextView mOwnerType;
    @Bind(R.id.status)
    TextView mTvStatus;
    @Bind(R.id.person_yuezhan)
    TextView mPersonYuezhan;
    @Bind(R.id.person_yingzhan)
    TextView mPersonYingzhan;
    @Bind(R.id.tv_ok)
    TextView mTvOk;
    @Bind(R.id.rl_image)
    RelativeLayout mRlImage;
    @Bind(R.id.gd_yuezhan)
    GridView mGdYuezhan;
    @Bind(R.id.gd_yingzhan)
    GridView mGdYingzhan;

    private GameDeskDetails.ResultBean mResult;

    private List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> mLeftBeen = new ArrayList<>();
    private List<GameDeskDetails.ResultBean.PlayersBean.RightBean> mRight = new ArrayList<>();
    private GameDeskDetails.ResultBean.PlayersBean.RightBean mRightBean;
    private GameDeskDetails.ResultBean.PlayersBean.LeftBean mLeftBean;
    private int mPeopleNumer;
    private int mLeftSize;
    private int mRightSize;
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
    private int mStatus;
    private boolean isJoin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_desk);
        ButterKnife.bind(this);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        DisplayMetrics displayMetrics = MyUtils.getDisplayMetrics(this);
        mWidthPixels = displayMetrics.widthPixels;
        mToast = YuwanApplication.getToast();
        user = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        mGameDeskDetails = getIntent().getParcelableExtra("gameDeskDetails");
        initData();
        initView();
    }

    private void initView() {
        mTvChat.setEnabled(isJoin);
        mResult = mGameDeskDetails.getResult();
        String status = mResult.getStatus();
        switch (status) {
            case "pedding":
                mTvStatus.setText("迎战中");
                mTvOk.setText("我要参战");
                mTvStatus.setBackgroundResource(R.mipmap.red_round_background);
                break;
            case "doing":
                mTvStatus.setText("进行中");
                mTvStatus.setBackgroundResource(R.mipmap.green_round_background);
                mTvOk.setText("确认交战结果");
                break;
            case "done":
                mTvStatus.setText("已结束");
                mTvStatus.setBackgroundResource(R.mipmap.orange_round_background);
                mTvOk.setVisibility(View.GONE);
                break;
        }

        String ownerName = getIntent().getStringExtra("ownerName");
        if (!TextUtils.isEmpty(ownerName))
            mOwnerType.setText(ownerName);

        String bgImage = mResult.getBgImage();
        if (!TextUtils.isEmpty(bgImage)) {
            ImageRequest request = new ImageRequest(bgImage, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    if (response != null) {
                        mRlImage.setBackground(new BitmapDrawable(response));
                    }
                }
            }, mWidthPixels, 390, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            mQueue.add(request);
        }
        String gameName = mResult.getGameName();
        if (!TextUtils.isEmpty(gameName)) {
            mTvGameName.setText(gameName);
        }

        String peopleNumber = mResult.getPeopleNumber();
        int totalPeople = Integer.parseInt(peopleNumber) / 2;
        GameDeskDetails.ResultBean.PlayersBean players = mResult.getPlayers();
        if (players != null) {
            mLeftBeen = players.getLeft();
            mPersonYuezhan.setText(mLeftBeen.size() + "/" + totalPeople);
            mRight = players.getRight();
            mPersonYingzhan.setText(mRight.size() + "/" + totalPeople);
        } else {
            mPersonYuezhan.setText("0/" + totalPeople);
            mPersonYingzhan.setText("0/" + totalPeople);
        }
        mWiner = mResult.getWiner();


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

    @OnClick({R.id.take, R.id.tv_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take:
                break;
            case R.id.tv_ok:
                break;
        }
    }

    class MyLeftAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLeftBeen.size() + 1;
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
                convertView = View.inflate(GameDeskActivity.this, R.layout.item_gridview_person, null);
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
            return mRight.size() + 1;
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
                convertView = View.inflate(GameDeskActivity.this, R.layout.item_gridview_person, null);
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
        if (!TextUtils.isEmpty(mWiner) && "left".equals(mWiner)) {
            viewHolder.mIvCrown.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mIvCrown.setVisibility(View.GONE);
        }
        if (position == mLeftBeen.size()) {
            viewHolder.mIvCrown.setImageResource(R.mipmap.vacant);
            viewHolder.mTvName.setText("");
        } else {
            GameDeskDetails.ResultBean.PlayersBean.LeftBean leftBean = mLeftBeen.get(position);
            String headImg = leftBean.getHeadImg();
            if (!TextUtils.isEmpty(headImg))
                Picasso.with(getApplicationContext()).load(headImg).resize(80, 80).centerCrop().into(viewHolder.mView);
            String nickName = leftBean.getNickName();
            if (!TextUtils.isEmpty(nickName)) {
                viewHolder.mTvName.setText(nickName);
            }
        }
    }

    private void setDetailsR(ViewHolder viewHolder, int position) {
        if (!TextUtils.isEmpty(mWiner) && "right".equals(mWiner)) {
            viewHolder.mIvCrown.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mIvCrown.setVisibility(View.GONE);
        }
        if (position == mRight.size()) {
            viewHolder.mIvCrown.setImageResource(R.mipmap.vacant);
            viewHolder.mTvName.setText("");
        } else {
            GameDeskDetails.ResultBean.PlayersBean.RightBean leftBean = mRight.get(position);
            String headImg = leftBean.getHeadImg();
            if (!TextUtils.isEmpty(headImg))
                Picasso.with(getApplicationContext()).load(headImg).resize(80, 80).centerCrop().into(viewHolder.mView);
            String nickName = leftBean.getNickName();
            if (!TextUtils.isEmpty(nickName)) {
                viewHolder.mTvName.setText(nickName);
            }
        }

    }

    //加入游戏桌
    private void showDialog(ViewHolder viewHolder, final String s) {
//        viewHolder.mBtnJoin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (user != null) {
//                    DialogUtil.showSingerDialog(GameDeskActivity.this, "提示", s, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            if (NetUtil.is_Network_Available(getApplicationContext())) {
//                                mProgressDialog = DialogUtil.initDialog("加入中...", GameDeskActivity.this);
//                                mProgressDialog.show();
//                                sendInternet(s);
//                            } else {
//                                mToast.setText("网络不可用，请检查网络连接");
//                                mToast.show();
//                            }
//                        }
//                    }, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                } else {
//                    mToast.setText("请先登录");
//                    mToast.show();
//                }
//            }
//        });
    }

    //退出游戏桌
    private void showDialog2(ViewHolder viewHolder, final String s) {
//        viewHolder.mBtnJoin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (user != null) {
//                    DialogUtil.showSingerDialog(GameDeskActivity.this, "提示", s, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            if (NetUtil.is_Network_Available(getApplicationContext())) {
//                                mProgressDialog = DialogUtil.initDialog("", GameDeskActivity.this);
//                                mProgressDialog.show();
//                                exitDesk();
//                            } else {
//                                mToast.setText("网络不可用，请检查网络连接");
//                                mToast.show();
//                            }
//                        }
//                    }, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                } else {
//                    mToast.setText("请先登录");
//                    mToast.show();
//                }
//            }
//        });
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

        @Bind(R.id.roundedImageView)
        RoundImageView mView;
        @Bind(R.id.iv_crown)
        ImageView mIvCrown;
        @Bind(R.id.tv_name)
        TextView mTvName;


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
