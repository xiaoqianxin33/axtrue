package com.chinalooke.yuwan.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bigkoo.pickerview.OptionsPickerView;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.engine.PickerEngine;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.activity.FrequentlyGameActivity;
import com.chinalooke.yuwan.activity.MainActivity;
import com.chinalooke.yuwan.bean.GameMessage;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.Auth;
import com.chinalooke.yuwan.utils.BitmapUtils;
import com.chinalooke.yuwan.utils.DateUtils;
import com.chinalooke.yuwan.utils.ImageUtils;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.picasso.Picasso;
import com.zhy.autolayout.utils.AutoUtils;

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
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity;
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout;

import static android.app.Activity.RESULT_OK;

/**
 * 发布赛事广告fragment
 * Created by xiao on 2016/12/14.
 */

public class GameAdFragment extends Fragment {

    @Bind(R.id.et_title)
    EditText mEtTitle;
    @Bind(R.id.et_content)
    EditText mEtContent;
    @Bind(R.id.snpl_moment_add_photos)
    BGASortableNinePhotoLayout mPhotosSnpl;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_people)
    TextView mTvPeople;
    @Bind(R.id.tv_times)
    TextView mTvTimes;
    @Bind(R.id.tv_game_name)
    TextView mTvGameName;
    @Bind(R.id.iv_gameimage)
    RoundedImageView mIvGameimage;
    @Bind(R.id.tv_pay)
    TextView mTvPay;
    @Bind(R.id.tv_cup)
    TextView mTvCup;
    @Bind(R.id.tv_playerLevel)
    TextView mTvPlayerLevel;
    private int CHOOSE_GAME = 0;
    private GameMessage.ResultBean mChoseGame;
    private String mGameId;
    private ArrayList<String> mPeopleNumberList = new ArrayList<>();
    private ArrayList<String> mTimesList = new ArrayList<>();
    private ArrayList<String> mMoneyList = new ArrayList<>();
    private Date mBeginDate;
    private Toast mToast;
    private MainActivity mActivity;
    private int REQUEST_CODE_PHOTO_PREVIEW = 2;
    private int REQUEST_CODE_CHOOSE_PHOTO = 3;
    private ProgressDialog mProgressDialog;
    private LoginUser.ResultBean mUser;
    private String mTitle;
    private String mContent;
    private String mTime;
    private String mPeople;
    private String mTimes;
    private ArrayList<String> mPhotos;
    private UploadManager mUploadManager;
    private int upCount;
    private RequestQueue mQueue;
    private int minLevel = -1;
    private int maxLevel;
    private String mPay;
    private String mCup;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gameadfragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mToast = YuwanApplication.getToast();
        mUploadManager = YuwanApplication.getmUploadManager();
        mActivity = (MainActivity) getActivity();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(mActivity, LoginUserInfoUtils.KEY);
        mQueue = YuwanApplication.getQueue();
        initEvent();
    }

    private void initEvent() {
        mPhotosSnpl.init(mActivity);
        mPhotosSnpl.setDelegate(mActivity);
        mPhotosSnpl.setMaxItemCount(3);
        mActivity.setOnBGAListener(new MainActivity.OnBGAListener() {
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.rl_game, R.id.rl_time, R.id.rl_people, R.id.rl_times, R.id.rl_pay, R.id.rl_cup
            , R.id.rl_playerLevel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_playerLevel:
                PickerEngine.alertLevelPicker(mActivity, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3) {
                        minLevel = options1 + 1;
                        maxLevel = options1 + option2 + 1;
                        mTvPlayerLevel.setText("最低" + minLevel + ",最高" + maxLevel);
                    }
                });
                break;
            case R.id.rl_cup:
                showEditDialog();
                break;
            case R.id.rl_pay:
                if (mChoseGame != null) {
                    alertPicker(mMoneyList, "选择游戏币金额", 1);
                } else {
                    mToast.setText("请先选择游戏");
                    mToast.show();
                }
                break;
            case R.id.rl_game:
                Intent intent = new Intent(getActivity(), FrequentlyGameActivity.class);
                intent.putExtra("isNetbar", true);
                startActivityForResult(intent, CHOOSE_GAME);
                break;
            case R.id.rl_time:
                alertTimePicker();
                break;
            case R.id.rl_people:
                if (mPeopleNumberList.size() == 0) {
                    mToast.setText("请先选择游戏");
                    mToast.show();
                } else {
                    alertPicker(mPeopleNumberList, "选择游戏参与人数", 0);
                }
                break;
            case R.id.rl_times:
                alertPicker(mTimesList, "选择游戏场数", 3);
                break;
        }
    }

    private void showEditDialog() {
        final Dialog dialog = new Dialog(mActivity, R.style.Dialog);
        View inflate = View.inflate(mActivity, R.layout.dialog_edit_cup, null);
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
                    mTvCup.setText(text.toString());
                }
            }
        });
        AutoUtils.autoSize(inflate);
        dialog.setContentView(inflate);
        dialog.show();
    }

    private void alertPicker(ArrayList<String> list, String title, final int type) {
        OptionsPickerView<String> optionsPickerView = new OptionsPickerView<>(getActivity());
        optionsPickerView.setTitle(title);
        optionsPickerView.setPicker(list);
        optionsPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                switch (type) {
                    case 0:
                        mTvPeople.setText(mPeopleNumberList.get(options1));
                        break;
                    case 3:
                        mTvTimes.setText(mTimesList.get(options1));
                        break;
                    case 1:
                        mTvPay.setText(mMoneyList.get(options1));
                        break;
                }
            }
        });
        optionsPickerView.setCyclic(false);
        optionsPickerView.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_GAME) {
            if (data != null) {
                mChoseGame = (GameMessage.ResultBean) data.getSerializableExtra("choseGame");
                String thumb = mChoseGame.getThumb();
                mGameId = mChoseGame.getGameId();
                if (!TextUtils.isEmpty(thumb)) {
                    String loadImageUrl = ImageEngine.getLoadImageUrl(mActivity, thumb, 60, 60);
                    Picasso.with(getActivity()).load(loadImageUrl).into(mIvGameimage);
                }

                String name = mChoseGame.getName();
                if (!TextUtils.isEmpty(name))
                    mTvGameName.setText(name);
                setPeopleCount(mChoseGame);
                setTimesCount();
                setMoneyCount();
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            mPhotosSnpl.setData(BGAPhotoPickerActivity.getSelectedImages(data));
        } else if (requestCode == REQUEST_CODE_PHOTO_PREVIEW) {
            mPhotosSnpl.setData(BGAPhotoPickerPreviewActivity.getSelectedImages(data));
        }
    }

    //设置参与游戏币范围
    private void setMoneyCount() {
        String wagerMin = mChoseGame.getWagerMin();
        String wagerMax = mChoseGame.getWagerMax();
        if (!TextUtils.isEmpty(wagerMin) && !TextUtils.isEmpty(wagerMax)) {
            double min = Double.parseDouble(wagerMin);
            double max = Double.parseDouble(wagerMax);
            mMoneyList.add(min + "");
            int imin = (int) min;
            for (int i = imin + 10; i < max; i = i + 10) {
                mMoneyList.add(i + "");
            }
            mMoneyList.add(max + "");
        }
    }

    //设置游戏人数范围
    private void setPeopleCount(GameMessage.ResultBean choseGame) {
        String maxPeopleNumber = choseGame.getMaxPeopleNumber();
        if (!TextUtils.isEmpty(maxPeopleNumber)) {
            int maxPeople = Integer.parseInt(maxPeopleNumber);
            for (int i = 2; i < maxPeople + 1; i = i + 2) {
                mPeopleNumberList.add(i + "");
            }
        }
    }

    private void setTimesCount() {
        String times = mChoseGame.getTimes();
        if (!TextUtils.isEmpty(times)) {
            int parseInt = Integer.parseInt(times);
            for (int i = 1; i <= parseInt; i++) {
                mTimesList.add(i + "");
            }
        } else {
            mTimesList.add("1");
        }
    }

    private void alertTimePicker() {
        OptionsPickerView<String> optionsPickerView = new OptionsPickerView<>(getActivity());
        optionsPickerView.setTitle("选择开战时间");
        final ArrayList<String> dayList = new ArrayList<>();
        final ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
        final ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
        dayList.add("今天");
        dayList.add("明天");
        dayList.add("后天");
        for (int i = 0; i < 3; i++) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (int j = 0; j < 24; j++) {
                arrayList.add(j + "");
            }
            options2Items.add(arrayList);
        }

        for (int k = 0; k < 3; k++) {
            ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
            for (int j = 0; j < 24; j++) {
                ArrayList<String> arrayList = new ArrayList<>();
                for (int i = 0; i < 60; i++) {
                    arrayList.add(i + "");
                }
                arrayLists.add(arrayList);
            }
            options3Items.add(arrayLists);
        }
        optionsPickerView.setPicker(dayList, options2Items, options3Items, true);

        optionsPickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                switch (options1) {
                    case 0:
                        mBeginDate = DateUtils.getCurrentDay(new Date());
                        break;
                    case 1:
                        mBeginDate = DateUtils.getNextDay(new Date());
                        break;
                    case 2:
                        mBeginDate = DateUtils.getNextNextDay(new Date());
                        break;
                }

                String hour = options2Items.get(options1).get(option2);
                if (hour.length() == 1)
                    hour = "0" + hour;
                String minute = options3Items.get(0).get(0).get(options3);
                if (minute.length() == 1)
                    minute = "0" + minute;
                mTvTime.setText(DateUtils.getFormatShortTime(mBeginDate) + "     " + hour + ":" + minute);
            }
        });
        optionsPickerView.setLabels("", "时", "分");
        optionsPickerView.setCyclic(false);
        optionsPickerView.show();
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

    //上传图片至七牛
    private void updateImage() {
        Auth auth = Auth.create(Constant.QINIU_ACCESSKEY, Constant.QINIU_SECRETKEY);
        String token = auth.uploadToken("yuwan");
        final ArrayList<String> paths = new ArrayList<>();
        for (int i = 0; i < mPhotos.size(); i++) {
            Bitmap bitmap = ImageUtils.getBitmap(mPhotos.get(i));
            Bitmap compressBitmap = ImageEngine.getCompressBitmap(bitmap, mActivity);
            if (compressBitmap == null) {
                mToast.setText("当前设置为wifi下才可上传图片，请连接wifi");
                mToast.show();
                mProgressDialog.dismiss();
                return;
            }
            String fileName = "netbar_ad" + new Date().getTime();
            paths.add(Constant.QINIU_DOMAIN + "/" + fileName);
            mUploadManager.put(BitmapUtils.toArray(compressBitmap), fileName, token, new UpCompletionHandler() {
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
        try {
            final String[] arrString = paths.toArray(new String[paths.size()]);
            String s = Arrays.toString(arrString);
            String substring = s.substring(1, s.length() - 1);
            final String replace = substring.replace(" ", "");
            String url = Constant.HOST + "sendAD&type=1&userId=" + mUser.getUserId() + "&title=" + URLEncoder.encode(mTitle, "utf8") +
                    "&gameId=" + mGameId + "&detail=" + URLEncoder.encode(mContent, "UTF-8") + "&startTime=" + URLEncoder.encode(mTime, "UTF-8") + "&maxPeopleNumber=" +
                    mPeople + "&gameCount=" + mTimes + "&imgs=" + replace + "&playerLevel=" + minLevel + "," + maxLevel + "&gamePay=" + mPay + "&cup=" + mCup;
            Log.e("TAG", url);
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
                            mTvTimes.setText("");
                            mTvPeople.setText("");
                            mGameId = "";
                            mTvTime.setText("");
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

    //检查输入
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

        if (TextUtils.isEmpty(mGameId)) {
            mToast.setText("请选择游戏");
            mToast.show();
            return false;
        }

        mTime = mTvTime.getText().toString();
        if (TextUtils.isEmpty(mTime)) {
            mToast.setText("请选择时间");
            mToast.show();
            return false;
        }

        mPeople = mTvPeople.getText().toString();
        if (TextUtils.isEmpty(mPeople)) {
            mToast.setText("请选择比赛人数");
            mToast.show();
            return false;
        }

        mTimes = mTvTimes.getText().toString();
        if (TextUtils.isEmpty(mTimes)) {
            mToast.setText("请选择比赛场次");
            mToast.show();
            return false;
        }
        mCup = mTvCup.getText().toString();
        if (TextUtils.isEmpty(mCup)) {
            mToast.setText("请填写比赛奖金");
            mToast.show();
            return false;
        }

        mPay = mTvPay.getText().toString();
        if (TextUtils.isEmpty(mPay)) {
            mToast.setText("请选择参赛费");
            mToast.show();
            return false;
        }


        if (minLevel < 0) {
            mToast.setText("请选择参赛玩家积分等级范围");
            mToast.show();
            return false;
        }

        return true;
    }
}
