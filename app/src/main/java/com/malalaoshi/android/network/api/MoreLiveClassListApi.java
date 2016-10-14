package com.malalaoshi.android.network.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.network.result.LiveClassResult;
import com.malalaoshi.android.network.result.TeacherListResult;

/**
 * More teacher list api
 * Created by tianwei on 4/17/16.
 */
public class MoreLiveClassListApi extends BaseApi {

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
        return false;
    }

    public LiveClassResult getLiveClassList(String nextUrl) throws Exception {
        return httpGet(nextUrl, LiveClassResult.class);
    }
}
