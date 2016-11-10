package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.malalaoshi.android.core.base.BaseTitleActivity;
import com.malalaoshi.android.fragments.LiveCourseIntroduceFragment;

/**
 * Created by kang on 16/11/10.
 */

public class LiveCourseIntroduceActivity extends BaseTitleActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTitleBar().setRightVisibility(View.GONE);
        if(savedInstanceState==null){
            replaceFragment(Fragment.instantiate(this, LiveCourseIntroduceFragment.class.getName(), getIntent().getExtras()));
        }
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, LiveCourseIntroduceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected String getStatName() {
        return "双师介绍";
    }
}
