package com.chinalooke.yuwan.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SendCallback;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.GameDesk;
import com.chinalooke.yuwan.bean.GameDeskDetails;
import com.chinalooke.yuwan.bean.LevelList;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.UserBalance;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DialogUtil;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.chinalooke.yuwan.view.HorizontalListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

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
    HorizontalListView mGdYuezhan;
    @Bind(R.id.gd_yingzhan)
    HorizontalListView mGdYingzhan;
    @Bind(R.id.rl_people)
    RelativeLayout mRlPeople;
    @Bind(R.id.rl_rule)
    RelativeLayout mRlRule;
    @Bind(R.id.tv_fight_number)
    TextView mTvFightNumber;
    @Bind(R.id.tv_pay)
    TextView mTvPay;
    @Bind(R.id.tv_score)
    TextView mTvScore;
    @Bind(R.id.tv_rule)
    TextView mTvRule;
    @Bind(R.id.iv_arrow)
    ImageView mIvArrow;
    @Bind(R.id.tv_exit)
    TextView mTvExit;

    private List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> mLeftBeen = new ArrayList<>();
    private List<GameDeskDetails.ResultBean.PlayersBean.RightBean> mRight = new ArrayList<>();
    private String mWiner;
    private RequestQueue mQueue;
    private int mWidthPixels;
    private LoginUser.ResultBean user;
    private Toast mToast;
    private String mGameDeskId;
    private ProgressDialog mProgressDialog;
    private GameDeskDetails mGameDeskDetails;
    //记录游戏桌状态变量 0-迎战中 1-进行中 2-已结束
    private int mStatus;
    private boolean isJoin = false;
    private Handler mHandler = new Handler();
    private int mLeftSize;
    private int mRightSize;
    private int mTotalPeople;
    private GameDesk.ResultBean mGameDesk;
    private String mRoomId;
    private int DESK_TYPE;
    private ProgressDialog mSubmitDialog;
    private int mGroup;
    private boolean isFirst = true;
    private Runnable mRunnable;
    private boolean isOwner = false;
    private Gson mGson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_desk);
        ButterKnife.bind(this);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mGson = new Gson();
        DisplayMetrics displayMetrics = MyUtils.getDisplayMetrics(this);
        mWidthPixels = displayMetrics.widthPixels;
        mToast = YuwanApplication.getToast();
        user = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        initData();
        initEvent();
    }

    private void initEvent() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                refreshUI();
                mHandler.postDelayed(this, 5000);
            }
        };
        mHandler.postDelayed(mRunnable, 0);

        //gridView item 点击监听
        mGdYingzhan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != mRight.size()) {
                    GameDeskDetails.ResultBean.PlayersBean.RightBean rightBean = mRight.get(position);
                    String userId = rightBean.getUserId();
                    Intent intent = new Intent(GameDeskActivity.this, DeskUserInfoActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            }
        });
        mGdYuezhan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != mLeftBeen.size()) {
                    GameDeskDetails.ResultBean.PlayersBean.LeftBean rightBean = mLeftBeen.get(position);
                    String userId = rightBean.getUserId();
                    Intent intent = new Intent(GameDeskActivity.this, DeskUserInfoActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        if (mHandler != null && mRunnable != null)
            mHandler.removeCallbacks(mRunnable);
        super.onDestroy();

    }

    private void initView() {
        isJoin = false;
        GameDeskDetails.ResultBean result = mGameDeskDetails.getResult();
        mRoomId = result.getRoomId();
        mLeftBeen.clear();
        mRight.clear();
        String peopleNumber = result.getPeopleNumber();
        mTotalPeople = 0;


        if (!TextUtils.isEmpty(peopleNumber)) {
            mTotalPeople = Integer.parseInt(peopleNumber) / 2;
        }
        if (mTvFightNumber != null)
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
                mGroup = 1;
                break;
            }
        }

        String details = result.getDetails();
        if (!TextUtils.isEmpty(details))
            mTvRule.setText(details);
        for (GameDeskDetails.ResultBean.PlayersBean.RightBean rightBean : mRight) {
            if (userId.equals(rightBean.getUserId())) {
                isJoin = true;
                mGroup = 2;
                break;
            }
        }


        String mOwnerName = mGameDesk.getOwnerName();
        int DESK_TYPE_PERSONAL = 1;
        if (!TextUtils.isEmpty(mOwnerName)) {
            if ("官方".equals(mOwnerName)) {
                DESK_TYPE = 0;
                mOwnerType.setText(mOwnerName);
                mTvPay.setText("奖金");
                String cup = mGameDesk.getCup();
                if (!TextUtils.isEmpty(cup))
                    mTvScore.setText(cup);
            } else {
                DESK_TYPE = DESK_TYPE_PERSONAL;
                mOwnerType.setText("个人");
                mTvPay.setText("参赛费");
                String gamePay = mGameDesk.getGamePay();
                if (!TextUtils.isEmpty(gamePay))
                    mTvScore.setText(gamePay + "雷熊币");

                String ownerId = mGameDesk.getOwnerId();
                if (!TextUtils.isEmpty(ownerId)) {
                    if (ownerId.equals(user.getUserId())) {
                        mTvExit.setVisibility(View.VISIBLE);
                        isOwner = true;
                    } else {
                        mTvExit.setVisibility(View.GONE);
                        isOwner = false;
                    }

                }
            }
        }

        mTvChat.setEnabled(isJoin);
        String status = result.getStatus();
        if (!TextUtils.isEmpty(status)) {
            switch (status) {
                case "pedding":
                    mStatus = 0;
                    if (isOwner) {
                        mTvOk.setText("开战");
                    } else {
                        if (isJoin) {
                            mTvOk.setText("退出战场");
                        } else {
                            mTvOk.setText("我要参战");
                        }
                        mTvStatus.setText("迎战中");
                    }
                    mTvStatus.setBackgroundResource(R.mipmap.red_round_background);
                    break;
                case "doing":
                    mStatus = 1;
                    mTvExit.setVisibility(View.GONE);
                    mTvStatus.setText("进行中");
                    mTvStatus.setBackgroundResource(R.mipmap.green_round_background);
                    if (isJoin && DESK_TYPE == DESK_TYPE_PERSONAL) {
                        mTvOk.setVisibility(View.VISIBLE);
                        mTvOk.setText("确认交战结果");
                        setResult();
                    } else {
                        mTvOk.setVisibility(View.GONE);
                    }
                    break;
                case "done":
                    mStatus = 2;
                    mTvStatus.setText("已结束");
                    mTvStatus.setBackgroundResource(R.mipmap.orange_round_background);
                    mTvOk.setVisibility(View.GONE);
                    mRlPeople.setVisibility(View.GONE);
                    mRlRule.setVisibility(View.GONE);
                    mTvChat.setEnabled(false);
                    mIvArrow.setVisibility(View.GONE);
                    mTvExit.setVisibility(View.GONE);
                    break;
            }
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
        mGameDesk = (GameDesk.ResultBean) getIntent().getSerializableExtra("gameDesk");
        mGameDeskId = mGameDesk.getGameDeskId();

        if (!TextUtils.isEmpty(mGameDeskId)) {
            getGameDeskWithId(mGameDeskId);
        }

        String gameImage = mGameDesk.getGameImage();

        if (!TextUtils.isEmpty(gameImage)) {
            ImageRequest request = new ImageRequest(gameImage, new Response.Listener<Bitmap>() {
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

    }

    private void setResult() {
        boolean isReceiver = getIntent().getBooleanExtra("isReceiver", false);
        if (isReceiver) {
            MyUtils.showCustomDialog(GameDeskActivity.this, "提示", "确认此战场您的结果为输吗？"
                    , "不同意，裁判仲裁", "确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (NetUtil.is_Network_Available(getApplicationContext())) {
                                loserConfirm();
                                mProgressDialog.setMessage("提交中");
                                mProgressDialog.show();
                            } else {
                                mToast.setText("网络未连接，请检查网络");
                                mToast.show();
                            }
                        }
                    });
        }
    }

    //输家确定输
    private void loserConfirm() {
        String uri = Constant.HOST + "loserConfirm&userId=" + user.getUserId() + "&gameDeskId=" + mGameDeskId;
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                if (AnalysisJSON.analysisJson(response)) {
                    mToast.setText("提交成功");
                    mToast.show();
                    mTvOk.setVisibility(View.GONE);
                } else {
                    mToast.setText("服务器抽风了，请稍后重试");
                    mToast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("提交失败，请重试");
                mToast.show();
            }
        });
        mQueue.add(request);
    }

    @OnClick({R.id.tv_chat, R.id.tv_ok, R.id.iv_back, R.id.tv_exit})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.tv_exit:
                    closeGameDesk();
                    break;
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.tv_chat:
                    if (NetUtil.is_Network_Available(getApplicationContext())) {
                        if (TextUtils.isEmpty(mRoomId)) {
                            createRoom();
                        } else {
                            intentToRoom();
                        }
                    } else {
                        mToast.setText("网络不可用，无法连接聊天室");
                        mToast.show();
                    }
                    break;
                case R.id.tv_ok:
                    switch (mStatus) {
                        case 0:
                            if (isOwner) {
                                startGame();
                            } else {
                                if (isJoin) {
                                    showQuitDialog();
                                } else {
                                    showJoinDialog();
                                }
                            }
                            break;
                        case 1:
                            MyUtils.showNorDialog(GameDeskActivity.this, "提示", "确定提交您为赢家吗？"
                                    , new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            submitWinner();
                                        }
                                    });
                            break;
                    }
                    break;
            }
        }
    }

    //跳转聊天室
    private void intentToRoom() {
        Intent intent = new Intent(GameDeskActivity.this, EaseGroupChatActivity.class);
        intent.putExtra("groupId", mRoomId);
        startActivity(intent);
    }

    //创建聊天室
    private void createRoom() {
        EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
        option.maxUsers = 200;
        option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
        try {
            EMGroup group = EMClient.getInstance().groupManager().createGroup(mGameDeskDetails.getResult().getGameName(), "对战群组", new String[0], null, option);
            mRoomId = group.getGroupId();
            intentToRoom();
            updateRoomId(mRoomId);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新游戏桌聊天室id
     *
     * @param roomId 聊天室id
     */
    private void updateRoomId(String roomId) {
        String url = Constant.HOST + "updateRoomId&gameDeskId=" + mGameDeskId + "&roomId=" + roomId;
        StringRequest request = new StringRequest(url, null, null);
        mQueue.add(request);
    }

    //房主关闭游戏桌
    private void closeGameDesk() {
        mProgressDialog.setMessage("提交中");
        mProgressDialog.show();
        String url = Constant.HOST + "closeGameDesk&gameDeskId=" + mGameDesk.getGameDeskId();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            MyUtils.showDialog(GameDeskActivity.this, "提示", "关闭成功！", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
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
    }

    //房主开始游戏
    private void startGame() {
        mProgressDialog.setMessage("提交中");
        mProgressDialog.show();
        String url = Constant.HOST + "startGame&gameDeskId=" + mGameDesk.getGameDeskId() + "&userId=" + user.getUserId();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            MyUtils.showDialog(GameDeskActivity.this, "提示", "开战成功！", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mTvOk.setVisibility(View.GONE);
                                }
                            });
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
    }

    //用户提交自己为赢家
    private void submitWinner() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            mSubmitDialog = MyUtils.initDialog("提交结果中", this);
            mSubmitDialog.show();
            String uri = Constant.HOST + "JudgeWinerForUser&userId=" + user.getUserId() +
                    "&gameDeskId=" + mGameDeskId;
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mSubmitDialog.dismiss();
                    if (AnalysisJSON.analysisJson(response)) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean result = jsonObject.getBoolean("Result");
                            if (result) {
                                pushToLose();
                                mTvOk.setText("请等待对方确认");
                                mTvOk.setEnabled(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            mToast.setText("提交失败，" + msg);
                            mToast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSubmitDialog.dismiss();
                    mToast.setText("提交失败，请稍后尝试");
                    mToast.show();
                }
            });
            mQueue.add(request);

        } else {
            mToast.setText("网络未连接，请稍后尝试");
            mToast.show();
        }
    }

    //推送消息给输家
    private void pushToLose() {
        String pushUserId = null;
        switch (mGroup) {
            case 1:
                pushUserId = mRight.get(0).getUserId();
                break;
            case 2:
                pushUserId = mLeftBeen.get(0).getUserId();
                break;
        }
        AVQuery<AVInstallation> pushQuery = AVInstallation.getQuery();
        pushQuery.whereEqualTo("channels", pushUserId + "game_result");
        AVPush push = new AVPush();
        JSONObject jsonObject = new JSONObject();
        mGameDeskDetails.getResult().setAgree(false);
        mGameDeskDetails.getResult().setDeskId(mGameDeskId);
        String gameDeskDetails = mGson.toJson(mGameDeskDetails);
        try {
            jsonObject.put("title", "雷熊");
            jsonObject.put("alert", "您有战场结果出炉了，请确认输赢");
            jsonObject.put("gameDeskDetails", gameDeskDetails);
            push.setData(jsonObject);
            push.setQuery(pushQuery);
            push.setPushToAndroid(true);
            push.setPushToIOS(true);
            push.sendInBackground(new SendCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {

                    } else {
                        pushToLose();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //加入环信群组
    private void joinEaseGroup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mRoomId != null)
                        EMClient.getInstance().groupManager().joinGroup(mRoomId);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //退出环信群组
    private void quitEaseGroup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mRoomId != null)
                        EMClient.getInstance().groupManager().leaveGroup(mRoomId);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();


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
            getUserBalance(i);
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }

    }

    //查询余额
    private void getUserBalance(final int i) {
        String url = Constant.HOST + "getUserBalance&userId=" + user.getUserId();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AnalysisJSON.analysisJson(response)) {
                    UserBalance userBalance = mGson.fromJson(response, UserBalance.class);
                    String score = userBalance.getResult().getScore();
                    checkLevel(score, i);
                } else {
                    mProgressDialog.dismiss();
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

    //检查是否符合等级要求
    private void checkLevel(String score, int i) {
        String playerLevel = mGameDeskDetails.getResult().getPlayerLevel();
        String[] split = playerLevel.split(",");
        String min = split[0];
        String max = split[1];
        String least = null;
        String max1 = null;
        String level = PreferenceUtils.getPrefString(getApplicationContext(), "level", "");
        if (!TextUtils.isEmpty(level)) {
            LevelList levelList = mGson.fromJson(level, LevelList.class);
            List<LevelList.ResultBean> result = levelList.getResult();
            for (LevelList.ResultBean resultBean : result) {
                String levelId = resultBean.getLevelId();
                if (min.equals(levelId)) {
                    least = resultBean.getLeast();
                } else if (max.equals(levelId)) {
                    max1 = resultBean.getMax();
                }
            }
            if (Integer.parseInt(score) < Integer.parseInt(least) && Integer.parseInt(score) > Integer.parseInt(max1)) {
                mProgressDialog.dismiss();
                mToast.setText("不满足该战场参赛范围，换个战场试试！");
                mToast.show();
            } else {
                sendInternet(i);
            }
        } else {
            mProgressDialog.dismiss();
            mToast.setText("连接失败，请稍后重试");
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
                AutoUtils.autoSize(convertView);
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
                AutoUtils.autoSize(convertView);
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
            viewHolder.mView.setImageResource(R.mipmap.vacant_left);
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
            viewHolder.mView.setImageResource(R.mipmap.vacant_right);
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
                            quitEaseGroup();
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
        String netBarId = mGameDesk.getNetBarId();
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
                    Type type = new TypeToken<GameDeskDetails>() {
                    }.getType();
                    GameDeskDetails o = mGson.fromJson(response, type);
                    if (o != null)
                        mGameDeskDetails = o;
                    initView();
                    joinEaseGroup();
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
        if (mLeftSize >= mTotalPeople)
            tvLeft.setVisibility(View.GONE);
        else
            tvLeft.setVisibility(View.VISIBLE);
        if (mRightSize >= mTotalPeople)
            tvRight.setVisibility(View.GONE);
        else
            tvRight.setVisibility(View.VISIBLE);

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
        MyUtils.showCustomDialog(this, "提示", "您已发起挑战\r系统将自动扣除相应金额", null, "确定", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void refreshUI() {
        getGameDeskWithId(mGameDeskId);
    }

    //按照游戏桌id取得游戏桌详情
    private void getGameDeskWithId(final String gameDeskId) {

        StringRequest stringRequest = new StringRequest(Constant.HOST + "getGameDeskWithId&gameDeskId=" + gameDeskId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (AnalysisJSON.analysisJson(response)) {
                            Type type = new TypeToken<GameDeskDetails>() {
                            }.getType();
                            GameDeskDetails gameDesk = mGson.fromJson(response, type);
                            if (gameDesk != null) {
                                mGameDeskDetails = gameDesk;
                                initView();
                                isFirst = false;
                            }
                        } else {
                            if (isFirst) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    mToast.setText(jsonObject.getString("Msg"));
                                    mToast.show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
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
