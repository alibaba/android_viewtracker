/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.process.biz.click;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tmall.wireless.viewtracker.internal.delegate.ViewDelegate;
import com.tmall.wireless.viewtracker.internal.globals.GlobalsContext;
import com.tmall.wireless.viewtracker.internal.process.CommonHelper;
import com.tmall.wireless.viewtracker.internal.util.TrackerLog;

import java.util.HashMap;

/**
 * Created by zhiyongli on 17/1/22.
 */
public class ClickManager {
    private static ClickManager instance;
    private ViewDelegate mDelegate;
    private Boolean isSampleHit;

    private ClickManager() {
        mDelegate = new ViewDelegate();
    }

    public static ClickManager getInstance() {
        if (instance == null) {
            instance = new ClickManager();
        }
        return instance;
    }

    /**
     * find the clicked view, register the View.AccessibilityDelegate, commit data when trigger the click event.
     *
     * @param activity
     * @param event
     */
    public void eventAspect(Activity activity, MotionEvent event, HashMap<String, Object> commonInfo) {
        GlobalsContext.start = System.currentTimeMillis();
        if (!GlobalsContext.trackerOpen) {
            return;
        }
        if (activity == null) {
            return;
        }
        // sample not hit
        if (isSampleHit == null) {
            isSampleHit = CommonHelper.isSamplingHit(GlobalsContext.sampling);
        }
        if (!isSampleHit) {
            TrackerLog.d("click isSampleHit is false");
            return;
        }
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handleViewClick(activity, event, commonInfo);
            }
        } catch (Throwable th) {
            TrackerLog.e(th.getMessage());
        }

    }

    private void handleViewClick(Activity activity, MotionEvent event, HashMap<String, Object> commonInfo) {
        View view = activity.getWindow().getDecorView();
        View tagView = null;
        View clickView = getClickView(view, event, tagView);
        if (clickView != null) {
            if (mDelegate != null) {
                mDelegate.setCommonInfo(commonInfo);
            }
            clickView.setAccessibilityDelegate(mDelegate);
        }
    }

    /**
     * find the clicked view while loop.
     *
     * @param view
     * @param event
     * @return
     */
    private View getClickView(View view, MotionEvent event, View tagView) {
        View clickView = null;
        if (isClickView(view, event) && view.getVisibility() == View.VISIBLE) {
            // if the click view is a layout with tag, just return.
            if (CommonHelper.isViewHasTag(view)) {
                tagView = view;
            }
            // traverse the layout
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                for (int i = group.getChildCount() - 1; i >= 0; i--) {
                    View childView = group.getChildAt(i);
                    clickView = getClickView(childView, event, tagView);
                    if (clickView != null && CommonHelper.isViewHasTag(clickView)) {
                        return clickView;
                    }
                }
            }
            if (tagView != null) {
                clickView = tagView;
            }
        }
        return clickView;
    }

    private boolean isClickView(View view, MotionEvent event) {
        float clickX = event.getRawX();
        float clickY = event.getRawY();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int width = view.getWidth();
        int height = view.getHeight();
        if (clickX < x || clickX > (x + width) || clickY < y || clickY > (y + height)) {
            return false;
        }
        return true;
    }

}
