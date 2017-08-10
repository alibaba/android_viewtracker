/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.util;

import android.util.Log;

import com.tmall.wireless.viewtracker.constants.TrackerInternalConstants;
import com.tmall.wireless.viewtracker.internal.globals.GlobalsContext;

/**
 * Created by zhiyongli on 15/12/4.
 */
public class TrackerLog {
    public static void d(String msg) {
        if (GlobalsContext.logOpen) {
            Log.d(TrackerInternalConstants.TAG, msg);
        }
    }

    public static void v(String msg) {
        if (GlobalsContext.logOpen) {
            Log.v(TrackerInternalConstants.TAG, msg);
        }
    }

    public static void e(String msg) {
        Log.e(TrackerInternalConstants.TAG, msg);
    }
}
