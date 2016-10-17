package com.malalaoshi.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.LoginActivity;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.entity.LiveCourse;
import com.malalaoshi.android.network.api.LiveCourseInfoApi;
import com.malalaoshi.android.utils.MiscUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/10/14.
 */

public class LiveCourseInfoFragment extends BaseFragment implements View.OnClickListener {
    private static String ARGS_FRAGEMENT_COURSE_ID = "order_id";
    private static int REQUEST_CODE_LOGIN = 1000;

    @Bind(R.id.tv_live_course)
    protected TextView tvLiveCourseName;

    @Bind(R.id.tv_course_type)
    protected TextView tvCourseType;

    @Bind(R.id.tv_grade_course)
    protected TextView tvGradeCourse;

    @Bind(R.id.tv_course_date)
    protected TextView tvCourseDate;

    @Bind(R.id.tv_course_time)
    protected TextView tvCourseTime;

    @Bind(R.id.tv_stu_count)
    protected TextView tvStuCount;

    @Bind(R.id.tv_course_disc)
    protected TextView tvCourseDisc;

    @Bind(R.id.tv_lecturer_name)
    protected TextView tvLecturer;

    @Bind(R.id.tv_lecture_honorary)
    protected TextView tvLectureHonorary;

    @Bind(R.id.iv_leature_avatar)
    protected MalaImageView icLeatureAvatar;

    @Bind(R.id.tv_course_price)
    protected TextView tvCoursePrice;

    @Bind(R.id.tv_buy_course)
    protected TextView tvBuyCourse;

    private LiveCourse liveCourse;

    private String courseId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getString(ARGS_FRAGEMENT_COURSE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_course_info, container, false);
        ButterKnife.bind(this, view);
        initData();
        initViews();
        setEvent();
        return view;
    }

    private void initData() {
        loadData();
    }

    private void initViews() {
        /*tvLiveCourseName;
        tvCourseType;
        tvGradeCourse;
        tvCourseDate;
        tvCourseTime;
        tvStuCount;
        tvCourseDisc;
        tvLecturer;
        tvLectureHonorary;
        icLeatureAvatar;
        tvCoursePrice;*/
    }

    private void setEvent() {
        tvBuyCourse.setOnClickListener(this);
    }

    private void loadData() {
        if (courseId == null) {
            return;
        }
        startProcessDialog("正在加载...");
        ApiExecutor.exec(new LiveCourseInfoFragment.LoadLiveCourseInfoRequest(this, courseId));
    }

    @Override
    public void onClick(View v) {
        if (liveCourse!=null){
            buyCourse();
        }

    }

    private void buyCourse() {
        StatReporter.buyCourse();
        //判断是否登录
        if (UserManager.getInstance().isLogin()) {
            //跳转至支付页
            startBuyLiveCourseActivity();
        } else {
            //跳转登录页
            startSmsActivityRes();
        }
    }

    private void startBuyLiveCourseActivity() {
        MiscUtil.toast("敬请期待!");
    }

    //启动登录页
    private void startSmsActivityRes() {
        Intent intent = new Intent();
        intent.setClass(getContext(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN) {
            if (resultCode == LoginActivity.RESULT_CODE_LOGIN_SUCCESS) {
                //跳转到课程购买页
                startBuyLiveCourseActivity();
            }
        }
    }

    private void onLoadSuccess(LiveCourse response) {
        this.liveCourse = response;
        updateUI();
    }

    private void updateUI() {


    }

    private void onLoadError() {
        MiscUtil.toast("班级信息加载失败!");
    }

    private static final class LoadLiveCourseInfoRequest extends BaseApiContext<LiveCourseInfoFragment, LiveCourse> {

        private String courseId;

        public LoadLiveCourseInfoRequest(LiveCourseInfoFragment fragment, String courseId) {
            super(fragment);
            this.courseId = courseId;
        }

        @Override
        public LiveCourse request() throws Exception {
            return new LiveCourseInfoApi().get(courseId);
        }

        @Override
        public void onApiSuccess(@NonNull LiveCourse response) {
            get().onLoadSuccess(response);
        }

        @Override
        public void onApiFinished() {
            get().stopProcessDialog();
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().onLoadError();
        }
    }

    @Override
    public String getStatName() {
        return "班级页";
    }


}
