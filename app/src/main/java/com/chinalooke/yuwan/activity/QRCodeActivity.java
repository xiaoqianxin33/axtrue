package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.chinalooke.yuwan.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class QRCodeActivity extends AppCompatActivity implements QRCodeView.Delegate {


    private QRCodeView mQRCodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        mQRCodeView.stopSpot();
        Intent intent = new Intent(this, SendUpAddFriendActivity.class);
        intent.putExtra("peopleId", result);
        startActivity(intent);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e("TAG", "onScanQRCodeOpenCameraError");
    }

    @OnClick(R.id.iv_wirte_back)
    public void onClick() {
        finish();
    }
}
