package com.chinalooke.yuwan.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.view.EditNameDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

import static com.chinalooke.yuwan.constant.Constant.MIN_CLICK_DELAY_TIME;
import static com.chinalooke.yuwan.constant.Constant.lastClickTime;

public class CreateCircleActivity extends AutoLayoutActivity implements EasyPermissions.PermissionCallbacks {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.tv_circle_name)
    TextView mTvCircleName;
    @Bind(R.id.rl_game_name)
    RelativeLayout mRlGameName;
    @Bind(R.id.iv1)
    ImageView mIv1;
    @Bind(R.id.iv_gameimage)
    RoundedImageView mIvGameimage;
    @Bind(R.id.rl_head)
    RelativeLayout mRlHead;
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
    @Bind(R.id.btn_create)
    Button mBtnCreate;
    @Bind(R.id.activity_create_circle)
    LinearLayout mActivityCreateCircle;
    private int RC_ACCESS_FINE_LOCATION = 0;
    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Picasso.with(context).load("file://" + path).into(imageView);
        }
    };
    private ImgSelConfig mConfig;
    private int REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_circle);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initEvent() {
        initPhotoPicker();
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
        mTvTitle.setText("创建圈子");
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnClick({R.id.iv_back, R.id.rl_game_name, R.id.rl_head, R.id.rl_game, R.id.rl_address, R.id.rl_time, R.id.rl_explain, R.id.btn_create})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.rl_game_name:
                    showEditDialog();
                    break;
                case R.id.rl_head:
                    req();
                    break;
                case R.id.rl_game:

                    break;
                case R.id.rl_address:
                    break;
                case R.id.rl_time:
                    break;
                case R.id.rl_explain:
                    break;
                case R.id.btn_create:
                    break;
            }
        }
    }

    private void showEditDialog() {
        final EditNameDialog editNameDialog = new EditNameDialog(this);
        editNameDialog.setTvTitle("编辑圈子昵称");
        editNameDialog.setNoOnclickListener(new EditNameDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                editNameDialog.dismiss();
            }
        });
        editNameDialog.setYesOnclickListener(new EditNameDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String input) {
                editNameDialog.dismiss();
                if (!TextUtils.isEmpty(input)) {
                    mTvCircleName.setText(input);
                }
            }
        });

        editNameDialog.show();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == RC_ACCESS_FINE_LOCATION)
            ImgSelActivity.startActivity(this, mConfig, REQUEST_CODE);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void req() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            ImgSelActivity.startActivity(this, mConfig, REQUEST_CODE);
        } else {
            EasyPermissions.requestPermissions(this, "需要定位权限",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 图片选择结果回调
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            for (String path : pathList) {
                Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
                Picasso.with(getApplicationContext()).load("file://" + path).into(mIvGameimage);
            }
        }
    }
}
