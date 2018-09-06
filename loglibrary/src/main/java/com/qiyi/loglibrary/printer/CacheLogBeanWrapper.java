package com.qiyi.loglibrary.printer;

import com.qiyi.loglibrary.LogEntity;
import com.qiyi.loglibrary.flattener.Flattener;

public class CacheLogBeanWrapper extends LogEntity{

    private String flatterMsg = "";
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

    public CacheLogBeanWrapper(int level, String tag, String msg, String threadInfo, String stackTraceInfo) {
        super(level, tag, msg, threadInfo, stackTraceInfo);
    }

    public CacheLogBeanWrapper(int level, String tag, String msg, Flattener flattener) {
        super(level, tag, msg);
        this.flattener = flattener;
    }

}
