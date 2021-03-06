package com.qiyi.loglibrary.task;

import android.util.Log;

import com.qiyi.loglibrary.util.ExceptionUtils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * 异步处理task队列，排队处理task
 *
 */
public class AsyncLogTaskQueue extends Thread {
    private Queue<AbstractLogTask> taskQueue = new ConcurrentLinkedQueue<AbstractLogTask>();
    private boolean isStop = false;

    public AsyncLogTaskQueue() {
        super("PassPort");
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
            taskQueue.offer(task);
            taskQueue.notifyAll();
        }
    }
}
