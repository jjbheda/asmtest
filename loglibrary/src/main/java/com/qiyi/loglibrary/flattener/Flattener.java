package com.qiyi.loglibrary.flattener;

public interface Flattener {
    CharSequence flatten(int logLevel, String tag, String message);
    CharSequence flattenWithoutTime(int logLevel, String tag, String message);
    CharSequence flatten(String time, int logLevel, String filePath, String message);

}
