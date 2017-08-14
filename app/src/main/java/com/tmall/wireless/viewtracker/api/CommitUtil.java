/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.api;

import android.text.TextUtils;

import com.tmall.wireless.viewtracker.internal.globals.GlobalsContext;
import com.tmall.wireless.viewtracker.internal.process.commit.DataProcess;
import com.tmall.wireless.viewtracker.internal.util.TrackerUtil;

import java.util.HashMap;

/**
 * special scene: commit data when double click or long click event trigger.
 * Created by zhiyongli on 15/12/9.
 */
public class CommitUtil {
    /**
     * @param viewName
     * @param data
     */
    public static void commitEventParams(String viewName, HashMap<String, Object> data, HashMap<String, Object> commonInfo) {
        if (GlobalsContext.trackerOpen && !TextUtils.isEmpty(viewName)) {
            DataProcess.commitClickParams(commonInfo, TrackerUtil.getClickViewName(viewName, false), data);
        }
    }

    public static void commitEventParams(String viewName, HashMap<String, Object> data) {
        if (GlobalsContext.trackerOpen && !TextUtils.isEmpty(viewName)) {
            DataProcess.commitClickParams(null, TrackerUtil.getClickViewName(viewName, false), data);
        }
    }
}
