package com.chinalooke.yuwan.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
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
    private int RC_ACCESS_FINE_LOCATION = 1;
    private int READ_PHONE_STATE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        mTvTitle.setText("添加好友");
        requestReadPermission();
    }

    //设置手机号码
    private void setPhone() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String line1Number = tm.getLine1Number();
        if (!TextUtils.isEmpty(line1Number))
            mTvPhone.setText("我的手机号： " + line1Number.substring(3));
    }


    private void requestReadPermission() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            setPhone();
        } else {
            EasyPermissions.requestPermissions(this, "需要读取手机号码权限",
                    READ_PHONE_STATE, perms);
        }
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
        if (requestCode == RC_ACCESS_FINE_LOCATION)
            startActivity(new Intent(this, AddAddressBookFriendActivity.class));
        else if (requestCode == READ_PHONE_STATE)
            setPhone();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
