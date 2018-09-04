package com.qiyi.loglibrary.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {

    static ThreadLocal<SimpleDateFormat> mLocalTimeFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS ", Locale.US);
        }
    };

    public static String getFormateTimeStr(long timestamp) {
        SimpleDateFormat sdf = mLocalTimeFormat.get();
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(new Date(timestamp));
    }


}
