package com.chinalooke.yuwan.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.chinalooke.yuwan.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 编辑姓名的dialog
 * Created by xiao on 2016/11/9.
 */

public class EditNameDialog extends AlertDialog {
    @Bind(R.id.et_input)
    EditText mEtInput;
    @Bind(R.id.iv_cancel)
    ImageView mIvCancel;
    @Bind(R.id.btn_cancel)
    Button mBtnCancel;
    @Bind(R.id.btn_ok)
    Button mBtnOk;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    public EditNameDialog(Context context) {
        super(context);
    }

    protected EditNameDialog(Context context, int theme) {
        super(context, theme);
    }

    protected EditNameDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_name);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        initEvent();
    }

    private void initEvent() {
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });

        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    String input = mEtInput.getText().toString();
                    yesOnclickListener.onYesClick(input);
                }
            }
        });

        mIvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtInput.setText("");
                mIvCancel.setVisibility(View.GONE);
            }
        });

        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String s1 = s.toString();
                if (!TextUtils.isEmpty(s1)) {
                    mIvCancel.setVisibility(View.VISIBLE);
                } else {
                    mIvCancel.setVisibility(View.GONE);
                }
            }
        });
    }


    public interface onYesOnclickListener {
        void onYesClick(String input);
    }

    public interface onNoOnclickListener {
        void onNoClick();
    }

    public void setNoOnclickListener(onNoOnclickListener onNoOnclickListener) {
        this.noOnclickListener = onNoOnclickListener;
    }

    public void setYesOnclickListener(onYesOnclickListener onYesOnclickListener) {
        this.yesOnclickListener = onYesOnclickListener;
    }
}
