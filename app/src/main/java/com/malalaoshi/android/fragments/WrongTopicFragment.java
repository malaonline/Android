package com.malalaoshi.android.fragments;

import android.view.View;

import com.hitomi.cslibrary.CrazyShadow;
import com.malalaoshi.android.activitys.WrongTopicDetailActivity;
import com.malalaoshi.android.adapters.WrongTopicAdapter;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.base.BaseRefreshFragment;
import com.malalaoshi.android.core.base.OnItemClickListener;
import com.malalaoshi.android.core.view.ShadowHelper;
import com.malalaoshi.android.entity.WrongTopic;
import com.malalaoshi.android.entity.WrongTopicList;
import com.malalaoshi.android.network.api.WrongTopicApi;

import java.util.List;

/**
 * Created by donald on 2017/5/10.
 */

public class WrongTopicFragment extends BaseRefreshFragment<WrongTopicList> implements OnItemClickListener {

    private String mNextUrl;
    private WrongTopicApi mWrongTopicApi;
    private static int mSubjectId = -1;
    private CrazyShadow mShadow;
    private WrongTopicAdapter mWrongTopicAdapter;

    public static WrongTopicFragment newInstance(int id) {
        mSubjectId = id;
        return new WrongTopicFragment();
    }

    @Override
    public String getStatName() {
        return "错题列表";
    }


    @Override
    protected BaseRecycleAdapter createAdapter() {
        mWrongTopicAdapter = new WrongTopicAdapter(getContext());
        mWrongTopicAdapter.setOnItemClickListener(this);
        return mWrongTopicAdapter;
    }

    @Override
    protected WrongTopicList refreshRequest() throws Exception {
        mWrongTopicApi = new WrongTopicApi();
        return mWrongTopicApi.getTopics(mSubjectId);
    }

    @Override
    protected WrongTopicList loadMoreRequest() throws Exception {
        if (mWrongTopicApi == null)
            mWrongTopicApi = new WrongTopicApi();
        return mWrongTopicApi.getMoreTopic(mNextUrl);
    }

    @Override
    protected void afterCreateView() {

    }

    @Override
    protected void refreshFinish(WrongTopicList response) {
        super.refreshFinish(response);
        if (response != null) {
            mNextUrl = response.getNext();
        }
    }

    @Override
    protected void loadMoreFinish(WrongTopicList response) {
        super.loadMoreFinish(response);
        if (response != null) {
            mNextUrl = response.getNext();
        }
    }

    @Override
    protected void setShadow() {
        if (mShadow != null) {
            if (recyclerView.getVisibility() == View.VISIBLE) {
                mShadow.show();
            } else {
                mShadow.hide();
            }
        } else {
            if (recyclerView.getVisibility() == View.VISIBLE)
                mShadow = ShadowHelper.setDrawShadow(getContext(), 8, recyclerView);
        }
    }

    public void setSubject(int subjectId) {
        mSubjectId = subjectId;
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoRefresh();
            }
        }, 100);
    }


    @Override
    public void onClick(Object o, int position) {
        List<WrongTopic> wrongTopics = (List<WrongTopic>) o;
        if (wrongTopics == null || wrongTopics.size() <= 0) return;
        WrongTopicDetailActivity.launch(mContext, mItemTotalCount, position, wrongTopics);
//        Intent intent = new Intent(mContext, WrongTopicDetailActivity.class);
//        intent.putExtra("item_total_count", mItemTotalCount);
//        intent.putExtra("selected_item", position);
//        intent.putExtra("wrong_topic", (Serializable) wrongTopics);
//        mContext.startActivity(intent);

    }
}
