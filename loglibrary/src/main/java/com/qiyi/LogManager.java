package com.qiyi;

import com.qiyi.loglibrary.LogConfiguration;
import com.qiyi.loglibrary.LogEntity;
import com.qiyi.loglibrary.LogStorer;
import com.qiyi.loglibrary.SystemCompat;
import com.qiyi.loglibrary.formatter.object.ObjectFormatter;
import com.qiyi.loglibrary.formatter.stacktrace.StackTraceFormatter;
import com.qiyi.loglibrary.formatter.thread.ThreadFormatter;
import com.qiyi.loglibrary.formatter.throwable.ThrowableFormatter;
import com.qiyi.loglibrary.interceptor.Interceptor;
import com.qiyi.loglibrary.printer.Printer;
import com.qiyi.loglibrary.printer.PrinterSet;
import com.qiyi.loglibrary.strategy.LogLevel;
import com.qiyi.loglibrary.util.DefaultsFactory;
import com.qiyi.loglibrary.util.StackTraceUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A LogManager is used to do the real logging work, can use multiple log printers to print the log.
 */

public class LogManager {

    private LogConfiguration logConfiguration;
    private Printer printer;

    public LogManager(LogConfiguration logConfiguration, Printer printer) {
        this.logConfiguration = logConfiguration;
        this.printer = printer;
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
                    logConfigBuilder.withStackTrace(builder.stackTraceOrigin, builder.stackTraceDepth);
                } else {
                    logConfigBuilder.withStackTrace(builder.stackTraceDepth);
                }

            } else {
                logConfigBuilder.withNoStackTrace();
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

        if (builder.printer != null) {
            printer = builder.printer;
        } else {
            printer = LogStorer.mPrinter;
        }
    }

    public void v(Object object) {
        println(LogLevel.VERBOSE, object);
    }

    public void v(Object[] array) {
        println(LogLevel.VERBOSE, array);
    }

    public void v(String msg) {
        println(LogLevel.VERBOSE, msg);
    }

    public void v(String format, Object... args) {
        println(LogLevel.VERBOSE, format, args);
    }

    public void v(String msg, Throwable tr) {
        println(LogLevel.VERBOSE, msg, tr);
    }

    public void d(Object object) {
        println(LogLevel.DEBUG, object);
    }

    public void d(Object[] array) {
        println(LogLevel.DEBUG, array);
    }

    public void d(String format, Object... args) {
        println(LogLevel.DEBUG, format, args);
    }

    public void d(String msg) {
        println(LogLevel.DEBUG, msg);
    }

    public void d(String msg, Throwable tr) {
        println(LogLevel.DEBUG, msg, tr);
    }

    public void i(Object object) {
        println(LogLevel.INFO, object);
    }

    public void i(Object[] array) {
        println(LogLevel.INFO, array);
    }

    public void i(String format, Object... args) {
        println(LogLevel.INFO, format, args);
    }

    public void i(String msg) {
        println(LogLevel.INFO, msg);
    }

    public void i(String msg, Throwable tr) {
        println(LogLevel.INFO, msg, tr);
    }

    public void w(Object object) {
        println(LogLevel.WARN, object);
    }

    public void w(Object[] array) {
        println(LogLevel.WARN, array);
    }

    public void w(String format, Object... args) {
        println(LogLevel.WARN, format, args);
    }

    public void w(String msg) {
        println(LogLevel.WARN, msg);
    }

    public void w(String msg, Throwable tr) {
        println(LogLevel.WARN, msg, tr);
    }

    public void e(Object object) {
        println(LogLevel.ERROR, object);
    }

    public void e(Object[] array) {
        println(LogLevel.ERROR, array);
    }

    public void e(String format, Object... args) {
        println(LogLevel.ERROR, format, args);
    }

    public void e(String msg) {
        println(LogLevel.ERROR, msg);
    }

    public void e(String msg, Throwable tr) {
        println(LogLevel.ERROR, msg, tr);
    }

    public void log(int logLevel, Object object) {
        println(logLevel, object);
    }

    public void log(int logLevel, Object[] array) {
        println(logLevel, array);
    }

    public void log(int logLevel, String format, Object... args) {
        println(logLevel, format, args);
    }

    public void log(int logLevel, String msg) {
        println(logLevel, msg);
    }

    public void log(int logLevel, String msg, Throwable tr) {
        println(logLevel, msg, tr);
    }

    /**
     * Print a log in a new line.
     *
     * @param logLevel the log level of the printing log
     * @param msg      the message you would like to log
     * @param tr       a throwable object to log
     */
    private void println(int logLevel, String msg, Throwable tr) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternal(logLevel, ((msg == null || msg.length() == 0)
                ? "" : (msg + SystemCompat.lineSeparator)) + logConfiguration.throwableFormatter.format(tr));
    }

    public void println(int logLevel, Object[] array) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternal(logLevel, array.toString());
    }


    private void println(int logLevel, String format, Object... args) {
        if (logLevel < logConfiguration.logLevel) {
            return;
        }
        printlnInternal(logLevel, formatArgs(format, args));
    }

    private <T> void println(int logLevel, T object) {
        if (logLevel < logConfiguration.logLevel) { //过滤日志级别
            return;
        }

        String objectString;
        if (object != null) {
            ObjectFormatter<? super T> objectFormatter = logConfiguration.getObjectFormatter(object);
            if (objectFormatter != null) {
                objectString = objectFormatter.format(object);
            } else {
                objectString = object.toString();
            }
        } else {
            objectString = "null";
        }

        printlnInternal(logLevel, objectString);
    }

    private void printlnInternal(int logLevel, String msg) {
        String tag = logConfiguration.tag;
        String thread = logConfiguration.withThread
                ? logConfiguration.threadFormatter.format(Thread.currentThread()) : null;

        String stackTrace = logConfiguration.withStackTrace
                ? logConfiguration.stackTraceFormatter.format(
                StackTraceUtil.getCroppedRealStackTrack(new Throwable().getStackTrace(),
                        logConfiguration.stackTraceOrigin, logConfiguration.stackTraceDepth)) : null;

        if (logConfiguration.interceptors != null) {
            LogEntity log = new LogEntity(logLevel, tag, thread, stackTrace, msg);
            for (Interceptor interceptor : logConfiguration.interceptors) {
                log = interceptor.intercept(log);
                if (log == null) {
                    return;
                }

                // Check if the log still healthy.
                if (log.tag == null || log.msg == null) {
                    throw new IllegalStateException("Interceptor " + interceptor
                            + " should not remove the tag or message of a log,"
                            + " if you don't want to print this log,"
                            + " just return a null when intercept.");
                }
            }

            logLevel = log.level;
            tag = log.tag;
            thread = log.threadInfo;
            stackTrace = log.stackTraceInfo;
            msg = log.msg;
        }
        printer.println(logLevel, tag, (thread != null ? (thread + SystemCompat.lineSeparator) : "")
                + (stackTrace != null ? (stackTrace + SystemCompat.lineSeparator) : "") + msg);
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

        public void v(Object object) {
            build().v(object);
        }


        public LogManager build() {
            return new LogManager(this);
        }
    }

}
