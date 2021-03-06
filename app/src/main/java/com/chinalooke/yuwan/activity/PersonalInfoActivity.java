package com.chinalooke.yuwan.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVAnalytics;
import com.bigkoo.pickerview.OptionsPickerView;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.GameMessage;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.db.DBManager;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.hyphenate.chat.EMClient;
import com.lljjcoder.citypickerview.widget.CityPickerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalInfoActivity extends AutoLayoutActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.tv_name)
    TextView mEtName;
    @Bind(R.id.tv_sex)
    TextView mTvSex;
    @Bind(R.id.tv_play_age)
    TextView mEtPlayAge;
    @Bind(R.id.tv_location)
    TextView mTvLocation;
    @Bind(R.id.ll_game)
    LinearLayout mLlGame;
    @Bind(R.id.et_age)
    TextView mEtAge;

    private ArrayList<String> sexListDatas;
    private ArrayList<String> playAgeListDatas;

    private List<GameMessage.ResultBean> mChose = new ArrayList<>();

    //定义PickerView
    OptionsPickerView<String> pvOptions;
    private String updateUserInfo = Constant.HOST + "updateUserInfo";
    String sexPersonalInfo;
    String playAge;
    String address;
    String name;
    LoginUser.ResultBean userInfo;
    RequestQueue mQueue;
    private Toast mToast;
    private String[] mStrings;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        ButterKnife.bind(this);
        mQueue = Volley.newRequestQueue(this);
        mToast = YuwanApplication.getToast();
        mProgressDialog = MyUtils.initDialog("保存中", this);
        initView();
        initData();
    }

    private void initView() {
        mTvTitle.setText("完善个人信息");
        mTvSkip.setText("跳过");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        userInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mTvSkip.setVisibility(View.VISIBLE);
        String sex = userInfo.getSex();
        if (!TextUtils.isEmpty(sex))
            mTvSex.setText(sex);
        if (!TextUtils.isEmpty(userInfo.getRealName()))
            mEtName.setText(userInfo.getRealName());
        if (!TextUtils.isEmpty(userInfo.getPlayAge()))
            mEtPlayAge.setText(userInfo.getPlayAge());
        if (!TextUtils.isEmpty(userInfo.getAddress()))
            mTvLocation.setText(userInfo.getAddress());
        String[] gameId = userInfo.getGameId();
        if (gameId != null) {
            DBManager dbManager = new DBManager(getApplicationContext());
            for (String aGameId : gameId) {
                GameMessage.ResultBean resultBean = dbManager.queryById(aGameId);
                if (resultBean != null) {
                    RoundedImageView imageView = new RoundedImageView(getApplicationContext());
                    imageView.setOval(true);
                    imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(50, 50));
                    imageView.setImageURI(Uri.parse(resultBean.getThumb()));
                    mLlGame.addView(imageView);
                }
            }
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_skip, R.id.rl_sex, R.id.rl_play_age,
            R.id.rl_name, R.id.rl_location, R.id.rl_game, R.id.btn_enter, R.id.rl_age})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_skip:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            //点击性别
            case R.id.rl_sex:
                setSex();
                MyUtils.hiddenKeyboard(this, view);
                break;
            //点击玩龄
            case R.id.rl_play_age:
                setPlayAge();
                MyUtils.hiddenKeyboard(this, view);
                pvOptions.show();
                break;
            //点击位置
            case R.id.rl_location:
                MyUtils.hiddenKeyboard(this, view);
                selectLocation();
                break;
            //添加常玩游戏
            case R.id.rl_game:
                Intent intent2 = new Intent(PersonalInfoActivity.this, FrequentlyGameActivity.class);
                startActivityForResult(intent2, 0);
                break;
            //保存
            case R.id.btn_enter:
                if (savePersonalInfo()) {
                    mProgressDialog.show();
                    submitSaveInfo();
                }
                break;
            case R.id.rl_name:
                showEditDialog();
                break;
            case R.id.rl_age:
                setAge();
                MyUtils.hiddenKeyboard(this, view);
                break;
        }
    }

    private void setAge() {
        playAgeListDatas = new ArrayList<>();
        for (int i = 10; i <= 60; i++) {
            playAgeListDatas.add("" + i);
        }
        //选项选择器
        pvOptions = new OptionsPickerView<>(this);
        //三级联动效果
        pvOptions.setPicker(playAgeListDatas);
        //设置选择的三级单位
        pvOptions.setTitle("选择年龄");
        pvOptions.setCyclic(false);
        //设置默认选中的三级项目
        //监听确定选择按钮
        pvOptions.setSelectOptions(20);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = playAgeListDatas.get(options1);
                mEtAge.setText(tx);

            }
        });
        pvOptions.show();
    }

    private void showEditDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View inflate = View.inflate(this, R.layout.dialog_edit_name, null);
        final EditText editText = (EditText) inflate.findViewById(R.id.et_input);
        Button noButton = (Button) inflate.findViewById(R.id.btn_cancel);
        Button yesButton = (Button) inflate.findViewById(R.id.btn_ok);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Editable text = editText.getText();
                if (!TextUtils.isEmpty(text)) {
                    mEtName.setText(text.toString());
                    userInfo.setNickName(text.toString());
                }
            }
        });
        AutoUtils.autoSize(inflate);
        dialog.setContentView(inflate);
        dialog.show();
    }

    /**
     * 保存个人信息
     */
    private boolean savePersonalInfo() {
        updateUserInfo = updateUserInfo + "&userId=" + userInfo.getUserId() + "&headImg=" + userInfo.getHeadImg();

        sexPersonalInfo = mTvSex.getText().toString();
        playAge = mEtPlayAge.getText().toString();
        address = mTvLocation.getText().toString();
        name = mEtName.getText().toString();
        String age = mEtAge.getText().toString();
        if (mChose.size() == 0) {
            mToast.setText("请选择常玩游戏");
            mToast.show();
            return false;
        }
        if (!TextUtils.isEmpty(name) && !"请输入昵称".equals(name)) {
            userInfo.setNickName(name);
            try {
                updateUserInfo = updateUserInfo + "&nickName=" + URLEncoder.encode(name, "utf8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(age) && !"请输入真实年龄".equals(age)) {
            userInfo.setAge(age);
            updateUserInfo = updateUserInfo + "&age=" + age;
        }
        if (!TextUtils.isEmpty(playAge) && !"请输入真实玩龄".equals(playAge)) {
            userInfo.setPlayAge(playAge);
            updateUserInfo = updateUserInfo + "&playAge=" + playAge;
        }

        if (!TextUtils.isEmpty(sexPersonalInfo)) {
            userInfo.setSex(sexPersonalInfo);
            if (sexPersonalInfo.equals("男"))
                updateUserInfo = updateUserInfo + "&sex=1";
            else
                updateUserInfo = updateUserInfo + "&sex=0";
        }

        if (!TextUtils.isEmpty(address) && !"点击获取位置".equals(address)) {
            userInfo.setAddress(address);
            updateUserInfo = updateUserInfo + "&address=" + userInfo.getAddress();
        }

        if (mStrings != null) {
            userInfo.setGameId(mStrings);
        }
        try {
            LoginUserInfoUtils.saveLoginUserInfo(getApplicationContext(),
                    LoginUserInfoUtils.KEY, userInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 提交保存信息
     */
    private void submitSaveInfo() {
        //拼接接口信息
        String[] gameId = userInfo.getGameId();
        StringBuilder stringBuffer = new StringBuilder();
        if (gameId != null) {
            for (int i = 0; i < gameId.length; i++) {
                if (i == gameId.length - 1) {
                    stringBuffer.append(gameId[i]);
                } else {
                    stringBuffer.append(gameId[i]).append(",");
                }
            }
        }
        updateUserInfo = updateUserInfo + "&gameId=" + stringBuffer.toString();

        StringRequest stringRequest = new StringRequest(updateUserInfo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //解析数据
                        mProgressDialog.dismiss();
                        if (response != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("Success");
                                if (success) {
                                    boolean result = jsonObject.getBoolean("Result");
                                    if (result) {
                                        setSaveDialog("保存成功");
                                        if (!TextUtils.isEmpty(name))
                                            EMClient.getInstance().updateCurrentUserNick(name);
                                    } else {
                                        MyUtils.showMsg(mToast, response);
                                    }
                                } else {
                                    MyUtils.showMsg(mToast, response);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mToast.setText("服务器抽风了，请重试");
                mToast.show();
            }
        });
        mQueue.add(stringRequest);
    }

    /**
     * 设置性别
     */
    private void setSex() {

        //数据
        sexListDatas = new ArrayList<>();
        sexListDatas.add("男");
        sexListDatas.add("女");
        //选项选择器
        pvOptions = new OptionsPickerView<>(this);
        //三级联动效果
        pvOptions.setPicker(sexListDatas);
        //设置选择的三级单位
//        pwOptions.setLabels("省", "市", "区");
        pvOptions.setTitle("选择性别");
        pvOptions.setCyclic(false);
        //设置默认选中的三级项目
        //监听确定选择按钮
        pvOptions.setSelectOptions(1);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = sexListDatas.get(options1);
                mTvSex.setText(tx);

            }
        });
        pvOptions.show();
    }

    /**
     * 设置年龄
     */
    private void setPlayAge() {

        //数据
        playAgeListDatas = new ArrayList<>();
        for (int i = 0; i <= 60; i++) {
            playAgeListDatas.add("" + i);
        }
        //选项选择器
        pvOptions = new OptionsPickerView<>(this);
        //三级联动效果
        pvOptions.setPicker(playAgeListDatas);
        //设置选择的三级单位
        pvOptions.setTitle("选择玩龄");
        pvOptions.setCyclic(false);
        //设置默认选中的三级项目
        //监听确定选择按钮
        pvOptions.setSelectOptions(1);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = playAgeListDatas.get(options1);
                mEtPlayAge.setText(tx);

            }
        });
    }

    /* popuwindow内部的点击事件*/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // msexPersonalInfo.setText(sexListDatas.get(position));
    }

    private void selectLocation() {
        CityPickerView cityPickerView = new CityPickerView(this);
        cityPickerView.setTextColor(Color.BLACK);
        cityPickerView.setTextSize(20);
        cityPickerView.setVisibleItems(5);
        cityPickerView.setIsCyclic(false);
        cityPickerView.show();

        cityPickerView.setOnCityItemClickListener(new CityPickerView.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                mTvLocation.setText(citySelected[0] + citySelected[1] + citySelected[2]);
            }
        });

    }

    private void setSaveDialog(String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//设置对话框图标，可以使用自己的图片，Android本身也提供了一些图标供我们使用
        builder.setIcon(android.R.drawable.ic_dialog_info);
//设置对话框标题
        builder.setTitle("提示");
//设置对话框内的文本
        builder.setMessage(result);
//设置确定按钮，并给按钮设置一个点击侦听，注意这个OnClickListener使用的是DialogInterface类里的一个内部接口
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 执行点击确定按钮的业务逻辑
                startActivity(new Intent(PersonalInfoActivity.this, MainActivity.class));
                finish();
            }
        });
//使用builder创建出对话框对象
        AlertDialog dialog = builder.create();
//显示对话框
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            mChose = (List<GameMessage.ResultBean>) data.getSerializableExtra("list");
            for (GameMessage.ResultBean resultBean : mChose) {
                String thumb = resultBean.getThumb();
                RoundedImageView imageView = new RoundedImageView(getApplicationContext());
                imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(70, 70));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), thumb, 70, 70);
                Picasso.with(getApplicationContext()).load(loadImageUrl).into(imageView);
                imageView.setOval(true);
                mLlGame.addView(imageView);
            }
            mStrings = new String[mChose.size()];
            for (int i = 0; i < mChose.size(); i++) {
                mStrings[i] = mChose.get(i).getGameId();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }
}
