package com.chinalooke.yuwan.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.AbstractSpinerAdapter;

import java.util.List;


/**
 * 优惠劵下拉条
 * Created by xiao on 2016/10/31.
 */

public class SpinnerPopWindow extends PopupWindow implements AdapterView.OnItemClickListener {

    private Context mContext;
    private ListView mListView;
    private AbstractSpinerAdapter mAdapter;
    private AbstractSpinerAdapter.IOnItemSelectListener mItemSelectListener;


    public SpinnerPopWindow(Context context) {
        super(context);

        mContext = context;
        init();
    }


    public void setItemListener(AbstractSpinerAdapter.IOnItemSelectListener listener) {
        mItemSelectListener = listener;
    }

    public void setAdatper(AbstractSpinerAdapter adapter) {
        mAdapter = adapter;
        mListView.setAdapter(mAdapter);
    }


    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.spiner_window_layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);


        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);
    }


    public <T> void refreshData(List<T> list, int selIndex, List<T> countList) {
        if (list != null && selIndex != -1) {
            if (mAdapter != null) {
                mAdapter.refreshData(list, selIndex, countList);
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        dismiss();
        for (int i = 0; i < arg0.getCount(); i++) {
            View childAt = arg0.getChildAt(i);
            TextView textView = (TextView) childAt.findViewById(R.id.textView);
            if (pos == i) {
                textView.setTextColor(mContext.getResources().getColor(R.color.blue));
            } else {
                textView.setTextColor(mContext.getResources().getColor(R.color.black_word));
            }
        }
        if (mItemSelectListener != null) {
            mItemSelectListener.onItemClick(pos);
        }
    }


    public void clearCache() {
        for (int i = 0; i < mListView.getCount(); i++) {
            View childAt = mListView.getChildAt(i);
            TextView textView = (TextView) childAt.findViewById(R.id.textView);
            if (i == 0) {
                textView.setTextColor(mContext.getResources().getColor(R.color.blue));
            } else {
                textView.setTextColor(mContext.getResources().getColor(R.color.black_word));
            }
        }
    }
}