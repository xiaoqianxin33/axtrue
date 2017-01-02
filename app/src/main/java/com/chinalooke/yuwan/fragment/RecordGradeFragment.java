package com.chinalooke.yuwan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.bean.DeskUserInfo;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.view.PieChartView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 对战成绩fragment
 * Created by xiao on 2016/11/29.
 */

public class RecordGradeFragment extends Fragment {

    @Bind(R.id.tv_total_fight)
    TextView mTvTotalFight;
    @Bind(R.id.pieChart)
    PieChartView mPieChart;
    @Bind(R.id.tv_win)
    TextView mTvWin;
    @Bind(R.id.tv_lose)
    TextView mTvLose;
    @Bind(R.id.tv_run)
    TextView mTvRun;
    private RequestQueue mQueue;
    private LoginUser.ResultBean mUser;
    private Toast mToast;
    private DeskUserInfo mDeskUserInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_gradle, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mQueue = YuwanApplication.getQueue();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getActivity(), LoginUserInfoUtils.KEY);
        mToast = YuwanApplication.getToast();
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initData() {
        String uri = Constant.HOST + "getUserInfoWithId&userId=" + mUser.getUserId();
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

        String winCount = result.getWinCount();
        easySet(winCount, mTvWin);
        String loseCount = result.getLoseCount();
        easySet(loseCount, mTvLose);
        String breakCount = result.getBreakCount();
        easySet(breakCount, mTvRun);
        String sumPlayCount = result.getSumPlayCount();
        easySet(sumPlayCount, mTvTotalFight);

        if (!TextUtils.isEmpty(winCount) && !TextUtils.isEmpty(loseCount) && !TextUtils.isEmpty(breakCount)) {
            List<PieChartView.PieceDataHolder> pieceDataHolders = new ArrayList<>();
            pieceDataHolders.add(new PieChartView.PieceDataHolder(Integer.parseInt(winCount), 0xFFFEC100, ""));
            pieceDataHolders.add(new PieChartView.PieceDataHolder(Integer.parseInt(loseCount), 0xFF1DAD91, ""));
            pieceDataHolders.add(new PieChartView.PieceDataHolder(Integer.parseInt(breakCount), 0xFF01BBF1, ""));
            mPieChart.setData(pieceDataHolders);
            mPieChart.setMarkerLineLength(0);
        }
    }

    private void easySet(String string, TextView textView) {
        if (!TextUtils.isEmpty(string))
            textView.setText(string);
    }
}
