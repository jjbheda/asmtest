package com.example.printer;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

public class LogPrinter {
    private static final String TAG = "LogPrinter";

    public static void printBefore(String method) {
        Log.e(TAG, method + "begin!!!");
    }

    public static void printAfter(String method) {
        Log.e(TAG, method + "end!!!");
    }

    public static void printException(Throwable e) {
        StackTraceElement[] st = StackTraceUtil.getCroppedRealStackTrack(e.getStackTrace(),null,10);
        String stackStr = StackTraceUtil.format(st);
        Log.e(TAG, "e" + stackStr);
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
