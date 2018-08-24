package com.qiyi.loglibrary.interceptor;

import com.qiyi.loglibrary.LogEntity;

public abstract class AbstractFilterInterceptor implements Interceptor {
    @Override
    public LogEntity intercept(LogEntity log) {
        if (reject(log))
            return null;

        return log;
    }

    /**
     * Whether specific log should be filtered out.
     *
     * @param log the specific log
     * @return true if the log should be filtered out, false otherwise
     */
    protected abstract boolean reject(LogEntity log);

}
