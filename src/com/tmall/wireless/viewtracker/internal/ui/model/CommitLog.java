/*
 * Copyright （C）2010-2017 Alibaba Group Holding Limited
 */

package com.tmall.wireless.viewtracker.internal.ui.model;

import java.util.HashMap;

/**
 * a commit log differentiate the pageName and viewName.
 * Created by mengliu on 2017/3/16.
 */

public class CommitLog {
    public String pageName;

    public String viewName;

    /**
     * total exposure times inside the page
     */
    public int exposureTimes = 0;

    /**
     * total exposure duration inside the page
     */
    public long totalDuration = 0;

    /**
     * the attached info
     */
    public HashMap<String, String> argsInfo = new HashMap<String, String>();

    public CommitLog(String pageName, String viewName) {
        this.pageName = pageName;
        this.viewName = viewName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitLog commitLog = (CommitLog) o;

        if (!pageName.equals(commitLog.pageName)) return false;
        return viewName.equals(commitLog.viewName);

    }

    @Override
    public int hashCode() {
        int result = pageName.hashCode();
        result = 31 * result + viewName.hashCode();
        return result;
    }
}
