package com.chinalooke.yuwan.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.SearchActivity;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.Advertisement;
import com.chinalooke.yuwan.model.GameDesk;
import com.chinalooke.yuwan.model.GameDeskDetails;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.chinalooke.yuwan.view.ScrollableViewGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;

public class BattleFieldFragment extends Fragment {


    @Bind(R.id.banner)
    BGABanner mBanner;
    @Bind(R.id.scrollable)
    ScrollableViewGroup mScrollableViewGroup;
    @Bind(R.id.listview_yz)
    ListView mListviewYz;
    @Bind(R.id.pb_load_yz)
    ProgressBar mPbLoadYz;
    @Bind(R.id.tv_none_yz)
    TextView mTvNoneYz;
    @Bind(R.id.listview_jx)
    ListView mListviewJx;
    @Bind(R.id.pb_load_jx)
    ProgressBar mPbLoadJx;
    @Bind(R.id.tv_none_jx)
    TextView mTvNoneJx;
    @Bind(R.id.listview_js)
    ListView mListviewJs;
    @Bind(R.id.pb_load_js)
    ProgressBar mPbLoadJs;
    @Bind(R.id.tv_none_js)
    TextView mTvNoneJs;
    @Bind(R.id.ll_gone)
    LinearLayout mLlGone;
    @Bind(R.id.scrollView)
    NestedScrollView mMyScrollView;
    @Bind(R.id.rl_search_title)
    RelativeLayout mRlSearchTitle;
    @Bind(R.id.rl_yz_g)
    RelativeLayout mRlYzG;
    @Bind(R.id.rl_jx_g)
    RelativeLayout mRlJxG;
    @Bind(R.id.rl_js_g)
    RelativeLayout mRlJsG;
    //    @Bind(R.id.sr)
//    SwipeRefreshLayout mSr;
    private RequestQueue mQueue;
    private Toast mToast;
    private int mWidth;
    private List<View> mAdList = new ArrayList<>();
    private List<Advertisement.ResultBean> mShowAd = new ArrayList<>();
    private List<GameDesk.ResultBean> mYzList = new ArrayList<>();
    private List<GameDesk.ResultBean> mJxList = new ArrayList<>();
    private List<GameDesk.ResultBean> mJsList = new ArrayList<>();
    private int mPage;
    private MyAdapter mYzAdapter;
    private MyAdapter mJxAdapter;
    private MyAdapter mJsAdapter;


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
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mBanner.setOnItemClickListener(new BGABanner.OnItemClickListener() {
            @Override
            public void onBannerItemClick(BGABanner banner, View view, Object model, int position) {
                Advertisement.ResultBean resultBean = mShowAd.get(position);
            }
        });

        mScrollableViewGroup.setOnCurrentViewChangedListener(new ScrollableViewGroup.OnCurrentViewChangedListener() {
            @Override
            public void onCurrentViewChanged(View view, int currentview) {

            }
        });

        AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (mListviewJs != null && view.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = view.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = view.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
//                mSr.setEnabled(enable);
            }
        };
        mListviewJs.setOnScrollListener(onScrollListener);
        mListviewJx.setOnScrollListener(onScrollListener);
        mListviewYz.setOnScrollListener(onScrollListener);

        mMyScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (mBanner != null) {
                    if (scrollY >= mBanner.getHeight() + mRlSearchTitle.getHeight()) {
                        mLlGone.setVisibility(View.VISIBLE);
                    } else {
                        mLlGone.setVisibility(View.GONE);
                    }
                }
            }
        });
        mMyScrollView.smoothScrollTo(0, 0);

        mScrollableViewGroup.setOnCurrentViewChangedListener(new ScrollableViewGroup.OnCurrentViewChangedListener() {
            @Override
            public void onCurrentViewChanged(View view, int currentview) {
                mMyScrollView.smoothScrollTo(0, 0);
            }
        });
    }

    private void initData() {
        getGameDeskListWithStatus(0, mPbLoadYz, mTvNoneYz);
        getGameDeskListWithStatus(1, mPbLoadJx, mTvNoneJx);
        getGameDeskListWithStatus(2, mPbLoadJs, mTvNoneJs);
    }

    //按状态取游戏桌列表
    private void getGameDeskListWithStatus(final int status, final ProgressBar mPbLoad, final TextView mTvNone) {
        String uri = Constant.HOST + "getGameDeskListWithStatus&gameStatus=" + status + "&pageNo=" + mPage + "&pageSize=5";
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
                                        mYzList.addAll(result);
                                        mYzAdapter.notifyDataSetChanged();
                                        break;
                                    case 1:
                                        mJxList.addAll(result);
                                        mJxAdapter.notifyDataSetChanged();
                                        break;
                                    case 2:
                                        mJsList.addAll(result);
                                        mJsAdapter.notifyDataSetChanged();
                                        break;
                                }

                            } else {
                                MyUtils.showMsg(mToast, response);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mPbLoad.setVisibility(View.GONE);
                    mToast.setText("网络不给力，换个地方试试");
                    mToast.show();
                }
            });

            mQueue.add(stringRequest);
        } else {
            mPbLoad.setVisibility(View.GONE);
            mTvNone.setText("网络未连接");
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
        mYzAdapter = new MyAdapter(mYzList, 0);
        mJxAdapter = new MyAdapter(mJxList, 1);
        mJsAdapter = new MyAdapter(mJsList, 2);
        mListviewYz.setAdapter(mYzAdapter);
        mListviewJx.setAdapter(mJxAdapter);
        mListviewJs.setAdapter(mJsAdapter);
//        mSr.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.iv_search, R.id.tv_search, R.id.iv_qcode, R.id.rl_yz, R.id.rl_jx, R.id.rl_js
            , R.id.rl_js_g, R.id.rl_jx_g, R.id.rl_yz_g})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_js_g:
                break;
            case R.id.rl_jx_g:
                break;
            case R.id.rl_yz_g:
                break;
            case R.id.rl_yz:
                mScrollableViewGroup.setCurrentView(0);
                break;
            case R.id.rl_jx:
                mScrollableViewGroup.setCurrentView(1);
                break;
            case R.id.rl_js:
                mScrollableViewGroup.setCurrentView(2);
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


    private class MyAdapter extends MyBaseAdapter {

        MyAdapter(List dataSource, int status) {
            super(dataSource, status);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_zc_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            GameDesk.ResultBean resultBean = (GameDesk.ResultBean) mDataSource.get(position);
            String gameImage = resultBean.getGameImage();
            if (!TextUtils.isEmpty(gameImage)) {
                Picasso.with(getActivity()).load(gameImage).into(viewHolder.mImage);
            }
            String ownerName = resultBean.getOwnerName();
            if (!TextUtils.isEmpty(ownerName)) {
                viewHolder.mName.setText(ownerName);
                if ("官方".equals(ownerName)) {
                    viewHolder.mTvOwner.setText("官方");
                    viewHolder.mTvOwner.setBackgroundResource(R.mipmap.gf_zc);
                } else {
                    viewHolder.mTvOwner.setText("个人");
                    viewHolder.mTvOwner.setBackgroundResource(R.mipmap.gr_zc);
                }
            }
            String gamePay = resultBean.getGamePay();
            if (!TextUtils.isEmpty(gamePay)) {
                double pay = Double.parseDouble(gamePay);
                long round = Math.round(pay);
                viewHolder.mTvPrice.setText(round + "元");
            }
            String netBarName = resultBean.getNetBarName();
            if (!TextUtils.isEmpty(netBarName)) {
                viewHolder.mTvLocation.setText(netBarName);
            }

            switch (STATUS) {
                case 0:
                    viewHolder.mTvStatus.setText("约战中");
                    viewHolder.mTvStatus.setBackgroundResource(R.mipmap.yzz);
                    String startTime = resultBean.getStartTime();
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
                            viewHolder.mTvTime.setText(hour + ":" + minute + ":" + sec);
                        } else {
                            viewHolder.mTvTime.setText("00:00:00");
                        }
                    }
                    break;
                case 1:
                    viewHolder.mTvStatus.setText("进行中");
                    viewHolder.mTvStatus.setBackgroundResource(R.mipmap.jxz);
                    viewHolder.mTvTime.setText("已结束");
                    break;
                case 2:
                    viewHolder.mTvStatus.setText("已结束");
                    viewHolder.mTvStatus.setBackgroundResource(R.mipmap.yjs);
                    viewHolder.mTvTime.setText("已结束");
                    break;
            }

            String gameDeskId = resultBean.getGameDeskId();
            getGameDeskWithId(gameDeskId, viewHolder.mTvPeople);

            return convertView;
        }

    }

    //按照游戏桌id取得游戏桌详情
    private void getGameDeskWithId(final String gameDeskId, final TextView textView) {
        StringRequest stringRequest = new StringRequest(Constant.HOST + "getGameDeskWithId&gameDeskId=" + gameDeskId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (AnalysisJSON.analysisJson(response)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<GameDeskDetails>() {
                            }.getType();
                            GameDeskDetails gameDesk = gson.fromJson(response, type);
                            if (textView != null) {
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
                                textView.setText(current + "/" + peopleNumber);
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
                Log.e("TAG", gameDeskId);
            }
        });
        mQueue.add(stringRequest);
    }


    static class ViewHolder {
        @Bind(R.id.image)
        RoundedImageView mImage;
        @Bind(R.id.tv_status)
        TextView mTvStatus;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.tv_price)
        TextView mTvPrice;
        @Bind(R.id.tv_people)
        TextView mTvPeople;
        @Bind(R.id.tv_location)
        TextView mTvLocation;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_owner)
        TextView mTvOwner;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mQueue = ((MainActivity) getActivity()).getQueue();
//        mWindowManager = (WindowManager) mMainActivity.getSystemService(Context.WINDOW_SERVICE);
//        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
//        mRadioLayout = (LinearLayout) mMainActivity.findViewById(R.id.radiogroup);
//        mToast = YuwanApplication.getToast();
//        initView();
//        initEvent();
//    }
//
//    private void getSetoutData() {
//        if (NetUtil.is_Network_Available(getActivity())) {
//
//            StringRequest stringRequest = new StringRequest(getGameDeskListWithStatus + "&gameStatus=" + mCurrent
//                    + "&pageNo=" + mPage + "&pageSize=3",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<GameDesk>() {
//                            }.getType();
//                            mGameDesk = gson.fromJson(response, type);
//                            if (!mGameDesk.isSuccess()) {
//                                mToast.setText("获取数据失败!");
//                                mToast.show();
//                            } else {
//                                if (mGameDesk.getResult() == null || mGameDesk.getResult().size() == 0) {
//                                    if (!isLoading) {
//                                        mTvBattlefiledNodata.setVisibility(View.VISIBLE);
//                                    } else {
//                                        isLoading = false;
//                                    }
//                                } else {
//                                    mTvBattlefiledNodata.setVisibility(View.GONE);
//                                    mResult.addAll(mGameDesk.getResult());
//                                    mMyAdapter.notifyDataSetChanged();
//                                    mPage++;
//                                    isLoading = false;
//                                }
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    mToast.setText("获取数据失败!");
//                    mToast.show();
//                }
//            });
//            mQueue.add(stringRequest);
//        } else {
//            mTvBattlefiledNodata.setText("网络不可用");
//            mTvBattlefiledNodata.setVisibility(View.VISIBLE);
//        }
//
//    }
//
//
//    private void initEvent() {
//
//
//        mLvBattlefield.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
//                    loadMore();
//                }
//            }
//        });
//
//        mLvBattlefield.setOnItemClickListener(this);
//        mMyscrollviewBattlefield.setOnScrollListener(this);
//        mRgBattlefield.check(R.id.rb_setout);
//    }
//
//
//    private void loadMore() {
//        isLoading = true;
//        getSetoutData();
//    }
//
//
//    @Override
//    public void onScroll(int scrollY) {
//        if (scrollY >= MyUtils.Dp2Px(getActivity(), 160)) {
//            isDosplay = true;
//            mLlS.setVisibility(View.VISIBLE);
//            switch (mCurrent) {
//                case 0:
//                    mRgBattlefieldS.check(R.id.rb_setout_s);
//                    break;
//                case 1:
//                    mRgBattlefieldS.check(R.id.rb_combat_s);
//                    break;
//                case 2:
//                    mRgBattlefieldS.check(R.id.rb_finish_s);
//                    break;
//
//            }
//        } else {
//            isDosplay = false;
//            mLlS.setVisibility(View.GONE);
//            switch (mCurrent) {
//                case 0:
//                    mRgBattlefield.check(R.id.rb_setout);
//                    break;
//                case 1:
//                    mRgBattlefield.check(R.id.rb_combat);
//                    break;
//                case 2:
//                    mRgBattlefield.check(R.id.rb_finish);
//                    break;
//            }
//        }
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        isFirst = false;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (!isFirst)
//            mCurrent = PreferenceUtils.getPrefInt(getActivity(), "mCurrent", 0);
//    }
//
//    @OnClick(R.id.iv_qcord)
//    public void onClick() {
//        startActivityForResult(new Intent(mMainActivity, QRCodeActivity.class), 0);
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//        GameDesk.ResultBean resultBean = mResult.get(position);
//        if (NetUtil.is_Network_Available(getActivity())) {
//            getGameDeskDetails(resultBean);
//        } else {
//            mToast.setText("网络不可用，请检查连接情况");
//            mToast.show();
//        }
//    }
//
//    /**
//     * 获得游戏桌详情
//     *
//     * @param resultBean
//     */
//    private void getGameDeskDetails(final GameDesk.ResultBean resultBean) {
//
//        final String gameDeskId = resultBean.getGameDeskId();
//
//        StringRequest stringRequest = new StringRequest(getGameDeskWithId + gameDeskId,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        String substring = response.substring(11, 15);
//                        if (substring.equals("true")) {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<GameDeskDetails>() {
//                            }.getType();
//                            mGameDeskDetails = gson.fromJson(response, type);
//                            if (!mGameDeskDetails.isSuccess()) {
//                                MyUtils.showToast(getActivity(), "获取数据失败!");
//                            } else {
//                                mResultBean = mGameDeskDetails.getResult();
//                                PreferenceUtils.setPrefInt(getActivity(), "mCurrent", mCurrent);
//                                Intent intent = new Intent();
//                                intent.putExtra("gameDeskId", gameDeskId);
//                                String ownerName = resultBean.getNetBarId();
//                                if (!TextUtils.isEmpty(ownerName))
//                                    intent.putExtra("netBarId", ownerName);
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("mResultBean", mResultBean);
//                                intent.putExtras(bundle);
//                                intent.setClass(getActivity(), GameDeskActivity.class);
//                                startActivity(intent);
//                            }
//
//                        } else {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                mToast.setText(jsonObject.getString("Msg"));
//                                mToast.show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                MyUtils.showToast(getActivity(), "获取数据失败!");
//            }
//        });
//        mQueue.add(stringRequest);
//    }
//
//    @OnClick({R.id.rb_setout_s, R.id.rb_combat_s, R.id.rb_finish_s, R.id.rb_setout, R.id.rb_combat, R.id.rb_finish})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.rb_setout_s:
//                mCurrent = 0;
//                mResult.clear();
//                mLlS.setVisibility(View.GONE);
//                mRgBattlefield.check(R.id.rb_setout);
//                mPage = 1;
//                getSetoutData();
//                break;
//            case R.id.rb_combat_s:
//                mCurrent = 1;
//                mResult.clear();
//                mLlS.setVisibility(View.GONE);
//                mRgBattlefield.check(R.id.rb_combat);
//                mPage = 1;
//                getSetoutData();
//                break;
//            case R.id.rb_finish_s:
//                mCurrent = 2;
//                mResult.clear();
//                mRgBattlefield.check(R.id.rb_finish);
//                mLlS.setVisibility(View.GONE);
//                mPage = 1;
//                getSetoutData();
//                break;
//            case R.id.rb_setout:
//                mResult.clear();
//                mCurrent = 0;
//                mPage = 1;
//                getSetoutData();
//                break;
//            case R.id.rb_combat:
//                mResult.clear();
//                mCurrent = 1;
//                mPage = 1;
//                getSetoutData();
//                break;
//            case R.id.rb_finish:
//                mResult.clear();
//                mCurrent = 2;
//                mPage = 1;
//                getSetoutData();
//                break;
//        }
//    }
//
//
//    private class MyAdapter extends BaseAdapter {
//
//
//        @Override
//        public int getCount() {
//            return mResult.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder;
//            if (convertView == null) {
//                convertView = View.inflate(getActivity(), R.layout.item_fragment_battlefield_listview, null);
//                viewHolder = new ViewHolder(convertView);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            viewHolder.mTvDeskName.setCompoundDrawables(mPeopleDrawble, null, null, null);
//            viewHolder.mTvDeskMoney.setCompoundDrawables(moneyDrawble, null, null, null);
//            viewHolder.mTvDeskTime.setCompoundDrawables(timeDrawble, null, null, null);
//            viewHolder.mTvDeskLocation.setCompoundDrawables(locationDrawble, null, null, null);
//            setDetails(viewHolder, position, mResult);
//            return convertView;
//        }
//
//
//    }
//
//
//    private void setDetails(ViewHolder viewHolder, int position, List<GameDesk.ResultBean> list) {
//        final GameDesk.ResultBean mResultBean = list.get(position);
//        Picasso.with(getActivity()).load(mResultBean.getGameImage()).resize(200, 200).centerCrop().into(viewHolder.mIvGamedesk);
//        viewHolder.mTvDeskLocation.setText(mResultBean.getNetBarName());
//        viewHolder.mTvDeskMoney.setText(mResultBean.getGamePay());
//        String ownerName = mResultBean.getOwnerName();
//        if (TextUtils.isEmpty(ownerName)) {
//            viewHolder.mTvDeskName.setText("官方");
//        } else {
//            viewHolder.mTvDeskName.setText(ownerName);
//        }
//        viewHolder.mTvDeskTime.setText(mResultBean.getStartTime());
//
//    }
//
//    static class ViewHolder {
//        @Bind(R.id.iv_gamedesk)
//        ImageView mIvGamedesk;
//        @Bind(R.id.tv_desk_name)
//        TextView mTvDeskName;
//        @Bind(R.id.tv_desk_time)
//        TextView mTvDeskTime;
//        @Bind(R.id.tv_desk_location)
//        TextView mTvDeskLocation;
//        @Bind(R.id.tv_desk_money)
//        TextView mTvDeskMoney;
//
//
//        ViewHolder(View view) {
//            ButterKnife.bind(this, view);
//        }
//    }
//
//
//    private void initView() {
//        mBattlefieldCarouseview.setImagesRes(Constant.battleFieldImage);
//        mSearchDrawable = ImageUtils.setDrwableSize(getActivity(), R.mipmap.unsearch, 38);
//        EditText viewById = (EditText) getActivity().findViewById(R.id.search_et_input);
//        viewById.setCompoundDrawables(mSearchDrawable, null, null, null);
//        mMyAdapter = new MyAdapter();
//        mLvBattlefield.setAdapter(mMyAdapter);
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ButterKnife.unbind(this);
//    }


