package com.chinalooke.yuwan.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.Auth;
import com.chinalooke.yuwan.utils.BitmapUtils;
import com.chinalooke.yuwan.utils.ImageUtils;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//网吧资料提交页面
public class NetbarInfoActivity extends AutoLayoutActivity {

    @Bind(R.id.iv_back)
    FrameLayout mIvBack;
    @Bind(R.id.iv_arrow_head)
    ImageView mIvArrowHead;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.roundedImageView)
    RoundedImageView mRoundedImageView;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_location)
    TextView mTvLocation;
    @Bind(R.id.et_address)
    EditText mEtAddress;
    private String mNetbarId;
    private String mNetbarLicense;
    private ImgSelConfig mConfig;
    private int CHOSE_HEAD_REQUEST = 0;
    private String mHead;
    private String mName;
    private UploadManager mUploadManager;
    private RequestQueue mQueue;
    private Toast mToast;
    private String mAddress;
    private String mAddressDetail;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netbar_info);
        ButterKnife.bind(this);
        mUploadManager = YuwanApplication.getmUploadManager();
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        initPhotoPicker();
    }

    //初始化图片选择器
    private void initPhotoPicker() {
        ImageLoader loader = new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Picasso.with(context).load("file://" + path).into(imageView);
            }
        };

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

    private void initData() {
        mNetbarId = getIntent().getStringExtra("netbarId");
        mNetbarLicense = getIntent().getStringExtra("netbarLicense");
        mHead = getIntent().getStringExtra("head");
        if (!TextUtils.isEmpty(mHead)) {
            String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), mHead, 120, 120);
            Picasso.with(getApplicationContext()).load(loadImageUrl).into(mRoundedImageView);
        }
    }

    private void initView() {
        mTvTitle.setText("完善网吧资料");
        mIvBack.setVisibility(View.GONE);
        mIvArrowHead.setVisibility(View.GONE);
    }

    @OnClick({R.id.rl_head, R.id.rl_name, R.id.rl_location, R.id.btn_enter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_head:
                ImgSelActivity.startActivity(this, mConfig, CHOSE_HEAD_REQUEST);
                break;
            case R.id.rl_name:
                showEditDialog();
                break;
            case R.id.rl_location:
                MyUtils.hiddenKeyboard(this, view);
                selectLocation();
                break;
            case R.id.btn_enter:
                if (checkInput()) {
                    if (NetUtil.is_Network_Available(getApplicationContext())) {
                        uploadingHeadImage();
                    } else {
                        mToast.setText("网络不可用，请检查网络连接");
                        mToast.show();
                    }
                }
                break;
        }
    }

    //上传头像至7七牛
    private void uploadingHeadImage() {
        mProgressDialog = MyUtils.initDialog("提交中", this);
        mProgressDialog.show();
        Auth auth = Auth.create(Constant.QINIU_ACCESSKEY, Constant.QINIU_SECRETKEY);
        String token = auth.uploadToken("yuwan");
        final String fileName = "head" + new Date().getTime();
        Bitmap bitmap = ImageUtils.getBitmap(mHead);
        Bitmap compressBitmap = ImageEngine.getCompressBitmap(bitmap, getApplicationContext());
        if (compressBitmap == null) {
            mToast.setText("当前设置为非wifi下不能上传图片，请连接wifi");
            mToast.show();
            return;
        }
        mUploadManager.put(BitmapUtils.toArray(compressBitmap), fileName, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.error == null) {
                    mHead = Constant.QINIU_DOMAIN + "/" + fileName;
                    submit();
                } else {
                    mProgressDialog.dismiss();
                }
            }
        }, null);
    }

    //提交资料至服务器
    private void submit() {
        try {
            String url = Constant.HOST + "updateUserInfo&userId=" + mNetbarId + "&headImg=" + mHead + "&nickName=" +
                    URLEncoder.encode(mName, "utf8") + "&address=" + URLEncoder.encode(mAddress + mAddressDetail, "utf8") + "&license=" + mNetbarLicense;
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
                                    MyUtils.showDialog(NetbarInfoActivity.this, "提示", "您的资料已提交，正在审核", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
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
                    mToast.setText("服务器抽风了，请稍后再试");
                    mToast.show();
                }
            });

            mQueue.add(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    //检查输入
    private boolean checkInput() {
        mName = mTvName.getText().toString();
        if (TextUtils.isEmpty(mName) || "请填写网吧名称".equals(mName)) {
            mToast.setText("请填写网吧名称");
            mToast.show();
            return false;
        }

        mAddress = mTvLocation.getText().toString();

        if (TextUtils.isEmpty(mAddress) || "请选择".equals(mAddress)) {
            mToast.setText("请选择网吧所在地");
            mToast.show();
            return false;
        }

        mAddressDetail = mEtAddress.getText().toString();
        if (TextUtils.isEmpty(mAddressDetail)) {
            mToast.setText("请填写网吧详细地址");
            mToast.show();
            return false;
        }
        return true;
    }

    //网吧地址地区选择
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

    //网吧名字填写输入框
    private void showEditDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View inflate = View.inflate(this, R.layout.dialog_edit_name, null);
        final EditText editText = (EditText) inflate.findViewById(R.id.et_input);
        TextView textView = (TextView) inflate.findViewById(R.id.tv_title);
        textView.setText("编辑网吧名称");
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
                }
            }
        });
        AutoUtils.autoSize(inflate);
        dialog.setContentView(inflate);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOSE_HEAD_REQUEST && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            mHead = pathList.get(0);
            Picasso.with(getApplicationContext()).load("file://" + mHead).into(mRoundedImageView);
        }
    }
}
