package com.chinalooke.yuwan.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.CircleDynamicActivity;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.Circle;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CircleWodeWFragment extends Fragment {


    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.pb_load)
    ProgressBar mPbLoad;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    private LoginUser.ResultBean mUserInfo;
    private RequestQueue mQueue;
    private List<Circle.ResultBean> mCircle = new ArrayList<>();
    private MyAdapter mMyAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_wode_w, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mQueue = YuwanApplication.getQueue();
        mMyAdapter = new MyAdapter(mCircle);
        mListView.setAdapter(mMyAdapter);
        initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getActivity(), LoginUserInfoUtils.KEY);
        initView();
        initData();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Circle.ResultBean resultBean = mCircle.get(position);
                Intent intent = new Intent(getActivity(), CircleDynamicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("circle", resultBean);
                intent.putExtra("circle_type", 0);
                intent.putExtras(bundle);
                startActivity(intent);
                addHits(resultBean);
            }
        });
    }

    //增加圈子点击量
    private void addHits(Circle.ResultBean resultBean) {
        String url = Constant.HOST + "addHits&groupId=" + resultBean.getGroupId();
        StringRequest request = new StringRequest(url, null, null);
        mQueue.add(request);
    }

    private void initView() {
        if (mUserInfo == null) {
            mPbLoad.setVisibility(View.GONE);
            mTvNone.setVisibility(View.VISIBLE);
            mTvNone.setText("请登录查看");
        }
    }

    private void initData() {
        if (mUserInfo != null) {
            if (NetUtil.is_Network_Available(getActivity())) {
                getMyCircle();
            } else {
                mPbLoad.setVisibility(View.GONE);
                mTvNone.setText("网络未连接");
                mTvNone.setVisibility(View.VISIBLE);
            }
        }
    }

    //获得我加入的圈子
    private void getMyCircle() {
        String uri = Constant.HOST + "getGroupListWithType&userId=" + mUserInfo.getUserId() + "&groupType=mygroup";
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mPbLoad.setVisibility(View.GONE);
                if (AnalysisJSON.analysisJson(response)) {
                    mTvNone.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    Type type = new TypeToken<Circle>() {
                    }.getType();
                    Circle circle = gson.fromJson(response, type);
                    if (circle.getResult() != null) {
                        mCircle.clear();
                        mCircle.addAll(circle.getResult());
                        mMyAdapter.notifyDataSetChanged();
                    }
                } else {
                    mTvNone.setVisibility(View.VISIBLE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.getString("Msg");
                        mTvNone.setText(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    class MyAdapter extends MyBaseAdapter {

        MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_circle_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Circle.ResultBean resultBean = mCircle.get(position);
            String headImg = resultBean.getHeadImg();
            if (!TextUtils.isEmpty(headImg)) {
                String loadImageUrl = ImageEngine.getLoadImageUrl(getActivity(), headImg, MyUtils.Dp2Px(getActivity()
                        , 80), MyUtils.Dp2Px(getActivity(), 80));
                Picasso.with(getActivity()).load(loadImageUrl).into(viewHolder.mIvCircleImage);
            }
            viewHolder.mTvCircleName.setText(resultBean.getGroupName());
            viewHolder.mTvCircleDetails.setText(resultBean.getDetails());
            viewHolder.mTvDiscountCircle.setVisibility(View.GONE);
            return convertView;
        }
    }

    static class ViewHolder {
        @Bind(R.id.iv_image)
        ImageView mIvCircleImage;
        @Bind(R.id.tv_name)
        TextView mTvCircleName;
        @Bind(R.id.tv_slogen)
        TextView mTvCircleDetails;
        @Bind(R.id.tv_distance)
        TextView mTvDiscountCircle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}