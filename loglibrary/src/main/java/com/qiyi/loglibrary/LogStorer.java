package com.qiyi.loglibrary;

import android.content.Context;

import com.qiyi.loglibrary.formatter.object.ObjectFormatter;
import com.qiyi.loglibrary.formatter.stacktrace.StackTraceFormatter;
import com.qiyi.loglibrary.formatter.thread.ThreadFormatter;
import com.qiyi.loglibrary.formatter.throwable.ThrowableFormatter;
import com.qiyi.loglibrary.interceptor.Interceptor;
import com.qiyi.loglibrary.printer.Printer;
import com.qiyi.loglibrary.printer.PrinterSet;
import com.qiyi.loglibrary.strategy.LogLevel;
import com.qiyi.loglibrary.util.DefaultsFactory;
import com.qiyi.loglibrary.util.PollingTimerUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.CopyOnWriteArrayList;


public class LogStorer {
   private static boolean sIsInitialized;
   public static LogConfiguration mLogConfiguration;
   private static LogManager mLogManager;

   public static Context mBaseContext;

    public static void assertInitialized() {
        if (!sIsInitialized) {
            throw  new IllegalStateException("LogStorer has not be initialized!!!");
        }
    }

//    public static void init(Context context, LogConfiguration logConfiguration) {
//        init(context, logConfiguration);
//    }

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
    }

//    public static void init(Context context, int logLevel, LogConfiguration logConfiguration) {
//        init(context, new LogConfiguration.Builder(logConfiguration).logLevel(logLevel).build());
//    }

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

    public static LogManager.Builder printers(Printer... printers) {
        return new LogManager.Builder().printers(printers);
    }

//    public static void v(Object o) {
//        assertInitialized();
//        mLogManager.v(o);
//    }

    public static void v(Object[] array) {
        assertInitialized();
        mLogManager.v(array);
    }

//    public static void v(String format, Object... args) {
//        assertInitialized();
//        mLogManager.v(format, args);
//    }

//    public static void v(String msg) {
//        assertInitialized();
//        mLogManager.v(msg);
//    }
//
//    public static void v(String msg, Throwable tr) {
//        assertInitialized();
//        mLogManager.v(msg, tr);
//    }
//
//    public static void d(Object o) {
//        assertInitialized();
//        mLogManager.d(o);
//    }

    public static void d(Object[] array) {
        assertInitialized();
        mLogManager.d(array);
    }

//    public static void d(String format, Object... args) {
//        assertInitialized();
//        mLogManager.d(format, args);
//    }
//
//    public static void d(String msg) {
//        assertInitialized();
//        mLogManager.d(msg);
//    }

//    public static void d(String msg, Throwable tr) {
//        assertInitialized();
//        mLogManager.d(msg, tr);
//    }

    public static void d(String moduleName, String msg) {
        assertInitialized();
        mLogManager.d(moduleName, msg);
    }

//    public static void i(Object o) {
//        assertInitialized();
//        mLogManager.i(o);
//    }

//    public static void i(Object[] array) {
//        assertInitialized();
//        mLogManager.i(array);
//    }

//    public static void i(String format, Object... args) {
//        assertInitialized();
//        mLogManager.i(format, args);
//    }

//    public static void i(String msg) {
//        assertInitialized();
//        mLogManager.i(msg);
//    }

//    public static void i(String msg, Throwable tr) {
//        assertInitialized();
//        mLogManager.i(msg, tr);
//    }
//
//    public static void w(Object o) {
//        assertInitialized();
//        mLogManager.w(o);
//    }

    public static void w(Object[] array) {
        assertInitialized();
        mLogManager.w(array);
    }

//    public static void w(String format, Object... args) {
//        assertInitialized();
//        mLogManager.w(format, args);
//    }

//    public static void w(String msg) {
//        assertInitialized();
//        mLogManager.w(msg);
//    }

    public static void w(String moduleName, String msg) {
        assertInitialized();
        mLogManager.w(moduleName, msg);
    }

    public static void w(String moduleName, Throwable tr) {
        assertInitialized();
        mLogManager.w(moduleName, tr);
    }

//    public static void e(Object o) {
////        assertInitialized();
////        mLogManager.e(o);
////    }

    public static void e(Object[] array) {
        assertInitialized();
        mLogManager.e(array);
    }

//    public static void e(String format, Object... args) {
//        assertInitialized();
//        mLogManager.e(format, args);
//    }

        public static void e(String moduleName,String msg) {
        assertInitialized();
        mLogManager.e(moduleName, msg);
    }


//    public static void e(String msg) {
//        assertInitialized();
//        mLogManager.e(msg);
//    }

//    public static void e(String msg, Throwable tr) {
//        assertInitialized();
//        mLogManager.e(msg, tr);
//    }

    public static void log(int logLevel, Object[] array) {
        assertInitialized();
        mLogManager.log(logLevel, array);
    }

//    public static void log(int logLevel, String format, Object... args) {
//        assertInitialized();
//        mLogManager.log(logLevel, format, args);
//    }

//    public static void log(int logLevel, String msg) {
//        assertInitialized();
//        mLogManager.log(logLevel, msg);
//    }

}
