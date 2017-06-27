package com.malalaoshi.android.ui.widgets;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * Created by donald on 2017/6/27.
 */

public class SurroundLeadingMarginSpan implements LeadingMarginSpan.LeadingMarginSpan2 {

    private final int mLines;
    private final int mMargin;

    public SurroundLeadingMarginSpan(int lines, int margin) {
        mLines = lines;
        mMargin = margin;
    }

    @Override
    public int getLeadingMarginLineCount() {
        return mLines;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        if (first)
            return mMargin;
        else
            return 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline,
                                  int bottom, CharSequence text, int start, int end,
                                  boolean first, Layout layout) {

    }
}
