package com.malalaoshi.android.core.utils;

import android.graphics.Rect;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.malalaoshi.android.core.MalaContext;

/**
 * Created by donald on 2017/4/13.
 */

public class SoftKeyboardUtils {

    private static ViewTreeObserver.OnGlobalLayoutListener sGlobalLayoutListener;

    public static void setOnHideShowListener(final ViewGroup root, final OnHideShowListener listener){
        if (root == null) return;
        sGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            int statusBarHeight = StatusBarCompat.getStatusBarHeight(MalaContext.getContext());
            int keyboardHeight;
            boolean isShowKeyboard;

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                root.getWindowVisibleDisplayFrame(rect);
                int screenHeight = root.getRootView().getHeight();
                int heightDiff = screenHeight - (rect.bottom - rect.top);
                if (keyboardHeight == 0 && heightDiff > statusBarHeight) {
                    keyboardHeight = heightDiff - statusBarHeight;
                }
                if (isShowKeyboard) {
                    if (heightDiff <= statusBarHeight) {
                        isShowKeyboard = false;
                        listener.onHideKeyboard();
                    }
                } else {
                    if (heightDiff > statusBarHeight) {
                        isShowKeyboard = true;
                        listener.onShowKeyboard();
                    }
                }
            }
        };
        root.getViewTreeObserver().addOnGlobalLayoutListener(sGlobalLayoutListener);
    }
    public interface OnHideShowListener{
        void onShowKeyboard();
        void onHideKeyboard();
    }
    public static void removeListener(ViewGroup root){
        if (root == null) return;
        root.getViewTreeObserver().removeOnGlobalLayoutListener(sGlobalLayoutListener);
    }
}
