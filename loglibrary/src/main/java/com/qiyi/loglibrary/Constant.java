package com.qiyi.loglibrary;

public class Constant {

    public static String ROOT_DIR = "logstorer";

    public static String  ROOT_TAG = "LogStorer";

    public static boolean IS_POLLING_TIME = false;

    //轮询写入时间间隔
    public static long POLLING_PERIOD = 30 * 1000L;    //30s

    //单个TAG 最大文件大小 100M
    public static int DIR_MAX_FILE = 100 * 1024 * 1024 ;

    //单个文件最大  1M
    public static int SINGLE_FILE_MAX_LENGTH = 1024 * 1024;

    //当天tag文件夹最大  5M
    public static int TAG_MAX_LENGTH = 5 * 1024 * 1024;

    //批量写入条目
    public static int SINGLE_WIRTING_ITEMS = 5;


}
