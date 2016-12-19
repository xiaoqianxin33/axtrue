package com.chinalooke.yuwan.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
import com.bigkoo.pickerview.OptionsPickerView;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.GameMessage;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.db.DBManager;
import com.chinalooke.yuwan.utils.Auth;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.lljjcoder.citypickerview.widget.CityPickerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.picasso.Picasso;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;


public class UserInfoActivity extends AutoLayoutActivity implements EasyPermissions.PermissionCallbacks {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_sex)
    TextView mTvSex;
    @Bind(R.id.tv_age)
    TextView mTvAge;
    @Bind(R.id.tv_play_age)
    TextView mTvPlayAge;
    @Bind(R.id.tv_address)
    TextView mTvAddress;
    @Bind(R.id.tv_slogen)
    TextView mTvSlogen;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.ll_game)
    LinearLayout mLlGame;
    @Bind(R.id.ll1)
    LinearLayout mLl1;
    @Bind(R.id.ll2)
    LinearLayout mLl2;
    @Bind(R.id.tv_name_netbar)
    TextView mTvNameNetbar;
    @Bind(R.id.tv_age_netbar)
    TextView mTvAgeNetbar;
    @Bind(R.id.tv_address_netbar)
    TextView mTvAddressNetbar;
    @Bind(R.id.ll3)
    LinearLayout mLl3;
    private LoginUser.ResultBean mUserInfo;
    private ImgSelConfig mConfig;
    private int RC_ACCESS_FINE_LOCATION = 0;
    private int REQUEST_CODE = 2;
    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Picasso.with(context).load("file://" + path).into(imageView);
        }
    };
    private UploadManager mUploadManager;
    private String mPath;
    private RequestQueue mQueue;
    private ArrayList<String> sexListDatas = new ArrayList<>();
    private OptionsPickerView<String> pvOptions;
    private boolean isChangeHead = false;
    private ArrayList<String> playAgeListDatas = new ArrayList<>();
    private String mSlogan;
    private String mName;
    private String mSex;
    private String mAge;
    private String mPlayAge;
    private String mAddress;
    private ProgressDialog mProgressDialog;
    private Toast mToast;
    private int REQUEST_GAME = 3;
    private List<GameMessage.ResultBean> mChose;
    private String[] mStrings;
    private boolean isNetbar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        mToast = YuwanApplication.getToast();
        mUploadManager = YuwanApplication.getmUploadManager();
        mQueue = YuwanApplication.getQueue();
        mConfig = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(false)
                // “确定”按钮背景色
                .btnBgColor(Color.GRAY)
                // “确定”按钮文字颜色
                .btnTextColor(Color.BLUE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 返回图标ResId
                .backResId(R.drawable.ic_back)
                // 标题
                .title("图片")
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(Color.parseColor("#3F51B5"))
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(1, 1, 200, 200)
                .needCrop(false)
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(9)
                .build();
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
    }

    private void initView() {
        mTvTitle.setText("个人资料");
        mTvSkip.setText("保存");

        String headImg = mUserInfo.getHeadImg();
        if (!TextUtils.isEmpty(headImg)) {
            mPath = headImg;
            Picasso.with(getApplicationContext()).load(headImg).resize(120, 120).centerCrop().into(mRoundedImageView);
        }

        String userType = mUserInfo.getUserType();
        if (userType.equals("netbar"))
            isNetbar = true;
        mLl1.setVisibility(userType.equals("netbar") ? View.GONE : View.VISIBLE);
        mLl2.setVisibility(userType.equals("netbar") ? View.GONE : View.VISIBLE);
        mLl3.setVisibility(userType.equals("netbar") ? View.VISIBLE : View.GONE);

        mName = mUserInfo.getNickName();
        if (!TextUtils.isEmpty(mName)) {
            mTvName.setText(mName);
            mTvNameNetbar.setText(mName);
        }
        mAddress = mUserInfo.getAddress();
        if (!TextUtils.isEmpty(mAddress)) {
            mTvAddress.setText(mAddress);
            mTvAddressNetbar.setText(mAddress);
        }
        mPlayAge = mUserInfo.getPlayAge();
        if (!TextUtils.isEmpty(mPlayAge))
            mTvPlayAge.setText(mPlayAge);
        mAge = mUserInfo.getAge();
        if (!TextUtils.isEmpty(mAge)) {
            mTvAge.setText(mAge);
            mTvAgeNetbar.setText(mAge);
        }
        mSex = mUserInfo.getSex();
        if (!TextUtils.isEmpty(mSex))
            mTvSex.setText(mSex);
        mSlogan = mUserInfo.getSlogan();
        if (!TextUtils.isEmpty(mSlogan))
            mTvSlogen.setText(mSlogan);

        String[] gameId = mUserInfo.getGameId();
        mStrings = gameId;
        if (gameId != null) {
            for (String s : gameId) {
                DBManager dbManager = new DBManager(getApplicationContext());
                GameMessage.ResultBean resultBean = dbManager.queryById(s);
                String thumb = resultBean.getThumb();
                RoundedImageView imageView = new RoundedImageView(getApplicationContext());
                imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(70, 70));
                Picasso.with(getApplicationContext()).load(thumb).resize(70, 70).centerCrop().into(imageView);
                imageView.setOval(true);
                mLlGame.addView(imageView);
            }
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
                String s = playAgeListDatas.get(options1);
                if (!TextUtils.isEmpty(s)) {
                    mAge = s;
                    mTvAge.setText(mAge);
                    mTvAgeNetbar.setText(mAge);
                }

            }
        });
        pvOptions.show();
    }

    @OnClick({R.id.rl_netbar_address, R.id.rl_age_netbar, R.id.rl_name_netbar, R.id.tv_skip, R.id.iv_back, R.id.rl_head_t, R.id.rl_name, R.id.rl_sex, R.id.rl_age, R.id.rl_play_age, R.id.rl_address, R.id.rl_id, R.id.rl_qcode, R.id.rl_slogen})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_netbar_address:
                selectLocation();
                break;
            case R.id.rl_age_netbar:
                setAge();
                break;
            case R.id.rl_name_netbar:
                showEditDialog();
                break;
            case R.id.tv_skip:
                updateInfo();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_head_t:
                req();
                break;
            case R.id.rl_name:
                showEditDialog();
                break;
            case R.id.rl_sex:
                setSex();
                break;
            case R.id.rl_age:
                setAge();
                break;
            case R.id.rl_play_age:
                setPlayAge();
                break;
            case R.id.rl_address:
                selectLocation();
                break;
            case R.id.rl_id:
                Intent intent2 = new Intent(this, FrequentlyGameActivity.class);
                startActivityForResult(intent2, REQUEST_GAME);
                break;
            case R.id.rl_qcode:
                startActivity(new Intent(this, MyQRCodeActivity.class));
                break;
            case R.id.rl_slogen:
                showRuleDialog();
                break;
        }
    }

    private void showRuleDialog() {
        final Dialog dialog = new Dialog(UserInfoActivity.this, R.style.Dialog);
        View inflate = LayoutInflater.from(UserInfoActivity.this).inflate(R.layout.dialog_add_game_rule, null);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_ok);
        TextView tvCancel = (TextView) inflate.findViewById(R.id.tv_cancel);
        TextView tvTitle = (TextView) inflate.findViewById(R.id.tv_title);
        tvTitle.setText("填写个人说明");
        final EditText etRule = (EditText) inflate.findViewById(R.id.et_rule);
        if (!TextUtils.isEmpty(mSlogan))
            etRule.setText(mSlogan);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Editable text = etRule.getText();
                if (!TextUtils.isEmpty(text)) {
                    mSlogan = etRule.getText().toString();
                }
            }
        });
        dialog.setContentView(inflate);
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        dialog.getWindow().setAttributes(lp);
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
                String s = citySelected[0] + citySelected[1] + citySelected[2];
                if (!TextUtils.isEmpty(s)) {
                    mAddress = s;
                    mTvAddress.setText(s);
                    mTvAddressNetbar.setText(s);
                }
            }
        });

    }

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
                if (!TextUtils.isEmpty(tx))
                    mPlayAge = tx;
                mTvPlayAge.setText(tx);

            }
        });
        pvOptions.show();
    }

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
        pvOptions.setTitle("选择性别");
        pvOptions.setCyclic(false);
        //设置默认选中的三级项目
        //监听确定选择按钮
        pvOptions.setSelectOptions(1);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String s = sexListDatas.get(options1);
                if (!TextUtils.isEmpty(s)) {
                    mSex = s;
                    mTvSex.setText(mSex);
                }
            }
        });

        pvOptions.show();

    }

    private void showEditDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_edit_name, null);
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
                    mTvName.setText(text.toString());
                    mTvNameNetbar.setText(text.toString());
                    mName = text.toString();
                }
            }
        });
        AutoUtils.autoSize(inflate);
        dialog.setContentView(inflate);
        dialog.show();
    }

    private void req() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            ImgSelActivity.startActivity(this, mConfig, REQUEST_CODE);
        } else {
            EasyPermissions.requestPermissions(this, "需要摄像头权限",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == RC_ACCESS_FINE_LOCATION)
            ImgSelActivity.startActivity(this, mConfig, REQUEST_CODE);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            mPath = pathList.get(0);
            isChangeHead = true;
            Picasso.with(getApplicationContext()).load("file://" + mPath).into(mRoundedImageView);
        } else if (requestCode == REQUEST_GAME) {
            mChose = (List<GameMessage.ResultBean>) data.getSerializableExtra("list");
            if (mChose != null && mChose.size() != 0) {
                mLlGame.removeAllViews();
                for (GameMessage.ResultBean resultBean : mChose) {
                    String thumb = resultBean.getThumb();
                    RoundedImageView imageView = new RoundedImageView(getApplicationContext());
                    imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(70, 70));
                    Picasso.with(getApplicationContext()).load(thumb).resize(70, 70).centerCrop().into(imageView);
                    imageView.setOval(true);
                    mLlGame.addView(imageView);
                }
                mStrings = new String[mChose.size()];
                for (int i = 0; i < mChose.size(); i++) {
                    mStrings[i] = mChose.get(i).getGameId();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //更新资料
    private void updateInfo() {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            mProgressDialog = MyUtils.initDialog("保存中", this);
            mProgressDialog.show();
            if (isChangeHead) {
                Auth auth = Auth.create(Constant.QINIU_ACCESSKEY, Constant.QINIU_SECRETKEY);
                String token = auth.uploadToken("yuwan");
                final String fileName = "head" + new Date().getTime();
                mUploadManager.put(mPath, fileName, token, new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        if (info.error == null) {
                            mPath = Constant.QINIU_DOMAIN + "/" + fileName;
                            update();
                        } else {
                            mProgressDialog.dismiss();
                        }
                    }
                }, null);
            } else {
                update();
            }
        }
    }

    private void update() {
        String url = Constant.HOST + "updateUserInfo&userId=" + mUserInfo.getUserId() + "&headImg=" + mPath;
        if (mAddress != null)
            url = url + "&address=" + mAddress;
        else
            url = url + "&address=";
        if (mSlogan != null)
            try {
                url = url + "&slogan=" + URLEncoder.encode(mSlogan, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        else
            url = url + "&slogan=";
        if (mName != null)
            try {
                url = url + "&nickName=" + URLEncoder.encode(mName, "utf8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        else
            url = url + "&nickName=";
        if (mSex != null)
            try {
                url = url + "&sex=" + URLEncoder.encode(mSex, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        else
            url = url + "&sex=";
        if (mAge != null)
            url = url + "&age=" + mAge;
        else
            url = url + "&age=";
        if (mPlayAge != null)
            url = url + "&playAge=" + mPlayAge;
        else
            url = url + "&playAge=";
        if (mStrings != null) {
            String s = Arrays.toString(mStrings);
            String substring = s.substring(1, s.length() - 1);
            String replace = substring.replace(" ", "");
            url = url + "&gameId=" + replace;
        }
        Log.e("TAG", url);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            boolean result = jsonObject.getBoolean("Result");
                            if (result) {
                                mToast.setText("修改成功！");
                                mToast.show();
                                mUserInfo.setHeadImg(mPath);
                                if (mName != null)
                                    mUserInfo.setNickName(mName);
                                if (mAge != null)
                                    mUserInfo.setAge(mAge);
                                if (mAddress != null)
                                    mUserInfo.setAddress(mAddress);
                                if (!TextUtils.isEmpty(mPlayAge))
                                    mUserInfo.setPlayAge(mPlayAge);
                                if (!TextUtils.isEmpty(mSlogan))
                                    mUserInfo.setSlogan(mSlogan);
                                if (!TextUtils.isEmpty(mSex))
                                    mUserInfo.setSex(mSex);
                                LoginUserInfoUtils.saveLoginUserInfo(getApplicationContext(), LoginUserInfoUtils.KEY, mUserInfo);
                            }
                        } else {
                            String msg = jsonObject.getString("Msg");
                            mToast.setText(msg);
                            mToast.show();
                        }
                    } catch (JSONException | IOException e) {
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
    }
}

