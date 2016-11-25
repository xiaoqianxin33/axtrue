package com.chinalooke.yuwan.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.CircleRankingActivity;
import com.chinalooke.yuwan.adapter.MainPagerAdapter;
import com.chinalooke.yuwan.model.Circle;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.view.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CircleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CircleFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.tabs)
    PagerSlidingTabStrip mTabs;
    @Bind(R.id.viewpage)
    ViewPager mViewpage;
    @Bind(R.id.tv_scoreboard)
    TextView mTvScoreBoard;

    private String mParam1;
    private String mParam2;
    private AMapLocationClient mLocationClient;
    //    private MyAdapt mMyAdapt;
    List<Circle.ResultBean> mCircles = new ArrayList<>();
    private int mPage = 1;
    private LoginUser.ResultBean mUserInfo;
    private Circle mMyCircle;
    private List<Circle.ResultBean> mMyCircleResult = new ArrayList<>();
//    private GridAdapt mGridAdapt;


    public CircleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CircleFragment.
     */
    public static CircleFragment newInstance(String param1, String param2) {
        CircleFragment fragment = new CircleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUserInfo = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        initEvent();
    }

    private void initEvent() {
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mTvScoreBoard.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mTvScoreBoard.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void initView() {
        List<Fragment> list = new ArrayList<>();
        list.add(new CircleNormalFragment());
        list.add(new CircleWodeFragment());
        mViewpage.setAdapter(new MainPagerAdapter(getChildFragmentManager(), list));
        mTabs.setViewPager(mViewpage);
        mTabs.setIndicatorColor(getResources().getColor(R.color.indicator_color));
        mTabs.setIndicatorHeight(5);
        mTabs.setSelectedTextColor(getResources().getColor(R.color.indicator_color));
        mTabs.setTextColor(getResources().getColor(R.color.white));
        mTabs.setTextSize(MyUtils.Dp2Px(getActivity(), 16));
        mTabs.setSelectedTabTextSize(MyUtils.Dp2Px(getActivity(), 16));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.tv_scoreboard)
    public void onClick() {
        Intent intent = new Intent(getActivity(), CircleRankingActivity.class);
        intent.putExtra("ranking_type", 0);
        startActivity(intent);
    }
}
