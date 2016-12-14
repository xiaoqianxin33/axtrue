package com.chinalooke.yuwan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MainPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 广告界面
 * Created by xiao on 2016/12/14.
 */

public class AdvertisementFragment extends Fragment {

    @Bind(R.id.iv_back)
    FrameLayout mIvBack;
    @Bind(R.id.iv_arrow_head)
    ImageView mIvArrowHead;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.rl_head)
    RelativeLayout mRlHead;
    @Bind(R.id.tv_game)
    TextView mTvGame;
    @Bind(R.id.tv_photo)
    TextView mTvPhoto;
    @Bind(R.id.ll_title)
    LinearLayout mLlTitle;
    @Bind(R.id.viewPage)
    ViewPager mViewPage;
    private GameAdFragment mGameAdFragment;
    private PhotoAdFragment mPhotoAdFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advertisement, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        mIvBack.setVisibility(View.GONE);
        mIvArrowHead.setVisibility(View.GONE);
        mTvTitle.setText("广告");
        mTvSkip.setText("发送");

        mGameAdFragment = new GameAdFragment();
        mPhotoAdFragment = new PhotoAdFragment();
        List<Fragment> list = new ArrayList<>();
        list.add(mGameAdFragment);
        list.add(mPhotoAdFragment);
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getChildFragmentManager(), list);
        mViewPage.setAdapter(mainPagerAdapter);
        setSelect(0);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private void setSelect(int i) {
        mTvGame.setSelected(i == 0);
        mTvPhoto.setSelected(i == 1);
        switch (i) {
            case 0:
                mLlTitle.setBackgroundResource(R.mipmap.wode_circle_left);
                break;
            case 1:
                mLlTitle.setBackgroundResource(R.mipmap.wode_circle_right);
                break;
        }
    }

    public ViewPager getViewPage() {
        return mViewPage;
    }

    @OnClick({R.id.tv_game, R.id.tv_photo, R.id.tv_skip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_skip:
                int currentItem = mViewPage.getCurrentItem();
                switch (currentItem) {
                    case 0:
                        mGameAdFragment.releaseAd();
                        break;
                    case 1:
                        mPhotoAdFragment.releaseAd();
                        break;
                }
                break;
            case R.id.tv_game:
                setSelect(0);
                mViewPage.setCurrentItem(0);
                break;
            case R.id.tv_photo:
                setSelect(1);
                mViewPage.setCurrentItem(1);
                break;
        }
    }
}
