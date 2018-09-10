package com.qiyi.loglibrary.task;

import com.qiyi.loglibrary.LogStorer;
import com.qiyi.loglibrary.strategy.LogLevel;

public class LogTask extends AbstractLogTask {
    //msg 初始化时，做null 保护
    public LogTask(int logLevel, String moduleName,  String msg, Throwable tr, String thradInfo) {
        super(logLevel, moduleName, msg, tr, thradInfo);
    }

    @Override
    protected void doInBackground() {

        switch (logLevel) {
            case LogLevel.VERBOSE:
                LogTaskController.v(moduleName, msg, tr, threadInfo);
                break;
            case LogLevel.DEBUG:
                LogTaskController.d(moduleName, msg, tr, threadInfo);
                break;
            case LogLevel.INFO:
                LogTaskController.i(moduleName, msg, tr, threadInfo);
                break;
            case LogLevel.WARN:
                LogTaskController.w(moduleName, msg, tr, threadInfo);
                break;
            case LogLevel.ERROR:
                LogTaskController.e(moduleName, msg, tr, threadInfo);
                break;
            default:
                LogTaskController.v(moduleName, msg, tr, threadInfo);
                break;
        }

    }
}
