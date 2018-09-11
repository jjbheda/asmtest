package com.qiyi.loglibrary;

import android.content.Context;
import android.util.Log;

import com.qiyi.loglibrary.strategy.LogLevel;
import com.qiyi.loglibrary.task.LogTaskController;
import com.qiyi.loglibrary.task.LogTask;
import com.qiyi.loglibrary.util.PollingTimerUtil;
import com.qiyi.loglibrary.util.ThreadUtil;

public class LogStorer {
    private static boolean sIsInitialized;
    public static LogConfiguration mLogConfiguration;

    public static Context mBaseContext;
    private static LogTaskController controller;

    public static boolean checkInitialized() {
        if (!sIsInitialized) {
            Log.d(Constant.ROOT_TAG, "LogStorer is not initialized!!!");
        }
        return sIsInitialized;
    }

    /**
     * 日志线程整体上，应该跑在单独的子线程中
     *
     * @param context
     * @param logConfiguration
     */


    public static void init(Context context, LogConfiguration logConfiguration) {

        if (sIsInitialized) {
            Platform.get().warn("LogStorer is already initialized, do not initialize again");
        }

        sIsInitialized = true;
        if (logConfiguration == null) {
            throw new IllegalArgumentException("Please specify a LogConfiguration");
        }

        mLogConfiguration = logConfiguration;
        mBaseContext = context;

        PollingTimerUtil util = new PollingTimerUtil();
        util.begin();

        controller = new LogTaskController(mLogConfiguration);
        controller.init();

    }

    public static LogManager.Builder logLevel(int logLevel) {
        return new LogManager.Builder().logLevel(logLevel);
    }

    public static LogManager.Builder tag(String tag) {
        return new LogManager.Builder().tag(tag);
    }

    public static LogManager.Builder moduleName(String moduleName) {
        return new LogManager.Builder().moduleName(moduleName);
    }

    public static void v(String moduleName, Throwable tr) {
        if (!checkInitialized()){
            return;
        }
        String threadInfo = ThreadUtil.getThreadInfo(Thread.currentThread());
        LogTask task = new LogTask(LogLevel.VERBOSE, moduleName, "", tr, threadInfo);
        controller.addLogTask(task);
    }

    public static void v(String moduleName, String msg) {
        if(!checkInitialized()){
            return;
        }
        String threadInfo = ThreadUtil.getThreadInfo(Thread.currentThread());
        LogTask task = new LogTask(LogLevel.VERBOSE, moduleName, msg, null, threadInfo);
        controller.addLogTask(task);
    }

    public static void d(String moduleName, Throwable tr) {
        if(!checkInitialized()){
            return;
        }
        String threadInfo = ThreadUtil.getThreadInfo(Thread.currentThread());
        LogTask task = new LogTask(LogLevel.DEBUG, moduleName, "", tr, threadInfo);
        controller.addLogTask(task);
    }

    public static void d(String moduleName, String msg) {
        if(!checkInitialized()){
            return;
        }
        String threadInfo = ThreadUtil.getThreadInfo(Thread.currentThread());
        LogTask task = new LogTask(LogLevel.WARN, moduleName, msg, null, threadInfo);
        controller.addLogTask(task);
    }

    public static void i(String moduleName, Throwable tr) {
        if(!checkInitialized()){
            return;
        }
        String threadInfo = ThreadUtil.getThreadInfo(Thread.currentThread());
        LogTask task = new LogTask(LogLevel.INFO, moduleName, "", tr, threadInfo);
        controller.addLogTask(task);
    }

    public static void i(String moduleName, String msg) {
        if(!checkInitialized()){
            return;
        }
        String threadInfo = ThreadUtil.getThreadInfo(Thread.currentThread());
        LogTask task = new LogTask(LogLevel.INFO, moduleName, msg, null, threadInfo);
        controller.addLogTask(task);
    }

    public static void w(String moduleName, String msg) {
        if(!checkInitialized()){
            return;
        }
        String threadInfo = ThreadUtil.getThreadInfo(Thread.currentThread());
        LogTask task = new LogTask(LogLevel.WARN, moduleName, msg, null, threadInfo);
        controller.addLogTask(task);
    }

    public static void w(String moduleName, Throwable tr) {
        if(!checkInitialized()){
            return;
        }
        String threadInfo = ThreadUtil.getThreadInfo(Thread.currentThread());
        LogTask task = new LogTask(LogLevel.WARN, moduleName, "", tr, threadInfo);
        controller.addLogTask(task);
    }

    public static void e(String moduleName, String msg) {
        if(!checkInitialized()){
            return;
        }
        String threadInfo = ThreadUtil.getThreadInfo(Thread.currentThread());
        LogTask task = new LogTask(LogLevel.ERROR, moduleName, msg, null, threadInfo);
        controller.addLogTask(task);
    }

    public static void e(String moduleName, String msg, Throwable tr) {
        if(!checkInitialized()){
            return;
        }
        String threadInfo = ThreadUtil.getThreadInfo(Thread.currentThread());
        LogTask task = new LogTask(LogLevel.ERROR, moduleName, "", tr, threadInfo);
        controller.addLogTask(task);
    }

    public static void e(String moduleName, Throwable tr) {
        if(!checkInitialized()){
            return;
        }
        String threadInfo = ThreadUtil.getThreadInfo(Thread.currentThread());
        LogTask task = new LogTask(LogLevel.ERROR, moduleName, "", tr, threadInfo);
        controller.addLogTask(task);
    }

    public static void e(Throwable tr) {
        if(!checkInitialized()){
            return;
        }
        String threadInfo = ThreadUtil.getThreadInfo(Thread.currentThread());
        LogTask task = new LogTask(LogLevel.ERROR, Constant.ROOT_TAG, "", tr, threadInfo);
        controller.addLogTask(task);
    }

}

