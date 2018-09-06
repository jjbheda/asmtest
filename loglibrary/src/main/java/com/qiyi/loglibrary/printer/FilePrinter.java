package com.qiyi.loglibrary.printer;

import android.util.Log;

import com.qiyi.loglibrary.Constant;
import com.qiyi.loglibrary.LogEntity;
import com.qiyi.loglibrary.LogStorer;
import com.qiyi.loglibrary.flattener.Flattener;
import com.qiyi.loglibrary.printer.backup.BackupStrategy;
import com.qiyi.loglibrary.printer.naming.FileNameGenerator;
import com.qiyi.loglibrary.threadpool.LogSaveThreadPoolExecutor;
import com.qiyi.loglibrary.util.DefaultsFactory;
import com.qiyi.loglibrary.strategy.FileChecker;
import com.qiyi.loglibrary.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class FilePrinter implements Printer {
    private static String TAG = "FilePrinter";
    private BackupStrategy backupStrategy;
    private Flattener flattener;
//    LogFileWriter writer;   //执行IO

    public static LinkedHashMap<String,CacheLogBeanPool> cacheMap = new LinkedHashMap<>();

    public FilePrinter(Builder builder) {
        backupStrategy = builder.backupStrategy;
        flattener = builder.flattener;
//        writer = new LogFileWriter();
    }

    @Override
    public synchronized void println(int logLevel, String mouduleName, String msg) {
        if (!cacheMap.containsKey(mouduleName)) {
            CacheLogBeanPool cacheLogpool = new CacheLogBeanPool(flattener, mouduleName);
            cacheMap.put(mouduleName,cacheLogpool);
        }
        CacheLogBeanPool cacheLogpool = cacheMap.get(mouduleName);
        cacheLogpool.addBean(new LogEntity(logLevel, mouduleName, msg));
        LogSaveThreadPoolExecutor.LOG_SAVE_THREAD_POOL.submit(new Recorder(cacheLogpool, false));
    }

    @Override
    public void println() {
        Iterator iter = cacheMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            CacheLogBeanPool cacheLogBeanPool = (CacheLogBeanPool)entry.getValue();
            if (cacheLogBeanPool.beanArray.size() == 0) {
                Log.e(TAG, "本次轮询未发现要写入的文件");
                return;
            }
            LogSaveThreadPoolExecutor.LOG_SAVE_THREAD_POOL.submit(new Recorder(cacheLogBeanPool, true));
        }
    }

    public static class Builder {
        FileNameGenerator fileNameGenerator;
        BackupStrategy backupStrategy;
        Flattener flattener;

        public Builder() {
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
            if (backupStrategy == null) {
                backupStrategy = DefaultsFactory.createBackupStrategy();
            }

            if (flattener == null) {
                flattener = DefaultsFactory.createFlattener();
            }
        }

    }

    private class Recorder implements Runnable {
        String logMsg;
        CacheLogBeanPool cacheLogpool;
        boolean isPolling; //是否是轮询写入
        Recorder(CacheLogBeanPool cacheLogpool, boolean isPolling) {
            this.cacheLogpool = cacheLogpool;
            this.isPolling = isPolling;
        }

        @Override
        public void run() {
            try {
                if(!"".equals(getLogMsg())) {
                    printToFile(cacheLogpool.getFilePath(), logMsg);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String getLogMsg() {

            if (cacheLogpool.isWritting()) {
                Log.e(TAG,cacheLogpool.getModuleName() + "正在写入");
                return "";
            }

            if (!isPolling && cacheLogpool.beanArray.size() < Constant.SINGLE_WIRTING_ITEMS) {
                Log.e(TAG,TAG + "当前条目"+ cacheLogpool.beanArray.size() +
                        ", 数目小于"+ Constant.SINGLE_WIRTING_ITEMS +"条，且未到轮询时间");
                return "";
            }

            //满40条写入，如果此时有相同TAG的log请求，暂时的处理方案是先丢弃掉这部分日志
            // 或者定期轮询 30分钟 统一的写入
            StringBuilder sb = new StringBuilder();
            File file = FileChecker.getFile(cacheLogpool.getModuleName(), sb.toString().length());
            cacheLogpool.setFilePath(file.getAbsolutePath());

            for (CacheLogBeanWrapper logEntity : cacheLogpool.beanArray) {
                sb.append(logEntity.getFlatterMsg());
            }
            logMsg = sb.toString();

            if (!FileChecker.check(cacheLogpool.getModuleName(), LogStorer.mBaseContext,logMsg.length())) {
                Log.e(TAG, "写入检查不通过，丢弃本次写操作!!!");
                Constant.IS_POLLING_TIME = false;
                cacheLogpool.beanArray.clear();
                return "";
            }

            cacheLogpool.beanArray.clear();

            if (file == null) {
                Log.e(TAG,"获取可写入文件失败，丢弃本次写操作!!!");
                return "";
            }

            Log.e(TAG, TAG +"开始写入，路径" + file.getAbsolutePath() + ", 写入内容" + logMsg);
            cacheLogpool.setIsWritting(true);
            return logMsg;
        }


        void printToFile(String file, String logMsg) {
            if (file == null) {
                Log.e(TAG, file + "== mull");
                return;
            }
            FileUtils.string2File(logMsg, file, true);

//            String lastFileName = writer.getLastFileName();
//            if (lastFileName == null ) {
//
//                if (writer.isOpened()) {
//                    writer.close();
//                }
//                if (!writer.open(file)) {
//                    return;
//                }
//            }
//
//            File lastFile = writer.getFile();
//            if (backupStrategy.shouldBackup(lastFile)) {
//                // Backup the log file, and create a new log file.
//                writer.close();
//
//                File backupFile = new File(writer.logFile.getParent(), lastFileName + ".bak");
//                if (backupFile.exists()) {
//                    backupFile.delete();
//                }
//                lastFile.renameTo(backupFile);
//                if (!writer.open(lastFileName)) {
//                    return;
//                }
//            }
//            writer.appendLog(logMsg);
//            writer.close();
            cacheLogpool.setIsWritting(false);

        }
    }

//    private class LogFileWriter {
//        private String lastFileName;
//        private File logFile;
//        private BufferedWriter bufferedWriter;
//
//        boolean isOpened() {
//            return bufferedWriter != null;
//        }
//
//        String getLastFileName() {
//            return lastFileName;
//        }
//
//        File getFile() {
//            return logFile;
//        }
//
//        boolean open(String filePath) {
//            logFile = new File(filePath);
//
//            if (!logFile.exists()) {
//              Log.e(TAG, "打开文件"+ filePath + "失败");
//              return false;
//            }
//            lastFileName = logFile.getName();
//
//            try {
//                bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
//            } catch (IOException e) {
//                e.printStackTrace();
//                lastFileName = null;
//                logFile = null;
//                return false;
//            }
//            return true;
//
//        }
//
//        boolean close() {
//            if (bufferedWriter != null) {
//                try {
//                    bufferedWriter.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return false;
//                } finally {
//                    bufferedWriter = null;
//                    lastFileName = null;
//                    logFile = null;
//                }
//            }
//            return true;
//        }
//
//        void appendLog(String flattenedLog) {
//            try {
//                bufferedWriter.write(flattenedLog);
//                bufferedWriter.newLine();
//                bufferedWriter.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

//    }


}
