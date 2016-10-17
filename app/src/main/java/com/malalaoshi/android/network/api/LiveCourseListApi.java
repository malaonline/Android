package com.malalaoshi.android.network.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.network.result.LiveCourseResult;

/**
 * 老师列表页
 * Created by tianwei on 4/17/16.
 */
public class LiveCourseListApi extends BaseApi {

    @Override
    protected String getPath() {
        return "/api/v1/teachers";
    }

    @Override
    protected boolean addAuthHeader() {
        return false;
    }

    public LiveCourseResult getLiveClassList()
            throws Exception {
        String subUrl = "";
        boolean hasParam = false;
        Long cityId = UserManager.getInstance().getCityId();
        Long schoolId = UserManager.getInstance().getSchoolId();
        if (cityId!=null&&cityId>0){
            subUrl += "?region=" + cityId;
            hasParam = true;
        }
        if (schoolId!=null&&schoolId>0) {
            subUrl += hasParam ? "&school=" : "?school=";
            subUrl += schoolId;
            hasParam = true;
        }

        return httpGet(getPath() + subUrl, LiveCourseResult.class);
    }
}
