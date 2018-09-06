package com.qiyi.loglibrary;

public class LogEntity {
    public int level;
    public String moduleName;
    public String msg;
    public String threadInfo;
    public String stackTraceInfo;
    public String time;


    public LogEntity(int level, String moduleName, String msg, String threadInfo, String stackTraceInfo) {
        this.level = level;
        this.moduleName = moduleName;
        this.msg = msg;
        this.threadInfo = threadInfo;
        this.stackTraceInfo = stackTraceInfo;
    }

    public LogEntity(int level, String moduleName, String msg, String threadInfo) {
        this.level = level;
        this.moduleName = moduleName;
        this.msg = msg;
        this.threadInfo = threadInfo;
    }

    public LogEntity(int level, String moduleName, String msg) {
        this.level = level;
        this.moduleName = moduleName;
        this.msg = msg;

    }

}
