package com.chinalooke.yuwan.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.GameMessage;
import com.chinalooke.yuwan.model.LoginUser;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.chinalooke.yuwan.view.EditNameDialog;
import com.lljjcoder.citypickerview.widget.CityPickerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

import static com.chinalooke.yuwan.constant.Constant.MIN_CLICK_DELAY_TIME;
import static com.chinalooke.yuwan.constant.Constant.lastClickTime;

public class CreateCircleActivity extends AutoLayoutActivity implements EasyPermissions.PermissionCallbacks {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.tv_circle_name)
    TextView mTvCircleName;
    @Bind(R.id.rl_game_name)
    RelativeLayout mRlGameName;
    @Bind(R.id.iv1)
    ImageView mIv1;
    @Bind(R.id.iv_gameimage)
    RoundedImageView mIvGameimage;
    @Bind(R.id.rl_head)
    RelativeLayout mRlHead;
    @Bind(R.id.iv2)
    ImageView mIv2;
    @Bind(R.id.ll_game)
    LinearLayout mLlGame;
    @Bind(R.id.rl_game)
    RelativeLayout mRlGame;
    @Bind(R.id.iv3)
    ImageView mIv3;
    @Bind(R.id.tv_circle_address)
    TextView mTvCircleAddress;
    @Bind(R.id.rl_address)
    RelativeLayout mRlAddress;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.rl_time)
    RelativeLayout mRlTime;
    @Bind(R.id.iv4)
    ImageView mIv4;
    @Bind(R.id.tv_circle_expalin)
    TextView mTvCircleExpalin;
    @Bind(R.id.rl_explain)
    RelativeLayout mRlExplain;
    @Bind(R.id.btn_create)
    Button mBtnCreate;
    @Bind(R.id.activity_create_circle)
    LinearLayout mActivityCreateCircle;
    private int RC_ACCESS_FINE_LOCATION = 0;
    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Picasso.with(context).load("file://" + path).into(imageView);
        }
    };
    private ImgSelConfig mConfig;
    private int REQUEST_CODE = 2;
    private int CHOSE_GAME = 3;
    private List<GameMessage.ResultBean> mChose = new ArrayList<>();
    private String[] mStrings;
    private String mRule;
    private LoginUser.ResultBean mUserInfo;
    private String mCircleName;
    private Toast mToast;
    private String mPath;
    private String mCircleAddress;
    private ProgressDialog mProgressDialog;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_circle);
        ButterKnife.bind(this);
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mToast = YuwanApplication.getToast();
        mQueue = YuwanApplication.getQueue();
        initView();
        initEvent();
    }

    private void initEvent() {
        initPhotoPicker();
    }

    //初始化图片选择器
    private void initPhotoPicker() {
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
    }


    private void initView() {
        mTvTitle.setText("创建圈子");
        Date date = new Date();
        String formatShortTime = DateUtils.getFormatShortTime(date);
        mTvTime.setText(formatShortTime);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnClick({R.id.iv_back, R.id.rl_game_name, R.id.rl_head, R.id.rl_game, R.id.rl_address, R.id.rl_explain, R.id.btn_create})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.rl_game_name:
                    showEditDialog();
                    break;
                case R.id.rl_head:
                    req();
                    break;
                case R.id.rl_game:
                    Intent intent2 = new Intent(this, FrequentlyGameActivity.class);
                    startActivityForResult(intent2, CHOSE_GAME);
                    break;
                case R.id.rl_address:
                    selectLocation();
                    break;
                case R.id.rl_explain:
                    showRuleDialog();
                    break;
                case R.id.btn_create:
                    if (checkInput()) {
                        if (NetUtil.is_Network_Available(getApplicationContext())) {
                            mProgressDialog = MyUtils.initDialog("创建中", this);
                            mProgressDialog.show();
                            checkIsExistGroup();
                        } else {
                            mToast.setText("网络不可用，请检查网络连接");
                            mToast.show();
                        }

                    }
                    break;
            }
        }
    }

    //判断圈子是否存在，创建时防止圈子名称重复
    private void checkIsExistGroup() {
        String uri = Constant.HOST + "isExistGroup&groupName=" + mCircleName;
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("Success");
                    if (success) {
                        boolean result = jsonObject.getBoolean("Result");
                        if (result) {
                            mProgressDialog.dismiss();
                            mToast.setText("该圈子名称已存在，请重新填写");
                            mToast.show();
                        } else {
                            createCircle();
                        }
                    } else {
                        mProgressDialog.dismiss();
                        mToast.setText("服务器抽风了，请稍后重试");
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
                mToast.setText("服务器抽风了，请稍后重试");
                mToast.show();
            }
        });

        mQueue.add(request);
    }

    //上传创建圈子
    private void createCircle() {
// TODO: 2016/11/23 头像云存储
        if (TextUtils.isEmpty(mRule))
            mRule = "";
        String longitude = PreferenceUtils.getPrefString(getApplicationContext(), "longitude", "");
        String latitude = PreferenceUtils.getPrefString(getApplicationContext(), "latitude", "");
        String uri = Constant.HOST + "addGroup&userId=" + mUserInfo.getUserId() + "&lng=" + longitude + "&lat="
                + latitude + "&address=" + mCircleAddress + "&gameIds=" + Arrays.toString(mStrings) + "&groupName=" + mCircleName
                + "&createTime=" + mTvTime.getText().toString() + "&slogan=" + mRule;
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);

    }

    //检查建圈子信息
    private boolean checkInput() {
        mCircleName = mTvCircleName.getText().toString();
        if (TextUtils.isEmpty(mCircleName)) {
            mToast.setText("请填写圈子昵称");
            mToast.show();
            return false;
        }

        if (TextUtils.isEmpty(mPath)) {
            mToast.setText("请选择圈子头像");
            mToast.show();
            return false;
        }

        if (mChose.size() == 0) {
            mToast.setText("请添加圈子游戏");
            mToast.show();
            return false;
        }

        mCircleAddress = mTvCircleAddress.getText().toString();
        if (TextUtils.isEmpty(mCircleAddress)) {
            mToast.setText("请选择圈子位置");
            mToast.show();
            return false;
        }
        return true;
    }

    private void showEditDialog() {
        final EditNameDialog editNameDialog = new EditNameDialog(this);
        editNameDialog.setTvTitle("编辑圈子昵称");
        editNameDialog.setNoOnclickListener(new EditNameDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                editNameDialog.dismiss();
            }
        });
        editNameDialog.setYesOnclickListener(new EditNameDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String input) {
                editNameDialog.dismiss();
                if (!TextUtils.isEmpty(input)) {
                    mTvCircleName.setText(input);
                }
            }
        });

        editNameDialog.show();
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
                mTvCircleAddress.setText(citySelected[0] + citySelected[1] + citySelected[2]);
            }
        });

    }

    private void showRuleDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_add_game_rule, null);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_ok);
        TextView tvCancel = (TextView) inflate.findViewById(R.id.tv_cancel);
        TextView tvTitle = (TextView) inflate.findViewById(R.id.tv_title);
        tvTitle.setText("填写圈子说明");
        final EditText etRule = (EditText) inflate.findViewById(R.id.et_rule);
        if (!TextUtils.isEmpty(mRule))
            etRule.setText(mRule);
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
                mRule = etRule.getText().toString();
            }
        });

        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        dialog.getWindow().setAttributes(lp);
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void req() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            ImgSelActivity.startActivity(this, mConfig, REQUEST_CODE);
        } else {
            EasyPermissions.requestPermissions(this, "需要定位权限",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 图片选择结果回调
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            mPath = pathList.get(0);
            Picasso.with(getApplicationContext()).load("file://" + mPath).into(mIvGameimage);
        } else if (requestCode == CHOSE_GAME) {
            mChose = (List<GameMessage.ResultBean>) data.getSerializableExtra("list");
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
}
