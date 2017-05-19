package com.malalaoshi.android.ui.widgets;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 嵌套scrollview
 * Created by donald on 2017/5/11.
 */

public class FullyLinearLayoutManager extends LinearLayoutManager {
    private int[] mMeasuredDimension = new int[2];

    public FullyLinearLayoutManager(Context context) {
        super(context);
    }

    public FullyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public FullyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int widthMode = View.MeasureSpec.getMode(widthSpec);
        int heightMode = View.MeasureSpec.getMode(heightSpec);
        int widthSize = View.MeasureSpec.getSize(widthSpec);
        int heightSize = View.MeasureSpec.getSize(heightSpec);

        int width = 0;
        int height = 0;

        for (int i = 0; i < getItemCount(); i++) {
            measureScrapChild(recycler, i, View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED)
            , View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), mMeasuredDimension);
            if (getOrientation() == HORIZONTAL){
                width = width + mMeasuredDimension[0];
                if (i == 0){
                    height = mMeasuredDimension[1];
                }
            }else {
                height = height + mMeasuredDimension[1];
                if (i == 0){
                    width = mMeasuredDimension[0];
                }
            }
        }
        switch (widthMode){
            case View.MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }
        switch (heightMode){
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }
        setMeasuredDimension(width, height);

    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
        try {
            View view = recycler.getViewForPosition(0);
            if (view != null){
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
                int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), layoutParams.width);
                int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), layoutParams.height);
                view.measure(childWidthSpec, childHeightSpec);
                mMeasuredDimension[0] = view.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                mMeasuredDimension[1] = view.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
                recycler.recycleView(view);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
