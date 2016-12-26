package com.chinalooke.yuwan.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiao on 2016/8/23.
 */
public abstract class MyListViewAdapter extends BaseAdapter {

    List mList = new ArrayList();
    private ViewHolder mViewHolder;


    public MyListViewAdapter(List list, Object viewHolder) {
        this.mList = list;
        mViewHolder = (ViewHolder) viewHolder;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mViewHolder= null;
        if (convertView == null) {
            setConvertView(convertView, mViewHolder);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        setDetails(mViewHolder, position);

        return convertView;
    }

    protected abstract void setDetails(ViewHolder viewHolder, int position);

    protected abstract void setConvertView(View convertView, ViewHolder viewHolder);

    public class ViewHolder {

    }
}
