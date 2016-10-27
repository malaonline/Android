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
import com.malalaoshi.android.common.pay.PayActivity;
import com.malalaoshi.android.common.pay.PayManager;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.LoginActivity;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.entity.CreateCourseOrderEntity;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.LiveCourse;
import com.malalaoshi.android.network.api.LiveCourseInfoApi;
import com.malalaoshi.android.ui.dialogs.PromptDialog;
import com.malalaoshi.android.utils.CalendarUtils;
import com.malalaoshi.android.utils.DialogUtil;
import com.malalaoshi.android.utils.MiscUtil;
import com.malalaoshi.android.utils.Number;
import com.malalaoshi.android.utils.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/10/14.
 */

public class LiveCourseInfoFragment extends BaseFragment implements View.OnClickListener {
    public static String ARGS_COURSE_ID = "order_id";
    public static String ARGS_COURSE = "order info";
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
            liveCourse = getArguments().getParcelable(ARGS_COURSE);
            if (liveCourse!=null){
                courseId = String.valueOf(liveCourse.getId());
            }else{
                courseId = getArguments().getString(ARGS_COURSE_ID);
            }
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
        if (liveCourse!=null){
            updateUI();
        }
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
            createOrder();
        } else {
            //跳转登录页
            startSmsActivityRes();
        }
    }



    private void onPayOrder(CreateCourseOrderResultEntity entity) {

        if (!entity.isOk() && entity.getCode() == -1) {
            DialogUtil.showPromptDialog(
                    getFragmentManager(), R.drawable.ic_timeallocate,
                    "该老师部分时段已被占用，请重新选择上课时间!", "知道了", new PromptDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            //更新上课列表
                            EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_FETCHEVALUATED));
                        }
                    }, false, false);
        } else if (!entity.isOk() && entity.getCode() == -2) {
            DialogUtil.showPromptDialog(
                    getFragmentManager(), R.drawable.ic_timeallocate,
                    "奖学金使用失败，请重新选择奖学金!", "知道了", new PromptDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                        }
                    }, false, false);
        } else if (!entity.isOk() && entity.getCode() == -3) {
            DialogUtil.showPromptDialog(
                    getFragmentManager(), R.drawable.ic_timeallocate,
                    "报名人数已满，请重新选择直播班级!", "知道了", new PromptDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                        }
                    }, false, false);
        } else {
            openPayActivity(entity);
        }
    }

    private void openPayActivity(CreateCourseOrderResultEntity entity) {
        if (entity == null) return;
        PayActivity.startPayActivity(entity, getActivity(), false);
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
                createOrder();
            }
        }
    }

    private void createOrder() {
        //跳转至支付页
        ApiExecutor.exec(new CreateOrderRequest(this, new CreateCourseOrderResultEntity()));
    }

    private void onLoadSuccess(LiveCourse response) {
        this.liveCourse = response;
        updateUI();
    }

    private void updateUI() {
        if (liveCourse!=null){
            tvLiveCourseName.setText(liveCourse.getCourse_name());
            tvCourseType.setText(liveCourse.getRoom_capacity()+"人班");
            tvGradeCourse.setText(liveCourse.getCourse_grade());
            tvCourseDate.setText(CalendarUtils.formatDate(liveCourse.getCourse_start())+"—"+CalendarUtils.formatDate(liveCourse.getCourse_end()));
            String oldPeriod = liveCourse.getCourse_period();
            String newPeriod = "";
            if (!EmptyUtils.isEmpty(oldPeriod)){
                newPeriod = oldPeriod.replace(';','\n');
            }
            tvCourseTime.setText(newPeriod);
            tvStuCount.setText(liveCourse.getStudents_count()+"");
            tvCourseDisc.setText(liveCourse.getCourse_description());
            tvLecturer.setText(liveCourse.getLecturer_name());
            String oldBio = liveCourse.getLecturer_bio();
            String newBio = "";
            if (!EmptyUtils.isEmpty(oldBio)){
                newBio = oldBio.replace(';','\n');
            }
            tvLectureHonorary.setText(newBio);
            icLeatureAvatar.loadCircleImage(liveCourse.getLecturer_avatar(),R.drawable.ic_default_teacher_avatar);

            if (liveCourse.getCourse_fee() != null) {
                String str1 = String.format("￥%s",Number.subZeroAndDot(liveCourse.getCourse_fee().doubleValue() * 0.01d));
                String str2 = String.format("%d次",liveCourse.getCourse_lessons());
                StringUtil.setHumpText(tvCoursePrice.getContext(),tvCoursePrice,str1,R.style.LiveCoursePriceStyle,str2,R.style.LiveCourseStuNum);
            }
        }
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

    //创建订单
    private static final class CreateOrderRequest extends
            BaseApiContext<LiveCourseInfoFragment, CreateCourseOrderResultEntity> {

        private CreateCourseOrderEntity entity;

        public CreateOrderRequest(LiveCourseInfoFragment liveCourseInfoFragment,
                                  CreateCourseOrderEntity entity) {
            super(liveCourseInfoFragment);
            this.entity = entity;
        }

        @Override
        public CreateCourseOrderResultEntity request() throws Exception {
            return PayManager.getInstance().createOrder(entity);
        }

        @Override
        public void onApiSuccess(@NonNull CreateCourseOrderResultEntity response) {
            get().onPayOrder(response);
        }

        @Override
        public void onApiStarted() {
            get().tvBuyCourse.setOnClickListener(null);
        }

        @Override
        public void onApiFinished() {
            get().tvBuyCourse.setOnClickListener(get());
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("创建订单失败");
        }
    }


    @Override
    public String getStatName() {
        return "班级页";
    }


}
