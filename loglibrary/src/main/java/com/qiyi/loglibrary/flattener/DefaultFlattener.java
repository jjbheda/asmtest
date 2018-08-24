package com.qiyi.loglibrary.flattener;

import com.qiyi.loglibrary.strategy.LogLevel;

public class DefaultFlattener implements Flattener {

    @Override
    public CharSequence flatten(int logLevel, String tag, String message) {
        return Long.toString(System.currentTimeMillis())
                + '|' + LogLevel.getShortLevelName(logLevel)
                + '|' + tag
                + '|' + message;
    }
}
