package com.malalaoshi.android.network.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.WrongTopicList;
import com.malalaoshi.android.network.result.WrongTopicResult;

/**
 * Created by donald on 2017/5/15.
 */

public class WrongTopicApi extends BaseApi{
    
    private static final String URL_GET_TOPICS = "/api/v1/exercise";
    private static final String URL_GET_SUBJECTS = "/api/v1/my_center";
    private boolean isMore = false;
    private boolean isAddAuthHeader = true;

    @Override
    protected String getPath() {
        return URL_GET_SUBJECTS;
    }

    @Override
    protected String getUrl(String url) {
        if (isMore) return url;
        return super.getUrl(url);
    }

    @Override
    protected boolean addAuthHeader() {
        return isAddAuthHeader;
    }

    public WrongTopicResult getSubject() throws Exception {
        isMore = false;
        isAddAuthHeader = true;
        return httpGet(getPath(), WrongTopicResult.class);
    }
    public WrongTopicList getTopics(int subjectId) throws Exception{
        isMore = false;
        isAddAuthHeader = true;
        String url = URL_GET_TOPICS + "?subject="+subjectId;
        return httpGet(url, WrongTopicList.class);
    }

    public WrongTopicList getMoreTopic(String url) throws Exception {
        isMore = true;
        isAddAuthHeader = true;
        return httpGet(url, WrongTopicList.class);
    }

    public WrongTopicList getAllTopics(int subjectId) throws Exception {
        isAddAuthHeader = false;
        isMore = false;
        String url = URL_GET_TOPICS + "?subject="+subjectId;
        return httpGet(url, WrongTopicList.class);
    }
}
