package com.chinalooke.yuwan.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.avos.avoscloud.AVAnalytics;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.Circle;
import com.chinalooke.yuwan.bean.CircleDetail;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.Auth;
import com.chinalooke.yuwan.utils.BitmapUtils;
import com.chinalooke.yuwan.utils.ImageUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.picasso.Picasso;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class CircleInfoActivity extends AutoLayoutActivity implements EasyPermissions.PermissionCallbacks {

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
    @Bind(R.id.iv_back)
    FrameLayout mIvBack;
    @Bind(R.id.iv_arrow_head)
    ImageView mIvArrowHead;
    @Bind(R.id.rl_head)
    RelativeLayout mRlHead;
    @Bind(R.id.rl_game_name)
    RelativeLayout mRlGameName;
    @Bind(R.id.rl_game)
    RelativeLayout mRlGame;
    @Bind(R.id.rl_address)
    RelativeLayout mRlAddress;
    @Bind(R.id.rl_time)
    RelativeLayout mRlTime;
    @Bind(R.id.rl_explain)
    RelativeLayout mRlExplain;
    @Bind(R.id.activity_circle_info)
    LinearLayout mActivityCircleInfo;
    private LoginUser.ResultBean mUserInfo;
    private Circle.ResultBean mCircle;
    private CircleDetail mCircleDetail;
    private boolean isOwner = false;
    private ImgSelConfig mConfig;
    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Picasso.with(context).load("file://" + path).into(imageView);
        }
    };
    private int RC_ACCESS_FINE_LOCATION = 0;
    private String mPath;
    private boolean isHeadChange = false;
    private String mRule;
    private UploadManager mUploadManager;
    private Toast mToast;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_info);
        ButterKnife.bind(this);
        mUploadManager = YuwanApplication.getmUploadManager();
        mToast = YuwanApplication.getToast();
        mQueue = YuwanApplication.getQueue();
        initPhotoPicker();
        initData();
        initView();
    }

    private void initData() {
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mCircle = (Circle.ResultBean) getIntent().getSerializableExtra("circle");
        mCircleDetail = (CircleDetail) getIntent().getSerializableExtra("circleDetail");
        if (mCircleDetail != null)
            setGame();
    }

    //初始化图片选择器
    private void initPhotoPicker() {
        mConfig = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(false)
                // “确定”按钮背景色
                .btnBgColor(Color.GRAY)
                // “确定”按钮文字颜色
                .btnTextColor(Color.BLUE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 返回图标ResId
                .backResId(R.drawable.ic_back)
                // 标题
                .title("图片")
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#3F51B5"))
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(1, 1, 200, 200)
                .needCrop(false)
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(9)
                .build();
    }

    private void initView() {
        mTvTitle.setText("圈子资料");
        if (mUserInfo != null) {
            if (mCircleDetail != null)
                isOwner = mUserInfo.getUserId().equals(mCircleDetail.getResult().getOwnerId());
        }
        if (isOwner)
            mTvSkip.setText("提交修改");
        String headImg = mCircle.getHeadImg();
        if (!TextUtils.isEmpty(headImg))
            setHeadImg(headImg);

        String groupName = mCircle.getGroupName();
        if (!TextUtils.isEmpty(groupName))
            mTvCircleName.setText(groupName);
        String address = mCircle.getAddress();
        if (!TextUtils.isEmpty(address))
            mTvCircleAddress.setText(address);
        if (mCircleDetail != null && mCircleDetail.getResult() != null) {
            String createTime = mCircleDetail.getResult().getCreateTime();
            if (!TextUtils.isEmpty(createTime))
                mTvTime.setText(createTime);
        }
        String details = mCircle.getDetails();
        if (!TextUtils.isEmpty(details))
            mTvCircleExpalin.setText(details);

        if (mCircleDetail != null) {
            CircleDetail.ResultBean result = mCircleDetail.getResult();
            if (result != null) {
                List<CircleDetail.ResultBean.GamesBean> games = result.getGames();
                if (games != null) {
                    mRlGame.setVisibility(View.VISIBLE);
                    mRlAddress.setVisibility(View.GONE);
                } else {
                    mRlAddress.setVisibility(View.VISIBLE);
                    mRlGame.setVisibility(View.GONE);
                }
            }
        }
    }

    //设置游戏
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setGame() {
        CircleDetail.ResultBean result = mCircleDetail.getResult();
        if (result != null) {
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
    }

    //设置头像
    private void setHeadImg(String headImg) {
        String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), headImg, 120, 120);
        Picasso.with(getApplicationContext()).load(loadImageUrl).into(mRoundedImageView);
    }

    @OnClick({R.id.iv_back, R.id.rl_head, R.id.rl_game_name, R.id.rl_explain})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_explain:
                if (isOwner)
                    showRuleDialog();
                break;
            case R.id.rl_game_name:
                if (isOwner) {
                    showEditDialog();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_head:
                if (isOwner)
                    req();
                break;
            case R.id.tv_skip:
                if (isHeadChange) {
                    uploadImage();
                }
                break;
        }
    }

    //上传头像至七牛
    private void uploadImage() {
        Auth auth = Auth.create(Constant.QINIU_ACCESSKEY, Constant.QINIU_SECRETKEY);
        String token = auth.uploadToken("yuwan");
        final String fileName = "circle_name" + new Date().getTime();
        Bitmap bitmap = ImageUtils.getBitmap(mPath);
        Bitmap compressBitmap = ImageEngine.getCompressBitmap(bitmap, getApplicationContext());
        if (compressBitmap == null) {
            mToast.setText("当前设置为非wifi下不能上传图片，请连接wifi");
            mToast.show();
            return;
        }
        mUploadManager.put(BitmapUtils.toArray(compressBitmap), fileName, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

            }
        }, null);
    }

    //编辑圈子昵称dialog
    private void showEditDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View inflate = View.inflate(this, R.layout.dialog_edit_name, null);
        TextView viewById = (TextView) inflate.findViewById(R.id.tv_title);
        final EditText etInput = (EditText) inflate.findViewById(R.id.et_input);
        Button btnCancel = (Button) inflate.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) inflate.findViewById(R.id.btn_ok);
        viewById.setText("编辑圈子名称");
        AutoUtils.autoSize(inflate);
        dialog.setContentView(inflate);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Editable text = etInput.getText();
                if (!TextUtils.isEmpty(text))
                    mTvCircleName.setText(text.toString());
            }
        });

        dialog.show();
        etInput.requestFocus();
    }

    private void showRuleDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View inflate = View.inflate(this, R.layout.dialog_add_game_rule, null);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_ok);
        TextView tvCancel = (TextView) inflate.findViewById(R.id.tv_cancel);
        TextView tvTitle = (TextView) inflate.findViewById(R.id.tv_title);
        tvTitle.setText("填写圈子说明");
        final EditText etRule = (EditText) inflate.findViewById(R.id.et_rule);
        if (!TextUtils.isEmpty(mRule))
            etRule.setText(mRule);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mRule = etRule.getText().toString();
                if (!TextUtils.isEmpty(mRule))
                    mTvCircleExpalin.setText(mRule);
            }
        });

        dialog.setContentView(inflate);
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void req() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            ImgSelActivity.startActivity(this, mConfig, 5);
        } else {
            EasyPermissions.requestPermissions(this, "需要拍照权限",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5 && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            mPath = pathList.get(0);
            Picasso.with(getApplicationContext()).load(mPath).into(mRoundedImageView);
            isHeadChange = true;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == RC_ACCESS_FINE_LOCATION)
            ImgSelActivity.startActivity(this, mConfig, 5);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
