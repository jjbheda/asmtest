package com.qiyi.loglibrary.printer.backup;

import java.io.File;

public class FileSizeBackupStrategy implements BackupStrategy {
    private long maxSize;

    public FileSizeBackupStrategy(long maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean shouldBackup(File file) {
        return file.length() > maxSize;
    }
}
