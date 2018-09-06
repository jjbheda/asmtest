package com.qiyi.loglibrary.util;

import android.text.TextUtils;
import android.util.Log;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

/**
 * Created by songguobin on 2017/6/13.
 */

public class ExceptionUtils {

    private static final String TAG = "ExceptionUtils";

    public static void printStackTrace(Exception e) {

        if (e != null && e.getMessage() != null) {
            Log.d(TAG, e.getMessage());
        }

//        if(e != null && DebugLog.isDebug()){
//            e.printStackTrace();
//        }

    }

    public static void printStackTrace(String customTag,Exception e){

        if (e != null && e.getMessage() != null) {
//            if(!TextUtils.isEmpty(customTag)){
//                DebugLog.d(customTag, e.getMessage());
//            }else{
//                DebugLog.d(TAG, e.getMessage());
//            }
        }

//        if(e != null && DebugLog.isDebug()){
//            e.printStackTrace();
//        }

    }

    public static void printStackTrace(Error e) {

        if (e != null && e.getMessage() != null) {
          Log.d(TAG, e.getMessage());
        }

//        if(e != null && DebugLog.isDebug()){
            e.printStackTrace();
//        }

    }

    public static void printStackTrace(Throwable e){
        if (e != null && e.getMessage() != null) {
            Log.d(TAG, e.getMessage());
        }
        if(e != null ){
            e.printStackTrace();
        }
    }

    public static String getStackTraceString(Throwable e) {
        boolean causedByUnknownHost = isCausedByUnknownHost(e);
        // Log.getStackTraceString 高性能，但是会忽略来自 UnknownHostException 直接返回空串
        if (causedByUnknownHost) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        } else {
            return Log.getStackTraceString(e);
        }
    }

    private static boolean isCausedByUnknownHost(Throwable e) {
        Throwable t = e;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
}
