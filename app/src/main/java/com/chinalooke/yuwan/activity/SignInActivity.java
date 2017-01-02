package com.chinalooke.yuwan.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.adapter.SignAdapter;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.SignEntity;
import com.chinalooke.yuwan.bean.SignHistory;
import com.chinalooke.yuwan.bean.SignMoney;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.ViewHelper;
import com.chinalooke.yuwan.view.MyScrollView;
import com.chinalooke.yuwan.view.NoSlidingListView;
import com.chinalooke.yuwan.view.SignView;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//签到页面
public class SignInActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.btn_sign)
    Button mBtnSign;
    @Bind(R.id.tv_month)
    TextView mTvMonth;
    @Bind(R.id.scrollView)
    MyScrollView mScrollView;
    @Bind(R.id.signView)
    SignView signView;
    @Bind(R.id.list_view)
    NoSlidingListView mListView;
    @Bind(R.id.rl_head)
    RelativeLayout mRlHead;
    private LoginUser.ResultBean mUser;
    private RequestQueue mQueue;
    private SignHistory mSignHistory;
    private Toast mToast;
    private int mDayOfMonthToday;
    private Calendar mCalendarToday;
    private ProgressDialog mProgressDialog;
    private ArrayList<SignEntity> mDate;
    private List<SignMoney.ResultBean> mSignMontyList = new ArrayList<>();
    private MyAdapter mMyAdapter;
    private int START_ALPHA = 0;
    private Drawable mDrawable;
    private int END_ALPHA = 255;
    private int mHeight;
    private int mHeightPixels;
    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        initView();
        initData();
        initEvent();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initEvent() {
        //头部渐变效果实现
        mHeightPixels = ViewHelper.getDisplayMetrics(this).heightPixels;
        mDrawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.actionbar_color_else);
        mDrawable.setAlpha(START_ALPHA);

        mRlHead.setBackground(mDrawable);
        ViewTreeObserver viewTreeObserver = mScrollView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mHeight = mScrollView.getHeight();
                final int height = Math.abs(mHeight - mHeightPixels);
                mScrollView.setOnScrollChangedListener(new MyScrollView.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged(ScrollView who, int x, int y, int oldx, int oldy) {
                        if (y > height) {
                            y = height;   //当滑动到指定位置之后设置颜色为纯色，之前的话要渐变---实现下面的公式即可
                        }
                        mDrawable.setAlpha(y * (END_ALPHA - START_ALPHA) / height + START_ALPHA);

                    }
                });
            }
        });
    }

    private void initData() {
        getSignInHistory();
        getSignMoney();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mDrawable != null)
            mDrawable.setAlpha(END_ALPHA);
    }

    //查询签到奖励虚拟币额度
    private void getSignMoney() {
        String url = Constant.HOST + "getSignMoney";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AnalysisJSON.analysisJson(response)) {
                    Gson gson = new Gson();
                    SignMoney signMoney = gson.fromJson(response, SignMoney.class);
                    if (signMoney != null && signMoney.getResult() != null && signMoney.getResult().size() != 0) {
                        mSignMontyList.addAll(signMoney.getResult());
                        mMyAdapter.notifyDataSetChanged();
                    }
                }
            }
        }, null);
        mQueue.add(request);
    }

    //查询用户签到记录
    private void getSignInHistory() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String url = Constant.HOST + "getSignInHistory&userId=" + mUser.getUserId();
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        if (AnalysisJSON.analysisJson(response)) {
                            Gson gson = new Gson();
                            mSignHistory = gson.fromJson(response, SignHistory.class);
                            if (mSignHistory != null) {
                                changeSignView();
                            }
                        }
                    }
                }
            }, null);
            mQueue.add(request);
        } else {
            mToast.setText("网络不可用，无法获取签到记录");
            mToast.show();
        }
    }

    private void initView() {
        mRlHead.setBackgroundResource(R.color.transparent);
        mTvTitle.setText("每日签到");
        mCalendar = Calendar.getInstance();
        int month = mCalendar.get(Calendar.MONTH);
        mTvMonth.setText(getResources().getStringArray(R.array.month_array)[month] + "签到表");
        mCalendarToday = Calendar.getInstance();
        mDayOfMonthToday = mCalendarToday.get(Calendar.DAY_OF_MONTH);
        initSignView();
        String headImg = mUser.getHeadImg();
        String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), headImg, 150, 150);
        Picasso.with(getApplicationContext()).load(loadImageUrl).into(mRoundedImageView);
        String nickName = mUser.getNickName();
        mTvName.setText(nickName);

        mMyAdapter = new MyAdapter(mSignMontyList);
        mListView.setAdapter(mMyAdapter);

    }

    //初始化日历
    private void initSignView() {
        mDate = new ArrayList<>();
        for (int i = 1; i < 31; i++) {
            SignEntity signEntity = new SignEntity();
            if (i == mDayOfMonthToday) {
                signEntity.setDayType(2);
            } else {
                signEntity.setDayType(3);
            }
            mDate.add(signEntity);
        }
        SignAdapter signAdapter = new SignAdapter(mDate);
        signView.setAdapter(signAdapter);
    }

    //根据数据刷新日历信息
    private void changeSignView() {
        mDate.clear();
        int year = mCalendarToday.get(Calendar.YEAR);
        int month = mCalendarToday.get(Calendar.MONTH);
        if (mSignHistory != null) {
            List<String> result = mSignHistory.getResult();
            List<String> signDay = new ArrayList<>();
            for (String s : result) {
                Date date = DateUtils.getDate(s, "yyyy-MM-dd HH:mm:ss");
                mCalendar.setTime(date);
                int year1 = mCalendar.get(Calendar.YEAR);
                if (year == year1) {
                    int month1 = mCalendar.get(Calendar.MONTH);
                    if (month == month1) {
                        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                        signDay.add(day + "");
                    }
                }
            }

            for (int i = 1; i < 31; i++) {
                SignEntity signEntity = new SignEntity();
                if (signDay.contains(i + "")) {
                    signEntity.setDayType(0);
                } else {
                    signEntity.setDayType(1);
                }
                if (i == mDayOfMonthToday) {
                    if (signDay.contains(mDayOfMonthToday + "")) {
                        mBtnSign.setText("今日已签到");
                        mBtnSign.setEnabled(false);
                        signEntity.setDayType(0);
                    } else {
                        signEntity.setDayType(2);
                    }
                }
                mDate.add(signEntity);
            }
            signView.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.btn_sign, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign:
                signIn();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    //签到
    private void signIn() {
        mBtnSign.setEnabled(false);
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            mProgressDialog = MyUtils.initDialog("", this);
            mProgressDialog.show();
            String url = Constant.HOST + "signIn&userId=" + mUser.getUserId() + "&signInDate=" + DateUtils.getCurrentDate();
            Log.e("TAG", url);
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressDialog.dismiss();
                    Log.e("TAG", response);
                    if (AnalysisJSON.analysisJson(response)) {
                        mToast.setText("签到成功");
                        mToast.show();
                        mBtnSign.setText("今日已签到");
                        refreshView();
                    } else {
                        mBtnSign.setEnabled(true);
                        MyUtils.showMsg(mToast, response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressDialog.dismiss();
                    mBtnSign.setEnabled(true);
                    mToast.setText("服务器抽风了，签到失败");
                    mToast.show();
                }
            });

            mQueue.add(request);
        } else {
            mToast.setText("网络不给力啊，换个地方试试");
            mToast.show();
            mBtnSign.setEnabled(true);
        }
    }

    //签到成功，刷新页面
    private void refreshView() {
        mDate.get(signView.getDayOfMonthToday() - 1).setDayType(SignView.DayType.SIGNED.getValue());
        signView.notifyDataSetChanged();
    }

    class MyAdapter extends MyBaseAdapter {

        public MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(SignInActivity.this, R.layout.item_sign_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            SignMoney.ResultBean resultBean = mSignMontyList.get(position);
            String days = resultBean.getDays();
            if (!TextUtils.isEmpty(days))
                viewHolder.mTvDay.setText(getString(R.string.libao,days));
            String payMoney = resultBean.getPayMoney();
            if (!TextUtils.isEmpty(payMoney))
                viewHolder.mTvDescription.setText("连续签到" + days + "天奖励" + payMoney + "雷熊币");
            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.tv_day)
        TextView mTvDay;
        @Bind(R.id.tv_description)
        TextView mTvDescription;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
