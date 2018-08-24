package com.qiyi.loglibrary.interceptor;

import com.qiyi.loglibrary.LogEntity;
/**
 * Interceptors are used to intercept every log after formatting message, thread info and
 * stack trace info, and before printing, normally we can modify or drop the log.
 * <p>
 * Remember interceptors are ordered, which means earlier added interceptor will get the log
 * first.
 * <p>
 * If any interceptor remove the log(by returning null when {@link #intercept(LogEntity)}),
 * then the interceptors behind that one won't receive the log, and the log won't be printed at all.
 *
 *
 */

public interface Interceptor {
    LogEntity intercept(LogEntity log);
}
