package com.chinalooke.yuwan.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinalooke.yuwan.R;

/**
 * Created by Administrator on 2016/8/28.
 */
public class LoadingProgressDialogView extends ProgressDialog{
    private AnimationDrawable mAnimation;
    private ImageView mImageView;
    private TextView mTextView;
    private String loadingTip;
    private int resid;
    private Animation operatingAnim;
    Context context;
    /**
     *
     * @param context 上下文对象
     * @param content 显示文字提示信息内容
     * @paramid 动画id
     */
    public LoadingProgressDialogView(Context context, String content, int resid) {
        super(context);
        this.context=context;
        this.loadingTip = content;
        this.resid = resid;
        //点击提示框外面是否取消提示框
        setCanceledOnTouchOutside(false);
        //点击返回键是否取消提示框
        setCancelable(false);
        setIndeterminate(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
        mTextView = (TextView)findViewById(R.id.loadingTv);
        mImageView = (ImageView) findViewById(R.id.loadingIv);
       // mImageView.setBackgroundResource(resid);
        // 通过ImageView对象拿到背景显示的AnimationDrawable
         operatingAnim = AnimationUtils.loadAnimation(context,resid);//加载动画
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        mImageView.setAnimation(operatingAnim);
        /*mAnimation = (AnimationDrawable) mImageView.getBackground();*/
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                operatingAnim.start();
               /* mAnimation.start();*/
            }
        });
        mTextView.setText(loadingTip);
    }
}
