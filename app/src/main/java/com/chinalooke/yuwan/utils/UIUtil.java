package com.chinalooke.yuwan.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.GameDeskActivity;

/**
 * UI工具类
 * Created by xiao on 2016/11/16.
 */

public class UIUtil {

    //gridView动态设置高度
    public static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listView的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 5;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加5，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listView的每一个item
            if (i > 15)
                break;
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listView的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置参数
        listView.setLayoutParams(params);
    }

    public static void showJoinSucceedDialog(Activity activity, String message) {
        final Dialog dialog = new Dialog(activity, R.style.Dialog);
        View inflate = LayoutInflater.from(activity).inflate(R.layout.dialog_desk_succeed, null);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_ok);
        TextView tvMessage = (TextView) inflate.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(inflate);
        dialog.show();
    }
}
