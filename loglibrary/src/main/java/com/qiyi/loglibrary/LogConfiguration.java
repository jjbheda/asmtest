package com.qiyi.loglibrary;

import com.qiyi.loglibrary.formatter.object.ObjectFormatter;
import com.qiyi.loglibrary.formatter.stacktrace.StackTraceFormatter;
import com.qiyi.loglibrary.formatter.throwable.ThrowableFormatter;
import com.qiyi.loglibrary.interceptor.Interceptor;
import com.qiyi.loglibrary.strategy.LogLevel;
import com.qiyi.loglibrary.util.DefaultsFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogConfiguration {
    public int logLevel;
    public String moduleName;
    public String tag;
    public boolean withExceptionStackTrace;      //是否打印异常  栈信息
    /**
     * The origin of stack trace elements from which we should NOT log when logging with stack trace,
     * it can be a package name like "com.qiyi.loglibrary", a class name like "com.qiyi.loglibrary.LogStorer",
     * or something else between package name and class name, like "com.qiyi.".
     * <p>
     * It is mostly used when you are using a logger wrapper.
     */
    public String stackTraceOrigin;
    public int stackTraceDepth;
    public ThrowableFormatter throwableFormatter;
    public StackTraceFormatter stackTraceFormatter;
    public List<Interceptor> interceptors;
    /**
     * The object formatters, used when logging an object.
     */
    private Map<Class<?>, ObjectFormatter<?>> objectFormatters;


    public LogConfiguration(Builder builder) {
        logLevel = builder.logLevel;
        moduleName = builder.moduleName;
        tag = builder.tag;
        withExceptionStackTrace = builder.withExceptionStackTrace;
        stackTraceDepth = builder.stackTraceDepth;
        stackTraceOrigin = builder.stackTraceOrigin;
        throwableFormatter = builder.throwableFormatter;
        stackTraceFormatter = builder.stackTraceFormatter;
        interceptors = builder.interceptors;
        objectFormatters = builder.objectFormatters;
    }

    /**
     * Get {@link ObjectFormatter} for specific object.
     *
     * @param object the object
     * @param <T>    the type of object
     * @return the object formatter for the object, or null if not found
     *
     */
    public <T> ObjectFormatter<? super T> getObjectFormatter(T object) {
        if (objectFormatters == null) {
            return null;
        }

        Class<? super T> clazz;
        Class<? super T> superClazz = (Class<? super T>) object.getClass();
        ObjectFormatter<? super T> formatter;
        do {
            clazz = superClazz;
            formatter = (ObjectFormatter<? super T>) objectFormatters.get(clazz);
            superClazz = clazz.getSuperclass();
        } while (formatter != null && superClazz !=null);

        return formatter;
    }

    public static class Builder {
        private static final int DEFAULT_LOG_LEVEL = LogLevel.ALL;
        private static final String DEFAULT_TAG = "LOG_STORER";
        private int logLevel = DEFAULT_LOG_LEVEL;
        private String moduleName = DEFAULT_TAG;
        private String tag = DEFAULT_TAG;
        private boolean withExceptionStackTrace;
        private String stackTraceOrigin;

        private int stackTraceDepth;
        private ThrowableFormatter throwableFormatter;
        private StackTraceFormatter stackTraceFormatter;
        private List<Interceptor> interceptors;
        private Map<Class<?>, ObjectFormatter<?>> objectFormatters;

        public Builder() {
        }

        public Builder(LogConfiguration logConfiguration) {
            logLevel = logConfiguration.logLevel;
            moduleName = logConfiguration.moduleName;
            tag = logConfiguration.tag;
            withExceptionStackTrace = logConfiguration.withExceptionStackTrace;
            stackTraceOrigin = logConfiguration.stackTraceOrigin;
            stackTraceDepth = logConfiguration.stackTraceDepth;

            throwableFormatter = logConfiguration.throwableFormatter;
            stackTraceFormatter = logConfiguration.stackTraceFormatter;

            if (logConfiguration.interceptors != null) {
                interceptors = new ArrayList<>(logConfiguration.interceptors);
            }

            if (logConfiguration.objectFormatters != null) {
                objectFormatters = new HashMap<>(logConfiguration.objectFormatters);
            }
        }

        public Builder logLevel(int logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Builder moduleName(String moduleName) {
            this.moduleName = moduleName;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder withStackTrace(int depth) {
            withExceptionStackTrace(null, depth);
            return this;
        }

        public Builder withExceptionStackTrace(String stackTraceOrigin, int depth) {
            this.withExceptionStackTrace = true;
            this.stackTraceOrigin = stackTraceOrigin;
            this.stackTraceDepth = depth;
            return this;
        }

        public Builder withNoExceptionStackTrace() {
            this.withExceptionStackTrace = false;
            this.stackTraceOrigin = null;
            this.stackTraceDepth = 0;
            return this;
        }

        public Builder throwableFormatter(ThrowableFormatter throwableFormatter) {
            this.throwableFormatter = throwableFormatter;
            return this;
        }

        public Builder stackTraceFormatter(StackTraceFormatter stackTraceFormatter) {
            this.stackTraceFormatter = stackTraceFormatter;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptors == null) {
                interceptors = new ArrayList<>();
            }

            interceptors.add(interceptor);
            return this;
        }

        public Builder interceptors(List<Interceptor> interceptors) {
            this.interceptors = interceptors;
            return this;
        }

        /**
         * Copy all object formatters, only for internal usage.
         *
         * @param objectFormatters the object formatters to copy
         * @return the builder
         */
        public Builder objectFormatters(Map<Class<?>, ObjectFormatter<?>> objectFormatters) {
            this.objectFormatters = objectFormatters;
            return this;
        }

        /**
         * Add a {@link ObjectFormatter} for specific class of object.
         *
         * @param objectClass     the class of object
         * @param objectFormatter the object formatter to add
         * @param <T>             the type of object
         * @return the builder
         *
         */
        public <T> Builder addObjectFormatter(Class<T> objectClass,
                                              ObjectFormatter<? super T> objectFormatter) {
            if (objectFormatters == null) {
                objectFormatters = new HashMap<>(DefaultsFactory.builtinObjectFormatters());
            }
            objectFormatters.put(objectClass, objectFormatter);
            return this;
        }

        public LogConfiguration build() {
            initWithDefaultValues();
            return new LogConfiguration(this);
        }

        private void initWithDefaultValues() {
            if (throwableFormatter == null) {
                throwableFormatter = DefaultsFactory.createThrowableFormatter();
            }

            if (stackTraceFormatter == null) {
                stackTraceFormatter = DefaultsFactory.createStackTraceFormatter();
            }

            if (objectFormatters == null) {
                objectFormatters = new HashMap<>(DefaultsFactory.builtinObjectFormatters());
            }
        }
    }
}
