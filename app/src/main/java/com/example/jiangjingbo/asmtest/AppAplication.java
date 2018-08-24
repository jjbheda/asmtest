package com.example.jiangjingbo.asmtest;

import android.app.Application;
import android.os.Environment;

import com.qiyi.loglibrary.LogConfiguration;
import com.qiyi.loglibrary.LogStorer;
import com.qiyi.loglibrary.flattener.ClassicFlattener;
import com.qiyi.loglibrary.flattener.DefaultFlattener;
import com.qiyi.loglibrary.printer.AndroidPrinter;
import com.qiyi.loglibrary.printer.FilePrinter;
import com.qiyi.loglibrary.printer.Printer;
import com.qiyi.loglibrary.printer.naming.DateFileNameGenerator;
import com.qiyi.loglibrary.strategy.LogLevel;

import java.io.File;

public class AppAplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initLogStorer();
    }

    private void initLogStorer() {
        LogConfiguration configuration = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.NONE)
                .withTread()
//                .withStackTrace(2)
                .tag("LogStorer")
                .build();

        Printer androidPrinter = new AndroidPrinter();

        String logFilePath = new File(Environment.getExternalStorageDirectory(), "logstorer").getPath();
        Printer filePrinter = new FilePrinter.Builder(logFilePath).fileNameGenerator(new DateFileNameGenerator())
                .logFlattener(new ClassicFlattener()).build();

        LogStorer.init(configuration, androidPrinter, filePrinter);
    }

}
