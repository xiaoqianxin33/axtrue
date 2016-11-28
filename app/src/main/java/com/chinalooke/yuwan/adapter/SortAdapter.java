package com.chinalooke.yuwan.adapter;

/**
 * 好友列表adapter
 * Created by xiao on 2016/11/21.
 */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.model.FriendInfo;
import com.chinalooke.yuwan.model.FriendsList;
import com.chinalooke.yuwan.model.SortModel;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

public class SortAdapter extends BaseAdapter implements SectionIndexer {
    private List<SortModel> list = null;
    private Context mContext;
    private int type;

    public SortAdapter(Context mContext, List<SortModel> list, int type) {
        this.mContext = mContext;
        this.list = list;
        this.type = type;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<SortModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final SortModel mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_friends_recyclerview, null);
            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tvSlogen = (TextView) view.findViewById(R.id.tv_slogen);
            viewHolder.ivHead = (ImageView) view.findViewById(R.id.iv_head);
            viewHolder.ivCheck = (ImageView) view.findViewById(R.id.iv_check);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.tv_letter);
            view.setTag(viewHolder);
            AutoUtils.autoSize(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);
        if(type==0){
            FriendInfo.ResultBean friend = mContent.getFriend();
            String nickName = friend.getNickName();
            if (!TextUtils.isEmpty(nickName))
                viewHolder.tvName.setText(nickName);

            String headImg = friend.getHeadImg();
            if (!TextUtils.isEmpty(headImg))
                Picasso.with(mContext).load(headImg).resize(100, 100).into(viewHolder.ivHead);
            String slogan = friend.getSlogan();
            if (!TextUtils.isEmpty(slogan))
                viewHolder.tvSlogen.setText(slogan);
        }else if(type==1){
            FriendsList.ResultBean friends = mContent.getFriends();
            String nickName = friends.getNickName();
            if (!TextUtils.isEmpty(nickName))
                viewHolder.tvName.setText(nickName);

            String headImg = friends.getHeadImg();
            if (!TextUtils.isEmpty(headImg))
                Picasso.with(mContext).load(headImg).resize(100, 100).into(viewHolder.ivHead);
            String slogan = friends.getSlogan();
            if (!TextUtils.isEmpty(slogan))
                viewHolder.tvSlogen.setText(slogan);
        }

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        return view;

    }


    final static class ViewHolder {
        TextView tvName;
        TextView tvSlogen;
        ImageView ivHead;
        ImageView ivCheck;
        TextView tvLetter;
    }


    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }


    @Override
    public Object[] getSections() {
        return null;
    }
}