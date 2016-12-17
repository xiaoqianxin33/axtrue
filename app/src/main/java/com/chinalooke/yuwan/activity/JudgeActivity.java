package com.chinalooke.yuwan.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.GameDesk;
import com.chinalooke.yuwan.bean.GameDeskDetails;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.ViewHelper;
import com.chinalooke.yuwan.view.MyScrollView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class JudgeActivity extends AutoLayoutActivity {

    @Bind(R.id.rl_image)
    RelativeLayout mRlImage;
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.btn_submit)
    Button mBtnSubmit;
    @Bind(R.id.iv_back)
    FrameLayout mIvBack;
    @Bind(R.id.iv_arrow_head)
    ImageView mIvArrowHead;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_sign_up)
    TextView mTvSignUp;
    @Bind(R.id.rl_top)
    RelativeLayout mRlTop;
    @Bind(R.id.activity_judge)
    RelativeLayout mRlScroll;
    @Bind(R.id.scrollView)
    MyScrollView mMyScrollView;
    private int START_ALPHA = 0;
    private int mHeight;
    private int END_ALPHA = 255;
    private GameDesk.ResultBean mGameDesk;
    private RequestQueue mQueue;
    private Toast mToast;
    private List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> mLeftPlayersBeenList = new ArrayList<>();
    private List<GameDeskDetails.ResultBean.PlayersBean.RightBean> mRightPlayersBeenList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_judge);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        initTopScroll();
        initData();

    }

    private void initData() {
        mGameDesk = (GameDesk.ResultBean) getIntent().getSerializableExtra("gameDesk");
        String gameDeskId = mGameDesk.getGameDeskId();
        if (!TextUtils.isEmpty(gameDeskId))
            getGameDeskWithId(gameDeskId);
    }

    //顶部滑动渐变设置
    private void initTopScroll() {
        final int heightPixels = ViewHelper.getDisplayMetrics(getApplicationContext()).heightPixels;
        final Drawable drawable = getResources().getDrawable(R.drawable.actionbar_color_else);
        drawable.setAlpha(START_ALPHA);
        mRlTop.setBackground(drawable);
        ViewTreeObserver viewTreeObserver = mRlScroll.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRlScroll.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mHeight = mRlScroll.getHeight();
                final int height = Math.abs(heightPixels - mHeight);
                mMyScrollView.setOnScrollChangedListener(new MyScrollView.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged(ScrollView who, int x, int y, int oldx, int oldy) {
                        if (y > height) {
                            y = height;   //当滑动到指定位置之后设置颜色为纯色，之前的话要渐变---实现下面的公式即可
                        }
                        drawable.setAlpha(y * (END_ALPHA - START_ALPHA) / height + START_ALPHA);
                    }
                });
            }
        });
    }

    //按照游戏桌id取得游戏桌详情
    private void getGameDeskWithId(final String gameDeskId) {

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
                                GameDeskDetails.ResultBean.PlayersBean players = gameDesk.getResult().getPlayers();
                                if (gameDesk.getResult() != null && players != null) {
                                    String winer = gameDesk.getResult().getWiner();
                                    if (winer.equals("left")) {
                                        if (players.getLeft() != null)
                                            mLeftPlayersBeenList.addAll(players.getLeft());
                                        MyAdapter myAdapter = new MyAdapter(mLeftPlayersBeenList, 0);
                                        mListView.setAdapter(myAdapter);
                                    } else if (winer.equals("right")) {
                                        if (players.getRight() != null)
                                            mRightPlayersBeenList.addAll(players.getRight());
                                        MyAdapter myAdapter = new MyAdapter(mRightPlayersBeenList, 1);
                                        mListView.setAdapter(myAdapter);
                                    }
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

    class MyAdapter extends MyBaseAdapter {
        private int mWinner;

        public MyAdapter(List dataSource, int winner) {
            super(dataSource);
            this.mWinner = winner;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(JudgeActivity.this, R.layout.item_judge_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String headImg;
            String nickName;
            if (mWinner == 0) {
                GameDeskDetails.ResultBean.PlayersBean.LeftBean leftBean = (GameDeskDetails.ResultBean.PlayersBean.LeftBean) mDataSource.get(position);
                headImg = leftBean.getHeadImg();
                nickName = leftBean.getNickName();
            } else {
                GameDeskDetails.ResultBean.PlayersBean.RightBean rightBean = (GameDeskDetails.ResultBean.PlayersBean.RightBean) mDataSource.get(position);
                headImg = rightBean.getHeadImg();
                nickName = rightBean.getNickName();
            }

            if (!TextUtils.isEmpty(nickName))
                viewHolder.mTvName.setText(nickName);

            if (!TextUtils.isEmpty(headImg))
                Picasso.with(getApplicationContext()).load(headImg + "?imageView2/1/w/64/h/64").into(viewHolder.mRoundedImageView);

            viewHolder.mRlTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.mRlTop.setSelected(!viewHolder.mRlTop.isSelected());
                    boolean selected = viewHolder.mRlTop.isSelected();
                    if (selected) {
                        viewHolder.mRlPrice.setVisibility(View.VISIBLE);
                        viewHolder.mRlRanking.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.mRlPrice.setVisibility(View.GONE);
                        viewHolder.mRlRanking.setVisibility(View.GONE);
                    }
                }
            });


            viewHolder.mTvRanking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertPicker(viewHolder.mTvRanking,viewHolder.mTvPrice);
                }
            });

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.roundedImageView)
            RoundedImageView mRoundedImageView;
            @Bind(R.id.tv_name)
            TextView mTvName;
            @Bind(R.id.tv_ranking)
            TextView mTvRanking;
            @Bind(R.id.rl_ranking)
            RelativeLayout mRlRanking;
            @Bind(R.id.rl_top)
            RelativeLayout mRlTop;
            @Bind(R.id.tv_price)
            TextView mTvPrice;
            @Bind(R.id.rl_price)
            RelativeLayout mRlPrice;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    private void alertPicker(TextView tvRanking, TextView tvPrice) {

    }
}