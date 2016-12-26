package com.chinalooke.yuwan.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.bean.LoginUser;
import com.chinalooke.yuwan.bean.NearbyPeople;
import com.chinalooke.yuwan.config.YuwanApplication;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.utils.MyUtils;
import com.chinalooke.yuwan.utils.NetUtil;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//发送好友申请页面
public class SendUpAddFriendActivity extends AutoLayoutActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_skip)
    TextView mTvSkip;
    @Bind(R.id.et_input)
    EditText mEtInput;
    private String mInput;
    private LoginUser.ResultBean mUser;
    private RequestQueue mQueue;
    private Toast mToast;
    private ProgressDialog mProgressDialog;
    private String mPeopleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_up_add_frind);
        ButterKnife.bind(this);
        mUser = (LoginUser.ResultBean) LoginUserInfoUtils.readObject(getApplicationContext(), LoginUserInfoUtils.KEY);
        mQueue = YuwanApplication.getQueue();
        mToast = YuwanApplication.getToast();
        initView();
        initData();
        initEvent();
    }

    private void initData() {
        NearbyPeople.ResultBean nearbyPeople = (NearbyPeople.ResultBean) getIntent().getSerializableExtra("people");
        if (nearbyPeople != null) {
            mPeopleId = nearbyPeople.getId();
        } else {
            mPeopleId = getIntent().getStringExtra("peopleId");
        }
    }

    private void initEvent() {
        mEtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                return true;
            }
        });
    }

    private void initView() {
        mTvTitle.setText("验证");
        mTvSkip.setText("发送");
    }

    @OnClick({R.id.iv_back, R.id.tv_skip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_skip:
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        if (checkInput()) {
            if (NetUtil.is_Network_Available(getApplicationContext())) {
                mProgressDialog = MyUtils.initDialog("发送中", this);
                mProgressDialog.show();
                String uri = null;
                try {
                    uri = Constant.HOST + "sendUpAddFrind&userId=" + mUser.getUserId() + "&friendId=" + mPeopleId + "&sendUpMsg=" + URLEncoder.encode(mInput, "UTF-8");
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
                                        MyUtils.showDialog(SendUpAddFriendActivity.this, "提示", "验证发送成功，请等待对方确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        });
                                    } else {
                                        mToast.setText("发送失败，请稍后重试");
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
                mToast.setText("网络不可用，请检查网络连接");
                mToast.show();
            }
        }
    }

    private boolean checkInput() {
        mInput = mEtInput.getText().toString();
        if (TextUtils.isEmpty(mInput)) {
            mEtInput.setError("请填写验证内容");
            mEtInput.requestFocus();
            return false;
        }
        return true;
    }
}
