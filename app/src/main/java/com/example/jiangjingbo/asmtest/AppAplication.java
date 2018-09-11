package com.example.jiangjingbo.asmtest;

import android.app.Application;
import android.content.Context;

import com.qiyi.loglibrary.Constant;
import com.qiyi.loglibrary.LogConfiguration;
import com.qiyi.loglibrary.LogStorer;
import com.qiyi.loglibrary.printer.AndroidPrinter;
import com.qiyi.loglibrary.printer.Printer;
import com.qiyi.loglibrary.strategy.LogLevel;

public class AppAplication extends Application {

    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getBaseContext();
//        initLogStorer();
    }

}
