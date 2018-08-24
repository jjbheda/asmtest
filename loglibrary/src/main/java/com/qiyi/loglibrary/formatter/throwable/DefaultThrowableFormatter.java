package com.qiyi.loglibrary.formatter.throwable;

import com.qiyi.loglibrary.util.StackTraceUtil;

public class DefaultThrowableFormatter implements ThrowableFormatter {
    @Override
    public String format(Throwable tr) {
        return StackTraceUtil.getStackTraceString(tr);
    }
}
