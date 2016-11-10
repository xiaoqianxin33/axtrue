package com.chinalooke.yuwan.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chinalooke.yuwan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProceedFragment extends Fragment {


    public ProceedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proceed, container, false);
    }

}
