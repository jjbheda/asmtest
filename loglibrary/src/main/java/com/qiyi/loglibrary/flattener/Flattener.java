package com.qiyi.loglibrary.flattener;

public interface Flattener {

    CharSequence flatten(int logLevel, String tag, String message);
}
