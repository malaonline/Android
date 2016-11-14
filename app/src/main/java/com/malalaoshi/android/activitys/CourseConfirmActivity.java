package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.fragments.CourseConfirmFragment;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.utils.FragmentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Course confirm activity
 * Created by tianwei on 3/5/16.
 */
public class CourseConfirmActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {
    public static final String EXTRA_SCHOOLS = "extra_schools";
    public static final String EXTRA_PRICES = "extra_prices";

    public static final String EXTRA_SUBJECT = "extra_subject";
    public static final String EXTRA_TEACHER_ID = "extra_teacher_id";
    public static final String EXTRA_TEACHER_AVATOR = "extra_teacher_avator";
    public static final String EXTRA_TEACHER_NAME = "extra_teacher_name";
    public static final String EXTRA_SCHOOL_ID = "extra_school_id";
    private static final String TAG = "CourseConfirmActivity";
    @Bind(R.id.title_view)
    protected TitleBarView titleBarView;

    public static void launch(Context context, Long teacherId, String teacherName, String teacherAvator, Subject subject, Long schoolId){
        if (teacherId != null&&subject != null&&schoolId!=null) {
            Intent intent = new Intent(context, CourseConfirmActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EXTRA_TEACHER_ID, teacherId);
            intent.putExtra(EXTRA_SUBJECT, subject);
            intent.putExtra(EXTRA_TEACHER_NAME, teacherName);
            intent.putExtra(EXTRA_TEACHER_AVATOR, teacherAvator);
            intent.putExtra(EXTRA_SCHOOL_ID, schoolId);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_confirm);
        ButterKnife.bind(this);
        Log.i(TAG, "");
        Intent intent = getIntent();
        Long teacherId = intent.getLongExtra(EXTRA_TEACHER_ID, 0);
        Subject subject = intent.getParcelableExtra(EXTRA_SUBJECT);
        String teacherName = intent.getStringExtra(EXTRA_TEACHER_NAME);
        String teacherAvator = intent.getStringExtra(EXTRA_TEACHER_AVATOR);
        Long schoolId = intent.getLongExtra(EXTRA_SCHOOL_ID, 0);
        if (savedInstanceState==null){
            CourseConfirmFragment fragment = CourseConfirmFragment.newInstance(teacherId, teacherName,teacherAvator, subject, schoolId, false);
            FragmentUtil.openFragment(R.id.container, getSupportFragmentManager(), null
                    , fragment, CourseConfirmFragment.class.getName());
        }

        titleBarView.setOnTitleBarClickListener(this);
    }

    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CourseConfirmFragment fragment = (CourseConfirmFragment) getSupportFragmentManager().findFragmentByTag(CourseConfirmFragment.class.getName());
        if (fragment!=null){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected String getStatName() {
        return "课程确认";
    }
}
