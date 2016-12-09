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
import com.chinalooke.yuwan.activity.MainActivity;
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
import com.chinalooke.yuwan.view.ExpandTabView;
import com.chinalooke.yuwan.view.RecycleViewDivider;
import com.chinalooke.yuwan.view.ViewLeft;
import com.chinalooke.yuwan.view.ViewMiddle;
import com.chinalooke.yuwan.view.ViewRight;
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
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    @Bind(R.id.expandTabView)
    ExpandTabView expandTabView;


    private RequestQueue mQueue;
    private Toast mToast;
    private int mWidth;
    private List<View> mAdList = new ArrayList<>();
    private List<Advertisement.ResultBean> mShowAd = new ArrayList<>();
    private int mCurrentPage;
    private QuickAdapter mAdapter;
    private boolean isFirst = true;
    private boolean isFresh = false;
    private LoginUser.ResultBean user;
    private long refreshLastClickTime = 0;
    private long itemLastClickTime = 0;
    private ArrayList<View> mViewArray = new ArrayList<>();
    private MainActivity mActivity;
    private ViewLeft mViewLeft;
    private ViewMiddle mViewMiddle;
    private ViewRight mViewRight;
    private List<GameDesk.ResultBean> mDeskList = new ArrayList<>();
    private List<GameDesk.ResultBean> mDeskListCache = new ArrayList<>();
    private int CURRENT_STATUS;
    private int CURRENT_TYPE;
    private int PAGE = 1;

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
        mActivity = (MainActivity) getActivity();
        initView();
        initMenuData();
        initEvent();
        getGameDeskListWithStatus();
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
                    PAGE = 1;
                    getGameDeskListWithStatus();
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
        mAdapter.setOnRecyclerViewItemClickListener(new MyOnRecyclerViewItemClickListener());

        //recycleView滚动监听
        mRecyclerView.addOnScrollListener(new MyOnScrollListener());

        mViewLeft.setOnSelectListener(new ViewLeft.OnSelectListener() {

            @Override
            public void getValue(String distance, String showText, int position) {
                onRefresh(mViewLeft, showText);
                isFirst = true;
                CURRENT_TYPE = position;
                PAGE = 1;
                mDeskList.clear();
                getGameDeskListWithStatus();
            }
        });


        mViewRight.setOnSelectListener(new ViewRight.OnSelectListener() {
            @Override
            public void getValue(String distance, String showText, int position) {
                onRefresh(mViewRight, showText);
                CURRENT_STATUS = position;
                mDeskList.clear();
                PAGE = 1;
                getGameDeskListWithStatus();
                isFirst = true;
            }
        });

    }

    class MyOnRecyclerViewItemClickListener implements BaseQuickAdapter.OnRecyclerViewItemClickListener {

        @Override
        public void onItemClick(View view, int i) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - itemLastClickTime > 2000) {
                itemLastClickTime = currentTime;
                if (user == null) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    interItem(i);
                }
            }
        }
    }

    class MyOnScrollListener extends RecyclerView.OnScrollListener {


        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (isSlideToBottom(recyclerView)) {
                loadMore();
            }
        }

    }

    private void loadMore() {
        PAGE++;
        getGameDeskListWithStatus();
    }

    private void interItem(int i) {
        GameDesk.ResultBean resultBean = mDeskList.get(i);
        Intent intent = new Intent(mActivity, GameDeskActivity.class);
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
        return visibleItemCount >= 0 && lastVisibleItemPosition == totalItemCount - 1 && state == RecyclerView.SCROLL_STATE_IDLE;
    }

    //初始化下拉列表数据
    private void initMenuData() {
        mViewLeft = new ViewLeft(mActivity);
        mViewMiddle = new ViewMiddle(mActivity);
        mViewRight = new ViewRight(mActivity);
        initVaule();
    }

    private void initVaule() {
        mViewArray.add(mViewLeft);
        mViewArray.add(mViewRight);
        mViewArray.add(mViewMiddle);
        ArrayList<String> mTextArray = new ArrayList<>();
        mTextArray.add("归属");
        mTextArray.add("状态");
        mTextArray.add("游戏");
        expandTabView.setValue(mTextArray, mViewArray);//将三个下拉列表设置进去
        expandTabView.setTitle(mViewLeft.getShowText(), 0);
        expandTabView.setTitle(mViewRight.getShowText(), 1);
        expandTabView.setTitle(mViewMiddle.getShowText(), 2);
    }


    private void onRefresh(View view, String showText) {
        expandTabView.onPressBack();
        int position = getPositon(view);
        if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
            expandTabView.setTitle(showText, position);
        }
    }

    private int getPositon(View tView) {
        for (int i = 0; i < mViewArray.size(); i++) {
            if (mViewArray.get(i) == tView) {
                return i;
            }
        }
        return -1;
    }

    //按状态取游戏桌列表
    private void getGameDeskListWithStatus() {
        final String uri = Constant.HOST + "getGameDeskListWithStatus&gameStatus=" + CURRENT_STATUS + "&pageNo=" + PAGE + "&pageSize=5";
        if (NetUtil.is_Network_Available(mActivity)) {
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
                                    if (isFresh)
                                        mDeskList.clear();
                                    switch (CURRENT_TYPE) {
                                        case 0:
                                            mDeskList.addAll(result);
                                            break;
                                        case 1:
                                            List<GameDesk.ResultBean> list = new ArrayList<>();
                                            for (GameDesk.ResultBean resultBean : result) {
                                                String ownerName = resultBean.getOwnerName();
                                                if ("官方".equals(ownerName)) {
                                                    list.add(resultBean);
                                                }
                                            }
                                            mDeskList.addAll(list);
                                            if (mDeskList.size() < 5)
                                                loadMore();
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
                            mAdapter.notifyDataSetChanged();
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
        String longitude = PreferenceUtils.getPrefString(mActivity, "longitude", "");
        String latitude = PreferenceUtils.getPrefString(mActivity, "latitude", "");
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
                ImageView imageView = new ImageView(mActivity);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(mActivity).load(img + "?imageView2/1/w/" + mWidth + "/h/" + MyUtils.Dp2Px(mActivity, 180)).into(imageView);
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
        MyLinearLayoutManager myLinearLayoutManager = new MyLinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, true);
        mAdapter = new QuickAdapter(R.layout.item_zc_listview, mDeskList);
        RecycleViewDivider recycleViewDivider = new RecycleViewDivider(mActivity, LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.line_color));
        myLinearLayoutManager.setReverseLayout(false);
        myLinearLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(myLinearLayoutManager);
        mRecyclerView.addItemDecoration(recycleViewDivider);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.iv_search, R.id.tv_search, R.id.iv_qcode
    })
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
//                case R.id.rl_yz:
//                    setRecyclerView(0);
//                    setIconWordColor(0);
//                    mCurrentPage = 0;
//                    PAGE = 0;
//                    mAdapter.notifyDataSetChanged();
//                    break;
//                case R.id.rl_jx:
//                    setRecyclerView(1);
//                    setIconWordColor(1);
//                    mCurrentPage = 1;
//                    PAGE = 0;
//                    mJxAdapter.notifyDataSetChanged();
//                    break;
//                case R.id.rl_js:
//                    setRecyclerView(2);
//                    setIconWordColor(2);
//                    mCurrentPage = 2;
//                    PAGE = 0;
//                    mJsAdapter.notifyDataSetChanged();
//                    break;
                case R.id.iv_search:
                    startActivity(new Intent(mActivity, SearchActivity.class));
                    break;
                case R.id.tv_search:
                    startActivity(new Intent(mActivity, SearchActivity.class));
                    break;
                case R.id.iv_qcode:
                    break;
            }
        }
    }


    public class QuickAdapter extends BaseQuickAdapter<GameDesk.ResultBean> {

        QuickAdapter(int layoutResId, List<GameDesk.ResultBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, GameDesk.ResultBean item) {
            String ownerName = item.getOwnerName();
            if (!TextUtils.isEmpty(ownerName)) {
                helper.setText(R.id.name, ownerName);
                if ("官方".equals(ownerName)) {
                    helper.setText(R.id.tv_owner, "官方");
                    helper.setBackgroundRes(R.id.tv_owner, R.mipmap.gf_zc)
                            .setVisible(R.id.tv_location, true);
                } else {
                    helper.setText(R.id.tv_owner, "个人");
                    helper.setBackgroundRes(R.id.tv_owner, R.mipmap.gr_zc).setVisible(R.id.tv_location, false);
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

            switch (CURRENT_STATUS) {
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
            if (!TextUtils.isEmpty(gameImage)) {
                String url = gameImage + "?imageView2/1/w/225/h/172";
                Picasso.with(mContext).load(url).into((ImageView) helper.getView(R.id.image));
            }

            String curPlayNum = item.getCurPlayNum();
            String playerNum = item.getPlayerNum();
            if (!TextUtils.isEmpty(curPlayNum) && !TextUtils.isEmpty(playerNum))
                helper.setText(R.id.tv_people, curPlayNum + "/" + playerNum);
        }

    }
}



