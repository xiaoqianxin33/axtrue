package com.chinalooke.yuwan.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class AddFriendsActivity extends AutoLayoutActivity implements EasyPermissions.PermissionCallbacks {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.et_phone)
    EditText mEtPhone;
    @Bind(R.id.tv_phone)
    TextView mTvPhone;
    @Bind(R.id.rl_near)
    RelativeLayout mRlNear;
    @Bind(R.id.rl_phone)
    RelativeLayout mRlPhone;
    private LoginUser.ResultBean mUser;
    private int RC_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        mTvTitle.setText("添加好友");

    }

    @OnClick({R.id.iv_back, R.id.rl_near, R.id.rl_phone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_near:
                startActivity(new Intent(this, AddNearbyFriendActivity.class));
                break;
            case R.id.rl_phone:
                requestPermission();
                break;
        }
    }

    //获取读取通讯录权限
    private void requestPermission() {
        String[] perms = {Manifest.permission.READ_CONTACTS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            startActivity(new Intent(this, AddAddressBookFriendActivity.class));
        } else {
            EasyPermissions.requestPermissions(this, "需要读取通讯录权限",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        startActivity(new Intent(this, AddAddressBookFriendActivity.class));
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
