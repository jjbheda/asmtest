package com.qiyi.loglibrary.printer.backup;

import java.io.File;

public interface BackupStrategy {

    boolean shouldBackup(File file);
}
