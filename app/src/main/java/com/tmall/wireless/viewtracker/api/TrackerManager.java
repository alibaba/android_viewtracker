/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.api;

import android.app.Activity;
import android.app.Application;
import android.app.TabActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import com.tmall.wireless.viewtracker.internal.globals.GlobalsContext;
import com.tmall.wireless.viewtracker.internal.process.biz.expourse.ExposureManager;
import com.tmall.wireless.viewtracker.internal.process.commit.DataCommitImpl;
import com.tmall.wireless.viewtracker.internal.ui.TrackerFrameLayout;
import com.tmall.wireless.viewtracker.internal.util.TrackerLog;

import java.util.HashMap;

/**
 * Created by zhiyongli on 15/12/3.
 */
public class TrackerManager {
    private static TrackerManager instance;

    private IDataCommit trackerCommit;

    private ActivityLifecycleForTracker mActivityLifecycle;

    private HashMap<String, String> commonInfoMap;

    private TrackerManager() {
        commonInfoMap = new HashMap<String, String>();
    }

    public static TrackerManager getInstance() {
        if (instance == null) {
            instance = new TrackerManager();
        }
        return instance;
    }

    /**
     * initiate viewtracker SDK
     *
     * @param application         global application context
     * @param trackerOpen         whether or not track click event
     * @param trackerExposureOpen whether or not track exposure event
     * @param logOpen             whether or not print the log
     */
    public void init(Application application, boolean trackerOpen, boolean trackerExposureOpen, boolean logOpen) {
        GlobalsContext.mApplication = application;
        GlobalsContext.trackerOpen = trackerOpen;
        GlobalsContext.trackerExposureOpen = trackerExposureOpen;
        GlobalsContext.logOpen = logOpen;

        if (GlobalsContext.trackerOpen || GlobalsContext.trackerExposureOpen) {
            mActivityLifecycle = new ActivityLifecycleForTracker();
            application.registerActivityLifecycleCallbacks(mActivityLifecycle);
        }
    }

    /**
     * unregister Activity Lifecycle Callbacks
     *
     * @param application
     */
    public void unInit(Application application) {
        if (mActivityLifecycle != null) {
            application.unregisterActivityLifecycleCallbacks(mActivityLifecycle);
        }
    }

    /**
     * set common info inside the page
     *
     * @param commonMap
     */
    public void setCommonInfoMap(HashMap<String, String> commonMap) {
        commonInfoMap.clear();
        commonInfoMap.putAll(commonMap);
    }

    public HashMap<String, String> getCommonInfoMap() {
        return commonInfoMap;
    }

    public void setSampling(int sampling) {
        if (sampling < 0) {
            sampling = 0;
        } else if (sampling > 100) {
            sampling = 100;
        }

        GlobalsContext.sampling = sampling;
    }

    public void attachTrackerFrameLayout(Activity activity) {
        // this is a problem: several activity exist in the TabActivity
        if (activity == null || activity instanceof TabActivity) {
            return;
        }


        // exist android.R.id.content not found crash
        try {
            ViewGroup container = (ViewGroup) activity.findViewById(android.R.id.content);

            if (container == null) {
                return;
            }

            if (container.getChildCount() > 0) {
                View root = container.getChildAt(0);
                if (root instanceof TrackerFrameLayout) {
                    TrackerLog.d("no attachTrackerFrameLayout " + activity.toString());
                } else {
                    TrackerFrameLayout trackerFrameLayout = new TrackerFrameLayout(activity);

                    while (container.getChildCount() > 0) {
                        View view = container.getChildAt(0);
                        container.removeViewAt(0);
                        trackerFrameLayout.addView(view, view.getLayoutParams());
                    }

                    container.addView(trackerFrameLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
        } catch (Exception e) {
            TrackerLog.e(e.toString());
        }

    }

    private void detachTrackerFrameLayout(Activity activity) {
        if (activity == null || activity instanceof TabActivity) {
            return;
        }

        try {
            ViewGroup container = (ViewGroup) activity.findViewById(android.R.id.content);

            if (container == null) {
                return;
            }

            if (container.getChildAt(0) instanceof TrackerFrameLayout) {
                container.removeViewAt(0);
            }
        } catch (Exception e) {
            TrackerLog.e(e.toString());
        }

    }

    /**
     * set own data commit method
     *
     * @param externalCommit
     */
    public void setCommit(IDataCommit externalCommit) {
        this.trackerCommit = externalCommit;
    }

    public IDataCommit getTrackerCommit() {
        if (trackerCommit == null) {
            trackerCommit = new DataCommitImpl();
        }
        return trackerCommit;
    }

    /**
     * commit the data for exposure event in batch
     */
    private void batchReport() {
        long time = System.currentTimeMillis();

        Handler handler = ExposureManager.getInstance().getExposureHandler();
        Message message = handler.obtainMessage();
        message.what = ExposureManager.BATCH_COMMIT_EXPOSURE;
        handler.sendMessage(message);

        TrackerLog.v("batch report exposure views " + (System.currentTimeMillis() - time) + "ms");
    }

    private class ActivityLifecycleForTracker implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            TrackerLog.d("onActivityResumed activity " + activity.toString());
            attachTrackerFrameLayout(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (GlobalsContext.trackerExposureOpen) {
                TrackerLog.d("onActivityPaused activity " + activity.toString());
                if (GlobalsContext.batchOpen) {
                    batchReport();
                }
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            TrackerLog.d("onActivityDestroyed activity " + activity.toString());
            detachTrackerFrameLayout(activity);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }
    }
}
