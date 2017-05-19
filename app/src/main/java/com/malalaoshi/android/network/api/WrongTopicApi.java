package com.malalaoshi.android.network.api;

import android.util.Log;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.WrongTopicList;
import com.malalaoshi.android.network.result.WrongTopicResult;

/**
 * Created by donald on 2017/5/15.
 */

public class WrongTopicApi extends BaseApi{
    
    private static final String URL_GET_TOPICS = "/api/v1/exercise";
    private static final String URL_GET_SUBJECTS = "/api/v1/my_center";
    
    @Override
    protected String getPath() {
        return URL_GET_SUBJECTS;
    }

    public WrongTopicResult getSubject() throws Exception {
        return httpGet(getPath(), WrongTopicResult.class);
    }
    public WrongTopicList getTopics(int subjectId) throws Exception{
        String url = URL_GET_TOPICS + "?subject="+subjectId;
        Log.e("WrongTopicApi", "getTopics: "+url);
        return httpGet(url, WrongTopicList.class);
    }

    public WrongTopicList getMoreTopic(String url) throws Exception {
        Log.e("WrongTopicApi", "getMoreTopic: "+url);
        return httpGet(url, WrongTopicList.class);
    }
}
