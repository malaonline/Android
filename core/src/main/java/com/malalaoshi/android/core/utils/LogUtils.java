package com.malalaoshi.android.core.utils;

import android.util.Log;

import com.malalaoshi.android.core.BuildConfig;


/**
 * Created by donald on 2017/5/3.
 */

public class LogUtils {
    private static String DEBUG = BuildConfig.BUILD_TYPE;
    public static void e(String TAG, Object method, Object msg){
        if ("debug".equals(DEBUG))
            Log.e(TAG, method + ": " + msg);
    }
}
