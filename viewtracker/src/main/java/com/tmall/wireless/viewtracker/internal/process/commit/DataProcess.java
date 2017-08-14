/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.process.commit;

import android.text.TextUtils;
import android.view.View;

import com.tmall.wireless.viewtracker.api.IDataCommit;
import com.tmall.wireless.viewtracker.api.TrackerManager;
import com.tmall.wireless.viewtracker.constants.TrackerConstants;
import com.tmall.wireless.viewtracker.internal.globals.GlobalsContext;
import com.tmall.wireless.viewtracker.internal.util.TrackerLog;
import com.tmall.wireless.viewtracker.internal.util.TrackerUtil;

import java.util.HashMap;

/**
 * Created by zhiyonglizy on 15/12/1.
 */
public class DataProcess {
    public static void processClickParams(HashMap<String, Object> commonInfo, View clickView) {
        try {
            HashMap<String, Object> data = (HashMap<String, Object>) clickView.getTag(TrackerConstants.VIEW_TAG_PARAM);
            String viewName = TrackerUtil.getClickViewName(clickView);
            if (TextUtils.isEmpty(viewName)) {
                TrackerLog.d("processClickParams viewName is null");
                return;
            }
            commitClickParams(commonInfo, viewName, data);
        } catch (Throwable th) {
            TrackerLog.e("processClickParams fail," + th.getMessage());
        }

    }

    public static synchronized void commitClickParams(HashMap<String, Object> commonInfo, String viewName, HashMap<String, Object> viewData) {
        if (GlobalsContext.logOpen) {
            //TrackerLog.toastShort("viewName=" + viewName + ",data=" + data);
            TrackerLog.d("costTime=" + (System.currentTimeMillis() - GlobalsContext.start));
        }
        IDataCommit commit = TrackerManager.getInstance().getTrackerCommit();
        commit.commitClickEvent(commonInfo, viewName, viewData);
    }

    public static void processExposureParams(HashMap<String, Object> commonInfo, View exposureView, long exposureData, HashMap<String, Object> exposureIndex) {
        try {
            HashMap<String, Object> viewData = (HashMap<String, Object>) exposureView.getTag(TrackerConstants.VIEW_TAG_PARAM);
            String viewName = TrackerUtil.getClickViewName(exposureView);
            if (TextUtils.isEmpty(viewName)) {
                TrackerLog.d("processExposureParams viewName is null");
                return;
            }

            commitExposureParams(commonInfo, viewName, viewData, exposureData, exposureIndex);
        } catch (Throwable th) {
            TrackerLog.e("processExposureParams fail," + th.getMessage());
        }
    }

    public static synchronized void commitExposureParams(HashMap<String, Object> commonInfo, String viewName, HashMap<String, Object> viewData, long exposureData, HashMap<String, Object> exposureIndex) {
        if (GlobalsContext.logOpen) {
            TrackerLog.v("commitExposureParams commonInfo=" + commonInfo.toString() + ",viewName=" + viewName + ",viewData=" + viewData + ",exposureData=" + exposureData + ",exposureIndex=" + exposureIndex);
        }
        IDataCommit commit = TrackerManager.getInstance().getTrackerCommit();
        commit.commitExposureEvent(commonInfo, viewName, viewData, exposureData, exposureIndex);
    }
}
