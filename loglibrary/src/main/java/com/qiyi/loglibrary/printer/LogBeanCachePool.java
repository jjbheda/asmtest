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
//    private boolean writtingFlag = false;
    private String filePath;
    private Flattener flattener;
    private String moduleName;
    private Vector<CacheLogBeanWrapper> beanArray = new Vector<>();
    private boolean isBaseSizeCheckPass = true;
    public CountDownLatch countDownLatch = new CountDownLatch(1);

    private Object lock = new Object();

    public LogBeanCachePool(Flattener flattener, String moduleName) {
        this.flattener = flattener;
        this.moduleName = moduleName;
    }

    public void recordLog(int logLevel, String msg, boolean isThrowable) {

        if (!isBaseSizeCheckPass) {
            Log.e(Constant.ROOT_TAG, "基础Size 检查不通过，丢弃本次写操作!!!");
            return;
        }
        addBean(new LogEntity(logLevel, moduleName, msg, isThrowable));
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
        LogSaveThreadPoolExecutor.THREAD_POOL_EXECUTOR.submit(new Recorder(isPolling));
    }

    public synchronized Vector<CacheLogBeanWrapper> getBeanArray() {
            return beanArray;
    }

    public void addBean(LogEntity logEntity) {

        CacheLogBeanWrapper bean = new CacheLogBeanWrapper(logEntity.level, logEntity.moduleName,
                logEntity.msg, logEntity.isThrowable, flattener);
        bean.setTime(TimeUtil.getFormateTimeStr(System.currentTimeMillis()));
        getBeanArray().add(bean);

    }

    public void clear() {
        getBeanArray().clear();
        Log.e(Constant.ROOT_TAG,"执行CLear......................................");
    }

    public synchronized void setFilePath(String mFilePath) {
        filePath = mFilePath;
    }

    public synchronized String getFilePath() {
        return filePath;
    }

//    public synchronized void setIsWritting(boolean writtingFlag) {
//        synchronized (lock) {
//            this.writtingFlag = writtingFlag;
//        }
//
//    }

//    public  synchronized boolean isWritting() {
//        synchronized (lock) {
//            return writtingFlag;
//        }
//    }

    private class Recorder implements Runnable {
        String logMsg;
        boolean isPolling; //是否是轮询写入
        Recorder(boolean isPolling) {
            this.isPolling = isPolling;
        }

        @Override
        public void run() {
            try {
                if (!FileChecker.baseSizeCheck(moduleName)) {
                    isBaseSizeCheckPass = false;
                    Log.e(Constant.ROOT_TAG,"isBaseSizeCheckPass == false");
                    reset();
                    return;
                }

//                if (isWritting()) {
//                    Log.e(Constant.ROOT_TAG,moduleName + "正在写入");
//                    return;
//                }

                if(!"".equals(getLogMsg())) {
//                    setIsWritting(true);
                    printToFile(getFilePath(), logMsg);
                }

            } catch (Exception e) {
                e.printStackTrace();
//                countDownLatch.countDown();
            }
        }

        String getLogMsg() {
            StringBuilder sb = new StringBuilder();
            Vector<String> throwableBeanArray = new Vector<>();

            for (CacheLogBeanWrapper logEntity : beanArray) {

                if (logEntity.isThrowable) {
                    String msg = logEntity.getCheckFlatterMsg();
                    if (!throwableBeanArray.contains(msg)) {
                        throwableBeanArray.add(msg);
                        sb.append(logEntity.getFlatterMsg());
                    }

                } else {
                    sb.append(logEntity.getFlatterMsg());
                }

            }
            File file = FileChecker.getFile(moduleName, sb.toString().length());
            if (file == null) {
                Log.e(Constant.ROOT_TAG, "获取写文件位置失败，丢弃本次写操作!!！");
                reset();
                return "";
            }

            setFilePath(file.getAbsolutePath());

            logMsg = sb.toString();

//            if (logMsg.equals("")) {
//                Log.e(Constant.ROOT_TAG, "本次写入数据为空!!!");
//                reset();
//                return "";
//            }
            if (!FileChecker.check(moduleName, LogStorer.mBaseContext,logMsg.length())) {
                Log.e(Constant.ROOT_TAG, "写入检查不通过，丢弃本次写操作!!!");
                reset();
                return "";
            }
            Log.e(Constant.ROOT_TAG, Constant.ROOT_TAG +"开始写入，路径" + file.getAbsolutePath() + ", 写入内容" + logMsg);
            return logMsg;
        }

        void printToFile(String file, String logMsg) {
            if (file == null) {
                Log.e(Constant.ROOT_TAG,moduleName + "写入文件发生异常");
                reset();
                return;
            }
            boolean canWrite = FileChecker.checkFile(new File(file).getAbsolutePath(), logMsg.length());
            if (!canWrite) {
                Log.e(Constant.ROOT_TAG, Constant.ROOT_TAG +"检查不能写入");
                reset();
                return;
            }
            FileUtils.string2File(logMsg, file, true);
            reset();
        }
    }

    private void reset() {
        Constant.IS_POLLING_TIME = false;
        clear();
        countDownLatch.countDown();
//        setIsWritting(false);
    }

}
