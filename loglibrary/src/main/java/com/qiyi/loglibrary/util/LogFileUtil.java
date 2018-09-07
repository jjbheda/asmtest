package com.qiyi.loglibrary.util;

import android.os.Environment;
import android.util.Log;

import com.qiyi.loglibrary.Constant;
import com.qiyi.loglibrary.menu.DateDirGenerator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogFileUtil {
    private static String TAG = "LogFileUtil";
    /**
     * 获取指定文件夹
     * @param dir 要扫描的文件夹
     * @return
     * @throws Exception
     */
    public static long getFileSizes(String dir) {
        long size = 0;
        if (StringUtils.isEmpty(dir)) {
            return size;
        }

        File file = new File(dir);

        if (!file.exists()) {
            return size;
        }
        File fList[] = file.listFiles();
        if (fList == null || fList.length == 0) {
            return size;
        }

        for (int i = 0; i < fList.length; i++) {
            if (fList[i].isDirectory()) {
                size = size + getFileSizes(fList[i].getAbsolutePath());
            } else {
                size =size + getFileSize(fList[i].getAbsolutePath());
            }
        }
        return size;
    }

    /**
     * 获取指定文件大小
     * @param filePath
     * @return
     * @throws Exception
     */
    public static long getFileSize(String filePath) {
        long size = 0;
        if (StringUtils.isEmpty(filePath)) {
            return size;
        }
        File file = new File(filePath);
        if (file.exists()) {
            size = file.length();
        }
        return size;
    }

    public static String getDateDirFilePath() {
        File rootPath = new File(Environment.getExternalStorageDirectory(), Constant.ROOT_DIR);
        if (!rootPath.exists()) {
            rootPath.mkdir();
        }

        File dateDir = new File(rootPath, DateDirGenerator.generateDateDir(System.currentTimeMillis()));
        if (!dateDir.exists()) {
            dateDir.mkdir();
        }
        return dateDir.getPath();
    }

    /**
     *
     * @param moduleName 标签名，如 FW
     * @param date 如2018-09-07
     * @return
     */
    public static File[] readDir(String moduleName, String date) {
        File rootPath = new File(Environment.getExternalStorageDirectory(), Constant.ROOT_DIR);
        if (!rootPath.exists()) {
            Log.e(TAG, "日志总目录不存在!");
            return null;
        }

        File dateDir = new File(rootPath, date);
        if (!dateDir.exists()) {
            Log.e(TAG, date + "无日志文件");
            return null;
        }

        File moduleDir = new File(dateDir, moduleName);
        if (!moduleDir.exists()) {
            Log.e(TAG, date + "/" + moduleDir + "目录不存在");
            return null;
        }

        File[] logFileList = moduleDir.listFiles();
        if (logFileList == null || logFileList.length == 0) {
            Log.e(TAG, date + "/" + moduleDir + "文件夹是空的");
            return null;
        }

        return logFileList;
    }

    /**
     * 获取总目录下，日期目录文件
     *
     * @return
     */
    public static void removeOldDayDir() {
        File rootPath = new File(Environment.getExternalStorageDirectory(), Constant.ROOT_DIR);
        if (!rootPath.exists()) {
            Log.e(TAG, "日志总目录不存在!");
            return;
        }

        File[] logFileList = rootPath.listFiles();
        if (logFileList == null || logFileList.length == 0) {
            Log.e(TAG,  "日志总目录,文件夹是空的");
            return;
        }

        for (File file : logFileList) {
            String dateName = file.getName();
            Date date = LogDateUtils.getDate(dateName);
            if (date != null) {
                if (LogDateUtils.isOverTime(new Date(), date, Constant.INTERVAL_DAY)) {
                    FileUtils.deleteFiles(file);
                    Log.e(TAG,  "删除文件夹: " + file.getAbsolutePath());
                }
            }
        }
    }


    /**
     * 未避免产生性能问题，读取日志操作应该放在专门的线程池中
     * @param file 单个日志文件
     * @return
     */

    public static String readLog(File file) {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.file2String(file));
        return sb.toString();
    }

    /**
     * 判断是否有需要删除的文件,规则：超过5天的文件会被删除掉
     *
     */

    public void rmOldDir(String tag, Date date) {


    }

}
