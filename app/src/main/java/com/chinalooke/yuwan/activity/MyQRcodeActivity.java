package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
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

public class MyQRCodeActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_slogen)
    TextView mTvSlogen;
    @Bind(R.id.iv_qrcode)
    ImageView mIvQrcode;
    private LoginUser.ResultBean mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode);
        ButterKnife.bind(this);
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        initView();
        initData();
    }

    private void initData() {
        String data = mUserInfo.getUserId() + mUserInfo.getNickName();
        String uri = "http://api.k780.com:88/?app=qr.get&data=" + data + "&level=L&size=6";
        Picasso.with(getApplicationContext()).load(uri).resize(530, 530).centerCrop().into(mIvQrcode);
    }

    private void initView() {
        mTvTitle.setText("我的二维码");
        String nickName = mUserInfo.getNickName();
        if (!TextUtils.isEmpty(nickName))
            mTvName.setText(nickName);
        String slogan = mUserInfo.getSlogan();
        if (!TextUtils.isEmpty(slogan))
            mTvSlogen.setText(slogan);
        String headImg = mUserInfo.getHeadImg();
        if (!TextUtils.isEmpty(headImg))
            Picasso.with(getApplicationContext()).load(headImg).resize(100, 100).centerCrop().into(mRoundedImageView);

    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
