package com.qiyi.loglibrary.interceptor;

import com.qiyi.loglibrary.LogEntity;

import java.util.Arrays;

public class BlacklistTagsFilterInterceptor extends AbstractFilterInterceptor {

    private Iterable<String> blackListTags;

    public BlacklistTagsFilterInterceptor(String... blacklistTags) {
        this(Arrays.asList(blacklistTags));
    }

    public BlacklistTagsFilterInterceptor(Iterable<String> blackListTags) {
        if (blackListTags == null) {
            throw new NullPointerException();
        }
        this.blackListTags = blackListTags;
    }

    @Override
    protected boolean reject(LogEntity log) {
        if (blackListTags != null) {
            for (String disableTag : blackListTags) {
                if (log.moduleName.equals(disableTag)) {
                    return true;
                }
            }
        }
        return false;
    }

}
