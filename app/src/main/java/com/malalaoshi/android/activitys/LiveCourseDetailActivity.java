package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapters.CourseIntroductionAdapter;
import com.malalaoshi.android.adapters.CourseServiceAdapter;
import com.malalaoshi.android.adapters.HonoraryAdapter;
import com.malalaoshi.android.common.pay.PayActivity;
import com.malalaoshi.android.common.pay.PayManager;
import com.malalaoshi.android.common.pay.utils.OrderDef;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.LoginActivity;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.core.utils.StatusBarCompat;
import com.malalaoshi.android.core.view.ShadowHelper;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.CreateLiveCourseOrderEntity;
import com.malalaoshi.android.entity.LiveCourse;
import com.malalaoshi.android.network.api.LiveCourseInfoApi;
import com.malalaoshi.android.ui.dialogs.PromptDialog;
import com.malalaoshi.android.ui.widgets.FullyGridLayoutManager;
import com.malalaoshi.android.ui.widgets.FullyLinearLayoutManager;
import com.malalaoshi.android.ui.widgets.MixtureTextView;
import com.malalaoshi.android.utils.CalendarUtils;
import com.malalaoshi.android.utils.DialogUtil;
import com.malalaoshi.android.utils.Number;
import com.malalaoshi.android.utils.ShareUtils;
import com.malalaoshi.android.utils.StringUtil;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 课程详情页
 * Created by donald on 2017/6/22.
 */

public class LiveCourseDetailActivity extends BaseActivity {
    public static String ARGS_COURSE_ID = "order_id";
    public static String ARGS_COURSE = "order_info";
    private static int REQUEST_CODE_LOGIN = 1000;


    @Bind(R.id.tv_course_detail_title)
    TextView mTvCourseDetailTitle;
    @Bind(R.id.tv_course_detail_num)
    TextView mTvCourseDetailNum;
    @Bind(R.id.tv_course_detail_grade)
    TextView mTvCourseDetailGrade;
    @Bind(R.id.tv_course_detail_date)
    TextView mTvCourseDetailDate;
    @Bind(R.id.pb_course_detail_plane)
    ProgressBar mPbCourseDetailPlane;
    @Bind(R.id.tv_course_detail_remaining)
    TextView mTvCourseDetailRemaining;
    @Bind(R.id.tv_course_detail_school)
    TextView mTvCourseDetailSchool;
    @Bind(R.id.tv_course_detail_address)
    TextView mTvCourseDetailAddress;
    @Bind(R.id.ll_course_detail_info)
    LinearLayout mLlCourseDetailInfo;
    @Bind(R.id.rv_course_detail_service)
    RecyclerView mRvCourseDetailService;
    @Bind(R.id.ll_course_detail_service_title)
    LinearLayout mLlCourseDetailServiceTitle;
    @Bind(R.id.rv_course_detail_introduction)
    RecyclerView mRvCourseDetailIntroduction;
    @Bind(R.id.ll_course_detail_introduction_title)
    LinearLayout mLlCourseDetailIntroductionTitle;
    @Bind(R.id.tv_course_detail_teacher_title)
    TextView mTvCourseDetailTeacherTitle;
    @Bind(R.id.view_course_detail)
    View mViewCourseDetail;
    @Bind(R.id.iv_course_detail_teacher_avatar)
    MalaImageView mIvCourseDetailTeacherAvatar;
    @Bind(R.id.tv_course_detail_teacher_name)
    TextView mTvCourseDetailTeacherName;
    //    @Bind(R.id.rv_course_detail_teacher_honorary)
    //    RecyclerView mRvCourseDetailTeacherHonorary;
    @Bind(R.id.rl_course_detail_teacher)
    RelativeLayout mRlCourseDetailTeacher;
    @Bind(R.id.tv_course_detail_time)
    TextView mTvCourseDetailTime;
    @Bind(R.id.tv_course_detail_assistant_name)
    TextView mTvCourseDetailAssistantName;
    @Bind(R.id.iv_course_detail_call)
    ImageView mIvCourseDetailCall;
    @Bind(R.id.rl_course_detail_assistant)
    RelativeLayout mRlCourseDetailAssistant;
    @Bind(R.id.tv_course_detail_price)
    TextView mTvCourseDetailPrice;
    @Bind(R.id.tv_course_detail_buy)
    TextView mTvCourseDetailBuy;
    @Bind(R.id.ll_course_detail_contact)
    LinearLayout mLlCourseDetailContact;
    @Bind(R.id.tbv_course_detail_title)
    TitleBarView mTbvCourseDetailTitle;
    @Bind(R.id.mtv_course_detail_honorary)
    MixtureTextView mMtvCourseDetailHonorary;
    //    @Bind(R.id.tv_course_detail_teacher_honorary)
    //    TextView mTvCourseDetailTeacherHonorary;

    private LiveCourse mLiveCourse;
    private Long mLiveCourseId;
    private CourseIntroductionAdapter mIntroductionAdapter;
    private HonoraryAdapter mHonoraryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        StatusBarCompat.compat(this);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            mLiveCourse = intent.getParcelableExtra(ARGS_COURSE);
            if (mLiveCourse != null) {
                mLiveCourseId = mLiveCourse.getId();
            } else {
                mLiveCourseId = intent.getLongExtra(ARGS_COURSE_ID, -1);
            }
        }
        iniView();
        loadData();
    }

    private void iniView() {
        initShadow();
        //课程服务
        FullyGridLayoutManager gridLayoutManager = new FullyGridLayoutManager(this, 3);
        mRvCourseDetailService.setLayoutManager(gridLayoutManager);
        CourseServiceAdapter courseServiceAdapter = new CourseServiceAdapter(this);
        courseServiceAdapter.addData(Arrays.asList(MalaApplication.getInstance().getResources().getStringArray(R.array.course_service_item)));
        mRvCourseDetailService.setAdapter(courseServiceAdapter);

        //课程介绍
        FullyLinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(this);
        mRvCourseDetailIntroduction.setLayoutManager(linearLayoutManager);
        mIntroductionAdapter = new CourseIntroductionAdapter(this);
        mRvCourseDetailIntroduction.setAdapter(mIntroductionAdapter);

        //教师荣誉
        //        mRvCourseDetailTeacherHonorary.setLayoutManager(new FullyLinearLayoutManager(this));
        //        mHonoraryAdapter = new HonoraryAdapter(this);
        //        mRvCourseDetailTeacherHonorary.setAdapter(mHonoraryAdapter);


        mTbvCourseDetailTitle.setOnTitleBarClickListener(new TitleBarView.OnTitleBarClickListener() {
            @Override
            public void onTitleLeftClick() {
                finish();
            }

            @Override
            public void onTitleRightClick() {
                showWxShare();
            }
        });
        mTbvCourseDetailTitle.setTitle(getStatName(), Color.WHITE);
        mTbvCourseDetailTitle.setRightBackgroundResource(R.drawable.bitmap_share_white);
    }

    private void initShadow() {
        ShadowHelper.setDrawShadow(this, mLlCourseDetailInfo, 8);
        ShadowHelper.setDrawShadow(this, mLlCourseDetailServiceTitle, 8);
        ShadowHelper.setDrawShadow(this, mLlCourseDetailIntroductionTitle, 8);
        ShadowHelper.setDrawShadow(this, mRlCourseDetailTeacher, 8);
        ShadowHelper.setDrawShadow(this, mRlCourseDetailAssistant, 8);
    }

    private void loadData() {
        if (mLiveCourseId == -1) {
            return;
        }
        startProcessDialog("正在加载...");
        ApiExecutor.exec(new LoadLiveCourseInfoRequest(this, mLiveCourseId));
    }


    private void onLoadError() {
        MiscUtil.toast("班级信息加载失败!");
    }

    private void onLoadSuccess(LiveCourse response) {
        mLiveCourse = response;
        setupData();
    }

    private void setupData() {
        if (mLiveCourse != null) {
            mIntroductionAdapter.addData(Arrays.asList(mLiveCourse.getCourse_description().split("\n")));

            //直播名师
            mIvCourseDetailTeacherAvatar.loadRoundedImage(mLiveCourse.getLecturer_avatar());
            mTvCourseDetailTeacherName.setText(mLiveCourse.getLecturer_name());
            //            mHonoraryAdapter.addData(Arrays.asList(mLiveCourse.getLecturer_bio().split(";")));
            //            makeSpan(mLiveCourse.getLecturer_bio());
            String honorary = mLiveCourse.getLecturer_bio().replace(";", "\n\n");
            mMtvCourseDetailHonorary.setText(honorary);

            //课程信息
            mTvCourseDetailTitle.setText(mLiveCourse.getCourse_name());
            int totalStudents = mLiveCourse.getRoom_capacity();
            mTvCourseDetailNum.setText(totalStudents + "人班");
            mTvCourseDetailGrade.setText(mLiveCourse.getCourse_grade());
            mTvCourseDetailDate.setText(CalendarUtils.formatDate(mLiveCourse.getCourse_start()) + "—" +
                    CalendarUtils.formatDate(mLiveCourse.getCourse_end()));
            String coursePeriod = mLiveCourse.getCourse_period();
            if (!TextUtils.isEmpty(coursePeriod)) {
                coursePeriod = coursePeriod.replace(';', '\n');
            }
            mTvCourseDetailTime.setText(coursePeriod);
            int registeredStus = mLiveCourse.getStudents_count();
            mPbCourseDetailPlane.setMax(totalStudents);
            mPbCourseDetailPlane.setProgress(registeredStus);
            String unregistered = "剩余名额：" + (totalStudents - registeredStus) + "人";
            SpannableString spannableString = new SpannableString(unregistered);
            spannableString.setSpan(new ForegroundColorSpan(MiscUtil.getColor(R.color.color_red_f9151b)), 5, unregistered.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvCourseDetailRemaining.setText(spannableString, TextView.BufferType.SPANNABLE);
            mTvCourseDetailSchool.setText(mLiveCourse.getSchool_name());
            mTvCourseDetailAddress.setText(mLiveCourse.getSchool_address());
            mTvCourseDetailAssistantName.setText("助教：" + mLiveCourse.getAssistant_name());
            String str1 = String.format("¥ %s", Number.subZeroAndDot(mLiveCourse.getCourse_fee().doubleValue() * 0.01d));
            String str2 = String.format("%d次", mLiveCourse.getCourse_lessons());
            StringUtil.setHumpText(this, mTvCourseDetailPrice, str1, R.style.LiveCoursePriceStyle, str2, R.style.LiveCourseStuNum);

        }
    }

    private void makeSpan(String honorary) {
        Spanned htmlText = Html.fromHtml(honorary);
        Log.e("LiveCourseDetail", "makeSpan: " + htmlText);
        honorary = honorary.replace(";", "\n");
        SpannableString spannableString = new SpannableString(honorary);
        int allTextStart = 0;
        int allTextEnd = honorary.length() - 1;
        int lines;
        //        Rect bounds = new Rect();
        //        mTvCourseDetailTeacherHonorary.getPaint().getTextBounds(honorary.substring(0,10),0,1,bounds);
        //        float fontSpacing = mTvCourseDetailTeacherHonorary.getPaint().getFontSpacing();
        //        lines = (int) (mIvCourseDetailTeacherAvatar.getHeight()/fontSpacing);
        //        SurroundLeadingMarginSpan marginSpan = new SurroundLeadingMarginSpan(lines, mIvCourseDetailTeacherAvatar.getWidth() + 10);
        //        spannableString.setSpan(marginSpan, allTextStart, allTextEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //        mTvCourseDetailTeacherHonorary.setText(spannableString);
    }

    public static void launch(Context context, LiveCourse liveCourse) {
        if (liveCourse != null) {
            Intent intent = new Intent(context, LiveCourseDetailActivity.class);
            intent.putExtra(LiveCourseDetailActivity.ARGS_COURSE, liveCourse);
            context.startActivity(intent);
        }
    }

    @OnClick({R.id.iv_course_detail_call, R.id.tv_course_detail_buy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_course_detail_call:
                callAssistPhone();
                break;
            case R.id.tv_course_detail_buy:
                buyCourse();
                break;
        }
    }

    private void callAssistPhone() {
        //liveCourse
        String number = mLiveCourse.getAssistant_phone();
        if (EmptyUtils.isEmpty(number)) {
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

    private void createOrder() {
        //跳转至支付页
        if (mLiveCourse == null) return;

        CreateLiveCourseOrderEntity entity = new CreateLiveCourseOrderEntity();
        entity.setLive_class(mLiveCourse.getId());
        ApiExecutor.exec(new CreateOrderRequest(this, entity));
    }

    //启动登录页
    private void startSmsActivityRes() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    private void showWxShare() {
        if (mLiveCourse == null) return;
        String title = String.format("%s在麻辣老师为您讲授%s", mLiveCourse.getLecturer_name(), mLiveCourse.getCourse_name());
        String lectureImg;
        String api_host = getString(R.string.api_host);
        if (api_host.startsWith("http://dev")) {
            lectureImg = "https://s3.cn-north-1.amazonaws.com.cn/dev-static/web/images/ad/ic_launcher.png";
        } else {
            lectureImg = "https://s3.cn-north-1.amazonaws.com.cn/mala-static/web/images/ad/ic_launcher.png";
        }
        String host = String.format(api_host + "/wechat/order/course_choosing/?step=live_class_page&liveclassid=%d", mLiveCourse.getId());
        ShareUtils.showWxShare(this, title, "顶级名师直播授课，当地老师全程辅导，赶快加入我们吧", lectureImg, host);
    }

    private static final class LoadLiveCourseInfoRequest extends BaseApiContext<LiveCourseDetailActivity, LiveCourse> {

        private long courseId;

        LoadLiveCourseInfoRequest(LiveCourseDetailActivity activity, long courseId) {
            super(activity);
            this.courseId = courseId;
        }

        @Override
        public LiveCourse request() throws Exception {
            return new LiveCourseInfoApi().get(courseId + "");
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
            BaseApiContext<LiveCourseDetailActivity, CreateCourseOrderResultEntity> {

        private CreateLiveCourseOrderEntity entity;

        CreateOrderRequest(LiveCourseDetailActivity liveCourseInfoFragment,
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
            get().mTvCourseDetailBuy.setOnClickListener(null);
        }

        @Override
        public void onApiFinished() {
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("创建订单失败");
        }
    }

    private void onPayOrder(CreateCourseOrderResultEntity entity) {
        if (!entity.isOk() && entity.getCode() == -1) {
            DialogUtil.showPromptDialog(
                    getSupportFragmentManager(), R.drawable.ic_timeallocate,
                    "该老师部分时段已被占用，请重新选择上课时间!", "知道了", new PromptDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            //更新上课列表
                            EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_FETCHEVALUATED));
                        }
                    }, false, false);
        } else if (!entity.isOk() && entity.getCode() == -2) {
            DialogUtil.showPromptDialog(
                    getSupportFragmentManager(), R.drawable.ic_timeallocate,
                    "奖学金使用失败，请重新选择奖学金!", "知道了", new PromptDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                        }
                    }, false, false);
        } else if (!entity.isOk() && entity.getCode() == -3) {
            //已满
            mTvCourseDetailBuy.setClickable(false);
            mTvCourseDetailBuy.setText(getResources().getString(R.string.live_course_full));
            mTvCourseDetailBuy.setTextColor(getResources().getColor(R.color.white_alpha60));
            mTvCourseDetailBuy.setBackground(getResources().getDrawable(R.drawable.bg_red_rectangle_btn_normal));
            DialogUtil.showPromptDialog(
                    getSupportFragmentManager(), R.drawable.ic_timeallocate,
                    "报名人数已满，请重新选择直播班级!", "知道了", new PromptDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                        }
                    }, false, false);
        } else if (!entity.isOk() && entity.getCode() == -4) {
            //已购买
            mTvCourseDetailBuy.setOnClickListener(null);
            mTvCourseDetailBuy.setText(getResources().getString(R.string.live_course_paid));
            mTvCourseDetailBuy.setBackground(getResources().getDrawable(R.drawable.bg_grey_rectangle_btn_disable));
            DialogUtil.showPromptDialog(
                    getSupportFragmentManager(), R.drawable.ic_timeallocate,
                    "您已购买过该课程，同一账户只能购买一次!", "知道了", new PromptDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                        }
                    }, false, false);
        } else {
            mTvCourseDetailBuy.setClickable(true);
            entity.setOrderType(OrderDef.ORDER_TYPE_LIVE_COURSE);
            launchPayActivity(entity);
        }
    }

    private void launchPayActivity(CreateCourseOrderResultEntity entity) {
        if (entity == null) return;
        PayActivity.launch(entity, this, true);
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

    @Override
    protected String getStatName() {
        return "课程页";
    }
}
