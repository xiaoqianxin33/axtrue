package com.chinalooke.yuwan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.GameList;
import com.chinalooke.yuwan.model.NearNetBar;
import com.chinalooke.yuwan.model.UserInfo;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //游戏名字
    @Bind(R.id.game_name_yuezhan)
    Spinner gameNameYuezhan;
    //游戏时间
    @Bind(R.id.game_time_yuezhan)
    Spinner gameTimeYuezhan;
    //游戏地址
    @Bind(R.id.game_address_yuezhan)
    Spinner gameAddressYuezhan;
    //游戏价格
    @Bind(R.id.game_price_yuezhan)
    Spinner gamePriceYuezhan;
    //保存
    @Bind(R.id.save_personal_info)
    Button savePersonalInfo;

    // TODO: Rename and change types of parameters
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
        location();
        getGameIdDatas();
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
        getGameIdDatas();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (mLocationClient != null)
            mLocationClient.stopLocation();
    }

    @OnClick({R.id.save_personal_info})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_personal_info:
                //保存按钮
                break;
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
        }
    }

    String[] gameID;

    /**
     * h获取游戏id的数据
     */
    private void getGameIdDatas() {

        UserInfo userInfo = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        if (userInfo != null) {
            gameID = userInfo.getGameId();
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
    }

    private void bindAdapt(String[] strings) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        Log.d("TAG", gameNameYuezhan + "---");
        Log.d("TAG", adapter + "---");
        if (gameNameYuezhan != null) {
            gameNameYuezhan.setAdapter(adapter);
            gameNameYuezhan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    GameList.ResultBean resultBean = mGameMessage.getResult().get(position);
                    mChoseGameId = resultBean.getGameId();
                    String wagerMin = resultBean.getWagerMin();
                    String wagerMax = resultBean.getWagerMax();
                    initPrice(wagerMin, wagerMax);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    /**
     * 初始化价格下拉数据
     *
     * @param wagerMin 最小价格
     * @param wagerMax 最大价格
     */
    private void initPrice(String wagerMin, String wagerMax) {
        // TODO: 2016/9/20  初始化价格
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
            if (gameAddressYuezhan != null) {
                gameAddressYuezhan.setAdapter(adapter);
                gameAddressYuezhan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mNetBarid = result.get(position).getNetBarid();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }
    }
}
