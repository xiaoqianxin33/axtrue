package com.chinalooke.yuwan.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.FrequentlyGameActivity;
import com.chinalooke.yuwan.activity.LoginActivity;
import com.chinalooke.yuwan.activity.PersonalInfoActivity;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.db.DBManager;
import com.chinalooke.yuwan.model.GameList;
import com.chinalooke.yuwan.model.GameMessage;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.model.NearNetBar;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@linkYueZhanFragmentOnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YueZhanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YueZhanFragment extends Fragment implements AMapLocationListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.iv_back)
    ImageView mIvBack;
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
    @Bind(R.id.tv_address)
    TextView mTvAddress;
    @Bind(R.id.tv_people)
    TextView mTvPeople;
    @Bind(R.id.tv_money)
    TextView mTvMoney;


    private String mParam1;
    private String mParam2;
    private RequestQueue mQueue;
    private Toast mToast;
    private GameList mGameMessage;
    private String mChoseGameId;
    private AMapLocationClient mLocationClient;
    private double mLongitude;
    private double mLatitude;
    private NearNetBar mNearNetBar;
    private String mNetBarid;
    private LoginUser.ResultBean mUsrInfo;
    private int CHOOSE_GAME = 1;


    public YueZhanFragment() {
        // Required empty public constructor
    }

    public static YueZhanFragment newInstance(String param1, String param2) {
        YueZhanFragment fragment = new YueZhanFragment();
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

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_yue_zhan, container, false);
        ButterKnife.bind(this, view);
        mQueue = Volley.newRequestQueue(getActivity());
        mToast = YuwanApplication.getToast();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        location();

    }

    private void initData() {
        mUsrInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getActivity(), LoginUserInfoUtils.KEY);
        if (mUsrInfo != null) {
            gameID = mUsrInfo.getGameId();
        }
    }

    private void initView() {
        mIvBack.setVisibility(View.GONE);
        mTvTitle.setText("约战");
        mTvSkip.setText("发布");
    }


    private void location() {
        //声明mLocationOption对象

        mLocationClient = new AMapLocationClient(getActivity());
        //初始化定位参数
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        //设置定位监听

        locationOption.setOnceLocation(true);
        mLocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        locationOption.setInterval(2000);
        //设置定位参数
        mLocationClient.setLocationOption(locationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mLocationClient.startLocation();
    }


    @Override
    public void onPause() {
        super.onPause();
        getGameIdData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (mLocationClient != null)
            mLocationClient.stopLocation();
    }

//    @OnClick({R.id.save_personal_info})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.save_personal_info:
//                //保存按钮
//                break;
//            case R.id.game_time_yuezhan:
//                TimePickerView timePickerView = new TimePickerView(getActivity(), TimePickerView.Type.ALL);
//                timePickerView.setTime(new Date());
//                timePickerView.setCyclic(false);
//                timePickerView.setCancelable(true);
//                timePickerView.show();
//                timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
//                    @Override
//                    public void onTimeSelect(Date date) {
//                        String time = MyUtils.getTime(date);
//
//                    }
//                });
//                break;
//        }
//    }

    String[] gameID;

    /**
     * h获取游戏id的数据
     */
    private void getGameIdData() {


        if (gameID != null) {
            DBManager dbManager = new DBManager(getActivity());
            for (String id : gameID) {
                GameMessage.ResultBean game = dbManager.queryById(id);


            }

        }


        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < gameID.length; i++) {

            if (i == gameID.length - 1) {
                stringBuffer.append(gameID[i]);
            } else {
                stringBuffer.append(gameID[i]).append(",");
            }

        }
        String uri = Constant.HOST + "getGameInfoWithGameId&gameId=" + stringBuffer.toString();

        StringRequest stringRequest = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String substring = response.substring(11, 15);
                if ("true".equals(substring)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<GameList>() {
                    }.getType();
                    mGameMessage = gson.fromJson(response, type);
                    if (mGameMessage != null) {
                        List<GameList.ResultBean> result = mGameMessage.getResult();
                        String[] strings = new String[result.size()];
                        for (int i = 0; i < result.size(); i++) {
                            String name = result.get(i).getGameName();
                            strings[i] = name;
                        }
                        bindAdapt(strings);
                    }
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

            }
        });
        mQueue.add(stringRequest);
    }

    private void bindAdapt(String[] strings) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
//
//        if (gameNameYuezhan != null) {
//            gameNameYuezhan.setAdapter(adapter);
//            gameNameYuezhan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    GameList.ResultBean resultBean = mGameMessage.getResult().get(position);
//                    mChoseGameId = resultBean.getGameId();
//                    String wagerMin = resultBean.getWagerMin();
//                    String wagerMax = resultBean.getWagerMax();
//                    initPrice(wagerMin, wagerMax);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//        }
    }

    /**
     * 初始化价格下拉数据
     *
     * @param wagerMin 最小价格
     * @param wagerMax 最大价格
     */
    private void initPrice(String wagerMin, String wagerMax) {
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            mLongitude = aMapLocation.getLongitude();
            mLatitude = aMapLocation.getLatitude();
            PreferenceUtils.setPrefString(getActivity(), "longitude", mLongitude + "");
            PreferenceUtils.setPrefString(getActivity(), "latitude", mLatitude + "");
            if (NetUtil.is_Network_Available(getActivity())) {
                getNet();
            } else {
                mToast.setText("网络不可用，请检查网络连接");
                mToast.show();
            }
        }
    }

    private void getNet() {
        String uri = Constant.mainUri + "getNetBarWithGPS&lng=" + mLongitude + "&lat=" + mLatitude;
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    String substring = response.substring(11, 15);
                    if ("true".equals(substring)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<NearNetBar>() {
                        }.getType();
                        mNearNetBar = gson.fromJson(response, type);
                        setNearNetBar();
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
                } else {
                    mToast.setText("获取附近网吧失败");
                    mToast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);
    }

    private void setNearNetBar() {
        final List<NearNetBar.ResultBean> result = mNearNetBar.getResult();
        if (result != null) {
            String[] netBars = new String[result.size()];
            for (int i = 0; i < result.size(); i++) {
                String netBarName = result.get(i).getNetBarName();
                netBars[i] = netBarName;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, netBars);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            if (gameAddressYuezhan != null) {
//                gameAddressYuezhan.setAdapter(adapter);
//                gameAddressYuezhan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        mNetBarid = result.get(position).getNetBarid();
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//
//                    }
//                });
//            }
        }
    }

    @OnClick({R.id.rl_game_name, R.id.rl_time, R.id.rl_address, R.id.rl_people, R.id.rl_money, R.id.rl_friend, R.id.rl_rule})
    public void onClick(View view) {
        if (mUsrInfo == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            switch (view.getId()) {
                case R.id.rl_game_name:
                    if (mChoseGameId == null) {
                        MyUtils.showCustomDialog(getActivity(), "提示", "还没有添加常玩游戏无法约战，现在就去添加常玩游戏么?"
                                , "不了", "好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
                                    }
                                });
                    } else {
                        Intent intent = new Intent(getActivity(), FrequentlyGameActivity.class);
                        intent.putExtra("isYueZhan", true);
                        startActivityForResult(intent, CHOOSE_GAME);
                    }

                    break;
                case R.id.rl_time:
                    alertTimePicker();
                    break;
                case R.id.rl_address:

                    break;
                case R.id.rl_people:
                    break;
                case R.id.rl_money:
                    break;
                case R.id.rl_friend:
                    break;
                case R.id.rl_rule:
                    break;
            }
        }

    }

    private void alertTimePicker() {
        OptionsPickerView<String> optionsPickerView = new OptionsPickerView<>(getActivity());
        optionsPickerView.setTitle("选择开战时间");
        ArrayList<String> dayList = new ArrayList<>();
        dayList.add("今天");
        dayList.add("明天");
        dayList.add("后天");
        ArrayList<ArrayList<String>> hourList = new ArrayList<>();
        hourList.add(dayList);
        for(ArrayList<String> arrayList:hourList){
            for(String day:arrayList){

            }
        }

        ArrayList<String> minList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            minList.add(i + "");
        }
        optionsPickerView.setPicker(dayList, hourList, minList, true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_GAME) {
            if (data != null) {
                GameMessage.ResultBean choseGame = (GameMessage.ResultBean) data.getSerializableExtra("choseGame");
                String thumb = choseGame.getThumb();
                if (!TextUtils.isEmpty(thumb))
                    Picasso.with(getActivity()).load(thumb).resize(60, 60).centerCrop().into(mIvGameimage);
                String name = choseGame.getName();
                if (!TextUtils.isEmpty(name))
                    mTvGameName.setText(name);
            }
        }
    }
}
