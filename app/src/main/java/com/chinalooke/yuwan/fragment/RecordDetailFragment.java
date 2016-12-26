package com.chinalooke.yuwan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.RecordActivity;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.GameDesk;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.view.NoSlidingListView;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<GameDesk.ResultBean> mList = new ArrayList<>();
    private List<String> mDateList = new ArrayList<>();
    private Map<String, ArrayList<GameDesk.ResultBean>> mMap = new HashMap<>();
    private RecordActivity mActivity;
    private MyAdapter mMyAdapter;
    private int PAGE_NO;
    private boolean isLoading = false;
    private View mFoot;
    private boolean isFirst = true;
    private boolean isFoot = false;


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
        mActivity = (RecordActivity) getActivity();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(mActivity, LoginUserInfoUtils.KEY);
        mFoot = View.inflate(mActivity, R.layout.foot, null);
        initView();
        initEvent();

    }

    private void initEvent() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        isLoading = true;
        if (!isFoot) {
            mListView.removeFooterView(mFoot);
            PAGE_NO++;
            initData();
        }
    }

    private void initView() {
        mMyAdapter = new MyAdapter(mDateList);
        mListView.setAdapter(mMyAdapter);
    }

    private void initData() {
        if (NetUtil.is_Network_Available(mActivity)) {
            String url = Constant.HOST + "getGameDeskListWithStatus&gameStatus=2&keywords=&userId="
                    + mUser.getUserId() + "&pageNo=" + PAGE_NO + "&pageSize=8";
            Log.e("TAG", url);
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mPbLoad.setVisibility(View.GONE);
                    if (AnalysisJSON.analysisJson(response)) {
                        Gson gson = new Gson();
                        mTvNone.setVisibility(View.GONE);
                        GameDesk gameDesk = gson.fromJson(response, GameDesk.class);
                        if (gameDesk != null && gameDesk.getResult() != null && gameDesk.getResult().size() != 0) {
                            mList.addAll(gameDesk.getResult());
                            sortList();
                        } else {
                            if (isFirst) {
                                mTvNone.setText("无数据");
                                mTvNone.setVisibility(View.VISIBLE);
                            } else {
                                mListView.addFooterView(mFoot);
                                isFoot = true;
                            }
                        }

                        isFirst = false;

                    } else {
                        if (isFirst) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String msg = jsonObject.getString("Msg");
                                if (!TextUtils.isEmpty(msg)) {
                                    mTvNone.setText(msg);
                                    mTvNone.setVisibility(View.VISIBLE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    isLoading = false;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    isLoading = false;
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

    //数据排序分类
    private void sortList() {
        Collections.sort(mList, new Comparator<GameDesk.ResultBean>() {
            @Override
            public int compare(GameDesk.ResultBean lhs, GameDesk.ResultBean rhs) {
                String lstartTime = lhs.getStartTime();
                String rstartTime = rhs.getStartTime();
                Date ldate = DateUtils.getDate(lstartTime, "yyyy-MM-dd HH:mm:ss");
                Date rdate = DateUtils.getDate(rstartTime, "yyyy-MM-dd HH:mm:ss");
                if (ldate.before(rdate))
                    return 1;
                else
                    return -1;
            }
        });

        for (GameDesk.ResultBean resultBean : mList) {
            String startTime = resultBean.getStartTime();
            String substring = startTime.substring(0, 10);
            if (!mDateList.contains(substring)) {
                ArrayList<GameDesk.ResultBean> list1 = new ArrayList<>();
                list1.add(resultBean);
                mDateList.add(substring);
                mMap.put(substring, list1);
            } else {
                ArrayList<GameDesk.ResultBean> list = mMap.get(substring);
                list.add(resultBean);
                mMap.put(substring, list);
            }
        }

        mMyAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class MyAdapter extends MyBaseAdapter {

        public MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.item_record_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String s = mDateList.get(position);
            String substring = s.substring(5, 10);
            viewHolder.mTvDate.setText(substring);
            ArrayList<GameDesk.ResultBean> list = mMap.get(s);
            InnerAdapter innerAdapter = new InnerAdapter(list);
            viewHolder.mListView.setAdapter(innerAdapter);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tv_date)
            TextView mTvDate;
            @Bind(R.id.list_view)
            NoSlidingListView mListView;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    class InnerAdapter extends MyBaseAdapter {

        InnerAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.item_record_detail_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            GameDesk.ResultBean resultBean = (GameDesk.ResultBean) mDataSource.get(position);
            String headImg = mUser.getHeadImg();
            if (!TextUtils.isEmpty(headImg)){
                String loadImageUrl = ImageEngine.getLoadImageUrl(mActivity, headImg, 60, 60);
                Picasso.with(mActivity).load(loadImageUrl).into(viewHolder.mRoundedImageView);
            }

            String nickName = mUser.getNickName();
            if (!TextUtils.isEmpty(nickName))
                viewHolder.mTvName.setText(nickName);

            boolean userWin = resultBean.isUserWin();
            if (userWin)
                viewHolder.mIvResult.setImageResource(R.mipmap.win);
            else
                viewHolder.mIvResult.setImageResource(R.mipmap.lose);


            String gamePay = resultBean.getGamePay();
            if (!TextUtils.isEmpty(gamePay))
                viewHolder.mTvPrice.setText(gamePay);

            String slogan = mUser.getSlogan();
            if (!TextUtils.isEmpty(slogan))
                viewHolder.mTvDescription.setText(slogan);

            String startTime = resultBean.getStartTime();

            if (!TextUtils.isEmpty(startTime)) {
                String substring = startTime.substring(0, 10);
                viewHolder.mTvGameTime.setText(substring);
            }


            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.roundedImageView)
            RoundedImageView mRoundedImageView;
            @Bind(R.id.tv_name)
            TextView mTvName;
            @Bind(R.id.tv_description)
            TextView mTvDescription;
            @Bind(R.id.iv_result)
            ImageView mIvResult;
            @Bind(R.id.tv_price)
            TextView mTvPrice;
            @Bind(R.id.tv_game_time)
            TextView mTvGameTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }


}
