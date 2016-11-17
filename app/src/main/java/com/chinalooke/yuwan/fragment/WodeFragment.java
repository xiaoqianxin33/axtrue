package com.chinalooke.yuwan.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.LoginActivity;
import com.chinalooke.yuwan.activity.WoDeZiLiaoActivity;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.model.UserInfo;
import com.chinalooke.yuwan.utils.DialogUtil;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.view.CircleImageView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WodeFragment extends Fragment {

    RequestQueue mQueue;
    View view;
    CircleImageView mCircleImageView;
    @Bind(R.id.name__wode)
    TextView mNameView;
    @Bind(R.id.gerenshuoming__wode)
    TextView mShuoMingView;
    @Bind(R.id.btn_cancel_wode)
    Button mBtnCancel;

    @Bind(R.id.xiaoxi_wode)
    RelativeLayout mXiaoxiRela;
    @Bind(R.id.ziliao_wode)
    RelativeLayout mZiliaoRela;
    @Bind(R.id.liaotian_wode)
    RelativeLayout mLiaotianRela;
    @Bind(R.id.zhanji_wode)
    RelativeLayout mZhanjiRela;
    @Bind(R.id.yue_wode)
    RelativeLayout mYuERela;
    @Bind(R.id.zhanyou_wode)
    RelativeLayout mZhanyouRela;
    @Bind(R.id.setting_wode)
    RelativeLayout mSetRela;


    LoginUser.ResultBean user;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wode, container, false);
        ButterKnife.bind(this, view);
        mQueue = Volley.newRequestQueue(getActivity());
        user = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        return view;
    }


    @OnClick({R.id.name__wode, R.id.xiaoxi_wode, R.id.ziliao_wode, R.id.btn_cancel_wode, R.id.liaotian_wode, R.id.setting_wode, R.id.zhanji_wode, R.id.yue_wode, R.id.zhanyou_wode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.name__wode:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;

            case R.id.ziliao_wode:
                //设置资料信息
                setZiLiaoClick();
                break;
            case R.id.btn_cancel_wode:
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
                            initDatas();
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
        user = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        initDatas();
    }

    //初始化数据
    private void initDatas() {
        mCircleImageView = (CircleImageView) view.findViewById(R.id.icon_face_wode);
        //获取用户登录信息
        if (user != null) {
            //获取网络图片
            ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(mCircleImageView, R.mipmap.yw80_orange, R.mipmap.yw80_orange);
            imageLoader.get(user.getHeadImg(), listener);
            mNameView.setText(user.getNickName());
            mShuoMingView.setText(user.getSlogan());

        } else {
            mCircleImageView.setBackgroundResource(R.mipmap.yw80_orange);
            mNameView.setText("登录/注册");
            mBtnCancel.setText("登录");
            mShuoMingView.setText("");
        }

    }

    //资料点击事件
    private void setZiLiaoClick() {
        if (user != null) {
            Intent intent = new Intent(getActivity(), WoDeZiLiaoActivity.class);
            startActivity(intent);
        } else {
            setDialog();
        }
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
            }
        });
        //使用builder创建出对话框对象
        AlertDialog dialog = builder.create();
        //显示对话框
        dialog.show();
    }
}
