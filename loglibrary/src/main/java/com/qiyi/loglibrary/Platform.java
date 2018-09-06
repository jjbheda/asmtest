package com.qiyi.loglibrary;

import android.annotation.SuppressLint;
import android.os.Build;

import com.qiyi.loglibrary.printer.AndroidPrinter;
import com.qiyi.loglibrary.printer.Printer;

public class Platform {
    private static final Platform PLATFORM = findPlatform();

    public static Platform get() {
        return PLATFORM;
    }

    @SuppressLint("NewApi")
    String lineSeparator() {
        return System.lineSeparator();
    }

   public Printer defaultPrinter() {
        return new AndroidPrinter();
    }

    public void warn(String msg) {
        System.out.println(msg);
    }

    private static Platform findPlatform() {
        try {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0) {
                return new AndroidPlatform();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Platform();
    }

    static class AndroidPlatform extends Platform {
        @Override
        String lineSeparator() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return "\n";
            }
            return System.lineSeparator();
        }

        @Override
        public Printer defaultPrinter() {
            return new AndroidPrinter();
        }

    }

}
