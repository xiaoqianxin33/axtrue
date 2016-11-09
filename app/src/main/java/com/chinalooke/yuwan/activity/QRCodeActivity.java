package com.chinalooke.yuwan.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.utils.MyUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
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
    public void onScanQRCodeSuccess(final String result) {
        mQRCodeView.stopSpot();
        MyUtils.showToast(getApplicationContext(), result);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }

    @OnClick(R.id.iv_wirte_back)
    public void onClick() {
        finish();
    }
}
