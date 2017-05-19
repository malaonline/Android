package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapters.FragmentGroupAdapter;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.utils.StatusBarCompat;
import com.malalaoshi.android.core.view.ShadowHelper;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.entity.WrongTopic;
import com.malalaoshi.android.fragments.WrongTopicDetailFragment;

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

    @Bind(R.id.tbv_topic_detail_title)
    TitleBarView mTbvTopicDetailTitle;
    @Bind(R.id.vp_topic_detail_content)
    ViewPager mVpTopicDetailContent;

    private static final String ITEM_TOTAL_COUNT= "item_total_count";
    private static final String SELECTED_ITEM = "selected_item";
    private static final String WRONG_TOPIC = "wrong_topic";

    private Map<Integer, WrongTopicDetailFragment> mFragments = new HashMap<>();
    private FragmentGroupAdapter mAdapter;
    private int mCount;
    private int mSelectedItem;
    private List<WrongTopic> mTopics;

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
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null){
            mCount = intent.getIntExtra(ITEM_TOTAL_COUNT, 0);
            mSelectedItem = intent.getIntExtra(SELECTED_ITEM, 0);
            mTopics = (List<WrongTopic>) intent.getSerializableExtra(WRONG_TOPIC);
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
        WrongTopicDetailFragment detailFragment = mFragments.get(position);
        if (detailFragment == null){
            detailFragment = WrongTopicDetailFragment.newInstance(position, mCount, mTopics.get(position));
            mFragments.put(position,detailFragment);
        }
        return detailFragment;
    }

    @Override
    public int getFragmentCount() {
        return mCount;
    }
    public static void launch(Context context, int count, int selected, List<WrongTopic> wrongTopics){
        Intent intent = new Intent(context, WrongTopicDetailActivity.class);
        intent.putExtra(ITEM_TOTAL_COUNT, count);
        intent.putExtra(SELECTED_ITEM, selected);
        intent.putExtra(WRONG_TOPIC, (Serializable) wrongTopics);
        context.startActivity(intent);
    }
}
