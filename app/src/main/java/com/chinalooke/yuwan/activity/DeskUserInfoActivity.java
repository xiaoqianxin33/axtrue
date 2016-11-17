package com.chinalooke.yuwan.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.DeskUserInfo;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//游戏桌玩家资料查看
public class DeskUserInfoActivity extends AutoLayoutActivity {

    @Bind(R.id.iv_head)
    RoundedImageView mIvHead;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_age)
    TextView mTvAge;
    @Bind(R.id.address)
    TextView mAddress;
    @Bind(R.id.rl_sex)
    RelativeLayout mRlSex;
    @Bind(R.id.tv_total_fight)
    TextView mTvTotalFight;
    @Bind(R.id.pieChart)
    PieChart mPieChart;
    @Bind(R.id.tv_win)
    TextView mTvWin;
    @Bind(R.id.tv_lose)
    TextView mTvLose;
    @Bind(R.id.tv_run)
    TextView mTvRun;
    @Bind(R.id.tv_phone)
    TextView mTvPhone;
    @Bind(R.id.tv_location)
    TextView mTvLocation;
    private RequestQueue mQueue;
    private Toast mToast;
    private DeskUserInfo mDeskUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_desk_user_info);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        initData();
    }

    private void initData() {
        String userId = getIntent().getStringExtra("userId");
        String uri = Constant.HOST + "getUserInfoWithId&userId=" + userId;
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AnalysisJSON.analysisJson(response)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<DeskUserInfo>() {
                    }.getType();
                    mDeskUserInfo = gson.fromJson(response, type);
                    if (mDeskUserInfo != null) {
                        initView();
                    }
                } else {
                    mToast.setText("网络不佳，无法获取用户资料");
                    mToast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mToast.setText("网络不佳，无法获取用户资料");
                mToast.show();
            }
        });
        mQueue.add(request);
    }

    //获得数据，初始化界面数据
    private void initView() {
        DeskUserInfo.ResultBean result = mDeskUserInfo.getResult();
        String headImg = result.getHeadImg();
        if (!TextUtils.isEmpty(headImg))
            Picasso.with(getApplicationContext()).load(headImg).resize(200, 200).centerCrop().into(mIvHead);
        String sex = result.getSex();
        if (!TextUtils.isEmpty(sex)) {
            switch (sex) {
                case "女":
                    mRlSex.setBackgroundResource(R.mipmap.female_background);
                    break;
                case "男":
                    mRlSex.setBackgroundResource(R.mipmap.male_backgroud);
                    break;
            }
        }
        String nickName = result.getNickName();
        easySet(nickName, mTvName);
        String age = result.getAge();
        easySet(age, mTvAge);

        String address = result.getAddress();
        if (!TextUtils.isEmpty(address)) {
            String substring = address.substring(0, 6);
            mTvLocation.setText(address);
            mAddress.setText(substring);
        }

        String winCount = result.getWinCount();
        easySet(winCount, mTvWin);
        String loseCount = result.getLoseCount();
        easySet(loseCount, mTvLose);
        String breakCount = result.getBreakCount();
        easySet(breakCount, mTvRun);
        String sumPlayCount = result.getSumPlayCount();
        easySet(sumPlayCount, mTvTotalFight);
        String phone = result.getPhone();
        if (!TextUtils.isEmpty(phone)) {
            String head = phone.substring(0, 3);
            String foot = phone.substring(phone.length() - 4);
            String phone1 = head + "***" + foot;
            mTvPhone.setText(phone1);
        }


        if (!TextUtils.isEmpty(winCount) && !TextUtils.isEmpty(loseCount) && !TextUtils.isEmpty(breakCount)) {
            PieData pieData = getPieData(winCount, loseCount, breakCount);
            setPieChart(pieData);
        }

    }

    private PieData getPieData(String winCount, String loseCount, String breakCount) {
        int win = Integer.parseInt(winCount);
        int lose = Integer.parseInt(loseCount);
        int breakC = Integer.parseInt(breakCount);
        int total = win + lose + breakC;
        List<PieEntry> yValues = new ArrayList<>();
        float quarterly1 = win * 100 / total;
        float quarterly2 = lose * 100 / total;
        float quarterly3 = breakC * 100 / total;
        yValues.add(new PieEntry(quarterly1, 0));
        yValues.add(new PieEntry(quarterly2, 1));
        yValues.add(new PieEntry(quarterly3, 2));
        ArrayList<Integer> colors = new ArrayList<>();
        // 饼图颜色
        colors.add(Color.rgb(254, 193, 0));
        colors.add(Color.rgb(29, 173, 145));
        colors.add(Color.rgb(0, 186, 242));
        PieDataSet pieDataSet = new PieDataSet(yValues, "");
        pieDataSet.setSliceSpace(0f);
        pieDataSet.setColors(colors);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度
        return new PieData(pieDataSet);
    }

    //初始化饼状图
    private void setPieChart(PieData pieData) {
        mPieChart.setHoleColor(getResources().getColor(R.color.transparent));
        mPieChart.setHoleRadius(45);
        mPieChart.setUsePercentValues(true);
        mPieChart.setData(pieData);
    }

    private void easySet(String string, TextView textView) {
        if (!TextUtils.isEmpty(string))
            textView.setText(string);
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
