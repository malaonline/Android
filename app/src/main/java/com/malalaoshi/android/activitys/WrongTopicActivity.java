package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.utils.StatusBarCompat;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.entity.TopicSubject;
import com.malalaoshi.android.fragments.WrongTopicFragment;
import com.malalaoshi.android.listener.OnItemClickListener;
import com.malalaoshi.android.ui.dialogs.TopicSubjectsDialog;
import com.malalaoshi.android.utils.FragmentUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 错题列表
 * Created by donald on 2017/5/9.
 */

public class WrongTopicActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener, OnItemClickListener {

    @Bind(R.id.tbv_wrong_topic_title)
    TitleBarView mTbvWrongTopicTitle;
    @Bind(R.id.tv_topic_list_title_num)
    TextView mTvTopicListTitleNum;
    @Bind(R.id.fl_wrong_topic_content)
    FrameLayout mFlWrongTopicContent;
    private int mPosition;
    private static final String SELECTED_POSITION = "selected_position";
    private static final String TOPIC_SUBJECT_LIST = "topic_subject_list";
    private ArrayList<TopicSubject> mSubjects;
    private WrongTopicFragment mWrongTopicFragment;
    private int mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_topic);
        ButterKnife.bind(this);
        StatusBarCompat.compat(this);
        mTbvWrongTopicTitle.setOnTitleBarClickListener(this);
        init();
        if (savedInstanceState == null) {
            mWrongTopicFragment = WrongTopicFragment.newInstance(mId);
            FragmentUtil.openFragment(R.id.fl_wrong_topic_content, getSupportFragmentManager(), null,
                    mWrongTopicFragment, WrongTopicFragment.class.getName());
        }

    }

    private void init() {
        Intent intent = getIntent();
        if (intent != null){
            mPosition = intent.getIntExtra(SELECTED_POSITION, -1);
            mSubjects = (ArrayList<TopicSubject>) intent.getSerializableExtra(TOPIC_SUBJECT_LIST);
        }
        if (mSubjects != null && mPosition != -1){
            TopicSubject topicSubject = mSubjects.get(mPosition);
            mId = topicSubject.getId();
            mTvTopicListTitleNum.setText("科目："+ topicSubject.getSubject()+" "+topicSubject.getTopicNum());
        }
    }

    @Override
    protected String getStatName() {
        return "错题本";
    }

    @OnClick(R.id.tv_topic_list_title_num)
    public void onViewClicked() {
        TopicSubjectsDialog topicSubjectsDialog = new TopicSubjectsDialog();
        topicSubjectsDialog.setOnItemClickListener(this);
        topicSubjectsDialog.setData(mSubjects, mPosition);
        topicSubjectsDialog.show(getSupportFragmentManager(), "TopicSubjectsDialog");
    }

    @Override
    public void onTitleLeftClick() {
        finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    @Override
    public void itemClick(int position) {
        mPosition = position;
        TopicSubject topicSubject = mSubjects.get(position);
        mTvTopicListTitleNum.setText("科目："+ topicSubject.getSubject()+" "+topicSubject.getTopicNum());
        mWrongTopicFragment.setSubject(topicSubject.getId());
    }
    public  static void launch(Context context, int position, List<TopicSubject> sujects){
        Intent intent = new Intent(context, WrongTopicActivity.class);
        intent.putExtra(SELECTED_POSITION, position);
        intent.putExtra(TOPIC_SUBJECT_LIST, (Serializable)sujects);
        context.startActivity(intent);
    }
}
