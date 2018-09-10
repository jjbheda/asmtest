package com.qiyi.loglibrary.util;

public class ThreadUtil {

    public static String getThreadInfo(Thread thread) {
        String threadInfo = "";
        String threadName = thread.getName();
        if (!StringUtils.isEmpty(threadName)) {
            threadInfo = threadName + "_" + thread.getId();
        } else {
            threadInfo = thread.getId() + "";
        }
        return threadInfo;
    }
}
