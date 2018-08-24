package com.qiyi.loglibrary.formatter.stacktrace;

import com.qiyi.loglibrary.SystemCompat;

public class DefaultStackTraceFormatter implements StackTraceFormatter {

    @Override
    public String format(StackTraceElement[] stackTrace) {

        if (stackTrace == null || stackTrace.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder(256);

        if (stackTrace.length == 1) {
            return "\t" + stackTrace[0].toString();
        }

        for (int i = 0,N = stackTrace.length; i < N; i++) {
            sb.append("\t");
            sb.append(stackTrace[i].toString());
            if (i != N - 1) {
                sb.append(SystemCompat.lineSeparator);
            }
        }

        return sb.toString();
    }
}
