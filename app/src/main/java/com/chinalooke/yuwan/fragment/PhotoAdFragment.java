package com.chinalooke.yuwan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chinalooke.yuwan.R;

/**
 * 发布图文广告fragment
 * Created by xiao on 2016/12/14.
 */

public class PhotoAdFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gameadfragment, container, false);
        return view;
    }
}
