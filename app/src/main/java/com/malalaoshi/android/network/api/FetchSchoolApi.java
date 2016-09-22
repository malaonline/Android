package com.malalaoshi.android.network.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.network.result.SchoolListResult;

import java.util.HashMap;

/**
 * Get shcool list
 * Created by tianwei on 4/17/16.
 */
public class FetchSchoolApi extends BaseApi {


    private static final String URL_SCHOOL = "/api/v1/schools/%d";
    public static final String REGION = "region";

    @Override
    protected String getPath() {
        return URL_SCHOOL;
    }

    @Override
    protected boolean addAuthHeader() {
        return false;
    }

    @Override
    protected HashMap<String, String> getHeaders() {
        HashMap<String, String> map = new HashMap<>();
        map.put(REGION, UserManager.getInstance().getToken());
        return map;
    }

    public School get(long schoolId) throws Exception {
        return httpGet(String.format(getPath(),schoolId), School.class);
    }
}
