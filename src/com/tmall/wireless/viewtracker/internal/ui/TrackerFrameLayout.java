/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.tmall.wireless.viewtracker.constants.TrackerInternalConstants;
import com.tmall.wireless.viewtracker.internal.process.CommonHelper;
import com.tmall.wireless.viewtracker.internal.process.biz.click.ClickManager;
import com.tmall.wireless.viewtracker.internal.process.biz.expourse.ExposureManager;
import com.tmall.wireless.viewtracker.internal.ui.model.ExposureModel;
import com.tmall.wireless.viewtracker.internal.ui.model.ReuseLayoutHook;
import com.tmall.wireless.viewtracker.internal.util.TrackerLog;

import java.util.HashMap;
import java.util.Map;

/**
 * the parent layout of content view inside Activity
 * Created by mengliu on 16/6/14.
 */
public class TrackerFrameLayout extends FrameLayout implements GestureDetector.OnGestureListener {

    /**
     * Custom threshold is used to determine whether it is a click event,
     * When the user moves more than 20 pixels in screen, it is considered as the scrolling event instead of a click.
     */
    private static final float CLICK_LIMIT = 20;

    /**
     * the X Position
     */
    private float mOriX;

    /**
     * the Y Position
     */
    private float mOriY;

    private GestureDetector mGestureDetector;

    private ReuseLayoutHook mReuseLayoutHook;

    /**
     * common info attached with the view inside page
     */
    public HashMap<String, Object> commonInfo = new HashMap<String, Object>();

    /**
     * all the visible views inside page, key is viewName
     */
    private Map<String, ExposureModel> lastVisibleViewMap = new ArrayMap<String, ExposureModel>();

    private long lastOnLayoutSystemTimeMillis = 0;

    public TrackerFrameLayout(Context context) {
        super(context);
        this.mGestureDetector = new GestureDetector(context, this);
        this.mReuseLayoutHook = new ReuseLayoutHook(this, commonInfo);
        // after the onActivityResumed
        CommonHelper.addCommonArgsInfo(this);
    }

    public TrackerFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);

        if (getContext() != null && getContext() instanceof Activity) {
            // trigger the click event
            ClickManager.getInstance().eventAspect((Activity) getContext(), ev, commonInfo);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mOriX = ev.getX();
                mOriY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if ((Math.abs(ev.getX() - mOriX) > CLICK_LIMIT) || (Math.abs(ev.getY() - mOriY) > CLICK_LIMIT)) {
                    // Scene 1: Scroll beginning
                    long time = System.currentTimeMillis();
                    TrackerLog.v("dispatchTouchEvent triggerViewCalculate begin ");
                    ExposureManager.getInstance().triggerViewCalculate(TrackerInternalConstants.TRIGGER_VIEW_CHANGED, this, commonInfo, lastVisibleViewMap);
                    TrackerLog.v("dispatchTouchEvent triggerViewCalculate end costTime=" + (System.currentTimeMillis() - time));
                } else {
                    TrackerLog.d("dispatchTouchEvent ACTION_MOVE but not in click limit");
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    public Map<String, ExposureModel> getLastVisibleViewMap() {
        return lastVisibleViewMap;
    }

    /**
     * all the state change of view trigger the exposure event
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        TrackerLog.v("onLayout traverseViewTree begin");
        // duplicate message in 1s
        long time = System.currentTimeMillis();
        if (time - lastOnLayoutSystemTimeMillis > 1000) {
            lastOnLayoutSystemTimeMillis = time;

            CommonHelper.addCommonArgsInfo(this);
            TrackerLog.v("onLayout addCommonArgsInfo");
            ExposureManager.getInstance().traverseViewTree(this, mReuseLayoutHook);
        }
        //ExposureManager.getInstance().triggerViewCalculate(TrackerInternalConstants.TRIGGER_VIEW_CHANGED, this, commonInfo, lastVisibleViewMap);
        TrackerLog.v("onLayout traverseViewTree end costTime=" + (System.currentTimeMillis() - time));
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        TrackerLog.v("onDown");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        TrackerLog.v("onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        TrackerLog.v("onSingleTapUp");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        TrackerLog.v("onLongPress");
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    /**
     * Scene 2: Scroll ending
     *
     * @param motionEvent
     * @param motionEvent1
     * @param v
     * @param v1
     * @return
     */
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        long time = System.currentTimeMillis();
        TrackerLog.v("onFling triggerViewCalculate begin");
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                ExposureManager.getInstance().triggerViewCalculate(TrackerInternalConstants.TRIGGER_VIEW_CHANGED, TrackerFrameLayout.this, commonInfo, lastVisibleViewMap);
            }
        }, 1000);
        TrackerLog.v("onFling triggerViewCalculate end costTime=" + (System.currentTimeMillis() - time));
        return false;
    }

    /**
     * the state change of window trigger the exposure event.
     * Scene 3: switch back and forth when press Home button.
     * Scene 4: enter into the next page
     * Scene 5: window replace
     *
     * @param hasFocus
     */
    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        TrackerLog.v("dispatchWindowFocusChanged triggerViewCalculate begin");
        long ts = System.currentTimeMillis();
        ExposureManager.getInstance().triggerViewCalculate(TrackerInternalConstants.TRIGGER_WINDOW_CHANGED, this, commonInfo, lastVisibleViewMap);
        TrackerLog.v("dispatchWindowFocusChanged triggerViewCalculate end costTime=" + (System.currentTimeMillis() - ts));
        super.dispatchWindowFocusChanged(hasFocus);
    }

    @Override
    protected void dispatchVisibilityChanged(View changedView, int visibility) {
        // Scene 6: switch page in the TabActivity
        if (visibility == View.GONE) {
            TrackerLog.v("dispatchVisibilityChanged triggerViewCalculate begin");
            long ts = System.currentTimeMillis();
            ExposureManager.getInstance().triggerViewCalculate(TrackerInternalConstants.TRIGGER_WINDOW_CHANGED, this, commonInfo, lastVisibleViewMap);
            TrackerLog.v("dispatchVisibilityChanged triggerViewCalculate end costTime=" + (System.currentTimeMillis() - ts));
        } else {
            TrackerLog.v("trigger dispatchVisibilityChanged, visibility =" + visibility);
        }
        super.dispatchVisibilityChanged(changedView, visibility);
    }
}
