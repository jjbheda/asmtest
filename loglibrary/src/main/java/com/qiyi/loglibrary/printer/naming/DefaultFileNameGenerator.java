package com.qiyi.loglibrary.printer.naming;

public class DefaultFileNameGenerator implements FileNameGenerator {
    private String fileName;

    public DefaultFileNameGenerator(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String generateFileName(long timestamp) {
        return fileName;
    }
}
