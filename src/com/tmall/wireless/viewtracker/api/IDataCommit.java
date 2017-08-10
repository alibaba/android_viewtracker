/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.api;

import java.util.HashMap;

/**
 * Created by zhiyongli on 15/12/3.
 */
public interface IDataCommit {
    public void commitClickEvent(HashMap<String, Object> commonInfo, String viewName, HashMap<String, Object> viewData);

    public void commitExposureEvent(HashMap<String, Object> commonInfo, String viewName, HashMap<String, Object> viewData, long exposureData, HashMap<String, Object> exposureIndex);
}