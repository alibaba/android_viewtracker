/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.config;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by zhiyongli on 16/1/1.
 */
public class CommitViewsConfig {

    /**
     * view to be commit from server configuration,
     * key is the pageName, value is views to be commit which has no tag, existed ID in the XML resource file inside page named with key.
     */
    public static Map<String, ArrayList<String>> commitViews;
}
