package com.chinalooke.yuwan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chinalooke.yuwan.R;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractSpinerAdapter<T> extends BaseAdapter {

    public static interface IOnItemSelectListener {
        public void onItemClick(int pos);
    }

    ;

    private Context mContext;
    private List<T> mObjects = new ArrayList<T>();
    private List<T> mCounts = new ArrayList<T>();
    private int mSelectItem = 0;


    private LayoutInflater mInflater;

    public AbstractSpinerAdapter(Context context) {
        init(context);
    }

    public void refreshData(List<T> objects, int selIndex, List<T> counts) {
        mObjects = objects;
        if (counts != null) {
            mCounts = counts;
        }
        if (selIndex < 0) {
            selIndex = 0;
        }
        if (selIndex >= mObjects.size()) {
            selIndex = mObjects.size() - 1;
        }

        mSelectItem = selIndex;
    }

    private void init(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {

        return mObjects.size();
    }

    @Override
    public Object getItem(int pos) {
        return mObjects.get(pos).toString();
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spiner_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.mCount = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Object item = getItem(pos);
        viewHolder.mTextView.setText(item.toString());

        if (mCounts.size() > pos) {
            viewHolder.mCount.setText(mCounts.get(pos).toString());
        }
        return convertView;
    }

    public static class ViewHolder {
        public TextView mTextView;
        private TextView mCount;
    }


}
