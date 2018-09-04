package com.qiyi.loglibrary.util;

import android.os.Environment;
import android.util.Log;

import com.qiyi.loglibrary.Constant;
import com.qiyi.loglibrary.menu.DateDirGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUitl {

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

}
