package com.chinalooke.yuwan.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.CustemSpinerAdapter;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.CustemObject;
import com.chinalooke.yuwan.bean.GameDesk;
import com.chinalooke.yuwan.bean.GameDeskDetails;
import com.chinalooke.yuwan.bean.PlayerBean;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.ViewHelper;
import com.chinalooke.yuwan.view.MyScrollView;
import com.chinalooke.yuwan.view.SpinnerPopWindow;
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
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @Bind(R.id.tv_count)
    TextView mTvCount;
    @Bind(R.id.tv_chose)
    TextView mTvChose;
    @Bind(R.id.gridView)
    GridView mGridView;
    @Bind(R.id.ll_front)
    LinearLayout mLlFront;
    @Bind(R.id.viewLeft)
    TextView mViewLeft;
    private int START_ALPHA = 0;
    private int mHeight;
    private int END_ALPHA = 255;
    private GameDesk.ResultBean mGameDesk;
    private RequestQueue mQueue;
    private Toast mToast;
    private List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> mLeftPlayersBeenList = new ArrayList<>();
    private List<GameDeskDetails.ResultBean.PlayersBean.RightBean> mRightPlayersBeenList = new ArrayList<>();
    private List<CustemObject> mCustemObjects;
    private CustemSpinerAdapter mCustemSpinerAdapter;
    private SpinnerPopWindow mSpinnerPopWindow;
    private List<PlayerBean> mPlayerBeanList = new ArrayList<>();
    private GridAdapter mGridAdapter;
    private int GAME_COUNT = 1;
    private int mCount;
    private HashMap<PlayerBean, String> mHashMap = new HashMap<>();
    private int mChose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_judge);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mGameDesk = (GameDesk.ResultBean) getIntent().getSerializableExtra("gameDesk");
        initTopScroll();
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        //gridView item点击选择事件
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayerBean playerBean = (PlayerBean) parent.getItemAtPosition(position);
                ImageView viewById = (ImageView) view.findViewById(R.id.iv_check);
                String s = mHashMap.get(playerBean);
                if ("0".equals(s)) {
                    viewById.setVisibility(View.VISIBLE);
                    mHashMap.put(playerBean, "1");
                    mChose++;
                } else if ("1".equals(s)) {
                    viewById.setVisibility(View.GONE);
                    mHashMap.put(playerBean, "0");
                    mChose--;
                }
                mTvChose.setText("已选(" + mChose + ")");
            }
        });

    }

    private void initView() {
        mTvSignUp.setVisibility(View.GONE);
        String gameImage = mGameDesk.getGameImage();
        if (!TextUtils.isEmpty(gameImage)) {
            ImageRequest request = new ImageRequest(gameImage, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    if (response != null)
                        mRlImage.setBackground(new BitmapDrawable(response));
                }
            }, ViewHelper.getDisplayMetrics(getApplicationContext()).widthPixels, 390, Bitmap.Config.ARGB_8888, null);

            mQueue.add(request);
        }
        mGridAdapter = new GridAdapter(mPlayerBeanList);
        mGridView.setAdapter(mGridAdapter);
    }

    private void initData() {
        String count = getIntent().getStringExtra("count");
        mViewLeft.setText("第" + count + "场");
        mTvCount.setText("第" + count + "场赢家选择");
        mCount = Integer.parseInt(count);
        String gameDeskId = getIntent().getStringExtra("gameDeskId");
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
                                GameDeskDetails.ResultBean result = gameDesk.getResult();
                                String gameCount = result.getGameCount();
                                int gameCountInt = Integer.parseInt(gameCount);
                                if (mCount < gameCountInt) {
                                    mListView.setVisibility(View.GONE);
                                    mGridView.setVisibility(View.VISIBLE);
                                } else {
                                    mListView.setVisibility(View.VISIBLE);
                                    mGridView.setVisibility(View.GONE);
                                }
                                initSpinnerData(gameCount);
                                GameDeskDetails.ResultBean.PlayersBean players = gameDesk.getResult().getPlayers();
                                if (gameDesk.getResult() != null && players != null) {
                                    List<GameDeskDetails.ResultBean.PlayersBean.RightBean> right = players.getRight();
                                    List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> left = players.getLeft();
                                    if (right != null && right.size() != 0) {
                                        for (GameDeskDetails.ResultBean.PlayersBean.RightBean rightBean : right) {
                                            boolean isLoser = rightBean.isIsLoser();
                                            if (!isLoser) {
                                                PlayerBean playerBean = new PlayerBean();
                                                if (!TextUtils.isEmpty(rightBean.getHeadImg()))
                                                    playerBean.setHeadImg(rightBean.getHeadImg());
                                                if (!TextUtils.isEmpty(rightBean.getNickName()))
                                                    playerBean.setNickName(rightBean.getNickName());
                                                if (!TextUtils.isEmpty(rightBean.getStatus()))
                                                    playerBean.setStatus(rightBean.getStatus());
                                                if (!TextUtils.isEmpty(rightBean.getUserId()))
                                                    playerBean.setUserId(rightBean.getUserId());
                                                mPlayerBeanList.add(playerBean);
                                            }
                                        }
                                    }
                                    if (left != null && left.size() != 0) {
                                        for (GameDeskDetails.ResultBean.PlayersBean.LeftBean rightBean : left) {
                                            boolean isLoser = rightBean.isIsLoser();
                                            if (!isLoser) {
                                                PlayerBean playerBean = new PlayerBean();
                                                if (!TextUtils.isEmpty(rightBean.getHeadImg()))
                                                    playerBean.setHeadImg(rightBean.getHeadImg());
                                                if (!TextUtils.isEmpty(rightBean.getNickName()))
                                                    playerBean.setNickName(rightBean.getNickName());
                                                if (!TextUtils.isEmpty(rightBean.getStatus()))
                                                    playerBean.setStatus(rightBean.getStatus());
                                                if (!TextUtils.isEmpty(rightBean.getUserId()))
                                                    playerBean.setUserId(rightBean.getUserId());
                                                mPlayerBeanList.add(playerBean);
                                            }
                                        }
                                    }

                                    for (int i = 0; i < mPlayerBeanList.size(); i++) {
                                        PlayerBean playerBean = mPlayerBeanList.get(i);
                                        mHashMap.put(playerBean, "0");
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

    //初始化spinner数据
    private void initSpinnerData(String gameCount) {
        mCustemSpinerAdapter = new CustemSpinerAdapter(this);
        mSpinnerPopWindow = new SpinnerPopWindow(this);
        mSpinnerPopWindow.setAdatper(mCustemSpinerAdapter);
        mCustemObjects = new ArrayList<>();
        for (int i = 1; i <= Integer.parseInt(gameCount); i++) {
            CustemObject custemObject = new CustemObject();
            custemObject.data = "第" + i + "场";
            mCustemObjects.add(custemObject);
        }
        mViewLeft.setText("第1场");
    }

    @OnClick({R.id.iv_back, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                JudgeWinerForNetbar();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    //提交赢家
    private void JudgeWinerForNetbar() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {

        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }
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
                    alertPicker(viewHolder.mTvRanking, viewHolder.mTvPrice);
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

    class GridAdapter extends MyBaseAdapter {

        GridAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_gamelist_gradview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            PlayerBean playerBean = mPlayerBeanList.get(position);
            String nickName = playerBean.getNickName();
            if (!TextUtils.isEmpty(nickName))
                viewHolder.mTvGameName.setText(nickName);
            String headImg = playerBean.getHeadImg();
            if (!TextUtils.isEmpty(headImg))
                Picasso.with(getApplicationContext()).load(headImg).into(viewHolder.mIvGameimage);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.iv_gameimage)
            ImageView mIvGameimage;
            @Bind(R.id.tv_game_name)
            TextView mTvGameName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    private void alertPicker(TextView tvRanking, TextView tvPrice) {

    }
}
