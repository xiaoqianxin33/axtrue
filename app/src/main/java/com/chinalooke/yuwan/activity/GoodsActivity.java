package com.chinalooke.yuwan.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.GoodsDetail;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.UserBalance;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.ViewHelper;
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
import cn.bingoogolapple.bgabanner.BGABanner;

public class GoodsActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.banner)
    BGABanner mBanner;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_Exchange)
    TextView mTvExchange;
    @Bind(R.id.tv_price)
    TextView mTvPrice;
    @Bind(R.id.tv_sales)
    TextView mTvSales;
    @Bind(R.id.tv_description)
    TextView mTvDescription;
    @Bind(R.id.tv_ok)
    TextView mTvOk;
    private Toast mToast;
    private RequestQueue mQueue;
    private String mGoodsId;
    private GoodsDetail.ResultBean mGoods;
    private String mPaymoney;
    private String mTitle;
    private LoginUser.ResultBean mUser;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);
        ButterKnife.bind(this);
        mToast = YuwanApplication.getToast();
        mQueue = YuwanApplication.getQueue();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        initView();
        initData();
    }

    private void initData() {
        mGoodsId = getIntent().getStringExtra("goodsId");
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String uri = Constant.HOST + "getGoodsDetail&goodsId=" + mGoodsId;
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        if (AnalysisJSON.analysisJson(response)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<GoodsDetail>() {
                            }.getType();
                            GoodsDetail goodsDetail = gson.fromJson(response, type);
                            if (goodsDetail != null)
                                setData(goodsDetail);
                        } else {
                            MyUtils.showMsg(mToast, response);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mToast.setText("服务器抽风了,无法获取商品详情");
                    mToast.show();
                }
            });

            mQueue.add(request);
        } else {
            mToast.setText("网络未连接,无法获取商品详情");
            mToast.show();
        }
    }

    //加载页面数据
    private void setData(GoodsDetail goodsDetail) {
        List<GoodsDetail.ResultBean> result = goodsDetail.getResult();
        for (GoodsDetail.ResultBean resultBean : result) {
            String goodsId = resultBean.getGoodsId();
            if (mGoodsId.equals(goodsId))
                mGoods = resultBean;
        }

        if (mGoods != null) {
            List<View> viewList = new ArrayList<>();
            List<String> images = mGoods.getImages();
            for (String uri : images) {
                ImageView imageView = new ImageView(this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), uri, ViewHelper.getDisplayMetrics(this).widthPixels, 470);
                Picasso.with(this).load(loadImageUrl).into(imageView);
                viewList.add(imageView);
            }
            if (mBanner != null) {
                mBanner.setData(viewList);
            }


            mTitle = mGoods.getTitle();
            if (!TextUtils.isEmpty(mTitle))
                mTvName.setText(mTitle);
            String details = mGoods.getDetails();
            if (!TextUtils.isEmpty(details))
                mTvDescription.setText(details);
            mPaymoney = mGoods.getPaymoney();
            if (!TextUtils.isEmpty(mPaymoney))
                mTvExchange.setText("¥" + mPaymoney);

            String price = mGoods.getPrice();
            if (!TextUtils.isEmpty(price)) {
                mTvPrice.setText("原价：" + price);
                mTvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
            String sales = mGoods.getSales();
            if (!TextUtils.isEmpty(sales))
                mTvSales.setText("已兑换：" + sales);
        }
    }

    private void initView() {
        mTvTitle.setText("商品详情");
    }

    @OnClick({R.id.iv_back, R.id.tv_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_ok:
                if (mUser != null) {
                    showConfirmDialog();
                } else
                    startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    //弹出是否确认购买dialog
    private void showConfirmDialog() {
        String message = "鱼丸币：" + mPaymoney + "\n" + "兑换商品：" + mTitle;
        MyUtils.showWodeDialog(this, "确认兑换", message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mProgressDialog = MyUtils.initDialog("", GoodsActivity.this);
                mProgressDialog.show();
                getUserBalance();
            }
        }, "我再想想", "确认兑换");
    }

    //查询用户余额
    private void getUserBalance() {
        String uri = Constant.HOST + "getUserBalance&userId=" + mUser.getUserId();
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AnalysisJSON.analysisJson(response)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<UserBalance>() {
                    }.getType();
                    UserBalance userBalance = gson.fromJson(response, type);
                    String payMoney = userBalance.getResult().getPayMoney();
                    double v = Double.parseDouble(payMoney);
                    if (v < Double.parseDouble(mPaymoney)) {
                        mProgressDialog.dismiss();
                        mToast.setText("虚拟币余额不足");
                        mToast.show();
                    } else {
                        exchangeGoods();
                    }
                } else {
                    mProgressDialog.dismiss();
                    MyUtils.showMsg(mToast, response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                MyUtils.showToast(GoodsActivity.this, "服务器抽风了");
            }
        });
        mQueue.add(request);
    }

    //虚拟币兑换
    private void exchangeGoods() {
        String uri = Constant.HOST + "exchangeGoods&userId=" + mUser.getUserId() + "&goodsId=" + mGoodsId;
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                if (response != null) {
                    if (AnalysisJSON.analysisJson(response)) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("Result");
                            showSucceedDialog(code);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        MyUtils.showMsg(mToast, response);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("服务器抽风了");
                mToast.show();
            }
        });

        mQueue.add(request);
    }

    private void showSucceedDialog(final String code) {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_exchange_succeed, null);
        TextView tvCode = (TextView) inflate.findViewById(R.id.tv_code);
        tvCode.setText(code);
        TextView tvCopy = (TextView) inflate.findViewById(R.id.tv_copy);
        tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager systemService = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Text", code);
                systemService.setPrimaryClip(clipData);
                mToast.setText("兑换码已复制到剪切板");
                mToast.show();
            }
        });
        AutoUtils.autoSize(inflate);
        dialog.setContentView(inflate);
        dialog.show();
    }
}
