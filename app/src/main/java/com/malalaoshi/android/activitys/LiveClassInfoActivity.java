package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;

import com.malalaoshi.android.core.base.BaseTitleActivity;
import com.malalaoshi.android.core.utils.EmptyUtils;

/**
 * Created by kang on 16/10/14.
 */

public class LiveClassInfoActivity extends BaseTitleActivity {
    private static String EXTRA_CLASS_ID = "order_id";

    public static void open(Context context, String classId) {
        if (!EmptyUtils.isEmpty(classId)) {
            Intent intent = new Intent(context, LiveClassInfoActivity.class);
            intent.putExtra(EXTRA_CLASS_ID, classId);
            context.startActivity(intent);
        }
    }



    @Override
    protected String getStatName() {
        return "课程页";
    }
}
