package com.chinalooke.yuwan.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.GameDeskActivity;
import com.chinalooke.yuwan.activity.LoginActivity;
import com.chinalooke.yuwan.activity.SearchActivity;
import com.chinalooke.yuwan.bean.Advertisement;
import com.chinalooke.yuwan.bean.GameDesk;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.constant.MyLinearLayoutManager;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.chinalooke.yuwan.view.RecycleViewDivider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;

import static com.chinalooke.yuwan.constant.Constant.MIN_CLICK_DELAY_TIME;
import static com.chinalooke.yuwan.constant.Constant.lastClickTime;

public class BattleFieldFragment extends Fragment {

    @Bind(R.id.banner)
    BGABanner mBanner;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    @Bind(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.recyclerView1)
    RecyclerView mRecyclerView1;
    @Bind(R.id.recyclerView2)
    RecyclerView mRecyclerView2;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    @Bind(R.id.tv_yz)
    TextView mTvYz;
    @Bind(R.id.tv_jx)
    TextView mTvJx;
    @Bind(R.id.tv_js)
    TextView mTvJs;

    private RequestQueue mQueue;
    private Toast mToast;
    private int mWidth;
    private List<View> mAdList = new ArrayList<>();
    private List<Advertisement.ResultBean> mShowAd = new ArrayList<>();
    private List<GameDesk.ResultBean> mYzList = new ArrayList<>();
    private List<GameDesk.ResultBean> mJxList = new ArrayList<>();
    private List<GameDesk.ResultBean> mJsList = new ArrayList<>();
    private int YZPAGE = 1;
    private int JXPAGE = 1;
    private int JSPAGE = 1;
    private int mCurrentPage;
    private QuickAdapter mYzAdapter;
    private QuickAdapter mJxAdapter;
    private QuickAdapter mJsAdapter;
    private boolean isFirst = true;
    private boolean isFresh = false;
    private LoginUser.ResultBean user;
    private long refreshLastClickTime = 0;
    private long itemLastClickTime = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battlefield, container, false);
        ButterKnife.bind(this, view);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
        user = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        initView();
        initData();
        initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        user = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
    }


    private void initEvent() {
        //banner的item点击事件
        mBanner.setOnItemClickListener(new BGABanner.OnItemClickListener() {
            @Override
            public void onBannerItemClick(BGABanner banner, View view, Object model, int position) {
                Advertisement.ResultBean resultBean = mShowAd.get(position);
            }
        });

        //swipeRefreshLayout下拉刷新事件
        mSr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - refreshLastClickTime > 3000) {
                    refreshLastClickTime = currentTime;
                    isFresh = true;
                    switch (mCurrentPage) {
                        case 0:
                            YZPAGE = 0;
                            getGameDeskListWithStatus(0, YZPAGE);
                            break;
                        case 1:
                            JXPAGE = 0;
                            getGameDeskListWithStatus(0, JXPAGE);
                            break;
                        case 2:
                            JSPAGE = 0;
                            getGameDeskListWithStatus(0, JSPAGE);
                            break;
                    }
                    mSr.setRefreshing(false);
                } else {
                    mSr.setRefreshing(false);
                }
            }
        });

        //appbarLayout与SwipeRefreshLayout冲突解决
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    mSr.setEnabled(true);
                } else {
                    mSr.setEnabled(false);
                }
            }
        });


        //recycleView item 点击事件
        mYzAdapter.setOnRecyclerViewItemClickListener(new MyOnRecyclerViewItemClickListener(0));
        mJxAdapter.setOnRecyclerViewItemClickListener(new MyOnRecyclerViewItemClickListener(1));
        mJsAdapter.setOnRecyclerViewItemClickListener(new MyOnRecyclerViewItemClickListener(2));

        //recycleView滚动监听
        mRecyclerView.addOnScrollListener(new MyOnScrollListener(0));
        mRecyclerView1.addOnScrollListener(new MyOnScrollListener(1));
        mRecyclerView2.addOnScrollListener(new MyOnScrollListener(2));
    }

    class MyOnRecyclerViewItemClickListener implements BaseQuickAdapter.OnRecyclerViewItemClickListener {
        private int mInt;

        MyOnRecyclerViewItemClickListener(int position) {
            this.mInt = position;
        }

        @Override
        public void onItemClick(View view, int i) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - itemLastClickTime > 2000) {
                itemLastClickTime = currentTime;
                if (user == null) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    switch (mInt) {
                        case 0:
                            interItem(i, mYzList);
                            break;
                        case 1:
                            interItem(i, mJxList);
                            break;
                        case 2:
                            interItem(i, mJsList);
                            break;
                    }
                }
            }
        }
    }

    class MyOnScrollListener extends RecyclerView.OnScrollListener {

        private int mInt;

        MyOnScrollListener(int position) {
            this.mInt = position;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (isSlideToBottom(recyclerView)) {
                loadMore(mInt);
            }
        }

    }

    private void loadMore(int anInt) {
        Log.e("TAG", "loadMore");
        switch (anInt) {
            case 0:
                YZPAGE++;
                getGameDeskListWithStatus(0, YZPAGE);
                break;
            case 1:
                JXPAGE++;
                getGameDeskListWithStatus(1, JXPAGE);
                break;
            case 2:
                JSPAGE++;
                getGameDeskListWithStatus(2, JSPAGE);
                break;
        }
    }

    private void interItem(int i, List<GameDesk.ResultBean> list) {
        GameDesk.ResultBean resultBean = list.get(i);
        Intent intent = new Intent(getActivity(), GameDeskActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameDesk", resultBean);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //判断recycleView是否已经滑到底部
    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        return visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == RecyclerView.SCROLL_STATE_IDLE;
    }

    private void initData() {
        getGameDeskListWithStatus(0, YZPAGE);
        getGameDeskListWithStatus(1, JXPAGE);
        getGameDeskListWithStatus(2, JSPAGE);

    }

    //按状态取游戏桌列表
    private void getGameDeskListWithStatus(final int status, final int page) {
        int PAGE_SIZE = 5;
        final String uri = Constant.HOST + "getGameDeskListWithStatus&gameStatus=" + status + "&pageNo=" + page + "&pageSize=" + PAGE_SIZE;
        if (NetUtil.is_Network_Available(getActivity())) {
            StringRequest stringRequest = new StringRequest(uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mPbLoad.setVisibility(View.GONE);
                            if (AnalysisJSON.analysisJson(response)) {
                                mTvNone.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                Type type = new TypeToken<GameDesk>() {
                                }.getType();
                                GameDesk gameDesk = gson.fromJson(response, type);
                                List<GameDesk.ResultBean> result = gameDesk.getResult();
                                if (result != null && result.size() != 0) {
                                    switch (status) {
                                        case 0:
                                            if (isFresh)
                                                mYzList.clear();
                                            mYzList.addAll(result);
                                            mYzAdapter.notifyDataSetChanged();
                                            break;
                                        case 1:
                                            if (isFresh)
                                                mJxList.clear();
                                            mJxList.addAll(result);
                                            mJxAdapter.notifyDataSetChanged();
                                            break;
                                        case 2:
                                            if (isFresh)
                                                mJsList.clear();
                                            mJsList.addAll(result);
                                            mJsAdapter.notifyDataSetChanged();
                                            break;
                                    }
                                } else {
                                    if (isFirst) {
                                        MyUtils.showMsg(mToast, response);
                                    }
                                }
                            } else {
                                if (isFirst) {
                                    MyUtils.showMsg(mToast, response);
                                }
                            }
                            isFresh = false;
                            isFirst = false;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mPbLoad.setVisibility(View.GONE);
                    if (isFirst)
                        mTvNone.setVisibility(View.VISIBLE);
                    isFresh = false;
                }
            });
            mQueue.add(stringRequest);
        } else {
            mPbLoad.setVisibility(View.GONE);
            mTvNone.setText("网络未连接");
            isFresh = false;
        }
    }

    //获得最近的广告
    public void getADListWithGPS() {
        String longitude = PreferenceUtils.getPrefString(getActivity(), "longitude", "");
        String latitude = PreferenceUtils.getPrefString(getActivity(), "latitude", "");
        if (!TextUtils.isEmpty(longitude) && !TextUtils.isEmpty(latitude)) {
            double lng = Double.parseDouble(longitude);
            double lat = Double.parseDouble(latitude);
            String uri = Constant.HOST + "getADListWithGPS&lng=" + lng + "&lat=" + lat;
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (AnalysisJSON.analysisJson(response)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Advertisement>() {
                        }.getType();
                        Advertisement advertisement = gson.fromJson(response, type);
                        setBanner(advertisement);
                    }
                }
            }, null);

            mQueue.add(request);
        }
    }


    //设置顶部广告条
    private void setBanner(Advertisement advertisement) {
        mAdList.clear();
        List<Advertisement.ResultBean> result = advertisement.getResult();
        for (Advertisement.ResultBean resultBean : result) {
            List<Advertisement.ResultBean.ImagesBean> images = resultBean.getImages();
            if (images.size() != 0) {
                Advertisement.ResultBean.ImagesBean imagesBean = images.get(0);
                String img = imagesBean.getImg();
                ImageView imageView = new ImageView(getActivity());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(getActivity()).load(img).resize(MyUtils.Dp2Px(getActivity(), mWidth), 340)
                        .centerCrop().into(imageView);
                mShowAd.add(resultBean);
                mAdList.add(imageView);
            }
        }

        if (mBanner != null) {
            mBanner.setData(mAdList);
        }
    }

    private void initView() {
        mSr.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        MyLinearLayoutManager myLinearLayoutManager = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        MyLinearLayoutManager myLinearLayoutManager1 = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        MyLinearLayoutManager myLinearLayoutManager2 = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        mYzAdapter = new QuickAdapter(R.layout.item_zc_listview, mYzList, 0);
        mJxAdapter = new QuickAdapter(R.layout.item_zc_listview, mJxList, 1);
        mJsAdapter = new QuickAdapter(R.layout.item_zc_listview, mJsList, 2);
        RecycleViewDivider recycleViewDivider = new RecycleViewDivider(getActivity(), LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.line_color));
        myLinearLayoutManager.setReverseLayout(false);
        myLinearLayoutManager.setAutoMeasureEnabled(true);
        myLinearLayoutManager2.setAutoMeasureEnabled(true);
        myLinearLayoutManager1.setAutoMeasureEnabled(true);
        myLinearLayoutManager1.setReverseLayout(false);
        myLinearLayoutManager2.setReverseLayout(false);
        mRecyclerView.setLayoutManager(myLinearLayoutManager);
        mRecyclerView1.setLayoutManager(myLinearLayoutManager1);
        mRecyclerView2.setLayoutManager(myLinearLayoutManager2);
        mRecyclerView.addItemDecoration(recycleViewDivider);
        mRecyclerView1.addItemDecoration(recycleViewDivider);
        mRecyclerView2.addItemDecoration(recycleViewDivider);
        mRecyclerView.setAdapter(mYzAdapter);
        mRecyclerView1.setAdapter(mJxAdapter);
        mRecyclerView2.setAdapter(mJsAdapter);
        setIconWordColor(0);
        setRecyclerView(0);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.iv_search, R.id.tv_search, R.id.iv_qcode, R.id.rl_yz, R.id.rl_jx, R.id.rl_js
    })
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.rl_yz:
                    setRecyclerView(0);
                    setIconWordColor(0);
                    mCurrentPage = 0;
                    YZPAGE = 0;
                    mYzAdapter.notifyDataSetChanged();
                    break;
                case R.id.rl_jx:
                    setRecyclerView(1);
                    setIconWordColor(1);
                    mCurrentPage = 1;
                    YZPAGE = 0;
                    mJxAdapter.notifyDataSetChanged();
                    break;
                case R.id.rl_js:
                    setRecyclerView(2);
                    setIconWordColor(2);
                    mCurrentPage = 2;
                    YZPAGE = 0;
                    mJsAdapter.notifyDataSetChanged();
                    break;
                case R.id.iv_search:
                    startActivity(new Intent(getActivity(), SearchActivity.class));
                    break;
                case R.id.tv_search:
                    startActivity(new Intent(getActivity(), SearchActivity.class));
                    break;
                case R.id.iv_qcode:
                    break;
            }
        }
    }

    private void setRecyclerView(int i) {
        mRecyclerView.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
        mRecyclerView1.setVisibility(i == 1 ? View.VISIBLE : View.GONE);
        mRecyclerView2.setVisibility(i == 2 ? View.VISIBLE : View.GONE);
    }

    private void setIconWordColor(int i) {
        mTvYz.setSelected(i == 0);
        mTvJx.setSelected(i == 1);
        mTvJs.setSelected(i == 2);
    }

    public class QuickAdapter extends BaseQuickAdapter<GameDesk.ResultBean> {
        private int mPage;

        QuickAdapter(int layoutResId, List<GameDesk.ResultBean> data, int page) {
            super(layoutResId, data);
            this.mPage = page;
        }

        @Override
        protected void convert(BaseViewHolder helper, GameDesk.ResultBean item) {
            String ownerName = item.getOwnerName();
            if (!TextUtils.isEmpty(ownerName)) {
                helper.setText(R.id.name, ownerName);
                if ("官方".equals(ownerName)) {
                    helper.setText(R.id.tv_owner, "官方");
                    helper.setBackgroundRes(R.id.tv_owner, R.mipmap.gf_zc);
                } else {
                    helper.setText(R.id.tv_owner, "个人");
                    helper.setBackgroundRes(R.id.tv_owner, R.mipmap.gr_zc);
                }
            }

            String gamePay = item.getGamePay();
            if (!TextUtils.isEmpty(gamePay)) {
                double pay = Double.parseDouble(gamePay);
                long round = Math.round(pay);
                helper.setText(R.id.tv_price, round + "元");
            }

            String netBarName = item.getNetBarName();
            if (!TextUtils.isEmpty(netBarName)) {
                helper.setText(R.id.tv_location, netBarName);
            }
            String startTime = item.getStartTime();
            Date date = DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss");
            Date currentDate = new Date();
            assert date != null;

            switch (mPage) {
                case 0:
                    helper.setText(R.id.tv_status, "约战中")
                            .setBackgroundRes(R.id.tv_status, R.mipmap.yzz);
                    if (!TextUtils.isEmpty(startTime)) {
                        long l = date.getTime() - currentDate.getTime();
                        if (l > 0) {
                            l = l / 1000;
                            long hour = l / 60 / 60;
                            long minute = (l - hour * 60 * 60) / 60;
                            long sec = (l - hour * 60 * 60) - minute * 60;
                            helper.setText(R.id.tv_time, hour + ":" + minute + ":" + sec);
                        } else {
                            helper.setText(R.id.tv_time, "00:00:00");
                        }
                    }
                    break;
                case 1:
                    helper.setText(R.id.tv_status, "进行中")
                            .setText(R.id.tv_apply, "已开战")
                            .setBackgroundRes(R.id.tv_status, R.mipmap.jxz);

                    if (!TextUtils.isEmpty(startTime)) {
                        long l = currentDate.getTime() - date.getTime();
                        if (l > 0) {
                            l = l / 1000;
                            long hour = l / 60 / 60;
                            long minute = (l - hour * 60 * 60) / 60;
                            long sec = (l - hour * 60 * 60) - minute * 60;
                            helper.setText(R.id.tv_time, hour + ":" + minute + ":" + sec);
                        } else {
                            helper.setText(R.id.tv_time, "00:00:00");
                        }
                    }
                    break;
                case 2:
                    String winnerTeam = item.getWinnerTeam();
                    if ("A".equals(winnerTeam)) {
                        helper.setText(R.id.tv_time, "约战方");
                    } else if ("B".equals(winnerTeam)) {
                        helper.setText(R.id.tv_time, "应战方");
                    }
                    helper.setText(R.id.tv_status, "已结束")
                            .setText(R.id.tv_apply, "获胜方")
                            .setBackgroundRes(R.id.tv_status, R.mipmap.yjs);
                    break;
            }

            String gameImage = item.getGameImage();
            if (!TextUtils.isEmpty(gameImage))
                Picasso.with(mContext).load(item.getGameImage()).into((ImageView) helper.getView(R.id.image));

            String curPlayNum = item.getCurPlayNum();
            String playerNum = item.getPlayerNum();
            if (!TextUtils.isEmpty(curPlayNum) && !TextUtils.isEmpty(playerNum))
                helper.setText(R.id.tv_people, curPlayNum + "/" + playerNum);
        }

    }
}



