package com.chinalooke.yuwan.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.chinalooke.yuwan.activity.CreateCircleActivity;
import com.chinalooke.yuwan.activity.FrequentlyGameActivity;
import com.chinalooke.yuwan.activity.MainActivity;
import com.chinalooke.yuwan.bean.GameMessage;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.Auth;
import com.chinalooke.yuwan.utils.BitmapUtils;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.ImageUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.chinalooke.yuwan.utils.PreferenceUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.picasso.Picasso;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 创建兴趣圈子fragment
 * Created by xiao on 2016/12/3.
 */

public class CreateInterestCircleFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private static final int CHOSE_GAME = 2;
    @Bind(R.id.tv_circle_name)
    TextView mTvCircleName;
    @Bind(R.id.ll_game)
    LinearLayout mLlGame;
    @Bind(R.id.tv_circle_expalin)
    TextView mTvCircleExpalin;
    private RequestQueue mQueue;
    private Toast mToast;
    private CreateCircleActivity mActivity;
    private List<GameMessage.ResultBean> mChose = new ArrayList<>();
    private String mStrings;
    private String mRule;
    private String mCircleName;
    private String mPath;
    private ProgressDialog mProgressDialog;
    private ImgSelConfig mConfig;
    private int RC_ACCESS_FINE_LOCATION = 0;
    private LoginUser.ResultBean mUser;
    private UploadManager mUploadManager;
    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Picasso.with(context).load("file://" + path).into(imageView);
        }
    };
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_create_circle_interest, container, false);
        ButterKnife.bind(this, mView);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (CreateCircleActivity) getActivity();
        mUploadManager = YuwanApplication.getmUploadManager();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(mActivity, LoginUserInfoUtils.KEY);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.rl_game_name, R.id.rl_game, R.id.rl_explain, R.id.btn_create, R.id.rl_head})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_head:
                req();
                break;
            case R.id.rl_game_name:
                showEditDialog();
                break;
            case R.id.rl_game:
                Intent intent2 = new Intent(mActivity, FrequentlyGameActivity.class);
                startActivityForResult(intent2, CHOSE_GAME);
                break;
            case R.id.rl_explain:
                showRuleDialog();
                break;
            case R.id.btn_create:
                if (checkInput()) {
                    if (NetUtil.is_Network_Available(mActivity)) {
                        mProgressDialog = MyUtils.initDialog("创建中", mActivity);
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

    //判断圈子是否存在，创建时防止圈子名称重复
    private void checkIsExistGroup() {
        String uri = Constant.HOST + "isExistGroup&groupName=" + mCircleName;
        StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("checkIsExistGroup", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("Success");
                    if (success) {
                        mProgressDialog.dismiss();
                        mToast.setText("该圈子名称已存在，请重新填写");
                        mToast.show();
                    } else {
                        createCircle();
                        mProgressDialog.dismiss();
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
        Bitmap bitmap = ImageUtils.getBitmap(mPath);
        Bitmap compressBitmap = ImageEngine.getCompressBitmap(bitmap, mActivity);
        if (compressBitmap == null) {
            mToast.setText("当前设置为非wifi下不能上传图片，请连接wifi");
            mToast.show();
            return;
        }
        mUploadManager.put(BitmapUtils.toArray(compressBitmap), fileName, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.error == null) {
                    if (TextUtils.isEmpty(mRule))
                        mRule = "";
                    String longitude = PreferenceUtils.getPrefString(mActivity, "longitude", "");
                    String latitude = PreferenceUtils.getPrefString(mActivity, "latitude", "");
                    String uri = null;
                    try {
                        uri = Constant.HOST + "addGroup&userId=" + mUser.getUserId() + "&lng=" + longitude + "&lat="
                                + latitude + "&gameIds=" + mStrings + "&groupName=" + URLEncoder.encode(mCircleName, "utf8")
                                + "&slogan=" + mRule + "&head=" + Constant.QINIU_DOMAIN + "/" + fileName + "&createTime=" + URLEncoder.encode(DateUtils.getCurrentDateTime(), "utf8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
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
                                            MyUtils.showDialog(mActivity, "提示", "圈子创建成功！\n 可以去我的圈子里面查看!", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    startActivity(new Intent(mActivity, MainActivity.class));
                                                    mActivity.finish();
                                                }
                                            });
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
                            Log.e("TAG", error.getMessage());
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
        return true;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == RC_ACCESS_FINE_LOCATION)
            ImgSelActivity.startActivity(mActivity, mConfig, 5);
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
        if (EasyPermissions.hasPermissions(mActivity, perms)) {
            ImgSelActivity.startActivity(mActivity, mConfig, 5);
        } else {
            EasyPermissions.requestPermissions(this, "需要拍照权限",
                    RC_ACCESS_FINE_LOCATION, perms);
        }
    }

    private void showRuleDialog() {
        final Dialog dialog = new Dialog(mActivity, R.style.Dialog);
        View inflate = View.inflate(mActivity, R.layout.dialog_add_game_rule, null);
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
                if (!TextUtils.isEmpty(mRule))
                    mTvCircleExpalin.setText(mRule);
            }
        });

        dialog.setContentView(inflate);
        dialog.show();
        WindowManager windowManager = mActivity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth(); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    //编辑圈子昵称dialog
    private void showEditDialog() {
        final Dialog dialog = new Dialog(mActivity, R.style.Dialog);
        View inflate = View.inflate(mActivity, R.layout.dialog_edit_name, null);
        TextView viewById = (TextView) inflate.findViewById(R.id.tv_title);
        final EditText etInput = (EditText) inflate.findViewById(R.id.et_input);
        Button btnCancel = (Button) inflate.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) inflate.findViewById(R.id.btn_ok);
        viewById.setText("编辑圈子名称");
        AutoUtils.autoSize(inflate);
        dialog.setContentView(inflate);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Editable text = etInput.getText();
                if (!TextUtils.isEmpty(text))
                    mTvCircleName.setText(text.toString());
            }
        });

        dialog.show();
        etInput.requestFocus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOSE_GAME) {
            mChose = (List<GameMessage.ResultBean>) data.getSerializableExtra("list");
            if (mChose != null && mChose.size() != 0) {
                mLlGame.removeAllViews();
                for (GameMessage.ResultBean resultBean : mChose) {
                    String thumb = resultBean.getThumb();
                    RoundedImageView imageView = new RoundedImageView(mActivity);
                    imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(70, 70));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    String loadImageUrl = ImageEngine.getLoadImageUrl(mActivity, thumb, 70, 70);
                    Picasso.with(mActivity).load(loadImageUrl).into(imageView);
                    imageView.setOval(true);
                    mLlGame.addView(imageView);
                }
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setHead(String path) {
        mPath = path;
        RoundedImageView viewById = (RoundedImageView) mView.findViewById(R.id.iv_gameimage);
        if (viewById != null)
            Picasso.with(mActivity).load("file://" + mPath).into(viewById);
    }
}
