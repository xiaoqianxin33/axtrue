package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MainPagerAdapter;
import com.chinalooke.yuwan.fragment.CreateInterestCircleFragment;
import com.chinalooke.yuwan.fragment.CreateLocationCircleFragment;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.chinalooke.yuwan.constant.Constant.MIN_CLICK_DELAY_TIME;
import static com.chinalooke.yuwan.constant.Constant.lastClickTime;

public class CreateCircleActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.viewPage)
    ViewPager mViewPage;
    @Bind(R.id.tv_location)
    TextView mTvLocation;
    @Bind(R.id.tv_interest)
    TextView mTvInterest;
    @Bind(R.id.ll_title)
    LinearLayout mLlTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_circle);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }


    private void initEvent() {

        mViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void initView() {
        mTvTitle.setText("创建圈子");
        CreateLocationCircleFragment createLocationCircleFragment = new CreateLocationCircleFragment();
        CreateInterestCircleFragment createInterestCircleFragment = new CreateInterestCircleFragment();
        List<Fragment> list = new ArrayList<>();
        list.add(createLocationCircleFragment);
        list.add(createInterestCircleFragment);
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), list);
        mViewPage.setAdapter(mainPagerAdapter);

    }

    @OnClick({R.id.tv_location, R.id.tv_interest, R.id.iv_back})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.tv_location:
                    setSelect(0);
                    mViewPage.setCurrentItem(0);
                    break;
                case R.id.tv_interest:
                    setSelect(1);
                    mViewPage.setCurrentItem(1);
                    break;
                case R.id.iv_back:
                    finish();
                    break;
            }
        }
    }

    private void setSelect(int i) {
        mTvLocation.setSelected(i == 0);
        mTvInterest.setSelected(i == 1);
        switch (i) {
            case 0:
                mLlTitle.setBackgroundResource(R.mipmap.wode_circle_left);
                break;
            case 1:
                mLlTitle.setBackgroundResource(R.mipmap.wode_circle_right);
                break;
        }
    }

}
