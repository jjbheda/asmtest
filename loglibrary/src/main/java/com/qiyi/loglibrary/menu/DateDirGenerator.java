package com.qiyi.loglibrary.menu;

import android.os.Environment;

import com.qiyi.loglibrary.Constant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateDirGenerator {

    static ThreadLocal<SimpleDateFormat> mLocalDateFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        }
    };

    public static String generateDateDir(long timestamp) {
        SimpleDateFormat sdf = mLocalDateFormat.get();
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(new Date(timestamp));
    }

}
