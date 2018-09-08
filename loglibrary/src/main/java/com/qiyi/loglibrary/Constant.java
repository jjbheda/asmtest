package com.qiyi.loglibrary;

public class Constant {

    public static String ROOT_DIR = "logstorer";

    public static String  ROOT_TAG = "LogStorer";

    public static String  ROOT_THREAD_NAME = "LogStorerThread";

    //是否正在轮询
    public static boolean IS_POLLING_TIME = false;

    // 超期清理的日期间隔
    public static int INTERVAL_DAY = 2;

    //轮询写入时间间隔
    public static long POLLING_PERIOD = 30 * 1000L;    //30s

    //日志根目录文件夹大小
    public static int DIR_MAX_FILE = 100 * 1024 * 1024 ;

    //当天tag文件夹最大  5M
    public static int TAG_MAX_LENGTH = 20 * 1024 * 1024;

    //单个文件最大  1M
    public static int SINGLE_FILE_MAX_LENGTH = 1024 * 1024;

    //批量写入条目
    public static int SINGLE_WIRTING_ITEMS = 40;


}
