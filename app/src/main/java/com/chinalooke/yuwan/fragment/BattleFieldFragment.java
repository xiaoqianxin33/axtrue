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
import com.chinalooke.yuwan.view.RecycleViewDivider;
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
    private int PAGE_SIZE = 3;
    private long lastTime = 0;
    private boolean isFresh = false;
    private View mFootView;
    private LoginUser.ResultBean user;
    private String mOwnerName;


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
                if (currentTime - lastClickTime > 3000) {
                    lastClickTime = currentTime;
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
        mYzAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                if (user == null) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    switch (mCurrentPage) {
                        case 0:
                            GameDesk.ResultBean resultBean = mYzList.get(i);
                            String gameDeskId = resultBean.getGameDeskId();
                            getGameDeskWithId(gameDeskId, null);
                            break;
                        case 1:
                            GameDesk.ResultBean resultBean1 = mJxList.get(i);
                            String gameDeskId1 = resultBean1.getGameDeskId();
                            getGameDeskWithId(gameDeskId1, null);
                            break;
                        case 2:
                            GameDesk.ResultBean resultBean2 = mJsList.get(i);
                            String gameDeskId2 = resultBean2.getGameDeskId();
                            getGameDeskWithId(gameDeskId2, null);
                            break;
                    }
                }
            }
        });

        mYzAdapter.openLoadMore(PAGE_SIZE, true);
        mJxAdapter.openLoadMore(PAGE_SIZE, true);
        mJsAdapter.openLoadMore(PAGE_SIZE, true);

        mYzAdapter.setOnLoadMoreListener(new MyRequestLoadMoreListener(0));
        mJxAdapter.setOnLoadMoreListener(new MyRequestLoadMoreListener(1));
        mJsAdapter.setOnLoadMoreListener(new MyRequestLoadMoreListener(2));
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
            Log.e("TAG", "onLoadMoreRequested");
            final QuickAdapter finalQuickAdapter = quickAdapter;
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    assert finalQuickAdapter != null;
                    if (mCurrentCounter >= 0) {
                        finalQuickAdapter.notifyDataChangedAfterLoadMore(false);
                        finalQuickAdapter.addFooterView(mFootView);
                        Log.e("TAG", ">0");
                    } else {
                        finalQuickAdapter.notifyDataChangedAfterLoadMore(mYzList, true);
                        mCurrentCounter = finalQuickAdapter.getItemCount();
                        Log.e("TAG", "<0");
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
        mRecyclerView.setLayoutManager(myLinearLayoutManager);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL, 20, getResources().getColor(R.color.background)));
        mRecyclerView.setAdapter(mYzAdapter);
        setIconWordColor(0);
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
                    mJxAdapter.removeAllFooterView();
                    mJsAdapter.removeAllFooterView();
                    setIconWordColor(0);
                    mCurrentPage = 0;
                    mPage = 0;
                    mRecyclerView.setAdapter(mYzAdapter);
                    mYzAdapter.notifyDataSetChanged();
                    break;
                case R.id.rl_jx:
                    mYzAdapter.removeAllFooterView();
                    mJsAdapter.removeAllFooterView();
                    setIconWordColor(1);
                    mCurrentPage = 1;
                    mPage = 0;
                    mRecyclerView.setAdapter(mJxAdapter);
                    mJxAdapter.notifyDataSetChanged();
                    break;
                case R.id.rl_js:
                    mYzAdapter.removeAllFooterView();
                    mJxAdapter.removeAllFooterView();
                    setIconWordColor(2);
                    mCurrentPage = 2;
                    mPage = 0;
                    mRecyclerView.setAdapter(mJsAdapter);
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

            switch (mCurrentPage) {
                case 0:
                    helper.setText(R.id.tv_status, "约战中")
                            .setBackgroundRes(R.id.tv_status, R.mipmap.yzz);

                    String startTime = item.getStartTime();
                    if (!TextUtils.isEmpty(startTime)) {
                        Date date = DateUtils.getDate(startTime, "yyyy-MM-dd HH:mm:ss");
                        Date currentDate = new Date();
                        assert date != null;
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
                            .setBackgroundRes(R.id.tv_status, R.mipmap.jxz)
                            .setText(R.id.tv_time, "00:00:00");
                    ;
                    break;
                case 2:
                    helper.setText(R.id.tv_status, "已结束")
                            .setBackgroundRes(R.id.tv_status, R.mipmap.yjs)
                            .setText(R.id.tv_time, "00:00:00");
                    break;
            }

            mOwnerName = item.getOwnerName();
            String gameImage = item.getGameImage();
            if (gameImage != null)
                Picasso.with(mContext).load(item.getGameImage()).into((ImageView) helper.getView(R.id.image));
        }

    }

    //按照游戏桌id取得游戏桌详情
    private void getGameDeskWithId(final String gameDeskId, final BaseViewHolder helper) {
        StringRequest stringRequest = new StringRequest(Constant.HOST + "getGameDeskWithId&gameDeskId=" + gameDeskId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (AnalysisJSON.analysisJson(response)) {
                            Log.e("TAG", "getGameDeskWithId");
                            Gson gson = new Gson();
                            Type type = new TypeToken<GameDeskDetails>() {
                            }.getType();
                            GameDeskDetails gameDesk = gson.fromJson(response, type);
                            if (gameDesk != null) {
                                int current = 0;
                                List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> left = gameDesk.getResult().getPlayers().getLeft();
                                if (left != null) {
                                    current += left.size();
                                }
                                List<GameDeskDetails.ResultBean.PlayersBean.RightBean> right = gameDesk.getResult().getPlayers().getRight();
                                if (right != null) {
                                    current += right.size();
                                }
                                String peopleNumber = gameDesk.getResult().getPeopleNumber();
                                if (helper != null) {
                                    helper.setText(R.id.tv_people, current + "/" + peopleNumber);
                                } else {
                                    Intent intent = new Intent(getActivity(), GameDeskActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("gameDeskDetails", gameDesk);
                                    if (!TextUtils.isEmpty(mOwnerName))
                                        intent.putExtra("ownerName", mOwnerName);
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
                Log.e("TAG", "onErrorResponse");
                mToast.setText("获取数据失败!");
                mToast.show();
            }
        });
        mQueue.add(stringRequest);
    }
}



