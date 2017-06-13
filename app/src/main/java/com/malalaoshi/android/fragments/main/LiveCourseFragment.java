package com.malalaoshi.android.fragments.main;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malalaoshi.android.adapters.LiveCourseAdapter;
import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.base.BaseRefreshFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.network.api.LiveCourseListApi;
import com.malalaoshi.android.network.api.MoreLiveCourseListApi;
import com.malalaoshi.android.network.result.LiveCourseResult;

import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/10/13.
 */

public class LiveCourseFragment extends BaseRefreshFragment<LiveCourseResult> {
    private String nextUrl;

    private LiveCourseAdapter adapter;

    public static LiveCourseFragment newInstance() {
        LiveCourseFragment fragment = new LiveCourseFragment();
        return fragment;
    }

    public void refresh(){
        recyclerView.scrollToPosition(0);
        MalaContext.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                autoRefresh();
            }
        });
    }

    @Override
    protected BaseRecycleAdapter createAdapter() {
        adapter = new LiveCourseAdapter(getContext());
        return adapter;
    }

    @Override
    protected LiveCourseResult refreshRequest() throws Exception {
        return new LiveCourseListApi().getLiveClassList();
    }

    @Override
    protected LiveCourseResult loadMoreRequest() throws Exception {
        return new MoreLiveCourseListApi().getLiveClassList(nextUrl);
    }

    @Override
    protected void afterCreateView() {
        setEmptyViewText("当前课程正在开通中，敬请期待");
    }

    @Override
    protected void refreshFinish(LiveCourseResult response) {
        super.refreshFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

//    @Override
//    protected String getEmptyString() {
//        return "当前课程正在开通中，敬请期待";
//    }

    @Override
    protected void loadMoreFinish(LiveCourseResult response) {
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
                break;
        }
    }


    @Override
    public String getStatName() {
        return "双师直播";
    }

    @Override
    protected int getStableItem() {
        return 1;
    }
}
