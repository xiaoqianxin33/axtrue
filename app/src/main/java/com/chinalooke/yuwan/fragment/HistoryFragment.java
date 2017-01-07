package com.chinalooke.yuwan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.MainActivity;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.GameDesk;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 历史界面
 * Created by xiao on 2016/12/14.
 */

public class HistoryFragment extends Fragment {

    @Bind(R.id.iv_arrow_head)
    ImageView mIvArrowHead;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.listView)
    ListView mListView;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    private MainActivity mActivity;
    private LoginUser.ResultBean mUser;
    private int PAGE;
    private RequestQueue mQueue;
    private List<GameDesk.ResultBean> mDeskList = new ArrayList<>();
    private boolean isFresh = false;
    private boolean isFirst = true;
    private View mFoot;
    private boolean isLoading = false;
    private MyAdapter mMyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(mActivity, LoginUserInfoUtils.KEY);
        mQueue = YuwanApplication.getQueue();
        mFoot = View.inflate(mActivity, R.layout.foot, null);
        initView();
        initEvent();
    }

    private void initEvent() {
        mSr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isFresh = true;
                isFirst = true;
                PAGE = 1;
                getGameDeskListWithStatus();
                mSr.setRefreshing(false);
                mListView.removeFooterView(mFoot);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mListView != null && mListView.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    mSr.setEnabled(enable);
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                    loadMore();
                }
            }
        });
    }

    //加载更多
    private void loadMore() {
        isLoading = true;
        PAGE++;
        getGameDeskListWithStatus();
    }

    //获得网吧结束的游戏桌
    private void getGameDeskListWithStatus() {
        String url = Constant.HOST + "getGameDeskListWithStatus&gameStatus=2" + "&pageNo=" + PAGE + "&pageSize=5&netbarId=" + mUser.getNetBarId();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (mPbLoad != null)
                    mPbLoad.setVisibility(View.GONE);
                if (AnalysisJSON.analysisJson(response)) {
                    if (mTvNone != null)
                        mTvNone.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    GameDesk gameDesk = gson.fromJson(response, GameDesk.class);
                    if (gameDesk != null) {
                        List<GameDesk.ResultBean> result = gameDesk.getResult();
                        if (result != null && result.size() != 0) {
                            if (isFresh)
                                mDeskList.clear();
                            mDeskList.addAll(result);
                            mMyAdapter.notifyDataSetChanged();
                        } else {
                            if (isFirst) {
                                mTvNone.setVisibility(View.VISIBLE);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String msg = jsonObject.getString("Msg");
                                    if (!TextUtils.isEmpty(msg))
                                        mTvNone.setText(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                mListView.addFooterView(mFoot);
                            }
                        }
                    }
                    isLoading = false;
                } else {
                    if (isFirst) {
                        mTvNone.setVisibility(View.VISIBLE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            if (!TextUtils.isEmpty(msg))
                                mTvNone.setText(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mListView.addFooterView(mFoot);
                    }
                }
                isFresh = false;
                isFirst = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mPbLoad != null)
                    mPbLoad.setVisibility(View.GONE);
                mTvNone.setText("服务器抽风了，请稍后重试");
                mTvNone.setVisibility(View.VISIBLE);
            }
        });

        mQueue.add(request);

    }

    private void initView() {
        mTvTitle.setText("历史");
        mIvArrowHead.setVisibility(View.GONE);
        mSr.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mMyAdapter = new MyAdapter(mDeskList);
        mListView.setAdapter(mMyAdapter);
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
                convertView = View.inflate(mActivity, R.layout.item_history_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            GameDesk.ResultBean resultBean = mDeskList.get(position);
            String gameImage = resultBean.getGameImage();
            if (!TextUtils.isEmpty(gameImage)) {
                String loadImageUrl = ImageEngine.getLoadImageUrl(mActivity, gameImage, 226, 172);
                Picasso.with(mActivity).load(loadImageUrl).into(viewHolder.mRoundedImageView);
            }
            String startTime = resultBean.getStartTime();
            if (!TextUtils.isEmpty(startTime)) {
                viewHolder.mTvTime.setText(getString(R.string.start_time, startTime.substring(5)));
            }

            List<GameDesk.ResultBean.Winer> winers = resultBean.getWiners();
            if (winers != null) {
                for (GameDesk.ResultBean.Winer winer : winers) {
                    String nickName = winer.getNickName();
                    String rating = winer.getRating();
                    String money = winer.getMoney();
                    TextView textView = new TextView(mActivity);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    textView.setTextSize(32);
                    textView.setText("第" + rating + "名： " + nickName + "  奖金:" + money);
                    viewHolder.mLinearLayout.addView(textView);
                }
            }

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.roundedImageView)
            RoundedImageView mRoundedImageView;
            @Bind(R.id.tv_time)
            TextView mTvTime;
            @Bind(R.id.ll_rating)
            LinearLayout mLinearLayout;


            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

}
