package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.Circle;
import com.chinalooke.yuwan.model.Dynamic;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.ViewHelper;
import com.chinalooke.yuwan.view.NoSlidingListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CircleDynamicActivity extends AutoLayoutActivity {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.iv_camera)
    ImageView mIvCamera;
    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_slogen)
    TextView mTvSlogen;
    @Bind(R.id.tv_join)
    TextView mTvJoin;
    @Bind(R.id.list_view)
    NoSlidingListView mListView;
    @Bind(R.id.sr)
    SwipeRefreshLayout mScrollview;
    @Bind(R.id.activity_circle_dynamic)
    LinearLayout mActivityCircleDynamic;
    @Bind(R.id.rl_top)
    RelativeLayout mRlTop;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    private Circle.ResultBean mCircle;
    private RequestQueue mQueue;
    private DisplayMetrics mDisplayMetrics;
    private int mCircle_type;
    private int PAGE_NO;
    private LoginUser.ResultBean mUserInfo;
    private List<Dynamic.ResultBean.ListBean> mDynamics = new ArrayList<>();
    private MyAdapter mMyAdapter;
    private boolean isLoading = false;
    private boolean isRefresh = false;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_circle_dynamic);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mDisplayMetrics = ViewHelper.getDisplayMetrics(CircleDynamicActivity.this);
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mMyAdapter = new MyAdapter(mDynamics);
        mListView.setAdapter(mMyAdapter);
        initData();
        initView();
        initEvent();

    }

    private void initEvent() {
        //初始化swipeRefresh颜色
        mScrollview.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //下拉刷新事件
        mScrollview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PAGE_NO = 0;
                isRefresh = true;
                getActiveList();
                mScrollview.setRefreshing(false);
            }
        });


        //上拉加载更多
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mListView != null && mListView.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    mScrollview.setEnabled(enable);
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        PAGE_NO++;
        isLoading = true;
        getActiveList();
    }

    private void initView() {
        //设置顶部背景图片
        String bgImage = mCircle.getBgImage();
        ImageRequest request = new ImageRequest(bgImage, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if (response != null)
                    mRlTop.setBackground(new BitmapDrawable(response));
            }
        }, mDisplayMetrics.widthPixels, 400, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(request);

        String groupName = mCircle.getGroupName();
        if (!TextUtils.isEmpty(groupName))
            mTvName.setText(groupName);

        String details = mCircle.getDetails();
        if (!TextUtils.isEmpty(details))
            mTvSlogen.setText(details);

        String headImg = mCircle.getHeadImg();
        if (!TextUtils.isEmpty(headImg))
            Picasso.with(getApplicationContext()).load(headImg).resize(130, 130).centerCrop().into(mRoundedImageView);
    }

    private void initData() {
        mCircle = (Circle.ResultBean) getIntent().getSerializableExtra("circle");
        mCircle_type = getIntent().getIntExtra("circle_type", 0);
    }

    //获取圈子动态信息
    private void getActiveList() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String uri = Constant.HOST + "getActiveListWithGroup&groupId=" + mCircle.getGroupId()
                    + "&pageNo=" + PAGE_NO + "&pageSize=5";
            if (mUserInfo != null)
                uri = uri + "&userId=" + mUserInfo.getUserId();

            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mPbLoad.setVisibility(View.GONE);
                    if (AnalysisJSON.analysisJson(response)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Dynamic>() {
                        }.getType();
                        Dynamic dynamic = gson.fromJson(response, type);
                        if (dynamic.getResult() != null) {
                            if (isRefresh)
                                mDynamics.clear();
                            mDynamics.addAll(dynamic.getResult().getList());
                            mMyAdapter.notifyDataSetChanged();
                            isRefresh = false;
                            isLoading = false;
                        }
                        isFirst = false;
                    } else {
                        if (isFirst) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                mTvNone.setVisibility(View.VISIBLE);
                                String msg = jsonObject.getString("Msg");
                                mTvNone.setText(msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        isFirst = false;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mPbLoad.setVisibility(View.GONE);
                    mTvNone.setVisibility(View.VISIBLE);
                    mTvNone.setText("服务器抽风了，请稍后重试");
                }
            });

            mQueue.add(request);
        } else {
            mPbLoad.setVisibility(View.GONE);
            mTvNone.setVisibility(View.VISIBLE);
            mTvNone.setText("网络未连接");
        }

    }

    @OnClick({R.id.iv_back, R.id.iv_camera, R.id.roundedImageView, R.id.tv_name})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_camera:
                Intent intent = new Intent(this, SendDynamicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("circle", mCircle);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.roundedImageView:
                skipToInfo();
                break;
            case R.id.tv_name:
                break;
        }
    }

    private void skipToInfo() {
        Intent intent = new Intent(this, CircleInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("circle", mCircle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    class MyAdapter extends MyBaseAdapter {

        MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_circle_dynamic_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Dynamic.ResultBean.ListBean resultBean = mDynamics.get(position);
            String headImg = resultBean.getHeadImg();
            if (!TextUtils.isEmpty(headImg))
                Picasso.with(getApplicationContext()).load(headImg).resize(72, 72).centerCrop().into(viewHolder.mRoundedImageView);
            String content = resultBean.getContent();
            if (!TextUtils.isEmpty(content))
                viewHolder.mTvContent.setText(content);
            String nickName = resultBean.getNickName();
            if (!TextUtils.isEmpty(nickName))
                viewHolder.mTvName.setText(nickName);

            String images = resultBean.getImages();
            if (!TextUtils.isEmpty(images)) {
                String[] split = images.split(",");
                viewHolder.mGridView.setAdapter(new GridAdapter(split));
            }

            String likes = resultBean.getLikes();
            if (!TextUtils.isEmpty(likes)) {
                viewHolder.mTvDianzan.setText(likes);
            } else {
                viewHolder.mTvDianzan.setText("0");
            }

            String comments = resultBean.getComments();
            if (!TextUtils.isEmpty(comments))
                viewHolder.mTvPinglun.setText(comments);
            else
                viewHolder.mTvPinglun.setText("0");

            boolean isLoginUserLike = resultBean.isIsLoginUserLike();
            if (isLoginUserLike)
                viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzanhou);
            else
                viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);

            String address = resultBean.getAddress();
            if (!TextUtils.isEmpty(address))
                viewHolder.mTvAddress.setText(address);

            String addTime = resultBean.getAddTime();
            if (!TextUtils.isEmpty(addTime))
                viewHolder.mTvTime.setText(addTime.substring(0, 10));

            return convertView;
        }

    }

    static class ViewHolder {
        @Bind(R.id.roundedImageView)
        RoundedImageView mRoundedImageView;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_content)
        TextView mTvContent;
        @Bind(R.id.gridView)
        GridView mGridView;
        @Bind(R.id.iv1)
        ImageView mIv1;
        @Bind(R.id.tv_address)
        TextView mTvAddress;
        @Bind(R.id.iv_pinglun)
        ImageView mIvPinglun;
        @Bind(R.id.tv_pinglun)
        TextView mTvPinglun;
        @Bind(R.id.tv_dianzan)
        TextView mTvDianzan;
        @Bind(R.id.iv_dianzan)
        ImageView mIvDianzan;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    class GridAdapter extends BaseAdapter {
        private String[] mStrings;

        GridAdapter(String[] strings) {
            this.mStrings = strings;
        }

        @Override
        public int getCount() {
            return mStrings.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageview;
            if (convertView == null) {
                imageview = new ImageView(getApplicationContext());
                imageview.setImageResource(R.mipmap.placeholder);
                imageview.setLayoutParams(new GridView.LayoutParams(235, 235));
                imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageview.setPadding(6, 6, 6, 6);
            } else {
                imageview = (ImageView) convertView;
            }
            Picasso.with(getApplicationContext()).load(mStrings[position]).into(imageview);
            return imageview;
        }
    }
}
