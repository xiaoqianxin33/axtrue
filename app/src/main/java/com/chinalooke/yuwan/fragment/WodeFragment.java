package com.chinalooke.yuwan.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.FriendsActivity;
import com.chinalooke.yuwan.activity.LoginActivity;
import com.chinalooke.yuwan.activity.MyBalanceActivity;
import com.chinalooke.yuwan.activity.RecordActivity;
import com.chinalooke.yuwan.activity.ShopActivity;
import com.chinalooke.yuwan.activity.UserInfoActivity;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.utils.DialogUtil;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
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
    private RequestQueue mQueue;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wode, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mQueue = Volley.newRequestQueue(getActivity());
    }

    @OnClick({R.id.tv_name, R.id.roundedImageView, R.id.tv_login, R.id.rl_info, R.id.rl_friend
            , R.id.rl_shop, R.id.rl_record,R.id.rl_balance})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_balance:
                if (user != null)
                    startActivity(new Intent(getActivity(), MyBalanceActivity.class));
                else
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_record:
                if (user != null)
                    startActivity(new Intent(getActivity(), RecordActivity.class));
                else
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_shop:
                startActivity(new Intent(getActivity(), ShopActivity.class));
                break;
            case R.id.rl_friend:
                if (user != null)
                    startActivity(new Intent(getActivity(), FriendsActivity.class));
                else
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.rl_info:
                if (user != null)
                    startActivity(new Intent(getActivity(), UserInfoActivity.class));
                else
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.tv_name:
                if (user == null)
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.roundedImageView:
                if (user == null)
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                break;

            case R.id.tv_login:
                //退出登录时清除资料
                if (user != null) {
                    DialogUtil.showSingerDialog(getActivity(), "提示", "确定注销吗？", new DialogInterface.OnClickListener() {
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
                            LoginUserInfoUtils.getLoginUserInfoUtils().clearData(getActivity());//清除资料
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
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        user = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getActivity(), LoginUserInfoUtils.KEY);
        initView();
    }

    private void initView() {
        if (user != null) {
            mTvLogin.setText("退出登录");
            mTvSlogen.setVisibility(View.VISIBLE);
            String nickName = user.getNickName();
            if (!TextUtils.isEmpty(nickName))
                mTvName.setText(nickName);
            String headImg = user.getHeadImg();
            if (!TextUtils.isEmpty(headImg))
                Picasso.with(getActivity()).load(headImg).resize(160, 160).centerCrop().into(mRoundedImageView);
            String slogan = user.getSlogan();
            if (!TextUtils.isEmpty(slogan))
                mTvSlogen.setText("简介：  " + slogan);
        } else {
            mTvLogin.setText("登录/注册");
            mTvSlogen.setVisibility(View.GONE);
            mTvName.setText("登录/注册");
        }
    }

    //初始化数据
//    private void initDatas() {
//        mCircleImageView = (CircleImageView) view.findViewById(R.id.icon_face_wode);
//        //获取用户登录信息
//        if (user != null) {
//            //获取网络图片
//            ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
//            ImageLoader.ImageListener listener = ImageLoader.getImageListener(mCircleImageView, R.mipmap.yw80_orange, R.mipmap.yw80_orange);
//            imageLoader.get(user.getHeadImg(), listener);
//            mNameView.setText(user.getNickName());
//            mShuoMingView.setText(user.getSlogan());
//            mBtnCancel.setText("注销");
//        } else {
//            mCircleImageView.setBackgroundResource(R.mipmap.yw80_orange);
//            mNameView.setText("登录/注册");
//            mBtnCancel.setText("登录");
//            mShuoMingView.setText("");
//        }
//
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 图片缓存
     */
    public class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }


    private void setDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //设置对话框图标，可以使用自己的图片，Android本身也提供了一些图标供我们使用
        builder.setIcon(android.R.drawable.ic_dialog_info);
        //设置对话框标题
        builder.setTitle("提示");

        //设置对话框内的文本
        builder.setMessage("你还未登录，请先登录");
        //设置确定按钮，并给按钮设置一个点击侦听，注意这个OnClickListener使用的是DialogInterface类里的一个内部接口
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 执行点击确定按钮的业务逻辑
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //使用builder创建出对话框对象
        AlertDialog dialog = builder.create();
        //显示对话框
        dialog.show();
    }


    private void setCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //设置对话框图标，可以使用自己的图片，Android本身也提供了一些图标供我们使用
        builder.setIcon(android.R.drawable.ic_dialog_info);
        //设置对话框标题
        builder.setTitle("提示");
        //设置对话框内的文本
        builder.setMessage("注销成功");
        //设置确定按钮，并给按钮设置一个点击侦听，注意这个OnClickListener使用的是DialogInterface类里的一个内部接口
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 执行点击确定按钮的业务逻辑
                dialog.dismiss();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        //使用builder创建出对话框对象
        AlertDialog dialog = builder.create();
        //显示对话框
        dialog.show();
    }
}
