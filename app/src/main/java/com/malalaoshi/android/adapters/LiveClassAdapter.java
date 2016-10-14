package com.malalaoshi.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.LiveClassInfoActivity;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.entity.LiveClass;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/10/13.
 */

public class LiveClassAdapter extends BaseRecycleAdapter<LiveClassAdapter.ViewHolder, LiveClass> {
    public LiveClassAdapter(Context context) {
        super(context);
    }

    @Override
    public LiveClassAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.liveclass_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(LiveClassAdapter.ViewHolder holder, int position) {
        holder.update(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.iv_lecturer_avator)
        protected MalaImageView ivLecturerAvator;
        @Bind(R.id.iv_assist_avator)
        protected MalaImageView ivAssistAvator;
        @Bind(R.id.tv_class_type)
        protected TextView tvClassType;
        @Bind(R.id.tv_lecturer_name)
        protected TextView tvLecturerName;
        @Bind(R.id.tv_lecture_honorary)
        protected TextView tvLectureHonorary;
        @Bind(R.id.tv_assist_name)
        protected TextView tvAssistName;
        @Bind(R.id.tv_live_class)
        protected TextView tvLiveClass;
        @Bind(R.id.tv_class_time)
        protected TextView tvClassTime;
        @Bind(R.id.tv_grade)
        protected TextView tvGrade;
        @Bind(R.id.tv_total_price)
        protected TextView tvTotalPrice;

        protected LiveClass liveClass;
        protected View view;

        protected ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }

        protected void update(LiveClass liveClass) {
            view.setOnClickListener(this);
            this.liveClass = liveClass;
        }

        private void setTotalPrice(Long money, Long count){
            String text = String.format("￥%d\\%d次",money,count);
            SpannableString styledText = new SpannableString(text);
            styledText.setSpan(new TextAppearanceSpan(tvTotalPrice.getContext(), R.style.LiveclassPriceStyle), 0, text.indexOf("\\")+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new TextAppearanceSpan(tvTotalPrice.getContext(), R.style.LiveclassStuNum), text.indexOf("\\")+1, text.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvTotalPrice.setText(styledText, TextView.BufferType.SPANNABLE);
        }

        @Override
        public void onClick(View v) {
            LiveClassInfoActivity.open(this.view.getContext(), liveClass.getId() != null ? String.valueOf(liveClass.getId()) : null);
        }
    }
}
