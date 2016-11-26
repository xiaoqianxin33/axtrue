package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DynamicDetailActivity extends AutoLayoutActivity {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.iv_camera)
    ImageView mIvCamera;
    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_content)
    TextView mTvContent;
    @Bind(R.id.gridView)
    GridView mGridView;
    @Bind(R.id.iv1)
    ImageView mIv1;
    @Bind(R.id.tv_address)
    TextView mTvAddress;
    @Bind(R.id.iv_pinglun)
    ImageView mIvPinglun;
    @Bind(R.id.tv_pinglun)
    TextView mTvPinglun;
    @Bind(R.id.tv_dianzan)
    TextView mTvDianzan;
    @Bind(R.id.iv_dianzan)
    ImageView mIvDianzan;
    @Bind(R.id.activity_dynamic_detail)
    LinearLayout mActivityDynamicDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_detail);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_back, R.id.iv_camera})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                break;
            case R.id.iv_camera:
                break;
        }
    }
}
