package com.chinalooke.yuwan.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.MainActivity;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.Auth;
import com.chinalooke.yuwan.utils.BitmapUtils;
import com.chinalooke.yuwan.utils.ImageUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;

import static android.app.Activity.RESULT_OK;

/**
 * 发布图文广告fragment
 * Created by xiao on 2016/12/14.
 */

public class PhotoAdFragment extends Fragment {

    @Bind(R.id.et_title)
    EditText mEtTitle;
    @Bind(R.id.et_content)
    EditText mEtContent;
    @Bind(R.id.snpl_moment_add_photos)
    BGASortableNinePhotoLayout mPhotosSnpl;
    private MainActivity mActivity;
    private LoginUser.ResultBean mUser;
    private RequestQueue mQueue;
    private int REQUEST_CODE_PHOTO_PREVIEW = 2;
    private int REQUEST_CODE_CHOOSE_PHOTO = 3;
    private ProgressDialog mProgressDialog;
    private Toast mToast;
    private String mTitle;
    private String mContent;
    private ArrayList<String> mPhotos;
    private UploadManager mUploadManager;
    private int upCount;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photoadfragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mUploadManager = YuwanApplication.getmUploadManager();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(mActivity, LoginUserInfoUtils.KEY);
        initEvent();
    }

    private void initEvent() {
        mPhotosSnpl.init(mActivity);
        mPhotosSnpl.setDelegate(mActivity);
        mPhotosSnpl.setMaxItemCount(3);
        mActivity.setPhotoOnBGAListener(new MainActivity.OnBGAListener() {
            @Override
            public void onClickDeleteNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
                mPhotosSnpl.removeItem(position);
            }

            @Override
            public void onClickNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
                startActivityForResult(BGAPhotoPickerPreviewActivity.newIntent(mActivity, mPhotosSnpl.getMaxItemCount(), models, models, position, false), REQUEST_CODE_PHOTO_PREVIEW);
            }

            @Override
            public void onClickAddNinePhotoItem(BGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, ArrayList<String> models) {
                choicePhotoWrapper();
            }
        });
    }

    private void choicePhotoWrapper() {
        File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");
        startActivityForResult(BGAPhotoPickerActivity.newIntent(mActivity, takePhotoDir, mPhotosSnpl.getMaxItemCount(), mPhotosSnpl.getData(), true), REQUEST_CODE_CHOOSE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            mPhotosSnpl.setData(BGAPhotoPickerActivity.getSelectedImages(data));
        } else if (requestCode == REQUEST_CODE_PHOTO_PREVIEW) {
            mPhotosSnpl.setData(BGAPhotoPickerPreviewActivity.getSelectedImages(data));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void releaseAd() {
        if (NetUtil.is_Network_Available(mActivity)) {
            if (checkInput()) {
                mProgressDialog = MyUtils.initDialog("发布中", mActivity);
                mProgressDialog.show();
                updateImage();
            }
        } else {
            mToast.setText("网络未连接");
            mToast.show();
        }
    }

    private void updateImage() {
        Auth auth = Auth.create(Constant.QINIU_ACCESSKEY, Constant.QINIU_SECRETKEY);
        String token = auth.uploadToken("yuwan");
        final ArrayList<String> paths = new ArrayList<>();
        for (int i = 0; i < mPhotos.size(); i++) {
            Bitmap bitmap = ImageUtils.getBitmap(mPhotos.get(i));
            Bitmap bitmap1 = ImageUtils.compressByScale(bitmap, 235, 235);
            String fileName = "netbar_ad" + new Date().getTime();
            paths.add(Constant.QINIU_DOMAIN + "/" + fileName);
            mUploadManager.put(BitmapUtils.toArray(bitmap1), fileName, token, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if (info.error == null) {
                        upCount++;
                        if (upCount == mPhotos.size()) {
                            sendAd(paths);
                        }
                    }
                }
            }, null);
        }
    }

    private void sendAd(ArrayList<String> paths) {
        final String[] arrString = paths.toArray(new String[paths.size()]);
        String s = Arrays.toString(arrString);
        String substring = s.substring(1, s.length() - 1);
        final String replace = substring.replace(" ", "");
        try {
            String url = Constant.HOST + "sendAD&type=1&userId=" + mUser.getUserId() + "&title=" + mTitle +
                    "&detail=" + URLEncoder.encode(mContent, "UTF-8") + "&imgs=" + replace;
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("Success");
                        if (success) {
                            String result = jsonObject.getString("Result");
                            mToast.setText(result);
                            mToast.show();
                            mEtContent.setText("");
                            mEtTitle.setText("");
                            for (int i = 1; i <= mPhotos.size(); i++) {
                                mPhotosSnpl.removeItem(i);
                            }
                        } else {
                            MyUtils.showMsg(mToast, response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    private boolean checkInput() {
        mTitle = mEtTitle.getText().toString();
        if (TextUtils.isEmpty(mTitle)) {
            mEtTitle.setError("请填写标题");
            mEtTitle.setFocusable(true);
            mEtTitle.setFocusableInTouchMode(true);
            mEtTitle.requestFocus();
            return false;
        }

        mContent = mEtContent.getText().toString();
        if (TextUtils.isEmpty(mContent)) {
            mEtContent.setError("请填写内容");
            mEtContent.setFocusable(true);
            mEtContent.setFocusableInTouchMode(true);
            mEtContent.requestFocus();
            return false;
        }
        mPhotos = mPhotosSnpl.getData();
        if (mPhotos == null || mPhotos.size() == 0) {
            mToast.setText("请添加至少一张图片");
            mToast.show();
            return false;
        }
        return true;
    }
}
