package com.qiyi.loglibrary.printer;

import com.qiyi.loglibrary.flattener.Flattener;
import com.qiyi.loglibrary.util.DefaultsFactory;

public class ConsolePrinter implements Printer {

    /**
     * The log flattener when print a log.
     */
    private Flattener flattener;

    /**
     * Constructor.
     */
    public ConsolePrinter() {
        this.flattener = DefaultsFactory.createFlattener();
    }

    /**
     * Constructor.
     *
     * @param flattener the log flattener when print a log
     */
    public ConsolePrinter(Flattener flattener) {
        this.flattener = flattener;
    }

    @Override
    public void println(int logLevel, String tag, String msg) {
        String flattenedLog = flattener.flatten(logLevel, tag, msg).toString();
        System.out.println(flattenedLog);
    }
}

