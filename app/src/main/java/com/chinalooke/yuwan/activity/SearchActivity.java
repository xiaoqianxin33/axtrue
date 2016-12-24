package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.GameDesk;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.constant.MyLinearLayoutManager;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.KeyboardUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.view.RecycleViewDivider;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_search)
    EditText mTvSearch;
    @Bind(R.id.rl_yz)
    RelativeLayout mRlYz;
    @Bind(R.id.rl_jx)
    RelativeLayout mRlJx;
    @Bind(R.id.rl_js)
    RelativeLayout mRlJs;
    @Bind(R.id.list_view)
    RecyclerView mRecyclerView;
    private int GAME_STATUS;
    private int PAGE_NO = 1;
    private RequestQueue mQueue;
    private Toast mToast;
    private List<GameDesk.ResultBean> mDeskList = new ArrayList<>();
    private QuickAdapter mAdapter;
    private String mSearch;
    private boolean isFirst = true;
    private View mFoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mFoot = getLayoutInflater().inflate(R.layout.foot, null, false);
        initView();
        initEvent();
    }

    private void initView() {
        mRlYz.setSelected(true);
        MyLinearLayoutManager myLinearLayoutManager = new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        RecycleViewDivider recycleViewDivider = new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.line_color));
        mAdapter = new QuickAdapter(R.layout.item_zc_listview, mDeskList);
        myLinearLayoutManager.setReverseLayout(false);
        myLinearLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(myLinearLayoutManager);
        mRecyclerView.addItemDecoration(recycleViewDivider);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initEvent() {
        mTvSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mSearch = mTvSearch.getText().toString();
                    if (!TextUtils.isEmpty(mSearch)) {
                        PAGE_NO = 1;
                        mDeskList.clear();
                        getGameDeskListWithStatus(mSearch);
                        KeyboardUtils.hideSoftInput(SearchActivity.this);
                    }
                }

                return true;
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        });

        mTvSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    requestFocus();
                }
                return false;
            }
        });
    }

    private void requestFocus() {
        mTvSearch.setFocusable(true);
        mTvSearch.setFocusableInTouchMode(true);
        mTvSearch.clearFocus();
        mTvSearch.requestFocus();
    }

    private void loadMore() {
        PAGE_NO++;
        getGameDeskListWithStatus(mSearch);
        Log.e("TAG", "loadMore");
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

    //搜索游戏桌
    private void getGameDeskListWithStatus(String search) {
        if (!TextUtils.isEmpty(search)) {
            try {
                String encode = URLEncoder.encode(search, "UTF-8");
                String url = Constant.HOST + "getGameDeskListWithStatus&gameStatus=" + GAME_STATUS
                        + "&pageNo=" + PAGE_NO + "&pageSize=10&keywords=" + encode;

                StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (AnalysisJSON.analysisJson(response)) {
                            Gson gson = new Gson();
                            GameDesk gameDesk = gson.fromJson(response, GameDesk.class);
                            if (gameDesk != null) {
                                List<GameDesk.ResultBean> result = gameDesk.getResult();
                                if (result != null && result.size() != 0) {
                                    mDeskList.addAll(result);
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    if (isFirst) {
                                        mToast.setText("该搜索无结果");
                                        mToast.show();
                                    } else {
                                        mAdapter.removeAllFooterView();
                                        mAdapter.addFooterView(mFoot);
                                    }
                                }
                            }
                        } else {
                            MyUtils.showMsg(mToast, response);
                        }
                        isFirst = false;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mToast.setText("服务器抽风了，请稍后再试");
                        mToast.show();
                    }
                });

                mQueue.add(request);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }


    @OnClick({R.id.tv_cancel, R.id.rl_yz, R.id.rl_jx, R.id.rl_js})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.rl_yz:
                GAME_STATUS = 0;
                setSelect(0);
                mDeskList.clear();
                PAGE_NO = 1;
                getGameDeskListWithStatus(mSearch);
                break;
            case R.id.rl_jx:
                GAME_STATUS = 1;
                setSelect(1);
                mDeskList.clear();
                PAGE_NO = 1;
                getGameDeskListWithStatus(mSearch);
                break;
            case R.id.rl_js:
                GAME_STATUS = 2;
                setSelect(2);
                mDeskList.clear();
                PAGE_NO = 1;
                getGameDeskListWithStatus(mSearch);
                break;
        }
    }

    private void setSelect(int i) {
        mRlYz.setSelected(i == 0);
        mRlJx.setSelected(i == 1);
        mRlJs.setSelected(i == 2);
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

            switch (GAME_STATUS) {
                case 0:
                    helper.setText(R.id.tv_status, "约战中")
                            .setText(R.id.tv_apply, "离报名结束")
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
                String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), gameImage, 225, 172);
                Picasso.with(mContext).load(loadImageUrl).into((ImageView) helper.getView(R.id.image));
            }

            String curPlayNum = item.getCurPlayNum();
            String playerNum = item.getPlayerNum();
            if (!TextUtils.isEmpty(curPlayNum) && !TextUtils.isEmpty(playerNum))
                helper.setText(R.id.tv_people, curPlayNum + "/" + playerNum);
        }

    }

}
