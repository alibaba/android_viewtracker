/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.process.commit;

import android.text.TextUtils;

import com.tmall.wireless.viewtracker.api.IDataCommit;
import com.tmall.wireless.viewtracker.constants.TrackerConstants;
import com.tmall.wireless.viewtracker.internal.globals.GlobalsContext;
import com.tmall.wireless.viewtracker.internal.process.biz.expourse.ExposureManager;
import com.tmall.wireless.viewtracker.internal.ui.model.CommitLog;
import com.tmall.wireless.viewtracker.internal.util.TrackerLog;
import com.tmall.wireless.viewtracker.internal.util.TrackerUtil;

import java.util.HashMap;

/**
 * commit the data by default
 * Created by zhiyongli on 15/12/3.
 */
public class DataCommitImpl implements IDataCommit {
    public void commitClickEvent(HashMap<String, Object> commonInfo, String viewName, HashMap<String, Object> viewData) {
        if (TextUtils.isEmpty(viewName)) {
            TrackerLog.d("commitClickEvent viewName is null");
            return;
        }
        TrackerLog.d("viewName=" + viewName);

        HashMap<String, String> argsInfo = new HashMap<String, String>();
        // add the common info
        if (commonInfo != null && !commonInfo.isEmpty()) {
            argsInfo.putAll(TrackerUtil.getHashMap(commonInfo));
        }

        if (argsInfo.containsKey(TrackerConstants.PAGE_NAME)) {
            argsInfo.remove(TrackerConstants.PAGE_NAME);
        }
        // add the special info
        if (viewData != null && !viewData.isEmpty()) {
            argsInfo.putAll(TrackerUtil.getHashMap(viewData));
        }

        if (GlobalsContext.trackerOpen) {
            if (!argsInfo.isEmpty()) {
                TrackerUtil.commitCtrlEvent(viewName, argsInfo);
            } else {
                TrackerUtil.commitCtrlEvent(viewName, null);
            }
        }
    }

    @Override
    public void commitExposureEvent(HashMap<String, Object> commonInfo, String viewName, HashMap<String, Object> viewData,
                                    long exposureData, HashMap<String, Object> exposureIndex) {
        if (TextUtils.isEmpty(viewName)) {
            TrackerLog.d("commitExposureEvent viewName is null");
            return;
        }

        HashMap<String, String> argsInfo = new HashMap<String, String>();
        // add the common info
        if (commonInfo != null && !commonInfo.isEmpty()) {
            argsInfo.putAll(TrackerUtil.getHashMap(commonInfo));
        }

        String pageName = argsInfo.remove(TrackerConstants.PAGE_NAME);
        TrackerLog.d("commitExposureEvent pageName is " + (TextUtils.isEmpty(pageName) ? "UT" : pageName));
        // add the exposure info
        if (!GlobalsContext.batchOpen) {
            if (exposureIndex != null && !exposureIndex.isEmpty()) {
                argsInfo.putAll(TrackerUtil.getHashMap(exposureIndex));
            }
        }
        // add the special info
        if (viewData != null && !viewData.isEmpty()) {
            argsInfo.putAll(TrackerUtil.getHashMap(viewData));
        }

        if (GlobalsContext.trackerExposureOpen) {
            // data commit one by one
            if (!GlobalsContext.batchOpen) {
                TrackerUtil.commitExtendEvent(pageName, 2201, viewName, null, String.valueOf(exposureData), argsInfo);
                if (GlobalsContext.logOpen) {
                    TrackerLog.v("commitExposureEvent commit " + " pageName=" + pageName + ",viewName=" + viewName
                            + ",duration=" + exposureData + ",args=" + argsInfo.toString());
                }
            } else {
                // batch data commit
                if (!ExposureManager.getInstance().commitLogs.containsKey(pageName + "_" + viewName)) {
                    CommitLog commitLog = new CommitLog(pageName, viewName);
                    commitLog.exposureTimes = 1;
                    commitLog.totalDuration += exposureData;
                    commitLog.argsInfo = argsInfo;

                    ExposureManager.getInstance().commitLogs.put(pageName + "_" + viewName, commitLog);
                } else {
                    CommitLog commitLog = ExposureManager.getInstance().commitLogs.get(pageName + "_" + viewName);
                    commitLog.exposureTimes++;
                    commitLog.totalDuration += exposureData;
                    commitLog.argsInfo = argsInfo;
                }
            }
        }
    }
}
