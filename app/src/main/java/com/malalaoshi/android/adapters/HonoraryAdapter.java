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

public class HonoraryAdapter extends BaseRecycleAdapter<HonoraryAdapter.HonoraryViewHolder, String> {

    public HonoraryAdapter(Context context) {
        super(context);
    }

    @Override
    public HonoraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(mContext);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = MiscUtil.dp2px(10);
        textView.setLayoutParams(layoutParams);
        textView.setTextColor(MiscUtil.getColor(R.color.color_black_333333));
        return new HonoraryViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(HonoraryViewHolder holder, int position) {
        holder.setup(dataList.get(position));
    }

    class HonoraryViewHolder extends RecyclerView.ViewHolder {
        public HonoraryViewHolder(View itemView) {
            super(itemView);
        }

        public void setup(String honorary) {
            TextView textView = (TextView) this.itemView;
            textView.setText(honorary);
        }
    }
}
