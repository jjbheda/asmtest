package com.qiyi.loglibrary.formatter.object;

import android.content.Intent;

import com.qiyi.loglibrary.util.ObjectToStringUtil;

public class IntentFormatter implements ObjectFormatter<Intent>{

    @Override
    public String format(Intent data) {
        return ObjectToStringUtil.intentToString(data);
    }
}
