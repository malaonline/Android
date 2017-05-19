package com.malalaoshi.android.ui.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kang on 15/12/31.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Drawable mDivider;

    private int mOrientation;

    //阴影宽度
    int cardElevation = -1;
    private Paint mPaint;
    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;

    public DividerItemDecoration(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }

    public DividerItemDecoration(Context context, int orientation, int drawableId) {
        this(context, orientation);
        mDivider = ContextCompat.getDrawable(context, drawableId);
        cardElevation = mDivider.getIntrinsicHeight();
    }
    public DividerItemDecoration(Context context, int orientation, int dividerHeight, int dividerColor){
        this(context, orientation);
        cardElevation = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }
    public void setMargin(int left, int top, int right, int bottom){
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop() + mTop;
        final int bottom = parent.getHeight() - parent.getPaddingBottom() - mBottom;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
            if (mPaint != null){
                c.drawRect(left, top, left+cardElevation, bottom, mPaint);
            }
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {

        final int left = parent.getPaddingLeft() + mLeft;
        final int right = parent.getWidth() - parent.getPaddingRight() -  mRight;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
            if (mPaint != null)
                c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {

        /*if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            //获取阴影的宽度
            if (cardElevation==-1){
                cardElevation = parent.getContext().getResources().getDimensionPixelSize(R.dimen.schedule_list_card_elevation);
            }
            if (mOrientation == VERTICAL_LIST) {
                if (itemPosition==0){
                    outRect.set(0 , mDivider.getIntrinsicHeight() - cardElevation, 0, 0);
                }else{
                    outRect.set(0 , mDivider.getIntrinsicHeight() - 2*cardElevation, 0, 0);
                }

            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }

        }else{
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, mDivider.getIntrinsicHeight() , 0, 0);
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }*/
        outRect.set(0, 0, 0, cardElevation);
    }
}