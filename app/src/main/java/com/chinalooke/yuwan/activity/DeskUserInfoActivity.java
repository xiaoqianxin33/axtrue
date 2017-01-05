package com.chinalooke.yuwan.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.DeskUserInfo;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.view.PieChartView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
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

//玩家资料查看
public class DeskUserInfoActivity extends AutoLayoutActivity {

    @Bind(R.id.iv_head)
    RoundedImageView mIvHead;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_age)
    TextView mTvAge;
    @Bind(R.id.rl_sex)
    RelativeLayout mRlSex;
    @Bind(R.id.tv_total_fight)
    TextView mTvTotalFight;
    @Bind(R.id.pieChart)
    PieChartView mChart;
    @Bind(R.id.tv_win)
    TextView mTvWin;
    @Bind(R.id.tv_lose)
    TextView mTvLose;
    @Bind(R.id.tv_run)
    TextView mTvRun;
    @Bind(R.id.tv_phone)
    TextView mTvPhone;
    @Bind(R.id.tv_location)
    TextView mTvLocation;
    @Bind(R.id.add_friends)
    TextView mAddFriends;
    @Bind(R.id.roundedImageView_no)
    RoundedImageView mRoundedImageViewNo;
    private RequestQueue mQueue;
    private Toast mToast;
    private DeskUserInfo mDeskUserInfo;
    private int mType;
    private LoginUser.ResultBean mUser;
    private String mUserId;
    private String mNickName;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_desk_user_info);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        initData();
    }

    private void initData() {
        mUserId = getIntent().getStringExtra("userId");
        if (mUser != null) {
            mAddFriends.setVisibility(View.VISIBLE);
            isUserFriend();
        } else {
            mAddFriends.setVisibility(View.GONE);
        }
        String uri = Constant.HOST + "getUserInfoWithId&userId=" + mUserId;
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AnalysisJSON.analysisJson(response)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<DeskUserInfo>() {
                    }.getType();
                    mDeskUserInfo = gson.fromJson(response, type);
                    if (mDeskUserInfo != null) {
                        initView();
                    }
                } else {
                    mToast.setText("网络不佳，无法获取用户资料");
                    mToast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mToast.setText("网络不佳，无法获取用户资料");
                mToast.show();
            }
        });
        mQueue.add(request);
    }

    //判断是否是好友
    private void isUserFriend() {
        String url = Constant.HOST + "isUserFriend&userId=" + mUser.getUserId() + "&friendId=" + mUserId;
        final StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("Success");
                    if (success) {
                        boolean result = jsonObject.getBoolean("Result");
                        if (result) {
                            mType = 1;
                            mAddFriends.setText("聊天");
                        } else {
                            mType = 0;
                            mAddFriends.setText("添加好友");
                        }
                    } else {
                        mType = 0;
                        mAddFriends.setText("添加好友");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);
    }

    //获得数据，初始化界面数据
    private void initView() {
        DeskUserInfo.ResultBean result = mDeskUserInfo.getResult();
        String headImg = result.getHeadImg();
        if (!TextUtils.isEmpty(headImg)) {
            String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), headImg, 200, 200);
            Picasso.with(getApplicationContext()).load(loadImageUrl).into(mIvHead);
        }
        String sex = result.getSex();
        if (!TextUtils.isEmpty(sex)) {
            switch (sex) {
                case "女":
                    mRlSex.setBackgroundResource(R.mipmap.female_background);
                    break;
                case "男":
                    mRlSex.setBackgroundResource(R.mipmap.male_backgroud);
                    break;
            }
        }
        mNickName = result.getNickName();
        easySet(mNickName, mTvName);
        String age = result.getAge();
        if (!TextUtils.isEmpty(age))
            mTvAge.setText(getString(R.string.age, age));

        mPhone = result.getPhone();

        String address = result.getAddress();
        if (!TextUtils.isEmpty(address)) {
            mTvLocation.setText(address);
        }

        String winCount = result.getWinCount();
        easySet(winCount, mTvWin);
        String loseCount = result.getLoseCount();
        easySet(loseCount, mTvLose);
        String breakCount = result.getBreakCount();
        easySet(breakCount, mTvRun);
        String sumPlayCount = result.getSumPlayCount();
        easySet(sumPlayCount, mTvTotalFight);
        if (!TextUtils.isEmpty(sumPlayCount))
            mRoundedImageViewNo.setVisibility(Integer.parseInt(sumPlayCount) == 0 ? View.VISIBLE : View.GONE);
        String phone = result.getPhone();
        if (!TextUtils.isEmpty(phone)) {
            String head = phone.substring(0, 3);
            String foot = phone.substring(phone.length() - 4);
            String phone1 = head + "***" + foot;
            mTvPhone.setText(phone1);
        }


        if (!TextUtils.isEmpty(winCount) && !TextUtils.isEmpty(loseCount) && !TextUtils.isEmpty(breakCount)) {
            List<PieChartView.PieceDataHolder> pieceDataHolders = new ArrayList<>();
            pieceDataHolders.add(new PieChartView.PieceDataHolder(Integer.parseInt(winCount), 0xFFFEC100, ""));
            pieceDataHolders.add(new PieChartView.PieceDataHolder(Integer.parseInt(loseCount), 0xFF1DAD91, ""));
            pieceDataHolders.add(new PieChartView.PieceDataHolder(Integer.parseInt(breakCount), 0xFF01BBF1, ""));
            mChart.setData(pieceDataHolders);
            mChart.setMarkerLineLength(0);
        }
    }

    private void easySet(String string, TextView textView) {
        if (!TextUtils.isEmpty(string))
            textView.setText(string);
    }

    @OnClick({R.id.iv_back, R.id.add_friends})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.add_friends:
                if (mUser != null) {
                    if (mType == 0) {
                        Intent intent = new Intent(this, SendUpAddFriendActivity.class);
                        intent.putExtra("peopleId", mUserId);
                        startActivity(intent);
                    } else if (mType == 1) {
                        if (!TextUtils.isEmpty(mPhone)) {
                            Intent intent = new Intent(this, EaseChatActivity.class);
                            intent.putExtra("userId", mPhone);
                            intent.putExtra("nickName", mNickName);
                            startActivity(intent);
                        }
                    }
                } else {
                    MyUtils.showCustomDialog(DeskUserInfoActivity.this, "登录提示", "该功能需要登录，现在去登录吗？", "不了", "去登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(DeskUserInfoActivity.this, LoginActivity.class));
                        }
                    });
                }
                break;

        }
    }
}
