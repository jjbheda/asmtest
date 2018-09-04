package com.qiyi.loglibrary.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.qiyi.loglibrary.Constant;
import com.qiyi.loglibrary.printer.AndroidPrinter;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileChecker {

    private static String TAG = "FileChecker";
    //单个TAG 最大文件大小 100M
    public static int DIR_MAX_FILE = 100 * 1024 * 1024 ;

    //单个文件最大  1M
    public static int SINGLE_FILE_MAX_LENGTH = 1024 * 1024;

    //当天tag文件夹最大  5M
    public static int TAG_MAX_LENGTH = 5 * 1024 * 1024;

    public static boolean check(String tag, Context context , long contentLength) {

        if (!hasExternalStoragePermission(context)){
            Log.e(TAG, "未获取存储权限");
            return false;
        }
        if (!isFolderSizelegal(Constant.ROOT_DIR)) {
            Log.e(TAG, "日志总文件大小超过" + (DIR_MAX_FILE / 1024 / 1024) + "M");
            return false;
        }

        if (!isTagFolderlegal(tag)) {
            Log.e(TAG, tag + "日志文件大小超过" + (TAG_MAX_LENGTH / 1024 / 1024) + "M");
            return false;
        }

        if (contentLength >= SINGLE_FILE_MAX_LENGTH) {
            Log.e(TAG, tag + "本次写操作文件长度超过" + (SINGLE_FILE_MAX_LENGTH / 1024 / 1024) + "M");
            return false;
        }

        return true;
    }



    public static boolean checkFile(String filePath, Context context , long contentLength) {

        if (!hasExternalStoragePermission(context)){
            Log.e(TAG, "未获取存储权限");
            return false;
        }
        if (!isFolderSizelegal(Constant.ROOT_DIR)) {
            Log.e(TAG, "日志总文件大小超过" + (DIR_MAX_FILE / 1024 / 1024) + "M");
            return false;
        }

        if (!isFilelegal(filePath)) {
            Log.e(TAG, filePath + "日志文件大小超过" + (TAG_MAX_LENGTH / 1024 / 1024) + "M");
            return false;
        }

        if (contentLength >= SINGLE_FILE_MAX_LENGTH) {
            Log.e(TAG, filePath + "本次写操作文件长度超过" + (SINGLE_FILE_MAX_LENGTH / 1024 / 1024) + "M");
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

        String dateDir = FileUitl.getDateDirFilePath();
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
      return FileUitl.getFileSizes(dir) <= DIR_MAX_FILE;
    }

    /**
     * 判断文件夹当天 该tag文件夹 总大小不大于5M
     * @param tag 业务名称如，Passport
     */
    public static boolean isTagFolderlegal(String tag) {
        return FileUitl.getFileSizes(tag) <= TAG_MAX_LENGTH;
    }

    public static boolean isFilelegal(String filepath) {
        return FileUitl.getFileSize(filepath) <= TAG_MAX_LENGTH;
    }

    /**
     * 写入后单个文件大小不大于1M
     * @param singleFile 业务名称如，Passport
     */
    public static boolean canWriteToFile(long contentLength, String singleFile) {
        return contentLength + FileUitl.getFileSize(singleFile) <= SINGLE_FILE_MAX_LENGTH;
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
