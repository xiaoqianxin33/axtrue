package com.chinalooke.yuwan.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.avos.avoscloud.PushService;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.MyChatActivity;
import com.chinalooke.yuwan.activity.FriendsActivity;
import com.chinalooke.yuwan.activity.LoginActivity;
import com.chinalooke.yuwan.activity.MainActivity;
import com.chinalooke.yuwan.activity.MyBalanceActivity;
import com.chinalooke.yuwan.activity.PayForPlayerActivity;
import com.chinalooke.yuwan.activity.RecordActivity;
import com.chinalooke.yuwan.activity.SettingActivity;
import com.chinalooke.yuwan.activity.ShopActivity;
import com.chinalooke.yuwan.activity.SignInActivity;
import com.chinalooke.yuwan.activity.UserInfoActivity;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.utils.DialogUtil;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.ViewHelper;
import com.chinalooke.yuwan.view.MyScrollView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WodeFragment extends Fragment {


    LoginUser.ResultBean user;
    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_slogen)
    TextView mTvSlogen;
    @Bind(R.id.tv_login)
    TextView mTvLogin;
    @Bind(R.id.tv_sign_up)
    TextView mTvSignUp;
    @Bind(R.id.scrollView)
    MyScrollView mMyScrollView;
    @Bind(R.id.rl_top)
    RelativeLayout mRlTop;
    @Bind(R.id.rl_scroll)
    RelativeLayout mRlScroll;
    @Bind(R.id.rl_message)
    RelativeLayout mRlMessage;
    @Bind(R.id.rl_shop)
    RelativeLayout mRlShop;
    @Bind(R.id.rl_record)
    RelativeLayout mRlRecord;
    @Bind(R.id.rl_friend)
    RelativeLayout mRlFriend;
    @Bind(R.id.rl_balance)
    RelativeLayout mRlBalance;
    @Bind(R.id.rl_setting)
    RelativeLayout mRlSetting;
    @Bind(R.id.iv1)
    ImageView mIv1;
    @Bind(R.id.tv1)
    TextView mTv1;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.tv2)
    TextView mTv2;
    @Bind(R.id.iv3)
    ImageView mIv3;
    @Bind(R.id.tv3)
    TextView mTv3;
    private int START_ALPHA = 0;
    private int END_ALPHA = 255;
    private int mHeight;
    private MainActivity mActivity;
    private boolean isNetbar = false;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wode, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        final int heightPixels = ViewHelper.getDisplayMetrics(mActivity).heightPixels;
        final Drawable drawable = getResources().getDrawable(R.drawable.actionbar_color_else);
        drawable.setAlpha(START_ALPHA);
        mRlTop.setBackground(drawable);
        ViewTreeObserver viewTreeObserver = mRlScroll.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRlScroll.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mHeight = mRlScroll.getHeight();
                final int height = Math.abs(heightPixels - mHeight);
                mMyScrollView.setOnScrollChangedListener(new MyScrollView.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged(ScrollView who, int x, int y, int oldx, int oldy) {
                        if (y > height) {
                            y = height;   //当滑动到指定位置之后设置颜色为纯色，之前的话要渐变---实现下面的公式即可
                        }
                        drawable.setAlpha(y * (END_ALPHA - START_ALPHA) / height + START_ALPHA);

                    }
                });
            }
        });
        initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        user = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(mActivity, LoginUserInfoUtils.KEY);
        initView();
    }

    private void initEvent() {
        mTvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null)
                    startActivity(new Intent(mActivity, SignInActivity.class));
                else
                    startActivity(new Intent(mActivity, LoginActivity.class));
            }
        });
    }

    @OnClick({R.id.tv_name, R.id.roundedImageView, R.id.tv_login, R.id.rl_info, R.id.rl_friend
            , R.id.rl_shop, R.id.rl_record, R.id.rl_balance, R.id.rl_setting, R.id.rl_message
            , R.id.rl_chat})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_chat:
                if (user != null) {
                    if (isNetbar) {
                        startActivity(new Intent(mActivity, PayForPlayerActivity.class));
                    } else {
                        Intent intent = new Intent(mActivity, MyChatActivity.class);
                        intent.putExtra("chat", true);
                        startActivity(intent);
                    }
                } else
                    startActivity(new Intent(mActivity, LoginActivity.class));
                break;
            case R.id.rl_message:
                if (user != null) {
                    if (isNetbar) {
                        startActivity(new Intent(mActivity, UserInfoActivity.class));
                    } else {

                    }
                } else
                    startActivity(new Intent(mActivity, LoginActivity.class));
                break;
            case R.id.rl_setting:
                if (user != null)
                    startActivity(new Intent(mActivity, SettingActivity.class));
                else
                    startActivity(new Intent(mActivity, LoginActivity.class));
                break;
            case R.id.rl_balance:
                if (user != null)
                    startActivity(new Intent(mActivity, MyBalanceActivity.class));
                else
                    startActivity(new Intent(mActivity, LoginActivity.class));
                break;
            case R.id.rl_record:
                if (user != null)
                    startActivity(new Intent(mActivity, RecordActivity.class));
                else
                    startActivity(new Intent(mActivity, LoginActivity.class));
                break;
            case R.id.rl_shop:
                startActivity(new Intent(mActivity, ShopActivity.class));
                break;
            case R.id.rl_friend:
                if (user != null)
                    startActivity(new Intent(mActivity, FriendsActivity.class));
                else
                    startActivity(new Intent(mActivity, LoginActivity.class));
                break;
            case R.id.rl_info:
                if (user != null) {
                    if (isNetbar) {
                        startActivity(new Intent(mActivity, SettingActivity.class));
                    } else
                        startActivity(new Intent(mActivity, UserInfoActivity.class));
                } else
                    startActivity(new Intent(mActivity, LoginActivity.class));
                break;
            case R.id.tv_name:
                if (user == null)
                    startActivity(new Intent(mActivity, LoginActivity.class));
                break;
            case R.id.roundedImageView:
                if (user == null)
                    startActivity(new Intent(mActivity, LoginActivity.class));
                else {
                    startActivity(new Intent(mActivity, UserInfoActivity.class));
                }
                break;

            case R.id.tv_login:
                //退出登录时清除资料
                if (user != null) {
                    DialogUtil.showSingerDialog(mActivity, "提示", "确定注销吗？", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //退出环信
                            EMClient.getInstance().logout(true, new EMCallBack() {

                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onProgress(int progress, String status) {

                                }

                                @Override
                                public void onError(int code, String message) {

                                }
                            });
                            PushService.unsubscribe(mActivity, user.getUserId());
                            LoginUserInfoUtils.getLoginUserInfoUtils().clearData(mActivity);//清除资料
                            LoginUserInfoUtils.getLoginUserInfoUtils().setUserInfo(null);
                            setCancelDialog();
                            onResume();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                } else {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                }
                break;
        }
    }


    private void initView() {
        if (user != null) {
            mTvLogin.setText("退出登录");
            mTvSlogen.setVisibility(View.VISIBLE);
            String nickName = user.getNickName();
            if (!TextUtils.isEmpty(nickName))
                mTvName.setText(nickName);
            else
                mTvName.setText("暂未设置昵称");
            String headImg = user.getHeadImg();
            if (!TextUtils.isEmpty(headImg))
                Picasso.with(mActivity).load(headImg).resize(160, 160).centerCrop().into(mRoundedImageView);
            String slogan = user.getSlogan();
            if (!TextUtils.isEmpty(slogan))
                mTvSlogen.setText("简介：  " + slogan);

            if (user.getUserType().equals("netbar")) {
                mTvSignUp.setVisibility(View.GONE);
                isNetbar = true;
                mRlBalance.setVisibility(View.GONE);
                mRlFriend.setVisibility(View.GONE);
                mRlShop.setVisibility(View.GONE);
                mRlRecord.setVisibility(View.GONE);
                mRlSetting.setVisibility(View.GONE);
                mIv1.setImageResource(R.mipmap.wode_info);
                mTv1.setText("我的资料");
                mIv2.setImageResource(R.mipmap.money_wode);
                mTv2.setText("给玩家充值");
                mIv3.setImageResource(R.mipmap.wode_setting);
                mTv3.setText("设置");
            } else {
                mTvSignUp.setVisibility(View.VISIBLE);
                isNetbar = false;
                mRlBalance.setVisibility(View.VISIBLE);
                mRlFriend.setVisibility(View.VISIBLE);
                mRlMessage.setVisibility(View.VISIBLE);
                mRlShop.setVisibility(View.VISIBLE);
                mRlRecord.setVisibility(View.VISIBLE);
                mRlSetting.setVisibility(View.VISIBLE);
                mIv1.setImageResource(R.mipmap.wode_message);
                mTv1.setText("我的消息");
                mIv2.setImageResource(R.mipmap.wode_chat);
                mTv2.setText("我的聊天");
                mIv3.setImageResource(R.mipmap.wode_info);
                mTv3.setText("我的资料");
            }

        } else {
            mTvLogin.setText("登录/注册");
            mTvSlogen.setVisibility(View.GONE);
            mTvName.setText("登录/注册");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private void setCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        //设置对话框图标，可以使用自己的图片，Android本身也提供了一些图标供我们使用
        builder.setIcon(android.R.drawable.ic_dialog_info);
        //设置对话框标题
        builder.setTitle("提示");
        //设置对话框内的文本
        builder.setMessage("退出成功");
        //设置确定按钮，并给按钮设置一个点击侦听，注意这个OnClickListener使用的是DialogInterface类里的一个内部接口
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 执行点击确定按钮的业务逻辑
                dialog.dismiss();
                startActivity(new Intent(mActivity, LoginActivity.class));
            }
        });
        //使用builder创建出对话框对象
        AlertDialog dialog = builder.create();
        //显示对话框
        dialog.show();
    }
}
