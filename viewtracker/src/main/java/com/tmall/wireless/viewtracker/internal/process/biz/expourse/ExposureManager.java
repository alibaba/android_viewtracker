/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.process.biz.expourse;

import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import com.tmall.wireless.viewtracker.constants.TrackerConstants;
import com.tmall.wireless.viewtracker.constants.TrackerInternalConstants;
import com.tmall.wireless.viewtracker.internal.globals.GlobalsContext;
import com.tmall.wireless.viewtracker.internal.process.CommonHelper;
import com.tmall.wireless.viewtracker.internal.process.commit.DataProcess;
import com.tmall.wireless.viewtracker.internal.ui.model.CommitLog;
import com.tmall.wireless.viewtracker.internal.ui.model.ExposureModel;
import com.tmall.wireless.viewtracker.internal.ui.model.ReuseLayoutHook;
import com.tmall.wireless.viewtracker.internal.util.TrackerLog;
import com.tmall.wireless.viewtracker.internal.util.TrackerUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhiyongli on 17/1/22.
 */
public class ExposureManager {
    private static final int SINGLE_COMMIT_EXPOSURE = 0;
    public static final int BATCH_COMMIT_EXPOSURE = 1;
    private static ExposureManager instance;

    /**
     * whether or not to hit the exposure event
     */
    private Boolean isSampleHit;

    private long traverseTime;

    /**
     * key is "pageName_viewName", value is the commit log
     */
    public Map<String, CommitLog> commitLogs = new ArrayMap<String, CommitLog>();

    private Handler exposureHandler;

    private ExposureManager() {
        HandlerThread exposureThread = new HandlerThread("ViewTracker_exposure");
        exposureThread.start();

        exposureHandler = new Handler(exposureThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case SINGLE_COMMIT_EXPOSURE:
                        ExposureInner exposureInner = (ExposureInner) msg.obj;
                        switch (exposureInner.triggerType) {
                            case TrackerInternalConstants.TRIGGER_WINDOW_CHANGED:
                                for (String controlName : exposureInner.lastVisibleViewMap.keySet()) {
                                    // If the current window invokes change, all the visible views need to be committed.
                                    ExposureModel model = exposureInner.lastVisibleViewMap.get(controlName);
                                    model.endTime = System.currentTimeMillis();
                                    reportExposureData(exposureInner.commonInfo, model, controlName);
                                }
                                break;
                            case TrackerInternalConstants.TRIGGER_VIEW_CHANGED:
                                for (String controlName : exposureInner.lastVisibleViewMap.keySet()) {
                                    // If the view is visible in the last trigger timing, but invisible this time, then we commit the view as a exposure event.
                                    if (!exposureInner.currentVisibleViewMap.containsKey(controlName)) {
                                        ExposureModel model = exposureInner.lastVisibleViewMap.get(controlName);
                                        model.endTime = System.currentTimeMillis();
                                        reportExposureData(exposureInner.commonInfo, model, controlName);
                                    }
                                }
                                break;
                        }

                        break;
                    case BATCH_COMMIT_EXPOSURE:
                        for (CommitLog commitLog : commitLogs.values()) {
                            // the exposure times inside page
                            commitLog.argsInfo.put("exposureTimes", String.valueOf(commitLog.exposureTimes));
                            // Scene 3 (switch back and forth when press Home button) is excluded.
                            TrackerUtil.commitExtendEvent(commitLog.pageName, 2201, commitLog.viewName, null, String.valueOf(commitLog.totalDuration), commitLog.argsInfo);
                            TrackerLog.v("onActivityPaused batch commit " + "pageName=" + commitLog.pageName + ",viewName=" + commitLog.viewName
                                    + ",totalDuration=" + commitLog.totalDuration + ",args=" + commitLog.argsInfo.toString());
                        }

                        // clear after committed.
                        commitLogs.clear();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    public static ExposureManager getInstance() {
        if (instance == null) {
            instance = new ExposureManager();
        }
        return instance;
    }

    public Handler getExposureHandler() {
        return exposureHandler;
    }

    /**
     * for the exposure event
     *
     * @param view
     * @return
     */
    public void triggerViewCalculate(int triggerType, View view, HashMap<String, Object> commonInfo,
                                     Map<String, ExposureModel> lastVisibleViewMap) {
        if (!GlobalsContext.trackerExposureOpen) {
            return;
        }

        long triggerTime = System.currentTimeMillis();
        if (triggerTime - traverseTime < 100) {
            TrackerLog.d("triggerTime interval is too close to 100ms");
            return;
        }
        traverseTime = triggerTime;
        if (view == null) {
            TrackerLog.d("view is null");
            return;
        }
        // Sample not hit
        if (isSampleHit == null) {
            isSampleHit = CommonHelper.isSamplingHit(GlobalsContext.exposureSampling);
        }
        if (!isSampleHit) {
            TrackerLog.d("exposure isSampleHit is false");
            return;
        }

        Map<String, ExposureModel> currentVisibleViewMap = new ArrayMap<String, ExposureModel>();
        traverseViewTree(view, lastVisibleViewMap, currentVisibleViewMap);
        commitExposure(triggerType, commonInfo, lastVisibleViewMap, currentVisibleViewMap);
        TrackerLog.d("triggerViewCalculate");
    }

    public void traverseViewTree(View view, ReuseLayoutHook reuseLayoutHook) {
        if (!GlobalsContext.trackerExposureOpen) {
            return;
        }

        if (reuseLayoutHook != null) {
            reuseLayoutHook.checkHookLayout(view);
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                traverseViewTree(group.getChildAt(i), reuseLayoutHook);
            }
        }
    }

    /**
     * find all the view that can be seen in screen.
     *
     * @param view
     */
    private void traverseViewTree(View view, Map<String, ExposureModel> lastVisibleViewMap,
                                  Map<String, ExposureModel> currentVisibleViewMap) {
        if (CommonHelper.isViewHasTag(view)) {
            wrapExposureCurrentView(view, lastVisibleViewMap, currentVisibleViewMap);
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                traverseViewTree(group.getChildAt(i), lastVisibleViewMap, currentVisibleViewMap);
            }
        }
    }

    private void wrapExposureCurrentView(View view, Map<String, ExposureModel> lastVisibleViewMap,
                                         Map<String, ExposureModel> currentVisibleViewMap) {
        String viewTag = (String) view.getTag(TrackerConstants.VIEW_TAG_UNIQUE_NAME);
        HashMap<String, Object> params = (HashMap<String, Object>) view.getTag(TrackerConstants.VIEW_TAG_PARAM);

        boolean isWindowChange = view.hasWindowFocus();
        boolean exposureValid = checkExposureViewDimension(view);
        boolean needExposureProcess = isWindowChange && exposureValid;
        if (!needExposureProcess) {
            return;
        }

        // only add the visible view in screen
        if (lastVisibleViewMap.containsKey(viewTag)) {
            ExposureModel model = lastVisibleViewMap.get(viewTag);
            model.params = params;
            currentVisibleViewMap.put(viewTag, model);
        } else if (!currentVisibleViewMap.containsKey(viewTag)) {
            ExposureModel model = new ExposureModel();
            model.beginTime = System.currentTimeMillis();
            model.tag = viewTag;
            model.params = params;
            currentVisibleViewMap.put(viewTag, model);
        }
    }

    private void commitExposure(int triggerType, HashMap<String, Object> commonInfo,
                                Map<String, ExposureModel> lastVisibleViewMap, Map<String, ExposureModel> currentVisibleViewMap) {
        ExposureInner exposureInner = new ExposureInner();
        exposureInner.triggerType = triggerType;

        exposureInner.commonInfo = new HashMap<String, Object>();
        exposureInner.commonInfo.putAll(commonInfo);

        exposureInner.lastVisibleViewMap = new HashMap<String, ExposureModel>();
        for (Map.Entry<String, ExposureModel> entry : lastVisibleViewMap.entrySet()) {
            exposureInner.lastVisibleViewMap.put(entry.getKey(), (ExposureModel) entry.getValue().clone());
        }

        exposureInner.currentVisibleViewMap = new HashMap<String, ExposureModel>();
        for (Map.Entry<String, ExposureModel> entry : currentVisibleViewMap.entrySet()) {
            exposureInner.currentVisibleViewMap.put(entry.getKey(), (ExposureModel) entry.getValue().clone());
        }

        lastVisibleViewMap.clear();
        lastVisibleViewMap.putAll(currentVisibleViewMap);

        // transfer time-consuming operation to new thread.
        Message message = exposureHandler.obtainMessage();
        message.what = SINGLE_COMMIT_EXPOSURE;
        message.obj = exposureInner;
        exposureHandler.sendMessage(message);
    }

    /**
     * check the visible width and height of the view, compared with the its original width and height.
     *
     * @param view
     * @return
     */
    private boolean checkExposureViewDimension(View view) {
        int width = view.getWidth();
        int height = view.getHeight();
        Rect GlobalVisibleRect = new Rect();
        boolean isVisibleRect = view.getGlobalVisibleRect(GlobalVisibleRect);
        if (isVisibleRect) {

            int visibleWidth = GlobalVisibleRect.width();
            int visibleHeight = GlobalVisibleRect.height();

            if ((visibleWidth * 1.00 / width > GlobalsContext.dimThreshold) && (visibleHeight * 1.00 / height > GlobalsContext.dimThreshold)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void reportExposureData(HashMap<String, Object> commonInfo, ExposureModel model, String viewTag) {
        long duration = getExposureViewDuration(model);
        if (duration > 0) {
            TrackerLog.v("ExposureView report " + model.toString() + " exposure data " + duration);
            HashMap<String, Object> indexMap = new HashMap<String, Object>();
            if (!GlobalsContext.exposureIndex.containsKey(viewTag)) {
                // commit firstly
                GlobalsContext.exposureIndex.put(viewTag, 1);
                indexMap.put("exposureIndex", 1);
            } else {
                int index = GlobalsContext.exposureIndex.get(viewTag);
                GlobalsContext.exposureIndex.put(viewTag, index + 1);
                indexMap.put("exposureIndex", index + 1);
            }

            DataProcess.commitExposureParams(commonInfo, model.tag, model.params, duration, indexMap);
        }
    }

    /**
     * check the exposure duration
     *
     * @param model
     * @return
     */
    private long getExposureViewDuration(ExposureModel model) {
        if (model.beginTime > 0 && model.endTime > 0
                && model.endTime > model.beginTime) {
            long duration = model.endTime - model.beginTime;
            // omit the value less than 100
            if (duration > GlobalsContext.timeThreshold && duration < GlobalsContext.maxTimeThreshold) {
                return duration;
            }
        }
        return 0;
    }

    private class ExposureInner {
        private int triggerType;
        private HashMap<String, Object> commonInfo;
        private Map<String, ExposureModel> lastVisibleViewMap;
        private Map<String, ExposureModel> currentVisibleViewMap;
    }
}
