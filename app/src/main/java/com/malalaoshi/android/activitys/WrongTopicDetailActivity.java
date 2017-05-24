package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapters.FragmentGroupAdapter;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.utils.StatusBarCompat;
import com.malalaoshi.android.core.view.ShadowHelper;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.entity.WrongTopic;
import com.malalaoshi.android.entity.WrongTopicList;
import com.malalaoshi.android.fragments.WrongTopicDetailFragment;
import com.malalaoshi.android.network.api.WrongTopicApi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by donald on 2017/5/10.
 */

public class WrongTopicDetailActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener, FragmentGroupAdapter.IFragmentGroup {

    private static final String ITEM_TOTAL_COUNT= "item_total_count";
    private static final String SELECTED_ITEM = "selected_item";
    private static final String WRONG_TOPIC = "wrong_topic";
    private static final String SUBJECT_ID = "subject_id";

    @Bind(R.id.tbv_topic_detail_title)
    TitleBarView mTbvTopicDetailTitle;

    @Bind(R.id.vp_topic_detail_content)
    ViewPager mVpTopicDetailContent;

    private Map<Integer, WrongTopicDetailFragment> mFragments = new HashMap<>();
    private FragmentGroupAdapter mAdapter;
    private int mCount;
    private int mSelectedItem;
    private List<WrongTopic> mTopics;
    private int mSubjectId;
    private String mNextUrl = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_topic_detail);
        ButterKnife.bind(this);
        StatusBarCompat.compat(this);
        initIntent();
        mTbvTopicDetailTitle.setOnTitleBarClickListener(this);
        mAdapter = new FragmentGroupAdapter(this, getSupportFragmentManager(), this);
        mVpTopicDetailContent.setAdapter(mAdapter);
        ShadowHelper.setDrawShadow(this, 8, mVpTopicDetailContent);
        mVpTopicDetailContent.setCurrentItem(mSelectedItem);
//        loadData();
    }

    private void loadData() {
        ApiExecutor.exec(new AllTopicTask(this));
    }

    private void dealResponse(WrongTopicList response) {
        if (TextUtils.isEmpty(mNextUrl)){
            mTopics.clear();
        }
        List<WrongTopic> results = response.getResults();
        mNextUrl = response.getNext();
        if (results == null) return;
        mTopics.addAll(results);
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null){
            mCount = intent.getIntExtra(ITEM_TOTAL_COUNT, 0);
            mSelectedItem = intent.getIntExtra(SELECTED_ITEM, 0);
            mTopics = (List<WrongTopic>) intent.getSerializableExtra(WRONG_TOPIC);
            mSubjectId = intent.getIntExtra(SUBJECT_ID, -1);
        }
    }

    @Override
    protected String getStatName() {
        return "错题详情";
    }


    @Override
    public void onTitleLeftClick() {
        finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    @Override
    public Fragment createFragment(int position) {
        Log.e("WrongTopicDetail", "createFragment: ");
        WrongTopicDetailFragment detailFragment = mFragments.get(position);
        if (detailFragment == null){
            WrongTopic topic = null;
            if (position < mTopics.size()){
                topic = mTopics.get(position);
            }
            detailFragment = WrongTopicDetailFragment.newInstance(position, mCount, topic);
            mFragments.put(position,detailFragment);
        }
        return detailFragment;
    }

    @Override
    public int getFragmentCount() {
        return mCount;
    }
    public static void launch(Context context, int count, int selected, List<WrongTopic> wrongTopics, int subjectId){
        Intent intent = new Intent(context, WrongTopicDetailActivity.class);
        intent.putExtra(ITEM_TOTAL_COUNT, count);
        intent.putExtra(SELECTED_ITEM, selected);
        intent.putExtra(WRONG_TOPIC, (Serializable) wrongTopics);
        intent.putExtra(SUBJECT_ID, subjectId);
        context.startActivity(intent);
    }


    private static final class AllTopicTask extends BaseApiContext<WrongTopicDetailActivity, WrongTopicList> {
        public AllTopicTask(WrongTopicDetailActivity wrongTopicDetailActivity) {
            super(wrongTopicDetailActivity);
        }

        @Override
        public WrongTopicList request() throws Exception {
            return new WrongTopicApi().getAllTopics(get().mSubjectId);
        }

        @Override
        public void onApiSuccess(@NonNull WrongTopicList response) {
            get().dealResponse(response);
        }
    }
    private static final class MoreTopicTask extends BaseApiContext<WrongTopicDetailActivity, WrongTopicList> {
        public MoreTopicTask(WrongTopicDetailActivity wrongTopicDetailActivity) {
            super(wrongTopicDetailActivity);
        }

        @Override
        public WrongTopicList request() throws Exception {
            return new WrongTopicApi().getMoreTopic(get().mNextUrl);
        }

        @Override
        public void onApiSuccess(@NonNull WrongTopicList response) {
            get().dealResponse(response);
        }
    }

}
