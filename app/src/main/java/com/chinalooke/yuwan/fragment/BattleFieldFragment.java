package com.chinalooke.yuwan.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.constant.MyLinearLayoutManager;
import com.chinalooke.yuwan.model.Advertisement;
import com.chinalooke.yuwan.model.GameDesk;
import com.chinalooke.yuwan.model.GameDeskDetails;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;

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
    FamiliarRefreshRecyclerView mRecyclerView;
    @Bind(R.id.recyclerView1)
    FamiliarRefreshRecyclerView mRecyclerView1;
    @Bind(R.id.recyclerView2)
    FamiliarRefreshRecyclerView mRecyclerView2;
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
    private int mPage = 1;
    private boolean isLoading = false;
    private int mCurrentPage;
    private QuickAdapter mYzAdapter;
    private QuickAdapter mJxAdapter;
    private QuickAdapter mJsAdapter;
    private boolean isFirst = true;
    private int mCurrentCounter;
    private int PAGE_SIZE = 5;
    private long lastTime = 0;
    private boolean isFresh = false;
    private View mFootView;
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
                            mPage = 0;
                            initData();
                            break;
                        case 1:
                            mPage = 0;
                            initData();
                            break;
                        case 2:
                            mPage = 0;
                            initData();
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
        BaseQuickAdapter.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener = new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - itemLastClickTime > 2000) {
                    itemLastClickTime = currentTime;
                    if (user == null) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    } else {
                        switch (mCurrentPage) {
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
        };
        mYzAdapter.setOnRecyclerViewItemClickListener(onRecyclerViewItemClickListener);
        mJxAdapter.setOnRecyclerViewItemClickListener(onRecyclerViewItemClickListener);
        mJsAdapter.setOnRecyclerViewItemClickListener(onRecyclerViewItemClickListener);

        //recyclerView 加载更多
        FamiliarRefreshRecyclerView.OnLoadMoreListener onLoadMoreListener = new FamiliarRefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("TAG", "onLoadMore");
                switch (mCurrentPage) {
                    case 0:
                        getGameDeskListWithStatus(0);
                        break;
                    case 1:
                        getGameDeskListWithStatus(1);
                        break;
                    case 2:
                        getGameDeskListWithStatus(2);
                        break;
                }
            }
        };
        mRecyclerView.setOnLoadMoreListener(onLoadMoreListener);
        mRecyclerView1.setOnLoadMoreListener(onLoadMoreListener);
        mRecyclerView2.setOnLoadMoreListener(onLoadMoreListener);


    }

    private void interItem(int i, List<GameDesk.ResultBean> list) {
        GameDesk.ResultBean resultBean = list.get(i);
        String gameDeskId = resultBean.getGameDeskId();
        getGameDeskWithId(gameDeskId, null, resultBean);
    }

    private class MyRequestLoadMoreListener implements BaseQuickAdapter.RequestLoadMoreListener {
        private int PAGE;

        MyRequestLoadMoreListener(int page) {
            this.PAGE = page;
        }

        @Override
        public void onLoadMoreRequested() {
            QuickAdapter quickAdapter = null;
            switch (PAGE) {
                case 0:
                    quickAdapter = mYzAdapter;
                    break;
                case 1:
                    quickAdapter = mJxAdapter;
                    break;
                case 2:
                    quickAdapter = mJsAdapter;
                    break;
            }
            mPage++;
            initData();
            final QuickAdapter finalQuickAdapter = quickAdapter;
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    assert finalQuickAdapter != null;
                    if (mCurrentCounter >= 0) {
                        finalQuickAdapter.notifyDataChangedAfterLoadMore(false);
                        finalQuickAdapter.addFooterView(mFootView);
                    } else {
                        finalQuickAdapter.notifyDataChangedAfterLoadMore(mYzList, true);
                        mCurrentCounter = finalQuickAdapter.getItemCount();
                    }
                }

            });
        }
    }

    private void initData() {
        if (isFirst) {
            getGameDeskListWithStatus(0);
            getGameDeskListWithStatus(1);
            getGameDeskListWithStatus(2);
        } else {
            getGameDeskListWithStatus(mCurrentPage);
        }
    }

    //按状态取游戏桌列表
    private void getGameDeskListWithStatus(final int status) {
        String uri = Constant.HOST + "getGameDeskListWithStatus&gameStatus=" + status + "&pageNo=" + mPage + "&pageSize=" + PAGE_SIZE;
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
                                    mTvNone.setVisibility(View.VISIBLE);
                                    MyUtils.showMsg(mToast, response);
                                } else {
                                    mYzAdapter.openLoadMore(false);
                                    mToast.setText("没有更多了");
                                    mToast.show();
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
                    mToast.setText("网络不给力，换个地方试试");
                    mToast.show();
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
            String uri = Constant.HOST + "getADListWithGPS&lng=" + lat + "&lat=" + lng;
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (AnalysisJSON.analysisJson(response)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Advertisement>() {
                        }.getType();
                        Advertisement advertisement = gson.fromJson(response, type);
                        setBanner(advertisement);
                    } else {
                        MyUtils.showMsg(mToast, response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mToast.setText("网络不给力啊，换个地方试试");
                    mToast.show();
                }
            });

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
        mYzAdapter = new QuickAdapter(R.layout.item_zc_listview, mYzList);
        mJxAdapter = new QuickAdapter(R.layout.item_zc_listview, mJxList);
        mJsAdapter = new QuickAdapter(R.layout.item_zc_listview, mJsList);
        myLinearLayoutManager.setReverseLayout(false);
        mRecyclerView.setAdapter(mYzAdapter);
        mRecyclerView1.setAdapter(mJxAdapter);
        mRecyclerView2.setAdapter(mJsAdapter);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView1.setPullRefreshEnabled(false);
        mRecyclerView2.setPullRefreshEnabled(false);
        mRecyclerView1.setLoadMoreEnabled(true);
        mRecyclerView2.setLoadMoreEnabled(true);
        setIconWordColor(0);
        setRecyclerView(0);
        mFootView = View.inflate(getActivity(), R.layout.foot, null);
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
                    mPage = 0;
                    mYzAdapter.notifyDataSetChanged();
                    break;
                case R.id.rl_jx:
                    setRecyclerView(1);
                    setIconWordColor(1);
                    mCurrentPage = 1;
                    mPage = 0;
                    mJxAdapter.notifyDataSetChanged();
                    break;
                case R.id.rl_js:
                    setRecyclerView(2);
                    setIconWordColor(2);
                    mCurrentPage = 2;
                    mPage = 0;
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

            switch (mCurrentPage) {
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

                    ;
                    break;
                case 2:
                    helper.setText(R.id.tv_status, "已结束")
                            .setText(R.id.tv_apply, "获胜方")
                            .setBackgroundRes(R.id.tv_status, R.mipmap.yjs);
                    break;
            }

            String gameDeskId = item.getGameDeskId();
            getGameDeskWithId(gameDeskId, helper, null);
            String gameImage = item.getGameImage();
            if (gameImage != null)
                Picasso.with(mContext).load(item.getGameImage()).into((ImageView) helper.getView(R.id.image));
        }

    }

    //按照游戏桌id取得游戏桌详情
    private void getGameDeskWithId(final String gameDeskId, final BaseViewHolder helper, final GameDesk.ResultBean resultBean) {

        StringRequest stringRequest = new StringRequest(Constant.HOST + "getGameDeskWithId&gameDeskId=" + gameDeskId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (AnalysisJSON.analysisJson(response)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<GameDeskDetails>() {
                            }.getType();
                            GameDeskDetails gameDesk = gson.fromJson(response, type);
                            if (gameDesk != null) {
                                int current = 0;
                                GameDeskDetails.ResultBean result = gameDesk.getResult();
                                List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> left = result.getPlayers().getLeft();
                                if (left != null) {
                                    current += left.size();
                                }
                                List<GameDeskDetails.ResultBean.PlayersBean.RightBean> right = result.getPlayers().getRight();
                                if (right != null) {
                                    current += right.size();
                                }
                                String peopleNumber = result.getPeopleNumber();
                                if (helper != null) {
                                    helper.setText(R.id.tv_people, current + "/" + peopleNumber);
                                    String winer = result.getWiner();
                                    if (!TextUtils.isEmpty(winer)) {
                                        switch (winer) {
                                            case "left":
                                                helper.setText(R.id.tv_time, "约战方");
                                                break;
                                            case "right":
                                                helper.setText(R.id.tv_time, "迎战方");
                                                break;
                                        }
                                    }
                                } else {
                                    Intent intent = new Intent(getActivity(), GameDeskActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("gameDeskDetails", gameDesk);
                                    bundle.putSerializable("gameDesk", resultBean);
                                    intent.putExtra("gameDeskId", gameDeskId);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                mToast.setText(jsonObject.getString("Msg"));
                                mToast.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mToast.setText("获取数据失败!");
                mToast.show();
            }
        });
        mQueue.add(stringRequest);
    }
}



