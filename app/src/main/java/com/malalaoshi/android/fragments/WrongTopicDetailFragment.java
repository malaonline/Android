package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapters.TopicAnswerAdapter;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.entity.TopicAnswer;
import com.malalaoshi.android.entity.WrongTopic;
import com.malalaoshi.android.ui.widgets.FullyLinearLayoutManager;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by donald on 2017/5/10.
 */

public class WrongTopicDetailFragment extends BaseFragment {

    @Bind(R.id.tv_topic_detail_type)
    TextView mTvTopicDetailType;
    @Bind(R.id.tv_topic_detail_idex)
    TextView mTvTopicDetailIdex;
    @Bind(R.id.tv_topic_detail_description)
    TextView mTvTopicDetailDescription;
    @Bind(R.id.tv_topic_detail_question)
    TextView mTvTopicDetailQuestion;
    @Bind(R.id.rv_topic_detail_answer)
    RecyclerView mRvTopicDetailAnswer;
    @Bind(R.id.tv_topic_detail_answer)
    TextView mTvTopicDetailAnswer;
    @Bind(R.id.tv_topic_detail_analysis)
    TextView mTvTopicDetailAnalysis;
    private int mSelectedItem;
    private TopicAnswerAdapter mAnswerAdapter;
    private static final String ITEM_TOTAL_COUNT = "item_total_count";
    private static final String SELECTED_ITEM = "selected_item";
    private static final String WRONG_TOPIC = "wrong_topic";
    private int mTotalCount;
    private WrongTopic mWrongTopic;

    public static WrongTopicDetailFragment newInstance(int position, int count, WrongTopic wrongTopic) {
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_ITEM, position);
        bundle.putInt(ITEM_TOTAL_COUNT, count);
        bundle.putSerializable(WRONG_TOPIC, wrongTopic);
        WrongTopicDetailFragment wrongTopicDetailFragment = new WrongTopicDetailFragment();
        wrongTopicDetailFragment.setArguments(bundle);
        return wrongTopicDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mSelectedItem = arguments.getInt(SELECTED_ITEM);
        mTotalCount = arguments.getInt(ITEM_TOTAL_COUNT);
        mWrongTopic = (WrongTopic) arguments.getSerializable(WRONG_TOPIC);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wrong_topic_detail, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        LinearLayoutManager layoutManager = new FullyLinearLayoutManager(getContext());
        mRvTopicDetailAnswer.setLayoutManager(layoutManager);
        mAnswerAdapter = new TopicAnswerAdapter(getContext());

        mRvTopicDetailAnswer.setAdapter(mAnswerAdapter);
        if (mWrongTopic == null) return;
        setup();
    }

    private void setup() {
        WrongTopic.QuestionGroup question_group = mWrongTopic.getQuestion_group();
        if (question_group != null) {
            mTvTopicDetailType.setText(question_group.getTitle());
            mTvTopicDetailDescription.setText(question_group.getDescription());
        }
        mTvTopicDetailIdex.setText((mSelectedItem + 1) + "/" + mTotalCount);
        WrongTopic.Question question = mWrongTopic.getQuestion();
        if (question != null) {
            mTvTopicDetailQuestion.setText((mSelectedItem + 1)+ "." + question.getTitle());
            List<TopicAnswer> optionList = question.getOptions();
            int solution = question.getSolution();
            String[] options = new String[]{ "A", "B", "C", "D", "E", "F", "G", "H", "I",
                    "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                    "W", "X", "Y", "Z"};
            mAnswerAdapter.setSelected(solution);
            mAnswerAdapter.resetData(optionList);
            mTvTopicDetailAnalysis.setText(question.getExplanation());
            for (int i = 0; i < optionList.size(); i++) {
                if (optionList.get(i).getId() ==  solution){
                    mTvTopicDetailAnswer.setText("【试题解析】 答案 " + options[i]);
                    break;
                }
            }

        }
    }

    public void onEventMainThread(List<WrongTopic> wrongTopics){
        mWrongTopic = wrongTopics.get(mSelectedItem);
        setup();
    }


    @Override
    public String getStatName() {
        return "错题详情";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
