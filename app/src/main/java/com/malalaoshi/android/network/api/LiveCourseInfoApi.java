package com.malalaoshi.android.network.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.LiveCourse;
import com.malalaoshi.android.entity.Teacher;

/**
 * Teacher info api
 * Created by tianwei on 4/17/16.
 */
public class LiveCourseInfoApi extends BaseApi {


    private static final String URL_COURSE_ID = "/api/v1/teachers/%s";

    @Override
    protected String getPath() {
        return URL_COURSE_ID;
    }

    @Override
    protected boolean addAuthHeader() {
        return true;
    }

    public LiveCourse get(String courseId) throws Exception {
        String url = String.format(getPath(), courseId);
        return httpGet(url, LiveCourse.class);
    }
}
