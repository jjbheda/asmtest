package com.qiyi.loglibrary;

import android.content.Context;
import com.qiyi.loglibrary.strategy.LogLevel;
import com.qiyi.loglibrary.task.LogTaskController;
import com.qiyi.loglibrary.task.LogTask;
import com.qiyi.loglibrary.util.PollingTimerUtil;

public class LogStorer {
    private static boolean sIsInitialized;
    public static LogConfiguration mLogConfiguration;

    public static Context mBaseContext;
    private static LogTaskController controller;

    public static void assertInitialized() {
        if (!sIsInitialized) {
            throw new IllegalStateException("LogStorer has not be initialized!!!");
        }
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
        assertInitialized();
        LogTask task = new LogTask(LogLevel.VERBOSE, moduleName, "", tr);
        controller.addLogTask(task);
    }

    public static void v(String moduleName, String msg) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.VERBOSE, moduleName, msg, null);
        controller.addLogTask(task);
    }

    public static void d(String moduleName, Throwable tr) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.DEBUG, moduleName, "", tr);
        controller.addLogTask(task);
    }

    public static void d(String moduleName, String msg) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.WARN, moduleName, msg, null);
        controller.addLogTask(task);
    }

    public static void i(String moduleName, Throwable tr) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.INFO, moduleName, "", tr);
        controller.addLogTask(task);
    }

    public static void i(String moduleName, String msg) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.INFO, moduleName, msg, null);
        controller.addLogTask(task);
    }

    public static void w(String moduleName, String msg) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.WARN, moduleName, msg, null);
        controller.addLogTask(task);
    }

    public static void w(String moduleName, Throwable tr) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.WARN, moduleName, "", tr);
        controller.addLogTask(task);
    }

    public static void e(String moduleName, String msg) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.ERROR, moduleName, msg, null);
        controller.addLogTask(task);
    }

    public static void e(String moduleName, String msg, Throwable tr) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.ERROR, moduleName, "", tr);
        controller.addLogTask(task);
    }


    public static void e(String moduleName, Throwable tr) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.ERROR, moduleName, "", tr);
        controller.addLogTask(task);
    }

}

