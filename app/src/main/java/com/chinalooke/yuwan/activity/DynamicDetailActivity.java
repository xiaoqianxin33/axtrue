package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.CommentList;
import com.chinalooke.yuwan.bean.Dynamic;
import com.chinalooke.yuwan.bean.LikeList;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.WholeDynamic;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.KeyboardUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    @Bind(R.id.et_comment)
    EditText mEtComment;
    @Bind(R.id.rl_comment)
    RelativeLayout mRlComment;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    private WholeDynamic.ResultBean mDynamic;
    private LoginUser.ResultBean mUserInfo;
    private String activeType;
    private RequestQueue mQueue;
    private String[] mSplit;
    private MyAdapter mMyAdapter;
    private Handler mHandler = new Handler();
    private ProgressDialog mProgressDialog;
    private Toast mToast;
    private String mCommentId;
    private int mDynamic_type;
    private Dynamic.ResultBean.ListBean mDynamicList;
    private boolean mIsJoin;
    private List<CommentList.ResultBean> mComments = new ArrayList<>();
    private int COMMENT_TYPE;
    private boolean mIsLoginUserLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_detail);
        ButterKnife.bind(this);
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mMyAdapter = new MyAdapter(mComments);
        mListView.setAdapter(mMyAdapter);
        mProgressDialog = MyUtils.initDialog("提交中", this);
        initData();
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

        //评论框输入监听
        mEtComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String comment = mEtComment.getText().toString();
                    KeyboardUtils.hideSoftInput(DynamicDetailActivity.this);
                    mRlComment.setVisibility(View.GONE);
                    String activeId = null;
                    switch (mDynamic_type) {
                        case 0:
                            activeId = mDynamic.getActiveId();
                            break;
                        case 1:
                            activeId = mDynamicList.getActiveId();
                            break;
                    }
                    if (!TextUtils.isEmpty(comment)) {
                        if (COMMENT_TYPE == 0) {
                            sendComment(comment, 0, activeId);
                        } else {
                            sendComment(comment, 1, activeId);
                        }
                    }
                    mEtComment.setText("");
                    return true;
                }
                return false;
            }
        });

        //listView item点击监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mUserInfo != null) {
                    CommentList.ResultBean comment = mComments.get(position);
                    mCommentId = comment.getCommentId();
                    String replayName = comment.getNickName();
                    COMMENT_TYPE = 1;
                    addComment(replayName);
                } else {
                    mToast.setText("需登录才可以发表评论");
                    mToast.show();
                }
            }
        });
    }

    //发表评论
    private void sendComment(String comment, int i, final String activeId) {
        mProgressDialog.show();
        String url = null;
        switch (i) {
            case 0:
                try {
                    url = Constant.HOST + "sendComment&activeId=" + activeId + "&userId=" + mUserInfo.getUserId() + "&commentContent=" + URLEncoder.encode(comment, "utf8")
                            + "&activeType=" + activeType;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    url = Constant.HOST + "replyComment&commentId=" + mCommentId + "&replyContent=" + URLEncoder.encode(comment, "utf8") + "&userId=" + mUserInfo.getUserId() + "&activeType=" + activeType;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("Success");
                    if (success) {
                        mToast.setText("评论成功！");
                        mToast.show();
                        mComments.clear();
                        getCommentList(activeId);
                    } else {
                        String msg = jsonObject.getString("Msg");
                        mToast.setText(msg);
                        mToast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("网络不给力，请稍后再试");
                mToast.show();
            }
        });
        mQueue.add(request);

    }

    private void initView(Object object) {
        String headImg;
        String nickName;
        String createTime;
        String content;
        String images;
        String likes;
        String comments;
        String address;
        boolean isLoginUserLike;
        if (mDynamic_type == 0) {
            WholeDynamic.ResultBean dynamic = (WholeDynamic.ResultBean) object;
            headImg = dynamic.getHeadImg();
            nickName = dynamic.getNickName();
            createTime = dynamic.getCreateTime();
            content = dynamic.getContent();
            images = dynamic.getImages();
            likes = dynamic.getLikes();
            comments = dynamic.getComments();
            address = dynamic.getAddress();
            isLoginUserLike = dynamic.isIsLoginUserLike();

        } else {
            Dynamic.ResultBean.ListBean dynamic = (Dynamic.ResultBean.ListBean) object;
            headImg = dynamic.getHeadImg();
            nickName = dynamic.getNickName();
            createTime = dynamic.getAddTime();
            content = dynamic.getContent();
            images = dynamic.getImages();
            likes = dynamic.getLikes();
            comments = dynamic.getComments();
            address = dynamic.getAddress();
            isLoginUserLike = dynamic.isLoginUserLike();
        }
        if (!TextUtils.isEmpty(headImg))
            Picasso.with(getApplicationContext()).load(headImg).into(mRoundedImageView);
        if (!TextUtils.isEmpty(nickName))
            mTvName.setText(nickName);
        if (!TextUtils.isEmpty(createTime))
            mTvTime.setText(createTime);
        if (!TextUtils.isEmpty(content))
            mTvContent.setText(content);
        if (!TextUtils.isEmpty(images)) {
            mSplit = images.split(",");
            mGridView.setAdapter(new GridAdapter(mSplit));
        }
        if (!TextUtils.isEmpty(likes)) {
            mTvDianzan.setText(likes);
        } else {
            mTvDianzan.setText("0");
        }

        if (!TextUtils.isEmpty(comments))
            mTvPinglun.setText(comments);
        else
            mTvPinglun.setText("0");

        if (!TextUtils.isEmpty(address))
            mTvAddress.setText(address);
        if (mUserInfo != null) {
            mIvDianzan.setImageResource(isLoginUserLike ? R.mipmap.dianzanhou : R.mipmap.dianzan);
        } else {
            mIvDianzan.setImageResource(R.mipmap.dianzan);
        }

    }

    private void initData() {
        mDynamic_type = getIntent().getIntExtra("dynamic_type", 0);
        mIsJoin = getIntent().getBooleanExtra("isJoin", false);
        switch (mDynamic_type) {
            case 0:
                mDynamic = (WholeDynamic.ResultBean) getIntent().getSerializableExtra("dynamic");
                activeType = "";
                mIsLoginUserLike = mDynamic.isIsLoginUserLike();
                initView(mDynamic);
                break;
            case 1:
                mDynamicList = (Dynamic.ResultBean.ListBean) getIntent().getSerializableExtra("dynamic");
                activeType = "group";
                mIsLoginUserLike = mDynamicList.isLoginUserLike();
                initView(mDynamicList);
                break;
        }
        if (mDynamic != null) {
            getFavourList(mDynamic.getActiveId());
            getCommentList(mDynamic.getActiveId());
        }

        if (mDynamicList != null) {
            getFavourList(mDynamicList.getActiveId());
            getCommentList(mDynamicList.getActiveId());
        }
    }

    //取得评论列表
    private void getCommentList(String activeId) {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String uri = Constant.HOST + "getCommentList&activeId=" + activeId + "&activeType=" + activeType;
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (AnalysisJSON.analysisJson(response)) {
                        Gson gson = new Gson();
                        CommentList commentList = gson.fromJson(response, CommentList.class);
                        if (commentList != null && commentList.getResult() != null) {
                            List<CommentList.ResultBean> result = commentList.getResult();
                            mComments.addAll(result);
                            Collections.sort(mComments, new Comparator<CommentList.ResultBean>() {
                                @Override
                                public int compare(CommentList.ResultBean lhs, CommentList.ResultBean rhs) {
                                    String laddTime = lhs.getAddTime();
                                    String raddTime = rhs.getAddTime();
                                    if (!TextUtils.isEmpty(laddTime) && !TextUtils.isEmpty(raddTime)) {
                                        Date ldate = DateUtils.getDate(laddTime, "yyyy-MM-dd HH:mm:ss");
                                        Date rdate = DateUtils.getDate(raddTime, "yyyy-MM-dd HH:mm:ss");
                                        assert ldate != null;
                                        if (ldate.before(rdate))
                                            return -1;
                                        else
                                            return 1;
                                    }
                                    return 0;
                                }
                            });
                            mMyAdapter.notifyDataSetChanged();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            mQueue.add(request);
        }
    }

    //取得点赞用户列表
    private void getFavourList(String activeId) {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            String uri = Constant.HOST + "getFavourList&activeId=" + activeId + "&activeType="
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

    @OnClick({R.id.iv_back, R.id.iv_camera, R.id.rl_pinglun, R.id.rl_dianzan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_dianzan:
                if (mUserInfo != null) {
                    switch (mDynamic_type) {
                        case 0:
                            if (mIsLoginUserLike) {
                                addFavour("delFavour", mDynamic.getActiveId());
                            } else {
                                addFavour("addFavour", mDynamic.getActiveId());
                            }
                            break;
                        case 1:
                            if (mIsLoginUserLike) {
                                addFavour("delFavour", mDynamicList.getActiveId());
                            } else
                                addFavour("addFavour", mDynamicList.getActiveId());
                            break;
                    }
                } else {
                    mToast.setText("需登录才可能点赞");
                    mToast.show();
                }
                break;
            case R.id.rl_pinglun:
                COMMENT_TYPE = 0;
                if (mUserInfo != null)
                    switch (mDynamic_type) {
                        case 0:
                            addComment(null);
                            break;
                        case 1:
                            if (mIsJoin) {
                                addComment(null);
                            } else {
                                mToast.setText("本圈子成员才可发表评论");
                                mToast.show();
                            }
                            break;
                    }
                else {
                    mToast.setText("需登录才可发表评论");
                    mToast.show();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_camera:
                break;
        }
    }

    //点赞
    private void addFavour(String s, final String avtiveId) {
        mProgressDialog.show();
        String url = Constant.HOST + s + "&activeId=" + avtiveId + "&userId=" + mUserInfo.getUserId() + "&activeType=" + activeType;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            if (mIsLoginUserLike)
                                mToast.setText("取消点赞成功");
                            else
                                mToast.setText("点赞成功");
                            mToast.show();
                            mIsLoginUserLike = !mIsLoginUserLike;
                            mIvDianzan.setImageResource(mIsLoginUserLike ? R.mipmap.dianzanhou : R.mipmap.dianzan);
                            getFavourList(avtiveId);
                        } else {
                            String msg = jsonObject.getString("Msg");
                            mToast.setText("点赞失败," + msg);
                            mToast.show();
                            mIvDianzan.setImageResource(mIsLoginUserLike ? R.mipmap.dianzanhou : R.mipmap.dianzan);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mToast.setText("点赞失败");
                    mToast.show();
                    mIvDianzan.setImageResource(R.mipmap.dianzan);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("点赞失败");
                mToast.show();
                mIvDianzan.setImageResource(R.mipmap.dianzan);
            }
        });
        mQueue.add(request);
    }

    //评论点击
    private void addComment(String replayName) {
        mRlComment.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(replayName))
            mEtComment.setHint("回复 "+replayName);
        KeyboardUtils.showSoftInput(this, mEtComment);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
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
            String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), mStrings[position], 235, 235);
            Picasso.with(DynamicDetailActivity.this).load(loadImageUrl).into(imageview);
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
                convertView = View.inflate(DynamicDetailActivity.this, R.layout.item_conmment_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            CommentList.ResultBean comment = mComments.get(position);
            String nickName = comment.getNickName();
            String content = comment.getContent();
            String replyName = comment.getReplayName();
            ForegroundColorSpan blueSpan = new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.comment_text));
            ForegroundColorSpan blueSpan1 = new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.comment_text));
            if (!TextUtils.isEmpty(nickName) && !TextUtils.isEmpty(content)) {
                if (TextUtils.isEmpty(replyName)) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(nickName + ":" + content);
                    builder.setSpan(blueSpan, 0, nickName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.mTvContent.setText(builder);
                } else {
                    SpannableStringBuilder builder = new SpannableStringBuilder(nickName + "回复" + replyName + ":" + content);
                    builder.setSpan(blueSpan1, 0, nickName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(blueSpan, nickName.length() + 2, nickName.length() + 2 + replyName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.mTvContent.setText(builder);
                }
            }
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tv_content)
            TextView mTvContent;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }


}
