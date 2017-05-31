package com.malalaoshi.android.ui.widgets;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by donald on 2017/5/31.
 */

public class GuidePageManager {
    public static void setHasShowGuidePage(Activity activity, String tag, boolean hasShowed) {
        SharedPreferences sp = activity.getSharedPreferences(activity.getPackageName(), 0);
        sp.edit().putBoolean(tag,hasShowed).commit();
    }

    public static boolean hasNotShowed(Activity activity, String tag) {
        return !hasShowedGuidePage(activity, tag);
    }

    private static boolean hasShowedGuidePage(Activity activity, String tag) {
        SharedPreferences sp = activity.getSharedPreferences(activity.getPackageName(), 0);
        boolean hasShowed = sp.getBoolean(tag, false);
        return hasShowed;
    }
}
