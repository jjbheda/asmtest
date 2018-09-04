package com.qiyi.loglibrary.printer;

import com.qiyi.loglibrary.flattener.Flattener;
import com.qiyi.loglibrary.printer.backup.BackupStrategy;
import com.qiyi.loglibrary.printer.naming.FileNameGenerator;
import com.qiyi.loglibrary.util.DefaultsFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class FilePrinter implements Printer {
    private String folderPath;
    private FileNameGenerator fileNameGenerator;
    private BackupStrategy backupStrategy;
    private Flattener flattener;
    LogFileWriter writer;   //执行IO
    private volatile Recorder recorder; //放入队列执行

    public FilePrinter(Builder builder) {
        folderPath = builder.folderPath;
        fileNameGenerator = builder.fileNameGenerator;
        backupStrategy = builder.backupStrategy;
        flattener = builder.flattener;
        writer = new LogFileWriter();
        recorder = new Recorder();
        checkLogFolder();
    }

    @Override
    public void println(int logLevel, String tag, String msg) {
        if (!recorder.isStarted()) {
            recorder.start();
        }
        recorder.enqueue(new LogEntity(logLevel, tag, msg));
    }

    @Override
    public void println() {

    }

    private void checkLogFolder() {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public static class Builder {
        String folderPath;
        FileNameGenerator fileNameGenerator;
        BackupStrategy backupStrategy;
        Flattener flattener;

        public Builder(String folderPath) {
            this.folderPath = folderPath;
        }

        public Builder fileNameGenerator(FileNameGenerator fileNameGenerator) {
            this.fileNameGenerator = fileNameGenerator;
            return this;
        }

        public Builder backupStrategy(BackupStrategy backupStrategy) {
            this.backupStrategy = backupStrategy;
            return this;
        }

        public Builder logFlattener(Flattener flattener) {
            this.flattener = flattener;
            return this;
        }

        public FilePrinter build() {
            buildDefaultMethod();
            return new FilePrinter(this);
        }

        public void buildDefaultMethod() {
            if (fileNameGenerator == null) {
                fileNameGenerator = DefaultsFactory.createFileNameGenerator();
            }

            if (backupStrategy == null) {
                backupStrategy = DefaultsFactory.createBackupStrategy();
            }

            if (flattener == null) {
                flattener = DefaultsFactory.createFlattener();
            }
        }

    }

    private class LogEntity {
        int level;
        String tag;
        String msg;

        LogEntity(int level, String tag, String msg) {
            this.level = level;
            this.tag = tag;
            this.msg = msg;
        }
    }

    void printToFile(int logLevel, String tag, String msg) {
        String lastFileName = writer.getLastFileName();
        if (lastFileName == null ) {
            String newFileName = fileNameGenerator.generateFileName(System.currentTimeMillis());
            if (newFileName == null || newFileName.trim().length() == 0) {
                throw new IllegalArgumentException("File name should not be empty.");
            }
            if (!newFileName.equals(lastFileName)) {
                if (writer.isOpened()) {
                    writer.close();
                }
                if (!writer.open(newFileName)) {
                    return;
                }
                lastFileName = newFileName;
            }
        }

        File lastFile = writer.getFile();
        if (backupStrategy.shouldBackup(lastFile)) {
            // Backup the log file, and create a new log file.
            writer.close();
            File backupFile = new File(folderPath, lastFileName + ".bak");
            if (backupFile.exists()) {
                backupFile.delete();
            }
            lastFile.renameTo(backupFile);
            if (!writer.open(lastFileName)) {
                return;
            }
        }
        String flattenedLog = flattener.flatten(logLevel, tag, msg).toString();
        writer.appendLog(flattenedLog);
    }

    private class Recorder implements Runnable {

        private BlockingQueue<LogEntity> logs = new LinkedBlockingDeque<>();
        private volatile boolean isStarted;

        void enqueue(LogEntity log) {
            try {
                logs.put(log);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        boolean isStarted() {
            synchronized (this) {
                return isStarted;
            }
        }

        void start() {
            synchronized (this) {
                new Thread(this).start();
                isStarted = true;
            }
        }

        @Override
        public void run() {
            LogEntity log;
            try {
                while ((log = logs.take()) != null) {
                    printToFile(log.level, log.tag, log.msg);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                synchronized (this) {
                    isStarted = false;
                }
            }
        }
    }

    private class LogFileWriter {
        private String lastFileName;
        private File logFile;
        private BufferedWriter bufferedWriter;

        boolean isOpened() {
            return bufferedWriter != null;
        }

        String getLastFileName() {
            return lastFileName;
        }

        File getFile() {
            return logFile;
        }

        boolean open(String newFileName) {
            lastFileName = newFileName;
            logFile = new File(folderPath, newFileName);

            if (!logFile.exists()) {
                File parent = logFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                    try {
                        logFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        lastFileName = null;
                        logFile = null;
                        return false;
                    }
                }
            }

            try {
                bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            } catch (IOException e) {
                e.printStackTrace();
                lastFileName = null;
                logFile = null;
                return false;
            }
            return true;

        }

        boolean close() {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    bufferedWriter = null;
                    lastFileName = null;
                    logFile = null;
                }
            }
            return true;
        }

        void appendLog(String flattenedLog) {
            try {
                bufferedWriter.write(flattenedLog);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
