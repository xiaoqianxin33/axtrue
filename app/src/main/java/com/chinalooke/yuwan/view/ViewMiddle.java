package com.chinalooke.yuwan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.TextAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ViewMiddle extends LinearLayout implements ViewBaseAction {

    private ListView regionListView;
    private ListView plateListView;
    private List<String> groups = new ArrayList<>();
    private LinkedList<String> childrenItem = new LinkedList<String>();
    private SparseArray<LinkedList<String>> children = new SparseArray<>();
    private TextAdapter plateListViewAdapter;
    private TextAdapter earaListViewAdapter;
    private OnSelectListener mOnSelectListener;
    private OnLeftSelectListener mOnLeftSelectListener;
    private int tEaraPosition = 0;
    private int tBlockPosition = 0;
    private String showString = "全部游戏";

    public ViewMiddle(Context context) {
        super(context);
        init(context);
    }

    public ViewMiddle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void updateShowText(String showArea, String showBlock) {
        if (showArea == null || showBlock == null) {
            return;
        }
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).equals(showArea)) {
                earaListViewAdapter.setSelectedPosition(i);
                childrenItem.clear();
                if (i < children.size()) {
                    childrenItem.addAll(children.get(i));
                }
                tEaraPosition = i;
                break;
            }
        }
        for (int j = 0; j < childrenItem.size(); j++) {
            if (childrenItem.get(j).replace("不限", "").equals(showBlock.trim())) {
                plateListViewAdapter.setSelectedPosition(j);
                tBlockPosition = j;
                break;
            }
        }
        setDefaultSelect();
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_region, this, true);
        regionListView = (ListView) findViewById(R.id.listView);
        plateListView = (ListView) findViewById(R.id.listView2);
        setBackgroundDrawable(getResources().getDrawable(
                R.drawable.popback));

        groups.add("全部游戏");
        earaListViewAdapter = new TextAdapter(context, groups,
                R.drawable.popback,
                R.drawable.choose_eara_item_selector);
        earaListViewAdapter.setTextSize(17);
        earaListViewAdapter.setSelectedPositionNoNotify(tEaraPosition);
        regionListView.setAdapter(earaListViewAdapter);
        earaListViewAdapter
                .setOnItemClickListener(new TextAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        mOnLeftSelectListener.getValue(position);
                    }
                });
        if (tEaraPosition < children.size())
            childrenItem.addAll(children.get(tEaraPosition));
        plateListViewAdapter = new TextAdapter(context, childrenItem,
                R.drawable.popback,
                R.drawable.choose_plate_item_selector);
        plateListViewAdapter.setTextSize(15);
        plateListViewAdapter.setSelectedPositionNoNotify(tBlockPosition);
        plateListView.setAdapter(plateListViewAdapter);
        plateListViewAdapter
                .setOnItemClickListener(new TextAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, final int position) {

                        showString = childrenItem.get(position);
                        if (mOnSelectListener != null) {

                            mOnSelectListener.getValue(showString);
                        }

                    }
                });
        if (tBlockPosition < childrenItem.size())
            showString = childrenItem.get(tBlockPosition);
        if (showString.contains("不限")) {
            showString = showString.replace("不限", "");
        }
        setDefaultSelect();

    }

    public void setDefaultSelect() {
        regionListView.setSelection(tEaraPosition);
        plateListView.setSelection(tBlockPosition);
    }

    public void setCurrentSelect(int position) {
        earaListViewAdapter.setSelectedPosition(position);
    }

    public String getShowText() {
        return showString;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }

    public void setOnLedtSelectListener(OnLeftSelectListener onSelectListener) {
        mOnLeftSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        void getValue(String showText);
    }

    public interface OnLeftSelectListener {
        void getValue(int position);
    }

    //刷新右边listView的item
    public void changeRightItem(LinkedList<String> linkedList) {
        childrenItem.clear();
        childrenItem.addAll(linkedList);
        plateListViewAdapter.notifyDataSetChanged();
    }

    //刷新左边listView的item
    public void changeLeftItem(List<String> list) {
        groups.addAll(list);
        earaListViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }
}
