package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.SignAdapter;
import com.chinalooke.yuwan.bean.SignEntity;
import com.chinalooke.yuwan.view.MyScrollView;
import com.chinalooke.yuwan.view.SignView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

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
    @Bind(R.id.signView)
    SignView mSignView;
    @Bind(R.id.scrollView)
    MyScrollView mScrollView;
    @Bind(R.id.activity_sign_in)
    RelativeLayout mActivitySignIn;
    private List<SignEntity> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mRlHead.setBackgroundResource(R.color.transparent);
        mTvTitle.setText("每日签到");
        initSignView();
    }

    //初始化签到日历
    private void initSignView() {

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        mTvMonth.setText(getResources().getStringArray(R.array.month_array)[month] + "签到表");

        Calendar calendarToday = Calendar.getInstance();
        int dayOfMonthToday = calendarToday.get(Calendar.DAY_OF_MONTH);

        data = new ArrayList<>();
        Random ran = new Random();
        for (int i = 0; i < 30; i++) {
            SignEntity signEntity = new SignEntity();
            if (dayOfMonthToday == i + 1)
                signEntity.setDayType(2);
            else
                signEntity.setDayType((ran.nextInt(1000) % 2 == 0) ? 0 : 1);
            data.add(signEntity);
        }
        SignAdapter signAdapter = new SignAdapter(data);
        mSignView.setAdapter(signAdapter);
    }
}
