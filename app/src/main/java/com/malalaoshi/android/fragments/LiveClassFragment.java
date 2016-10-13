package com.malalaoshi.android.fragments;


import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.base.BaseRefreshFragment;
import com.malalaoshi.android.network.result.LiveClassResult;

/**
 * Created by kang on 16/10/13.
 */

public class LiveClassFragment extends BaseRefreshFragment<LiveClassResult> {
    @Override
    public String getStatName() {
        return null;
    }

    @Override
    protected BaseRecycleAdapter createAdapter() {
        return null;
    }

    @Override
    protected LiveClassResult refreshRequest() throws Exception {
        return null;
    }

    @Override
    protected LiveClassResult loadMoreRequest() throws Exception {
        return null;
    }

    @Override
    protected void afterCreateView() {

    }
}
