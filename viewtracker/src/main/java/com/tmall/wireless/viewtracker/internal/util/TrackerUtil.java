/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.util;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;

import com.tmall.wireless.viewtracker.api.TrackerManager;
import com.tmall.wireless.viewtracker.constants.TrackerConstants;
import com.tmall.wireless.viewtracker.internal.config.CommitViewsConfig;
import com.tmall.wireless.viewtracker.internal.globals.GlobalsContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhiyongli on 15/12/2.
 */
public class TrackerUtil {
    private static final String ID_SPECIFIER = "#ID#";

    /**
     * getName form tag, if tag is null, from id
     *
     * @param clickView
     * @return
     */
    public static String getClickViewName(View clickView) {
        boolean isFromId = false;
        String viewId = (String) clickView.getTag(TrackerConstants.VIEW_TAG_UNIQUE_NAME);
        if (TextUtils.isEmpty(viewId)) {
            viewId = getNameByResourceId(clickView.getId());
            if (!isCommitFormConfig(viewId)) {
                return null;
            }
            isFromId = true;
        }
        return getClickViewName(viewId, isFromId);
    }

    /**
     * According to the server configuration, whether to submit the view that names with the view ID in the XML resource file.
     *
     * @return
     */
    private static boolean isCommitFormConfig(String viewName) {
        long t = System.currentTimeMillis();
        boolean isCommit = false;
        String currentPageName = "";
        HashMap<String, String> commonInfoMap = TrackerManager.getInstance().getCommonInfoMap();
        if (commonInfoMap != null && commonInfoMap.containsKey(TrackerConstants.PAGE_NAME)) {
            currentPageName = commonInfoMap.get(TrackerConstants.PAGE_NAME);
        }
        TrackerLog.d("isCommitFormConfig pageName " + currentPageName);
        if (!TextUtils.isEmpty(currentPageName) && CommitViewsConfig.commitViews != null && CommitViewsConfig.commitViews.size() > 0) {
            ArrayList viewNames = CommitViewsConfig.commitViews.get(currentPageName);
            if (viewNames != null && viewNames.size() > 0) {
                if (viewNames.contains(viewName)) {
                    isCommit = true;
                }
            }
        }
        TrackerLog.e("isCommitFormConfig costTime=" + (System.currentTimeMillis() - t));
        return isCommit;
    }

    /**
     * generate the formatted view name
     *
     * @param viewName
     * @param isFromId
     * @return
     */
    public static String getClickViewName(String viewName, boolean isFromId) {
        if (TextUtils.isEmpty(viewName)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        if (isFromId) {
            sb.append(ID_SPECIFIER);
        }
        sb.append(viewName);
        return sb.toString();
    }

    private static String getNameByResourceId(int id) {
        long t1 = System.currentTimeMillis();
        String viewName = "";
        try {
            Resources r = GlobalsContext.mApplication.getResources();
            viewName = r.getResourceEntryName(id);
        } catch (Throwable th) {
            TrackerLog.d("getNameByResourceId fail " + th.getMessage());
        }
        TrackerLog.d("getNameByResourceId, costTime=" + (System.currentTimeMillis() - t1));
        TrackerLog.d("getNameByResourceId, viewName=" + viewName);
        return viewName;
    }

    public static HashMap<String, String> getHashMap(HashMap<String, Object> map) {
        HashMap<String, String> args = new HashMap<String, String>();
        if (map != null && map.size() > 0) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey()) && entry.getValue() != null) {
                    args.put(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
        }
        return args;
    }

    public static HashMap<String, Object> getHashMapObject(HashMap<String, String> map) {
        HashMap<String, Object> args = new HashMap<String, Object>();
        if (map != null && map.size() > 0) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey()) && entry.getValue() != null) {
                    args.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return args;
    }

    public static void commitCtrlEvent(String controlName, HashMap<String, String> args) {
        TrackerLog.v("commitCtrlEvent"
                + " controlName:" + (!TextUtils.isEmpty(controlName) ? controlName : "null")
                + " args:" + (args != null && !args.isEmpty() ? args.toString() : "null"));
    }

    public static void commitExtendEvent(String pageName, int eventID, String arg1, String arg2, String arg3, HashMap<String, String> args) {
        TrackerLog.v("commitExtendEvent"
                + " pageName:" + (!TextUtils.isEmpty(pageName) ? pageName : "null")
                + " eventID:" + String.valueOf(eventID)
                + " arg1:" + (!TextUtils.isEmpty(arg1) ? arg1 : "null")
                + " arg2:" + (!TextUtils.isEmpty(arg2) ? arg2 : "null")
                + " arg3:" + (!TextUtils.isEmpty(arg3) ? arg3 : "null")
                + " args:" + (args != null && !args.isEmpty() ? args.toString() : "null"));
    }
}
