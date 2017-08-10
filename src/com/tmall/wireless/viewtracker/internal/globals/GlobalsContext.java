/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.globals;

import android.app.Application;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhiyongli on 15/12/3.
 */
public class GlobalsContext {
    public static Application mApplication;

    /**
     * whether or not to track click event
     */
    public static boolean trackerOpen = true;

    /**
     * whether or not to track exposure event
     */
    public static boolean trackerExposureOpen = true;

    /**
     * min threshold of the exposure duration
     */
    public static int timeThreshold = 100;

    /**
     * max threshold of the exposure duration
     */
    public static int maxTimeThreshold = 60 * 60 * 1000;

    /**
     * threshold of the view width and height
     */
    public static double dimThreshold = 0.8;

    /**
     * whether or not to print the log
     */
    public static boolean logOpen = true;

    /**
     * whether or not to commit the exposure event log in batch or one by one
     */
    public static boolean batchOpen = false;

    public static long start = 0L;

    /**
     * the exposure index in the lifecycle of APP
     */
    public static Map<Object, Integer> exposureIndex = new LinkedHashMap<Object, Integer>();

    /**
     * for click event, 100% by default
     */
    public static int sampling = 100;

    /**
     * for exposure event, 100% by default
     */
    public static int exposureSampling = 100;
}
