package com.chinalooke.yuwan.adapter;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 基本adapter
 * Created by xiao on 2016/9/28.
 */

public abstract class MyBaseAdapter<ITEMBEANTYPE> extends BaseAdapter {
    protected int STATUS;
    protected List<ITEMBEANTYPE> mDataSource = new ArrayList<>();

    public MyBaseAdapter(List<ITEMBEANTYPE> dataSource) {
        mDataSource = dataSource;
    }

    public MyBaseAdapter(List<ITEMBEANTYPE> dataSource, int status) {
        mDataSource = dataSource;
        this.STATUS = status;
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
