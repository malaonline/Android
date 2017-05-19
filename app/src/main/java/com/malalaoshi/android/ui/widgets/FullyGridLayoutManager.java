package com.malalaoshi.android.ui.widgets;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 嵌套在scrollview中的九宫格
 * Created by donald on 2017/5/9.
 */

public class FullyGridLayoutManager extends GridLayoutManager {
    private int[] mMeasuredDimension = new int[2];

    public FullyGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FullyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public FullyGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
//        super.onMeasure(recycler, state, widthSpec, heightSpec);
        int widthMode = View.MeasureSpec.getMode(widthSpec);
        int heightMode = View.MeasureSpec.getMode(heightSpec);
        int widthSize = View.MeasureSpec.getSize(widthSpec);
        int heightSize = View.MeasureSpec.getSize(heightSpec);

        int width = 0;
        int height = 0;

        int itemCount = getItemCount();
        int spanCount = getSpanCount();
        for (int i = 0; i < itemCount; i++) {
            measureScrapChild(recycler, i , View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    mMeasuredDimension);
            if (getOrientation() == HORIZONTAL){
                if (i % spanCount == 0){
                    width = width + mMeasuredDimension[0];
                }
                if (i == 0){
                    height = mMeasuredDimension[1];
                }
            }else {
                if (i % spanCount == 0){
                    height = height+ mMeasuredDimension[0];
                }
            }
        }
        switch (widthMode){
            case View.MeasureSpec.EXACTLY:
                width = widthSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:

        }
        switch (heightMode){
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
            case View.MeasureSpec.UNSPECIFIED:
            case View.MeasureSpec.AT_MOST:
        }
        setMeasuredDimension(width, height);
    }

    /**
     * 测量子view
     * @param recycler
     * @param position
     * @param widthSpec
     * @param heightSpec
     * @param measuredDimension
     */
    private void measureScrapChild(RecyclerView.Recycler recycler, int position,
                                   int widthSpec, int heightSpec, int[] measuredDimension) {
        if (position < getItemCount()){
            try {
                View view = recycler.getViewForPosition(0);
                if (view != null){
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
                    int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), layoutParams.width);
                    int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), layoutParams.height);
                    view.measure(childWidthSpec, childHeightSpec);
                    measuredDimension[0] = view.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                    measuredDimension[1] = view.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
                    recycler.recycleView(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
