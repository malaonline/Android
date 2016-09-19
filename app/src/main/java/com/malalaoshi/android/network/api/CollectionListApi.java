package com.malalaoshi.android.network.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.network.result.TeacherListResult;

/**
 * 收藏老师列表
 * Created by tianwei on 4/17/16.
 */
public class CollectionListApi extends BaseApi {

    @Override
    protected String getPath() {
        return "/api/v1/favorites";
    }

    @Override
    protected boolean addAuthHeader() {
        return true;
    }

    public TeacherListResult getTeacherList()
            throws Exception {
        return httpGet(getPath() , TeacherListResult.class);
    }
}
