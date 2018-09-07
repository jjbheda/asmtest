package com.qiyi.loglibrary.strategy;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.qiyi.loglibrary.Constant;
import com.qiyi.loglibrary.LogStorer;
import com.qiyi.loglibrary.menu.DateDirGenerator;
import com.qiyi.loglibrary.util.FileUtils;
import com.qiyi.loglibrary.util.LogFileUtil;
import com.qiyi.loglibrary.util.StringUtils;
import com.qiyi.loglibrary.util.TimeUtil;

import java.io.File;
import java.io.IOException;

public class FileChecker {

   public static void removeOldDayDir() {
      LogFileUtil.removeOldDayDir();
   }

    /**
     * 基本大小检查，不检查权限
     * @param tag
     * @return
     */
    public static boolean baseSizeCheck(String tag) {

        File rootPath = new File(Environment.getExternalStorageDirectory(), Constant.ROOT_DIR);

        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }
        if (!isFolderSizelegal(rootPath.getAbsolutePath())) {
            Log.e(Constant.ROOT_TAG, "日志总文件大小超过" + (Constant.DIR_MAX_FILE / 1024 / 1024) + "M");
            return false;
        }

        File tagDirWithDate = new File(rootPath + File.separator +
                DateDirGenerator.generateDateDir(System.currentTimeMillis()) + File.separator + tag);

        if (!tagDirWithDate.exists()) {
            tagDirWithDate.mkdirs();
        }
        if (!isTagFolderlegal(tagDirWithDate.getAbsolutePath())) {
            Log.e(Constant.ROOT_TAG, tagDirWithDate + "日志文件大小超过" + (Constant.TAG_MAX_LENGTH / 1024 / 1024) + "M");
            return false;
        }

        return true;
    }


    public static boolean check(String tag, Context context , long contentLength) {

        if (contentLength >= Constant.SINGLE_FILE_MAX_LENGTH) {
            Log.e(Constant.ROOT_TAG, tag + "本次写操作文件长度超过" + (Constant.SINGLE_FILE_MAX_LENGTH / 1024 / 1024) + "M");
            return false;
        }

        if (!hasExternalStoragePermission(context)){
            Log.e(Constant.ROOT_TAG, "未获取存储权限");
            return false;
        }
        File rootPath = new File(Environment.getExternalStorageDirectory(), Constant.ROOT_DIR);

        if (!rootPath.exists()) {
           rootPath.mkdirs();
        }
        if (!isFolderSizelegal(rootPath.getAbsolutePath())) {
            Log.e(Constant.ROOT_TAG, "日志总文件大小超过" + (Constant.DIR_MAX_FILE / 1024 / 1024) + "M");
            return false;
        }

        File tagDir = new File(rootPath + File.separator + DateDirGenerator.
                generateDateDir(System.currentTimeMillis()) + File.separator + tag);

        if (!tagDir.exists()) {
            tagDir.mkdirs();
        }
        if (!isTagFolderlegal(tagDir.getAbsolutePath())) {
            Log.e(Constant.ROOT_TAG, tag + "日志文件大小超过" + (Constant.TAG_MAX_LENGTH / 1024 / 1024) + "M");
        }

        return true;
    }

    public static boolean checkFile(String filePath, long contentLength) {
        Context context = LogStorer.mBaseContext;
        if (!hasExternalStoragePermission(context)){
            Log.e(Constant.ROOT_TAG, "未获取存储权限");
            return false;
        }
        if (!isFolderSizelegal(Constant.ROOT_DIR)) {
            Log.e(Constant.ROOT_TAG, "日志总文件大小超过" + (Constant.DIR_MAX_FILE / 1024 / 1024) + "M");
            return false;
        }

        if (!isFilelegal(filePath)) {
            Log.e(Constant.ROOT_TAG, filePath + "日志文件大小超过" + (Constant.TAG_MAX_LENGTH / 1024 / 1024) + "M");
            return false;
        }

        if (contentLength >= Constant.SINGLE_FILE_MAX_LENGTH) {
            Log.e(Constant.ROOT_TAG, filePath + "本次写操作文件长度超过" + (Constant.SINGLE_FILE_MAX_LENGTH / 1024 / 1024) + "M");
            return false;
        }

        return true;
    }

    /**
     *
     * @param tag
     * @return
     *
     */
    public static File getFile(String tag, long contentLength) {
        if (StringUtils.isEmpty(tag)) {
            return null;
        }

        String dateDir = LogFileUtil.getDateDirFilePath();
        File dateFile = new File(dateDir);

        if (!dateFile.exists()) {
             dateFile.mkdirs();
        }

        File tagDir = new File(dateDir + File.separator + tag);
        if (!tagDir.exists()) {
            tagDir.mkdirs();
        }

        File fList[] = tagDir.listFiles();
        String fileName = "";

        if (fList == null || fList.length == 0) {
            fileName = tag + "1" + ".log";
        } else {
            int N = fList.length;

            if (canWriteToFile(contentLength, fList[N - 1].getAbsolutePath() )) {
                fileName = tag + (N) + ".log";
            } else {
                fileName = tag + (N + 1) + ".log";
            }

        }

        File singleLogFile = new File(tagDir,fileName);
        if (!singleLogFile.exists()) {
            try {
                singleLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return singleLogFile;

    }

    /**
     * 判断文件夹总大小是否超过100M
     * @param dir 目录名
     */
    public static boolean isFolderSizelegal(String dir) {
      return LogFileUtil.getFileSizes(dir) <= Constant.DIR_MAX_FILE;
    }

    /**
     * 判断文件夹当天 该tag文件夹 总大小不大于5M
     * @param tag 业务名称如，Passport
     */
    public static boolean isTagFolderlegal(String tag) {
        return LogFileUtil.getFileSizes(tag) <= Constant.TAG_MAX_LENGTH;
    }

    public static boolean isFilelegal(String filepath) {
        return LogFileUtil.getFileSize(filepath) <= Constant.TAG_MAX_LENGTH;
    }

    /**
     * 写入后单个文件大小不大于1M
     * @param singleFile 业务名称如，Passport
     */
    public static boolean canWriteToFile(long contentLength, String singleFile) {
        return contentLength + LogFileUtil.getFileSize(singleFile) <= Constant.SINGLE_FILE_MAX_LENGTH;
    }

    /**
     * 检查是否已经开启外部存储读写权限
     */
    private static boolean hasExternalStoragePermission(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (context == null) {
            return false;
        }

        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


}
