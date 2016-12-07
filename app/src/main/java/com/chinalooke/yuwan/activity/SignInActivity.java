package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.view.MyScrollView;
import com.chinalooke.yuwan.view.SignView;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    private LoginUser.ResultBean mUser;
    private RequestQueue mQueue;
    private SignHistory mSignHistory;
    private Toast mToast;
    private int mDayOfMonthToday;
    private Calendar mCalendarToday;

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
    }

    //初始化日历
    private void initSignView() {
        ArrayList<SignEntity> arrayList = new ArrayList<>();
        for (int i = 1; i < 31; i++) {
            SignEntity signEntity = new SignEntity();
            if (i == mDayOfMonthToday) {
                signEntity.setDayType(2);
            } else {
                signEntity.setDayType(3);
            }
            arrayList.add(signEntity);
        }
        SignAdapter signAdapter = new SignAdapter(arrayList);
        signView.setAdapter(signAdapter);
    }

    //根据数据刷新日历信息
    private void changeSignView() {
        List<SignEntity> list = new ArrayList<>();
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
                } else if (mDayOfMonthToday == i) {
                    signEntity.setDayType(2);
                } else {
                    signEntity.setDayType(1);
                }

                list.add(signEntity);
            }
            SignAdapter signAdapter = new SignAdapter(list);
            signView.setAdapter(signAdapter);
        }
    }
}
