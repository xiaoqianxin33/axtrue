package com.chinalooke.yuwan.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.chinalooke.yuwan.R;
import com.chinalooke.yuwan.constant.Constant;
import com.chinalooke.yuwan.model.UserInfo;
import com.chinalooke.yuwan.utils.LoginUserInfoUtils;
import com.chinalooke.yuwan.view.CircleImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WoDeZiLiaoActivity extends AppCompatActivity {
    //返回
    @Bind(R.id.back_wodeziliao)
    ImageView backPersonalInfo;
    //圆形头像
    @Bind(R.id.toxiang_wodeziliao)
    CircleImageView mCircleImageView;
    //姓名
    @Bind(R.id.name_wodeziliao)
    TextView nameWodeziliao;
    //性别
    @Bind(R.id.sex_wodeziliao)
    TextView sexWodeziliao;
    //年龄
    @Bind(R.id.age_wodeziliao)
    TextView ageWodeziliao;
    //玩龄
    @Bind(R.id.playage_personal_info)
    TextView playagePersonalInfo;
    //地址
    @Bind(R.id.address_wodeziliao)
    TextView addressWodeziliao;
    //身份证号码
    @Bind(R.id.cardid_wodeziliao)
    TextView cardidWodeziliao;
    //二维码
    @Bind(R.id.erweima_wodeziliao)
    ImageView erweimaWodeziliao;
    //个人说明
    @Bind(R.id.gerenshuoming_wodezilaio)
    TextView gerenshuomingWodezilaio;
    //修改
    @Bind(R.id.edit_wodeziliao)
    TextView editWodeziliao;

    //用户登录信息
    UserInfo userInfo;
    RequestQueue mQueue;
    //图片加载
    ImageLoader imageLoader;
    String qrString;
    PopupWindow popupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_de_zi_liao);
        ButterKnife.bind(this);
        mQueue = Volley.newRequestQueue(this);
        //初始化
        initDatas();
    }

    //数据初始化
    private void initDatas() {
        userInfo = LoginUserInfoUtils.getLoginUserInfoUtils().getUserInfo();
        if (userInfo != null) {
            imageLoader = new ImageLoader(mQueue, new BitmapCache());
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(mCircleImageView, R.mipmap.yw80_orange, R.mipmap.yw80_orange);
            imageLoader.get(userInfo.getHeadImg(), listener);
            nameWodeziliao.setText(userInfo.getNickName());
            sexWodeziliao.setText(userInfo.getSex());
            gerenshuomingWodezilaio.setText(userInfo.getSlogan());
            playagePersonalInfo.setText(userInfo.getPlayAge());
            addressWodeziliao.setText(userInfo.getAddress());
            cardidWodeziliao.setText(userInfo.getCardNo());
            //  Log.d("TAG","--CardNo1111----"+userInfo.getCardNo());

            /*年龄*/
/*            String CardNo=userInfo.getCardNo();
            Log.d("TAG","--CardNo----"+CardNo);
            String age=CardNo.substring(6,10);
            Log.d("TAG","--age----"+age);
            Log.d("TAG","---getAge---"+userInfo.getAge());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Date date = new Date();
            String formatDate = sdf.format(date);
            System.out.println("格式化后的年份为:" + formatDate);
            ageWodeziliao.setText((Integer.parseInt(formatDate)-Integer.parseInt(age))+"");*/
            ageWodeziliao.setText(userInfo.getAge());
              /*  二维码
              * userId+ 姓名+玩龄+年龄+性别+住址+身份证号拼出来的字符串
              * */
            qrString = Constant.GET_QRCODE_WITH_DATA + userInfo.getUserId() + userInfo.getRealName() + userInfo.getPlayAge() +
                    userInfo.getAge() + userInfo.getSex() + userInfo.getAddress() + userInfo.getCardNo();
            Log.d("TAG", "--qrString----" + qrString);

            ImageLoader.ImageListener listenerQR = ImageLoader.getImageListener(erweimaWodeziliao, R.mipmap.yw80_orange, R.mipmap.yw80_orange);
            imageLoader.get(qrString, listenerQR);

        }

    }


    public class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }

    @OnClick({R.id.back_wodeziliao, R.id.toxiang_wodeziliao, R.id.erweima_wodeziliao, R.id.edit_wodeziliao})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_wodeziliao:
                finish();
                break;
            case R.id.edit_wodeziliao:
                Intent intent = new Intent(this, PersonalInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.toxiang_wodeziliao:
                break;
            case R.id.erweima_wodeziliao:
                View view2 = LayoutInflater.from(this).inflate(R.layout.qrcode_dialog, null);
                ImageView qrCodeDialog = (ImageView) view2.findViewById(R.id.qrCode_dialog);
                ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(qrCodeDialog, R.mipmap.yw80_orange, R.mipmap.yw80_orange);
                imageLoader.get(qrString, imageListener);
                initPopupWindow(view2);

                popupWindow.showAtLocation(erweimaWodeziliao, Gravity.BOTTOM, 0, 0);
                break;
        }
    }

    private void initPopupWindow(View view) {
        // 创建一个popupWindow

        popupWindow = new PopupWindow(view, LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT);
        // 设置事件处理
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置动画效果
        //popupWindow.setAnimationStyle(R.style.popupWindowAnimation);

    }

}
