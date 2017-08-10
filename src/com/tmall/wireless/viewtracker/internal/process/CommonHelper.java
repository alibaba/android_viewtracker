/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.process;

import android.app.Activity;
import android.view.View;

import com.tmall.wireless.viewtracker.api.TrackerManager;
import com.tmall.wireless.viewtracker.constants.TrackerConstants;
import com.tmall.wireless.viewtracker.internal.ui.TrackerFrameLayout;
import com.tmall.wireless.viewtracker.internal.util.TrackerLog;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by zhiyongli on 17/1/22.
 */
public class CommonHelper {

    /**
     * common info in the page for the click and exposure event
     *
     * @param tfLayout
     */
    public static void addCommonArgsInfo(TrackerFrameLayout tfLayout) {
        if (tfLayout.getContext() != null && tfLayout.getContext() instanceof Activity) {
            View decorView = ((Activity) tfLayout.getContext()).getWindow().getDecorView();

            tfLayout.commonInfo.clear();
            HashMap<String, String> commonInfoMap = TrackerManager.getInstance().getCommonInfoMap();
            if (commonInfoMap != null) {
                tfLayout.commonInfo.putAll(commonInfoMap);
            }

            // common info attached with the view tag
            HashMap<String, Object> commonInfo = (HashMap<String, Object>) decorView.getTag(TrackerConstants.DECOR_VIEW_TAG_COMMON_INFO);
            if (commonInfo != null && !commonInfo.isEmpty()) {
                tfLayout.commonInfo.putAll(commonInfo);
                TrackerLog.v("addCommonArgsInfo commonInfo " + commonInfo);
            }
            TrackerLog.v("addCommonArgsInfo all commonInfo " + tfLayout.commonInfo);
        }
    }

    public static boolean isViewHasTag(View view) {
        return view.getTag(TrackerConstants.VIEW_TAG_UNIQUE_NAME) != null;
    }

    public static boolean isSamplingHit(int sample) {
        Random rand = new Random();
        int samplingSeed = rand.nextInt(100);
        if (samplingSeed >= sample) {
            return false;
        } else {
            return true;
        }
    }
}
