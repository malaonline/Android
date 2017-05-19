package com.malalaoshi.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.WrongTopicActivity;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.entity.TopicSubject;

/**
 * Created by donald on 2017/5/9.
 */

public class TopicSubjectsAdapter extends BaseRecycleAdapter<TopicSubjectsAdapter.TopicSubjectViewHolder, TopicSubject> {


    public TopicSubjectsAdapter(Context context) {
        super(context);
    }

    @Override
    public TopicSubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(mContext);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.topMargin = MiscUtil.dp2px(10);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setCompoundDrawablePadding(MiscUtil.dp2px(18f));
        textView.setTextColor(MiscUtil.getColor(R.color.color_black_656970));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setLines(1);
        return new TopicSubjectViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(TopicSubjectViewHolder holder, final int position) {
        TopicSubject subject = getItem(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WrongTopicActivity.launch(mContext, position, dataList);
            }
        });
        holder.setup(subject);
    }

    class TopicSubjectViewHolder extends RecyclerView.ViewHolder {

        TopicSubjectViewHolder(View itemView) {
            super(itemView);
        }

        public void setup(TopicSubject subject) {
            TextView textView = (TextView) itemView;
            if (textView == null) return;
            String subjectName = subject.getSubject();
            int topicNum = subject.getTopicNum();
            if ("英语".equals(subjectName)){
                textView.setCompoundDrawablesWithIntrinsicBounds(null, MiscUtil.getDrawable(R.drawable.topic_english), null, null);
            }else if ("数学".equals(subjectName)){
                textView.setCompoundDrawablesWithIntrinsicBounds(null, MiscUtil.getDrawable(R.drawable.topic_math), null, null);
            }
            textView.setText(topicNum + "题");
        }
    }

}
