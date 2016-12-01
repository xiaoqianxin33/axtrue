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
 * 账单详情收入fragment
 * Created by xiao on 2016/12/1.
 */

public class AccountIncomeFragment extends Fragment {
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    @Bind(R.id.list_view)
    ListView mListView;
    private AccountDetailActivity mActivity;

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
        mActivity = (AccountDetailActivity) getActivity();
    }

    public void initData() {
        mPbLoad.setVisibility(View.GONE);
        List<Account.ResultBean> wholeList = mActivity.getWholeList();
        if (wholeList.size() == 0) {
            mTvNone.setText("暂无收支记录");
            mTvNone.setVisibility(View.VISIBLE);
        }
        AccountAdapter accountAdapter = new AccountAdapter(wholeList, mActivity);
        mListView.setAdapter(accountAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
