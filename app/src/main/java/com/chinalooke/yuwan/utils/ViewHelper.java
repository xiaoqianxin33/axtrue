package com.chinalooke.yuwan.utils;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * @name ViewHelper
 * @description 常用的系统控件操作，与业务相关
 * @author max
 * @date 2012-12-11
 * 
 */
public class ViewHelper {

	/**
	 * 取得屏幕尺寸 add by max [2013-1-21]
	 * 
	 * @param activity
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}
	/**
	 * 取得屏幕尺寸 add by max [2013-5-11]
	 * 
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm;
	}

	/**
	 * 隐藏EditeText键盘 {@code} setOnTouchListener(new OnTouchListener() { public
	 * boolean onTouch(View v, MotionEvent event) { return true;
	 * 
	 * @param et
	 *            EditeText
	 */
	public static void hiddenKeyboard(EditText et, MotionEvent event) {
		int inType = et.getInputType();
		et.setInputType(InputType.TYPE_NULL);
		et.onTouchEvent(event);
		et.setInputType(inType);
		et.setSelection(et.getText().length());
	}

	/**
	 * 打开键盘 add by max [2013-8-11]
	 */
	public static void openKeyboard(final Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	/**
	 * 关闭键盘 add by max [2013-7-2]
	 * 
	 */
	public static void closeKeyboard(Activity activity) {
		InputMethodManager inputManager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 选择日期，为EditeText赋值
	 * 
	 * @param context
	 * @param et
	 *            EditeText
	 */
	public static void showDatePickerDialog(Context context, final EditText et) {
		final Calendar calendar = Calendar.getInstance();
		final int currentYear = calendar.get(Calendar.YEAR);
		final int currentMonth = calendar.get(Calendar.MONTH);
		final int currentDate = calendar.get(Calendar.DATE);
		OnDateSetListener selectedListener = new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
				if (date.after(calendar.getTime())) {
					et.setText(DateUtils.getFormatDateTime(calendar.getTime(),
							"yyyy-MM-dd"));
				} else {
					et.setText(DateUtils.getFormatDateTime(date, "yyyy-MM-dd"));
				}
			}
		};
		/*
		 * 如果未输入日期，则以现在时间为准，否则显示用户已输入的时间
		 */
		if (TextUtils.isEmpty(et.getText())) {
			(new DatePickerDialog(context, selectedListener, currentYear,
					currentMonth, currentDate)).show();
		} else {
			Calendar inputCalendar = DateUtils.getCalendar(et.getText()
					.toString(), "yyyy-MM-dd");
			if (inputCalendar.getTime().after(calendar.getTime())) {
				(new DatePickerDialog(context, selectedListener, currentYear,
						currentMonth, currentDate)).show();
			} else {
				(new DatePickerDialog(context, selectedListener,
						inputCalendar.get(Calendar.YEAR),
						inputCalendar.get(Calendar.MONTH),
						inputCalendar.get(Calendar.DATE))).show();
			}

		}

	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
