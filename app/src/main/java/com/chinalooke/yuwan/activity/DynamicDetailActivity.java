package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.Comment;
import com.chinalooke.yuwan.bean.CommentList;
import com.chinalooke.yuwan.bean.LikeList;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.WholeDynamic;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
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
    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.iv_camera)
    ImageView mIvCamera;
    @Bind(R.id.iv_pinglun)
    ImageView mIvPinglun;
    @Bind(R.id.iv1)
    ImageView mIv1;
    @Bind(R.id.ll_like)
    LinearLayout mLlLike;
    @Bind(R.id.et_comment)
    EditText mEtComment;
    @Bind(R.id.rl_comment)
    RelativeLayout mRlComment;
    @Bind(R.id.activity_dynamic_detail)
    LinearLayout mActivityDynamicDetail;
    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    private WholeDynamic.ResultBean mDynamic;
    private LoginUser.ResultBean mUserInfo;
    private String activeType;
    private RequestQueue mQueue;
    private String[] mSplit;
    private MyAdapter mMyAdapter;
    private List<Comment> mList = new ArrayList<>();
    private Handler mHandler = new Handler();
    private ProgressDialog mProgressDialog;
    private Toast mToast;
    private String mUserId;
    private String mCommentId;
    private String mReplyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_detail);
        ButterKnife.bind(this);
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mMyAdapter = new MyAdapter(mList);
        mListView.setAdapter(mMyAdapter);
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

        //评论框输入监听
        mEtComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String comment = mEtComment.getText().toString();
                    KeyboardUtils.hideSoftInput(DynamicDetailActivity.this);
                    mRlComment.setVisibility(View.GONE);
                    Log.e("TAG", mReplyId);
                    if (!TextUtils.isEmpty(comment)) {
                        if (TextUtils.isEmpty(mUserId) && TextUtils.isEmpty(mCommentId) && TextUtils.isEmpty(mReplyId)) {
                            sendComment(comment, 0);
                        } else if (TextUtils.isEmpty(mUserId) && !TextUtils.isEmpty(mCommentId) && TextUtils.isEmpty(mReplyId)) {
                            sendComment(comment, 1);
                        } else if (!TextUtils.isEmpty(mReplyId)) {
                            sendComment(comment, 2);
                            Log.e("TAG", "2");
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
                    Comment comment = mList.get(position);
                    mUserId = comment.getUserId();
                    mCommentId = comment.getCommentId();
                    mReplyId = comment.getReplyId();
                    addComment();
                } else {
                    mToast.setText("需登录才可以发表评论");
                    mToast.show();
                }
            }
        });
    }


    //发表评论
    private void sendComment(String comment, int i) {
        mProgressDialog = MyUtils.initDialog("提交中", this);
        mProgressDialog.show();
        String url = null;
        try {
            if (i == 0) {
                url = Constant.HOST + "sendComment&activeId=" + mDynamic.getActiveId() + "&userId=" + mUserInfo.getUserId()
                        + "&commentContent=" + URLEncoder.encode(comment, "UTF-8");
            } else if (i == 1) {
                url = Constant.HOST + "replyComment&commentId=" + mCommentId + "&userId=" + mUserInfo.getUserId()
                        + "&replyContent=" + URLEncoder.encode(comment, "UTF-8");
            } else if (i == 2) {
                url = Constant.HOST + "replyComment&commentId=" + mCommentId + "&userId=" + mUserInfo.getUserId()
                        + "&replyContent=" + URLEncoder.encode(comment, "UTF-8") + "&replayId=" + mReplyId;
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
                            mList.clear();
                            getCommentList();
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
                    if (AnalysisJSON.analysisJson(response)) {
                        Log.e("TAG", response);
                        Gson gson = new Gson();
                        CommentList commentList = gson.fromJson(response, CommentList.class);
                        if (commentList != null && commentList.getResult() != null) {
                            classifyComment(commentList);
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

    //评论分组
    private void classifyComment(CommentList commentList) {
        List<CommentList.ResultBean> result = commentList.getResult();
        for (CommentList.ResultBean resultBean : result) {
            Comment comment = new Comment();
            String addTime = resultBean.getAddTime();
            if (!TextUtils.isEmpty(addTime)) {
                comment.setAddTime(addTime);
            }
            String nickName = resultBean.getNickName();
            if (!TextUtils.isEmpty(nickName))
                comment.setName(nickName);

            String content = resultBean.getContent();
            if (!TextUtils.isEmpty(content)) {
                comment.setContent(content);
            }
            String commentId = resultBean.getCommentId();
            if (!TextUtils.isEmpty(commentId))
                comment.setCommentId(commentId);

            mList.add(comment);
            List<CommentList.ResultBean.RepliesBean> replies = resultBean.getReplies();
            if (replies != null && replies.size() != 0) {
                for (CommentList.ResultBean.RepliesBean repliesBean : replies) {
                    Comment comment1 = new Comment();
                    comment1.setReplyName(nickName);
                    comment1.setCommentId(commentId);
                    String replyTime = repliesBean.getReplyTime();
                    if (!TextUtils.isEmpty(replyTime))
                        comment1.setAddTime(replyTime);

                    String content1 = repliesBean.getContent();
                    if (!TextUtils.isEmpty(content1)) {
                        comment1.setContent(content1);
                    }

                    String nickName1 = repliesBean.getNickName();
                    if (!TextUtils.isEmpty(nickName1))
                        comment1.setName(nickName1);

                    String userId = repliesBean.getUserId();
                    if (!TextUtils.isEmpty(userId))
                        comment1.setUserId(userId);
                    String commentId1 = repliesBean.getCommentId();
                    if (!TextUtils.isEmpty(commentId1))
                        comment1.setReplyId(commentId1);

                    mList.add(comment1);
                }
            }
        }
        Collections.sort(mList, new Comparator<Comment>() {
            @Override
            public int compare(Comment lhs, Comment rhs) {
                String laddTime = lhs.getAddTime();
                String raddTime = rhs.getAddTime();
                if (!TextUtils.isEmpty(laddTime) && !TextUtils.isEmpty(raddTime)) {
                    Date ldate = DateUtils.getDate(laddTime, "yyyy-MM-dd HH:mm:ss");
                    Date rdate = DateUtils.getDate(raddTime, "yyyy-MM-dd HH:mm:ss");
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

    @OnClick({R.id.iv_back, R.id.iv_camera, R.id.rl_pinglun})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_pinglun:
                if (mUserInfo != null)
                    addComment();
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

    //评论点击
    private void addComment() {
        mRlComment.setVisibility(View.VISIBLE);
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
                convertView = View.inflate(DynamicDetailActivity.this, R.layout.item_conmment_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Comment comment = mList.get(position);
            String nickName = comment.getName();
            String content = comment.getContent();
            String replyName = comment.getReplyName();
            ForegroundColorSpan blueSpan = new ForegroundColorSpan(getResources().getColor(R.color.comment_text));
            ForegroundColorSpan blueSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.comment_text));
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
