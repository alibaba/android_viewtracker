/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.ui.model;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.tmall.wireless.viewtracker.constants.TrackerInternalConstants;
import com.tmall.wireless.viewtracker.internal.process.biz.expourse.ExposureManager;
import com.tmall.wireless.viewtracker.internal.ui.TrackerFrameLayout;
import com.tmall.wireless.viewtracker.internal.util.TrackerLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author wuzhiji on 17/4/12.
 */
public class ReuseLayoutHook {

    private static final int HOOK_VIEW_TAG = -9100;

    private TrackerFrameLayout mRootLayout;
    private HashMap<String, Object> mCommonInfo;
    private List<ViewHookListener> mList = new ArrayList<ViewHookListener>();

    private interface ViewHookListener {
        boolean isValid(View view);

        void hookView(View view);
    }

//    private class RecyclerViewHook implements ViewHookListener {
//
//        @Override
//        public boolean isValid(View view) {
//            return view instanceof RecyclerView;
//        }
//
//        @Override
//        public void hookView(View view) {
//            RecyclerView recyclerView = (RecyclerView) view;
//            Object tag = recyclerView.getTag(HOOK_VIEW_TAG);
//            if (tag != null && !(tag instanceof Boolean)) {
//                return;
//            }
//            Boolean added = (Boolean) tag;
//            if (added != null && added) {
//                return;
//            }
//            recyclerView.addOnScrollListener(new RecyclerScrollListener());
//            recyclerView.setTag(HOOK_VIEW_TAG, true);
//        }
//    }
//
//    private class AbsListViewHook implements ViewHookListener {
//
//        @Override
//        public boolean isValid(View view) {
//            return view instanceof AbsListView;
//        }
//
//        @Override
//        public void hookView(View view) {
//            AbsListView listView = (AbsListView) view;
//            try {
//                Field field = listView.getClass().getField("mScrollListener");
//                field.setAccessible(true);
//                AbsListView.OnScrollListener currentListener = (AbsListView.OnScrollListener) field.get(listView);
//                if (currentListener instanceof AbsListViewScrollListener) {
//                    return;
//                }
//                AbsListView.OnScrollListener listener = new AbsListViewScrollListener(currentListener);
//                listView.setOnScrollListener(listener);
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//        }
//    }

    private class ViewPagerHook implements ViewHookListener {

        @Override
        public boolean isValid(View view) {
            return view instanceof ViewPager;
        }

        @Override
        public void hookView(View view) {
            ViewPager viewPager = (ViewPager) view;
            Object tag = viewPager.getTag(HOOK_VIEW_TAG);
            if (tag != null && !(tag instanceof Boolean)) {
                return;
            }
            Boolean added = (Boolean) tag;
            if (added != null && added) {
                return;
            }
            viewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener());
            viewPager.setTag(HOOK_VIEW_TAG, true);
            TrackerLog.d("ViewPager addOnPageChangeListener.");
        }
    }

    public ReuseLayoutHook(TrackerFrameLayout rootLayout, HashMap<String, Object> commonInfo) {
        this.mRootLayout = rootLayout;
        this.mCommonInfo = commonInfo;
        // replace with the onFling()
        //mList.add(new RecyclerViewHook());
        //mList.add(new AbsListViewHook());
        mList.add(new ViewPagerHook());
    }

    public void checkHookLayout(View view) {
        for (ViewHookListener listener : mList) {
            if (listener != null && listener.isValid(view)) {
                listener.hookView(view);
            }
        }
    }

//    private class RecyclerScrollListener extends RecyclerView.OnScrollListener {
//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//        }
//
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                ExposureManager.getInstance().triggerViewCalculate(TrackerInternalConstants.TRIGGER_VIEW_CHANGED, mRootLayout, mCommonInfo, mRootLayout.getLastVisibleViewMap());
//            }
//        }
//    }
//
//    private class AbsListViewScrollListener implements AbsListView.OnScrollListener {
//
//        private AbsListView.OnScrollListener listener;
//
//        public AbsListViewScrollListener(AbsListView.OnScrollListener listener) {
//            this.listener = listener;
//        }
//
//        @Override
//        public void onScrollStateChanged(AbsListView view, int scrollState) {
//            if (listener != null) {
//                listener.onScrollStateChanged(view, scrollState);
//            }
//            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                ExposureManager.getInstance().triggerViewCalculate(TrackerInternalConstants.TRIGGER_VIEW_CHANGED, mRootLayout, mCommonInfo, mRootLayout.getLastVisibleViewMap());
//            }
//        }
//
//        @Override
//        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            if (listener != null) {
//                listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
//            }
//        }
//    }

    private class ViewPagerOnPageChangeListener implements OnPageChangeListener {

        private int state = ViewPager.SCROLL_STATE_IDLE;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (state != ViewPager.SCROLL_STATE_SETTLING) {
                ExposureManager.getInstance().triggerViewCalculate(TrackerInternalConstants.TRIGGER_VIEW_CHANGED, mRootLayout, mCommonInfo, mRootLayout.getLastVisibleViewMap());
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (this.state == ViewPager.SCROLL_STATE_SETTLING && state == ViewPager.SCROLL_STATE_IDLE) {
                ExposureManager.getInstance().triggerViewCalculate(TrackerInternalConstants.TRIGGER_VIEW_CHANGED, mRootLayout, mCommonInfo, mRootLayout.getLastVisibleViewMap());
            }
            this.state = state;
        }
    }

}
