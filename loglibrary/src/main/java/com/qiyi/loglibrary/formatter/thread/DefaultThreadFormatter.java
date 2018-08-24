package com.qiyi.loglibrary.formatter.thread;

public class DefaultThreadFormatter implements ThreadFormatter {
    @Override
    public String format(Thread data) {
        return "Thread:" + data.getName();
    }
}
