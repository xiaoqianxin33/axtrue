package com.chinalooke.yuwan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;

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
    private Fragment mContent;
    private FragmentManager mFragmentManager;
    private CircleWodeWFragment mCircleWodeWFragment;
    private CircleWodeCFragment mCircleWodeCFragment;

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
        mCircleWodeWFragment = new CircleWodeWFragment();
        mCircleWodeCFragment = new CircleWodeCFragment();
        switchContent(new BlackFragment(), mCircleWodeWFragment);
        setSelect(0);
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
                switchContent(mCircleWodeCFragment, mCircleWodeWFragment);
                break;
            case R.id.tv_create:
                setSelect(1);
                switchContent(mCircleWodeWFragment, mCircleWodeCFragment);
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
