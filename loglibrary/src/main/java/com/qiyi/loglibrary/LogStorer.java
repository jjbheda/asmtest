package com.qiyi.loglibrary;

import android.content.Context;

import com.qiyi.loglibrary.formatter.object.ObjectFormatter;
import com.qiyi.loglibrary.formatter.stacktrace.StackTraceFormatter;
import com.qiyi.loglibrary.formatter.thread.ThreadFormatter;
import com.qiyi.loglibrary.formatter.throwable.ThrowableFormatter;
import com.qiyi.loglibrary.interceptor.Interceptor;
import com.qiyi.loglibrary.strategy.LogLevel;
import com.qiyi.loglibrary.task.LogTaskController;
import com.qiyi.loglibrary.task.LogTask;
import com.qiyi.loglibrary.util.PollingTimerUtil;


public class LogStorer {
    private static boolean sIsInitialized;
    public static LogConfiguration mLogConfiguration;
    private static LogManager mLogManager;

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
        mLogManager = new LogManager(mLogConfiguration);
        mBaseContext = context;

        PollingTimerUtil util = new PollingTimerUtil();
        util.begin();

        controller = new LogTaskController();
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

    public static LogManager.Builder bWithThread() {
        return new LogManager.Builder().bWithThread();
    }

    public static LogManager.Builder bWithNoThread() {
        return new LogManager.Builder().bWithNoThread();
    }

    public static LogManager.Builder bWithStackTrace(int depth) {
        return new LogManager.Builder().bWithStackTrace(depth);
    }

    public static LogManager.Builder bWithNoStackTrace() {
        return new LogManager.Builder().bWithNoStackTrace();
    }

    public static LogManager.Builder throwableFormatter(ThrowableFormatter throwableFormatter) {
        return new LogManager.Builder().throwableFormatter(throwableFormatter);
    }

    public static LogManager.Builder threadFormatter(ThreadFormatter threadFormatter) {
        return new LogManager.Builder().threadFormatter(threadFormatter);
    }

    public static LogManager.Builder stackTraceFormatter(StackTraceFormatter stackTraceFormatter) {
        return new LogManager.Builder().stackTraceFormatter(stackTraceFormatter);
    }

    public static <T> LogManager.Builder addObjectFormatter(Class<T> objectClass,
                                                            ObjectFormatter<? super T> objectFormatter) {
        return new LogManager.Builder().addObjectFormatter(objectClass, objectFormatter);
    }

    public static LogManager.Builder addInterceptor(Interceptor interceptor) {
        return new LogManager.Builder().addInterceptor(interceptor);
    }

    public static void d(String moduleName, String msg) {
        assertInitialized();
        mLogManager.d(moduleName, msg);
    }

    public static void w(String moduleName, String msg) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.WARN, moduleName, msg);
        controller.addLogTask(task);
    }

    public static void w(final String moduleName, final Throwable tr) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.WARN, moduleName, tr);
        controller.addLogTask(task);
    }

    public static void RealW(final String moduleName, String msg) {
        mLogManager.w(moduleName, msg);
    }

    public static void RealW(final String moduleName, final Throwable tr) {
        mLogManager.w(moduleName, tr);
    }

    public static void e(String moduleName, String msg) {
        assertInitialized();
        LogTask task = new LogTask(LogLevel.ERROR, moduleName, msg);
        controller.addLogTask(task);
    }

    public static void RealE(final String moduleName, String msg) {
        mLogManager.e(moduleName, msg);
    }

    public static void RealE(final String moduleName, Throwable tr) {
        mLogManager.e(moduleName, tr);
    }

    public static void e(String moduleName, Throwable tr) {
        assertInitialized();
        mLogManager.e(moduleName, tr);
    }

}

