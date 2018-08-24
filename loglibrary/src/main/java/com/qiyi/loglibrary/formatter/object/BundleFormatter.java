package com.qiyi.loglibrary.formatter.object;

import android.os.Bundle;

import com.qiyi.loglibrary.util.ObjectToStringUtil;

public class BundleFormatter implements ObjectFormatter<Bundle> {

    @Override
    public String format(Bundle data) {
        return ObjectToStringUtil.bundleToString(data);
    }
}
