package com.chinalooke.yuwan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.AccountDetailActivity;
import com.chinalooke.yuwan.adapter.AccountAdapter;
import com.chinalooke.yuwan.bean.Account;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 账单详情全部fragment
 * Created by xiao on 2016/12/1.
 */

public class AccountWholeFragment extends Fragment {
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    @Bind(R.id.list_view)
    ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void initData() {
        AccountDetailActivity activity = (AccountDetailActivity) getActivity();
        List<Account.ResultBean> wholeList = activity.getWholeList();
        if (mPbLoad != null)
            mPbLoad.setVisibility(View.GONE);
        if (wholeList.size() == 0) {
            mTvNone.setText("暂无收支记录");
            mTvNone.setVisibility(View.VISIBLE);
        } else {
            mTvNone.setVisibility(View.GONE);
        }
        AccountAdapter accountAdapter = new AccountAdapter(wholeList, activity);
        mListView.setAdapter(accountAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
