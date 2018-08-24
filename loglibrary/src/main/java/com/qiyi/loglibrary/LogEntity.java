package com.qiyi.loglibrary;

public class LogEntity {
    public int level;
    public String tag;
    public String msg;
    public String threadInfo;
    public String stackTraceInfo;

    public LogEntity(int level, String tag, String msg) {
        this.level = level;
        this.tag = tag;
        this.msg = msg;
    }

    public LogEntity(int level, String tag, String msg, String threadInfo, String stackTraceInfo) {
        this.level = level;
        this.msg = msg;
        this.msg = msg;
        this.threadInfo = threadInfo;
        this.stackTraceInfo = stackTraceInfo;
    }

}
