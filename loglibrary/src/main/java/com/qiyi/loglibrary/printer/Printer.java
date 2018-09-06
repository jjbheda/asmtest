package com.qiyi.loglibrary.printer;

public interface Printer {
//    void println(int loglevel, String moduleName, String msg);
    //增加tag 标识，方便AndroidPrinter过滤tag 标识
    void println(int loglevel, String moduleName, String msg);
    void println();
}
