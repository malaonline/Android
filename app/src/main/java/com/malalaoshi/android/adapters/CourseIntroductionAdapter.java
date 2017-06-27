package com.malalaoshi.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.utils.MiscUtil;

/**
 * Created by donald on 2017/6/26.
 */

public class CourseIntroductionAdapter extends BaseRecycleAdapter<CourseIntroductionAdapter.IntroductionViewHolder, String>{


    public CourseIntroductionAdapter(Context context) {
        super(context);
    }

    @Override
    public IntroductionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(mContext);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = MiscUtil.dp2px(10);
        textView.setLayoutParams(layoutParams);
        textView.setLines(1);
        textView.setTextColor(MiscUtil.getColor(R.color.color_black_333333));
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shape_circle_blue_dot, 0,0,0);
        textView.setCompoundDrawablePadding(MiscUtil.dp2px(12));
        return new IntroductionViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(IntroductionViewHolder holder, int position) {
        holder.setup(dataList.get(position));
    }

    class IntroductionViewHolder extends RecyclerView.ViewHolder {
        public IntroductionViewHolder(View itemView) {
            super(itemView);
        }


        public void setup(String introduction) {
            TextView textView = (TextView) this.itemView;
            textView.setText(introduction);
        }
    }
}
