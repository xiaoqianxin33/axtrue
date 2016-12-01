package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.db.DBManager;
import com.chinalooke.yuwan.bean.Circle;
import com.chinalooke.yuwan.bean.GameMessage;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CircleInfoActivity extends AutoLayoutActivity {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.iv1)
    ImageView mIv1;
    @Bind(R.id.tv_circle_name)
    TextView mTvCircleName;
    @Bind(R.id.rl_game_name)
    RelativeLayout mRlGameName;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_game)
    LinearLayout mLlGame;
    @Bind(R.id.rl_game)
    RelativeLayout mRlGame;
    @Bind(R.id.iv3)
    ImageView mIv3;
    @Bind(R.id.tv_circle_address)
    TextView mTvCircleAddress;
    @Bind(R.id.rl_address)
    RelativeLayout mRlAddress;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.rl_time)
    RelativeLayout mRlTime;
    @Bind(R.id.iv4)
    ImageView mIv4;
    @Bind(R.id.tv_circle_expalin)
    TextView mTvCircleExpalin;
    @Bind(R.id.rl_explain)
    RelativeLayout mRlExplain;
    @Bind(R.id.activity_circle_info)
    LinearLayout mActivityCircleInfo;
    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    private LoginUser.ResultBean mUserInfo;
    private Circle.ResultBean mCircle;

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
    }

    private void initView() {
        mTvSkip.setText("编辑");
        mTvTitle.setText("圈子资料");
        if (mUserInfo != null) {
            if (mCircle.getUserId().equals(mUserInfo.getUserId())) {
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
    private void setGame() {
        String games = mCircle.getGames();
        if (!TextUtils.isEmpty(games)) {
            String[] game = games.split(",");
            DBManager dbManager = new DBManager(getApplicationContext());
            for (String s : game) {
                GameMessage.ResultBean gameInfo = dbManager.queryById(s);
                String thumb = gameInfo.getThumb();
                RoundedImageView imageView = new RoundedImageView(getApplicationContext());
                imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(60, 60));
                imageView.setPaddingRelative(5, 0, 5, 0);
                Picasso.with(getApplicationContext()).load(thumb).resize(60, 60).centerCrop().into(imageView);
                imageView.setOval(true);
                mLlGame.addView(imageView);
            }
        }
    }

    //设置头像
    private void setHeadImg(String headImg) {
        String uri = headImg + "?imageView2/1/w/120/h/120";
        Picasso.with(getApplicationContext()).load(uri).into(mRoundedImageView);
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
