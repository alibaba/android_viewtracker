/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.viewtracker.demo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tmall.wireless.viewtracker.constants.TrackerConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuzhiji on 17/4/10.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private static final String TAG = "MyAdapter";
    
    private String[] mDataset;

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    public MyAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        Log.i(TAG, "onCreateViewHolder: "+Integer.toHexString(v.hashCode()));
        v.setOnClickListener(clickListener);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset[position]);
        holder.mTextView.setTag(TrackerConstants.VIEW_TAG_UNIQUE_NAME, "home_recycler_item_" + position);
        Map<String, String> map = new HashMap();
        map.put("content", mDataset[position]);
        holder.mTextView.setTag(TrackerConstants.VIEW_TAG_PARAM, map);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}