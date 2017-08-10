/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.viewtracker.demo;

import android.util.Log;

import com.tmall.wireless.viewtracker.api.IDataCommit;

import java.util.HashMap;

/**
 * @author wuzhiji on 17/4/9.
 */
public class DemoDataCommitImpl implements IDataCommit {
    private static final String TAG = "DemoDataCommitImpl";

    @Override
    public void commitClickEvent(HashMap<String, Object> commonInfo, String viewName, HashMap<String, Object> viewData) {
        String extra = viewData == null ? "" : "  extra=" + viewData.toString();
        Log.i(TAG, "commitClickEvent: viewName=" + viewName + extra);
    }

    @Override
    public void commitExposureEvent(HashMap<String, Object> commonInfo, String viewName, HashMap<String, Object> viewData, long exposureData, HashMap<String, Object> exposureIndex) {
        String extra = viewData == null ? "" : "  extra=" + viewData.toString();
        Log.i(TAG, "commitExposureEvent: viewName=" + viewName + " exposureTime=" + exposureData + extra);
    }
}
