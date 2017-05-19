package com.malalaoshi.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.entity.TopicAnswer;

/**
 * Created by donald on 2017/5/11.
 */

public class TopicAnswerAdapter extends BaseRecycleAdapter<TopicAnswerAdapter.TopicAnswerViewHolder, TopicAnswer> {

    private int mSelected;

    public TopicAnswerAdapter(Context context) {
        super(context);
    }

    @Override
    public TopicAnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(mContext);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = MiscUtil.dp2px(12f);
        textView.setLayoutParams(layoutParams);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(MiscUtil.getColor(R.color.color_black_444444));
        textView.setLines(1);
        return new TopicAnswerViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(TopicAnswerViewHolder holder, int position) {
        holder.setup(getItem(position), position, mSelected);
    }

    class TopicAnswerViewHolder extends RecyclerView.ViewHolder {
        String[] options = new String[]{ "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};


        public TopicAnswerViewHolder(View itemView) {
            super(itemView);
        }

        public void setup(TopicAnswer answer, int position, int selected) {
            if (answer == null) return;
            TextView textView = (TextView) this.itemView;
            if (selected == answer.getId()){
                textView.setTextColor(MiscUtil.getColor(R.color.main_color));
            }else {
                textView.setTextColor(MiscUtil.getColor(R.color.color_black_444444));
            }
            textView.setText(options[position]+"."+answer.getText());

        }
    }
    public void setSelected(int selected){
        mSelected = selected;
    }
}
