package com.chinalooke.yuwan.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.CircleRankingActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CircleFragment extends Fragment {

    @Bind(R.id.tv_scoreboard)
    TextView mTvScoreBoard;
    @Bind(R.id.tv_circle)
    TextView mTvCircle;
    @Bind(R.id.tv_wode_circle)
    TextView mTvWodeCircle;
    @Bind(R.id.fl_circle)
    FrameLayout mFlCircle;
    @Bind(R.id.tv_circle_bar)
    TextView mTvCircleBar;
    @Bind(R.id.tv_wode_circle_bar)
    TextView mTvWodeCircleBar;
    private FragmentManager mFragmentManager;
    private Fragment mContent;
    private CircleNormalFragment mCircleNormalFragment;
    private CircleWodeFragment mCircleWodeFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentManager = getChildFragmentManager();
        mCircleNormalFragment = new CircleNormalFragment();
        mCircleWodeFragment = new CircleWodeFragment();
        initView();
    }

    private void initView() {
        switchContent(new BlackFragment(), mCircleNormalFragment);
        setSelector(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.tv_scoreboard, R.id.tv_circle, R.id.tv_wode_circle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_scoreboard:
                Intent intent = new Intent(getActivity(), CircleRankingActivity.class);
                intent.putExtra("ranking_type", 0);
                startActivity(intent);
                break;
            case R.id.tv_circle:
                switchContent(mCircleWodeFragment, mCircleNormalFragment);
                setSelector(0);
                break;
            case R.id.tv_wode_circle:
                switchContent(mCircleNormalFragment, mCircleWodeFragment);
                setSelector(1);
                break;
        }

    }

    private void setSelector(int i) {
        mTvCircle.setSelected(i == 0);
        mTvCircleBar.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
        mTvWodeCircleBar.setVisibility(i == 1 ? View.VISIBLE : View.GONE);
        mTvWodeCircle.setSelected(i == 1);
    }

    public void switchContent(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fl_circle, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }
}
