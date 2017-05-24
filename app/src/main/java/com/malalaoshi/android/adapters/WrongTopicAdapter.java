package com.malalaoshi.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.utils.DateUtils;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.entity.WrongTopic;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by donald on 2017/5/10.
 */

public class WrongTopicAdapter extends BaseRecycleAdapter<WrongTopicAdapter.WrongTopicViewHolder, WrongTopic> {

    private static final int ITEM_BG_WHITE = 932;
    private static final int ITEM_BG_GRAY = 701;

    public WrongTopicAdapter(Context context) {
        super(context);
    }

    @Override
    public WrongTopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_wrong_topic, parent, false);
        if (viewType == ITEM_BG_WHITE) {
            view.setBackgroundColor(MiscUtil.getColor(R.color.white));
        } else {
            view.setBackgroundColor(MiscUtil.getColor(R.color.color_white_f8f8f8));
        }
        return new WrongTopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WrongTopicViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(dataList, position);
            }
        });
        holder.setup(getItem(position), position);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? ITEM_BG_WHITE : ITEM_BG_GRAY;
    }

    class WrongTopicViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_topic_group_name)
        TextView mTvTopicGroupName;
        @Bind(R.id.tv_topic_time)
        TextView mTvTopicTime;
        @Bind(R.id.tv_topic_question)
        TextView mTvTopicQuestion;

        public WrongTopicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setup(WrongTopic topic, int position) {
            if (topic == null) return;
            WrongTopic.QuestionGroup question_group = topic.getQuestion_group();
            if (question_group != null) {
                mTvTopicGroupName.setText(question_group.getTitle());
            }
            mTvTopicTime.setText(DateUtils.format(topic.getUpdated_at()));
            WrongTopic.Question question = topic.getQuestion();
            if (question != null) {
                mTvTopicQuestion.setText((position + 1) + "." + question.getTitle());
            }
        }
    }
}
