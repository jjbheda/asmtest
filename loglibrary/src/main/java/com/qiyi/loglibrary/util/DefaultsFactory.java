package com.qiyi.loglibrary.util;

import android.content.Intent;
import android.os.Bundle;

import com.qiyi.loglibrary.Platform;
import com.qiyi.loglibrary.flattener.DefaultFlattener;
import com.qiyi.loglibrary.flattener.Flattener;
import com.qiyi.loglibrary.formatter.object.BundleFormatter;
import com.qiyi.loglibrary.formatter.object.IntentFormatter;
import com.qiyi.loglibrary.formatter.object.ObjectFormatter;
import com.qiyi.loglibrary.formatter.stacktrace.DefaultStackTraceFormatter;
import com.qiyi.loglibrary.formatter.stacktrace.StackTraceFormatter;
import com.qiyi.loglibrary.formatter.thread.DefaultThreadFormatter;
import com.qiyi.loglibrary.formatter.thread.ThreadFormatter;
import com.qiyi.loglibrary.formatter.throwable.DefaultThrowableFormatter;
import com.qiyi.loglibrary.formatter.throwable.ThrowableFormatter;
import com.qiyi.loglibrary.printer.Printer;
import com.qiyi.loglibrary.printer.backup.BackupStrategy;
import com.qiyi.loglibrary.printer.backup.FileSizeBackupStrategy;
import com.qiyi.loglibrary.printer.naming.DefaultFileNameGenerator;
import com.qiyi.loglibrary.printer.naming.FileNameGenerator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultsFactory {

    private static final String DEFAULT_LOG_FILE_NAME = "log";
    private static final long DEFAULT_LOG_FILE_MAX_SIZE = 2* 1024 * 1024;   //日志最大2M
    private static Map<Class<?>, ObjectFormatter<?>> BUILTIN_OBJECT_FORMATTERS;

    static {
        Map<Class<?>, ObjectFormatter<?>> objectFormatters = new HashMap<>();
        objectFormatters.put(Bundle.class, new BundleFormatter());
        objectFormatters.put(Intent.class, new IntentFormatter());
        BUILTIN_OBJECT_FORMATTERS = Collections.unmodifiableMap(objectFormatters);
    }

    public static FileNameGenerator createFileNameGenerator() {
        return new DefaultFileNameGenerator(DEFAULT_LOG_FILE_NAME);
    }

    public static BackupStrategy createBackupStrategy() {
        return new FileSizeBackupStrategy(DEFAULT_LOG_FILE_MAX_SIZE);
    }

    public static Flattener createFlattener() {
        return new DefaultFlattener();
    }

    public static StackTraceFormatter createStackTraceFormatter() {
        return new DefaultStackTraceFormatter();
    }

    public static ThrowableFormatter createThrowableFormatter() {
        return new DefaultThrowableFormatter();
    }

    public static ThreadFormatter createThreadFormatter() {
        return new DefaultThreadFormatter();
    }

    public static Map<Class<?>, ObjectFormatter<?>> builtinObjectFormatters() {
        return BUILTIN_OBJECT_FORMATTERS;
    }

    /**
     * Create the default printer.
     */
    public static Printer createPrinter() {
        return Platform.get().defaultPrinter();
    }


}
