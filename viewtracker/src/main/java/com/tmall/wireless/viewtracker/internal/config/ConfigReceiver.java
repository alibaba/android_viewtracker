/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.config;

import com.tmall.wireless.viewtracker.internal.util.TrackerLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.tmall.wireless.viewtracker.internal.globals.GlobalsContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * receive the server configuration
 * Created by zhiyongli on 15/12/14.
 */
public class ConfigReceiver extends BroadcastReceiver {
    public static final String VIEWTRACKER_CONFIG_KEY = "viewtrackerConfig";

    public static final String VIEWTRACKER_EXPOSURE_CONFIG_KEY = "viewtrackerExposureConfig";

    public static final String ACTION_CONFIG_CHANGED = "com.tmall.wireless.viewtracker.config.changed";

    /**
     * server configuration for click event
     * <p>
     * {
     * "masterSwitch": true,
     * "sampling":100
     * "commitViews": [
     * {
     * "packageName": "FunHome",
     * "viewNames": [
     * "icon-0",
     * "icon-1"
     * ]
     * }
     * ]
     * }
     * <p>
     * server configuration for exposure event
     * <p>
     * {
     * "masterSwitch": true,
     * "timeThreshold": 100,
     * "dimThreshold": 0.8,
     * "exposureSampling": 100,
     * "batchOpen":false
     * }
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (context == null || intent == null) {
                TrackerLog.d("ConfigReceiver, context or intent is null");
                return;
            }

            if (ACTION_CONFIG_CHANGED.equals(intent.getAction())) {
                wrapConfig(intent.getStringExtra(VIEWTRACKER_CONFIG_KEY));
                wrapExposureConfig(intent.getStringExtra(VIEWTRACKER_EXPOSURE_CONFIG_KEY));
            }
        } catch (Exception e) {
            TrackerLog.e("ConfigReceiver onReceive fail " + e.getMessage());
        }
    }

    private void wrapConfig(String configStr) {
        if (TextUtils.isEmpty(configStr)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(configStr);
            Map<String, ArrayList<String>> views = new HashMap<String, ArrayList<String>>();
            GlobalsContext.trackerOpen = jsonObject.optBoolean("masterSwitch", true);
            GlobalsContext.sampling = jsonObject.optInt("sampling", 100);
            TrackerLog.v("ConfigReceiver trackerClickConfig " + jsonObject.toString());
            JSONArray array = jsonObject.optJSONArray("commitViews");
            if (array != null && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject view = array.optJSONObject(i);
                    if (view != null && view.length() > 0) {
                        ArrayList<String> viewNames = new ArrayList<String>();
                        String pageName = view.optString("pageName");
                        if (!TextUtils.isEmpty(pageName)) {
                            JSONArray vnames = view.optJSONArray("viewNames");
                            if (vnames != null && vnames.length() > 0) {
                                for (int j = 0; j < vnames.length(); j++) {
                                    if (!TextUtils.isEmpty(vnames.optString(j))) {
                                        viewNames.add(vnames.optString(j));
                                    }
                                }
                                if (viewNames.size() > 0) {
                                    views.put(pageName, viewNames);
                                }
                            }
                        }

                    }

                }
            }
            CommitViewsConfig.commitViews = views;
        } catch (Exception e) {
            TrackerLog.e("ConfigReceiver wrapConfig fail " + e.getMessage());
        }

    }

    private void wrapExposureConfig(String configStr) {
        if (TextUtils.isEmpty(configStr)) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(configStr);
            GlobalsContext.trackerExposureOpen = jsonObject.optBoolean("masterSwitch", true);
            GlobalsContext.timeThreshold = jsonObject.optInt("timeThreshold", 100);
            GlobalsContext.dimThreshold = jsonObject.optDouble("dimThreshold", 0.8);
            GlobalsContext.exposureSampling = jsonObject.optInt("exposureSampling", 100);
            GlobalsContext.batchOpen = jsonObject.optBoolean("batchOpen", false);

            TrackerLog.v("ConfigReceiver trackerExposureConfig " + jsonObject.toString());
        } catch (JSONException e) {
            TrackerLog.e("ConfigReceiver wrapExposureConfig fail " + e.getMessage());
        }
    }
}
