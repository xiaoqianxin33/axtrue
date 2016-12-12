package com.chinalooke.yuwan.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.Circle;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.Auth;
import com.chinalooke.yuwan.utils.BitmapUtils;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.ImageUtils;
import com.chinalooke.yuwan.utils.LocationUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SendDynamicActivity extends AutoLayoutActivity implements BGASortableNinePhotoLayout.Delegate, EasyPermissions.PermissionCallbacks {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_address)
    TextView mTvAddress;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.et_content)
    EditText mEtContent;
    @Bind(R.id.snpl_moment_add_photos)
    BGASortableNinePhotoLayout mPhotosSnpl;
    private int REQUEST_CODE_PHOTO_PREVIEW = 2;
    private static final int REQUEST_CODE_PERMISSION_PHOTO_PICKER = 1;
    private int REQUEST_CODE_CHOOSE_PHOTO = 3;
    private LoginUser.ResultBean mUserInfo;
    private String mContent;
    private Toast mToast;
    private UploadManager mUploadManager;
    private ProgressDialog mProgressDialog;
    private String groupId = "0";
    private RequestQueue mQueue;
    private int upCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_dynamic);
        ButterKnife.bind(this);
        mUserInfo = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mToast = YuwanApplication.getToast();
        mUploadManager = YuwanApplication.getmUploadManager();
        mQueue = YuwanApplication.getQueue();
        initData();
        initView();
        initEvent();
    }

    private void initData() {
        Circle.ResultBean circle = (Circle.ResultBean) getIntent().getSerializableExtra("circle");
        if (circle != null)
            groupId = circle.getGroupId();
    }

    private void initEvent() {
        mPhotosSnpl.init(this);
        mPhotosSnpl.setDelegate(this);
    }

    private void initView() {
        mTvTitle.setText("发动态");
        mTvSkip.setText("发送");
        mTvSkip.setVisibility(View.VISIBLE);
        mTvSkip.setTextColor(getResources().getColor(R.color.orange));
        AMapLocation aMapLocation = LocationUtils.getAMapLocation();
        if (aMapLocation != null) {
            String address = aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getStreet();
            if (!TextUtils.isEmpty(address))
                mTvAddress.setText(address);
        }
    }

    @Override
    public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
        choicePhotoWrapper();
    }

    @Override
    public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        mPhotosSnpl.removeItem(position);
    }

    @Override
    public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
        startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent(this, mPhotosSnpl.getMaxItemCount(), models, models, position, false), REQUEST_CODE_PHOTO_PREVIEW);
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_PHOTO_PICKER)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
            try {
                File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");
                startActivityForResult(BGAPhotoPickerActivity.newIntent(this, takePhotoDir, mPhotosSnpl.getMaxItemCount(), mPhotosSnpl.getData(), true), REQUEST_CODE_CHOOSE_PHOTO);
            } catch (Exception ignored) {
            }

        } else {
            EasyPermissions.requestPermissions(this, "图片选择需要以下权限:\n\n1.访问设备上的照片", REQUEST_CODE_PERMISSION_PHOTO_PICKER, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_CODE_PERMISSION_PHOTO_PICKER) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            mPhotosSnpl.setData(BGAPhotoPickerActivity.getSelectedImages(data));
        } else if (requestCode == REQUEST_CODE_PHOTO_PREVIEW) {
            mPhotosSnpl.setData(BGAPhotoPickerPreviewActivity.getSelectedImages(data));
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_skip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_skip:
                if (checkInput()) {
                    if (NetUtil.is_Network_Available(getApplicationContext())) {
                        mProgressDialog = MyUtils.initDialog("正在发表", SendDynamicActivity.this);
                        mProgressDialog.show();
                        ArrayList<String> mPhotos = mPhotosSnpl.getData();
                        if (mPhotos != null && mPhotos.size() != 0) {
                            uploadPhotos(mPhotos);
                        } else {
                            sendDynamic(null);
                        }
                    } else {
                        mToast.setText("网速不给力啊，换个地方试试");
                        mToast.show();
                    }
                }
                break;
        }
    }

    //上传图片至七牛
    private void uploadPhotos(final ArrayList<String> mPhotos) {
        Auth auth = Auth.create(Constant.QINIU_ACCESSKEY, Constant.QINIU_SECRETKEY);
        String token = auth.uploadToken("yuwan");
        final ArrayList<String> paths = new ArrayList<>();
        for (int i = 0; i < mPhotos.size(); i++) {
            Bitmap bitmap = ImageUtils.getBitmap(mPhotos.get(i));
            Bitmap bitmap1 = ImageUtils.compressByScale(bitmap, 235, 235);
            String fileName = "dynamic" + new Date().getTime();
            paths.add(Constant.QINIU_DOMAIN + "/" + fileName);
            mUploadManager.put(BitmapUtils.toArray(bitmap1), fileName, token, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if (info.error == null) {
                        upCount++;
                        if (upCount == mPhotos.size()) {
                            sendDynamic(paths);
                        }
                    }
                }
            }, null);
        }
    }

    //发布动态
    private void sendDynamic(ArrayList<String> paths) {
        String uri;
        try {
            String content = URLEncoder.encode(mContent, "UTF-8");
            String address = URLEncoder.encode(mTvAddress.getText().toString(), "UTF-8");
            String time = URLEncoder.encode(DateUtils.getCurrentDateTime(), "UTF-8");
            if (paths != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < paths.size(); i++) {
                    if (i == paths.size() - 1)
                        stringBuilder.append(paths.get(i));
                    else
                        stringBuilder.append(paths.get(i)).append(",");
                }
                uri = Constant.HOST + "sendActive&userId=" + mUserInfo.getUserId() + "&content=" + content + "&imgs="
                        + stringBuilder.toString() + "&sendTime=" + time + "&groupId=" + groupId + "&address=" + address;
            } else {
                uri = Constant.HOST + "sendActive&userId=" + mUserInfo.getUserId() + "&content=" + content
                        + "&sendTime=" + time + "&groupId=" + groupId + "&address=" + address;
            }

            Log.e("TAG", uri);
            StringRequest request = new StringRequest(uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressDialog.dismiss();
                    Log.e("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            boolean result = jsonObject.getBoolean("Result");
                            if (result) {
                                MyUtils.showDialog(SendDynamicActivity.this, "提示", "动态发布成功！", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                            } else {
                                String msg = jsonObject.getString("Msg");
                                mToast.setText(msg);
                                mToast.show();
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
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressDialog.dismiss();
                    mToast.setText("服务器抽风了，换个地方试试");
                    mToast.show();
                }
            });

            mQueue.add(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //弹出发布成功dialog
    private void showJoinSucceedDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_desk_succeed, null);
        TextView tvOk = (TextView) inflate.findViewById(R.id.tv_ok);
        TextView tv_message = (TextView) inflate.findViewById(R.id.tv_message);
        TextView tv_content = (TextView) inflate.findViewById(R.id.tv_content);
        tv_content.setText("");
        tv_message.setText("发布动态成功！");
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.setContentView(inflate);
        dialog.show();
    }

    //检查输入
    private boolean checkInput() {
        mContent = mEtContent.getText().toString();
        if (TextUtils.isEmpty(mContent)) {
            mEtContent.setError("填写点感想吧");
            mEtContent.requestFocus();
            return false;
        }
        return true;
    }
}
