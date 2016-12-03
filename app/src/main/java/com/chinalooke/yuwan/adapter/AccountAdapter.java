package com.chinalooke.yuwan.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.Account;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 账户明细 adapter
 * Created by xiao on 2016/12/1.
 */

public class AccountAdapter extends MyBaseAdapter {
    private Context mContext;


    public AccountAdapter(List dataSource) {
        super(dataSource);
    }

    public AccountAdapter(List dataSource, Context context) {
        super(dataSource);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_account_detail_listview, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            AutoUtils.autoSize(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Account.ResultBean resultBean = (Account.ResultBean) mDataSource.get(position);
        String createTime = resultBean.getCreateTime();
        if (!TextUtils.isEmpty(createTime))
            viewHolder.mTvTime.setText(createTime);

        String moneyType = resultBean.getMoneyType();
        String money = resultBean.getMoney();
        switch (moneyType) {
            case "income":
                viewHolder.mTvMoney.setText("+" + money);
                viewHolder.mTvType.setText("虚拟币收入");
                break;
            case "pay":
                viewHolder.mTvMoney.setText("-" + money);
                viewHolder.mTvType.setText("虚拟币支出");
                break;
            case "recharge":
                viewHolder.mTvMoney.setText("-" + money);
                viewHolder.mTvType.setText("充值够买虚拟币");
                break;
        }

        return convertView;
    }

    public class ViewHolder {
        @Bind(R.id.tv_type)
        TextView mTvType;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_money)
        TextView mTvMoney;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}