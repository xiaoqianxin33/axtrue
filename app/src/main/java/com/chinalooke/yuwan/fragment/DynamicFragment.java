package com.chinalooke.yuwan.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompatBase;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.MainActivity;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.Dynamic;
import com.chinalooke.yuwan.model.UserInfo;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.view.CircleImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DynamicFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.lv_dynamic)
    ListView mLvDynamic;
    @Bind(R.id.sr)
    SwipeRefreshLayout mSr;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.tv_no)
    TextView mTvNo;


    private String mParam1;
    private String mParam2;
    private int mPage = 1;
    private boolean isLoading = false;
    private RequestQueue mQueue;
    private UserInfo mUserInfo;
    private boolean isFirst = true;
    private List<Dynamic.ResultBean> mDynamics = new ArrayList<>();
    private Toast mToast;
    private MyListAdapater mMyListAdapater;
    private float mWidth;


    public static DynamicFragment newInstance(String param1, String param2) {
        DynamicFragment fragment = new DynamicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mQueue = ((MainActivity) getActivity()).getQueue();
        mUserInfo = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        mToast = YuwanApplication.getToast();
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
        initHead();
        mMyListAdapater = new MyListAdapater();
        mLvDynamic.setAdapter(mMyListAdapater);
        initEvent();
    }

    private void initHead() {
        ImageView imageView = new ImageView(getActivity());
        imageView.setLayoutParams(new AbsListView.LayoutParams((MyUtils.Dp2Px(getActivity(), mWidth)), MyUtils.Dp2Px(getActivity(), 160)));
        Picasso.with(getActivity()).load(R.mipmap.dynamicbackgroud).resize((MyUtils.Dp2Px(getActivity(), mWidth)), MyUtils.Dp2Px(getActivity(), 160)).centerCrop().into(imageView);
        mLvDynamic.addHeaderView(imageView);
    }

    private void initEvent() {
        mSr.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                mDynamics.clear();
                initData();
                mSr.setRefreshing(false);
            }
        });

        mLvDynamic.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLvDynamic != null && mLvDynamic.getChildCount() > 0) {
                    boolean enable = (firstVisibleItem == 0) && (view.getChildAt(firstVisibleItem).getTop() == 0);
                    mSr.setEnabled(enable);
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        isLoading = true;
        initData();
    }


    private void initData() {
        if (NetUtil.is_Network_Available(getActivity())) {
            String uri;
            if (mUserInfo != null) {
                uri = Constant.HOST + "getActiveList&pageNo=" + mPage + "&pageSize=4&userId"
                        + mUserInfo.getUserId();
            } else {
                uri = Constant.HOST + "getActiveList&pageNo=" + mPage + "&pageSize=4";
            }

            StringRequest stringRequest = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressBar.setVisibility(View.GONE);
                    String substring = response.substring(11, 15);
                    if ("true".equals(substring)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Dynamic>() {
                        }.getType();
                        Dynamic dynamic = gson.fromJson(response, type);
                        if (dynamic != null) {
                            mTvNo.setVisibility(View.GONE);
                            mDynamics.addAll(dynamic.getResult());
                            mMyListAdapater.notifyDataSetChanged();
                            mPage++;
                        } else {
                            if (isFirst) {
                                mTvNo.setText("没有动态");
                                mTvNo.setVisibility(View.VISIBLE);
                            } else {
                                mTvNo.setVisibility(View.GONE);
                                mToast.setText("没有更多了");
                                mToast.show();
                            }
                        }
                    } else {
                        if (isFirst) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String msg = jsonObject.getString("Msg");
                                mToast.setText(msg);
                                mToast.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mToast.setText("没有更多了");
                            mToast.show();
                        }
                    }
                    isLoading = false;
                    isFirst = false;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (isFirst) {
                        mTvNo.setVisibility(View.VISIBLE);
                        mTvNo.setText("加载失败");
                        mProgressBar.setVisibility(View.GONE);
                        isFirst = false;
                    }
                }
            });
            mQueue.add(stringRequest);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTvNo.setText("网络不可用");
            mTvNo.setVisibility(View.VISIBLE);
        }

    }

    class MyListAdapater extends BaseAdapter {
        private boolean loginUserLike = false;

        @Override
        public int getCount() {
            return mDynamics.size();
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
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_dynamic_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Dynamic.ResultBean resultBean = mDynamics.get(position);
            Picasso.with(getActivity()).load(resultBean.getHeadImg()).resize(MyUtils.Dp2Px(getActivity(), 32)
                    , MyUtils.Dp2Px(getActivity(), 32)).centerCrop().into(viewHolder.mCircleImageView);
            viewHolder.mTvContent.setText(resultBean.getContent());
            viewHolder.mTvNameUp.setText(resultBean.getNickName());
            viewHolder.mTvName.setText(resultBean.getNickName());
            viewHolder.mTvTime.setText(resultBean.getCreateTime().substring(0, 10));
            viewHolder.mTvDianzan.setText(resultBean.getLikes());
            viewHolder.mTextView6.setText(resultBean.getComments());
            viewHolder.mTvLocation.setText(resultBean.getAddress());
            String images = resultBean.getImages();
            if (!TextUtils.isEmpty(images)) {
                String[] split = images.split(",");
                viewHolder.mGdDynamic.setAdapter(new GridAdapter(split));
            }


            if (mUserInfo != null) {
                loginUserLike = resultBean.isLoginUserLike();
                if (loginUserLike) {
                    viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzanhou);
                } else {
                    viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                }

                viewHolder.mIvDianzan.setOnClickListener(new View.OnClickListener() {
                    private boolean isLike = loginUserLike;

                    @Override
                    public void onClick(View v) {
                        if (isLike) {
                            viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                            isLike = false;
                            addFavour("delFavour", resultBean.getActiveId(), viewHolder, isLike);
                        } else {
                            viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzanhou);
                            isLike = true;
                            addFavour("addFavour", resultBean.getActiveId(), viewHolder, isLike);
                        }
                    }
                });
            } else {
                viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
            }
            return convertView;
        }

    }


    private boolean isSuccess = false;

    private void addFavour(String s, String avtiveId, final ViewHolder viewHolder, boolean isLike) {

        String uri = Constant.HOST + "addFavour&" + s + "=" + avtiveId + " & userId = " + mUserInfo.getUserId();
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            boolean result = jsonObject.getBoolean("Result");
                            if (result) {
                                mToast.setText("点赞成功");
                                mToast.show();
                            }
                        } else {
                            String msg = jsonObject.getString("Msg");
                            mToast.setText("点赞失败," + msg);
                            mToast.show();
                            viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        isSuccess = false;
                    }
                } else {
                    mToast.setText("点赞失败");
                    mToast.show();
                    viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                    isSuccess = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mToast.setText("点赞失败");
                mToast.show();
                viewHolder.mIvDianzan.setImageResource(R.mipmap.dianzan);
                isSuccess = false;
            }
        });
        mQueue.add(request);
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
                imageview = new ImageView(getActivity());
                imageview.setImageResource(R.mipmap.placeholder);
                imageview.setLayoutParams(new GridView.LayoutParams(MyUtils.Dp2Px(getActivity()
                        , 80), MyUtils.Dp2Px(getActivity(), 80)));
                imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageview.setPadding(6, 6, 6, 6);
            } else {
                imageview = (ImageView) convertView;
            }
            Picasso.with(getActivity()).load(mStrings[position]).into(imageview);
            return imageview;
        }
    }

    static class ViewHolder {
        @Bind(R.id.circleImageView)
        CircleImageView mCircleImageView;
        @Bind(R.id.tv_name_up)
        TextView mTvNameUp;
        @Bind(R.id.tv_time)
        TextView mTvTime;
        @Bind(R.id.tv_name)
        TextView mTvName;
        @Bind(R.id.tv_content)
        TextView mTvContent;
        @Bind(R.id.gd_dynamic)
        GridView mGdDynamic;
        @Bind(R.id.imageView3)
        ImageView mImageView3;
        @Bind(R.id.tv_location)
        TextView mTvLocation;
        @Bind(R.id.iv_pinglun)
        ImageView mIvPinglun;
        @Bind(R.id.textView6)
        TextView mTextView6;
        @Bind(R.id.iv_dianzan)
        ImageView mIvDianzan;
        @Bind(R.id.tv_dianzan)
        TextView mTvDianzan;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.back_personal_info)
    public void onClick() {
    }
}
