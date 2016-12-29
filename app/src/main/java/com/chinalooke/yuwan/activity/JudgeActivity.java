package com.chinalooke.yuwan.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.bigkoo.pickerview.OptionsPickerView;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.GameDesk;
import com.chinalooke.yuwan.bean.GameDeskDetails;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.PlayerBean;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
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

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JudgeActivity extends AutoLayoutActivity {

    @Bind(R.id.rl_image)
    RelativeLayout mRlImage;
    @Bind(R.id.list_view)
    ListView mListView;
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
    @Bind(R.id.viewLeft)
    TextView mViewLeft;
    private int START_ALPHA = 0;
    private int mHeight;
    private int END_ALPHA = 255;
    private GameDesk.ResultBean mGameDesk;
    private RequestQueue mQueue;
    private Toast mToast;
    private ArrayList<PlayerBean> mPlayerBeanList = new ArrayList<>();
    private GridAdapter mGridAdapter;
    private int mCount;
    private HashMap<PlayerBean, String> mHashMap = new HashMap<>();
    private HashMap<String, String> mPayMap = new HashMap<>();
    private SparseIntArray mSparseIntArray = new SparseIntArray();
    private int mChose;
    private ProgressDialog mProgressDialog;
    private List<PlayerBean> mWinnerList = new ArrayList<>();
    private LoginUser.ResultBean mUser;
    private String mGameDeskId;
    private int mGameCountInt;
    private int SUBMIT_COUNT;
    private List<String> mRantingList = new ArrayList<>();
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        WeakReference<JudgeActivity> mActivity;

        MyHandler(JudgeActivity activity) {
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            JudgeActivity theActivity = mActivity.get();
            switch (msg.what) {
                case 1:
                    if (theActivity.mCount < theActivity.mGameCountInt) {
                        if (theActivity.SUBMIT_COUNT == theActivity.mWinnerList.size()) {
                            theActivity.showSucceedDialog();
                        }
                    } else {
                        if (theActivity.SUBMIT_COUNT == theActivity.mSparseIntArray.size()) {
                            theActivity.showSucceedDialog();
                        }
                    }
                    break;
            }
        }
    }

    private void showSucceedDialog() {
        mProgressDialog.dismiss();
        MyUtils.showDialog(JudgeActivity.this, "提示", "提交成功", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
    }

    private MyAdapter mMyAdapter;

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
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
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
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Bitmap response) {
                    if (response != null)
                        mRlImage.setBackground(new BitmapDrawable(getResources(), response));
                }
            }, ViewHelper.getDisplayMetrics(getApplicationContext()).widthPixels, 390, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, null);

            mQueue.add(request);
        }
        mGridAdapter = new GridAdapter(mPlayerBeanList);
        mGridView.setAdapter(mGridAdapter);
        mMyAdapter = new MyAdapter(mPlayerBeanList);
        mListView.setAdapter(mMyAdapter);
    }

    private void initData() {
        String count = getIntent().getStringExtra("count");
        mViewLeft.setText("第" + count + "场");
        mTvCount.setText("第" + count + "场赢家选择");
        mCount = Integer.parseInt(count);
        mGameDeskId = getIntent().getStringExtra("gameDeskId");
        if (!TextUtils.isEmpty(mGameDeskId))
            getGameDeskWithId(mGameDeskId);
    }

    //顶部滑动渐变设置
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initTopScroll() {
        final int heightPixels = ViewHelper.getDisplayMetrics(getApplicationContext()).heightPixels;
        final Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.actionbar_color_else);
        drawable.setAlpha(START_ALPHA);
        mRlTop.setBackground(drawable);
        ViewTreeObserver viewTreeObserver = mRlScroll.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRlScroll.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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
                                String gamePay = result.getGamePay();
                                initPay(gamePay);
                                String gameCount = result.getGameCount();
                                mGameCountInt = Integer.parseInt(gameCount);
                                if (mCount < mGameCountInt) {
                                    mListView.setVisibility(View.GONE);
                                    mGridView.setVisibility(View.VISIBLE);
                                } else {
                                    mListView.setVisibility(View.VISIBLE);
                                    mGridView.setVisibility(View.GONE);
                                }
                                GameDeskDetails.ResultBean.PlayersBean players = gameDesk.getResult().getPlayers();
                                if (gameDesk.getResult() != null && players != null) {
                                    List<GameDeskDetails.ResultBean.PlayersBean.RightBean> right = players.getRight();
                                    List<GameDeskDetails.ResultBean.PlayersBean.LeftBean> left = players.getLeft();
                                    if (right != null && right.size() != 0) {
                                        for (GameDeskDetails.ResultBean.PlayersBean.RightBean rightBean : right) {
                                            boolean isLoser = rightBean.isLoser();
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
                                            boolean isLoser = rightBean.isLoser();
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
                            mMyAdapter.notifyDataSetChanged();
                            mGridAdapter.notifyDataSetChanged();
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

    /**
     * 初始化奖金名次数据
     *
     * @param gamePay 网络请求返回字符串
     */
    private void initPay(String gamePay) {
        String[] substring = gamePay.split(",");
        for (String s : substring) {
            String[] split = s.split("|");
            mPayMap.put(split[0], split[1]);
        }
    }

    @OnClick({R.id.iv_back, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if (NetUtil.is_Network_Available(getApplicationContext())) {
                    if (mCount < mGameCountInt) {
                        for (Map.Entry<PlayerBean, String> next : mHashMap.entrySet()) {
                            String value = next.getValue();
                            if ("1".equals(value)) {
                                mWinnerList.add(next.getKey());
                            }
                        }
                        if (mWinnerList.size() == 0) {
                            mToast.setText("请选择战场赢家！");
                            mToast.show();
                            return;
                        } else {
                            SUBMIT_COUNT = 0;
                            mProgressDialog = MyUtils.initDialog("提交中", this);
                            mProgressDialog.show();
                            for (PlayerBean playerBean : mPlayerBeanList) {
                                submitRating(playerBean.getUserId(), "1");
                            }
                        }
                    } else if (mCount == mGameCountInt) {
                        if (checkRating()) {
                            for (int i = 0; i < mSparseIntArray.size(); i++) {
                                Integer key = mSparseIntArray.keyAt(i);
                                PlayerBean playerBean = mPlayerBeanList.get(key);
                                String userId = playerBean.getUserId();
                                submitRating(userId, mSparseIntArray.keyAt(i) + "");
                            }
                        }
                    }
                } else {
                    mToast.setText("网络不可用，请检查网络连接");
                    mToast.show();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    //检查是否有重复名次
    private boolean checkRating() {
        HashSet set = new HashSet();
        for (String s : mRantingList)
            set.add(s);
        if (!(set.size() == mRantingList.size())) {
            mToast.setText("名次不能重复，请重新选择");
            mToast.show();
            return false;
        }
        return true;
    }

    /**
     * 单次提交名次
     *
     * @param userId 玩家id
     * @param rating 玩家名次
     */
    private void submitRating(String userId, String rating) {
        String url = Constant.HOST + "JudgeWinerForNetbar&netbarId=" + mUser.getUserId() + "&netbarUserId=" + mUser.getUserId()
                + "&gameDeskId=" + mGameDeskId + "&gameCount=" + mCount + "&userId=" + userId + "&rating=" + rating;

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AnalysisJSON.analysisJson(response)) {
                    SUBMIT_COUNT++;
                    mHandler.sendEmptyMessage(1);
                } else {
                    mProgressDialog.dismiss();
                    MyUtils.showMsg(mToast, response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("服务器抽风了，请稍后再试");
                mToast.show();
            }
        });

        mQueue.add(request);
    }

    class MyAdapter extends MyBaseAdapter {

        public MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(JudgeActivity.this, R.layout.item_judge_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            PlayerBean playerBean = mPlayerBeanList.get(position);
            String headImg = playerBean.getHeadImg();
            String nickName = playerBean.getNickName();
            if (!TextUtils.isEmpty(nickName))
                viewHolder.mTvName.setText(nickName);
            if (!TextUtils.isEmpty(headImg)) {
                String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), headImg, 64, 64);
                Picasso.with(getApplicationContext()).load(loadImageUrl).into(viewHolder.mRoundedImageView);
            }

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
                    alertPicker(viewHolder.mTvRanking, viewHolder.mTvPrice, position);
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
            if (!TextUtils.isEmpty(headImg)) {
                String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), headImg, 200, 200);
                Picasso.with(getApplicationContext()).load(loadImageUrl).into(viewHolder.mIvGameimage);
            }
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

    private void alertPicker(final TextView tvRanking, final TextView tvPrice, final int position) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= mPlayerBeanList.size(); i++) {
            list.add(i + "");
        }
        OptionsPickerView optionsPickerView = new OptionsPickerView(this);
        optionsPickerView.setPicker(list);
        optionsPickerView.setTitle("选择名次");
        optionsPickerView.setCyclic(false);
        optionsPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String s1 = tvRanking.getText().toString();
                if (!TextUtils.isEmpty(s1))
                    mRantingList.remove(s1);
                tvRanking.setText(options1 + "");
                mRantingList.add(options1 + "");
                mSparseIntArray.put(options1, position);
                String s = mPayMap.get(options1 + "");
                if (!TextUtils.isEmpty(s))
                    tvPrice.setText(s);
            }
        });
        optionsPickerView.show();
    }
}
