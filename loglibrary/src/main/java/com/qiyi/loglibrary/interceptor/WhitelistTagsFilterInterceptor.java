package com.qiyi.loglibrary.interceptor;

import com.qiyi.loglibrary.LogEntity;

import java.util.Arrays;

public class WhitelistTagsFilterInterceptor extends AbstractFilterInterceptor {

    private Iterable<String> whitelistTags;

    /**
     * Constructor
     *
     * @param whitelistTags the whitelist tags, the logs with a tag that is NOT in the whitelist
     *                      will be filtered out
     */
    public WhitelistTagsFilterInterceptor(String... whitelistTags) {
        this(Arrays.asList(whitelistTags));
    }

    /**
     * Constructor
     *
     * @param whitelistTags the whitelist tags, the logs with a tag that is NOT in the whitelist
     *                      will be filtered out
     */
    public WhitelistTagsFilterInterceptor(Iterable<String> whitelistTags) {
        if (whitelistTags == null) {
            throw new NullPointerException();
        }
        this.whitelistTags = whitelistTags;
    }

    /**
     * {@inheritDoc}
     *
     * @return true if the tag of the log is NOT in the whitelist, false otherwise
     */
    @Override
    protected boolean reject(LogEntity log) {
        if (whitelistTags != null) {
            for (String enabledTag : whitelistTags) {
                if (log.moduleName.equals(enabledTag)) {
                    return false;
                }
            }
        }
        return true;
    }
}
