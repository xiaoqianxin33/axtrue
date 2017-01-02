package com.chinalooke.yuwan.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.Circle;
import com.chinalooke.yuwan.bean.CircleDetail;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CircleInfoActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.tv_circle_name)
    TextView mTvCircleName;
    @Bind(R.id.ll_game)
    LinearLayout mLlGame;
    @Bind(R.id.tv_circle_address)
    TextView mTvCircleAddress;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_circle_expalin)
    TextView mTvCircleExpalin;
    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    private LoginUser.ResultBean mUserInfo;
    private Circle.ResultBean mCircle;
    private CircleDetail mCircleDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_info);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mCircle = (Circle.ResultBean) getIntent().getSerializableExtra("circle");
        mCircleDetail = (CircleDetail) getIntent().getSerializableExtra("circleDetail");
        setGame();
    }

    private void initView() {
        mTvSkip.setText("编辑");
        mTvTitle.setText("圈子资料");
        if (mUserInfo != null) {
            if (mUserInfo.getUserId().equals(mCircle.getUserId())) {
                mTvSkip.setVisibility(View.VISIBLE);
            } else {
                mTvSkip.setVisibility(View.GONE);
            }
        } else {
            mTvSkip.setVisibility(View.GONE);
        }
        String headImg = mCircle.getHeadImg();
        if (!TextUtils.isEmpty(headImg))
            setHeadImg(headImg);

        String groupName = mCircle.getGroupName();
        if (!TextUtils.isEmpty(groupName))
            mTvCircleName.setText(groupName);
        String address = mCircle.getAddress();
        if (!TextUtils.isEmpty(address))
            mTvCircleAddress.setText(address);
        String createTime = mCircle.getCreateTime();
        if (!TextUtils.isEmpty(createTime))
            mTvTime.setText(createTime);
        String details = mCircle.getDetails();
        if (!TextUtils.isEmpty(details))
            mTvCircleExpalin.setText(details);
        setGame();

    }

    //设置游戏
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setGame() {
        List<CircleDetail.ResultBean.GamesBean> games = mCircleDetail.getResult().getGames();
        if (games != null && games.size() != 0) {
            for (CircleDetail.ResultBean.GamesBean gamesBean : games) {
                String thumb = gamesBean.getThumb();
                if (!TextUtils.isEmpty(thumb)) {
                    RoundedImageView imageView = new RoundedImageView(getApplicationContext());
                    imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(60, 60));
                    imageView.setPaddingRelative(5, 0, 5, 0);
                    String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), thumb, 60, 60);
                    Picasso.with(this).load(loadImageUrl).into(imageView);
                    imageView.setOval(true);
                    AutoUtils.autoSize(imageView);
                    mLlGame.addView(imageView);
                }
            }
        }
    }

    //设置头像
    private void setHeadImg(String headImg) {
        String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), headImg, 120, 120);
        Picasso.with(getApplicationContext()).load(loadImageUrl).into(mRoundedImageView);
    }

    @OnClick({R.id.iv_back, R.id.tv_skip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_skip:
                Intent intent = new Intent(CircleInfoActivity.this, CreateCircleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("circle", mCircle);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
