package com.malalaoshi.android.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kang on 16/9/6.
 */
public class SideBar extends View {
    public static String[] indexs = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };

    private OnTouchIndexChangedListener onTouchIndexChangedListener;

    public SideBar(Context context) {
        this(context,null);
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取高和宽
        int height = getHeight();
        int width = getWidth();

    }

    public void setOnTouchIndexChangedListener(OnTouchIndexChangedListener onTouchIndexChangedListener) {
        this.onTouchIndexChangedListener = onTouchIndexChangedListener;
    }

    public interface OnTouchIndexChangedListener{
        void onTouchIndexChanged(String  index);
    }
}
