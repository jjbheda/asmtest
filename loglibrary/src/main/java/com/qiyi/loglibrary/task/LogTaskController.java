package com.qiyi.loglibrary.task;


import com.qiyi.loglibrary.LogConfiguration;
import com.qiyi.loglibrary.LogManager;
import com.qiyi.loglibrary.util.ExceptionUtils;

/**
 *  日志任务调度器，负责将日志调用API加入队列
 *  该类持有LogManager 对象，供AbstractLogTask调用
 *
 */
public class LogTaskController {

	private AsyncLogTaskQueue mDatabaseTaskQueue;
	static LogManager logManager;
	public LogTaskController(LogConfiguration logConfiguration) {
		logManager = new LogManager(logConfiguration);

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

	public static void v(String moduleName, String msg, Throwable tr) {
		if (tr == null)
			logManager.v(moduleName, msg);
		else
			logManager.v(moduleName, tr);
	}

	public static void d(String moduleName, String msg, Throwable tr) {
		if (tr == null)
			logManager.d(moduleName, msg);
		else
			logManager.d(moduleName, tr);
	}

	public static void i(String moduleName, String msg, Throwable tr) {
		if (tr == null)
			logManager.i(moduleName, msg);
		else
			logManager.i(moduleName, tr);
	}

	public static void w(String moduleName, String msg, Throwable tr) {
		if (tr == null)
			logManager.w(moduleName, msg);
		else
			logManager.w(moduleName, tr);
	}

	public static void e(String moduleName, String msg, Throwable tr) {
		if (tr == null)
			logManager.e(moduleName, msg);
		else
			logManager.e(moduleName, tr);
	}

}
