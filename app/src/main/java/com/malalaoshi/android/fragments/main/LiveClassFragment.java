package com.malalaoshi.android.fragments.main;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malalaoshi.android.adapters.LiveClassAdapter;
import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.base.BaseRefreshFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.entity.LiveClass;
import com.malalaoshi.android.network.api.LiveClassListApi;
import com.malalaoshi.android.network.api.MoreLiveClassListApi;
import com.malalaoshi.android.network.result.LiveClassResult;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/10/13.
 */

public class LiveClassFragment extends BaseRefreshFragment<LiveClassResult> {
    private String nextUrl;

    private LiveClassAdapter adapter;

    public static LiveClassFragment newInstance() {
        LiveClassFragment fragment = new LiveClassFragment();
        return fragment;
    }

    public void refresh(){
        MalaContext.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                autoRefresh();
            }
        });
    }

    @Override
    protected BaseRecycleAdapter createAdapter() {
        adapter = new LiveClassAdapter(getContext());
        return adapter;
    }

    @Override
    protected LiveClassResult refreshRequest() throws Exception {
        return new LiveClassListApi().getLiveClassList();
    }

    @Override
    protected LiveClassResult loadMoreRequest() throws Exception {
        return new MoreLiveClassListApi().getLiveClassList(nextUrl);
    }

    @Override
    protected void afterCreateView() {

    }

    @Override
    protected void refreshFinish(LiveClassResult response) {
        super.refreshFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

    @Override
    protected void loadMoreFinish(LiveClassResult response) {
        super.loadMoreFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.getEventType()) {
            case BusEvent.BUS_EVENT_UPDATE_SCHOOL_SUCCESS:
                refresh();
                Log.d("LiveClassFragment","start loadDataBackground");
                break;
        }
    }


    @Override
    public String getStatName() {
        return "双师直播";
    }
}
