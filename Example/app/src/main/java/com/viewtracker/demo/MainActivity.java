/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.viewtracker.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.wireless.viewtracker.constants.TrackerConstants;

/**
 * ViewTracker Demo
 * adb logcat -s DemoDataCommitImpl 查看埋点提交日志
 * <p>
 * commitClickEvent: 点击埋点
 * commitExposureEvent: 曝光埋点
 */
public class MainActivity extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] myDataset = new String[100];

    private LinearLayout horizontalScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initUltraViewPager();
    }

    protected void initView() {

        TextView textView = (TextView) findViewById(R.id.click_me);
        textView.setTag(TrackerConstants.VIEW_TAG_UNIQUE_NAME, "home_click_me");
        textView.setOnClickListener(clickListener);

        horizontalScrollView = (LinearLayout) findViewById(R.id.horizontal_scrollview);
        int size = (int) getResources().getDisplayMetrics().density * 100;
        for (int i = 0; i < 10; i++) {
            TextView item = new TextView(this);
            item.setTextColor(Color.WHITE);
            item.setGravity(Gravity.CENTER);
            item.setTag(TrackerConstants.VIEW_TAG_UNIQUE_NAME, "home_horizontalScrollView_item " + i);
            item.setText("item " + i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.gravity = Gravity.CENTER;
            horizontalScrollView.addView(item, params);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        for (int i = 0; i < myDataset.length; i++) {
            myDataset[i] = "item " + i;
        }

        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initUltraViewPager() {
        UltraViewPager ultraViewPager = (UltraViewPager) findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        //UltraPagerAdapter 绑定子view到UltraViewPager
        PagerAdapter adapter = new UltraPagerAdapter(false);
        ultraViewPager.setAdapter(adapter);

        //内置indicator初始化
        ultraViewPager.initIndicator();
        //设置indicator样式
        ultraViewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(Color.GREEN)
                .setNormalColor(Color.WHITE)
                .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        //设置indicator对齐方式
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        //构造indicator,绑定到UltraViewPager
        ultraViewPager.getIndicator().build();

        //设定页面循环播放
        ultraViewPager.setInfiniteLoop(true);
        //设定页面自动切换  间隔2秒
        ultraViewPager.setAutoScroll(2000);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.click_me:
                    Toast.makeText(MainActivity.this, "check logcat", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
