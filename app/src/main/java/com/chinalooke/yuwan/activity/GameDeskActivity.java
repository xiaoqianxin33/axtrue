package com.chinalooke.yuwan.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
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
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DialogUtil;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.chinalooke.yuwan.constant.Constant.MIN_CLICK_DELAY_TIME;
import static com.chinalooke.yuwan.constant.Constant.lastClickTime;

/**
 * 游戏桌详情
 */
public class GameDeskActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_chat)
    TextView mTvChat;
    @Bind(R.id.game_name)
    TextView mTvGameName;
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
    @Bind(R.id.rl_people)
    RelativeLayout mRlPeople;
    @Bind(R.id.rl_rule)
    RelativeLayout mRlRule;
    @Bind(R.id.tv_fight_number)
    TextView mTvFightNumber;

    private List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> mLeftBeen = new ArrayList<>();
    private List<GameDeskDetails.ResultBean.PlayersBean.RightBean> mRight = new ArrayList<>();
    private String mWiner;
    private RequestQueue mQueue;
    private int mWidthPixels;
    private LoginUser.ResultBean user;
    private Toast mToast;
    private String mGameDeskId;
    private ProgressDialog mProgressDialog;
    private String getGameDeskWithId = Constant.mainUri + "getGameDeskWithId&gameDeskId=";
    private GameDeskDetails mGameDeskDetails;
    //记录游戏桌状态变量 0-迎战中 1-进行中 2-已结束
    private int mStatus;
    private boolean isJoin = false;

    private Handler mHandler = new Handler();
    private int mLeftSize;
    private int mRightSize;
    private int mTotalPeople;


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
        mGameDeskDetails = (GameDeskDetails) getIntent().getSerializableExtra("gameDeskDetails");
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshUI();
                mHandler.postDelayed(this, 5000);
            }
        }, 0);
    }

    private void initView() {
        isJoin = false;
        GameDeskDetails.ResultBean result = mGameDeskDetails.getResult();
        mLeftBeen.clear();
        mRight.clear();
        String peopleNumber = result.getPeopleNumber();
        mTotalPeople = 0;
        if (!TextUtils.isEmpty(peopleNumber)) {
            mTotalPeople = Integer.parseInt(peopleNumber) / 2;
        }
        mTvFightNumber.setText(mTotalPeople + "VS" + mTotalPeople);
        GameDeskDetails.ResultBean.PlayersBean players = result.getPlayers();
        if (players != null) {
            List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> left = players.getLeft();
            if (left != null) {
                mLeftBeen = players.getLeft();
                mLeftSize = left.size();
            }

            mPersonYuezhan.setText(mLeftBeen.size() + "/" + mTotalPeople);
            List<GameDeskDetails.ResultBean.PlayersBean.RightBean> right = players.getRight();
            if (right != null) {
                mRight = players.getRight();
                mRightSize = right.size();
            }
            mPersonYingzhan.setText(mRight.size() + "/" + mTotalPeople);
        } else {
            mPersonYuezhan.setText("0/" + mTotalPeople);
            mPersonYingzhan.setText("0/" + mTotalPeople);
        }
        String userId = user.getUserId();
        for (GameDeskDetails.ResultBean.PlayersBean.LeftBean leftBean : mLeftBeen) {
            if (userId.equals(leftBean.getUserId())) {
                isJoin = true;
                break;
            }
        }
        for (GameDeskDetails.ResultBean.PlayersBean.RightBean rightBean : mRight) {
            if (userId.equals(rightBean.getUserId())) {
                isJoin = true;
                break;
            }
        }
        mTvChat.setEnabled(isJoin);
        String status = result.getStatus();
        if (!TextUtils.isEmpty(status)) {
            switch (status) {
                case "pedding":
                    mStatus = 0;
                    if (isJoin) {
                        mTvOk.setText("退出战场");
                    } else {
                        mTvOk.setText("我要参战");
                    }
                    mTvStatus.setText("迎战中");
                    mTvStatus.setBackgroundResource(R.mipmap.red_round_background);
                    break;
                case "doing":
                    mStatus = 1;
                    mTvStatus.setText("进行中");
                    mTvStatus.setBackgroundResource(R.mipmap.green_round_background);
                    mTvOk.setText("确认交战结果");
                    break;
                case "done":
                    mStatus = 2;
                    mTvStatus.setText("已结束");
                    mTvStatus.setBackgroundResource(R.mipmap.orange_round_background);
                    mTvOk.setVisibility(View.GONE);
                    mRlPeople.setVisibility(View.GONE);
                    mRlRule.setVisibility(View.GONE);
                    mTvChat.setEnabled(false);
                    break;
            }
        }

        String ownerName = getIntent().getStringExtra("ownerName");
        if (!TextUtils.isEmpty(ownerName)) {
            if ("官方".equals(ownerName))
                mOwnerType.setText(ownerName);
            else
                mOwnerType.setText("个人");
        }

        String bgImage = result.getBgImage();
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
        String gameName = result.getGameName();
        if (!TextUtils.isEmpty(gameName)) {
            mTvGameName.setText(gameName);
        }


        mWiner = result.getWiner();
        //gridView 添加 adapter
        MyLeftAdapter myLeftAdapter = new MyLeftAdapter();
        MyRightAdapter myRightAdapter = new MyRightAdapter();
        mGdYuezhan.setAdapter(myLeftAdapter);
        mGdYingzhan.setAdapter(myRightAdapter);
    }


    private void initData() {
        mGameDeskId = getIntent().getStringExtra("gameDeskId");
    }

    @OnClick({R.id.tv_chat, R.id.tv_ok})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.tv_chat:
                    mToast.setText("啊啊啊啊");
                    mToast.show();
                    break;
                case R.id.tv_ok:
                    switch (mStatus) {
                        case 0:
                            if (isJoin) {
                                showQuitDialog();
                            } else {
                                showJoinDialog();
                            }
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                    }
                    break;
            }
        }
    }

    //弹出是否确认退出dialog
    private void showQuitDialog() {
        MyUtils.showNorDialog(GameDeskActivity.this, "提示", "确定要退出战场么？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                quitGameDesk();
            }
        });
    }

    //退出游戏桌
    private void quitGameDesk() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            mProgressDialog = DialogUtil.initDialog("", GameDeskActivity.this);
            mProgressDialog.show();
            mTvOk.setText("我要参战");
            exitDesk();
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }
    }

    //加入游戏桌
    private void joinGameDesk(int i) {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            mProgressDialog = MyUtils.initDialog("加入中...", GameDeskActivity.this);
            mProgressDialog.show();
            mTvOk.setText("退出战场");
            sendInternet(i);
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }

    }


    class MyLeftAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLeftSize >= mTotalPeople ? mLeftSize : mLeftBeen.size() + 1;
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

    class ViewHolder {
        @Bind(R.id.roundedImageView)
        RoundedImageView mView;
        @Bind(R.id.iv_crown)
        ImageView mIvCrown;
        @Bind(R.id.tv_name)
        TextView mTvName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class MyRightAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mRightSize >= mTotalPeople ? mRightSize : mRight.size() + 1;
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
            viewHolder.mView.setImageResource(R.mipmap.vacant);
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
            viewHolder.mView.setImageResource(R.mipmap.vacant);
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
                            refreshUI();
                        } else {
                            mTvOk.setText("退出战场");
                            String msg = jsonObject.getString("Msg");
                            mToast.setText("退出失败，" + msg);
                            mToast.show();
                        }
                    } else {
                        mTvOk.setText("退出战场");
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
                mTvOk.setText("退出战场");
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
    private void sendInternet(int s) {
        String netBarId = getIntent().getStringExtra("netBarId");
        String uri;
        if (TextUtils.isEmpty(netBarId)) {
            uri = Constant.mainUri + "takePartInGameDesk&userId=" + user.getUserId()
                    + "&gameDeskId=" + mGameDeskId + "&role=" + s;
        } else {
            uri = Constant.mainUri + "takePartInGameDesk&userId=" + user.getUserId()
                    + "&gameDeskId=" + mGameDeskId + "&role=" + s + "&netBarId=" + netBarId;
        }
        StringRequest stringRequest = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                if (AnalysisJSON.analysisJson(response)) {
                    showJoinSucceedDialog();
                    Gson gson = new Gson();
                    Type type = new TypeToken<GameDeskDetails>() {
                    }.getType();
                    GameDeskDetails o = gson.fromJson(response, type);
                    if (o != null)
                        mGameDeskDetails = o;
                    initView();
                } else {
                    mTvOk.setText("我要参战");
                    refreshUI();
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
                refreshUI();
                mTvOk.setText("我要参战");
                mProgressDialog.dismiss();
                mToast.setText("加入失败，请重试。");
                mToast.show();
            }
        });
        mQueue.add(stringRequest);
    }


    //弹出确定加入dialog
    private void showJoinDialog() {
        final Dialog dialog = new Dialog(GameDeskActivity.this, R.style.Dialog);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_join_desk, null);
        TextView tvCancel = (TextView) inflate.findViewById(R.id.tv_cancel);
        TextView tvLeft = (TextView) inflate.findViewById(R.id.tv_left);
        TextView tvRight = (TextView) inflate.findViewById(R.id.tv_right);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                joinGameDesk(1);

            }
        });
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                joinGameDesk(2);

            }
        });
        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        assert dialogWindow != null;
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    private void showJoinSucceedDialog() {
        final Dialog dialog = new Dialog(GameDeskActivity.this, R.style.Dialog);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_desk_succeed, null);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(inflate);
        dialog.show();
    }

    private void refreshUI() {
        StringRequest stringRequest = new StringRequest(getGameDeskWithId + mGameDeskId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (AnalysisJSON.analysisJson(response)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<GameDeskDetails>() {
                            }.getType();
                            GameDeskDetails o = gson.fromJson(response, type);
                            if (o != null)
                                mGameDeskDetails = o;
                            initView();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mQueue.add(stringRequest);
    }
}
