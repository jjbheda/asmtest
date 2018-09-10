package com.qiyi.loglibrary.printer;

import com.qiyi.loglibrary.LogEntity;
import com.qiyi.loglibrary.flattener.Flattener;

import java.util.concurrent.CountDownLatch;

public class LogBeanWrapper extends LogEntity {

    private String time;
    private Flattener flattener;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFlatterMsg() {
        return flattener.flatten(time, level, moduleName, msg).toString() + "\n";
    }

    public String getCheckFlatterMsg() {
        return flattener.flattenWithoutTime(level, moduleName, msg).toString() + "\n";
    }

    public LogBeanWrapper(int level, String tag, String msg, String threadInfo, String stackTraceInfo) {
        super(level, tag, msg, threadInfo, stackTraceInfo);
    }

    public LogBeanWrapper(int level, String tag, String msg, boolean isThrowable, Flattener flattener) {
        super(level, tag, msg, isThrowable);
        this.flattener = flattener;
    }

}
