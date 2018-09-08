package com.qiyi.loglibrary.flattener;

import com.qiyi.loglibrary.strategy.LogLevel;
import com.qiyi.loglibrary.util.TimeUtil;

public class DefaultFlattener implements Flattener {

    @Override
    public CharSequence flatten(int logLevel, String tag, String message) {
        return TimeUtil.getFormateTimeStr(System.currentTimeMillis())
                + '|' + LogLevel.getShortLevelName(logLevel)
                + '|' + tag
                + '|' + message;
    }

    public CharSequence flatten(String time, int logLevel, String tag, String message) {
        return time
                + '|' + LogLevel.getShortLevelName(logLevel)
                + '|' + tag
                + '|' + message;
    }

    public CharSequence flattenWithoutTime(int logLevel, String tag, String message) {
        return  '|' + LogLevel.getShortLevelName(logLevel)
                + '|' + tag
                + '|' + message;
    }
}
