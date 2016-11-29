package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.Goods;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    private RequestQueue mQueue;
    private List<Goods.ResultBean> mGoods = new ArrayList<>();
    private MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Goods.ResultBean resultBean = mGoods.get(position);
                String goodsId = resultBean.getGoodsId();
                Intent intent = new Intent(ShopActivity.this, GoodsActivity.class);
                intent.putExtra("goodsId", goodsId);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        getExchangeGoods();
    }

    //查询可兑换的虚拟商品
    private void getExchangeGoods() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String uri = Constant.HOST + "getExchangeGoods";
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mPbLoad.setVisibility(View.GONE);
                    if (response != null) {
                        if (AnalysisJSON.analysisJson(response)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<Goods>() {
                            }.getType();
                            Goods goods = gson.fromJson(response, type);
                            if (goods != null && goods.getResult() != null && goods.getResult().size() != 0) {
                                List<Goods.ResultBean> result = goods.getResult();
                                mGoods.addAll(result);
                                mMyAdapter.notifyDataSetChanged();
                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String msg = jsonObject.getString("Msg");
                                mTvNone.setText(msg);
                                mTvNone.setVisibility(View.VISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        mTvNone.setVisibility(View.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mPbLoad.setVisibility(View.GONE);
                    mTvNone.setText("服务器抽风了");
                    mTvNone.setVisibility(View.VISIBLE);
                }
            });
            mQueue.add(request);
        } else {
            mPbLoad.setVisibility(View.GONE);
            mTvNone.setText("网络未连接");
            mTvNone.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        mTvTitle.setText("鱼丸商城");
        mMyAdapter = new MyAdapter(mGoods);
        mListView.setAdapter(mMyAdapter);

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
                convertView = View.inflate(ShopActivity.this, R.layout.item_shop_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Goods.ResultBean resultBean = mGoods.get(position);
            String image = resultBean.getImage();
            if (!TextUtils.isEmpty(image))
                Picasso.with(getApplicationContext()).load(image).resize(150, 150).centerCrop().into(viewHolder.mIvImage);
            String paymoney = resultBean.getPaymoney();
            if (!TextUtils.isEmpty(paymoney))
                viewHolder.mTvPayMoney.setText("¥" + paymoney);
            String price = resultBean.getPrice();
            if (!TextUtils.isEmpty(price)) {
                viewHolder.mTvPrice.setText("原价：" + price);
                viewHolder.mTvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }

            String title = resultBean.getTitle();
            if (!TextUtils.isEmpty(title))
                viewHolder.mTvName.setText(title);
            String summary = resultBean.getSummary();
            if (!TextUtils.isEmpty(summary))
                viewHolder.mTvDescription.setText(summary);
            String sales = resultBean.getSales();
            if (!TextUtils.isEmpty(sales))
                viewHolder.mTvExchange.setText("已兑换：" + sales);
            return convertView;
        }

    }

    static class ViewHolder {
        @Bind(R.id.iv_image)
        ImageView mIvImage;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_description)
        TextView mTvDescription;
        @Bind(R.id.tv_payMoney)
        TextView mTvPayMoney;
        @Bind(R.id.tv_price)
        TextView mTvPrice;
        @Bind(R.id.tv_Exchange)
        TextView mTvExchange;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
