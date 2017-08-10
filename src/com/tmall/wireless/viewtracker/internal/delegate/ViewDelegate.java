/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.delegate;

import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import com.tmall.wireless.viewtracker.internal.process.commit.DataProcess;
import com.tmall.wireless.viewtracker.internal.util.TrackerLog;

import java.util.HashMap;

/**
 * Created by zhiyongli on 15/11/30.
 */
public class ViewDelegate extends View.AccessibilityDelegate {
    private HashMap<String, Object> commonInfo = new HashMap<String, Object>();

    public void setCommonInfo(HashMap<String, Object> commonInfo) {
        this.commonInfo = commonInfo;
    }

    public void sendAccessibilityEvent(View clickView, int eventType) {
        TrackerLog.d("eventType: " + eventType);
        if (eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            TrackerLog.d("click: " + clickView);
            DataProcess.processClickParams(commonInfo, clickView);
        }
        super.sendAccessibilityEvent(clickView, eventType);
    }
}
