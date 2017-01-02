package com.chinalooke.yuwan.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.PushMessage;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.db.ExchangeHelper;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.Auth;
import com.chinalooke.yuwan.utils.BitmapUtils;
import com.chinalooke.yuwan.utils.ImageUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.j256.ormlite.dao.Dao;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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

//申请重判界面
public class ApplyJudgeActivity extends AutoLayoutActivity implements BGASortableNinePhotoLayout.Delegate, EasyPermissions.PermissionCallbacks {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.snpl_moment_add_photos)
    BGASortableNinePhotoLayout mPhotosSnpl;
    private PushMessage mPushMessage;
    private String mId;
    private int REQUEST_CODE_PHOTO_PREVIEW = 2;
    private static final int REQUEST_CODE_PERMISSION_PHOTO_PICKER = 1;
    private int REQUEST_CODE_CHOOSE_PHOTO = 3;
    private Toast mToast;
    private UploadManager mUploadManager;
    private int upCount;
    private ProgressDialog mProgressDialog;
    private LoginUser.ResultBean mUser;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_judge);
        ButterKnife.bind(this);
        mToast = YuwanApplication.getToast();
        mUploadManager = YuwanApplication.getmUploadManager();
        mProgressDialog = MyUtils.initDialog("提交中", this);
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mQueue = YuwanApplication.getQueue();
        intData();
        initEvent();
    }

    private void initEvent() {
        mPhotosSnpl.init(this);
        mPhotosSnpl.setDelegate(this);
    }

    private void intData() {
        mPushMessage = (PushMessage) getIntent().getSerializableExtra("message");
        mId = getIntent().getStringExtra("id");
    }

    @OnClick({R.id.iv_back, R.id.tv_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_submit:
                ArrayList<String> mPhotos = mPhotosSnpl.getData();
                if (mPhotos == null || mPhotos.size() == 0) {
                    mToast.setText("请上传至少一张图片");
                    mToast.show();
                } else {
                    mProgressDialog.show();
                    uploadPhotos(mPhotos);
                }
                break;
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

    //上传图片至七牛
    private void uploadPhotos(final ArrayList<String> mPhotos) {
        Auth auth = Auth.create(Constant.QINIU_ACCESSKEY, Constant.QINIU_SECRETKEY);
        String token = auth.uploadToken("yuwan");
        final ArrayList<String> paths = new ArrayList<>();
        for (int i = 0; i < mPhotos.size(); i++) {
            Bitmap bitmap = ImageUtils.getBitmap(mPhotos.get(i));
            Bitmap bitmap1 = ImageEngine.getCompressBitmap(bitmap, getApplicationContext());
            if (bitmap1 == null) {
                mProgressDialog.dismiss();
                mToast.setText("当前设置为非wifi下不能上传图片，请连接wifi");
                mToast.show();
                return;
            }
            String fileName = "game" + new Date().getTime();
            paths.add(Constant.QINIU_DOMAIN + "/" + fileName);
            upCount = 0;
            mUploadManager.put(BitmapUtils.toArray(bitmap1), fileName, token, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if (info.error == null) {
                        upCount++;
                        if (upCount == mPhotos.size()) {
                            applyJudge(paths);
                        }
                    } else {
                        mProgressDialog.dismiss();
                        mToast.setText("图片上传失败，请稍后重试");
                        mToast.show();
                    }
                }
            }, null);
        }
    }

    //申请重判
    private void applyJudge(ArrayList<String> paths) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < paths.size(); i++) {
            if (i == paths.size() - 1)
                stringBuilder.append(paths.get(i));
            else
                stringBuilder.append(paths.get(i)).append(",");
        }
        String url = Constant.HOST + "loserConfirm&userId=" + mUser.getUserId() + "&gameDeskId=" + mId + "&gameImgs=" + stringBuilder.toString();
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                if (AnalysisJSON.analysisJson(response)) {
                    ExchangeHelper helper = ExchangeHelper.getHelper(getApplicationContext());
                    try {
                        Dao<PushMessage, Integer> pushDao = helper.getPushDao();
                        mPushMessage.setDone(true);
                        pushDao.update(mPushMessage);
                        pushDao.closeLastIterator();
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                    MyUtils.showDialog(ApplyJudgeActivity.this, "提示", "图片上传成功，请等待重判结果！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                } else {
                    MyUtils.showMsg(mToast, response);
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
}
