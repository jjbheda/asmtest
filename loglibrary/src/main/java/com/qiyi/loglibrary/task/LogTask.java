package com.qiyi.loglibrary.task;

import com.qiyi.loglibrary.LogStorer;
import com.qiyi.loglibrary.strategy.LogLevel;

public class LogTask extends AbstractLogTask {

    public LogTask(int logLevel, String moduleName, String msg) {
        super(logLevel, moduleName, msg);
    }

    public LogTask(int logLevel, String moduleName, Throwable tr) {
       super(logLevel, moduleName, tr);
    }

    @Override
    protected void doInBackground() {
        if (tr == null) {
            if (logLevel == LogLevel.ERROR) {
                LogStorer.RealW(moduleName, msg);
            } else if (logLevel == LogLevel.WARN) {
                LogStorer.RealE(moduleName, msg);
            }


        } else {
            if (logLevel == LogLevel.ERROR) {
                LogStorer.RealE(moduleName, tr);
            } else if (logLevel == LogLevel.WARN) {
                LogStorer.RealW(moduleName, tr);
            }
        }

    }
}
