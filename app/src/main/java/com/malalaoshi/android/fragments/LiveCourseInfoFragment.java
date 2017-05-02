package com.malalaoshi.android.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.GalleryActivity;
import com.malalaoshi.android.common.pay.PayActivity;
import com.malalaoshi.android.common.pay.PayManager;
import com.malalaoshi.android.common.pay.utils.OrderDef;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.LoginActivity;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.CreateLiveCourseOrderEntity;
import com.malalaoshi.android.entity.LiveCourse;
import com.malalaoshi.android.listener.OnTitleBarClickListener;
import com.malalaoshi.android.network.api.LiveCourseInfoApi;
import com.malalaoshi.android.ui.dialogs.PromptDialog;
import com.malalaoshi.android.utils.CalendarUtils;
import com.malalaoshi.android.utils.DialogUtil;
import com.malalaoshi.android.utils.MiscUtil;
import com.malalaoshi.android.utils.Number;
import com.malalaoshi.android.utils.ShareUtils;
import com.malalaoshi.android.utils.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.framework.ShareSDK;
import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/10/14.
 */

public class LiveCourseInfoFragment extends BaseFragment implements View.OnClickListener, OnTitleBarClickListener {
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

    @Bind(R.id.tv_stu_unregistered_count)
    protected TextView tvStuUnregisteredCount;

    @Bind(R.id.tv_school)
    protected TextView tvSchool;

    @Bind(R.id.tv_address)
    protected TextView tvAddress;

    @Bind(R.id.tv_course_disc)
    protected TextView tvCourseDisc;

    @Bind(R.id.tv_lecturer_name)
    protected TextView tvLecturer;

    @Bind(R.id.tv_lecture_honorary)
    protected TextView tvLectureHonorary;

    @Bind(R.id.iv_leature_avatar)
    protected MalaImageView icLeatureAvatar;

    @Bind(R.id.tv_assist_name)
    protected TextView tvAssistName;

    @Bind(R.id.iv_assist_avatar)
    protected MalaImageView ivAssistAvatar;

    @Bind(R.id.tv_course_price)
    protected TextView tvCoursePrice;

    @Bind(R.id.tv_buy_course)
    protected TextView tvBuyCourse;

    private LiveCourse liveCourse;

    private String courseId;

    //页面数据获取方式
    private boolean dataWay;

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
        ShareSDK.initSDK(getContext());
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
            setLiveCourse();
        }
    }

    private void setEvent() {
        //tvBuyCourse.setOnClickListener(this);
        ivAssistAvatar.setOnClickListener(this);
        icLeatureAvatar.setOnClickListener(this);
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
        switch (v.getId()){
            case R.id.tv_buy_course:
                if (liveCourse!=null){
                    buyCourse();
                }
                break;
            case R.id.iv_assist_avatar:
                if (liveCourse!=null){
                    callAssistPhone();
                }
                break;
            case R.id.iv_leature_avatar:
                if (liveCourse!=null) {
                    lunchGallery(liveCourse.getLecturer_avatar());
                }
                break;
        }

    }

    private void lunchGallery(String url) {
        Intent intent = new Intent(getContext(), GalleryActivity.class);
        intent.putExtra(GalleryActivity.GALLERY_URLS, new String[]{url});
        startActivity(intent);
    }

    private void showWxShare() {
        if (liveCourse==null) return;
        String title = String.format("%s在麻辣老师为您讲授%s",liveCourse.getLecturer_name(),liveCourse.getCourse_name());
        String lectureImg;
        String api_host = getString(R.string.api_host);
        if (api_host.startsWith("http://dev")){
            lectureImg = "https://s3.cn-north-1.amazonaws.com.cn/dev-static/web/images/ad/ic_launcher.png";
        }else {
            lectureImg = "https://s3.cn-north-1.amazonaws.com.cn/mala-static/web/images/ad/ic_launcher.png";
        }
        String host = String.format(api_host+"/wechat/order/course_choosing/?step=live_class_page&liveclassid=%d",liveCourse.getId());
        ShareUtils.showWxShare(getContext(),title,"顶级名师直播授课，当地老师全程辅导，赶快加入我们吧",lectureImg,host);
    }

    private void callAssistPhone() {
        //liveCourse
        String number = liveCourse.getAssistant_phone();
        if (EmptyUtils.isEmpty(number)){
            MiscUtil.toast("该老师暂时未提供手机号！");
            return;
        }
        //用intent启动拨打电话
        //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+number));
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + number);
        intent.setData(data);
        startActivity(intent);

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
            //已满
            tvBuyCourse.setOnClickListener(null);
            tvBuyCourse.setText(getResources().getString(R.string.live_course_full));
            tvBuyCourse.setTextColor(getResources().getColor(R.color.white_alpha60));
            tvBuyCourse.setBackground(getResources().getDrawable(R.drawable.bg_red_rectangle_btn_normal));
            DialogUtil.showPromptDialog(
                    getFragmentManager(), R.drawable.ic_timeallocate,
                    "报名人数已满，请重新选择直播班级!", "知道了", new PromptDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                        }
                    }, false, false);
        } else if (!entity.isOk() && entity.getCode() == -4) {
            //已购买
            tvBuyCourse.setOnClickListener(null);
            tvBuyCourse.setText(getResources().getString(R.string.live_course_paid));
            tvBuyCourse.setBackground(getResources().getDrawable(R.drawable.bg_grey_rectangle_btn_disable));
            DialogUtil.showPromptDialog(
                    getFragmentManager(), R.drawable.ic_timeallocate,
                    "您已购买过该课程，同一账户只能购买一次!", "知道了", new PromptDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                        }
                    }, false, false);
        }
        else {
            tvBuyCourse.setOnClickListener(this);
            entity.setOrderType(OrderDef.ORDER_TYPE_LIVE_COURSE);
            launchPayActivity(entity);
        }
    }

    private void launchPayActivity(CreateCourseOrderResultEntity entity) {
        if (entity == null) return;
        PayActivity.launch(entity, getActivity(), true);
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
        if (liveCourse==null) return;

        CreateLiveCourseOrderEntity entity = new CreateLiveCourseOrderEntity();
        entity.setLive_class(liveCourse.getId());
        ApiExecutor.exec(new CreateOrderRequest(this, entity));
    }

    private void onLoadSuccess(LiveCourse response) {
        this.liveCourse = response;
        setLiveCourse();
    }

    private void setLiveCourse() {
        if (liveCourse!=null){
            tvBuyCourse.setOnClickListener(null);
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
            tvStuUnregisteredCount.setText((20-liveCourse.getStudents_count())+"人");
            tvSchool.setText(liveCourse.getSchool_name());
            tvAddress.setText(liveCourse.getSchool_address());
            tvCourseDisc.setText(liveCourse.getCourse_description());
            tvLecturer.setText(liveCourse.getLecturer_name());
            tvAssistName.setText("助教："+liveCourse.getAssistant_name());
            ivAssistAvatar.loadCircleImage(liveCourse.getAssistant_avatar(),R.drawable.ic_default_avatar);
            String oldBio = liveCourse.getLecturer_bio();
            String newBio = "";
            if (!EmptyUtils.isEmpty(oldBio)){
                newBio = oldBio.replace(';','\n');
            }
            tvLectureHonorary.setText(newBio);
            icLeatureAvatar.loadCircleImage(liveCourse.getLecturer_avatar(),R.drawable.ic_default_avatar);

            if (liveCourse.getCourse_fee() != null) {
                String str1 = String.format("￥%s",Number.subZeroAndDot(liveCourse.getCourse_fee().doubleValue() * 0.01d));
                String str2 = String.format("%d次",liveCourse.getCourse_lessons());
                StringUtil.setHumpText(tvCoursePrice.getContext(),tvCoursePrice,str1,R.style.LiveCoursePriceStyle,str2,R.style.LiveCourseStuNum);
            }

            //已经购买
            if (liveCourse.is_paid()){
                //已经购买
                tvBuyCourse.setText(getResources().getString(R.string.live_course_paid));
                tvBuyCourse.setBackground(getResources().getDrawable(R.drawable.bg_grey_rectangle_btn_disable));
                return;
            }

            //判断时间
            Long endTime = liveCourse.getCourse_end();

            Long currentTime = CalendarUtils.getCurrentTimestamp();

            if (currentTime-endTime>=0){
                //课程已结束
                tvBuyCourse.setText(getResources().getString(R.string.live_course_over));
                tvBuyCourse.setBackground(getResources().getDrawable(R.drawable.bg_grey_rectangle_btn_disable));
                return;
            }

            //判断人数是否已满
            if (liveCourse.getStudents_count()>=liveCourse.getRoom_capacity()){
                //已满
                tvBuyCourse.setText(getResources().getString(R.string.live_course_full));
                tvBuyCourse.setTextColor(getResources().getColor(R.color.white_alpha60));
                tvBuyCourse.setBackground(getResources().getDrawable(R.drawable.bg_red_rectangle_btn_normal));
            }else{
                tvBuyCourse.setText(getResources().getString(R.string.live_course_buy));
                tvBuyCourse.setBackground(getResources().getDrawable(R.drawable.bg_blue_rectangle_btn));
                tvBuyCourse.setOnClickListener(this);
            }
        }
    }

    private void onLoadError() {
        MiscUtil.toast("班级信息加载失败!");
    }

    @Override
    public void onTitleLeftClick() {

    }

    @Override
    public void onTitleRightClick() {
        showWxShare();
    }

    private static final class LoadLiveCourseInfoRequest extends BaseApiContext<LiveCourseInfoFragment, LiveCourse> {

        private String courseId;

        LoadLiveCourseInfoRequest(LiveCourseInfoFragment fragment, String courseId) {
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

        private CreateLiveCourseOrderEntity entity;

        CreateOrderRequest(LiveCourseInfoFragment liveCourseInfoFragment,
                                  CreateLiveCourseOrderEntity entity) {
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
