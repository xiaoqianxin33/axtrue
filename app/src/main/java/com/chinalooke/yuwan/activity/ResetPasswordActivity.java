package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.ResultDatas;
import com.chinalooke.yuwan.utils.GetHTTPDatas;
import com.chinalooke.yuwan.utils.Validator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/8/29.
 */
public class ResetPasswordActivity extends AppCompatActivity {
    //密码
    @Bind(R.id.new_reset_password)
    EditText mNewPassword;
    //重填密码
    @Bind(R.id.renew_reset_password)
    EditText mReNewPassword;
    //获取验证码
    @Bind(R.id.btn_next_reset_password)
    Button mbtNextPassWord;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_reset_password);
        ButterKnife.bind(this);

    }
    @OnClick(R.id.btn_next_reset_password)
        public void onClick(View view) {
        resetPassword();
    }
    private void resetPassword(){
        String passWord=mNewPassword.getText().toString();
        String repassWord=mReNewPassword.getText().toString();
        if(!passWord.equals(repassWord)){
            mNewPassword.setError("两次输入密码不一致");
        }else if(!Validator.getValidator().isPassword(passWord)){
            //密码不在6-16位之间
            mNewPassword.setError("密码在6-16位之间");
        }else {
            //提交修改密码
            Intent intent=getIntent();
            String phone=intent.getStringExtra("phone");
            String uri= Constant.FORGOT_PWD+"&"+Constant.PHONE+phone+"&"+Constant.PWD+passWord;
            submitPassword(uri);
        }
    }
    private void submitPassword(String uri){
        GetHTTPDatas getHTTPDatas=new GetHTTPDatas();
        getHTTPDatas.getHTTPIsPhoneExists(uri);
        ResultDatas resultDatas= getHTTPDatas.getResult();
        if (resultDatas!=null){
            if("true".equals(resultDatas.getSuccess()) && "false".equals(resultDatas.getResult())){
                Toast.makeText(this,"修改失败",Toast.LENGTH_SHORT).show();

            }
            if("true".equals(resultDatas.getSuccess()) && "true".equals(resultDatas.getResult())){
                Log.d("TAG","修改成功跳到下一步");
                    successResetPassWord();
            }
        }

    }

    /**
     * 修改成功跳到下一步
     */
    private void successResetPassWord(){

    }
}
