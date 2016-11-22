package com.chinalooke.yuwan.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chinalooke.yuwan.R;

/**
 * 圈子第一页fragment
 * Created by xiao on 2016/11/22.
 */

public class CircleNormalFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_normal, container, false);
        return view;
    }
}
