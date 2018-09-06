package com.qiyi.loglibrary.printer;

import com.qiyi.loglibrary.LogEntity;
import com.qiyi.loglibrary.flattener.Flattener;
import com.qiyi.loglibrary.util.TimeUtil;

import java.util.ArrayList;

public class CacheLogBeanPool {
    private boolean writtingFlag = false;
    private String filePath;
    private String moduleName;
    private Flattener flattener;

    public CacheLogBeanPool(Flattener flattener, String moduleName) {
        this.moduleName = moduleName;
        this.flattener = flattener;
    }

    public ArrayList<CacheLogBeanWrapper> beanArray = new ArrayList();

    public void addBean(LogEntity logEntity) {
        CacheLogBeanWrapper bean = new CacheLogBeanWrapper(logEntity.level, logEntity.moduleName, logEntity.msg ,flattener);
        bean.setTime(TimeUtil.getFormateTimeStr(System.currentTimeMillis()));
        beanArray.add(bean);
    }

    public void setFilePath(String mFilePath) {
        filePath = mFilePath;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setIsWritting(boolean writtingFlag) {
        this.writtingFlag = writtingFlag;
    }

    public boolean isWritting() {
        return writtingFlag;
    }

    public String getModuleName() {
        return moduleName;
    }

}
