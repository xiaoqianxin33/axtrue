package com.chinalooke.yuwan.engine;

import android.content.Context;
import android.graphics.Bitmap;

import com.chinalooke.yuwan.utils.ImageUtils;
import com.chinalooke.yuwan.utils.NetworkUtils;
import com.chinalooke.yuwan.utils.PreferenceUtils;

/**
 * 图片上传下载引擎
 * Created by xiao on 2016/12/24.
 */

public class ImageEngine {

    /**
     * 下载图片质量控制
     *
     * @param url    图片原始url
     * @param width  控件宽度
     * @param height 控件高度
     * @return 根据设置判断之后的url
     */
    public static String getLoadImageUrl(Context context, String url, int width, int height) {
        String loadUrl = null;
        int load_image = PreferenceUtils.getPrefInt(context, "load_image", 1);
        switch (load_image) {
            case 1:
                NetworkUtils.NetworkType networkType = NetworkUtils.getNetworkType();
                if (networkType == NetworkUtils.NetworkType.NETWORK_WIFI) {
                    loadUrl = url + "?imageView2/1/w/" + width + "/h/" + height;
                } else {
                    loadUrl = url + "?imageView2/1/w/" + width / 2 + "/h/" + height / 2;
                }
                break;
            case 2:
                loadUrl = url + "?imageView2/1/w/" + width + "/h/" + height;
                break;
            case 3:
                loadUrl = url + "?imageView2/1/w/" + width / 2 + "/h/" + height / 2;
                break;
        }
        return loadUrl;
    }

    /**
     * 压缩上传图片
     *
     * @param bitmap 原始bitmap
     * @return 压缩后的Bitmap
     */
    public static Bitmap getCompressBitmap(Bitmap bitmap, Context context) {
        boolean wifi_image = PreferenceUtils.getPrefBoolean(context, "wifi_image", false);
        boolean auto_image = PreferenceUtils.getPrefBoolean(context, "auto_image", true);
        if (wifi_image) {
            NetworkUtils.NetworkType networkType = NetworkUtils.getNetworkType();
            if (networkType != NetworkUtils.NetworkType.NETWORK_WIFI) {
                return null;
            } else {
                if (auto_image) {
                    return ImageUtils.compressByScale(bitmap, 0.5f, 0.5f);
                } else {
                    return bitmap;
                }
            }
        } else {
            if (auto_image) {
                return ImageUtils.compressByScale(bitmap, 0.5f, 0.5f);
            } else {
                return bitmap;
            }
        }
    }

}
