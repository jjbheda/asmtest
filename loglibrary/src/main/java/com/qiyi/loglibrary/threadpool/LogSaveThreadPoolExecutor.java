package com.qiyi.loglibrary.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LogSaveThreadPoolExecutor extends ThreadPoolExecutor {
//    public static final LogSaveThreadPoolExecutor LOG_SAVE_THREAD_POOL = createLogThreadPool();
//
//
//    public LogSaveThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
//        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
//    }
//
//    public LogSaveThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
//        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
//    }
//
//    private static synchronized LogSaveThreadPoolExecutor createLogThreadPool() {
//        LogSaveThreadPoolExecutor mExecutor = new LogSaveThreadPoolExecutor(0, Integer.MAX_VALUE,
//                60L, TimeUnit.SECONDS,
//                new SynchronousQueue<Runnable>(), new ThreadFactory() {
//            private final AtomicInteger mCount = new AtomicInteger(1);
//
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r, "LogThread#" + mCount.getAndIncrement());
////                return new Thread(r);
//            }
//        });
//        return mExecutor;
//    }

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
//    // We want at least 2 threads and at most 4 threads in the core pool,
//    // preferring to have 1 less than the CPU count to avoid saturating
//    // the CPU with background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 15;
//    public static final LogReadThreadPoolExecutor LOG_READ_THREAD_POOL = createLogThreadPool();

    public static final LogSaveThreadPoolExecutor THREAD_POOL_EXECUTOR;
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);


    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    static {
        LogSaveThreadPoolExecutor threadPoolExecutor = new LogSaveThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }


    public LogSaveThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                     TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public LogSaveThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                     ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

}
