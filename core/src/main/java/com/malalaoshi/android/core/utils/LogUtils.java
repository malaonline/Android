package com.malalaoshi.android.core.utils;

import android.util.Log;


/**
 * Created by donald on 2017/5/3.
 */

public class LogUtils {
    private static boolean DEBUG = true;
    public static void e(String TAG, Object method, Object msg){
        if (DEBUG)
            Log.e(TAG, method +""+ msg);
    }
}
