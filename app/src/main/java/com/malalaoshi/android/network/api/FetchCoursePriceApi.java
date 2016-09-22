package com.malalaoshi.android.network.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.utils.JsonUtil;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.network.result.CoursePriceListResult;

/**
 * Created by kang on 16/5/10.
 */
public class FetchCoursePriceApi extends BaseApi {
    private static final String URL_ORDERS = "/api/v1/teacher/%d/school/%d/prices/";

    @Override
    protected String getPath() {
        return URL_ORDERS;
    }

    public CoursePriceListResult get(Long teacherId, Long schoolId) throws Exception {
        String url = String.format(getPath(), teacherId, schoolId);
        return httpGet(url, CoursePriceListResult.class);
    }
}
