package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.db.ExchangeHelper;
import com.chinalooke.yuwan.model.ExchangeLevels;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RechargeActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.gridView)
    GridView mGridView;
    private RequestQueue mQueue;
    private List<ExchangeLevels.ResultBean> mList = new ArrayList<>();
    private MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExchangeLevels.ResultBean resultBean = mList.get(position);
                RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_root);
                relativeLayout.setBackgroundResource(R.drawable.recharge_click_shape);
                TextView view1 = (TextView) view.findViewById(R.id.tv_pay);
                view1.setTextColor(getResources().getColor(R.color.orange));
                TextView view2 = (TextView) view.findViewById(R.id.tv_sale);
                view2.setTextColor(getResources().getColor(R.color.orange));
                Intent intent = new Intent(RechargeActivity.this, ChosePayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("pay", resultBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String uri = Constant.HOST + "getExchangeLevels";
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (AnalysisJSON.analysisJson(response)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ExchangeLevels>() {
                        }.getType();
                        ExchangeLevels exchangeLevels = gson.fromJson(response, type);
                        if (exchangeLevels != null && exchangeLevels.getResult() != null && exchangeLevels.getResult().size() != 0) {
                            List<ExchangeLevels.ResultBean> result = exchangeLevels.getResult();
                            mList.addAll(result);
                            mMyAdapter.notifyDataSetChanged();
                            upDateDB(result);
                        }
                    } else {
                        fromDB();
                    }
                }
            }, null);
            mQueue.add(request);
        } else {
            fromDB();
        }
    }

    //从数据库取缓存显示
    private void fromDB() {
        ExchangeHelper exchangeHelper = ExchangeHelper.getHelper(getApplicationContext());
        try {
            List<ExchangeLevels.ResultBean> resultBeen = exchangeHelper.getUserDao().queryForAll();
            if (resultBeen != null) {
                mList.addAll(resultBeen);
                mMyAdapter.notifyDataSetChanged();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("TAG", e.getMessage());
        }
    }

    //更新数据库数据
    private void upDateDB(List<ExchangeLevels.ResultBean> result) {
        ExchangeHelper exchangeHelper = ExchangeHelper.getHelper(getApplicationContext());
        for (int i = 0; i < result.size(); i++) {
            try {
                ExchangeLevels.ResultBean resultBean = result.get(i);
                resultBean.setId(i);
                exchangeHelper.getUserDao().createOrUpdate(resultBean);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private void initView() {
        mTvTitle.setText("充值");
        mMyAdapter = new MyAdapter(mList);
        mGridView.setAdapter(mMyAdapter);
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }

    class MyAdapter extends MyBaseAdapter {

        MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(RechargeActivity.this, R.layout.item_recharge_gridview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ExchangeLevels.ResultBean resultBean = mList.get(position);
            viewHolder.mTvPay.setText(resultBean.getMoney() + "元");
            viewHolder.mTvSale.setText(resultBean.getExchange() + "鱼丸");
            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.tv_pay)
        TextView mTvPay;
        @Bind(R.id.tv_sale)
        TextView mTvSale;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
