package com.chinalooke.yuwan.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 我的工具类
 * Created by xiao on 2016/8/23.
 */
public class MyUtils {

    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static float Dp2Px2(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static void showToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    //取得屏幕尺寸
    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static void hiddenKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 动态调整gridview高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 4;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    public static ProgressDialog initDialog(String message, Activity context) {
        ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(true);
        return mProgressDialog;
    }

    public static boolean CheckPhoneNumber(String str)

    {
        Pattern p = Pattern.compile("^1(3|4|5|7|8)\\d{9}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static void showNorDialog(final Activity context, String title, String message, DialogInterface.OnClickListener noClickListener
            , DialogInterface.OnClickListener yesClickListener) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("不了", noClickListener);
        builder.setPositiveButton("好的", yesClickListener);
        builder.show();
    }

    public static void showWodeDialog(final Activity context, String title, String message, DialogInterface.OnClickListener noClickListener
            , DialogInterface.OnClickListener yesClickListener, String no, String yes) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(no, noClickListener);
        builder.setPositiveButton(yes, yesClickListener);
        builder.show();
    }

    public static void showDialog(final Activity context, String title, String message, DialogInterface.OnClickListener yesClickListener) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", yesClickListener);
        builder.show();
    }

    public static void showCustomDialog(final Activity context, String title, String message, String leftMessage, String rightMessage, DialogInterface.OnClickListener noClickListener
            , DialogInterface.OnClickListener yesClickListener) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        if (leftMessage != null && noClickListener != null)
            builder.setNegativeButton(leftMessage, noClickListener);
        builder.setPositiveButton(rightMessage, yesClickListener);
        builder.show();
    }

    public static void showMsg(Toast toast, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String msg = jsonObject.getString("Msg");
            if (!TextUtils.isEmpty(msg)) {
                toast.setText(msg);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
