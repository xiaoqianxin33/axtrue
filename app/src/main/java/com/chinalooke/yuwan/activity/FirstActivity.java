package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.model.UserInfo;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/8/23.
 */
public class FirstActivity extends AppCompatActivity {
    //首页logo
    @Bind(R.id.logo_first)
    ImageView logoFirst;
    @Bind(R.id.logoname_first)
    ImageView logonameFirst;
    @Bind(R.id.suibiankankan_first)
    TextView suibiankankanFirst;
    @Bind(R.id.btnLogin_login_first)
    Button btnLoginLoginFirst;
    @Bind(R.id.btn_register_first)
    Button btnRegisterFirst;
    @Bind(R.id.linear_bottom_first)
    LinearLayout linearBottomFirst;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);
        ButterKnife.bind(this);
        try {
            init();
            Log.d("TAG","获取登录用户成功");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG","获取登录用户失败");
        }
    }
//    /初始化 判断是否登录过
    private void init() throws IOException {

        UserInfo user= LoginUserInfoUtils.getLoginUserInfoUtils().getLoginUserInfo(this,LoginUserInfoUtils.KEY);
        Log.d("TAG","---------获取登录用户成功");
            if (user!=null){
                Log.d("TAG","---------用户已登录过");
                LoginUserInfoUtils.getLoginUserInfoUtils().setUserInfo(user);
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Log.d("TAG","---------用户未登录");
            }
    }

    @OnClick({R.id.logo_first, R.id.logoname_first, R.id.suibiankankan_first, R.id.btnLogin_login_first, R.id.btn_register_first, R.id.linear_bottom_first})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logo_first:

                break;
            case R.id.logoname_first:
                break;
            //随便看看
            case R.id.suibiankankan_first:
                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            //登录
            case R.id.btnLogin_login_first:
                Intent intentLogin = new Intent(FirstActivity.this, LoginActivity.class);
                startActivity(intentLogin);
                break;
            //注册
            case R.id.btn_register_first:
                Intent intent2 = new Intent(FirstActivity.this, RegisterActivity.class);
                startActivity(intent2);
                break;
            case R.id.linear_bottom_first:
                break;
        }
    }
}
