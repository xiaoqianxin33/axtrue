package com.chinalooke.yuwan.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bigkoo.pickerview.OptionsPickerView;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.AddFriendActivity;
import com.chinalooke.yuwan.activity.FrequentlyGameActivity;
import com.chinalooke.yuwan.activity.LoginActivity;
import com.chinalooke.yuwan.activity.MainActivity;
import com.chinalooke.yuwan.activity.PersonalInfoActivity;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.FriendInfo;
import com.chinalooke.yuwan.bean.GameMessage;
import com.chinalooke.yuwan.bean.GameRules;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.SortModel;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.engine.PickerEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.KeyboardUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class YueZhanFragment extends Fragment {
    @Bind(R.id.iv_back)
    FrameLayout mIvBack;
    @Bind(R.id.iv_arrow_head)
    ImageView mIvArrowHead;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.tv_game_name)
    TextView mTvGameName;
    @Bind(R.id.iv_gameimage)
    RoundedImageView mIvGameimage;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_people)
    TextView mTvPeople;
    @Bind(R.id.tv_money)
    TextView mTvMoney;
    @Bind(R.id.tv_friends)
    TextView mTvFriends;
    @Bind(R.id.tv_times)
    TextView mTvTimes;
    @Bind(R.id.tv_playerLevel)
    TextView mTvPlayerLevel;

    private Toast mToast;
    private LoginUser.ResultBean mUsrInfo;
    private int CHOOSE_GAME = 1;
    private Date mBeginDate;
    private ArrayList<String> mPeopleNumberList = new ArrayList<>();
    private ArrayList<String> mMoneyList = new ArrayList<>();
    private ArrayList<String> mTimesList = new ArrayList<>();
    private List<GameRules.ResultBean> mGameRulesList = new ArrayList<>();
    private GameMessage.ResultBean mChoseGame;
    private int ADD_FRIENDS = 2;
    private String mRule;
    private boolean isPeopleChose = false;
    private boolean isGameChose = false;
    private boolean isTimeChose = false;
    private boolean isMoneyChose = false;
    View view;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (isPeopleChose && isGameChose && isMoneyChose && isTimeChose) {
                    mTvSkip.setEnabled(true);
                    mTvSkip.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                } else {
                    mTvSkip.setEnabled(false);
                    mTvSkip.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey));
                }
            }
            super.handleMessage(msg);
        }
    };
    private String mGameId;
    private List<SortModel> mChoseFriends;
    private RequestQueue mQueue;
    private ProgressDialog mProgressDialog;
    private MainActivity mMainActivity;
    private int minLevel = -1;
    private int maxLevel;
    private MyAdapter mMyAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_yue_zhan, container, false);
        ButterKnife.bind(this, view);
        mToast = YuwanApplication.getToast();
        mQueue = YuwanApplication.getQueue();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mTvPeople.addTextChangedListener(new CustomerTextWatcher(0));
        mTvTime.addTextChangedListener(new CustomerTextWatcher(1));
        mTvGameName.addTextChangedListener(new CustomerTextWatcher(2));
        mTvMoney.addTextChangedListener(new CustomerTextWatcher(3));
    }

    class CustomerTextWatcher implements TextWatcher {
        private int isChose;

        CustomerTextWatcher(int isChose) {
            this.isChose = isChose;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (isChose) {
                case 0:
                    isPeopleChose = !TextUtils.isEmpty(s);
                    break;
                case 1:
                    isTimeChose = !TextUtils.isEmpty(s);
                    break;
                case 2:
                    isGameChose = !TextUtils.isEmpty(s);
                    break;
                case 3:
                    isMoneyChose = !TextUtils.isEmpty(s);
                    break;
            }

            mHandler.sendEmptyMessage(1);
        }
    }

    private void initData() {
        mUsrInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(mMainActivity, LoginUserInfoUtils.KEY);
        if (mUsrInfo != null) {
            gameID = mUsrInfo.getGameId();
        }
    }

    private void initView() {
        mIvBack.setVisibility(View.GONE);
        mIvArrowHead.setVisibility(View.GONE);
        mTvTitle.setText("约战");
        mTvSkip.setText("发布");
        mTvSkip.setTextColor(ContextCompat.getColor(mMainActivity, R.color.grey));
        mTvSkip.setEnabled(false);
        mMyAdapter = new MyAdapter(mGameRulesList);
    }

    @Override
    public void onPause() {
        super.onPause();
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    String[] gameID;

    @OnClick({R.id.rl_game_name, R.id.rl_time, R.id.rl_people, R.id.rl_money,
            R.id.tv_skip, R.id.rl_friend, R.id.rl_rule, R.id.rl_times, R.id.rl_playerLevel})
    public void onClick(View view) {
        if (mUsrInfo == null) {
            startActivity(new Intent(mMainActivity, LoginActivity.class));
        } else {
            switch (view.getId()) {
                case R.id.rl_playerLevel:
                    PickerEngine.alertLevelPicker(mMainActivity, new OptionsPickerView.OnOptionsSelectListener() {
                        @Override
                        public void onOptionsSelect(int options1, int option2, int options3) {
                            minLevel = options1 + 1;
                            maxLevel = options1 + option2 + 1;
                            mTvPlayerLevel.setText("最低" + minLevel + ",最高" + maxLevel);
                        }
                    });
                    break;
                case R.id.rl_times:
                    alertPicker(mTimesList, "选择游戏场数", 3);
                    break;
                case R.id.tv_skip:
                    createRoom();
                    break;
                case R.id.rl_game_name:
                    if (gameID == null) {
                        MyUtils.showCustomDialog(mMainActivity, "提示", "还没有添加常玩游戏无法约战，现在就去添加常玩游戏么?"
                                , "不了", "好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(mMainActivity, PersonalInfoActivity.class));
                                    }
                                });
                    } else {
                        Intent intent = new Intent(mMainActivity, FrequentlyGameActivity.class);
                        intent.putExtra("isYueZhan", true);
                        startActivityForResult(intent, CHOOSE_GAME);
                    }

                    break;
                case R.id.rl_time:
                    alertTimePicker();
                    break;
                case R.id.rl_people:
                    if (mPeopleNumberList.size() == 0) {
                        mToast.setText("请先选择游戏");
                        mToast.show();
                    } else {
                        alertPicker(mPeopleNumberList, "选择游戏参与人数", 0);
                    }
                    break;
                case R.id.rl_money:
                    if (mChoseGame != null) {
                        alertPicker(mMoneyList, "选择游戏币金额", 1);
                    } else {
                        mToast.setText("请先选择游戏");
                        mToast.show();
                    }
                    break;
                case R.id.rl_friend:
                    if (mChoseGame != null) {
                        String maxPeopleNumber = mChoseGame.getMaxPeopleNumber();
                        Intent intent = new Intent(mMainActivity, AddFriendActivity.class);
                        intent.putExtra("maxPeopleNumber", maxPeopleNumber);
                        startActivityForResult(intent, ADD_FRIENDS);
                    } else {
                        mToast.setText("请先选择游戏");
                        mToast.show();
                    }
                    break;
                case R.id.rl_rule:
                    showRuleDialog();
                    break;
            }
        }

    }

    //创建环信群组
    private void createRoom() {
        if (minLevel < 0) {
            mToast.setText("请选择参战最大最小积分等级");
            mToast.show();
            return;
        }
        EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
        option.maxUsers = 200;
        option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
        try {
            EMGroup group = EMClient.getInstance().groupManager().createGroup(mChoseGame.getName(), "对战群组", null, null, option);
            createDesk(group);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    //提交发布战场
    private void createDesk(EMGroup group) {
        if (NetUtil.is_Network_Available(mMainActivity)) {
            mProgressDialog = MyUtils.initDialog("正在提交", mMainActivity);
            mProgressDialog.show();
            String time = mTvTime.getText().toString();
            String people = mTvPeople.getText().toString();
            String money = mTvMoney.getText().toString();
            String times = mTvTimes.getText().toString();
            String uri = Constant.HOST + "addGameDesk&gameId=" + mGameId +
                    "&playerNum=" + Integer.parseInt(people) + "&gamePay=" + money + "&gameCount=" + times
                    + "&ownerId=" + mUsrInfo.getUserId() + "&roomId=" + group.getGroupId() + "&playerLevel=" + minLevel + "," + maxLevel;
            if (mRule != null) {
                uri = uri + "&gameRule=" + mRule;
            }

            if (mChoseFriends != null) {
                StringBuilder stringBuffer = new StringBuilder();
                for (int i = 0; i < mChoseFriends.size(); i++) {
                    String userId = mChoseFriends.get(i).getFriend().getUserId();
                    stringBuffer.append(userId);
                    if (i != mChoseFriends.size() - 1)
                        stringBuffer.append(",");
                }
                uri = uri + "&userId=" + stringBuffer.toString();
            }

            uri = uri + "&startTime=" + time;
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressDialog.dismiss();
                    if (AnalysisJSON.analysisJson(response)) {
                        MyUtils.showDialog(mMainActivity, "提示", "您已发起了约战\r系统将自动扣除相应金额", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        mTvGameName.setText("");
                        mTvPeople.setText("");
                        mTvMoney.setText("");
                        mTvFriends.setText("");
                        mTvTime.setText("");
                        mRule = "";
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("Msg");
                            mToast.setText(msg);
                            mToast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }
    }

    private void showRuleDialog() {
        final Dialog dialog = new Dialog(mMainActivity, R.style.Dialog);
        View inflate = View.inflate(mMainActivity, R.layout.dialog_add_game_rule, null);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_ok);
        TextView tvCancel = (TextView) inflate.findViewById(R.id.tv_cancel);
        final EditText etRule = (EditText) inflate.findViewById(R.id.et_rule);
        GridView gridView = (GridView) inflate.findViewById(R.id.gridView);
        gridView.setAdapter(mMyAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GameRules.ResultBean resultBean = mGameRulesList.get(position);
                String content = resultBean.getContent();
                if (!TextUtils.isEmpty(content))
                    etRule.setText(content);
            }
        });
        if (!TextUtils.isEmpty(mRule))
            etRule.setText(mRule);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                KeyboardUtils.hideSoftInput(mMainActivity);
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mRule = etRule.getText().toString();
                KeyboardUtils.hideSoftInput(mMainActivity);
            }
        });
        AutoUtils.autoSize(inflate);
        dialog.setContentView(inflate);
        dialog.show();
        etRule.requestFocus();
        WindowManager windowManager = mMainActivity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    private void alertPicker(ArrayList<String> list, String title, final int type) {
        OptionsPickerView<String> optionsPickerView = new OptionsPickerView<>(mMainActivity);
        optionsPickerView.setTitle(title);
        optionsPickerView.setPicker(list);
        optionsPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                switch (type) {
                    case 0:
                        mTvPeople.setText(mPeopleNumberList.get(options1));
                        break;
                    case 1:
                        mTvMoney.setText(mMoneyList.get(options1));
                        break;
                    case 3:
                        mTvTimes.setText(mTimesList.get(options1));
                }
            }
        });
        optionsPickerView.setCyclic(false);
        optionsPickerView.show();
    }

    private void alertTimePicker() {
        OptionsPickerView<String> optionsPickerView = new OptionsPickerView<>(mMainActivity);
        optionsPickerView.setTitle("选择开战时间");
        final ArrayList<String> dayList = new ArrayList<>();
        final ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
        final ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
        dayList.add("今天");
        dayList.add("明天");
        dayList.add("后天");
        for (int i = 0; i < 3; i++) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (int j = 0; j < 24; j++) {
                arrayList.add(j + "");
            }
            options2Items.add(arrayList);
        }

        for (int k = 0; k < 3; k++) {
            ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
            for (int j = 0; j < 24; j++) {
                ArrayList<String> arrayList = new ArrayList<>();
                for (int i = 0; i < 60; i++) {
                    arrayList.add(i + "");
                }
                arrayLists.add(arrayList);
            }
            options3Items.add(arrayLists);
        }
        optionsPickerView.setPicker(dayList, options2Items, options3Items, true);

        optionsPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                switch (options1) {
                    case 0:
                        mBeginDate = DateUtils.getCurrentDay(new Date());
                        break;
                    case 1:
                        mBeginDate = DateUtils.getNextDay(new Date());
                        break;
                    case 2:
                        mBeginDate = DateUtils.getNextNextDay(new Date());
                        break;
                }

                String hour = options2Items.get(options1).get(option2);
                if (hour.length() == 1)
                    hour = "0" + hour;
                String minute = options3Items.get(0).get(0).get(options3);
                if (minute.length() == 1)
                    minute = "0" + minute;
                mTvTime.setText(DateUtils.getFormatShortTime(mBeginDate) + "     " + hour + ":" + minute);
            }
        });
        optionsPickerView.setLabels("", "时", "分");
        optionsPickerView.setCyclic(false);
        optionsPickerView.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_GAME) {
            if (data != null) {
                mChoseGame = (GameMessage.ResultBean) data.getSerializableExtra("choseGame");
                String thumb = mChoseGame.getThumb();
                mGameId = mChoseGame.getGameId();
                if (!TextUtils.isEmpty(mGameId)) {
                    getGameRules();
                }
                if (!TextUtils.isEmpty(thumb)) {
                    String loadImageUrl = ImageEngine.getLoadImageUrl(mMainActivity, thumb, 60, 60);
                    Picasso.with(mMainActivity).load(loadImageUrl).into(mIvGameimage);
                }
                String name = mChoseGame.getName();
                if (!TextUtils.isEmpty(name))
                    mTvGameName.setText(name);
                setPeopleCount(mChoseGame);
                setMoneyCount();
                setTimesCount();
            }
        } else if (requestCode == ADD_FRIENDS) {
            if (data != null) {
                mChoseFriends = (List<SortModel>) data.getSerializableExtra("mChose");
                setFriends(mChoseFriends);
            }
        }
    }

    //取游戏规则
    private void getGameRules() {
        String url = Constant.HOST + "getGameRules&gameId=" + mGameId;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (AnalysisJSON.analysisJson(response)) {
                    Gson gson = new Gson();
                    GameRules gameRules = gson.fromJson(response, GameRules.class);
                    if (gameRules != null) {
                        List<GameRules.ResultBean> result = gameRules.getResult();
                        if (result != null && result.size() != 0) {
                            mGameRulesList.clear();
                            mGameRulesList.addAll(result);
                            if (mMyAdapter != null)
                                mMyAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }, null);

        mQueue.add(request);
    }

    private void setTimesCount() {
        String times = mChoseGame.getTimes();
        if (!TextUtils.isEmpty(times)) {
            int parseInt = Integer.parseInt(times);
            for (int i = 1; i <= parseInt; i++) {
                mTimesList.add(i + "");
            }
        } else {
            mTimesList.add("1");
        }
    }

    //处理选择好友结果
    private void setFriends(List<SortModel> choseFriends) {
        StringBuilder stringBuilder = new StringBuilder();
        for (SortModel sortModel : choseFriends) {
            FriendInfo.ResultBean friend = sortModel.getFriend();
            String nickName = friend.getNickName();
            stringBuilder.append(nickName).append(",");
        }
        mTvFriends.setText(stringBuilder.toString());
    }

    //设置参与游戏币范围
    private void setMoneyCount() {
        String wagerMin = mChoseGame.getWagerMin();
        String wagerMax = mChoseGame.getWagerMax();
        if (!TextUtils.isEmpty(wagerMin) && !TextUtils.isEmpty(wagerMax)) {
            double min = Double.parseDouble(wagerMin);
            double max = Double.parseDouble(wagerMax);
            mMoneyList.add(min + "");
            int imin = (int) min;
            for (int i = imin + 10; i < max; i = i + 10) {
                mMoneyList.add(i + "");
            }
            mMoneyList.add(max + "");
        }
    }

    //设置游戏人数范围
    private void setPeopleCount(GameMessage.ResultBean choseGame) {
        String maxPeopleNumber = choseGame.getMaxPeopleNumber();
        if (!TextUtils.isEmpty(maxPeopleNumber)) {
            int maxPeople = Integer.parseInt(maxPeopleNumber);
            for (int i = 2; i < maxPeople + 1; i = i + 2) {
                mPeopleNumberList.add(i + "");
            }
        }
    }

    class MyAdapter extends MyBaseAdapter {

        public MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mMainActivity, R.layout.item_game_rule_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            GameRules.ResultBean resultBean = mGameRulesList.get(position);
            String content = resultBean.getTitle();
            if (content.length() > 5) {
                content = content.substring(0, 5);
            }
            viewHolder.mTvGameRule.setText(content);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tv_gameRule)
            TextView mTvGameRule;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

}
