package com.chinalooke.yuwan.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * bitmap工具类
 * Created by xiao on 2016/12/3.
 */

public class BitmapUtils {
    public static byte[] toArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}
