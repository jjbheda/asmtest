package com.qiyi.loglibrary.task;

import android.util.Log;

import com.qiyi.loglibrary.util.ExceptionUtils;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * 异步处理task队列，排队处理task
 *
 */
public class AsyncLogTaskQueue extends Thread {
    private Queue<AbstractLogTask> taskQueue = new LinkedList<AbstractLogTask>();
    private boolean isStop = false;

    public AsyncLogTaskQueue() {
        super("AsyncDBTaskQueue");
    }

    @Override
    public void run() {
        try {
            AbstractLogTask task = null;
            while (!isStop) {
                synchronized (taskQueue) {
                    if (taskQueue.isEmpty()) {
                        taskQueue.wait();
                        continue;
                    } else {
                        task = taskQueue.poll();
                        Thread trd = Thread.currentThread();
//                        Log.e("LogBeanCachePool","当前线程 取出" + task.moduleName +"  "  + task.msg +
//                                "》》》》》》》》----------" + trd.getId() + " " + trd.getId());
                    }
                }
                task.process();
            }
        } catch (InterruptedException e) {
            ExceptionUtils.printStackTrace(e);
            Thread.currentThread().interrupt();
        }
    }

    public void stopRun() {
        if (isAlive()) {
            isStop = true;
            this.stop();
        }
    }

    /**
     * 添加一个新任务到队列，并唤醒处理线程
     *
     * @param task
     */
    public void addTask(AbstractLogTask task) {
        synchronized (taskQueue) {
            Thread trd = Thread.currentThread();
            Log.e("LogBeanCachePool","添加task,"+task.moduleName + "---------------------------- 当前线程->" + trd.getId());
            taskQueue.offer(task);
            taskQueue.notifyAll();
        }
    }
}
