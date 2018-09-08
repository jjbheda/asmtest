package com.qiyi.loglibrary.task;


import com.qiyi.loglibrary.util.ExceptionUtils;

/**
 * 日志任务调度器，负责将日志调用API加入队列
 *
 */
public class LogTaskController {

	private AsyncLogTaskQueue mDatabaseTaskQueue;

	public LogTaskController() {
		try {
			mDatabaseTaskQueue = new AsyncLogTaskQueue();
		} catch (Exception e){
			ExceptionUtils.printStackTrace(e);
			mDatabaseTaskQueue = null;
		}
	}

	public void init() {
		try {
			if(mDatabaseTaskQueue != null) {
				mDatabaseTaskQueue.start();
			}
		} catch (Exception e){
			ExceptionUtils.printStackTrace(e);
		}
	}

	/**
	 * 添加日志打印任务，例如Logstorer.e
	 * @param task
	 */
	public void addLogTask(AbstractLogTask task) {
		if(mDatabaseTaskQueue != null) {
			mDatabaseTaskQueue.addTask(task);
		}
	}

}
