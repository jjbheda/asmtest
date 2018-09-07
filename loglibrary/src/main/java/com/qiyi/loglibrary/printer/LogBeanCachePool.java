package com.qiyi.loglibrary.printer;

import android.util.Log;

import com.qiyi.loglibrary.Constant;
import com.qiyi.loglibrary.LogEntity;
import com.qiyi.loglibrary.LogStorer;
import com.qiyi.loglibrary.flattener.Flattener;
import com.qiyi.loglibrary.strategy.FileChecker;
import com.qiyi.loglibrary.strategy.LogLevel;
import com.qiyi.loglibrary.threadpool.LogSaveThreadPoolExecutor;
import com.qiyi.loglibrary.util.ExceptionUtils;
import com.qiyi.loglibrary.util.FileUtils;
import com.qiyi.loglibrary.util.LogFileUtil;
import com.qiyi.loglibrary.util.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class LogBeanCachePool {
    private boolean writtingFlag = false;
    private String filePath;
    private Flattener flattener;
    private String moduleName;
    private Vector<CacheLogBeanWrapper> beanArray = new Vector();
    private boolean isBaseSizeCheckPass = true;
    public CountDownLatch countDownLatch = new CountDownLatch(1);

    public LogBeanCachePool(Flattener flattener, String moduleName) {
        this.flattener = flattener;
        this.moduleName = moduleName;
    }

    public void recordLog(int logLevel, String msg) {

        if (!isBaseSizeCheckPass) {
            Log.e(Constant.ROOT_TAG, "Size 检查不通过，丢弃本次写操作!!!");
            return;
        }
        addBean(new LogEntity(logLevel, moduleName, msg));
        Log.e(Constant.ROOT_TAG,Constant.ROOT_TAG + "当前条目"+ getBeanArray().size());
        if (getBeanArray().size() < Constant.SINGLE_WIRTING_ITEMS) {
            Log.e(Constant.ROOT_TAG,Constant.ROOT_TAG + "当前条目"+ beanArray.size() +
                    ", 数目小于"+ Constant.SINGLE_WIRTING_ITEMS +"条，且未到轮询时间");
            return;
        }

        pushToThreadPool(false);

        try {
            Log.e(Constant.ROOT_TAG, "执行等待........");
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        countDownLatch = new CountDownLatch(1);
    }

    /**
     * @param isPolling  是否是轮询写入
     */
    public void pushToThreadPool(boolean isPolling) {
        if (isWritting()) {
            Log.e(Constant.ROOT_TAG,moduleName + "正在写入");
            return;
        }
        LogSaveThreadPoolExecutor.THREAD_POOL_EXECUTOR.submit(new Recorder(isPolling));
    }

    public synchronized Vector<CacheLogBeanWrapper> getBeanArray() {
            return beanArray;
    }

    public void addBean(LogEntity logEntity) {

        CacheLogBeanWrapper bean = new CacheLogBeanWrapper(logEntity.level, logEntity.moduleName, logEntity.msg, flattener);
        bean.setTime(TimeUtil.getFormateTimeStr(System.currentTimeMillis()));
        getBeanArray().add(bean);

    }

    public void clear() {
        getBeanArray().clear();
    }

    public synchronized void setFilePath(String mFilePath) {
        filePath = mFilePath;
    }

    public synchronized String getFilePath() {
        return filePath;
    }

    public void setIsWritting(boolean writtingFlag) {
        this.writtingFlag = writtingFlag;
    }

    public boolean isWritting() {
        return writtingFlag;
    }

    private class Recorder implements Runnable {
        String logMsg;
        boolean isPolling; //是否是轮询写入
        Recorder(boolean isPolling) {
            this.isPolling = isPolling;
        }

        @Override
        public void run() {
            try {

               LogFileUtil.removeOldDayDir();

                if (!FileChecker.baseSizeCheck(moduleName)) {
                    isBaseSizeCheckPass = false;
                    Log.e(Constant.ROOT_TAG,"isBaseSizeCheckPass == false");
                    countDownLatch.countDown();
                    return;
                }

                if(!"".equals(getLogMsg())) {
                    printToFile(getFilePath(), logMsg);
                    countDownLatch.countDown();
                }

            } catch (Exception e) {
                e.printStackTrace();
//                countDownLatch.countDown();
            }
        }

        String getLogMsg() {
            if (isWritting()) {
                Log.e(Constant.ROOT_TAG,moduleName + "正在写入");
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (CacheLogBeanWrapper logEntity : beanArray) {
                sb.append(logEntity.getFlatterMsg());
            }
            File file = FileChecker.getFile(moduleName, sb.toString().length());
            setFilePath(file.getAbsolutePath());

            logMsg = sb.toString();

            if (!FileChecker.check(moduleName, LogStorer.mBaseContext,logMsg.length())) {
                Log.e(Constant.ROOT_TAG, "写入检查不通过，丢弃本次写操作!!!");
                Constant.IS_POLLING_TIME = false;
                clear();
                countDownLatch.countDown();
                return "";
            }

            clear();
            Log.e(Constant.ROOT_TAG,"执行了清理工作........");

            Log.e(Constant.ROOT_TAG, Constant.ROOT_TAG +"开始写入，路径" + file.getAbsolutePath() + ", 写入内容" + logMsg);
            setIsWritting(true);
            return logMsg;
        }

        void printToFile(String file, String logMsg) {
            if (file == null) {
                Log.e(Constant.ROOT_TAG,moduleName + "写入文件发生异常");
                return;
            }
            boolean canWrite = FileChecker.checkFile(new File(file).getAbsolutePath(), logMsg.length());
            if (!canWrite) {
                Log.e(Constant.ROOT_TAG, Constant.ROOT_TAG +"检查不能写入");
                setIsWritting(false);
                return;
            }
            FileUtils.string2File(logMsg, file, true);
            setIsWritting(false);
        }
    }

}
