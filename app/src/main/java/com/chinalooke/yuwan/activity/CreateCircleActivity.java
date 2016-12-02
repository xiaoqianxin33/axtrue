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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.db.DBManager;
import com.chinalooke.yuwan.bean.Circle;
import com.chinalooke.yuwan.bean.GameMessage;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.utils.Auth;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.chinalooke.yuwan.view.EditNameDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.tv_circle_name)
    TextView mTvCircleName;
    @Bind(R.id.iv_gameimage)
    RoundedImageView mIvGameimage;
    @Bind(R.id.ll_game)
    LinearLayout mLlGame;
    @Bind(R.id.tv_circle_address)
    TextView mTvCircleAddress;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_circle_expalin)
    TextView mTvCircleExpalin;
    @Bind(R.id.btn_create)
    Button mBtnCreate;
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
    private String mStrings;
    private String mRule;
    private LoginUser.ResultBean mUserInfo;
    private String mCircleName;
    private Toast mToast;
    private String mPath;
    private String mCircleAddress;
    private ProgressDialog mProgressDialog;
    private RequestQueue mQueue;
    private UploadManager mUploadManager;
    private Circle.ResultBean mCircle;
    private boolean isImageChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_circle);
        ButterKnife.bind(this);
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mToast = YuwanApplication.getToast();
        mQueue = YuwanApplication.getQueue();
        mUploadManager = YuwanApplication.getmUploadManager();
        mCircle = (Circle.ResultBean) getIntent().getSerializableExtra("circle");
        if (mCircle != null) {
            initDoneView();
        } else {
            initView();
        }
        initEvent();
    }

    //是否是编辑圈子
    private void initDoneView() {
        mTvSkip.setVisibility(View.GONE);
        mBtnCreate.setText("确定修改");
        mTvTitle.setText("编辑圈子");
        String headImg = mCircle.getHeadImg();
        if (!TextUtils.isEmpty(headImg))
            setHeadImg(headImg);

        String groupName = mCircle.getGroupName();
        if (!TextUtils.isEmpty(groupName))
            mTvCircleName.setText(groupName);
        String address = mCircle.getAddress();
        if (!TextUtils.isEmpty(address))
            mTvCircleAddress.setText(address);
        String createTime = mCircle.getCreateTime();
        if (!TextUtils.isEmpty(createTime))
            mTvTime.setText(createTime);
        String details = mCircle.getDetails();
        if (!TextUtils.isEmpty(details)) {
            mRule = details;
            mTvCircleExpalin.setText(details);
        }
        setGame();
    }


    //设置游戏
    private void setGame() {
        String games = mCircle.getGames();
        if (!TextUtils.isEmpty(games)) {
            String[] game = games.split(",");
            DBManager dbManager = new DBManager(getApplicationContext());
            for (String s : game) {
                GameMessage.ResultBean gameInfo = dbManager.queryById(s);
                mChose.add(gameInfo);
                String thumb = gameInfo.getThumb();
                RoundedImageView imageView = new RoundedImageView(getApplicationContext());
                imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(60, 60));
                imageView.setPaddingRelative(5, 0, 5, 0);
                Picasso.with(getApplicationContext()).load(thumb).resize(60, 60).centerCrop().into(imageView);
                imageView.setOval(true);
                mLlGame.addView(imageView);
            }
        }
    }

    //设置头像
    private void setHeadImg(String headImg) {
        mPath = headImg;
        String uri = headImg + "?imageView2/1/w/120/h/120";
        Picasso.with(getApplicationContext()).load(uri).into(mIvGameimage);
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
                            if (mCircle == null)
                                checkIsExistGroup();
                            else
                                updateGroup();
                        } else {
                            mToast.setText("网络不可用，请检查网络连接");
                            mToast.show();
                        }

                    }
                    break;
            }
        }
    }

    //更新圈子
    private void updateGroup() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mChose.size(); i++) {
            String gameId = mChose.get(i).getGameId();
            if (i != mChose.size() - 1) {
                stringBuilder.append(gameId).append(",");
            } else {
                stringBuilder.append(gameId);
            }
        }
        mStrings = stringBuilder.toString();
        if (isImageChange) {
            Auth auth = Auth.create(Constant.QINIU_ACCESSKEY, Constant.QINIU_SECRETKEY);
            String token = auth.uploadToken("yuwan");
            final String fileName = "circle_name" + new Date().getTime();
            if (TextUtils.isEmpty(mRule))
                mRule = "";
            mUploadManager.put(mPath, fileName, token, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if (info.error == null) {
                        update();
                    } else {
                        mProgressDialog.dismiss();
                        mToast.setText("图片上传失败，请稍后重试");
                        mToast.show();
                    }
                }
            }, null);
        } else {
            update();
        }


    }

    private void update() {
        mPath = Constant.QINIU_DOMAIN + mPath;
        String longitude = PreferenceUtils.getPrefString(getApplicationContext(), "longitude", "");
        String latitude = PreferenceUtils.getPrefString(getApplicationContext(), "latitude", "");
        String uri = Constant.HOST + "updateGroup&groupId=" + mCircle.getGroupId() + "&userId=" + mUserInfo.getUserId() + "&lng=" + longitude + "&lat="
                + latitude + "&head=" + mPath + "&gameIds" + mStrings + "&groupName=" + mTvCircleName.getText().toString() + "&slogan" + mRule + "&address=" + mTvCircleAddress.getText().toString();
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
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
                                showSucceedDialog("圈子信息更新成功！");
                            }
                        } else {
                            String msg = jsonObject.getString("Msg");
                            mToast.setText(msg);
                            mToast.show();
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
                mToast.setText("服务器抽风了，请稍后重试");
                mToast.show();
            }
        });

        mQueue.add(request);
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
        Auth auth = Auth.create(Constant.QINIU_ACCESSKEY, Constant.QINIU_SECRETKEY);
        String token = auth.uploadToken("yuwan");
        final String fileName = "circle_name" + new Date().getTime();
        mUploadManager.put(mPath, fileName, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.error == null) {
                    if (TextUtils.isEmpty(mRule))
                        mRule = "";
                    String longitude = PreferenceUtils.getPrefString(getApplicationContext(), "longitude", "");
                    String latitude = PreferenceUtils.getPrefString(getApplicationContext(), "latitude", "");
                    String uri = Constant.HOST + "addGroup&userId=" + mUserInfo.getUserId() + "&lng=" + longitude + "&lat="
                            + latitude + "&address=" + mCircleAddress + "&gameIds=" + mStrings + "&groupName=" + mCircleName
                            + "&createTime=" + mTvTime.getText().toString() + "&slogan=" + mRule + "&head=" + Constant.QINIU_DOMAIN + "/" + fileName;
                    StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
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
                                            showSucceedDialog("圈子创建成功!");
                                        }

                                    } else {
                                        String msg = jsonObject.getString("Msg");
                                        mToast.setText(msg);
                                        mToast.show();
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
                            mToast.setText("服务器抽风了，请稍后重试");
                            mToast.show();
                        }
                    });
                    mQueue.add(request);

                } else {
                    mProgressDialog.dismiss();
                    mToast.setText("图片上传失败，请稍后重试");
                    mToast.show();
                }
            }
        }, null);
    }

    //弹出创建成功对话框
    private void showSucceedDialog(String message) {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_ok_cancle, null);
        TextView textViewCancel = (TextView) inflate.findViewById(R.id.tv_cancel);
        TextView textViewOK = (TextView) inflate.findViewById(R.id.tv_ok);
        TextView textViewTitle = (TextView) inflate.findViewById(R.id.tv_title);
        textViewTitle.setText(message);
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        textViewOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(CreateCircleActivity.this, MainActivity.class));
                finish();
            }
        });
        dialog.show();
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

    //编辑圈子昵称dialog
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
            EasyPermissions.requestPermissions(this, "需要拍照权限",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 图片选择结果回调
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            isImageChange = true;
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
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < mChose.size(); i++) {
                String gameId = mChose.get(i).getGameId();
                if (i != mChose.size() - 1) {
                    stringBuilder.append(gameId).append(",");
                } else {
                    stringBuilder.append(gameId);
                }
            }
            mStrings = stringBuilder.toString();
        }
    }
}
