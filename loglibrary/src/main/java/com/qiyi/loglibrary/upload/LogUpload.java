package com.qiyi.loglibrary.upload;

import android.util.Log;

import com.qiyi.loglibrary.Constant;
import com.qiyi.loglibrary.threadpool.LogSaveThreadPoolExecutor;
import com.qiyi.loglibrary.util.LogFileUtil;

import java.io.File;

public class LogUpload {

    public static void getLog(String moudleName, String date) {
        LogSaveThreadPoolExecutor.THREAD_POOL_EXECUTOR.submit(new Reader(moudleName, date));
    }

    private static class Reader implements Runnable {
        String moudleName;
        String date;

        Reader(String moudleName, String date) {
            this.moudleName = moudleName;
            this.date = date;
        }

        @Override
        public void run() {
            try {
                File[] fileList = LogFileUtil.readDir(moudleName,date);
                if (fileList != null) {
                   for (File file: fileList) {
                       if (file.getName().endsWith(".log")) {
                           String sb = LogFileUtil.readLog(file);
                           Thread.sleep(500);

                            //todo  调上传接口
                           Log.e(Constant.ROOT_TAG, "调上传接口，上传数据" + sb);
                       }
                   }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
