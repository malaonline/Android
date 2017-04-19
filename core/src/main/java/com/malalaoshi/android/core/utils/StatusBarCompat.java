package com.malalaoshi.android.core.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by donald on 2017/4/13.
 */

public class StatusBarCompat {
    private static final int INVALID_VAL = -1;
    private static final int COLOR_DEFAULT = Color.parseColor("#73A4FC");

    public static void compat(Activity activity){
        compat(activity, INVALID_VAL);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void compat(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (statusColor != INVALID_VAL){
                window.setStatusBarColor(statusColor);
            }else {
                window.setStatusBarColor(COLOR_DEFAULT);
            }
            ViewGroup contentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View childView = contentView.getChildAt(0);
            if (childView != null){
                ViewCompat.setFitsSystemWindows(childView, true);
            }
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int color = COLOR_DEFAULT;
            if (statusColor != INVALID_VAL){
                color = statusColor;
            }
            ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            int statusBarHeight = getStatusBarHeight(activity);
            View childView = contentView.getChildAt(0);
            if (childView != null){
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) childView.getLayoutParams();
                if (lp != null && lp.topMargin < statusBarHeight && lp.height != statusBarHeight){
                    ViewCompat.setFitsSystemWindows(childView, false);
                    lp.topMargin += statusBarHeight;
                    childView.setLayoutParams(lp);
                }
            }

            View statusBarView = contentView.getChildAt(0);
            if (statusBarView != null && statusBarView.getLayoutParams() != null && statusBarView.getLayoutParams().height == statusBarHeight){
                statusBarView.setBackgroundColor(color);
                return;
            }
            statusBarView = new View(activity);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            statusBarView.setBackgroundColor(color);
            contentView.addView(statusBarView, 0, layoutParams);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


}
