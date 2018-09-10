package com.qiyi.loglibrary.printer;

import android.util.Log;

import com.qiyi.loglibrary.Constant;
import com.qiyi.loglibrary.LogEntity;
import com.qiyi.loglibrary.LogStorer;
import com.qiyi.loglibrary.flattener.Flattener;
import com.qiyi.loglibrary.strategy.FileChecker;
import com.qiyi.loglibrary.threadpool.LogSaveThreadPoolExecutor;
import com.qiyi.loglibrary.util.FileUtils;
import com.qiyi.loglibrary.util.TimeUtil;

import java.io.File;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LogBeanCachePool {
    private boolean writtingFlag = false;
    private String filePath;
    private Flattener flattener;
    private String moduleName;
    private Vector<LogBeanWrapper> beanArray = new Vector<>();
    private boolean isBaseSizeCheckPass = true;
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
        Log.e(Constant.ROOT_TAG, Constant.ROOT_TAG + "当前条目" + getBeanArray().size());
        if (getBeanArray().size() < Constant.SINGLE_WIRTING_ITEMS) {
            Log.e(Constant.ROOT_TAG, Constant.ROOT_TAG + "当前条目" + beanArray.size() +
                    ", 数目小于" + Constant.SINGLE_WIRTING_ITEMS + "条，且未到轮询时间");
            return;
        }

        runWithCallable();
    }

    class Worker implements Callable<Boolean> {

        @Override
        public Boolean call() {
            try {
                String msg = getLogMsg();
                printToFile(getFilePath(), msg);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    /**
     * 超时监控
     */
    private void runWithCallable() {
        Future<Boolean> future = LogSaveThreadPoolExecutor.THREAD_POOL_EXECUTOR.submit(new Worker());
        try {
            future.get(Constant.LOG_PATCH_TASK_TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Log.e(Constant.ROOT_TAG, "TimeoutException准备移除task" + e);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Constant.ROOT_TAG, "Exception准备移除task" + e.getMessage());
        } finally {
            reset();
//            Log.e(Constant.ROOT_TAG, "本次执行完成");
        }
    }

    /**
     * @param isPolling 是否是轮询写入
     */
    public void pushToThreadPool(boolean isPolling) {
        if (isPolling) {    // 轮询时间
            if (isWritting()) {     //如果正在写入
                Log.e(Constant.ROOT_TAG, "轮询时，发现" + moduleName + "正在写入");
                return;
            }
        }

        runWithCallable();
    }

    public synchronized Vector<LogBeanWrapper> getBeanArray() {
        return beanArray;
    }

    public void addBean(LogEntity logEntity) {

        LogBeanWrapper bean = new LogBeanWrapper(logEntity.level, logEntity.moduleName,
                logEntity.msg, logEntity.isThrowable, flattener);
        bean.setTime(TimeUtil.getFormateTimeStr(System.currentTimeMillis()));
        getBeanArray().add(bean);

    }

    public void clear() {
        getBeanArray().clear();
        Log.e(Constant.ROOT_TAG, "执行CLear......................................");
    }

    public synchronized void setFilePath(String mFilePath) {
        filePath = mFilePath;
    }

    public synchronized String getFilePath() {
        return filePath;
    }

    public synchronized void setIsWritting(boolean writtingFlag) {
        synchronized (lock) {
            this.writtingFlag = writtingFlag;
        }

    }

    public synchronized boolean isWritting() {
        synchronized (lock) {
            return writtingFlag;
        }
    }

    String getLogMsg() {
        StringBuilder sb = new StringBuilder();

        String logMsg = "";
        Vector<String> throwableBeanArray = new Vector<>();

        for (LogBeanWrapper logEntity : beanArray) {

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
        logMsg = sb.toString();
        if (logMsg.equals("")) {
            Log.e(Constant.ROOT_TAG, "本次写入数据为空!!!");
            reset();
            return "";
        }

        File file = FileChecker.getFile(moduleName, sb.toString().length());
        if (file == null) {
            Log.e(Constant.ROOT_TAG, "获取写文件位置失败，丢弃本次写操作!!！");
            reset();
            return "";
        }

        setFilePath(file.getAbsolutePath());


        if (!FileChecker.check(moduleName, LogStorer.mBaseContext, logMsg.length())) {
            Log.e(Constant.ROOT_TAG, "写入检查不通过，丢弃本次写操作!!!");
            reset();
            return "";
        }
        Log.e(Constant.ROOT_TAG, Constant.ROOT_TAG + "当前线程,开始写入，路径" + file.getAbsolutePath() + ", 写入内容" + logMsg);
        return logMsg;
    }

    private void printToFile(String file, String logMsg) {
        if (file == null) {
            Log.e(Constant.ROOT_TAG, moduleName + "写入文件发生异常");
            reset();
            return;
        }
        boolean canWrite = FileChecker.checkFile(new File(file).getAbsolutePath(), logMsg.length());
        if (!canWrite) {
            Log.e(Constant.ROOT_TAG, Constant.ROOT_TAG + "检查不能写入");
            reset();
            return;
        }

        FileUtils.string2File(logMsg, file, true);
        reset();

    }

    private void reset() {
        Constant.IS_POLLING_TIME = false;
        clear();
//        countDownLatch.countDown();
        setIsWritting(false);
    }

}
