package com.chinalooke.yuwan.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.SearchActivity;
import com.chinalooke.yuwan.adapter.MainPagerAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;

public class BattleFieldFragment extends Fragment {


    @Bind(R.id.iv_qcode)
    ImageView mIvQcode;
    @Bind(R.id.rl_search)
    RelativeLayout mRlSearch;
    @Bind(R.id.banner)
    BGABanner mBanner;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battlefield, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        FragmentManager fragmentManager = getChildFragmentManager();
        mFragments.add(new AgainstFragment());
        mFragments.add(new ProceedFragment());
        mFragments.add(new FinishFragment());
        mViewPager.setAdapter(new MainPagerAdapter(fragmentManager, mFragments));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.iv_search, R.id.tv_search, R.id.iv_qcode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.tv_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.iv_qcode:
                break;
        }
    }


//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mQueue = ((MainActivity) getActivity()).getQueue();
//        mWindowManager = (WindowManager) mMainActivity.getSystemService(Context.WINDOW_SERVICE);
//        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
//        mRadioLayout = (LinearLayout) mMainActivity.findViewById(R.id.radiogroup);
//        mToast = YuwanApplication.getToast();
//        initView();
//        initEvent();
//    }
//
//    private void getSetoutData() {
//        if (NetUtil.is_Network_Available(getActivity())) {
//
//            StringRequest stringRequest = new StringRequest(getGameDeskListWithStatus + "&gameStatus=" + mCurrent
//                    + "&pageNo=" + mPage + "&pageSize=3",
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<GameDesk>() {
//                            }.getType();
//                            mGameDesk = gson.fromJson(response, type);
//                            if (!mGameDesk.isSuccess()) {
//                                mToast.setText("获取数据失败!");
//                                mToast.show();
//                            } else {
//                                if (mGameDesk.getResult() == null || mGameDesk.getResult().size() == 0) {
//                                    if (!isLoading) {
//                                        mTvBattlefiledNodata.setVisibility(View.VISIBLE);
//                                    } else {
//                                        isLoading = false;
//                                    }
//                                } else {
//                                    mTvBattlefiledNodata.setVisibility(View.GONE);
//                                    mResult.addAll(mGameDesk.getResult());
//                                    mMyAdapter.notifyDataSetChanged();
//                                    mPage++;
//                                    isLoading = false;
//                                }
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    mToast.setText("获取数据失败!");
//                    mToast.show();
//                }
//            });
//            mQueue.add(stringRequest);
//        } else {
//            mTvBattlefiledNodata.setText("网络不可用");
//            mTvBattlefiledNodata.setVisibility(View.VISIBLE);
//        }
//
//    }
//
//
//    private void initEvent() {
//
//
//        mLvBattlefield.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                if (firstVisibleItem + visibleItemCount == totalItemCount && !isLoading) {
//                    loadMore();
//                }
//            }
//        });
//
//        mLvBattlefield.setOnItemClickListener(this);
//        mMyscrollviewBattlefield.setOnScrollListener(this);
//        mRgBattlefield.check(R.id.rb_setout);
//    }
//
//
//    private void loadMore() {
//        isLoading = true;
//        getSetoutData();
//    }
//
//
//    @Override
//    public void onScroll(int scrollY) {
//        if (scrollY >= MyUtils.Dp2Px(getActivity(), 160)) {
//            isDosplay = true;
//            mLlS.setVisibility(View.VISIBLE);
//            switch (mCurrent) {
//                case 0:
//                    mRgBattlefieldS.check(R.id.rb_setout_s);
//                    break;
//                case 1:
//                    mRgBattlefieldS.check(R.id.rb_combat_s);
//                    break;
//                case 2:
//                    mRgBattlefieldS.check(R.id.rb_finish_s);
//                    break;
//
//            }
//        } else {
//            isDosplay = false;
//            mLlS.setVisibility(View.GONE);
//            switch (mCurrent) {
//                case 0:
//                    mRgBattlefield.check(R.id.rb_setout);
//                    break;
//                case 1:
//                    mRgBattlefield.check(R.id.rb_combat);
//                    break;
//                case 2:
//                    mRgBattlefield.check(R.id.rb_finish);
//                    break;
//            }
//        }
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        isFirst = false;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (!isFirst)
//            mCurrent = PreferenceUtils.getPrefInt(getActivity(), "mCurrent", 0);
//    }
//
//    @OnClick(R.id.iv_qcord)
//    public void onClick() {
//        startActivityForResult(new Intent(mMainActivity, QRCodeActivity.class), 0);
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//        GameDesk.ResultBean resultBean = mResult.get(position);
//        if (NetUtil.is_Network_Available(getActivity())) {
//            getGameDeskDetails(resultBean);
//        } else {
//            mToast.setText("网络不可用，请检查连接情况");
//            mToast.show();
//        }
//    }
//
//    /**
//     * 获得游戏桌详情
//     *
//     * @param resultBean
//     */
//    private void getGameDeskDetails(final GameDesk.ResultBean resultBean) {
//
//        final String gameDeskId = resultBean.getGameDeskId();
//
//        StringRequest stringRequest = new StringRequest(getGameDeskWithId + gameDeskId,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        String substring = response.substring(11, 15);
//                        if (substring.equals("true")) {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<GameDeskDetails>() {
//                            }.getType();
//                            mGameDeskDetails = gson.fromJson(response, type);
//                            if (!mGameDeskDetails.isSuccess()) {
//                                MyUtils.showToast(getActivity(), "获取数据失败!");
//                            } else {
//                                mResultBean = mGameDeskDetails.getResult();
//                                PreferenceUtils.setPrefInt(getActivity(), "mCurrent", mCurrent);
//                                Intent intent = new Intent();
//                                intent.putExtra("gameDeskId", gameDeskId);
//                                String ownerName = resultBean.getNetBarId();
//                                if (!TextUtils.isEmpty(ownerName))
//                                    intent.putExtra("netBarId", ownerName);
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("mResultBean", mResultBean);
//                                intent.putExtras(bundle);
//                                intent.setClass(getActivity(), GameDeskActivity.class);
//                                startActivity(intent);
//                            }
//
//                        } else {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                mToast.setText(jsonObject.getString("Msg"));
//                                mToast.show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                MyUtils.showToast(getActivity(), "获取数据失败!");
//            }
//        });
//        mQueue.add(stringRequest);
//    }
//
//    @OnClick({R.id.rb_setout_s, R.id.rb_combat_s, R.id.rb_finish_s, R.id.rb_setout, R.id.rb_combat, R.id.rb_finish})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.rb_setout_s:
//                mCurrent = 0;
//                mResult.clear();
//                mLlS.setVisibility(View.GONE);
//                mRgBattlefield.check(R.id.rb_setout);
//                mPage = 1;
//                getSetoutData();
//                break;
//            case R.id.rb_combat_s:
//                mCurrent = 1;
//                mResult.clear();
//                mLlS.setVisibility(View.GONE);
//                mRgBattlefield.check(R.id.rb_combat);
//                mPage = 1;
//                getSetoutData();
//                break;
//            case R.id.rb_finish_s:
//                mCurrent = 2;
//                mResult.clear();
//                mRgBattlefield.check(R.id.rb_finish);
//                mLlS.setVisibility(View.GONE);
//                mPage = 1;
//                getSetoutData();
//                break;
//            case R.id.rb_setout:
//                mResult.clear();
//                mCurrent = 0;
//                mPage = 1;
//                getSetoutData();
//                break;
//            case R.id.rb_combat:
//                mResult.clear();
//                mCurrent = 1;
//                mPage = 1;
//                getSetoutData();
//                break;
//            case R.id.rb_finish:
//                mResult.clear();
//                mCurrent = 2;
//                mPage = 1;
//                getSetoutData();
//                break;
//        }
//    }
//
//
//    private class MyAdapter extends BaseAdapter {
//
//
//        @Override
//        public int getCount() {
//            return mResult.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder;
//            if (convertView == null) {
//                convertView = View.inflate(getActivity(), R.layout.item_fragment_battlefield_listview, null);
//                viewHolder = new ViewHolder(convertView);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            viewHolder.mTvDeskName.setCompoundDrawables(mPeopleDrawble, null, null, null);
//            viewHolder.mTvDeskMoney.setCompoundDrawables(moneyDrawble, null, null, null);
//            viewHolder.mTvDeskTime.setCompoundDrawables(timeDrawble, null, null, null);
//            viewHolder.mTvDeskLocation.setCompoundDrawables(locationDrawble, null, null, null);
//            setDetails(viewHolder, position, mResult);
//            return convertView;
//        }
//
//
//    }
//
//
//    private void setDetails(ViewHolder viewHolder, int position, List<GameDesk.ResultBean> list) {
//        final GameDesk.ResultBean mResultBean = list.get(position);
//        Picasso.with(getActivity()).load(mResultBean.getGameImage()).resize(200, 200).centerCrop().into(viewHolder.mIvGamedesk);
//        viewHolder.mTvDeskLocation.setText(mResultBean.getNetBarName());
//        viewHolder.mTvDeskMoney.setText(mResultBean.getGamePay());
//        String ownerName = mResultBean.getOwnerName();
//        if (TextUtils.isEmpty(ownerName)) {
//            viewHolder.mTvDeskName.setText("官方");
//        } else {
//            viewHolder.mTvDeskName.setText(ownerName);
//        }
//        viewHolder.mTvDeskTime.setText(mResultBean.getStartTime());
//
//    }
//
//    static class ViewHolder {
//        @Bind(R.id.iv_gamedesk)
//        ImageView mIvGamedesk;
//        @Bind(R.id.tv_desk_name)
//        TextView mTvDeskName;
//        @Bind(R.id.tv_desk_time)
//        TextView mTvDeskTime;
//        @Bind(R.id.tv_desk_location)
//        TextView mTvDeskLocation;
//        @Bind(R.id.tv_desk_money)
//        TextView mTvDeskMoney;
//
//
//        ViewHolder(View view) {
//            ButterKnife.bind(this, view);
//        }
//    }
//
//
//    private void initView() {
//        mBattlefieldCarouseview.setImagesRes(Constant.battleFieldImage);
//        mSearchDrawable = ImageUtils.setDrwableSize(getActivity(), R.mipmap.unsearch, 38);
//        EditText viewById = (EditText) getActivity().findViewById(R.id.search_et_input);
//        viewById.setCompoundDrawables(mSearchDrawable, null, null, null);
//        mMyAdapter = new MyAdapter();
//        mLvBattlefield.setAdapter(mMyAdapter);
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ButterKnife.unbind(this);
//    }

}
