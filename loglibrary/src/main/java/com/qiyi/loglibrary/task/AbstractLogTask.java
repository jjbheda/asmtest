package com.qiyi.loglibrary.task;


/**
 * 日志Task类
 *
 */
public abstract class AbstractLogTask {
	public static final String TAG = "AbstractDBTask";
	int logLevel;
	String moduleName;
	Throwable tr;
	String msg = "";

	public AbstractLogTask(int logLevel, String moduleName, String msg, Throwable tr) {
		this.logLevel = logLevel;
		this.moduleName = moduleName;
		this.tr = tr;
		this.msg = (msg == null ? "" : msg);
	}

//	public AbstractLogTask(int logLevel, String moduleName, String msg) {
//		this.logLevel = logLevel;
//		this.moduleName = moduleName;
//		this.msg = msg;
//	}


	/**
	 * 线程调用异步方法
	 */
	public void process() {
		doInBackground();
	}
	
	/**
	 * 异步操作实现方法
	 */
	protected abstract void doInBackground();
}
