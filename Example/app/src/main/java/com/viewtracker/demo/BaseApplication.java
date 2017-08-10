/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.viewtracker.demo;

import android.app.Application;

import com.tmall.wireless.viewtracker.api.TrackerManager;
import com.tmall.wireless.viewtracker.internal.globals.GlobalsContext;

/**
 * @author wuzhiji on 17/4/9.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TrackerManager.getInstance().setCommit(new DemoDataCommitImpl());
        GlobalsContext.logOpen = true;
        TrackerManager.getInstance().init(this, true, true, true);
    }
}
