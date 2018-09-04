package com.qiyi.loglibrary.util;

import android.content.Context;
import android.util.Log;

import com.qiyi.loglibrary.Constant;
import com.qiyi.loglibrary.LogEntity;
import com.qiyi.loglibrary.printer.FilePrinterWithPool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时器  每隔一段时间轮询一次
 */
public class PollingTimerUtil {

    private static Timer mTimer = new Timer();
    @Subscribe(threadMode = ThreadMode.MAIN,priority = 100)
    public void onMessageEvent(LogEntity event) {/* Do something */};
    public void begin() {
        Log.e("PollingTimerUtil" , "轮询启动");
        PollingTimer task = new PollingTimer();
        mTimer.schedule(task, Constant.POLLING_PERIOD, Constant.POLLING_PERIOD);
    }

   static class PollingTimer extends TimerTask {

        @Override
        public void run() {
            Log.e("PollingTimerUtil" , "轮询时间到了");
            Constant.IS_POLLING_TIME = true;
            EventBus.getDefault().post(new LogEntity(2, "", ""));
        }
    }

    public void exit() {
        Log.e("PollingTimerUtil" , "结束轮询启动");
        EventBus.getDefault().unregister(this);
    }


}
