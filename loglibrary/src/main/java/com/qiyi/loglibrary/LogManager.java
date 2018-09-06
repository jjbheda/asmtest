package com.qiyi.loglibrary;

import android.os.Environment;
import android.util.Log;

import com.qiyi.loglibrary.flattener.DefaultFlattener;
import com.qiyi.loglibrary.formatter.object.ObjectFormatter;
import com.qiyi.loglibrary.formatter.stacktrace.StackTraceFormatter;
import com.qiyi.loglibrary.formatter.thread.ThreadFormatter;
import com.qiyi.loglibrary.formatter.throwable.ThrowableFormatter;
import com.qiyi.loglibrary.interceptor.Interceptor;
import com.qiyi.loglibrary.printer.AndroidPrinter;
import com.qiyi.loglibrary.printer.FilePrinter;
import com.qiyi.loglibrary.printer.Printer;
import com.qiyi.loglibrary.printer.PrinterSet;
import com.qiyi.loglibrary.printer.naming.DefaultFileNameGenerator;
import com.qiyi.loglibrary.strategy.LogLevel;
import com.qiyi.loglibrary.util.DefaultsFactory;
import com.qiyi.loglibrary.util.StackTraceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A LogManager is used to do the real logging work, can use multiple log printers to print the log.
 */

public class LogManager {

    private LogConfiguration logConfiguration;
    private AndroidPrinter androidPrinter;
    FilePrinter filePrinter;

    public LogManager(LogConfiguration logConfiguration) {
        this.logConfiguration = logConfiguration;
        androidPrinter = new AndroidPrinter();
        filePrinter = new FilePrinter.Builder().logFlattener(new DefaultFlattener()).build();
    }

    public LogManager(Builder builder) {
        EventBus.getDefault().register(this);
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


        if (builder.threadSet) {
            if (builder.withThread) {
                logConfigBuilder.withTread();
            } else {
                logConfigBuilder.withNoThread();
            }
        }

        if (builder.stackTraceSet) {
            if (builder.withStackTrace) {
                if (builder.stackTraceOrigin != null) {
                    logConfigBuilder.withExceptionStackTrace(builder.stackTraceOrigin, builder.stackTraceDepth);
                } else {
                    logConfigBuilder.withStackTrace(builder.stackTraceDepth);
                }

            } else {
                logConfigBuilder.withNoExceptionStackTrace();
            }
        }

        if (builder.throwableFormatter != null) {
            logConfigBuilder.throwableFormatter(builder.throwableFormatter);
        }

        if (builder.threadFormatter != null) {
            logConfigBuilder.threadFormatter(builder.threadFormatter);
        }

        if (builder.stackTraceFormatter != null) {
            logConfigBuilder.stackTraceFormatter(builder.stackTraceFormatter);
        }

        if (builder.interceptors != null) {
            logConfigBuilder.interceptors(builder.interceptors);
        }

        logConfiguration = logConfigBuilder.build();

        androidPrinter = new AndroidPrinter();
        filePrinter = new FilePrinter.Builder().logFlattener(new DefaultFlattener()).build();
    }

//    public void v(Object object) {
//        println(LogLevel.VERBOSE, object);
//    }

    public void v(Object[] array) {
        println(LogLevel.VERBOSE, array);
    }

//    public void v(String msg) {
//        println(LogLevel.VERBOSE, msg);
//    }

//    public void v(String format, Object... args) {
//        println(LogLevel.VERBOSE, format, args);
//    }

//    public void v(String msg, Throwable tr) {
//        println(LogLevel.VERBOSE, msg, tr);
//    }

    public void d(Object[] array) {
        println(LogLevel.DEBUG, array);
    }

//    public void d(String format, Object... args) {
//        println(LogLevel.DEBUG, format, args);
//    }


    public void d(String moduleName, String msg) {
        println(LogLevel.DEBUG, moduleName, msg);
    }

//    public void i(Object object) {
//        println(LogLevel.INFO, object);
//    }
//
//    public void i(Object[] array) {
//        println(LogLevel.INFO, array);
//    }

//    public void i(String format, Object... args) {
//        println(LogLevel.INFO, format, args);
//    }

//    public void i(String msg) {
//        println(LogLevel.INFO, msg);
//    }
//
//    public void i(String msg, Throwable tr) {
//        println(LogLevel.INFO, msg, tr);
//    }

//    public void w(Object object) {
//        println(LogLevel.WARN, object);
//    }

    public void w(Object[] array) {
        println(LogLevel.WARN, array);
    }

//    public void w(String format, Object... args) {
//        println(LogLevel.WARN, format, args);
//    }


    public void w(String moduleName, Throwable tr) {
        println(LogLevel.WARN, moduleName, tr);
    }

    public void w(String moduleName, String msg) {
        println(LogLevel.WARN, moduleName, msg);
    }
//    public void e(Object object) {
//        println(LogLevel.ERROR, object);
//    }

    public void e(Object[] array) {
        println(LogLevel.ERROR, array);
    }

    public void e(String moduleName, String msg) {
        println(LogLevel.ERROR, moduleName, msg);
    }


//    public void e(String msg) {
//        println(LogLevel.ERROR, msg);
//    }
//
//    public void e(String msg, Throwable tr) {
//        println(LogLevel.ERROR, msg, tr);
//    }

//    public void log(int logLevel, Object object) {
//        println(logLevel, object);
//    }

    public void log(int logLevel, Object[] array) {
        println(logLevel, array);
    }

//    public void log(int logLevel, String format, Object... args) {
//        println(logLevel, format, args);
//    }

//    public void log(int logLevel, String msg) {
//        println(logLevel, msg);
//    }

    public void log(String moduleName, int logLevel, String msg, Throwable tr) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternalWithThrowble(moduleName, logLevel, msg, tr);
    }

    /**
     * Print a log in a new line.
     *
     * @param logLevel the log level of the printing log
     * @param msg      the message you would like to log
     * @param tr       a throwable object to log
     */
    private void println(String moduleName, int logLevel, String msg, Throwable tr) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternalWithThrowble(moduleName, logLevel, msg, tr);
    }

    public void println(int logLevel, Object[] array) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternal(logLevel, array.toString());
    }


    private void println(String moduleName, int logLevel, String format, Object... args) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternal(logLevel, moduleName, formatArgs(format, args));
    }

    private void println(String moduleName, int logLevel , String msg) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternal(logLevel, moduleName, msg);
    }

//    private void println(int logLevel, String format, Object... args) {
//        if (logLevel < logConfiguration.logLevel) {
//            return;
//        }
//        printlnInternal(logLevel, formatArgs(format, args));
//    }

    private void println(int logLevel, String moduleName, Throwable e) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternal(logLevel,moduleName, e);
    }

    private void println(int logLevel, String moduleName, String msg) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternal(logLevel,moduleName, msg);
    }
//
//    private <T> void println(String moduleName, int logLevel, T object) {
//        if (logLevel < logConfiguration.logLevel) { //过滤日志级别
//            return;
//        }
//
//        if (object instanceof Throwable) {
//            printlnInternalWithThrowble(moduleName,logLevel,"", (Throwable) object);
//            return;
//        }
//
//        String objectString;
//        if (object != null) {
//            ObjectFormatter<? super T> objectFormatter = logConfiguration.getObjectFormatter(object);
//            if (objectFormatter != null) {
//                objectString = objectFormatter.format(object);
//            } else {
//                objectString = object.toString();
//            }
//        } else {
//            objectString = "null";
//        }
//
//        printlnInternal(logLevel, objectString);
//    }

    private void printlnInternalWithThrowble(String moduleName, int logLevel, String msg, Throwable tr) {

        String thread = logConfiguration.withThread
                ? logConfiguration.threadFormatter.format(Thread.currentThread()) : null;

        String stackTrace = logConfiguration.withExceptionStackTrace
                ? logConfiguration.stackTraceFormatter.format(
                StackTraceUtil.getCroppedRealStackTrack(tr.getStackTrace(),
                        logConfiguration.stackTraceOrigin, logConfiguration.stackTraceDepth)) : null;

        if (logConfiguration.interceptors != null) {
            LogEntity log = new LogEntity(logLevel, moduleName, thread, stackTrace, msg);
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
            thread = log.threadInfo;
            stackTrace = log.stackTraceInfo;
            msg = log.msg;
        }

        androidPrinter.println(logLevel, moduleName, (thread != null ? (thread + SystemCompat.lineSeparator) : "")
                + (stackTrace != null ? (stackTrace + SystemCompat.lineSeparator) : "") + msg);
        filePrinter.println(logLevel, moduleName, (thread != null ? (thread + SystemCompat.lineSeparator) : "")
                + (stackTrace != null ? (stackTrace + SystemCompat.lineSeparator) : "") + msg);
    }

    private void printlnInternal(int logLevel, String msg) {
        String tag = logConfiguration.tag;
        String thread = logConfiguration.withThread
                ? logConfiguration.threadFormatter.format(Thread.currentThread()) : null;

        if (logConfiguration.interceptors != null) {
            LogEntity log = new LogEntity(logLevel, tag, msg, thread);
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
            tag = log.moduleName;
            thread = log.threadInfo;
            msg = log.msg;
        }
        androidPrinter.println(logLevel, tag, (thread != null ? (thread + SystemCompat.lineSeparator) : "") + msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 100)
    public void onMessageEvent(LogEntity event) {
        Log.e("LogManager", "收到Event");
        if (filePrinter != null) {
            filePrinter.println();
        }
    }

    private void printlnInternal(int logLevel, String moduleName, Throwable tr) {
//        String tag = logConfiguration.tag;
        String thread = logConfiguration.withThread
                ? logConfiguration.threadFormatter.format(Thread.currentThread()) : null;

        String stackTrace = logConfiguration.withExceptionStackTrace
                ? logConfiguration.stackTraceFormatter.format(
                StackTraceUtil.getCroppedRealStackTrack(tr.getStackTrace(),
                        logConfiguration.stackTraceOrigin, logConfiguration.stackTraceDepth)) : null;

        if (logConfiguration.interceptors != null) {
            LogEntity log = new LogEntity(logLevel, moduleName, "", thread);
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
            thread = log.threadInfo;
        }

        String msg = (thread != null ? (thread + SystemCompat.lineSeparator) : "")
                + (stackTrace != null ? (stackTrace + SystemCompat.lineSeparator) : "") ;
        androidPrinter.println(logLevel, moduleName, msg);
        filePrinter.println(logLevel, moduleName, msg);
    }

    private void printlnInternal(int logLevel, String moduleName, String msg) {
//        String tag = logConfiguration.tag;
        String thread = logConfiguration.withThread
                ? logConfiguration.threadFormatter.format(Thread.currentThread()) : null;

        if (logConfiguration.interceptors != null) {
            LogEntity log = new LogEntity(logLevel, moduleName, msg, thread);
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
            thread = log.threadInfo;
            msg = log.msg;
        }
        androidPrinter.println(logLevel, moduleName, (thread != null ? (thread + SystemCompat.lineSeparator) : "") + msg);
        filePrinter.println(logLevel, moduleName, msg);
    }

    private String formatArgs(String format, Object... args) {
        if (format != null) {
            return String.format(format, args);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(args[i]);
            }
            return sb.toString();
        }
    }

    public static class Builder {
        private int logLevel;
        private String tag;
        private String moduleName;
        private boolean withThread;

        /**
         * Whether we have enabled/disabled thread info.
         */
        private boolean threadSet;

        private boolean withStackTrace;

        /**
         * The origin of stack trace elements from which we should NOT log when logging with stack trace,
         * it can be a package name like "com.qiyi.loglibrary", a class name like "com.qiyi.loglibrary.LogStorer",
         * or something else between package name and class name, like "com.qiyi.".
         * <p>
         * It is mostly used when you are using a logger wrapper.
         */
        private String stackTraceOrigin;

        private int stackTraceDepth;

        /**
         * Whether we have enabled/disabled stack trace.
         */
        private boolean stackTraceSet;

        private ThrowableFormatter throwableFormatter;
        private ThreadFormatter threadFormatter;
        private StackTraceFormatter stackTraceFormatter;
        private List<Interceptor> interceptors;
        private Map<Class<?>, ObjectFormatter<?>> objectFormatters;
        private Printer printer;

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

        public Builder bWithThread() {
            this.withThread = true;
            this.threadSet = true;
            return this;
        }

        public Builder bWithNoThread() {
            this.withThread = false;
            this.threadSet = true;
            return this;
        }

        public Builder bWithStackTrace(int depth) {
            this.withStackTrace = true;
            this.stackTraceDepth = depth;
            this.stackTraceSet = true;
            return this;
        }

        public Builder bWithStackTrace(String stackTraceOrigin, int depth) {
            this.withStackTrace = true;
            this.stackTraceOrigin = stackTraceOrigin;
            this.stackTraceDepth = depth;
            this.stackTraceSet = true;
            return this;
        }

        public Builder bWithNoStackTrace() {
            this.withStackTrace = false;
            this.stackTraceOrigin = null;
            this.stackTraceDepth = 0;
            this.stackTraceSet = true;
            return this;
        }

        public Builder throwableFormatter(ThrowableFormatter throwableFormatter) {
            this.throwableFormatter = throwableFormatter;
            return this;
        }

        public Builder threadFormatter(ThreadFormatter threadFormatter) {
            this.threadFormatter = threadFormatter;
            return this;
        }

        public Builder stackTraceFormatter(StackTraceFormatter stackTraceFormatter) {
            this.stackTraceFormatter = stackTraceFormatter;
            return this;
        }

        public <T> Builder addObjectFormatter(Class<T> objectClass,
                                              ObjectFormatter<? super T> objectFormatter) {
            if (objectFormatters == null) {
                objectFormatters = new HashMap<>(DefaultsFactory.builtinObjectFormatters());
            }

            objectFormatters.put(objectClass, objectFormatter);
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptors == null) {
                interceptors = new ArrayList<>();
            }
            interceptors.add(interceptor);
            return this;
        }

        public Builder printers(Printer... printers) {
            if (printers.length == 0) {
                this.printer = null;
            } else if (printers.length == 1) {
                this.printer = printers[0];
            } else {
                this.printer = new PrinterSet(printers);
            }
            return this;
        }

//        public void v(Object object) {
//            build().v(object);
//        }


        public LogManager build() {
            return new LogManager(this);
        }
    }

}
