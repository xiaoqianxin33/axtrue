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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.NetUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 对战明细fragment
 * Created by xiao on 2016/11/29.
 */

public class RecordDetailFragment extends Fragment {

    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    private LoginUser.ResultBean mUser;
    private RequestQueue mQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mQueue = YuwanApplication.getQueue();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getActivity(), LoginUserInfoUtils.KEY);
        initData();
    }

    private void initData() {
        if (NetUtil.is_Network_Available(getActivity())) {
            String url = Constant.HOST + "getGameDeskListWithStatus&gameStatus=2&keywords=&userId="
                    + mUser.getUserId();
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mPbLoad.setVisibility(View.GONE);
                    if(AnalysisJSON.analysisJson(response)){

                    }else {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mPbLoad.setVisibility(View.GONE);
                    mTvNone.setText("服务器抽风了，请稍后再试");
                    mTvNone.setVisibility(View.VISIBLE);
                }
            });

            mQueue.add(request);

        } else {
            mTvNone.setText("网络未连接");
            mTvNone.setVisibility(View.VISIBLE);
            mPbLoad.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
