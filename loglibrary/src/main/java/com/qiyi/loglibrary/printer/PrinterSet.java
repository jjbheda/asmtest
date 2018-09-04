package com.qiyi.loglibrary.printer;

public class PrinterSet implements Printer {
    private Printer[] printers;

    public PrinterSet(Printer... printers) {
        this.printers = printers;
    }

    @Override
    public void println(int loglevel, String tag, String msg) {
        for (Printer printer : printers) {
            printer.println(loglevel, tag, msg);
        }
    }

    @Override
    public void println() {
        for (Printer printer : printers) {
            printer.println();
        }
    }
}
