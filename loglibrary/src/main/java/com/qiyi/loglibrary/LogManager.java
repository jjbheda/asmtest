package com.qiyi.loglibrary;

import android.util.Log;
import com.qiyi.loglibrary.flattener.DefaultFlattener;
import com.qiyi.loglibrary.interceptor.Interceptor;
import com.qiyi.loglibrary.printer.AndroidPrinter;
import com.qiyi.loglibrary.printer.FilePrinter;
import com.qiyi.loglibrary.strategy.LogLevel;
import com.qiyi.loglibrary.util.StackTraceUtil;
import com.qiyi.loglibrary.util.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * A LogManager is used to do the real logging work, can use multiple log printers to print the log.
 */

public class LogManager {

    private LogConfiguration logConfiguration;
    private AndroidPrinter androidPrinter;
    private FilePrinter filePrinter;

    public LogManager(LogConfiguration logConfiguration) {
        this.logConfiguration = logConfiguration;
        androidPrinter = new AndroidPrinter();
        EventBus.getDefault().register(this);
        filePrinter = new FilePrinter.Builder().logFlattener(new DefaultFlattener()).build();
    }

    public LogManager(Builder builder) {
        LogConfiguration.Builder logConfigBuilder = new LogConfiguration.
                Builder(LogStorer.mLogConfiguration);

        if (builder.logLevel != 0) {
            logConfigBuilder.logLevel(builder.logLevel);
        }

        if (builder.tag != null) {
            logConfigBuilder.tag(builder.tag);
        }

        if (builder.moduleName != null) {
            logConfigBuilder.moduleName(builder.moduleName);
        }

        logConfiguration = logConfigBuilder.build();

        androidPrinter = new AndroidPrinter();
        filePrinter = new FilePrinter.Builder().logFlattener(new DefaultFlattener()).build();
    }

    public void v(String moduleName, Throwable tr, String threadIfo) {
        println(LogLevel.VERBOSE, moduleName, tr, threadIfo);
    }

    public void v(String moduleName, String msg, String threadIfo) {
        println(LogLevel.VERBOSE, moduleName, msg, threadIfo);
    }

    public void d(String moduleName, Throwable tr, String threadIfo) {
        println(LogLevel.DEBUG, moduleName, tr, threadIfo);
    }

    public void d(String moduleName, String msg, String threadIfo) {
        println(LogLevel.DEBUG, moduleName, msg, threadIfo);
    }

    public void i(String moduleName, Throwable tr, String threadIfo) {
        println(LogLevel.INFO, moduleName, tr, threadIfo);
    }

    public void i(String moduleName, String msg, String threadIfo) {
        println(LogLevel.INFO, moduleName, msg, threadIfo);
    }

    public void w(String moduleName, Throwable tr, String threadIfo) {
        println(LogLevel.WARN, moduleName, tr, threadIfo);
    }

    public void w(String moduleName, String msg, String threadIfo) {
        println(LogLevel.WARN, moduleName, msg, threadIfo);
    }

    public void e(String moduleName, Throwable tr, String threadIfo) {
        println(LogLevel.ERROR, moduleName, tr, threadIfo);
    }
    public void e(String moduleName, String msg, String threadInfo) {
        println(LogLevel.ERROR, moduleName, msg, threadInfo);
    }

    public void log(String moduleName, int logLevel, String msg, Throwable tr, String threadInfo) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternalWithThrowble(moduleName, logLevel, msg, tr, threadInfo);
    }

    private void println(int logLevel, String moduleName, Throwable e, String threadInfo) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternal(logLevel,moduleName, e, threadInfo);
    }

    private void println(int logLevel, String moduleName, String msg, String threadInfo) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternal(logLevel,moduleName, msg, threadInfo);
    }

    private void printlnInternalWithThrowble(String moduleName, int logLevel, String msg, Throwable tr, String threadInfo) {
        String stackTrace = logConfiguration.withExceptionStackTrace
                ? logConfiguration.stackTraceFormatter.format(
                StackTraceUtil.getCroppedRealStackTrack(tr.getStackTrace(),
                        logConfiguration.stackTraceOrigin, logConfiguration.stackTraceDepth)) : null;

        if (logConfiguration.interceptors != null) {
            LogEntity log = new LogEntity(logLevel, moduleName, threadInfo, stackTrace, msg);
            for (Interceptor interceptor : logConfiguration.interceptors) {
                log = interceptor.intercept(log);
                if (log == null) {
                    return;
                }

                // Check if the log still healthy.
                if (log.moduleName == null || log.msg == null) {
                    throw new IllegalStateException("Interceptor " + interceptor
                            + " should not remove the tag or message of a log,"
                            + " if you don't want to print this log,"
                            + " just return a null when intercept.");
                }
            }

            logLevel = log.level;
            moduleName = log.moduleName;
            threadInfo = log.threadInfo;
            stackTrace = log.stackTraceInfo;
            msg = log.msg;
        }
        boolean isThrowable = (tr != null);
        androidPrinter.println(logLevel, moduleName, StringUtils.isEmpty(threadInfo) ? "" :  (threadInfo + SystemCompat.lineSeparator)
                + (stackTrace != null ? (stackTrace + SystemCompat.lineSeparator) : "") + msg, isThrowable );
        filePrinter.println(logLevel, moduleName,  StringUtils.isEmpty(threadInfo) ? "" :  (threadInfo + SystemCompat.lineSeparator)
                + (stackTrace != null ? (stackTrace + SystemCompat.lineSeparator) : "") + msg, isThrowable);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 100)
    public void onMessageEvent(LogEntity event) {
        Log.e("LogManager", "收到Event");
        if (filePrinter != null) {
            filePrinter.println();
        }
    }

    private void printlnInternal(int logLevel, String moduleName, Throwable tr, String threadInfo) {
        String stackTrace = logConfiguration.withExceptionStackTrace
                ? logConfiguration.stackTraceFormatter.format(
                StackTraceUtil.getCroppedRealStackTrack(tr.getStackTrace(),
                        logConfiguration.stackTraceOrigin, logConfiguration.stackTraceDepth)) : null;

        if (logConfiguration.interceptors != null) {
            LogEntity log = new LogEntity(logLevel, moduleName, "", threadInfo);
            for (Interceptor interceptor : logConfiguration.interceptors) {
                log = interceptor.intercept(log);
                if (log == null) {
                    return;
                }

                // Check if the log still healthy.
                if (log.moduleName == null || log.msg == null) {
                    throw new IllegalStateException("Interceptor " + interceptor
                            + " should not remove the tag or message of a log,"
                            + " if you don't want to print this log,"
                            + " just return a null when intercept.");
                }
            }

            logLevel = log.level;
            moduleName = log.moduleName;
            threadInfo = log.threadInfo;
        }

        String msg = StringUtils.isEmpty(threadInfo) ? "" : (threadInfo + SystemCompat.lineSeparator)
                + (stackTrace == null ? "" : stackTrace + SystemCompat.lineSeparator);
        boolean isThrowable = (tr != null);
        androidPrinter.println(logLevel, moduleName, msg, isThrowable);
        filePrinter.println(logLevel, moduleName, msg, isThrowable);
    }

    private void printlnInternal(int logLevel, String moduleName, String msg, String threadInfo) {

        if (logConfiguration.interceptors != null) {
            LogEntity log = new LogEntity(logLevel, moduleName, msg, threadInfo);
            for (Interceptor interceptor : logConfiguration.interceptors) {
                log = interceptor.intercept(log);
                if (log == null) {
                    return;
                }

                // Check if the log still healthy.
                if (log.moduleName == null || log.msg == null) {
                    throw new IllegalStateException("Interceptor " + interceptor
                            + " should not remove the tag or message of a log,"
                            + " if you don't want to print this log,"
                            + " just return a null when intercept.");
                }
            }

            logLevel = log.level;
            moduleName = log.moduleName;
            threadInfo = log.threadInfo;
            msg = log.msg;
        }
        androidPrinter.println(logLevel, moduleName, StringUtils.isEmpty(threadInfo) ? "" : (threadInfo + SystemCompat.lineSeparator) + msg , false);
        filePrinter.println(logLevel, moduleName, msg, false);
    }

    public static class Builder {
        private int logLevel;
        private String tag;
        private String moduleName;

        public Builder() {
            LogStorer.assertInitialized();
        }

        public Builder logLevel(int logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder moduleName(String moduleName) {
            this.moduleName = moduleName;
            return this;
        }

        public LogManager build() {
            return new LogManager(this);
        }
    }

}
