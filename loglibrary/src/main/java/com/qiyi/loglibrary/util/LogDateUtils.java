package com.qiyi.loglibrary.util;

import android.util.Log;

import com.qiyi.loglibrary.Constant;
import com.qiyi.loglibrary.menu.DateDirGenerator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by huangxiaolong on 2017/7/17.
 */

public class LogDateUtils {
    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }

    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }

    public static long parseTime(String str, String pattern) throws ParseException {
        Date date = new SimpleDateFormat(pattern).parse(str);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        if (year == 1970) {
            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        }
        return calendar.getTime().getTime();
    }

    public static String formatTime(String timeStamp, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        long time = Long.parseLong(timeStamp);
        return format.format(time);
    }

    public static Date getDate(String dateStr) {
        SimpleDateFormat sdf
                = new SimpleDateFormat(DateDirGenerator.DATE_PATTERN, DateDirGenerator.DATE_LOCALE);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return date;
    }

    /**
     *
     * @param date1  当前日期
     * @param date2  文件夹日期
     * @param intervalDay 默认的超时间隔天数
     * @return
     */

    public static boolean isOverTime(Date date1, Date date2, int intervalDay) {
        final long interval = date1.getTime() - date2.getTime();
        return interval > MILLIS_IN_DAY * intervalDay;

    }


    public static long ParseDate(String fileName , String pattern) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat(pattern).parse(fileName);
            Log.e(Constant.ROOT_TAG, date.toString());

            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            if (year == 1970) {
                calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTime().getTime();
    }

}
