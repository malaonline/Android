package com.malalaoshi.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.utils.MiscUtil;

/**
 * Created by donald on 2017/6/22.
 */
public class CourseServiceAdapter extends BaseRecycleAdapter<CourseServiceAdapter.ServiceViewHolder, String> {
    public CourseServiceAdapter(Context context) {
        super(context);
    }
    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(mContext);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = MiscUtil.dp2px(20);
        textView.setLayoutParams(layoutParams);
        textView.setTextColor(MiscUtil.getColor(R.color.color_black_a0a3ab));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_course_service, 0, 0, 0);
        textView.setCompoundDrawablePadding(MiscUtil.dp2px(6));
        textView.setLines(1);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        return new ServiceViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder holder, int position) {
        holder.setup(dataList.get(position),position);
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        public ServiceViewHolder(View itemView) {
            super(itemView);
        }

        public void setup(String item, int position) {
            TextView textView = (TextView) this.itemView;
            textView.setText(item);
        }
    }
}
