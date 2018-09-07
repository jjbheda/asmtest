package com.qiyi.loglibrary.printer;

import android.util.Log;

import com.qiyi.loglibrary.flattener.Flattener;
import com.qiyi.loglibrary.printer.backup.BackupStrategy;
import com.qiyi.loglibrary.printer.naming.FileNameGenerator;
import com.qiyi.loglibrary.strategy.FileChecker;
import com.qiyi.loglibrary.util.DefaultsFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FilePrinter implements Printer {
    private static String TAG = "FilePrinter";
    private Flattener flattener;
    private boolean clearOldDir = false;

    public static ConcurrentHashMap<String,LogBeanCachePool> cacheMap = new ConcurrentHashMap<>();

    public FilePrinter(Builder builder) {
        flattener = builder.flattener;
    }

    @Override
    public synchronized void println(int logLevel, String mouduleName, String msg, boolean isThrowable) {
        Log.e(TAG,TAG + "新的打印请求" + mouduleName + "__" + msg);

        if (!clearOldDir) {
            FileChecker.removeOldDayDir();
            Log.e(TAG,"删除了旧文件");
            clearOldDir = true;
        }

        if (!cacheMap.containsKey(mouduleName)) {
            LogBeanCachePool cacheLogpool = new LogBeanCachePool(flattener, mouduleName);
            cacheMap.put(mouduleName,cacheLogpool);
        }
        LogBeanCachePool cacheLogpool = cacheMap.get(mouduleName);
        cacheLogpool.recordLog(logLevel, msg);
    }

    @Override
    public void println() {
        Iterator iter = cacheMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            LogBeanCachePool cacheLogBeanPool = (LogBeanCachePool)entry.getValue();
            if (cacheLogBeanPool.getBeanArray().size() == 0) {
                Log.e(TAG, "本次轮询未发现要写入的文件");
                return;
            }
            cacheLogBeanPool.pushToThreadPool(true);
//            LogSaveThreadPoolExecutor.LOG_SAVE_THREAD_POOL.submit(new Recorder(cacheLogBeanPool, true));
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

}
