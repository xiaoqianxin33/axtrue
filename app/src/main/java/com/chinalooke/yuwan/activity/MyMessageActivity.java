package com.chinalooke.yuwan.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.adapter.MyBaseAdapter;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.PushMessage;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.db.ExchangeHelper;
import com.chinalooke.yuwan.engine.ImageEngine;
import com.chinalooke.yuwan.utils.AnalysisJSON;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.j256.ormlite.dao.Dao;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

//我的推送消息界面
public class MyMessageActivity extends AutoLayoutActivity implements EasyPermissions.PermissionCallbacks {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.tv_none)
    TextView mTvNone;
    private RequestQueue mQueue;
    private Toast mToast;
    private ProgressDialog mProgressDialog;
    private LoginUser.ResultBean mUser;
    private MyAdapter mMyAdapter;
    private List<PushMessage> mPushMessages = new ArrayList<>();
    private Dao<PushMessage, Integer> mPushDao;
    private ImgSelConfig mConfig;
    private int RC_ACCESS_FINE_LOCATION = 0;
    private int REQUEST_CODE = 2;
    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Picasso.with(context).load("file://" + path).into(imageView);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        ExchangeHelper helper = ExchangeHelper.getHelper(getApplicationContext());
        try {
            mPushDao = helper.getPushDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mConfig = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(true)
                // “确定”按钮背景色
                .btnBgColor(Color.GRAY)
                // “确定”按钮文字颜色
                .btnTextColor(Color.BLUE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 返回图标ResId
                .backResId(R.drawable.ic_back)
                // 标题
                .title("选择仲裁图片")
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

        initView();
        initData();
    }

    private void initView() {
        mTvTitle.setText("我的消息");
        mMyAdapter = new MyAdapter(mPushMessages);
        mListView.setAdapter(mMyAdapter);
        mProgressDialog = MyUtils.initDialog("正在提交", MyMessageActivity.this);
    }

    private void initData() {
        try {
            mPushMessages.clear();
            mPushMessages.addAll(mPushDao.queryForAll());
            mMyAdapter.notifyDataSetChanged();
            if (mPushMessages.size() == 0) {
                mTvNone.setText("暂无消息");
                mTvNone.setVisibility(View.VISIBLE);
            } else {
                mTvNone.setVisibility(View.GONE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mPushDao != null)
            try {
                mPushDao.closeLastIterator();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }

    class MyAdapter extends MyBaseAdapter {


        MyAdapter(List dataSource) {
            super(dataSource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(MyMessageActivity.this, R.layout.item_my_message_listview, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final PushMessage pushMessage = mPushMessages.get(position);
            String date = pushMessage.getDate();
            if (!TextUtils.isEmpty(date))
                viewHolder.mTvTime.setText(date);

            boolean done = pushMessage.isDone();
            viewHolder.mBtnOk.setSelected(done);
            viewHolder.mBtnOk.setEnabled(!done);
            viewHolder.mBtnReJudge.setVisibility(done ? View.GONE : View.VISIBLE);
            String type = pushMessage.getType();
            final String content = pushMessage.getContent();
            if (!TextUtils.isEmpty(content))
                viewHolder.mTvMessage.setText(content);
            String temp = pushMessage.getTemp();
            final String[] split = temp.split(",");
            if (!TextUtils.isEmpty(split[1])) {
                String loadImageUrl = ImageEngine.getLoadImageUrl(getApplicationContext(), split[1], 100, 100);
                Picasso.with(getApplicationContext()).load(loadImageUrl).into(viewHolder.mRoundedImageView);
            }
            switch (type) {
                case "userInfo":
                    viewHolder.mBtnReJudge.setText("拒绝");
                    viewHolder.mBtnOk.setText("同意");

                    viewHolder.mBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addFriendsClick(split[0], pushMessage, 0);
                        }
                    });
                    viewHolder.mBtnReJudge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addFriendsClick(split[0], pushMessage, 1);
                        }
                    });

                case "joindeskGameDesk":
                    viewHolder.mBtnReJudge.setText("忽略");
                    viewHolder.mBtnOk.setText("加入");
                    viewHolder.mBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (NetUtil.is_Network_Available(getApplicationContext())) {
                                joinDesk(split[0], pushMessage);
                            } else {
                                mToast.setText("网络不可用，请检查网络连接");
                                mToast.show();
                            }
                        }
                    });

                    viewHolder.mBtnReJudge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pushMessage.setDone(true);
                            try {
                                mPushDao.update(pushMessage);
                                initData();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case "resultGameDesk":
                    viewHolder.mBtnOk.setText("同意");
                    viewHolder.mBtnReJudge.setText("申请重判");
                    viewHolder.mBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loserConfirm(split[0], pushMessage);
                        }
                    });

                    viewHolder.mBtnReJudge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (NetUtil.is_Network_Available(getApplicationContext())) {
                                req();
                            } else {
                                mToast.setText("网络不可用，请检查网络连接");
                                mToast.show();
                            }
                        }
                    });
                    break;
                case "gameDesk":
                    viewHolder.mBtnReJudge.setVisibility(View.GONE);
                    viewHolder.mBtnOk.setText("确定");
                    viewHolder.mBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pushMessage.setDone(true);
                            try {
                                mPushDao.update(pushMessage);
                                initData();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case "netbarGameDesk":
                    viewHolder.mBtnReJudge.setVisibility(View.GONE);
                    viewHolder.mBtnOk.setText("查看");
                    viewHolder.mBtnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pushMessage.setDone(true);
                            try {
                                mPushDao.update(pushMessage);
                                initData();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(MyMessageActivity.this, JudgeActivity.class);
                            intent.putExtra("gameDeskId", split[0]);
                            intent.putExtra("count", split[1]);
                            startActivity(intent);
                        }
                    });
                    break;
            }

            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tv_time)
            TextView mTvTime;
            @Bind(R.id.roundedImageView)
            RoundedImageView mRoundedImageView;
            @Bind(R.id.tv_message)
            TextView mTvMessage;
            @Bind(R.id.btn_ok)
            Button mBtnOk;
            @Bind(R.id.btn_reJudge)
            Button mBtnReJudge;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    //点击加入游戏桌事件
    private void joinDesk(String s, final PushMessage pushMessage) {
        mProgressDialog.show();
        String url = Constant.HOST + "takePartInGameDesk&userId=" + mUser.getUserId() +
                "&gameDeskId=" + s + "&role=1";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                if (AnalysisJSON.analysisJson(response)) {
                    pushMessage.setDone(true);
                    try {
                        mPushDao.update(pushMessage);
                        initData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    MyUtils.showDialog(MyMessageActivity.this, "提示", "加入战场成功！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
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
                mToast.setText("服务器抽风了，请稍后再试");
                mToast.show();
            }
        });
        mQueue.add(request);
    }

    //同意添加好友事件
    private void addFriendsClick(String str, final PushMessage pushMessage, int i) {
        mProgressDialog.show();
        if (!TextUtils.isEmpty(str)) {
            String url = Constant.HOST + "agreeFriend&userId=" + mUser.getUserId() + "&friendId=" + str + "&agree=" + i + "&disagreeMsg=";
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressDialog.dismiss();
                    if (AnalysisJSON.analysisJson(response)) {
                        mToast.setText("好友添加成功！");
                        mToast.show();
                        pushMessage.setDone(true);
                        try {
                            mPushDao.update(pushMessage);
                            initData();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        MyUtils.showMsg(mToast, response);
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

    // 输家确定输
    private void loserConfirm(String gameDeskId, final PushMessage pushMessage) {
        if (NetUtil.is_Network_Available(getApplicationContext())) {
            mProgressDialog.show();
            String url = Constant.HOST + "loserConfirm&userId=" + mUser.getUserId() + "&gameDeskId=" + gameDeskId;
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    mProgressDialog.dismiss();
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("Success");
                            if (success) {
                                pushMessage.setDone(true);
                                mPushDao.update(pushMessage);
                                boolean result = jsonObject.getBoolean("Result");
                                if (result) {
                                    MyUtils.showDialog(MyMessageActivity.this, "提示", "已确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            initData();
                                        }
                                    });
                                }
                            } else {
                                MyUtils.showMsg(mToast, response);
                            }
                        } catch (JSONException | SQLException e) {
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
        } else {
            mToast.setText("网络不可用，请检查网络连接");
            mToast.show();
        }
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
            mProgressDialog.show();
            upLoadingImage(pathList);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //上传仲裁图片至七牛
    private void upLoadingImage(List<String> pathList) {

    }
}
