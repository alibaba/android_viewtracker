/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.viewtracker.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by wuzhiji on 17/4/11.
 */

public class CustomRecyclerView extends RecyclerView {
    private static final String TAG = "CustomRecyclerView";
    public CustomRecyclerView(Context context) {
        super(context);
    }
    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas c) {
        Log.i(TAG, "onDraw: ");
        super.onDraw(c);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.i(TAG, "dispatchDraw: ");
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
