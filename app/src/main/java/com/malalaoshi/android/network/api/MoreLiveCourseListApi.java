package com.malalaoshi.android.network.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.network.result.LiveCourseResult;

/**
 * More teacher list api
 * Created by tianwei on 4/17/16.
 */
public class MoreLiveCourseListApi extends BaseApi {

    @Override
    protected String getPath() {
        return "";
    }

    @Override
    public String getUrl(String url) {
        return url;
    }

    @Override
    protected boolean addAuthHeader() {
        return true;
    }

    public LiveCourseResult getLiveClassList(String nextUrl) throws Exception {
        return httpGet(nextUrl, LiveCourseResult.class);
    }
}
