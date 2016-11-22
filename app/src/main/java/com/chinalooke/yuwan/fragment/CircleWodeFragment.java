package com.chinalooke.yuwan.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chinalooke.yuwan.R;

/**
 * 我的圈子fragment
 * Created by xiao on 2016/11/22.
 */

public class CircleWodeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_wode, container, false);
        return view;
    }
}
