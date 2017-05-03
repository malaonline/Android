package com.malalaoshi.android.common.pay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.MainActivity;
import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.CourseConfirmActivity;
import com.malalaoshi.android.activitys.LiveCourseInfoActivity;
import com.malalaoshi.android.adapters.FragmentGroupAdapter;
import com.malalaoshi.android.common.pay.api.OrderStatusApi;
import com.malalaoshi.android.common.pay.utils.OrderDef;
import com.malalaoshi.android.common.pay.utils.OrderStatusUtils;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.OrderStatusModel;
import com.malalaoshi.android.exception.MalaRuntimeException;
import com.malalaoshi.android.ui.dialogs.PromptDialog;
import com.malalaoshi.android.utils.DialogUtil;
import com.malalaoshi.android.utils.MiscUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/11/7.
 */

public class QrPayFragment extends BaseFragment implements FragmentGroupAdapter.IFragmentGroup, View.OnClickListener, FragmentGroupAdapter.IGetPageTitleListener {
    public static final String ARG_ORDER_INFO = "order info";

    @Bind(R.id.indicator_tabs)
    protected TabLayout tabLayout;

    private String[] tabs = new String[]{"微信支付","支付宝支付"};

    @Bind(R.id.viewpage)
    protected ViewPager vpQr;

    @Bind(R.id.tv_pay_result)
    protected TextView tvPayResult;

    private DialogFragment pendingDialog;

    //具体数据内容页面
    private List<Fragment> fragments = new ArrayList<>();
    private CreateCourseOrderResultEntity resultEntity;
    private boolean isBackAction = false;
    private FragmentGroupAdapter mFragmentAdapter;

    @Override
    public String getStatName() {
        return null;
    }

    public static QrPayFragment newInstance(CreateCourseOrderResultEntity resultEntity) {
        if (resultEntity == null || resultEntity.getOrderType()==null) {
            throw new MalaRuntimeException(QrPayFragment.class,"order entity is null");
        }
        QrPayFragment fragment = new QrPayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_ORDER_INFO, resultEntity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            resultEntity = (CreateCourseOrderResultEntity) bundle.getSerializable(ARG_ORDER_INFO);
            fragments.add(QrCodeFragment.newInstance(resultEntity, PayManager.Pay.wx_pub_qr.name()));
            fragments.add(QrCodeFragment.newInstance(resultEntity, PayManager.Pay.alipay_qr.name()));
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_pay,container,false);
        ButterKnife.bind(this,view);
        init();
        setEvent();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pendingDialog != null) {
            showDialog(pendingDialog);
        }
    }

    private void setEvent() {
        tvPayResult.setOnClickListener(this);
    }

    void init(){
        mFragmentAdapter = new FragmentGroupAdapter(getContext(), getFragmentManager(), this);
        mFragmentAdapter.setGetPageTitleListener(this);
        vpQr.setAdapter(mFragmentAdapter);
        vpQr.setOffscreenPageLimit(0);//缓存页面
        vpQr.setCurrentItem(0);
        //TabLayout加载viewpager
        tabLayout.setupWithViewPager(vpQr);

        vpQr.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e("QrPayFragment", "onPageScrolled: position="+position+",positionOffset="+positionOffset+",positionOffsetPixels="+positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                QrCodeFragment fragment = (QrCodeFragment) mFragmentAdapter.getItem(position);
                if (fragment != null){
                    fragment.initData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = fragments.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = QrCodeFragment.newInstance(resultEntity, PayManager.Pay.wx_pub_qr.name());
                    break;
                case 1:
                    fragment = QrCodeFragment.newInstance(resultEntity, PayManager.Pay.alipay_qr.name());
                    break;
            }
        }
        return fragment;
    }

    @Override
    public int getFragmentCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.tv_pay_result){
            if (resultEntity == null) {
                return;
            }
            startProcessDialog("加载中...");
            ApiExecutor.exec(new QrPayFragment.FetchOrderStatusRequest(this, resultEntity.getId()));
        }
    }


    public void onBack() {
        if (resultEntity == null) {
            return;
        }
        startProcessDialog("加载中...");
        ApiExecutor.exec(new QrPayFragment.FetchOrderStatusRequest(this, resultEntity.getId()));
        isBackAction = true;
    }

    private static final class FetchOrderStatusRequest extends BaseApiContext<QrPayFragment, OrderStatusModel> {

        private String orderId;

        public FetchOrderStatusRequest(QrPayFragment qrPayFragment, String orderId) {
            super(qrPayFragment);
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

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            get().stopProcessDialog();
        }
    }

    private void onGetOrderStatusFailed() {
        MiscUtil.toast("订单状态请求失败");
    }

    private void onGetOrderStatusSuccess(OrderStatusModel response) {
        if (response == null) {
            MiscUtil.toast("订单状态请求失败");
        }
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

    private void dealLiveCourseOrderResult(int orderStatus) {
        switch (orderStatus){
            case OrderDef.LIVE_ORDER_STATUS_UNPAY_WAIT_PAY:         //未付款，等待支付
                if (isBackAction){
                    getActivity().finish();
                }else{
                    showWaitPayDialog();
                }
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
                if (isBackAction){
                    getActivity().finish();
                }else{
                    showWaitPayDialog();
                }
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

}
