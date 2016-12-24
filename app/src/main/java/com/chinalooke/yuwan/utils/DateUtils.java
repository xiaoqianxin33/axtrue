package com.chinalooke.yuwan.utils;

import android.text.TextUtils;
import android.widget.TextView;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author max
 * @name DateUtils
 * @description 日期工具类
 * @date 2012-12-11
 */
public final class DateUtils implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -3098985139095632110L;

    private DateUtils() {
    }

    /**
     * 返回sDate参数指定的时间对象。
     *
     * @param sDate      时间字符串
     * @param dateFormat 指定sDate的时间格式
     * @return
     */
    public static Date getDate(String sDate, String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = calendar.getTime();
        try {
            date = sdf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        calendar.setTime(date);
        return calendar.getTime();
    }

    /**
     * 返回sDate参数指定的时间对象。
     *
     * @param sDate 时间字符串,格式为yyyy-MM-dd
     * @return util.Date对象
     */
    public static Date getDate(String sDate) {
        if (TextUtils.isEmpty(sDate) || sDate.indexOf("0000") > -1) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = calendar.getTime();
        try {
            date = sdf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        return calendar.getTime();
    }

    /**
     * 返回sDate参数指定的日历对象。
     *
     * @param sDate      时间字符串
     * @param dateFormat 指定sDate的时间格式
     * @return
     */
    public static Calendar getCalendar(String sDate, String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = calendar.getTime();
        try {
            date = sdf.parse(sDate);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 取得当前日期的年份，以yyyy格式返回.
     *
     * @return 当年 yyyy
     */
    public static String getCurrentYear() {
        return getFormatCurrentTime("yyyy");
    }

    /**
     * 取得当前日期的月份，以MM格式返回.
     *
     * @return 当前月份 MM
     */
    public static String getCurrentMonth() {
        return getFormatCurrentTime("MM");
    }

    /**
     * 取得当前日期的天数，以格式"dd"返回.
     *
     * @return 当前月中的某天dd
     */
    public static String getCurrentDay() {
        return getFormatCurrentTime("dd");
    }

    /**
     * 返回当前指定的时间戳。格式为yyyy-MM-dd
     *
     * @return 格式为yyyy-MM-dd
     */
    public static String getCurrentDate() {
        return getFormatDateTime(new Date(), "yyyy-MM-dd");
    }

    /**
     * 返回当前指定的时间戳。格式为yyyy-MM-dd HH:mm:ss
     *
     * @return 格式为yyyy-MM-dd HH:mm:ss，总共19位。
     */
    public static String getCurrentDateTime() {
        return getFormatDateTime(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 返回给定时间字符串。
     * <p>
     * 格式：yyyy-MM-dd
     *
     * @param date 日期
     * @return String 指定格式的日期字符串.
     */
    public static String getFormatDate(Date date) {
        return getFormatDateTime(date, "yyyy-MM-dd");
    }

    /**
     * 根据给定的格式，返回时间字符串。
     * <p>
     * 格式参照类描绘中说明.和方法getFormatDate是一样的。
     *
     * @param format 日期格式字符串
     * @return String 指定格式的日期字符串.
     */
    public static String getFormatCurrentTime(String format) {
        return getFormatDateTime(new Date(System.currentTimeMillis()), format);
    }

    /**
     * 返回当前时间字符串。
     * <p>
     * 格式：yyyy-MM-dd HH:mm:ss
     *
     * @return String 指定格式的日期字符串.
     */
    public static String getCurrentTime() {
        return getFormatDateTime(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 返回给定时间字符串。
     * <p>
     * 格式：yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期
     * @return String 指定格式的日期字符串.
     */
    public static String getFormatTime(Date date) {
        return getFormatDateTime(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 返回给短时间字符串。
     * <p>
     * 格式：yyyy-MM-dd
     *
     * @param date 日期
     * @return String 指定格式的日期字符串.
     */
    public static String getFormatShortTime(Date date) {
        return getFormatDateTime(date, "yyyy-MM-dd");
    }

    /**
     * 根据给定的格式与时间(Date类型的)，返回时间字符串。最为通用。<br>
     *
     * @param date   指定的日期
     * @param format 日期格式字符串
     * @return String 指定格式的日期字符串.
     */
    public static String getFormatDateTime(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 取得指定年月日的日期对象.
     *
     * @param year  年
     * @param month 月注意是从1到12
     * @param day   日
     * @return 一个java.util.Date()类型的对象
     */
    public static Date getDateObj(int year, int month, int day) {
        Calendar c = new GregorianCalendar();
        c.set(year, month - 1, day);
        return c.getTime();
    }

    /**
     * 取得当前Date对象.
     *
     * @return Date 当前Date对象.
     */
    public static Date getDateObj() {
        Calendar c = new GregorianCalendar();
        return c.getTime();
    }

    /**
     * 根据指定的年月日小时分秒，返回一个java.Util.Date对象。
     *
     * @param year      年
     * @param month     月 0-11
     * @param date      日
     * @param hourOfDay 小时 0-23
     * @param minute    分 0-59
     * @param second    秒 0-59
     * @return 一个Date对象。
     */
    public static Date getDate(int year, int month, int date, int hourOfDay,
                               int minute, int second) {
        Calendar cal = new GregorianCalendar();
        cal.set(year, month, date, hourOfDay, minute, second);
        return cal.getTime();
    }

    /**
     * 根据指定的年、月、日返回当前是星期几。1表示星期天、2表示星期一、7表示星期六。
     *
     * @param year
     * @param month month是从1开始的12结束
     * @param day
     * @return 返回一个代表当期日期是星期几的数字。1表示星期天、2表示星期一、7表示星期六。
     */
    public static int getDayOfWeek(String year, String month, String day) {
        Calendar cal = new GregorianCalendar(new Integer(year).intValue(),
                new Integer(month).intValue() - 1, new Integer(day).intValue());
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 根据指定的年、月、日返回当前是星期几。1表示星期天、2表示星期一、7表示星期六。
     *
     * @param date "yyyy/MM/dd",或者"yyyy-MM-dd"
     * @return 返回一个代表当期日期是星期几的数字。1表示星期天、2表示星期一、7表示星期六。
     */
    public static int getDayOfWeek(String date) {
        String[] temp = null;
        if (date.indexOf("/") > 0) {
            temp = date.split("/");
        }
        if (date.indexOf("-") > 0) {
            temp = date.split("-");
        }
        return getDayOfWeek(temp[0], temp[1], temp[2]);
    }

    /**
     * 返回当前日期是星期几。例如：星期日、星期一、星期六等等。
     *
     * @param date 格式为 yyyy/MM/dd 或者 yyyy-MM-dd
     * @return 返回当前日期是星期几
     */
    public static String getChinaDayOfWeek(String date) {
        String[] weeks = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
                "星期六"};
        int week = getDayOfWeek(date);
        return weeks[week - 1];
    }

    /**
     * 根据指定的年、月、日返回当前是星期几。1表示星期天、2表示星期一、7表示星期六。
     *
     * @param date
     * @return 返回一个代表当期日期是星期几的数字。1表示星期天、2表示星期一、7表示星期六。
     */
    public static int getDayOfWeek(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 返回制定日期所在的周是一年中的第几个周。<br>
     * created by wangmj at 20060324.<br>
     *
     * @param year
     * @param month 范围1-12<br>
     * @param day
     * @return int
     */
    public static int getWeekOfYear(String year, String month, String day) {
        Calendar cal = new GregorianCalendar();
        cal.clear();
        cal.set(new Integer(year).intValue(),
                new Integer(month).intValue() - 1, new Integer(day).intValue());
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 取得给定日期加上一定天数后的日期对象.
     *
     * @param date   给定的日期对象
     * @param amount 需要添加的天数，如果是向前的天数，使用负数就可以.
     * @return Date 加上一定天数以后的Date对象.
     */
    public static Date getDateAdd(Date date, int amount) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(GregorianCalendar.DATE, amount);
        return cal.getTime();
    }

    /**
     * 取得给定日期加上一定天数后的日期对象.
     *
     * @param date   给定的日期对象
     * @param amount 需要添加的天数，如果是向前的天数，使用负数就可以.
     * @param format 输出格式.
     * @return Date 加上一定天数以后的Date对象.
     */
    public static String getFormatDateAdd(Date date, int amount, String format) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(GregorianCalendar.DATE, amount);
        return getFormatDateTime(cal.getTime(), format);
    }

    /**
     * 获得当前日期固定间隔天数的日期，如前60天dateAdd(-60)
     *
     * @param amount 距今天的间隔日期长度，向前为负，向后为正
     * @param format 输出日期的格式.
     * @return java.lang.String 按照格式输出的间隔的日期字符串.
     */
    public static String getFormatCurrentAdd(int amount, String format) {

        Date d = getDateAdd(new Date(), amount);

        return getFormatDateTime(d, format);
    }

    /**
     * 获取两个时间串时间的差值，单位为秒
     *
     * @param startTime 开始时间 yyyy-MM-dd HH:mm:ss
     * @param endTime   结束时间 yyyy-MM-dd HH:mm:ss
     * @return 两个时间的差值(秒)
     */
    public static long getDiff(String startTime, String endTime) {
        long diff = 0;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startDate = ft.parse(startTime);
            Date endDate = ft.parse(endTime);
            diff = startDate.getTime() - endDate.getTime();
            diff = diff / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    /**
     * 获取小时/分钟/秒
     *
     * @param second 秒
     * @return 包含小时、分钟、秒的时间字符串，例如3小时23分钟13秒。
     */
    public static String getHour(long second) {
        long hour = second / 60 / 60;
        long minute = (second - hour * 60 * 60) / 60;
        long sec = (second - hour * 60 * 60) - minute * 60;

        return hour + "Сʱ" + minute + "����" + sec + "��";

    }

    /**
     * 返回指定时间字符串。
     * <p>
     * 格式：yyyy-MM-dd HH:mm:ss
     *
     * @return String 指定格式的日期字符串.
     */
    public static String getDateTime(long microsecond) {
        return getFormatDateTime(new Date(microsecond), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 返回当前时间加实数小时后的日期时间。
     * <p>
     * 格式：yyyy-MM-dd HH:mm:ss
     *
     * @return Float 加几实数小时.
     */
    public static String getDateByAddFltHour(float flt) {
        int addMinute = (int) (flt * 60);
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(GregorianCalendar.MINUTE, addMinute);
        return getFormatDateTime(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 返回指定时间加指定小时数后的日期时间。
     * <p>
     * 格式：yyyy-MM-dd HH:mm:ss
     *
     * @return 时间.
     */
    public static String getDateByAddHour(String datetime, int minute) {
        String returnTime = null;
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = ft.parse(datetime);
            cal.setTime(date);
            cal.add(GregorianCalendar.MINUTE, minute);
            returnTime = getFormatDateTime(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnTime;

    }

    /**
     * 获取两个时间串时间的差值，单位为小时
     *
     * @param startTime 开始时间 yyyy-MM-dd HH:mm:ss
     * @param endTime   结束时间 yyyy-MM-dd HH:mm:ss
     * @return 两个时间的差值(小时)
     */
    public static int getDiffHour(String startTime, String endTime) {
        long diff = 0;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startDate = ft.parse(startTime);
            Date endDate = ft.parse(endTime);
            diff = startDate.getTime() - endDate.getTime();
            diff = diff / (1000 * 60 * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Long(diff).intValue();
    }

    /**
     * 返回两个日期之间相差多少天。 注意开始日期和结束日期要统一起来。
     *
     * @param startDate 格式"yyyy/MM/dd" 或者"yyyy-MM-dd"
     * @param endDate   格式"yyyy/MM/dd" 或者"yyyy-MM-dd"
     * @return 整数。
     */
    public static int getDiffDays(String startDate, String endDate) {
        long diff = 0;
        SimpleDateFormat ft = null;
        if (startDate.indexOf("/") > 0 && endDate.indexOf("/") > 0) {
            ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        }
        if (startDate.indexOf("-") > 0 && endDate.indexOf("-") > 0) {
            ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        try {
            Date sDate = ft.parse(startDate + " 00:00:00");
            Date eDate = ft.parse(endDate + " 00:00:00");
            diff = eDate.getTime() - sDate.getTime();
            diff = diff / 86400000;// 1000*60*60*24;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) diff;

    }

    /**
     * 判断一个日期是否在开始日期和结束日期之间。
     *
     * @param srcDate   目标日期 yyyy/MM/dd 或者 yyyy-MM-dd
     * @param startDate 开始日期 yyyy/MM/dd 或者 yyyy-MM-dd
     * @param endDate   结束日期 yyyy/MM/dd 或者 yyyy-MM-dd
     * @return 大于等于开始日期小于等于结束日期，那么返回true，否则返回false
     */
    public static boolean isInStartEnd(String srcDate, String startDate,
                                       String endDate) {
        return startDate.compareTo(srcDate) <= 0
                && endDate.compareTo(srcDate) >= 0;
    }

    //获取当天的日期
    public static Date getCurrentDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 0);
        date = calendar.getTime();
        return date;
    }


    //获取明天的日期
    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        return date;
    }

    //获取昨天的日期
    public static Date getBeforeDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        return date;
    }

    //获取后天的日期
    public static Date getNextNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        date = calendar.getTime();
        return date;
    }

    public static void setDynamicTime(String addTime, TextView textView) {
        textView.setText(addTime);
        Calendar cal = new GregorianCalendar();
        Date date = DateUtils.getDate(addTime, "yyyy-MM-dd HH:mm:ss");
        cal.setTime(date);
        int dateY = cal.get(Calendar.YEAR);
        int dateM = cal.get(Calendar.MONTH);
        int dateD = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTime(new Date());
        int cdateY = cal.get(Calendar.YEAR);
        int cdateM = cal.get(Calendar.MONTH);
        int cdateD = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTime(DateUtils.getBeforeDay(new Date()));
        int bdateD = cal.get(Calendar.DAY_OF_YEAR);
        if (dateM == cdateM && dateY == cdateY) {
            if (dateD == cdateD) {
                textView.setText("今天 " + addTime.substring(10));
            } else if (dateD == bdateD) {
                textView.setText("昨天 " + addTime.substring(10));
            }
        }
        long l = new Date().getTime() - date.getTime();
        l = l / 1000;
        if (l <= 60) {
            textView.setText("刚刚");
        } else if (l < 120 && l > 60) {
            textView.setText("1分钟之前");
        }
    }

}