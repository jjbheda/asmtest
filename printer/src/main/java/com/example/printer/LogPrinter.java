package com.example.printer;

import android.util.Log;

public class LogPrinter {
    private static final String TAG = "LogPrinter";

    public static void printBefore(String method) {
        Log.e(TAG, method + "begin!!!");
    }

    public static void printAfter(String method) {
        Log.e(TAG, method + "end!!!");
    }

    public static void printException(Exception e) {
        Log.e(TAG, e + "exception end!!!");
    }

}
