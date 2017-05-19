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
import com.malalaoshi.android.entity.MemberService;

/**
 * 会员服务条目adapter
 * Created by donald on 2017/5/12.
 */

public class MemberServiceAdapter extends BaseRecycleAdapter<MemberServiceAdapter.MemberServiceViewHolder, MemberService> {
    public MemberServiceAdapter(Context context) {
        super(context);
    }

    @Override
    public MemberServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(mContext);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = MiscUtil.dp2px(18);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setCompoundDrawablePadding(MiscUtil.dp2px(12f));
        textView.setTextColor(MiscUtil.getColor(R.color.color_black_333333));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setLines(1);
        return new MemberServiceViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(final MemberServiceViewHolder holder, final int position) {
        if (dataList.size() > 0) {
            MemberService service = getItem(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClick(holder, position);
                    }
                }
            });
            holder.setup(service);
        }
    }

    class MemberServiceViewHolder extends RecyclerView.ViewHolder {

        MemberServiceViewHolder(View itemView) {
            super(itemView);
        }

        public void setup(MemberService service) {
            TextView textView = (TextView) itemView;
            if (textView == null) return;
            textView.setCompoundDrawablesWithIntrinsicBounds(null, MiscUtil.getDrawable(service.getResId()), null, null);
            textView.setText(service.getTitle());
        }
    }

}
