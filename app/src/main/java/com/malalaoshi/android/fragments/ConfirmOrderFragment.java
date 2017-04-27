package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.common.pay.utils.OrderDef;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.utils.CourseHelper;
import com.malalaoshi.android.adapters.CourseTimeAdapter;
import com.malalaoshi.android.network.api.CourseTimesApi;
import com.malalaoshi.android.entity.CourseTimeModel;
import com.malalaoshi.android.ui.dialogs.PromptDialog;
import com.malalaoshi.android.entity.CreateCourseOrderEntity;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.entity.TimesModel;
import com.malalaoshi.android.common.pay.PayActivity;
import com.malalaoshi.android.common.pay.PayManager;
import com.malalaoshi.android.utils.DialogUtil;
import com.malalaoshi.android.utils.MiscUtil;
import com.malalaoshi.android.ui.widgets.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/5/24.
 */
public class ConfirmOrderFragment extends BaseFragment implements View.OnClickListener {

    private static final String ARG_CREATE_ORDER_INFO = "createCourseOrderEntity";
    private static final String ARG_ORDER_TEACHER_ID = "teacher id";
    private static final String ARG_ORDER_WEEKLY_TIME_SLOTS = "weekly time slots";
    private static final String ARG_ORDER_HOURS = "hours";
    private static final String ARG_ORDER_INFO = "order info";
    private static final String ARG_IS_CONFIRM_ORDER = "is confirm order";
    private static final String ARG_IS_EVALUATED = "is evaluated";

    @Bind(R.id.tv_teacher_name)
    protected TextView tvTeacherName;

    @Bind(R.id.tv_course_name)
    protected TextView tvCourseName;

    @Bind(R.id.tv_school)
    protected TextView tvSchool;

    @Bind(R.id.tv_address)
    protected TextView tvAddress;

    @Bind(R.id.iv_teacher_avator)
    protected MalaImageView ivTeacherAvator;

    @Bind(R.id.tv_total_hours)
    protected TextView tvTotalHours;

    @Bind(R.id.lv_show_times)
    protected NoScrollListView lvShowTimes;


    @Bind(R.id.tv_mount)
    protected TextView tvMount;

    @Bind(R.id.tv_submit)
    protected TextView tvSubmit;

    boolean isEvaluated = true;

    private CourseTimeAdapter timesAdapter;

    private Order order;

    private CreateCourseOrderEntity createCourseOrderEntity;

    private long hours;

    private String weeklyTimeSlots;

    private long teacherId;

    public static ConfirmOrderFragment newInstance(Order order, CreateCourseOrderEntity entity, long hours, String weeklyTimeSlots, long teacherId, boolean isEvaluated) {
        if (entity != null && TextUtils.isEmpty(weeklyTimeSlots) && hours <= 0) {
            return null;
        }
        ConfirmOrderFragment fragment = new ConfirmOrderFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ORDER_INFO, order);
        args.putSerializable(ARG_CREATE_ORDER_INFO, entity);
        args.putBoolean(ARG_IS_CONFIRM_ORDER, true);
        args.putLong(ARG_ORDER_HOURS, hours);
        args.putString(ARG_ORDER_WEEKLY_TIME_SLOTS, weeklyTimeSlots);
        args.putLong(ARG_ORDER_TEACHER_ID, teacherId);
        args.putBoolean(ARG_IS_EVALUATED, isEvaluated);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalArgumentException("arguments can not been null");
        }
        hours = args.getLong(ARG_ORDER_HOURS);
        weeklyTimeSlots = args.getString(ARG_ORDER_WEEKLY_TIME_SLOTS);
        teacherId = args.getLong(ARG_ORDER_TEACHER_ID);
        order = args.getParcelable(ARG_ORDER_INFO);
        isEvaluated = args.getBoolean(ARG_IS_EVALUATED, true);
        createCourseOrderEntity = (CreateCourseOrderEntity) args.getSerializable(ARG_CREATE_ORDER_INFO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_order, container, false);
        ButterKnife.bind(this, view);
        initViews();
        initData();
        setEvent();
        return view;
    }

    private void setEvent() {
        tvSubmit.setOnClickListener(this);
    }

    private void initData() {
        if (order == null) return;
        tvTeacherName.setText(order.getTeacher_name());
        tvCourseName.setText(order.getGrade() + " " + order.getSubject());
        tvSchool.setText(order.getSchool());
        tvAddress.setText(order.getSchool_address());
        tvTotalHours.setText(String.valueOf(order.getHours()));
        String strTopay = "金额异常";
        Double toPay = order.getTo_pay();
        if (toPay != null) {
            strTopay = String.format("%.2f", toPay);
        }
        tvMount.setText(strTopay);
        String imgUrl = order.getTeacher_avatar();
        ivTeacherAvator.loadCircleImage(imgUrl, R.drawable.ic_default_avatar);
        startProcessDialog("正在加载数据···");
        loadData();
    }

    private void initViews() {
        timesAdapter = new CourseTimeAdapter(getActivity());
        lvShowTimes.setAdapter(timesAdapter);
    }

    private void loadData() {
        ApiExecutor.exec(new FetchCourseTimesRequest(this, teacherId, hours, weeklyTimeSlots));
    }

    private void openPayActivity(CreateCourseOrderResultEntity entity) {
        if (entity == null) return;
        PayActivity.launch(entity, getActivity(), isEvaluated);
    }

    @Override
    public void onClick(View v) {
        tvSubmit.setOnClickListener(null);
        ApiExecutor.exec(new CreateOrderRequest(this, createCourseOrderEntity));
    }


    private static final class FetchCourseTimesRequest extends BaseApiContext<ConfirmOrderFragment, TimesModel> {

        private long teacherId;
        private String times;
        private long hours;

        public FetchCourseTimesRequest(ConfirmOrderFragment confirmOrderFragment,
                                       long teacherId, long hours, String times) {
            super(confirmOrderFragment);
            this.teacherId = teacherId;
            this.hours = hours;
            this.times = times;
        }

        @Override
        public TimesModel request() throws Exception {
            return new CourseTimesApi().get(teacherId, hours, times);
        }

        @Override
        public void onApiSuccess(@NonNull TimesModel response) {
            get().onFetchCourseTimesSuccess(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            super.onApiFailure(exception);
            MiscUtil.toast("上课时间获取失败");
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            get().stopProcessDialog();
        }
    }

    private void onFetchCourseTimesSuccess(TimesModel timesModel) {
        timesAdapter.clear();
        List<String[]> timeslots = new ArrayList<>();
        for (int i = 0; timesModel != null && i < timesModel.getData().size(); i++) {
            int size = timesModel.getData().get(i).size();
            timeslots.add(timesModel.getData().get(i).toArray(new String[size]));
        }
        List<CourseTimeModel> times = CourseHelper.courseTimes(timeslots);
        timesAdapter.addAll(times);
        timesAdapter.notifyDataSetChanged();
    }

    //创建订单
    private static final class CreateOrderRequest extends
            BaseApiContext<ConfirmOrderFragment, CreateCourseOrderResultEntity> {

        private CreateCourseOrderEntity entity;

        public CreateOrderRequest(ConfirmOrderFragment confirmOrderFragment,
                                  CreateCourseOrderEntity entity) {
            super(confirmOrderFragment);
            this.entity = entity;
        }

        @Override
        public CreateCourseOrderResultEntity request() throws Exception {
            return PayManager.getInstance().createOrder(entity);
        }

        @Override
        public void onApiSuccess(@NonNull CreateCourseOrderResultEntity response) {
            get().dealOrder(response);
        }

        @Override
        public void onApiStarted() {
            get().tvSubmit.setOnClickListener(null);
        }

        @Override
        public void onApiFinished() {
            get().tvSubmit.setOnClickListener(get());
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("创建订单失败");
        }
    }

    private void dealOrder(@NonNull CreateCourseOrderResultEntity entity) {
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
        } else {
            entity.setOrderType(OrderDef.ORDER_TYPE_NORMAL);
            openPayActivity(entity);
        }
    }


    @Override
    public String getStatName() {
        return "确认订单页";
    }
}
