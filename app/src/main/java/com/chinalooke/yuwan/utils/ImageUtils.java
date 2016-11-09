package com.chinalooke.yuwan.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

/**
 * Created by xiao on 2016/8/23.
 */
public class ImageUtils {

    public static Drawable setDrwableSize(Activity activity, int id,int size) {
        Drawable drawableWeiHui = ContextCompat.getDrawable(activity,id);
        drawableWeiHui.setBounds(0, 5, MyUtils.Dp2Px(activity, size), MyUtils.Dp2Px(activity, size));
        return drawableWeiHui;
    }
}
