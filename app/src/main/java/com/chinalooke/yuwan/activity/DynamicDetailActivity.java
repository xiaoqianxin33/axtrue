package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.CommentList;
import com.chinalooke.yuwan.bean.LikeList;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.WholeDynamic;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DynamicDetailActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
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
    @Bind(R.id.tv_address)
    TextView mTvAddress;
    @Bind(R.id.tv_pinglun)
    TextView mTvPinglun;
    @Bind(R.id.tv_dianzan)
    TextView mTvDianzan;
    @Bind(R.id.iv_dianzan)
    ImageView mIvDianzan;
    @Bind(R.id.tv_dianzan_people)
    TextView mTvDianzanPeople;
    @Bind(R.id.list_view)
    ListView mListView;
    private WholeDynamic.ResultBean mDynamic;
    private LoginUser.ResultBean mUserInfo;
    private String activeType;
    private RequestQueue mQueue;
    private List<CommentList.ResultBean> mCommentList = new ArrayList<>();
    private String[] mSplit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_detail);
        ButterKnife.bind(this);
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mQueue = YuwanApplication.getQueue();
        MyAdapter myAdapter = new MyAdapter(mCommentList);
        mListView.setAdapter(myAdapter);
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        //gridView点击事件
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DynamicDetailActivity.this, ImagePagerActivity.class);
                Bundle bundle = new Bundle();
                if (mSplit != null)
                    bundle.putStringArray("url", mSplit);
                intent.putExtras(bundle);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        String headImg = mDynamic.getHeadImg();
        if (!TextUtils.isEmpty(headImg))
            Picasso.with(getApplicationContext()).load(headImg).into(mRoundedImageView);
        String nickName = mDynamic.getNickName();
        if (!TextUtils.isEmpty(nickName))
            mTvName.setText(nickName);
        String createTime = mDynamic.getCreateTime();
        if (!TextUtils.isEmpty(createTime))
            mTvTime.setText(createTime);
        String content = mDynamic.getContent();
        if (!TextUtils.isEmpty(content))
            mTvContent.setText(content);
        String images = mDynamic.getImages();
        if (!TextUtils.isEmpty(images)) {
            mSplit = images.split(",");
            mGridView.setAdapter(new GridAdapter(mSplit));
        }

        String likes = mDynamic.getLikes();
        if (!TextUtils.isEmpty(likes)) {
            mTvDianzan.setText(likes);
        } else {
            mTvDianzan.setText("0");
        }

        String comments = mDynamic.getComments();
        if (!TextUtils.isEmpty(comments))
            mTvPinglun.setText(comments);
        else
            mTvPinglun.setText("0");

        String address = mDynamic.getAddress();
        if (!TextUtils.isEmpty(address))
            mTvAddress.setText(address);
        if (mUserInfo != null) {
            boolean isLoginUserLike = mDynamic.isIsLoginUserLike();
            if (isLoginUserLike)
                mIvDianzan.setImageResource(R.mipmap.dianzanhou);
            else
                mIvDianzan.setImageResource(R.mipmap.dianzan);
        } else {
            mIvDianzan.setImageResource(R.mipmap.dianzan);
        }

    }

    private void initData() {
        mDynamic = (WholeDynamic.ResultBean) getIntent().getSerializableExtra("dynamic");
        int mDynamic_type = getIntent().getIntExtra("dynamic_type", 0);
        switch (mDynamic_type) {
            case 0:
                activeType = "";
                break;
            case 1:
                activeType = "group";
                break;
        }
        if (mDynamic != null) {
            getFavourList();
            getCommentList();
        }
    }

    //取得评论列表
    private void getCommentList() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String uri = Constant.HOST + "getCommentList&activeId=" + mDynamic.getActiveId();
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    //取得点赞用户列表
    private void getFavourList() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String uri = Constant.HOST + "getFavourList&activeId=" + mDynamic.getActiveId() + "&activeType="
                    + activeType;
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        if (AnalysisJSON.analysisJson(response)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<LikeList>() {
                            }.getType();
                            LikeList likeList = gson.fromJson(response, type);
                            if (likeList.getResult() != null && likeList.getResult() != null && likeList.getResult().size() != 0) {
                                StringBuilder stringBuilder = new StringBuilder();
                                List<LikeList.ResultBean> result = likeList.getResult();
                                for (int i = 0; i < result.size(); i++) {
                                    if (i == result.size() - 1)
                                        stringBuilder.append(result.get(i).getNickName());
                                    else
                                        stringBuilder.append(result.get(i).getNickName()).append("、");
                                }
                                mTvDianzanPeople.setText(stringBuilder.toString());
                            }
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mTvDianzanPeople.setText("服务器抽风了，无法获取点赞详情");
                }
            });
            mQueue.add(request);
        } else {
            mTvDianzanPeople.setText("网络未连接，无法获取点赞详情");
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_camera})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_camera:
                break;
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
                imageview = new ImageView(DynamicDetailActivity.this);
                imageview.setImageResource(R.mipmap.placeholder);
                imageview.setLayoutParams(new GridView.LayoutParams(235, 235));
                imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageview.setPadding(6, 6, 6, 6);
                AutoUtils.autoSize(imageview);
            } else {
                imageview = (ImageView) convertView;
            }
            Picasso.with(DynamicDetailActivity.this).load(mStrings[position]).into(imageview);
            return imageview;
        }
    }

    class MyAdapter extends MyBaseAdapter {

        MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(DynamicDetailActivity.this, R.layout.item_commentlistview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            CommentList.ResultBean resultBean = mCommentList.get(position);
            StringBuilder stringBuilder = new StringBuilder();
            String nickName = resultBean.getNickName();
            List<CommentList.ResultBean.RepliesBean> replies = resultBean.getReplies();
            if (replies != null) {

            }
            return convertView;
        }

    }

    static class ViewHolder {
        @Bind(R.id.text)
        TextView mText;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
