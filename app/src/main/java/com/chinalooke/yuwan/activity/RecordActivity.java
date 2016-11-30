package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MainPagerAdapter;
import com.chinalooke.yuwan.fragment.RecordDetailFragment;
import com.chinalooke.yuwan.fragment.RecordGradeFragment;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//我的战绩界面
public class RecordActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_wode)
    TextView mTvWode;
    @Bind(R.id.tv_create)
    TextView mTvCreate;
    @Bind(R.id.ll_title)
    LinearLayout mLlTitle;
    @Bind(R.id.viewPage)
    ViewPager mViewPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initEvent() {
        //viewPage滑动监听
        mViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setSelect(0);
                        break;
                    case 1:
                        setSelect(1);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        mTvTitle.setText("我的战绩");
        RecordDetailFragment recordDetailFragment = new RecordDetailFragment();
        RecordGradeFragment recordGradeFragment = new RecordGradeFragment();
        List<Fragment> list = new ArrayList<>();
        list.add(recordDetailFragment);
        list.add(recordGradeFragment);
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), list);
        mViewPage.setAdapter(mainPagerAdapter);
        setSelect(0);
    }

    @OnClick({R.id.iv_back, R.id.tv_wode, R.id.tv_create})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_wode:
                setSelect(0);
                mViewPage.setCurrentItem(0);
                break;
            case R.id.tv_create:
                setSelect(1);
                mViewPage.setCurrentItem(1);
                break;
        }
    }

    private void setSelect(int i) {
        mTvWode.setSelected(i == 0);
        mTvCreate.setSelected(i == 1);
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
