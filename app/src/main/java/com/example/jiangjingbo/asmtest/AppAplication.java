package com.example.jiangjingbo.asmtest;

import android.app.Application;
import android.content.Context;

import com.qiyi.loglibrary.LogConfiguration;
import com.qiyi.loglibrary.LogStorer;
import com.qiyi.loglibrary.flattener.ClassicFlattener;
import com.qiyi.loglibrary.printer.AndroidPrinter;
import com.qiyi.loglibrary.printer.FilePrinter;
import com.qiyi.loglibrary.printer.FilePrinterWithPool;
import com.qiyi.loglibrary.printer.Printer;
import com.qiyi.loglibrary.menu.DateDirGenerator;
import com.qiyi.loglibrary.printer.naming.DefaultFileNameGenerator;
import com.qiyi.loglibrary.strategy.LogLevel;

import org.greenrobot.eventbus.EventBus;

public class AppAplication extends Application {

    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getBaseContext();
        initLogStorer();

    }

    private void initLogStorer() {
        LogConfiguration configuration = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.NONE)
                .withTread()
                .withStackTrace(3)
                .build();

        Printer androidPrinter = new AndroidPrinter();

        LogStorer.init(context, configuration, androidPrinter);
    }



}
