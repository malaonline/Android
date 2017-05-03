package com.malalaoshi.android.fragments;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.common.pay.PayManager;
import com.malalaoshi.android.common.pay.utils.OrderDef;
import com.malalaoshi.android.entity.LiveCourse;
import com.malalaoshi.android.network.api.FetchOrderApi;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.activitys.CourseConfirmActivity;
import com.malalaoshi.android.ui.widgets.DoubleAvatarView;
import com.malalaoshi.android.utils.CourseHelper;
import com.malalaoshi.android.adapters.CourseTimeAdapter;
import com.malalaoshi.android.entity.CourseTimeModel;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.common.pay.PayActivity;
import com.malalaoshi.android.common.pay.api.DeleteOrderApi;
import com.malalaoshi.android.network.result.OkResult;
import com.malalaoshi.android.utils.CalendarUtils;
import com.malalaoshi.android.utils.ConversionUtils;
import com.malalaoshi.android.utils.MiscUtil;
import com.malalaoshi.android.ui.widgets.NoScrollListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OrderDetailFragment extends BaseFragment {

    private static String TAG = "OrderDetailFragment";
    public static final String ARG_ORDER_ID = "order id";
    public static final String ARG_ORDER_TYPE = "order type";
    private String orderId;
    private int orderType;

    @Bind(R.id.tv_order_status)
    protected TextView tvOrderStatus;

    @Bind(R.id.tv_teacher_name)
    protected TextView tvTeacherName;

    @Bind(R.id.tv_assist_name)
    protected TextView tvAssistName;

    @Bind(R.id.tv_course_name)
    protected TextView tvCourseName;

    @Bind(R.id.tv_course_type)
    protected TextView tvCourseType;

    @Bind(R.id.tv_course_times)
    protected TextView tvCourseTimes;

    @Bind(R.id.tv_school)
    protected TextView tvSchool;

    @Bind(R.id.tv_address)
    protected TextView tvAddress;

    @Bind(R.id.iv_teacher_avator)
    protected MalaImageView ivTeacherAvator;

    @Bind(R.id.iv_live_course_avator)
    protected DoubleAvatarView ivLiveCourseAvator;

    @Bind(R.id.tv_total_hours)
    protected TextView tvTotalHours;

    @Bind(R.id.tv_total_class_lefttext)
    protected TextView tvTotalClassLefttext;

    @Bind(R.id.tv_total_class_righttext)
    protected TextView tvTotalClassRighttext;

    @Bind(R.id.lv_show_times)
    protected NoScrollListView lvShowTimes;

    @Bind(R.id.rl_pay_way)
    protected RelativeLayout rlPayWay;

    @Bind(R.id.tv_pay_way)
    protected TextView tvPayWay;

    @Bind(R.id.ll_order_time)
    protected LinearLayout llOrderTime;

    @Bind(R.id.tv_order_id)
    protected TextView tvOrderId;

    @Bind(R.id.tv_create_order_time)
    protected TextView tvCreateOrderTime;

    @Bind(R.id.ll_pay_order_time)
    protected LinearLayout llPayOrderTime;

    @Bind(R.id.tv_pay_order_time)
    protected TextView tvPayOrderTime;

    @Bind(R.id.tv_mount)
    protected TextView tvMount;

    @Bind(R.id.tv_left)
    protected TextView tvOperationLeft;

    @Bind(R.id.tv_right)
    protected TextView tvOperationRight;


    private CourseTimeAdapter timesAdapter;

    private Order order;

    public static OrderDetailFragment newInstance(String orderId, int orderType) {
        if (EmptyUtils.isEmpty(orderId)) {
            return null;
        }
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        args.putInt(ARG_ORDER_TYPE, orderType);
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
        orderId = args.getString(ARG_ORDER_ID);
        orderType = args.getInt(ARG_ORDER_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        ButterKnife.bind(this, view);
        initViews();
        initData();
        return view;
    }

    private void initData() {
        startProcessDialog("正在加载数据···");
        loadData();
    }

    private void initViews() {
        if (orderType==OrderDef.ORDER_TYPE_NORMAL){
            tvCourseType.setVisibility(View.GONE);
            tvCourseTimes.setVisibility(View.GONE);
            ivLiveCourseAvator.setVisibility(View.GONE);
            ivTeacherAvator.setVisibility(View.VISIBLE);
        }else{
            tvCourseType.setVisibility(View.VISIBLE);
            tvCourseTimes.setVisibility(View.VISIBLE);
            ivLiveCourseAvator.setVisibility(View.VISIBLE);
            ivTeacherAvator.setVisibility(View.GONE);
            tvTotalClassLefttext.setVisibility(View.GONE);
            tvTotalClassRighttext.setVisibility(View.GONE);
        }
        timesAdapter = new CourseTimeAdapter(getActivity());
        lvShowTimes.setAdapter(timesAdapter);
    }

    private void loadData() {
        ApiExecutor.exec(new FetchOrderRequest(this, orderId));
    }

    @OnClick(R.id.tv_right)
    public void onClickRight(View view) {
        if (order != null && order.getStatus() != null) {

            if (orderType==OrderDef.ORDER_TYPE_NORMAL){
                if ("u".equals(order.getStatus())) {
                    launchPayActivity();
                } else if ("p".equals(order.getStatus())) {
                    startCourseConfirmActivity();
                } else if ("d".equals(order.getStatus())) {
                    startCourseConfirmActivity();
                }
            }else{
                if ("u".equals(order.getStatus())) {
                    launchPayActivity();
                } else if ("p".equals(order.getStatus())) {
                    LiveCourse liveCourse = order.getLive_class();
                    Long end = -1L;
                    if (liveCourse!=null){
                        end = liveCourse.getCourse_end();
                    }
                    if (CalendarUtils.compareWidthNow(end)>=0){
                        MiscUtil.toast("敬请期待");
                    }
                }
            }
        }
    }

    //启动购买课程页
    private void startCourseConfirmActivity() {
        if (order != null && order.getTeacher() != null) {
            Subject subject = Subject.getSubjectIdByName(order.getSubject());
            Long teacherId = Long.valueOf(order.getTeacher());
            if (teacherId != null && subject != null) {
                CourseConfirmActivity.launch(getContext(), teacherId, order.getTeacher_name(), order.getTeacher_avatar(), subject, order.getSchool_id());
            }
        }
    }

    @OnClick(R.id.tv_left)
    public void onClickLeft(View view) {
        //取消订单
        if (order != null && order.getId() != null && order.getTo_pay() != null) {
            startProcessDialog("正在取消订单...");
            ApiExecutor.exec(new CancelCourseOrderRequest(this, order.getId() + ""));
        }
    }

    private void launchPayActivity() {
        if (order == null || order.getId() == null || EmptyUtils.isEmpty(order.getOrder_id()) || order.getTo_pay() == null)
            return;
        CreateCourseOrderResultEntity entity = new CreateCourseOrderResultEntity();
        entity.setId(order.getId() + "");
        entity.setOrder_id(order.getOrder_id());
        entity.setTo_pay((long) order.getTo_pay().doubleValue());
        if (order.is_live()){
            entity.setOrderType(OrderDef.ORDER_TYPE_LIVE_COURSE);
        }else{
            entity.setOrderType(OrderDef.ORDER_TYPE_NORMAL);
        }
        PayActivity.launch(entity, getActivity(), true);
        getActivity().finish();
    }

    private void getOrderInfoFailed() {
        MiscUtil.toast("订单信息获取失败!");
    }

    private void getOrderInfoSuccess(Order response) {
        if (response == null) {
            return;
        } else {
            order = response;
            setOrderData();
        }
    }

    private void setOrderData() {
        if (order!=null&&order.is_live()){
            tvCourseType.setVisibility(View.VISIBLE);
            tvCourseTimes.setVisibility(View.VISIBLE);
            ivLiveCourseAvator.setVisibility(View.VISIBLE);
            ivTeacherAvator.setVisibility(View.GONE);
            tvTotalClassLefttext.setVisibility(View.GONE);
            tvTotalClassRighttext.setVisibility(View.GONE);
            setLiveCourseData();
        }else{
            tvCourseType.setVisibility(View.GONE);
            tvCourseTimes.setVisibility(View.GONE);
            ivLiveCourseAvator.setVisibility(View.GONE);
            ivTeacherAvator.setVisibility(View.VISIBLE);
            setCourseData();
        }
    }

    private void setCourseData() {
        if (order == null) return;
        tvTeacherName.setText(order.getTeacher_name());
        tvCourseName.setText(order.getGrade() + " " + order.getSubject());
        tvSchool.setText(order.getSchool());
        tvAddress.setText(order.getSchool_address());
        String imgUrl = order.getTeacher_avatar();
        ivTeacherAvator.loadCircleImage(imgUrl, R.drawable.ic_default_avatar);
        tvTotalHours.setText(order.getHours().toString());

        String strTopay = "金额异常";
        Double toPay = order.getTo_pay();
        if (toPay != null) {
            strTopay = String.format("%.2f", toPay * 0.01d);
        }

        tvMount.setText(strTopay);
        //上课时间
        List<String[]> timeslots = order.getTimeslots();
        Collections.sort(timeslots, new Comparator<String[]>() {
            @Override
            public int compare(String[] strings, String[] t1) {
                if (strings.length<=0||t1.length<=0) return 0;
                return Integer.valueOf(strings[0]) - Integer.valueOf(t1[0]);
            }
        });

        if (timeslots != null) {
            List<CourseTimeModel> times = CourseHelper.courseTimes(timeslots);
            timesAdapter.addAll(times);
            timesAdapter.notifyDataSetChanged();
        }

        tvOrderId.setText(order.getOrder_id());
        tvCreateOrderTime.setText(CalendarUtils.timestampToTime(ConversionUtils.convertToLong(order.getCreated_at())));

        Resources res = getResources();
        if ("u".equals(order.getStatus())) {
            tvOrderStatus.setTextColor(res.getColor(R.color.color_red_e36a5d));
            tvOrderStatus.setText("待支付");
            rlPayWay.setVisibility(View.GONE);
            llPayOrderTime.setVisibility(View.GONE);
            tvOperationLeft.setVisibility(View.VISIBLE);
            tvOperationRight.setVisibility(View.VISIBLE);
            tvOperationRight.setTextColor(res.getColor(R.color.color_white_ffffff));
            tvOperationRight.setBackgroundColor(getResources().getColor(R.color.color_blue_8fbcdd));
            tvOperationRight.setText("去支付");
        } else if ("p".equals(order.getStatus())) {
            tvOrderStatus.setTextColor(res.getColor(R.color.color_blue_9bc3e1));
            tvOrderStatus.setText("支付成功");
            rlPayWay.setVisibility(View.VISIBLE);
            setPayChannel();
            llPayOrderTime.setVisibility(View.VISIBLE);
            tvPayOrderTime.setText(CalendarUtils.timestampToTime(ConversionUtils.convertToLong(order.getPaid_at())));
            tvOperationLeft.setVisibility(View.GONE);
            tvOperationRight.setVisibility(View.VISIBLE);
            tvOperationRight.setTextColor(res.getColor(R.color.color_white_ffffff));
            tvOperationRight.setBackgroundColor(getResources().getColor(R.color.color_blue_8fbcdd));
            tvOperationRight.setText("再次购买");
        } else if ("d".equals(order.getStatus())) {
            tvOrderStatus.setTextColor(res.getColor(R.color.color_gray_cfcfcf));
            tvOrderStatus.setText("已关闭");
            rlPayWay.setVisibility(View.GONE);
            llPayOrderTime.setVisibility(View.GONE);
            tvOperationLeft.setVisibility(View.GONE);
            tvOperationRight.setVisibility(View.VISIBLE);
            tvOperationRight.setTextColor(res.getColor(R.color.color_white_ffffff));
            tvOperationRight.setBackgroundColor(getResources().getColor(R.color.color_blue_8fbcdd));
            tvOperationRight.setText("重新购买");
        } else if ("r".equals(order.getStatus())){
            tvOrderStatus.setTextColor(res.getColor(R.color.color_green_9ec379));
            tvOrderStatus.setText("已退费");
            rlPayWay.setVisibility(View.VISIBLE);
            setPayChannel();
            llPayOrderTime.setVisibility(View.VISIBLE);
            tvPayOrderTime.setText(CalendarUtils.timestampToTime(ConversionUtils.convertToLong(order.getPaid_at())));
            tvOperationLeft.setVisibility(View.GONE);
            tvOperationRight.setVisibility(View.VISIBLE);
            tvOperationRight.setTextColor(res.getColor(R.color.color_white_ffffff_99));
            tvOperationRight.setBackgroundColor(res.getColor(R.color.color_green_9ec379));
            tvOperationRight.setText("已退费");
        }
        if (!order.is_teacher_published()) {
            tvOperationLeft.setVisibility(View.GONE);
            tvOperationRight.setVisibility(View.GONE);
        }

    }

    private void setPayChannel() {
        if (order==null){
            return;
        }
        if (PayManager.Pay.alipay.name().equals(order.getCharge_channel())){
            Drawable drawable = getResources().getDrawable(R.drawable.ic_ali_pay);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            tvPayWay.setCompoundDrawables(drawable,null,null,null);
            tvPayWay.setText("支付宝");
        }else if (PayManager.Pay.alipay_qr.name().equals(order.getCharge_channel())){
            Drawable drawable = getResources().getDrawable(R.drawable.ic_ali_pay);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            tvPayWay.setCompoundDrawables(drawable,null,null,null);
            tvPayWay.setText("支付宝扫码");
        }else if (PayManager.Pay.wx.name().equals(order.getCharge_channel())){
            Drawable drawable = getResources().getDrawable(R.drawable.ic_wx_pay);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            tvPayWay.setCompoundDrawables(drawable,null,null,null);
            tvPayWay.setText("微信");
        }else if (PayManager.Pay.wx_pub_qr.name().equals(order.getCharge_channel())){
            Drawable drawable = getResources().getDrawable(R.drawable.ic_wx_pay);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            tvPayWay.setCompoundDrawables(drawable,null,null,null);
            tvPayWay.setText("微信扫码");
        }else if (PayManager.Pay.wx_num_qr.name().equals(order.getCharge_channel())){
            Drawable drawable = getResources().getDrawable(R.drawable.ic_wx_pay);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            tvPayWay.setCompoundDrawables(drawable,null,null,null);
            tvPayWay.setText("微信公众号");
        }
    }

    private void setLiveCourseData() {
        if (order == null) return;
        LiveCourse liveCourse = order.getLive_class();
        if (liveCourse!=null){
            tvTeacherName.setText(liveCourse.getLecturer_name());
            tvAssistName.setText("(助教"+liveCourse.getAssistant_name()+")");
            tvCourseName.setText(liveCourse.getCourse_name());
            tvCourseType.setText(liveCourse.getRoom_capacity()+"人小班");
            tvCourseTimes.setText(liveCourse.getCourse_lessons()+"次");
            tvSchool.setText(order.getSchool());
            tvAddress.setText(order.getSchool_address());
            ivLiveCourseAvator.setLeftCircleImage(liveCourse.getLecturer_avatar(),R.drawable.ic_default_avatar);
            ivLiveCourseAvator.setRightCircleImage(liveCourse.getAssistant_avatar(),R.drawable.ic_default_avatar);
        }

        String strTopay = "金额异常";
        Double toPay = order.getTo_pay();
        if (toPay != null) {
            strTopay = String.format("%.2f", toPay * 0.01d);
        }

        tvMount.setText(strTopay);
        //上课时间
        List<String[]> timeslots = order.getTimeslots();
        Collections.sort(timeslots, new Comparator<String[]>() {
            @Override
            public int compare(String[] strings, String[] t1) {
                if (strings.length<=0||t1.length<=0) return 0;
                return Integer.valueOf(strings[0]) - Integer.valueOf(t1[0]);
            }
        });
        if (timeslots != null) {
            List<CourseTimeModel> times = CourseHelper.courseTimes(timeslots);
            timesAdapter.addAll(times);
            timesAdapter.notifyDataSetChanged();
        }

        tvOrderId.setText(order.getOrder_id());
        tvCreateOrderTime.setText(CalendarUtils.timestampToTime(ConversionUtils.convertToLong(order.getCreated_at())));
        Resources res = getResources();
        if ("u".equals(order.getStatus())) {
            tvOrderStatus.setTextColor(res.getColor(R.color.color_red_e36a5d));
            tvOrderStatus.setText("待支付");
            rlPayWay.setVisibility(View.GONE);
            llPayOrderTime.setVisibility(View.GONE);
            tvOperationLeft.setVisibility(View.VISIBLE);
            tvOperationRight.setVisibility(View.VISIBLE);
            tvOperationRight.setTextColor(res.getColor(R.color.color_white_ffffff));
            tvOperationRight.setBackgroundColor(res.getColor(R.color.color_blue_9bc3e1));
            tvOperationRight.setText("去支付");
        } else if ("p".equals(order.getStatus())) {
            tvOrderStatus.setTextColor(res.getColor(R.color.color_blue_9bc3e1));
            tvOrderStatus.setText("支付成功");
            rlPayWay.setVisibility(View.VISIBLE);
            setPayChannel();
            llPayOrderTime.setVisibility(View.VISIBLE);
            tvPayOrderTime.setText(CalendarUtils.timestampToTime(ConversionUtils.convertToLong(order.getPaid_at())));
            tvOperationLeft.setVisibility(View.GONE);
            tvOperationRight.setVisibility(View.VISIBLE);
            Long end = liveCourse.getCourse_end();
            if (CalendarUtils.compareWidthNow(end)>=0){
                tvOperationRight.setTextColor(res.getColor(R.color.color_white_ffffff));
                tvOperationRight.setBackgroundColor(res.getColor(R.color.color_green_9ec379));
                tvOperationRight.setText("申请退费");
            }else{
                tvOperationRight.setTextColor(res.getColor(R.color.color_white_ffffff));
                tvOperationRight.setBackgroundColor(res.getColor(R.color.color_gray_cfcfcf));
                tvOperationRight.setText("已结束");
            }
        } else if ("d".equals(order.getStatus())) {
            tvOrderStatus.setTextColor(res.getColor(R.color.color_gray_cfcfcf));
            tvOrderStatus.setText("已关闭");
            rlPayWay.setVisibility(View.GONE);
            llPayOrderTime.setVisibility(View.GONE);
            tvOperationLeft.setVisibility(View.GONE);
            tvOperationRight.setVisibility(View.VISIBLE);
            tvOperationRight.setTextColor(res.getColor(R.color.color_white_ffffff));
            tvOperationRight.setBackgroundColor(res.getColor(R.color.color_gray_cfcfcf));
            tvOperationRight.setText("已关闭");
        } else if ("r".equals(order.getStatus())) {
            tvOrderStatus.setTextColor(res.getColor(R.color.color_green_9ec379));
            tvOrderStatus.setText("已退费");
            rlPayWay.setVisibility(View.VISIBLE);
            setPayChannel();
            llPayOrderTime.setVisibility(View.VISIBLE);
            tvPayOrderTime.setText(CalendarUtils.timestampToTime(ConversionUtils.convertToLong(order.getPaid_at())));
            tvOperationLeft.setVisibility(View.GONE);
            tvOperationRight.setVisibility(View.VISIBLE);
            tvOperationRight.setTextColor(res.getColor(R.color.color_white_ffffff));
            tvOperationRight.setBackgroundColor(res.getColor(R.color.color_green_9ec379));
            tvOperationRight.setText("已退费");
        }
    }


    private static final class FetchOrderRequest extends BaseApiContext<OrderDetailFragment, Order> {

        private String orderId;

        public FetchOrderRequest(OrderDetailFragment orderDetailFragment, String orderId) {
            super(orderDetailFragment);
            this.orderId = orderId;
        }

        @Override
        public Order request() throws Exception {
            return new FetchOrderApi().get(orderId);
        }

        @Override
        public void onApiSuccess(@NonNull Order response) {
            if (response != null) {
                get().getOrderInfoSuccess(response);
            } else {
                get().getOrderInfoFailed();
            }
        }

        @Override
        public void onApiFinished() {
            get().stopProcessDialog();
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("订单信息获取失败,请检查网络!");
        }
    }


    private static final class CancelCourseOrderRequest extends BaseApiContext<OrderDetailFragment, OkResult> {

        private String orderId;

        public CancelCourseOrderRequest(OrderDetailFragment orderDetailFragment, String orderId) {
            super(orderDetailFragment);
            this.orderId = orderId;
        }

        @Override
        public OkResult request() throws Exception {
            return new DeleteOrderApi().delete(orderId);
        }

        @Override
        public void onApiSuccess(@NonNull OkResult response) {
            get().getActivity().finish();
            if (response.isOk()) {
                get().order.setStatus("d");
                get().setOrderData();
                MiscUtil.toast("订单已取消!");
            } else {
                MiscUtil.toast("订单取消失败!");
            }
        }

        @Override
        public void onApiFinished() {
            get().stopProcessDialog();
            get().getActivity().finish();
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("订单状态取消失败,请检查网络!");
        }
    }

    @Override
    public String getStatName() {
        return "订单详情页";
    }

}
