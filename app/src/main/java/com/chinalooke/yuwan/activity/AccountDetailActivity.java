package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MainPagerAdapter;
import com.chinalooke.yuwan.bean.Account;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.fragment.AccountIncomeFragment;
import com.chinalooke.yuwan.fragment.AccountPayFragment;
import com.chinalooke.yuwan.fragment.AccountWholeFragment;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.chinalooke.yuwan.view.PagerSlidingTabStrip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.autolayout.AutoLayoutActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//账户明细界面
public class AccountDetailActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.pagerSlidingTabStrip)
    PagerSlidingTabStrip mTabs;
    @Bind(R.id.viewPage)
    ViewPager mViewPage;
    private AccountWholeFragment mWholeFragment;
    private AccountIncomeFragment mAccountIncomeFragment;
    private AccountPayFragment mAccountPayFragment;
    private RequestQueue mQueue;
    private LoginUser.ResultBean mUser;
    private List<Account.ResultBean> mWholeList = new ArrayList<>();
    private List<Account.ResultBean> mPayList = new ArrayList<>();
    private List<Account.ResultBean> mIncomeList = new ArrayList<>();
    private Toast mToast;

    public List<Account.ResultBean> getWholeList() {
        return mWholeList;
    }

    public List<Account.ResultBean> getPayList() {
        return mPayList;
    }

    public List<Account.ResultBean> getIncomeList() {
        return mIncomeList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mToast = YuwanApplication.getToast();
        initView();
        initData();
    }

    private void initData() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String url = Constant.HOST + "getPayInfoList&userId=" + mUser.getUserId();
            StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        if (AnalysisJSON.analysisJson(response)) {
                            PreferenceUtils.setPrefString(getApplicationContext(), "account_detail", response);
                            analysisJson(response);
                        } else {
                            getCacheData();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    getCacheData();
                }
            });
            mQueue.add(stringRequest);
        } else {
            getCacheData();
        }
    }

    private void getCacheData() {
        String account_detail = PreferenceUtils.getPrefString(getApplicationContext(), "account_detail", "");
        if (!TextUtils.isEmpty(account_detail)) {
            analysisJson(account_detail);
        } else {
            mToast.setText("服务器抽风了，获取数据失败");
            mToast.show();
        }
    }

    private void analysisJson(String response) {
        Gson gson = new Gson();
        Type type = new TypeToken<Account>() {
        }.getType();
        Account account = gson.fromJson(response, type);
        if (account != null && account.getResult() != null && account.getResult().size() != 0) {
            List<Account.ResultBean> result = account.getResult();
            mWholeList.addAll(result);
            classifyList(result);
        }
    }

    //分类list
    private void classifyList(List<Account.ResultBean> result) {
        for (Account.ResultBean resultBean : result) {
            String moneyType = resultBean.getMoneyType();
            if ("pay".equals(moneyType) || "recharge".equals(moneyType)) {
                mPayList.add(resultBean);
            } else if ("income".equals(moneyType)) {
                mIncomeList.add(resultBean);
            }
        }
        mWholeFragment.initData();
        mAccountIncomeFragment.initData();
        mAccountPayFragment.initData();
    }

    private void initView() {
        mTvTitle.setText("账户明细");
        List<Fragment> list = new ArrayList<>();
        mWholeFragment = new AccountWholeFragment();
        mAccountIncomeFragment = new AccountIncomeFragment();
        mAccountPayFragment = new AccountPayFragment();
        list.add(mWholeFragment);
        list.add(mAccountIncomeFragment);
        list.add(mAccountPayFragment);
        String[] titles = {"全部", "收入", "支出"};
        mViewPage.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), list, titles));
        mTabs.setViewPager(mViewPage);
        mTabs.setIndicatorHeight(5);
        mTabs.setIndicatorColor(getResources().getColor(R.color.indicator_color));
        mTabs.setSelectedTextColor(getResources().getColor(R.color.indicator_color));
        mTabs.setTextColor(getResources().getColor(R.color.black_word));
        mTabs.setSelectedTabTextSize(MyUtils.Dp2Px(getApplicationContext(), 16));
        mTabs.setTextSize(MyUtils.Dp2Px(getApplicationContext(), 16));
        mTabs.setUnderlineHeight(0);
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
