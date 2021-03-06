package com.chinalooke.yuwan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * viewPage adapter
 * Created by xiao on 2016/8/23.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private String[] TITLES;

    private List<Fragment> fragments = new ArrayList<Fragment>();

    public MainPagerAdapter(FragmentManager fragmentManager,
                            List<Fragment> fragments, String[] titles) {
        super(fragmentManager);
        this.fragments = fragments;
        this.TITLES = titles;
    }

    public MainPagerAdapter(FragmentManager fragmentManager,
                            List<Fragment> fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}