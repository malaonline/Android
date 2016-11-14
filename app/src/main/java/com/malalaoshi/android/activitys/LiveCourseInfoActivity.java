package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.malalaoshi.android.core.base.BaseTitleActivity;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.entity.LiveCourse;
import com.malalaoshi.android.fragments.LiveCourseInfoFragment;

/**
 * Created by kang on 16/10/14.
 */

public class LiveCourseInfoActivity extends BaseTitleActivity {

    public static void launchClearTop(Context context){
        Intent intent = new Intent(context, LiveCourseInfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void open(Context context, String courseId) {
        if (!EmptyUtils.isEmpty(courseId)) {
            Intent intent = new Intent(context, LiveCourseInfoActivity.class);
            intent.putExtra(LiveCourseInfoFragment.ARGS_COURSE_ID, courseId);
            context.startActivity(intent);
        }
    }

    public static void open(Context context, LiveCourse liveCourse) {
        if (liveCourse!=null) {
            Intent intent = new Intent(context, LiveCourseInfoActivity.class);
            intent.putExtra(LiveCourseInfoFragment.ARGS_COURSE, liveCourse);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
            replaceFragment(Fragment.instantiate(this, LiveCourseInfoFragment.class.getName(), getIntent().getExtras()));
        }
    }

    @Override
    protected String getStatName() {
        return "课程页";
    }
}
