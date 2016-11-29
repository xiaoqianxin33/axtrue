package com.chinalooke.yuwan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MainPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的圈子fragment
 * Created by xiao on 2016/11/22.
 */

public class CircleWodeFragment extends Fragment {

    @Bind(R.id.tv_wode)
    TextView mTvWode;
    @Bind(R.id.tv_create)
    TextView mTvCreate;
    @Bind(R.id.ll_title)
    LinearLayout mLlTitle;
    @Bind(R.id.wode_viewPage)
    ViewPager mViewPager;
    private FragmentManager mFragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_wode, container, false);
        ButterKnife.bind(this, view);
        mFragmentManager = getChildFragmentManager();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CircleWodeWFragment circleWodeWFragment = new CircleWodeWFragment();
        CircleWodeCFragment circleWodeCFragment = new CircleWodeCFragment();
        List<Fragment> list = new ArrayList<>();
        list.add(circleWodeCFragment);
        list.add(circleWodeWFragment);
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(mFragmentManager, list);
        mViewPager.setAdapter(mainPagerAdapter);
        setSelect(0);
        initEvent();
    }

    private void initEvent() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.tv_wode, R.id.tv_create})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_wode:
                setSelect(0);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_create:
                setSelect(1);
                mViewPager.setCurrentItem(1);
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
