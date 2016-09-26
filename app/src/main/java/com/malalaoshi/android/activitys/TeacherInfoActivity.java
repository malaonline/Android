package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.malalaoshi.android.R;
import com.malalaoshi.android.fragments.TeacherInfoFragment;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.utils.FragmentUtil;

/**
 * Created by kang on 16/3/29.
 */
public class TeacherInfoActivity extends BaseActivity {

    private static final String EXTRA_TEACHER_ID = "teacherId";

    public static void open(Context context, Long teacherId) {
        if (teacherId != null) {
            Intent intent = new Intent(context, TeacherInfoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EXTRA_TEACHER_ID, teacherId);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);
        initViews(savedInstanceState);
    }

    private void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        long teacherId = intent.getLongExtra(EXTRA_TEACHER_ID,0);
        if (savedInstanceState==null){
            TeacherInfoFragment teacherInfoFragment = TeacherInfoFragment.newInstance(this,teacherId,false);
            FragmentUtil.openFragment(R.id.container, getSupportFragmentManager(), null, teacherInfoFragment, TeacherInfoFragment.class.getName());
        }
    }

    @Override
    protected String getStatName() {
        return "老师详情页";
    }
}
