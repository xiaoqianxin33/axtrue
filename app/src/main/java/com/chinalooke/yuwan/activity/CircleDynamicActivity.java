package com.chinalooke.yuwan.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.Circle;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.ViewHelper;
import com.chinalooke.yuwan.view.NoSlidingListView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CircleDynamicActivity extends AutoLayoutActivity {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.iv_camera)
    ImageView mIvCamera;
    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_slogen)
    TextView mTvSlogen;
    @Bind(R.id.tv_join)
    TextView mTvJoin;
    @Bind(R.id.list_view)
    NoSlidingListView mListView;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    @Bind(R.id.activity_circle_dynamic)
    LinearLayout mActivityCircleDynamic;
    @Bind(R.id.rl_top)
    RelativeLayout mRlTop;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    private Circle.ResultBean mCircle;
    private RequestQueue mQueue;
    private DisplayMetrics mDisplayMetrics;
    private int mCircle_type;
    private int PAGE_NO;
    private LoginUser.ResultBean mUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_circle_dynamic);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mDisplayMetrics = ViewHelper.getDisplayMetrics(CircleDynamicActivity.this);
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        initData();
        initView();

    }

    private void initView() {
        //设置顶部背景图片
        String bgImage = mCircle.getBgImage();
        ImageRequest request = new ImageRequest(bgImage, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if (response != null)
                    mRlTop.setBackground(new BitmapDrawable(response));
            }
        }, mDisplayMetrics.widthPixels, 400, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(request);

        String groupName = mCircle.getGroupName();
        if (!TextUtils.isEmpty(groupName))
            mTvName.setText(groupName);

        String details = mCircle.getDetails();
        if (!TextUtils.isEmpty(details))
            mTvSlogen.setText(details);

        String headImg = mCircle.getHeadImg();
        if (!TextUtils.isEmpty(headImg))
            Picasso.with(getApplicationContext()).load(headImg).resize(130, 130).centerCrop().into(mRoundedImageView);
    }

    private void initData() {
        mCircle = (Circle.ResultBean) getIntent().getSerializableExtra("circle");
        mCircle_type = getIntent().getIntExtra("circle_type", 0);
        getActiveList();
    }

    //获取圈子动态信息
    private void getActiveList() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String uri = Constant.HOST + "getActiveListWithGroup&groupId=" + mCircle.getGroupId()
                    + "&pageNo=" + PAGE_NO + "&pageSize=5";
            if (mUserInfo != null)
                uri = uri + "&userId=" + mUserInfo.getUserId();

            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mPbLoad.setVisibility(View.GONE);
                    if (AnalysisJSON.analysisJson(response)) {

                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            mTvNone.setVisibility(View.VISIBLE);
                            String msg = jsonObject.getString("Msg");
                            mTvNone.setText(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mPbLoad.setVisibility(View.GONE);
                    mTvNone.setVisibility(View.VISIBLE);
                    mTvNone.setText("服务器抽风了，请稍后重试");
                }
            });

            mQueue.add(request);
        } else {
            mPbLoad.setVisibility(View.GONE);
            mTvNone.setVisibility(View.VISIBLE);
            mTvNone.setText("网络未连接");
        }

    }


    @OnClick({R.id.iv_back, R.id.iv_camera})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_camera:
                break;
        }
    }
}
