package com.chinalooke.yuwan.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.chinalooke.yuwan.R;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AutoLayoutActivity {

    @Bind(R.id.iv_search)
    ImageView mIvSearch;
    @Bind(R.id.tv_search)
    EditText mTvSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.tv_cancel)
    public void onClick() {
        finish();
    }
}
