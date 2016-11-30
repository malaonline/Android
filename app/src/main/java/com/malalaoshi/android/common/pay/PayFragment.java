package com.malalaoshi.android.common.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.MainActivity;
import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.LiveCourseInfoActivity;
import com.malalaoshi.android.common.pay.utils.OrderDef;
import com.malalaoshi.android.common.pay.utils.OrderStatusUtils;
import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.activitys.CourseConfirmActivity;
import com.malalaoshi.android.ui.dialogs.PromptDialog;
import com.malalaoshi.android.entity.ChargeOrder;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.OrderStatusModel;
import com.malalaoshi.android.common.pay.api.OrderStatusApi;
import com.malalaoshi.android.utils.DialogUtil;
import com.malalaoshi.android.utils.JsonUtil;
import com.malalaoshi.android.utils.MiscUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Payment UI
 * Created by tianwei on 2/27/16.
 */
public class PayFragment extends Fragment implements View.OnClickListener {
    public static final String ARG_ORDER_ENTITY = "order_entity";
    public static final String ARG_IS_EVALUATED = "is_evaluated";
    @Bind(R.id.btn_ali)
    protected ImageView alipayBtn;
    @Bind(R.id.btn_wx)
    protected ImageView wxpayBtn;
    @Bind(R.id.btn_qr)
    protected ImageView qrpayBtn;
    @Bind(R.id.rl_ali)
    protected View alipayLayout;
    @Bind(R.id.rl_wx)
    protected View wxpayLayout;
    @Bind(R.id.rl_qr)
    protected View qrpayLayout;
    @Bind(R.id.tv_total)
    protected TextView totalView;

    private PayManager.Pay currentPay;

    @Bind(R.id.tv_pay)
    protected TextView payView;

    private DialogFragment pendingDialog;

    private CreateCourseOrderResultEntity resultEntity;
    private boolean isEvaluated = true;

    public static PayFragment newInstance(CreateCourseOrderResultEntity orderEntity, boolean isEvaluated) {
        if (orderEntity==null||orderEntity.getOrderType()==null){
            throw new RuntimeException("mala:order entity is empty");
        }
        PayFragment fragment = new PayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_ORDER_ENTITY,orderEntity);
        bundle.putBoolean(ARG_IS_EVALUATED,isEvaluated);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle!=null){
            resultEntity = (CreateCourseOrderResultEntity) bundle.getSerializable(ARG_ORDER_ENTITY);
            isEvaluated = bundle.getBoolean(ARG_IS_EVALUATED);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay, container, false);
        ButterKnife.bind(this, view);
        setCurrentPay(PayManager.Pay.alipay);
        alipayLayout.setOnClickListener(this);
        wxpayLayout.setOnClickListener(this);
        qrpayLayout.setOnClickListener(this);
        payView.setOnClickListener(this);
        if (resultEntity != null) {
            double value = Double.valueOf(resultEntity.getTo_pay()) * 0.01d;
            totalView.setText(com.malalaoshi.android.utils.Number.subZeroAndDot(value));
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pendingDialog != null) {
            showDialog(pendingDialog);
        }
    }

    private void setCurrentPay(PayManager.Pay pay) {
        currentPay = pay;
        alipayBtn.setImageResource(pay == PayManager.Pay.alipay ?
                R.drawable.ic_check : R.drawable.ic_check_out);
        wxpayBtn.setImageResource(pay == PayManager.Pay.wx ?
                R.drawable.ic_check : R.drawable.ic_check_out);
        qrpayBtn.setImageResource((pay != PayManager.Pay.wx&&pay != PayManager.Pay.alipay) ?
                R.drawable.ic_check : R.drawable.ic_check_out);
    }

    public void onClick(View view) {

        if (view.getId() == R.id.rl_ali) {
            setCurrentPay(PayManager.Pay.alipay);
        } else if (view.getId() == R.id.rl_wx) {
            setCurrentPay(PayManager.Pay.wx);
        } else if (view.getId() == R.id.rl_qr){
            setCurrentPay(null);
        }else if (view.getId() == R.id.tv_pay) {
            StatReporter.pay();
            pay();
        }
    }

    private void launchQrPayActivity() {
        QrPayActivity.launch(getContext(),resultEntity);
    }


    private void pay() {
        if (resultEntity == null || TextUtils.isEmpty(resultEntity.getOrder_id())) {
            return;
        }
        if ((currentPay != PayManager.Pay.wx&&currentPay != PayManager.Pay.alipay)){
            launchQrPayActivity();
        }else{
            ApiExecutor.exec(new FetchOrderInfoRequest(this, resultEntity.getId(), currentPay.name()));
        }
    }

    private void payInternal(final String charge) {
        if (charge==null){
            return;
        }
        ChargeOrder chargeOrder = JsonUtil.parseStringData(charge,ChargeOrder.class);
        if (chargeOrder!=null&&chargeOrder.isOk()&&-1==chargeOrder.getCode()){
            if (resultEntity.getOrderType()==OrderDef.ORDER_TYPE_NORMAL){
                showPayOrderTimeOccupiedDialog();
            }
            return;
        }
        MalaContext.exec(new Runnable() {
            @Override
            public void run() {
                PayManager.getInstance().pay(charge, getActivity());
            }
        });
    }

    /**
     * 处理返回值
     * "success" - payment succeed
     * "fail"    - payment failed
     * "cancel"  - user cancel
     * "invalid" - payment plugin not installed
     * TODO 现在只能模拟两种，一种失败，一种成功。其它每一支付或是支付重复我现在没有模拟出来。以后加上
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //支付页面返回处理
        if (requestCode == PayManager.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                Log.i("MALA", "On activity result: " + result);

                if (result == null) {
                    showPromptDialog(R.drawable.ic_pay_failed, "支付失败，请重试！", "知道了",null);
                } else if (result.equals("success")) {
                    EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_PAY_SUCCESS));
                    getOrderStatusFromOurServer();
                } else if (result.equals("cancel")) {
                    //showPromptDialog(R.drawable.ic_pay_failed, "您已取消支付！", "知道了",null);
                } else if (result.equals("invalid")) {
                    showPromptDialog(R.drawable.ic_pay_failed, "微信支付要先安装微信！", "知道了",null);
                } else {
                    showPromptDialog(R.drawable.ic_pay_failed, "支付失败，请重试！", "知道了",null);
                }

            }
        }
    }

    private void getOrderStatusFromOurServer() {
        if (resultEntity == null) {
            return;
        }
        MalaContext.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                ApiExecutor.exec(new FetchOrderStatusRequest(PayFragment.this, resultEntity.getId()));
            }
        });
    }

    private void onGetOrderStatusSuccess(OrderStatusModel response) {
        if (response == null) {
            MiscUtil.toast("订单状态请求失败");
        }
        Log.e("order",response.toString());
        int orderStatus;
        if (resultEntity.getOrderType() == OrderDef.ORDER_TYPE_NORMAL) {
            //一对一
            orderStatus = OrderStatusUtils.getNormalOrderStatus(response);
            dealNormalOrderResult(orderStatus);
        } else {
            //双师
            orderStatus = OrderStatusUtils.getLiveCourseOrderStatus(response);
            dealLiveCourseOrderResult(orderStatus);
        }
    }

    private void onGetOrderStatusFailed() {
        showOrderStatusFailedDialog();
    }

    private void dealLiveCourseOrderResult(int orderStatus) {
        switch (orderStatus){
            case OrderDef.LIVE_ORDER_STATUS_UNPAY_WAIT_PAY:         //未付款，等待支付
                showWaitPayDialog();
                break;
            case OrderDef.LIVE_ORDER_STATUS_UNPAY_ENROLLMENT_FULL:  //未付款，双师课程报名已满
                showLivePayEnrollmentFullDialog();
                break;
            case OrderDef.LIVE_ORDER_STATUS_UNPAY_ORDER_CLOSE:      //未付款，支付超时，订单已关闭
                showLivePayOrderCloseDialog();
                break;
            case OrderDef.LIVE_ORDER_STATUS_UNPAY_COURSE_OVER:      //未付款，课程已下架
                showLivePayOrderUnpublicDialog();
                break;
            case OrderDef.LIVE_ORDER_STATUS_PAY_SUCCESS:            //已付款，购课成功
                showPaySuccessDialog();
                break;
            case OrderDef.LIVE_ORDER_STATUS_PAY_ENROLLMENT_FULL:    //已付款，购课失败，双师课程报名已满
                showLivePayEnrollmentFullDialog();
                break;
            case OrderDef.LIVE_ORDER_STATUS_PAY_COURSE_OVER:        //已付款，购课失败，课程已下架
                showLivePayOrderUnpublicDialog();
                break;
            case OrderDef.LIVE_ORDER_STATUS_PAY_ORDER_CLOSE:        //已付款，但是支付时，订单已关闭
                showLivePayOrderCloseDialog();
                break;
            case OrderDef.LIVE_ORDER_STATUS_PAY_REFUND_AUDIT:       //已付款，退款审核中
                showLivePayOrderRefundAuditDialog();
                break;
            case OrderDef.LIVE_ORDER_STATUS_PAY_REFUND_SUCCESS:     //已付款，已退费
                showLivePayOrderRefundSuccessDialog();
                break;
            case OrderDef.ORDER_STATUS_UNKNOWN_ERROR:               //订单状态未知
                showOrderUnknownErrorDialog();
                break;
        }
    }

    private void dealNormalOrderResult(int orderStatus) {
        switch (orderStatus){
            case OrderDef.NORMAL_ORDER_STATUS_UNPAY_WAIT_PAY:         //未付款，等待支付
                showWaitPayDialog();
                break;
            case OrderDef.NORMAL_ORDER_STATUS_UNPAY_TIME_OCCUPIED:    //未付款，上课时间被占用
                showPayOrderTimeOccupiedDialog();
                break;
            case OrderDef.NORMAL_ORDER_STATUS_UNPAY_UNPUBLISH:        //未付款，教师已下架
                showPayOrderUnpublicDialog();
                break;
            case OrderDef.NORMAL_ORDER_STATUS_UNPAY_ORDER_CLOSE:      //未付款，支付超时，订单关闭
                showPayOrderCloseDialog();
                break;
            case OrderDef.NORMAL_ORDER_STATUS_PAY_SUCCESS:            //已付款，购课成功
                showPaySuccessDialog();
                break;
            case OrderDef.NORMAL_ORDER_STATUS_PAY_TIME_OCCUPIED:      //已付款，购课失败，上课时间被占用
                showPayOrderTimeOccupiedDialog();
                break;
            case OrderDef.NORMAL_ORDER_STATUS_PAY_ORDER_CLOSE:        //已付款，但是支付时，订单已关闭
                showPayOrderCloseDialog();
                break;
            case OrderDef.NORMAL_ORDER_STATUS_PAY_UNPUBLISH:          //已付款，购课失败，教师已经下架
                showPayOrderUnpublicDialog();
                break;
            case OrderDef.NORMAL_ORDER_STATUS_PAY_REFUND_AUDIT:       //已付款，退款审核中
                showPayOrderRefundAuditDialog();
                break;
            case OrderDef.NORMAL_ORDER_STATUS_PAY_REFUND_SUCCESS:     //已付款，已退费
                showPayOrderRefundSuccessDialog();
                break;
            case OrderDef.ORDER_STATUS_UNKNOWN_ERROR:                 //订单状态未知
                showOrderUnknownErrorDialog();
                break;
        }
    }

    private void showDialog(DialogFragment fragment) {
        final String FLAG = "payresultidialog";
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(FLAG);
        if (prev != null) {
            ft.remove(prev);
        }
        try {
            fragment.show(ft, FLAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pendingDialog = null;
    }

    //未付款，等待支付，当前页不变
    private void showWaitPayDialog() {
        showPromptDialog(R.drawable.ic_pay_failed, "暂未收到您的付款，请稍后再试哦！", "知道了",null);
    }

    //已付款，购课成功，返回课表页
    private void showPaySuccessDialog() {
        showPromptDialog(R.drawable.ic_pay_success
                , "恭喜您支付成功！您的课表已经安排好，快去查看吧！", "知道了",new PromptDialog.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //支付成功,跳转到课表页,更新课表
                        gotoSchedule();
                    }
                });
    }

    //订单状态未知
    private void showOrderUnknownErrorDialog() {
        showPromptDialog(R.drawable.ic_pay_failed, "错误的订单状态，请查看我的订单！", "知道了",new PromptDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                gotoLiveUserCenter();
            }
        });
    }

    //获取订单状态失败
    private void showOrderStatusFailedDialog() {
        showPromptDialog(R.drawable.ic_pay_failed, "订单状态获取失败,稍后请在订单列表中查看支付详情!", "知道了",new PromptDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                gotoLiveUserCenter();
            }
        });
    }

    //双师
    //已付款/未付款，购课失败，双师课程报名已满
    private void showLivePayEnrollmentFullDialog() {
        showPromptDialog(R.drawable.ic_pay_failed, "购课失败，您所购买的课程已经报满啦，请选择其他课程吧，如有付款，稍后将自动返还！", "知道了",new PromptDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                //返回至双师列表页
                gotoLiveCourseList();
            }
        });
    }

    //已付款/未付款，购课失败，课程已下架
    private void showLivePayOrderUnpublicDialog() {
        showPromptDialog(R.drawable.ic_pay_failed, "购课失败，该课程已经下架，请选择其他课程吧，如有付款，稍后将自动返还！", "知道了",new PromptDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                //返回至双师列表页
                gotoLiveCourseList();
            }
        });
    }

    //已付款/未付款，但是订单已关闭
    private void showLivePayOrderCloseDialog() {
        showDoubleButtonPromptDialog(R.drawable.ic_pay_failed, "支付超时，系统已经将您的订单取消了，请重新下单，如有付款，稍后将自动返还！", "返回首页", "重新下单", new PromptDialog.OnCloseListener() {
            @Override
            public void onLeftClick() {
                //返回双师列表页
                gotoLiveCourseList();
            }

            @Override
            public void onRightClick() {
                //返回双师课程详情页
                gotoLiveCourseInfo();
            }
        });
    }

    //已付款，退款审核中
    private void showLivePayOrderRefundAuditDialog() {
        showPromptDialog(R.drawable.ic_pay_failed, "本课程正在退款审核中，请重新选择课程！", "知道了",new PromptDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                //返回至双师列表页
                gotoLiveCourseList();
            }
        });
    }

    //已付款，已退费
    private void showLivePayOrderRefundSuccessDialog() {
        showPromptDialog(R.drawable.ic_pay_failed, "本课程已经退费，请重新选择课程！", "知道了",new PromptDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                //返回至双师列表页
                gotoLiveCourseList();
            }
        });
    }

    //一对一
    //已付款/未付款，购课失败，上课时间被占用
    private void showPayOrderTimeOccupiedDialog() {
        showDoubleButtonPromptDialog(R.drawable.ic_timeallocate, "购课失败,课程被抢占，请重新选择时间段，如有付款，稍后会自动退款！", "返回首页", "选择其他时间", new PromptDialog.OnCloseListener() {
            @Override
            public void onLeftClick() {
                //返回教师列表页
                gotoTeacherList();
            }

            @Override
            public void onRightClick() {
                //返回课程确认页
                gotoConfirmCourse();
            }
        });
    }

    //已付款/未付款，购课失败，教师已经下架
    private void showPayOrderUnpublicDialog() {
        showPromptDialog(R.drawable.ic_pay_failed, "购课失败,该老师已经下架,请选择其他教师，如有付款，稍后会自动退款！", "知道了",new PromptDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                //返回至教师列表页
                gotoTeacherList();
            }
        });
    }

    //已付款/未付款，但是订单已关闭
    private void showPayOrderCloseDialog() {
        showDoubleButtonPromptDialog(R.drawable.ic_pay_failed, "支付超时，系统已经将您的订单取消了,请重新下单，如有付款，稍后会自动退款！", "返回首页", "重新下单", new PromptDialog.OnCloseListener() {
            @Override
            public void onLeftClick() {
                //返回教师列表页
                gotoTeacherList();
            }

            @Override
            public void onRightClick() {
                //返回购买课程确认页
                gotoConfirmCourse();
            }
        });
    }

    //已付款，退款审核中
    private void showPayOrderRefundAuditDialog() {
        showPromptDialog(R.drawable.ic_pay_failed, "购课失败，本课程正在退款审核中，请选择其他教师！", "知道了",new PromptDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                //返回至教师列表页
                gotoTeacherList();
            }
        });
    }

    //已付款，已退费
    private void showPayOrderRefundSuccessDialog() {
        showPromptDialog(R.drawable.ic_pay_failed, "购课失败，本课程已经退费，请重新选择教师！", "知道了",new PromptDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                //返回至教师列表页
                gotoTeacherList();
            }
        });
    }

    //双选对话框
    private void showDoubleButtonPromptDialog(int resId, String message, String leftText, String rightText, PromptDialog.OnCloseListener listener) {
        PromptDialog dialog = DialogUtil.createDoubleButtonPromptDialog( resId
                , message,leftText, rightText,
                listener,false,false);
        if (isResumed()) {
            showDialog(dialog);
        } else {
            pendingDialog = dialog;
        }
    }

    //单选对话框
    private void showPromptDialog(int resId, String message, String btnText, PromptDialog.OnDismissListener listener) {
        PromptDialog dialog = DialogUtil.createPromptDialog(resId
                , message, btnText,
                listener
                , false, false);
        if (isResumed()) {
            showDialog(dialog);
        } else {
            pendingDialog = dialog;
        }
    }

    private void gotoSchedule() {  //跳转前重新加载课表页数据
        EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_TIMETABLE_DATA));
        Intent i = new Intent(getContext(), MainActivity.class);
        i.putExtra(MainActivity.EXTRAS_PAGE_INDEX, MainActivity.PAGE_INDEX_COURSES);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getContext().startActivity(i);
    }

    private void gotoTeacherList() {
        EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_TEACHERLIST_DATA));
        Intent i = new Intent(getContext(), MainActivity.class);
        i.putExtra(MainActivity.EXTRAS_PAGE_INDEX, MainActivity.PAGE_INDEX_TEACHERS);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getContext().startActivity(i);
    }

    private void gotoConfirmCourse() {
        EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_FETCHEVALUATED));
        Intent i = new Intent(getContext(), CourseConfirmActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getContext().startActivity(i);
    }

    private void gotoLiveCourseList() {
        EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_LIVECOURSELIST_DATA));
        Intent i = new Intent(getContext(), MainActivity.class);
        i.putExtra(MainActivity.EXTRAS_PAGE_INDEX, MainActivity.PAGE_INDEX_LIVE_COURSE);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getContext().startActivity(i);
    }

    private void gotoLiveCourseInfo() {
        LiveCourseInfoActivity.launchClearTop(getContext());
    }

    private void gotoLiveUserCenter() {
        Intent i = new Intent(getContext(), MainActivity.class);
        i.putExtra(MainActivity.EXTRAS_PAGE_INDEX, MainActivity.PAGE_INDEX_USER);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getContext().startActivity(i);
    }

    private static final class FetchOrderInfoRequest extends BaseApiContext<PayFragment, String> {

        private String orderId;
        private String payment;

        public FetchOrderInfoRequest(PayFragment payFragment, String orderId, String payChannel) {
            super(payFragment);
            this.orderId = orderId;
            this.payment = payChannel;
        }

        @Override
        public String request() throws Exception {
            return PayManager.getInstance().getOrderInfo(orderId, payment);
        }

        @Override
        public void onApiSuccess(@NonNull String response) {
            get().payInternal(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("订单状态不正确");
        }
    }

    private static final class FetchOrderStatusRequest extends BaseApiContext<PayFragment, OrderStatusModel> {

        private String orderId;

        public FetchOrderStatusRequest(PayFragment payFragment, String orderId) {
            super(payFragment);
            this.orderId = orderId;
        }

        @Override
        public OrderStatusModel request() throws Exception {
            return new OrderStatusApi().getOrderStatus(orderId);
        }

        @Override
        public void onApiSuccess(@NonNull OrderStatusModel response) {
            get().onGetOrderStatusSuccess(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().onGetOrderStatusFailed();
        }
    }

}
