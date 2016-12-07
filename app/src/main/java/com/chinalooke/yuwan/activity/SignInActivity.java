package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.SignAdapter;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.SignEntity;
import com.chinalooke.yuwan.bean.SignHistory;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.view.MyScrollView;
import com.chinalooke.yuwan.view.NoSlidingListView;
import com.chinalooke.yuwan.view.SignView;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//签到页面
public class SignInActivity extends AutoLayoutActivity {

    @Bind(R.id.iv_back)
    FrameLayout mIvBack;
    @Bind(R.id.iv_arrow_head)
    ImageView mIvArrowHead;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.rl_head)
    RelativeLayout mRlHead;
    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_continuous)
    TextView mTvContinuous;
    @Bind(R.id.btn_sign)
    Button mBtnSign;
    @Bind(R.id.tv_month)
    TextView mTvMonth;
    @Bind(R.id.scrollView)
    MyScrollView mScrollView;
    @Bind(R.id.activity_sign_in)
    RelativeLayout mActivitySignIn;
    @Bind(R.id.signView)
    SignView signView;
    @Bind(R.id.list_view)
    NoSlidingListView mListView;
    private LoginUser.ResultBean mUser;
    private RequestQueue mQueue;
    private SignHistory mSignHistory;
    private Toast mToast;
    private int mDayOfMonthToday;
    private Calendar mCalendarToday;
    private ProgressDialog mProgressDialog;
    private ArrayList<SignEntity> mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        initView();
        initData();
    }

    private void initData() {
        getSignInHistory();
    }

    //查询用户签到记录
    private void getSignInHistory() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String url = Constant.HOST + "getSignInHistory&userId=" + mUser.getUserId();
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        if (AnalysisJSON.analysisJson(response)) {
                            Gson gson = new Gson();
                            mSignHistory = gson.fromJson(response, SignHistory.class);
                            if (mSignHistory != null) {
                                changeSignView();
                            }
                        }
                    }
                }
            }, null);
            mQueue.add(request);
        } else {
            mToast.setText("网络不可用，无法获取签到记录");
            mToast.show();
        }
    }

    private void initView() {
        mRlHead.setBackgroundResource(R.color.transparent);
        mTvTitle.setText("每日签到");
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        mTvMonth.setText(getResources().getStringArray(R.array.month_array)[month] + "签到表");
        mCalendarToday = Calendar.getInstance();
        mDayOfMonthToday = mCalendarToday.get(Calendar.DAY_OF_MONTH);
        initSignView();
        String headImg = mUser.getHeadImg();
        Picasso.with(getApplicationContext()).load(headImg + "?imageView2/1/w/150/h/150").into(mRoundedImageView);
        String nickName = mUser.getNickName();
        mTvName.setText(nickName);
    }

    //初始化日历
    private void initSignView() {
        mDate = new ArrayList<>();
        for (int i = 1; i < 31; i++) {
            SignEntity signEntity = new SignEntity();
            if (i == mDayOfMonthToday) {
                signEntity.setDayType(2);
            } else {
                signEntity.setDayType(3);
            }
            mDate.add(signEntity);
        }
        SignAdapter signAdapter = new SignAdapter(mDate);
        signView.setAdapter(signAdapter);
    }

    //根据数据刷新日历信息
    private void changeSignView() {
        mDate.clear();
        int year = mCalendarToday.get(Calendar.YEAR);
        int month = mCalendarToday.get(Calendar.MONTH);
        if (mSignHistory != null) {
            List<String> result = mSignHistory.getResult();
            List<String> signDay = new ArrayList<>();
            for (String s : result) {
                Date date = DateUtils.getDate(s);
                int year1 = date.getYear();
                if (year == year1) {
                    int month1 = date.getMonth();
                    if (month == month1) {
                        int day = date.getDay();
                        signDay.add(day + "");
                    }
                }
            }

            for (int i = 1; i < 31; i++) {
                SignEntity signEntity = new SignEntity();
                if (signDay.contains(i + "")) {
                    signEntity.setDayType(0);
                } else if (i == mDayOfMonthToday) {
                    if (signDay.contains(mDayOfMonthToday + "")) {
                        mBtnSign.setText("今日已签到");
                        mBtnSign.setEnabled(false);
                        signEntity.setDayType(0);
                    } else {
                        signEntity.setDayType(2);
                    }
                } else {
                    signEntity.setDayType(1);
                }
                mDate.add(signEntity);
            }
            signView.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.btn_sign, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign:
                signIn();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    //签到
    private void signIn() {
        mBtnSign.setEnabled(false);
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            mProgressDialog = MyUtils.initDialog("", this);
            mProgressDialog.show();
            String url = Constant.HOST + "signIn&userId=" + mUser.getUserId() + "&signInDate=" + DateUtils.getCurrentDate();
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressDialog.dismiss();
                    if (AnalysisJSON.analysisJson(response)) {
                        mToast.setText("签到成功");
                        mToast.show();
                        mBtnSign.setText("今日已签到");
                        refreshView();
                    } else {
                        mBtnSign.setEnabled(true);
                        mToast.setText("签到失败");
                        mToast.show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressDialog.dismiss();
                    mBtnSign.setEnabled(true);
                    mToast.setText("服务器抽风了，签到失败");
                    mToast.show();
                }
            });

            mQueue.add(request);
        } else {
            mToast.setText("网络不给力啊，换个地方试试");
            mToast.show();
            mBtnSign.setEnabled(true);
        }
    }

    //签到成功，刷新页面
    private void refreshView() {
        mDate.get(signView.getDayOfMonthToday() - 1).setDayType(SignView.DayType.SIGNED.getValue());
        signView.notifyDataSetChanged();
    }
}
