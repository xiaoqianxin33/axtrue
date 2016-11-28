package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoActivity extends AutoLayoutActivity {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.iv1)
    ImageView mIv1;
    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    @Bind(R.id.rl_head)
    RelativeLayout mRlHead;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.rl_name)
    RelativeLayout mRlName;
    @Bind(R.id.iv3)
    ImageView mIv3;
    @Bind(R.id.tv_sex)
    TextView mTvSex;
    @Bind(R.id.rl_sex)
    RelativeLayout mRlSex;
    @Bind(R.id.iv4)
    ImageView mIv4;
    @Bind(R.id.tv_age)
    TextView mTvAge;
    @Bind(R.id.rl_age)
    RelativeLayout mRlAge;
    @Bind(R.id.iv5)
    ImageView mIv5;
    @Bind(R.id.tv_play_age)
    TextView mTvPlayAge;
    @Bind(R.id.rl_play_age)
    RelativeLayout mRlPlayAge;
    @Bind(R.id.iv6)
    ImageView mIv6;
    @Bind(R.id.tv_address)
    TextView mTvAddress;
    @Bind(R.id.rl_address)
    RelativeLayout mRlAddress;
    @Bind(R.id.iv7)
    ImageView mIv7;
    @Bind(R.id.tv_id)
    TextView mTvId;
    @Bind(R.id.rl_id)
    RelativeLayout mRlId;
    @Bind(R.id.iv8)
    ImageView mIv8;
    @Bind(R.id.tv_qcodr)
    TextView mTvQcodr;
    @Bind(R.id.rl_qcode)
    RelativeLayout mRlQcode;
    @Bind(R.id.iv9)
    ImageView mIv9;
    @Bind(R.id.tv_slogen)
    TextView mTvSlogen;
    @Bind(R.id.rl_slogen)
    RelativeLayout mRlSlogen;
    @Bind(R.id.activity_user_info)
    LinearLayout mActivityUserInfo;
    private LoginUser.ResultBean mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        initView();
    }


    private void initView() {
        mTvTitle.setText("个人资料");
        String headImg = mUserInfo.getHeadImg();
        if (!TextUtils.isEmpty(headImg))
            Picasso.with(getApplicationContext()).load(headImg).resize(120, 120).centerCrop().into(mRoundedImageView);
        String nickName = mUserInfo.getNickName();
        if (!TextUtils.isEmpty(nickName))
            mTvName.setText(nickName);
        String address = mUserInfo.getAddress();
        if (!TextUtils.isEmpty(address))
            mTvAddress.setText(address);
        String playAge = mUserInfo.getPlayAge();
        if (!TextUtils.isEmpty(playAge))
            mTvPlayAge.setText(playAge);
        String age = mUserInfo.getAge();
        if (!TextUtils.isEmpty(age))
            mTvAge.setText(age);
        String sex = mUserInfo.getSex();
        if (!TextUtils.isEmpty(sex))
            mTvSex.setText(sex);
        String slogan = mUserInfo.getSlogan();
        if (!TextUtils.isEmpty(slogan))
            mTvSlogen.setText(slogan);

    }

    @OnClick({R.id.iv_back, R.id.rl_head, R.id.rl_name, R.id.rl_sex, R.id.rl_age, R.id.rl_play_age, R.id.rl_address, R.id.rl_id, R.id.rl_qcode, R.id.rl_slogen})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_head:
                break;
            case R.id.rl_name:
                break;
            case R.id.rl_sex:
                break;
            case R.id.rl_age:
                break;
            case R.id.rl_play_age:
                break;
            case R.id.rl_address:
                break;
            case R.id.rl_id:
                break;
            case R.id.rl_qcode:
                startActivity(new Intent(this, MyQRcodeActivity.class));
                break;
            case R.id.rl_slogen:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
